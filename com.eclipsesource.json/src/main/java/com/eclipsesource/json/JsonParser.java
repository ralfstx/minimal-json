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


class JsonParser {

  private static final int MIN_BUFFER_SIZE = 10;
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  private final Reader reader;
  private final char[] buffer;
  private int bufferOffset;
  private int index;
  private int fill;
  private int line;
  private int lineOffset;
  private int current;
  private StringBuilder captureBuffer;
  private int captureStart;

  /**
   * <strong>Experimental:</strong>
   * <p>
   * Handles all parser events while parsing a JSON document. Methods can return arbitrary values to
   * be used by the handler for tracking arrays and objects.
   * </p>
   */
  static interface JsonHandler {

    /**
     * Handle a JSON <code>null</code> value.
     * @param begin the character index of the start position
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleNull(int begin);

    /**
     * Handle a JSON <code>true</code> value.
     * @param begin the character index of the start position
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleTrue(int begin);

    /**
     * Handle a JSON <code>false</code> value.
     * @param begin the character index of the start position
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleFalse(int begin);

    /**
     * Handle a JSON string.
     * @param begin the character index of the start position
     * @param end the character index that follows the string
     * @param string the string that has been read
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleString(int begin, int end, String string);

    /**
     * Handle a JSON number.
     * @param begin the character index of the start position
     * @param end the character index that follows the number
     * @param string the string that represents a JSON number
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleNumber(int begin, int end, String string);

    /**
     * Handle the start of an JSON array. This method can return an object for tracking this array.
     * This object will be passed to the next call to handleArrayElement or handleArrayEnd.
     * @param begin
     *          the character index of the array's opening bracket
     * @return An object to be passed to the next handleArrayElement or handleArrayEnd call
     */
    Object handleArrayStart(int begin);

    /**
     * Handle an element of a JSON array. This method is called after the value has been parsed. It
     * can be used to add the parsed value to the tracking object.
     *
     * @param arrayData
     *          the object returned by the previous handleArrayStart method
     * @param valueData
     *          the object returned by the handle method that processed the element value
     */
    void handleArrayElement(Object arrayData, Object valueData);

    /**
     * Handle the end of a JSON array.
     * @param begin the character index of opening bracket
     * @param end the character index that follows the closing bracket
     * @param arrayData
     *          the object returned by the previous handleArrayStart method
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleArrayEnd(int begin, int end, Object arrayData);

    /**
     * Handle the start of an JSON object. This method can return an object used to track this
     * object. This object will be passed to the next call to handleObjectMember or handleObjectEnd.
     *
     * @param begin
     *          the character index of the array's opening bracket
     * @return An object to be passed to the next handleObjectMember or handleObjectEnd call
     */
    Object handleObjectStart(int begin);

    /**
     * Handle the name of a new member of a JSON object. This method is called after the name has
     * been parsed.
     *
     * @param begin the first character index of the member name
     * @param end the character index that follows the member name
     * @param name
     *          the name of this member
     */
    void handleObjectName(int begin, int end, String name);

    /**
     * Handle a member of a JSON object. This method is called after the value has been parsed. It
     * can be used to add the parsed value to the tracking object.
     *
     * @param objectData
     *          the object returned by the previous handleObjectStart or handleObjectElement method
     * @param name
     *          the member name
     * @param valueData
     *          the object returned by the handle method that processed the element value
     */
    void handleObjectMember(Object objectData, String name, Object valueData);

    /**
     * Handle the end of a JSON object.
     *
     * @param begin
     *          the character index of opening brace
     * @param end
     *          the character index that follows the closing brace
     * @param objectData
     *          the object returned by the previous handleObjectStart or handlerObjectMember method
     * @return An object to pass to the caller, or <code>null</code>
     */
    Object handleObjectEnd(int begin, int end, Object objectData);

  }

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
    buffer = new char[buffersize];
    line = 1;
    captureStart = -1;
  }

  Object parse(JsonHandler handler) throws IOException {
    read();
    skipWhiteSpace();
    Object result = readValue(handler);
    skipWhiteSpace();
    if (!isEndOfText()) {
      throw error("Unexpected character");
    }
    return result;
  }

  private Object readValue(JsonHandler handler) throws IOException {
    switch (current) {
      case 'n':
        return readNull(handler);
      case 't':
        return readTrue(handler);
      case 'f':
        return readFalse(handler);
      case '"':
        return readString(handler);
      case '[':
        return readArray(handler);
      case '{':
        return readObject(handler);
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
        return readNumber(handler);
      default:
        throw expected("value");
    }
  }

  private Object readArray(JsonHandler handler) throws IOException {
    int begin = getOffset();
    read();
    Object data = handler.handleArrayStart(begin);
    skipWhiteSpace();
    if (readChar(']')) {
      return handler.handleArrayEnd(begin, begin, data);
    }
    do {
      skipWhiteSpace();
      Object value = readValue(handler);
      handler.handleArrayElement(data, value);
      skipWhiteSpace();
    } while (readChar(','));
    if (!readChar(']')) {
      throw expected("',' or ']'");
    }
    return handler.handleArrayEnd(begin, getOffset(), data);
  }

  private Object readObject(JsonHandler handler) throws IOException {
    int begin = getOffset();
    read();
    Object data = handler.handleObjectStart(begin);
    skipWhiteSpace();
    if (readChar('}')) {
      return handler.handleObjectEnd(begin, getOffset(), data);
    }
    do {
      skipWhiteSpace();
      String name = readName(handler);
      skipWhiteSpace();
      if (!readChar(':')) {
        throw expected("':'");
      }
      skipWhiteSpace();
      Object value = readValue(handler);
      handler.handleObjectMember(data, name, value);
      skipWhiteSpace();
    } while (readChar(','));
    if (!readChar('}')) {
      throw expected("',' or '}'");
    }
    return handler.handleObjectEnd(begin, getOffset(), data);
  }

  private String readName(JsonHandler handler) throws IOException {
    if (current != '"') {
      throw expected("name");
    }
    int begin = getOffset();
    String name = readStringInternal();
    handler.handleObjectName(begin, getOffset(), name);
    return name;
  }

  private Object readNull(JsonHandler handler) throws IOException {
    int begin = getOffset();
    read();
    readRequiredChar('u');
    readRequiredChar('l');
    readRequiredChar('l');
    return handler.handleNull(begin);
  }

  private Object readTrue(JsonHandler handler) throws IOException {
    int begin = getOffset();
    read();
    readRequiredChar('r');
    readRequiredChar('u');
    readRequiredChar('e');
    return handler.handleTrue(begin);
  }

  private Object readFalse(JsonHandler handler) throws IOException {
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

  private Object readString(JsonHandler handler) throws IOException {
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

  private Object readNumber(JsonHandler handler) throws IOException {
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

  private ParseException error(String message) {
    int absIndex = bufferOffset + index;
    int column = absIndex - lineOffset;
    int offset = isEndOfText() ? absIndex : absIndex - 1;
    return new ParseException(message, offset, line, column - 1);
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

  private int getOffset() {
    int absIndex = bufferOffset + index;
    return isEndOfText() ? absIndex : absIndex - 1;
  }

}
