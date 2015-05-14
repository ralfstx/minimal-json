/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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
import java.io.Writer;
import java.util.Arrays;


/**
 * Configures human readable JSON output. Whitespace will be inserted after comma and colon
 * characters. An indentation of two spaces is used by default.
 */
public class PrettyPrint extends WriterConfig {

  private final char[] indentChars;

  protected PrettyPrint( char[] indentChars ) {
    this.indentChars = indentChars;
  }

  /**
   * Print every value on a separate line. Use tabs (<code>\t</code>) for indentation.
   */
  public static PrettyPrint singleLine() {
    return new PrettyPrint( null );
  }

  /**
   * Print every value on a separate line. Use the given number of spaces for indentation.
   */
  public static PrettyPrint indentWithSpaces( int n ) {
    if( n < 0 ) {
      throw new IllegalArgumentException( "Negative number" );
    }
    char[] chars = new char[n];
    Arrays.fill( chars, ' ' );
    return new PrettyPrint( chars );
  }

  /**
   * Do not break lines.
   */
  public static PrettyPrint indentWithTabs() {
    return new PrettyPrint( new char[] { '\t' } );
  }

  @Override
  protected JsonWriter createWriter( Writer writer ) {
    return new PrettyPrintWriter( writer, indentChars );
  }

  private static class PrettyPrintWriter extends JsonWriter {

    private final char[] indentChars;
    private int indent;

    private PrettyPrintWriter( Writer writer, char[] indentChars ) {
      super( writer );
      this.indentChars = indentChars;
    }

    @Override
    protected void writeArrayOpen() throws IOException {
      indent++;
      writer.write( '[' );
      writeNewLine();
    }

    @Override
    protected void writeArrayClose() throws IOException {
      indent--;
      writeNewLine();
      writer.write( ']' );
    }

    @Override
    protected void writeArraySeparator() throws IOException {
      writer.write( ',' );
      if( !writeNewLine() ) {
        writer.write( ' ' );
      }
    }

    @Override
    protected void writeObjectOpen() throws IOException {
      indent++;
      writer.write( '{' );
      writeNewLine();
    }

    @Override
    protected void writeObjectClose() throws IOException {
      indent--;
      writeNewLine();
      writer.write( '}' );
    }

    @Override
    protected void writeMemberSeparator() throws IOException {
      writer.write( ':' );
      writer.write( ' ' );
    }

    @Override
    protected void writeObjectSeparator() throws IOException {
      writer.write( ',' );
      if( !writeNewLine() ) {
        writer.write( ' ' );
      }
    }

    private boolean writeNewLine() throws IOException {
      if( indentChars == null ) {
        return false;
      }
      writer.write( '\n' );
      for( int i = 0; i < indent; i++ ) {
        writer.write( indentChars );
      }
      return true;
    }

  }

}
