/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
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
