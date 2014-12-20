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

import java.io.*;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;


/*
 * Measures overall reading and writing performance with real-life JSON data. Should be used with
 * rather big JSON input to reduce the influence of the readers and writers creation overhead.
 */
public class ReadWriteBenchmark extends SimpleBenchmark {

  private JsonRunner runner;
  private String json;
  private byte[] jsonBytes;
  private Object model;

  @Param String input;
  @Param String parser;

  @Override
  protected void setUp() throws Exception {
    json = readResource( "input/" + input + ".json" );
    jsonBytes = json.getBytes( JsonRunner.UTF8 );
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

  public void timeReadFromByteArray( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      Object model = runner.readFromByteArray( jsonBytes );
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeReadFromReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      Reader reader = new StringReader( json );
      Object model = runner.readFromReader( reader );
      reader.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeReadFromInputStream( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = new ByteArrayInputStream( jsonBytes );
      Object model = runner.readFromInputStream( inputStream );
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

  public void timeWriteToByteArray( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      byte[] byteArray = runner.writeToByteArray( model );
      if( byteArray == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeWriteToWriter( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      Writer writer = new OutputStreamWriter( output );
      runner.writeToWriter( model, writer );
      writer.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public void timeWriteToOutputStream( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      runner.writeToOutputStream( model, output );
      output.close();
      if( output.size() == 0 ) {
        throw new RuntimeException();
      }
    }
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( ReadWriteBenchmark.class );
    runner.addParameterDefault( "parser", "org-json", "gson", "jackson", "json-simple", "minimal-json" );
    runner.addParameterDefault( "input", "rap", "caliper" );
    runner.exec( args );
  }

}
