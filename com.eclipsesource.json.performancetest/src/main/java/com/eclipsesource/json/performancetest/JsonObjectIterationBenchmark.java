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

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.JsonValue;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


public class JsonObjectIterationBenchmark extends SimpleBenchmark {

  @Param int size;

  private JsonObject jsonObject;

  @Override
  protected void setUp() throws IOException {
    jsonObject = new JsonObject();
    for( int index = 0; index < size; index++ ) {
      String name = Integer.toHexString( index );
      jsonObject.add( name, index );
    }
  }

  public void timeIterateMembers( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( Member member : jsonObject ) {
        String name = member.getName();
        JsonValue value = member.getValue();
        checkResult( name, value );
      }
    }
  }

  public void timeIterateNames( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( String name : jsonObject.names() ) {
        JsonValue value = jsonObject.get( name );
        checkResult( name, value );
      }
    }
  }

  void checkResult( String name, JsonValue value ) {
    if( name == null || value == null ) {
      throw new NullPointerException();
    }
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( JsonObjectIterationBenchmark.class );
    runner.addParameterDefault( "size", "4", "16", "64" );
    runner.exec( args );
  }

}
