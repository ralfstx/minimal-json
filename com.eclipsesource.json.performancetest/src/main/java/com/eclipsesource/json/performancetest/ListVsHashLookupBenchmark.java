/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json.performancetest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


public class ListVsHashLookupBenchmark extends SimpleBenchmark {

  @Param int size;

  private String[] names;
  private List<String> arrayList;
  private List<String> hashedList32;
  private ExampleArrayListWithHash hashedList64;
  private ExampleArrayListWithHash hashedList128;
  private Map<String,Integer> hashMap;

  @Override
  protected void setUp() throws IOException {
    names = new String[ size ];
    arrayList = new ArrayList<>();
    hashedList32 = new ExampleArrayListWithHash( 32 );
    hashedList64 = new ExampleArrayListWithHash( 64 );
    hashedList128 = new ExampleArrayListWithHash( 128 );
    hashMap = new HashMap<>();
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
      arrayList = new ArrayList<>();
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
      hashMap = new HashMap<>();
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

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( ListVsHashLookupBenchmark.class );
    runner.addParameterDefault( "size", "4", "16", "64", "256" );
    runner.exec( args );
  }

}
