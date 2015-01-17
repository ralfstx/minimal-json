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
package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;


public abstract class JsonRunner {

  public static final Charset UTF8 = Charset.forName( "UTF-8" );

  public abstract Object readFromString( String string ) throws Exception;

  public Object readFromByteArray( byte[] bytes ) throws Exception {
    return readFromString( new String( bytes, UTF8 ) );
  }

  public abstract Object readFromReader( Reader reader ) throws Exception;

  public Object readFromInputStream( InputStream in ) throws Exception {
    return readFromReader( new InputStreamReader( in, UTF8 ) );
  }

  public abstract String writeToString( Object model ) throws Exception;

  public byte[] writeToByteArray( Object model ) throws Exception {
    return writeToString( model ).getBytes( UTF8 );
  }

  public abstract void writeToWriter( Object model, Writer writer ) throws Exception;

  public void writeToOutputStream( Object model, OutputStream out ) throws Exception {
    Writer writer = new BufferedWriter( new OutputStreamWriter( new OutputStreamWrapper( out ), UTF8 ) );
    writeToWriter( model, writer );
    writer.flush();
  }

  /*
   * Delegates to the wrapped output stream, but doesn't flush or close it.
   */
  private static class OutputStreamWrapper extends OutputStream {

    private final OutputStream out;

    public OutputStreamWrapper( OutputStream out ) {
      this.out = out;
    }

    @Override
    public void write( int b ) throws IOException {
      out.write( b );
    }

    @Override
    public void write( byte[] b ) throws IOException {
      out.write( b );
    }

    @Override
    public void write( byte[] b, int off, int len ) throws IOException {
      out.write( b, off, len );
    }

  }

}
