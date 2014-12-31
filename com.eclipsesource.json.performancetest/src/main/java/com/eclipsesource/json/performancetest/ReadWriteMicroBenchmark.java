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

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;

import java.io.IOException;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


/*
 * Measures reading and writing performance for small inputs with different characteristics. Uses
 * strings only, as the overhead for creating readers and writers distorts the results for small
 * inputs.
 */
public class ReadWriteMicroBenchmark extends SimpleBenchmark {

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

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( ReadWriteMicroBenchmark.class );
    runner.addParameterDefault( "parser", "org-json", "gson", "jackson", "json-simple", "minimal-json" );
    runner.addParameterDefault( "input", "long-string", "numbers-array" );
    runner.exec( args );
  }

}
