/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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
package com.eclipsesource.json.performancetest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.google.caliper.SimpleBenchmark;


/*
 * Does it make sense to wrap a StringWriter in a BufferedWriter? No.
 */
public class BufferedStringWriterBenchmark extends SimpleBenchmark {

  private char[] buffer;

  @Override
  protected void setUp() throws Exception {
    buffer = "lorem ipsum dolor sit amet".toCharArray();
  }

  public void timeStringWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      StringWriter writer = new StringWriter();
      for( int j = 0; j < 1000; j++ ) {
        writer.write( buffer, 0, buffer.length );
      }
      if( writer.getBuffer().length() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public void timeBufferedStringWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      StringWriter writer = new StringWriter();
      BufferedWriter bufferedWriter = new BufferedWriter( writer );
      for( int j = 0; j < 1000; j++ ) {
        writer.write( buffer, 0, buffer.length );
      }
      bufferedWriter.close();
      if( writer.getBuffer().length() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( BufferedStringWriterBenchmark.class );
    runner.exec( args );
  }

}
