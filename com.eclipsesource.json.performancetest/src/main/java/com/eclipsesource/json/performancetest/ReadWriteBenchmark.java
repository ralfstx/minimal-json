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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.eclipsesource.json.performancetest.resources.Resources;
import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;


public class ReadWriteBenchmark extends SimpleBenchmark {

  private JsonRunner runner;
  private String json;
  private Object model;

  @Param String input; // set by -Dinput
  @Param String parser; // set by -Dparser

  @Override
  protected void setUp() throws Exception {
    json = readResource( input );
    runner = JsonRunnerFactory.findByName( parser );
    model = runner.readFromString( json );
  }

  public void timeReadFromString( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      Object model = runner.readFromString( json );
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeReadFromReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = Resources.getResourceAsStream( input );
      Object model = runner.readFromReader( new InputStreamReader( inputStream ) );
      inputStream.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeWriteToString( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      String string = runner.writeToString( model );
      if( string == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeWriteToWriter( int reps ) throws Exception {
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

  public static void main( String[] args ) {
    String[] defArgs = { "-Dparser=org-json,gson,jackson,json-simple,minimal-json",
                         "-Dinput=rap,caliper", // long-string,numbers-array
                         "--saveResults", "results.json" };
    Runner.main( ReadWriteBenchmark.class, args.length > 0 ? args : defArgs );
  }

}
