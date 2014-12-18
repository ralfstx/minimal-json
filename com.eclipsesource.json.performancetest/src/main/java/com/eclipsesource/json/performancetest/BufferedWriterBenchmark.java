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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;


/*
 * The JsonWriter writes tokens directly to the underlying writer. Hence, wrapping the writer in a
 * BufferedWriter can drastically improve the writing performance.
 */
public class BufferedWriterBenchmark extends SimpleBenchmark {

  private JsonRunner runner;
  private String json;
  private Object model;

  @Param String input;
  @Param String parser;

  @Override
  protected void setUp() throws Exception {
    json = readResource( "input/" + input + ".json" );
    runner = JsonRunnerFactory.findByName( parser );
    model = runner.readFromString( json );
  }

  public void timeWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter( output );
      runner.writeToWriter( model, writer );
      writer.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public void timeBufferedWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      OutputStreamWriter writer = new OutputStreamWriter( output );
      BufferedWriter bufferedWriter = new BufferedWriter( writer );
      runner.writeToWriter( model, bufferedWriter );
      bufferedWriter.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( BufferedWriterBenchmark.class );
    runner.addParameterDefault( "parser", "null", "gson", "jackson", "minimal-json" );
    runner.addParameterDefault( "input", "rap", "caliper", "long-string", "numbers-array" );
    runner.exec( args );
  }

}
