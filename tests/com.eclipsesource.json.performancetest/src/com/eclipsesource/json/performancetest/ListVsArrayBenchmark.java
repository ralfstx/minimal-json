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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.caliper.SimpleBenchmark;


public class ListVsArrayBenchmark extends SimpleBenchmark {

  private List<String> list;

  @Override
  protected void setUp() throws IOException {
    list = new ArrayList<String>();
    for( int i = 0; i < 1000; i++ ) {
      list.add( "item" + i );
    }
  }

  public void timeToArray( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      checkArray( list.toArray() );
    }
  }

  public void timeToArrayWithNewArray( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      checkArray( list.toArray( new String[ list.size() ] ) );
    }
  }

  public void timeNewList( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      checkList( new ArrayList<String>( list ) );
    }
  }

  public void timeUnmodifiableWrapper( int reps ) {
    for( int i = 0; i < reps; i++ ) {
      checkList( Collections.unmodifiableList( list ) );
    }
  }

  void checkList( List<?> copy ) {
    assert copy.size() == 1000;
  }

  void checkArray( Object[] array ) {
    assert array.length == 1000;
  }

}
