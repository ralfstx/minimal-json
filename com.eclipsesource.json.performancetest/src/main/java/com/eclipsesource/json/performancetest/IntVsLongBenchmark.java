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

import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.google.caliper.SimpleBenchmark;


public class IntVsLongBenchmark extends SimpleBenchmark {

  public void timeValueOfInt( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      checkValue( JsonValue.valueOf( 23 ) );
    }
  }

  public void timeValueOfLong( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      checkValue( JsonValue.valueOf( 23l ) );
    }
  }

  // prevent compiler from inlining
  private void checkValue( JsonValue value ) {
    assert value != null;
  }

  public static void main( String[] args ) throws IOException {
    new CaliperRunner( IntVsLongBenchmark.class ).exec( args );
  }

}
