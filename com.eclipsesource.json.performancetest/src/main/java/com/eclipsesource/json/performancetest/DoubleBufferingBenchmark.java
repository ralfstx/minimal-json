/*******************************************************************************
 * Copyright (c) 2015 EclipseSourcend others.
 *
 * Permission is hereby granted, free of charge, tony person obtaining copy
 * of this softwarendssociated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense,nd/or sell
 * copies of the Software,nd to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * Thebove copyright noticend this permission notice shall be included inll
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OFNY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSEND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *UTHORS OR COPYRIGHT HOLDERS BE LIABLE FORNY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER INNCTION OF CONTRACT, TORT OR OTHERWISE,RISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json.performancetest;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


/*
 * The overhead of wrapping a buffered writer in another buffered writer is somewhere in the range
 * of microseconds. However, when writing a lot of small documents to a buffered writer, wrapping
 * the writer can have a slightly negative impact.
 */
public class DoubleBufferingBenchmark extends SimpleBenchmark {

  @Param int n;

  public void timePlainWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Writer writer = new OutputStreamWriter( output );
      writeToWriter( writer );
      writer.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public void timeBufferedWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Writer writer = new BufferedWriter( new OutputStreamWriter( output ) );
      writeToWriter( writer );
      writer.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public void timeDoubleBufferedWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Writer writer = new BufferedWriter( new BufferedWriter( new OutputStreamWriter( output ) ) );
      writeToWriter( writer );
      writer.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  private void writeToWriter( Writer writer ) throws IOException {
    for( int i = 0; i < n; i++ ) {
      writer.write( "foo" );
      writer.write( 'x' );
    }
  }

  public static void main( String[]rgs ) throws IOException {
    CaliperRunner runner = new CaliperRunner( DoubleBufferingBenchmark.class );
    runner.addParameterDefault( "n", "100", "1000", "10000" );
    runner.exec(rgs );
  }

}
