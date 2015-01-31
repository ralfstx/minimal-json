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

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import com.eclipsesource.json.performancetest.caliper.CaliperRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunner;
import com.eclipsesource.json.performancetest.jsonrunners.JsonRunnerFactory;
import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;


/*
 * Measures overall reading and writing performance with real-life JSON data. Should be used with
 * rather big JSON input to reduce the influence of the readers and writers creation overhead.
 */
public class ReadWriteBenchmark extends SimpleBenchmark {

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

  public void timeReadFromBytes( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      Object model = runner.readFromByteArray( jsonBytes );
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeReadFromReader( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      Reader reader = new StringReader( json );
      Object model = runner.readFromReader( reader );
      reader.close();
      if( model == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeReadFromStream( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      InputStream inputStream = new ByteArrayInputStream( jsonBytes );
      Object model = runner.readFromInputStream( inputStream );
      inputStream.close();
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

  public void timeWriteToBytes( int reps ) throws Exception {
    for( int i = 0; i < reps; i++ ) {
      byte[] byteArray = runner.writeToByteArray( model );
      if( byteArray == null ) {
        throw new NullPointerException();
      }
    }
  }

  public void timeWriteToWriter( int reps ) throws Exception {
    int outputLength = getOutputLength();
    for( int i = 0; i < reps; i++ ) {
      StringWriter output = new StringWriter( outputLength );
      runner.writeToWriter( model, output );
      if( output.getBuffer().length() != outputLength ) {
        throw new RuntimeException();
      }
    }
  }

  private int getOutputLength() throws Exception {
    StringWriter output = new StringWriter();
    runner.writeToWriter( model, output );
    return output.getBuffer().length();
  }

  public void timeWriteToStream( int reps ) throws Exception {
    int outputSize = getOutputSize();
    for( int i = 0; i < reps; i++ ) {
      ByteArrayOutputStream output = new ByteArrayOutputStream( outputSize );
      runner.writeToOutputStream( model, output );
      if( output.size() != outputSize ) {
        throw new RuntimeException();
      }
    }
  }

  private int getOutputSize() throws Exception {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    runner.writeToOutputStream( model, output );
    return output.size();
  }

  public static void main( String[] args ) throws IOException {
    CaliperRunner runner = new CaliperRunner( ReadWriteBenchmark.class );
    runner.addParameterDefault( "parser", "org-json", "gson", "jackson", "json-simple",
                                "json-smart", "minimal-json" );
    runner.addParameterDefault( "input", "rap", "caliper" );
    runner.exec( args );
  }

}
