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
