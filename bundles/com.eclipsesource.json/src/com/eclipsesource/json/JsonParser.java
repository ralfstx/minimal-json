/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.json;

import java.io.IOException;
import java.io.Reader;


class JsonParser {

  private final Reader reader;
  private final StringBuilder recorder;
  private int current;
  private int line;
  private int column;

  JsonParser( Reader reader ) {
    this.reader = reader;
    recorder = new StringBuilder();
  }

  JsonValue parse() throws IOException {
    start();
    skipWhiteSpace();
    JsonValue result = readValue();
    skipWhiteSpace();
    if( !endOfText() ) {
      throw error( "Unexpected character" );
    }
    return result;
  }

  private void start() throws IOException {
    line = 1;
    column = -1;
    read();
  }

  private JsonValue readValue() throws IOException {
    switch( current ) {
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
      throw expected( "value" );
    }
  }

  private JsonArray readArray() throws IOException {
    read();
    JsonArray array = new JsonArray();
    skipWhiteSpace();
    if( readChar( ']' ) ) {
      return array;
    }
    do {
      skipWhiteSpace();
      array.add( readValue() );
      skipWhiteSpace();
    } while( readChar( ',' ) );
    if( !readChar( ']' ) ) {
      throw expected( "',' or ']'" );
    }
    return array;
  }

  private JsonObject readObject() throws IOException {
    read();
    JsonObject object = new JsonObject();
    skipWhiteSpace();
    if( readChar( '}' ) ) {
      return object;
    }
    do {
      skipWhiteSpace();
      String name = readName();
      skipWhiteSpace();
      if( !readChar( ':' ) ) {
        throw expected( "':'" );
      }
      skipWhiteSpace();
      object.add( name, readValue() );
      skipWhiteSpace();
    } while( readChar( ',' ) );
    if( !readChar( '}' ) ) {
      throw expected( "',' or '}'" );
    }
    return object;
  }

  private JsonValue readNull() throws IOException {
    read();
    readRequiredChar( 'u' );
    readRequiredChar( 'l' );
    readRequiredChar( 'l' );
    return JsonValue.NULL;
  }

  private JsonValue readTrue() throws IOException {
    read();
    readRequiredChar( 'r' );
    readRequiredChar( 'u' );
    readRequiredChar( 'e' );
    return JsonValue.TRUE;
  }

  private JsonValue readFalse() throws IOException {
    read();
    readRequiredChar( 'a' );
    readRequiredChar( 'l' );
    readRequiredChar( 's' );
    readRequiredChar( 'e' );
    return JsonValue.FALSE;
  }

  private void readRequiredChar( char ch ) throws IOException {
    if( !readChar( ch ) ) {
      throw expected( "'" + ch + "'" );
    }
  }

  private JsonValue readString() throws IOException {
    read();
    recorder.setLength( 0 );
    while( current != '"' ) {
      if( current == '\\' ) {
        readEscape();
      } else if( current < 0x20 ) {
        throw expected( "valid string character" );
      } else {
        recorder.append( (char)current );
        read();
      }
    }
    read();
    return new JsonString( recorder.toString() );
  }

  private void readEscape() throws IOException {
    read();
    switch( current ) {
    case '"':
    case '/':
    case '\\':
      recorder.append( (char)current );
      break;
    case 'b':
      recorder.append( '\b' );
      break;
    case 'f':
      recorder.append( '\f' );
      break;
    case 'n':
      recorder.append( '\n' );
      break;
    case 'r':
      recorder.append( '\r' );
      break;
    case 't':
      recorder.append( '\t' );
      break;
    case 'u':
      char[] hexChars = new char[4];
      for( int i = 0; i < 4; i++ ) {
        read();
        if( !isHexDigit( current ) ) {
          throw expected( "hexadecimal digit" );
        }
        hexChars[i] = (char)current;
      }
      recorder.append( (char)Integer.parseInt( String.valueOf( hexChars ), 16 ) );
      break;
    default:
      throw expected( "valid escape sequence" );
    }
    read();
  }

  private JsonValue readNumber() throws IOException {
    recorder.setLength( 0 );
    readAndAppendChar( '-' );
    int firstDigit = current;
    if( !readAndAppendDigit() ) {
      throw expected( "digit" );
    }
    if( firstDigit != '0' ) {
      while( readAndAppendDigit() ) {
      }
    }
    readFraction();
    readExponent();
    return new JsonNumber( recorder.toString() );
  }

  private boolean readFraction() throws IOException {
    if( !readAndAppendChar( '.' ) ) {
      return false;
    }
    if( !readAndAppendDigit() ) {
      throw expected( "digit" );
    }
    while( readAndAppendDigit() ) {
    }
    return true;
  }

  private boolean readExponent() throws IOException {
    if( !readAndAppendChar( 'e' ) && !readAndAppendChar( 'E' ) ) {
      return false;
    }
    if( !readAndAppendChar( '+' ) ) {
      readAndAppendChar( '-' );
    }
    if( !readAndAppendDigit() ) {
      throw expected( "digit" );
    }
    while( readAndAppendDigit() ) {
    }
    return true;
  }

  private String readName() throws IOException {
    if( current != '"' ) {
      throw expected( "name" );
    }
    readString();
    return recorder.toString();
  }

  private boolean readAndAppendChar( char ch ) throws IOException {
    if( current != ch ) {
      return false;
    }
    recorder.append( ch );
    read();
    return true;
  }

  private boolean readChar( char ch ) throws IOException {
    if( current != ch ) {
      return false;
    }
    read();
    return true;
  }

  private boolean readAndAppendDigit() throws IOException {
    if( !isDigit( current ) ) {
      return false;
    }
    recorder.append( (char)current );
    read();
    return true;
  }

  private void skipWhiteSpace() throws IOException {
    while( isWhiteSpace( current ) && !endOfText() ) {
      read();
    }
  }

  private void read() throws IOException {
    if( endOfText() ) {
      throw error( "Unexpected end of input" );
    }
    if( current == '\n' ) {
      line++;
      column = 0;
    } else {
      column++;
    }
    current = reader.read();
  }

  private boolean endOfText() {
    return current == -1;
  }

  private ParseException expected( String expected ) {
    if( endOfText() ) {
      return error( "Unexpected end of input" );
    }
    return error( "Expected " + expected );
  }

  private ParseException error( String message ) {
    return new ParseException( message, line, column );
  }

  private static boolean isWhiteSpace( int ch ) {
    return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
  }

  private static boolean isDigit( int ch ) {
    return ch >= '0' && ch <= '9';
  }

  private static boolean isHexDigit( int ch ) {
    return ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
  }

}
