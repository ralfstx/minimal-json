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
import java.io.Writer;


class JsonWriter {

  private static final int CONTROL_CHARACTERS_START = 0x0000;
  private static final int CONTROL_CHARACTERS_END = 0x001f;

  protected final Writer writer;

  JsonWriter( Writer writer ) {
    this.writer = writer;
  }

  void write( String string ) throws IOException {
    writer.write( string );
  }

  void writeString( String string ) throws IOException {
    writer.write( '"' );
    int length = string.length();
    char[] chars = new char[ length ];
    string.getChars( 0, length, chars, 0 );
    for( int i = 0; i < length; i++ ) {
      char ch = chars[ i ];
      if( ch == '"' || ch == '\\' ) {
        writer.write( '\\' );
        writer.write( ch );
      } else if( ch == '\n' ) {
        writer.write( '\\' );
        writer.write( 'n' );
      } else if( ch == '\r' ) {
        writer.write( '\\' );
        writer.write( 'r' );
      } else if( ch == '\t' ) {
        writer.write( "\\t" );
      // In JavaScript, U+2028 and U+2029 characters count as line endings and must be encoded.
      // http://stackoverflow.com/questions/2965293/javascript-parse-error-on-u2028-unicode-character
      } else if( ch == '\u2028' ) {
        writer.write( "\\u2028" );
      } else if( ch == '\u2029' ) {
        writer.write( "\\u2029" );
      } else if( ch >= CONTROL_CHARACTERS_START && ch <= CONTROL_CHARACTERS_END ) {
        writer.write( "\\u00" );
        if( ch <= 0x000f ) {
          writer.write( '0' );
        }
        writer.write( Integer.toHexString( ch ) );
      } else {
        writer.write( ch );
      }
    }
    writer.write( '"' );
  }

  protected void writeBeginObject() throws IOException {
    writer.write( '{' );
  }

  protected void writeEndObject() throws IOException {
    writer.write( '}' );
  }

  protected void writeNameValueSeparator() throws IOException {
    writer.write( ':' );
  }

  protected void writeObjectValueSeparator() throws IOException {
    writer.write( ',' );
  }

  protected void writeBeginArray() throws IOException {
    writer.write( '[' );
  }

  protected void writeEndArray() throws IOException {
    writer.write( ']' );
  }

  protected void writeArrayValueSeparator() throws IOException {
    writer.write( ',' );
  }

}
