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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

import static com.eclipsesource.json.performancetest.resources.Resources.getResourceAsStream;


/*
 * Since the JsonParser uses a buffer for reading, wrapping the reader in an additional
 * BufferedReader should not distinctly affect reading performance.
 */
public class BufferedReaderBenchmark extends SimpleBenchmark {

  private JsonRunner runner;

  @Param String input;
  @Param String parser;

  @Override
  protected void setUp() throws Exception {
    runner = JsonRunnerFactory.findByName( parser );
  }

  public void timeReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = getResourceAsStream( "input/" + input + ".json" );
      Reader reader = new InputStreamReader( inputStream );
      Object model = runner.readFromReader( reader );
      reader.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeBufferedReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = getResourceAsStream( "input/" + input + ".json" );
      Reader reader = new BufferedReader( new InputStreamReader( inputStream ) );
      Object model = runner.readFromReader( reader );
      reader.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( BufferedReaderBenchmark.class );
    runner.addParameterDefault( "parser", "null", "gson", "jackson", "minimal-json" );
    runner.addParameterDefault( "input", "rap", "caliper", "long-string", "numbers-array" );
    runner.exec( args );
  }

}
