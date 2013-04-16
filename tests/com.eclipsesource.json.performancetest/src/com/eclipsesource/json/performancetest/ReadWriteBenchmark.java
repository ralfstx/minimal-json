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
import com.google.caliper.SimpleBenchmark;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;


public class ReadWriteBenchmark extends SimpleBenchmark {

  private JsonRunner jsonImpl;
  private String json;
  private Object model;

  @Param String variant; // set by -Dvariant

  @Override
  protected void setUp() throws IOException {
    json = readResource( "rap.json" );
    if( "org-json".equals( variant ) ) {
      jsonImpl = new JsonOrgRunner();
    } else if( "gson".equals( variant ) ) {
      jsonImpl = new GsonRunner();
    } else if( "jackson".equals( variant ) ) {
      jsonImpl = new JacksonRunner();
    } else if( "json-simple".equals( variant ) ) {
      jsonImpl = new SimpleRunner();
    } else if( "minimal-json".equals( variant ) ) {
      jsonImpl = new MinimalJsonRunner();
    } else {
      throw new IllegalArgumentException( "Unknown variant: " + variant );
    }
    model = jsonImpl.read( json );
    String result = jsonImpl.write( model );
    if( !result.contains( "\"requestCounter\"" ) ) {
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

  public static void main( String[] args ) throws IOException {
    ReadWriteBenchmark benchmark = new ReadWriteBenchmark();
    benchmark.variant = "gson";
    benchmark.setUp();
    benchmark.timeRead( 1 );
    benchmark.timeWrite( 1 );
  }

}
