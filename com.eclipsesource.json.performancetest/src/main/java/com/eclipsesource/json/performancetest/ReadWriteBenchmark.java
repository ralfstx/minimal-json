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

import java.io.IOException;

import com.eclipsesource.json.performancetest.jsonrunners.GsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JacksonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonOrgRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.MinimalJsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.SimpleRunner;
import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;


public class ReadWriteBenchmark extends SimpleBenchmark {

  private JsonRunner jsonImpl;
  private String json;
  private Object model;

  @Param String input; // set by -Dinput
  @Param String parser; // set by -Dparser

  @Override
  protected void setUp() throws IOException {
    json = readResource( input + ".json" );
    if( "org-json".equals( parser ) ) {
      jsonImpl = new JsonOrgRunner();
    } else if( "gson".equals( parser ) ) {
      jsonImpl = new GsonRunner();
    } else if( "jackson".equals( parser ) ) {
      jsonImpl = new JacksonRunner();
    } else if( "json-simple".equals( parser ) ) {
      jsonImpl = new SimpleRunner();
    } else if( "minimal-json".equals( parser ) ) {
      jsonImpl = new MinimalJsonRunner();
    } else {
      throw new IllegalArgumentException( "Unknown variant: " + parser );
    }
    model = jsonImpl.read( json );
    String result = jsonImpl.write( model );
    if( !result.trim().startsWith( "{" ) ) {
      throw new RuntimeException( "Unexpected output" );
    }
  }

  public void timeRead( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      Object model = jsonImpl.read( json );
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeWrite( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      String string = jsonImpl.write( model );
      if( string == null ) {
        throw new NullPointerException();
      }
    }
  }

  public static void main( String[] args ) {
    String[] defArgs = { "-Dparser=org-json,gson,jackson,json-simple,minimal-json",
                         "-Dinput=long-string,numbers-array" }; // rap,caliper
    Runner.main( ReadWriteBenchmark.class, args.length > 0 ? args : defArgs );
  }

}
