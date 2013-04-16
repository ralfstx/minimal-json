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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


public class ListVsHashLookupBenchmark extends SimpleBenchmark {

  @Param int size; // set by -Dsize

  private String[] names;
  private List<String> arrayList;
  private List<String> hashedList32;
  private ExampleArrayListWithHash hashedList64;
  private ExampleArrayListWithHash hashedList128;
  private Map<String,Integer> hashMap;

  @Override
  protected void setUp() throws IOException {
    names = new String[ size ];
    arrayList = new ArrayList<String>();
    hashedList32 = new ExampleArrayListWithHash( 32 );
    hashedList64 = new ExampleArrayListWithHash( 64 );
    hashedList128 = new ExampleArrayListWithHash( 128 );
    hashMap = new HashMap<String, Integer>();
    for( int index = 0; index < size; index++ ) {
      names[index] = Integer.toHexString( index );
      arrayList.add( names[index] );
      hashedList32.add( names[index] );
      hashedList64.add( names[index] );
      hashedList128.add( names[index] );
      hashMap.put( names[index], Integer.valueOf( index ) );
    }
  }

  public void timeFillArrayList( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      arrayList = new ArrayList<String>();
      for( int index = 0; index < size; index++ ) {
        arrayList.add( names[index] );
      }
    }
  }

  public void timeFillArrayListWithHash32( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      hashedList32 = new ExampleArrayListWithHash( 32 );
      for( int index = 0; index < size; index++ ) {
        hashedList32.add( names[index] );
      }
    }
  }

  public void timeFillArrayListWithHash64( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      hashedList64 = new ExampleArrayListWithHash( 32 );
      for( int index = 0; index < size; index++ ) {
        hashedList64.add( names[index] );
      }
    }
  }

  public void timeFillArrayListWithHash128( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      hashedList128 = new ExampleArrayListWithHash( 32 );
      for( int index = 0; index < size; index++ ) {
        hashedList128.add( names[index] );
      }
    }
  }

  public void timeFillHashMap( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      hashMap = new HashMap<String, Integer>();
      for( int index = 0; index < size; index++ ) {
        hashMap.put( names[index], Integer.valueOf( index ) );
      }
    }
  }

  public void timeLookupArrayList( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( int index = 0; index < size; index++ ) {
        checkIndex( index, arrayList.indexOf( names[index] ) );
      }
    }
  }

  public void timeLookupArrayListWithHash32( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( int index = 0; index < size; index++ ) {
        checkIndex( index, hashedList32.indexOf( names[index] ) );
      }
    }
  }

  public void timeLookupArrayListWithHash64( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( int index = 0; index < size; index++ ) {
        checkIndex( index, hashedList64.indexOf( names[index] ) );
      }
    }
  }

  public void timeLookupArrayListWithHash128( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( int index = 0; index < size; index++ ) {
        checkIndex( index, hashedList128.indexOf( names[index] ) );
      }
    }
  }

  public void timeLookupHashMap( int reps ) {
    for( int r = 0; r < reps; r++ ) {
      for( int index = 0; index < size; index++ ) {
        checkIndex( index, hashMap.get( names[index] ).intValue() );
      }
    }
  }

  void checkIndex( int expected, int actual ) {
    if( expected != actual ) {
      throw new RuntimeException();
    }
  }

}
