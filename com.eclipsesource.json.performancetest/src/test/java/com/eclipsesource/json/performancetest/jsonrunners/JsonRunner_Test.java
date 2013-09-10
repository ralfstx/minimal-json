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
package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import static org.mockito.Mockito.*;


@RunWith( value = Parameterized.class )
public class JsonRunner_Test {

  private String json;
  private JsonRunner runner;

  @Parameter( 0 ) public String name;

  @Parameters( name = "{0}" )
  public static Iterable<Object[]> data() {
    return Arrays.asList( new Object[][] {
        { "org-json" },
        { "gson" },
        { "jackson" },
        { "json-simple" },
        { "minimal-json" } } );
  }

  @Before
  public void setUp() throws Exception {
    runner = JsonRunnerFactory.findByName( name );
    json = readResource( "caliper" );
  }

  @Test
  public void testReadWriteString() throws Exception {
    String result = runner.writeToString( runner.readFromString( json ) );

    assertTrue( equalsIgnoreOrder( JsonValue.readFrom( json ), JsonValue.readFrom( result ) ) );
  }

  @Test
  public void testReadFromReader() throws Exception {
    StringReader reader = new StringReader( json );

    Object readFromReader = runner.readFromReader( reader );

    String result = runner.writeToString( readFromReader );
    assertTrue( equalsIgnoreOrder( JsonValue.readFrom( json ), JsonValue.readFrom( result ) ) );
  }

  @Test
  public void testWriteToWriter() throws Exception {
    StringWriter writer = new StringWriter();
    Object model = runner.readFromString( json );

    runner.writeToWriter( model, writer );

    String result = writer.toString();
    assertTrue( equalsIgnoreOrder( JsonValue.readFrom( json ), JsonValue.readFrom( result ) ) );
  }

  @Test
  public void testReadFromReader_doesNotCloseReader() throws Exception {
    // Jackson does close the reader
    assumeFalse( "jackson".equals( name ) );
    Reader reader = spy( new StringReader( json ) );

    runner.readFromReader( reader );

    verify( reader, never() ).close();
  }

  @Test
  public void testWriteToWriter_doesNotCloseWriter() throws Exception {
    // Jackson does close the writer
    assumeFalse( "jackson".equals( name ) );
    StringWriter writer = spy( new StringWriter() );
    Object model = runner.readFromString( json );

    runner.writeToWriter( model, writer );

    verify( writer, never() ).close();
  }

  private boolean equalsIgnoreOrder( JsonValue value1, JsonValue value2 ) {
    if( value1.isObject() ) {
      return equalsIgnoreOrder( value1.asObject(), value2 );
    } else if( value1.isArray() ) {
      return equalsIgnoreOrder( value1.asArray(), value2 );
    }
    return value1.equals( value2 );
  }

  private boolean equalsIgnoreOrder( JsonArray array1, JsonValue value2 ) {
    if( !value2.isArray() ) {
      return false;
    }
    JsonArray array2 = value2.asArray();
    if( array1.size() != array2.size() ) {
      return false;
    }
    for( int i = 0; i < array1.size(); i++ ) {
      if( !equalsIgnoreOrder( array2.get( i ), array2.get( i ) ) ) {
        return false;
      }
    }
    return true;
  }

  private boolean equalsIgnoreOrder( JsonObject object1, JsonValue value2 ) {
    if( !value2.isObject() ) {
      return false;
    }
    JsonObject object2 = value2.asObject();
    List<String> names1 = object1.names();
    List<String> names2 = object2.names();
    if( !names1.containsAll( names2 ) || !names2.containsAll( names1 ) ) {
      return false;
    }
    for( String name : names1 ) {
      if( !equalsIgnoreOrder( object1.get( name ), object2.get( name ) ) ) {
        return false;
      }
    }
    return true;
  }

}
