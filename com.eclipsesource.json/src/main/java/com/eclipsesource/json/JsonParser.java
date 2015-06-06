/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.eclipsesource.json.JsonHandler.ElementList;
import com.eclipsesource.json.JsonHandler.MemberSet;

class JsonParser implements ParserContext {

  private static final int MIN_BUFFER_SIZE = 10;
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private final Reader reader;
  private final char[] buffer;
  private JsonHandler handler;
  private int bufferOffset;
  private int index;
  private int fill;
  private int line;
  private int lineOffset;
  private int current;
  private int nesting;
  private String name;
  private StringBuilder captureBuffer;
  private int captureStart;

  /*
   * |                      bufferOffset
   *                        v
   * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t]        < input
   *                       [l|m|n|o|p|q|r|s|t|?|?]    < buffer
   *                          ^               ^
   *                       |  index           fill
   */

  JsonParser(String string) {
    this(new StringReader(string),
         Math.max(MIN_BUFFER_SIZE, Math.min(DEFAULT_BUFFER_SIZE, string.length())));
  }

  JsonParser(Reader reader) {
    this(reader, DEFAULT_BUFFER_SIZE);
  }

  JsonParser(Reader reader, int buffersize) {
    this.reader = reader;
    buffer = new char[ buffersize ];
    line = 1;
    captureStart = -1;
  }

  JsonValue parse(JsonHandler handler) throws IOException {
    this.handler = handler;
    read();
    skipWhiteSpace();
    JsonValue result = readValue();
    skipWhiteSpace();
    if (!isEndOfText()) {
      throw error("Unexpected character");
    }
    return result;
  }

  JsonValue parse() throws IOException {
    return parse(JsonValue.defaultHandler);
  }

  private JsonValue readValue() throws IOException {
    switch (current) {
      case 'n':
        return readNull();
      case 't':
        return readTrue();
      case 'f':
        return readFalse();
      case '"':
        return readString();
      case '[':
        return readArray();
      case '{':
        return readObject();
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return readNumber();
      default:
        throw expected("value");
    }
  }

  private JsonValue readArray() throws IOException {
    int begin = getOffset();
    read();
    ElementList array = handler.handleArrayStart(this);
    nesting ++;
    name = null;
    skipWhiteSpace();
    if (readChar( ']')) {
      array = handler.handleArrayEnd(begin, begin, array);
      nesting --;
      return array;
    }
    do {
      skipWhiteSpace();
      JsonValue value = readValue();
      name = null;
      if (value != null && array != null) {
        array.addElement(value, this);
      }
      skipWhiteSpace();
    } while (readChar(','));
    if (!readChar( ']')) {
      throw expected("',' or ']'");
    }
    nesting --;
    return handler.handleArrayEnd(begin, begin, array);
  }

  private JsonValue readObject() throws IOException {
    int begin = getOffset();
    read();
    MemberSet object = handler.handleObjectStart(this);
    nesting ++;
    skipWhiteSpace();
    if (readChar('}')) {
      object = handler.handleObjectEnd(begin, getOffset(), object);
      nesting --;
      return object;
    }
    do {
      skipWhiteSpace();
      String name = this.name = readName();
      handler.handleMemberName(begin, getOffset(), name);
      skipWhiteSpace();
      if (!readChar( ':')) {
        throw expected("':'");
      }
      skipWhiteSpace();
      JsonValue value = readValue();
      this.name = name;
      if (value != null && object != null) {
        object.addMember(name, value, this);
      }
      skipWhiteSpace();
    } while (readChar( ',' ));
    if (!readChar('}')) {
      throw expected("',' or '}'");
    }
    nesting --;
    return handler.handleObjectEnd(begin, getOffset(), object);
  }

  private String readName() throws IOException {
    if (current != '"') {
      throw expected("name");
    }
    int begin = getOffset();
    String name = readStringInternal();
    handler.handleMemberName(begin, getOffset(), name);
    return name;
  }

  private JsonValue readNull() throws IOException {
    int begin = getOffset();
    read();
    readRequiredChar('u');
    readRequiredChar('l');
    readRequiredChar('l');
    return handler.handleNull(begin);
  }

  private JsonValue readTrue() throws IOException {
    int begin = getOffset();
    read();
    readRequiredChar('r');
    readRequiredChar('u');
    readRequiredChar('e');
    return handler.handleTrue(begin);
  }

  private JsonValue readFalse() throws IOException {
    int begin = getOffset();
    read();
    readRequiredChar('a');
    readRequiredChar('l');
    readRequiredChar('s');
    readRequiredChar('e');
    return handler.handleFalse(begin);
  }

  private void readRequiredChar(char ch) throws IOException {
    if (!readChar(ch)) {
      throw expected("'" + ch + "'");
    }
  }

  private JsonValue readString() throws IOException {
    int begin = getOffset();
    String string = readStringInternal();
    return handler.handleString(begin, getOffset(), string);
  }

  private String readStringInternal() throws IOException {
    read();
    startCapture();
    while (current != '"') {
      if (current == '\\') {
        pauseCapture();
        readEscape();
        startCapture();
      } else if (current < 0x20) {
        throw expected("valid string character");
      } else {
        read();
      }
    }
    String string = endCapture();
    read();
    return string;
  }

  private void readEscape() throws IOException {
    read();
    switch (current) {
      case '"':
      case '/':
      case '\\':
        captureBuffer.append((char)current);
        break;
      case 'b':
        captureBuffer.append('\b');
        break;
      case 'f':
        captureBuffer.append('\f');
        break;
      case 'n':
        captureBuffer.append('\n');
        break;
      case 'r':
        captureBuffer.append('\r');
        break;
      case 't':
        captureBuffer.append('\t');
        break;
      case 'u':
        char[] hexChars = new char[4];
        for (int i = 0; i < 4; i++) {
          read();
          if (!isHexDigit()) {
            throw expected("hexadecimal digit");
          }
          hexChars[i] = (char)current;
        }
        captureBuffer.append((char)Integer.parseInt(new String(hexChars), 16));
        break;
      default:
        throw expected("valid escape sequence");
    }
    read();
  }

  private JsonValue readNumber() throws IOException {
    int begin = getOffset();
    startCapture();
    readChar('-');
    int firstDigit = current;
    if (!readDigit()) {
      throw expected("digit");
    }
    if (firstDigit != '0') {
      while (readDigit()) {
      }
    }
    readFraction();
    readExponent();
    String string = endCapture();
    return handler.handleNumber(begin, getOffset(), string);
  }

  private boolean readFraction() throws IOException {
    if (!readChar('.')) {
      return false;
    }
    if (!readDigit()) {
      throw expected("digit");
    }
    while (readDigit()) {
    }
    return true;
  }

  private boolean readExponent() throws IOException {
    if (!readChar('e') && !readChar('E')) {
      return false;
    }
    if (!readChar('+')) {
      readChar('-');
    }
    if (!readDigit()) {
      throw expected("digit");
    }
    while (readDigit()) {
    }
    return true;
  }

  private boolean readChar(char ch) throws IOException {
    if (current != ch) {
      return false;
    }
    read();
    return true;
  }

  private boolean readDigit() throws IOException {
    if (!isDigit()) {
      return false;
    }
    read();
    return true;
  }

  private void skipWhiteSpace() throws IOException {
    while (isWhiteSpace()) {
      read();
    }
  }

  private void read() throws IOException {
    if (index == fill) {
      if (captureStart != -1) {
        captureBuffer.append(buffer, captureStart, fill - captureStart);
        captureStart = 0;
      }
      bufferOffset += fill;
      fill = reader.read(buffer, 0, buffer.length);
      index = 0;
      if (fill == -1) {
        current = -1;
        return;
      }
    }
    if (current == '\n') {
      line++;
      lineOffset = bufferOffset + index;
    }
    current = buffer[index++];
  }

  private void startCapture() {
    if (captureBuffer == null) {
      captureBuffer = new StringBuilder();
    }
    captureStart = index - 1;
  }

  private void pauseCapture() {
    int end = current == -1 ? index : index - 1;
    captureBuffer.append(buffer, captureStart, end - captureStart);
    captureStart = -1;
  }

  private String endCapture() {
    int end = current == -1 ? index : index - 1;
    String captured;
    if (captureBuffer.length() > 0) {
      captureBuffer.append(buffer, captureStart, end - captureStart);
      captured = captureBuffer.toString();
      captureBuffer.setLength(0);
    } else {
      captured = new String(buffer, captureStart, end - captureStart);
    }
    captureStart = -1;
    return captured;
  }

  private ParseException expected(String expected) {
    if (isEndOfText()) {
      return error("Unexpected end of input");
    }
    return error("Expected " + expected);
  }

  private void skipInner() throws IOException {
    boolean is_literal = false;
	int n_obj = 0, n_arr = 0;
	do {
	  read();
	  switch ( current ) {
	  case '{':
	    n_obj ++;
	    break;
	  case '[':
	    n_arr ++;
	    break;
	  case '}':
	    -- n_obj;
		break;
      case ']':
		-- n_arr;
		break;
      case '"':
	    is_literal = !is_literal;
	    break;
	  case '\\':
	    read();
	    break;
	  default:
	  }
	} while( is_literal || n_obj > 0 || n_arr > 0 ||
        current != ',' && current != ']' && current != '}' );
  }

  private int skip( int n_skip ) throws IOException {
	int n_skipped = 0;
    skipWhiteSpace();
    if( current != ',' && current != ']' && current != '}' ) {
      skipInner();
      n_skipped = 1;
    }
    while( current == ',' && n_skipped++ != n_skip ) {
      skipWhiteSpace();
      skipInner();
    }
	return n_skipped;
  }

  public int skipAll() throws IOException {
    return skip( -1 );
  }

  public int skipNext( int n ) throws IOException {
	if( name != null ) {
	  throw new IllegalStateException( "Attempted to skip element inside object" );
	}
	if ( n < 1 ) {
      throw new IllegalArgumentException( "Number of elements to skip must be greater than zero " );
	}
	return skip( n );
  }

  public boolean skipNext() throws IOException {
	return skipNext( 1 ) == 1;
  }

  public int getNesting() {
	return nesting;
  }

  public String getFieldName() {
	return name;
  }

  public int getLine() {
	return line;
  }

  public int getOffset() {
	int absIndex = bufferOffset + index;
    return isEndOfText() ? absIndex : absIndex - 1;
  }

  public int getColumn() {
	return bufferOffset + index - lineOffset - 1;
  }

  private ParseException error( String message ) {
    return new ParseException( message, getOffset(), line, getColumn() );
  }

  private boolean isWhiteSpace() {
    return current == ' ' || current == '\t' || current == '\n' || current == '\r';
  }

  private boolean isDigit() {
    return current >= '0' && current <= '9';
  }

  private boolean isHexDigit() {
    return current >= '0' && current <= '9'
        || current >= 'a' && current <= 'f'
        || current >= 'A' && current <= 'F';
  }

  private boolean isEndOfText() {
    return current == -1;
  }
}
