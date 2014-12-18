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

import static com.eclipsesource.json.performancetest.resources.Resources.getResourceAsStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


/*
 * Since the JsonParser uses a buffer for reading, wrapping the reader in an additional
 * BufferedReader should not distinctly affect reading performance.
 */
public class BufferedReaderBenchmark extends SimpleBenchmark {

  private JsonRunner runner;

  @Param String input;
  @Param String parser;

  @Override
  protected void setUp() throws Exception {
    runner = JsonRunnerFactory.findByName( parser );
  }

  public void timeReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = getResourceAsStream( "input/" + input + ".json" );
      Reader reader = new InputStreamReader( inputStream );
      Object model = runner.readFromReader( reader );
      reader.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeBufferedReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = getResourceAsStream( "input/" + input + ".json" );
      Reader reader = new BufferedReader( new InputStreamReader( inputStream ) );
      Object model = runner.readFromReader( reader );
      reader.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( BufferedReaderBenchmark.class );
    runner.addParameterDefault( "parser", "null", "gson", "jackson", "minimal-json" );
    runner.addParameterDefault( "input", "rap", "caliper", "long-string", "numbers-array" );
    runner.exec( args );
  }

}
