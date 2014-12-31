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
package com.eclipsesource.json.performancetest.jsonrunners;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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


@RunWith( value = Parameterized.class )
public class JsonRunner_Test {

  private String json;
  private byte[] jsonBytes;
  private JsonValue minimalJsonModel;
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
    json = readResource("input/caliper.json");
    jsonBytes = json.getBytes( JsonRunner.UTF8 );
    minimalJsonModel = JsonValue.readFrom( json );
  }

  private void assertJsonCorrect( String s ) {
    assertTrue( equalsIgnoreOrder( minimalJsonModel, JsonValue.readFrom( s ) ) );
  }

  private void assertJsonCorrect( byte[] ba ) {
    assertJsonCorrect( new String( ba, JsonRunner.UTF8 ) );
  }

  @Test
  public void testReadWriteString() throws Exception {
    String result = runner.writeToString( runner.readFromString( json ) );

    assertJsonCorrect( result );
  }

  @Test
  public void testReadWriteByteArray() throws Exception {
    byte[] result = runner.writeToByteArray( runner.readFromByteArray( jsonBytes ) );

    assertJsonCorrect( result );
  }

  @Test
  public void testReadFromReader() throws Exception {
    StringReader reader = new StringReader( json );

    Object readFromReader = runner.readFromReader( reader );

    String result = runner.writeToString( readFromReader );
    assertJsonCorrect( result );
  }

  @Test
  public void testReadFromReader_doesNotCloseReader() throws Exception {
    Reader reader = spy( new StringReader( json ) );

    runner.readFromReader( reader );

    verify( reader, never() ).close();
  }

  @Test
  public void testReadFromInputStream() throws Exception {
    ByteArrayInputStream inputStream = new ByteArrayInputStream( jsonBytes );

    Object readFromInputStream = runner.readFromInputStream( inputStream );

    String result = runner.writeToString( readFromInputStream );
    assertJsonCorrect( result );
  }

  @Test
  public void testReadFromInputStream_doesNotCloseInputStream() throws Exception {
    InputStream inputStream = spy( new ByteArrayInputStream( jsonBytes ) );

    runner.readFromInputStream( inputStream );

    verify( inputStream, never() ).close();
  }

  @Test
  public void testWriteToWriter() throws Exception {
    Object model = runner.readFromString( json );
    StringWriter writer = new StringWriter();

    runner.writeToWriter( model, writer );

    String result = writer.toString();
    assertJsonCorrect( result );
  }

  @Test
  public void testWriteToWriter_doesNotCloseWriter() throws Exception {
    StringWriter writer = spy( new StringWriter() );
    Object model = runner.readFromString( json );

    runner.writeToWriter( model, writer );

    verify( writer, never() ).close();
  }

  @Test
  public void testWriteToWriter_doesNotFlushWriter() throws Exception {
    StringWriter writer = spy( new StringWriter() );
    Object model = runner.readFromString( json );

    runner.writeToWriter( model, writer );

    verify( writer, never() ).flush();
  }

  @Test
  public void testWriteToOutputStream() throws Exception {
    Object model = runner.readFromString( json );
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    runner.writeToOutputStream( model, outputStream );

    byte[] result = outputStream.toByteArray();
    assertJsonCorrect( result );
  }

  @Test
  public void testWriteToOutputStream_doesNotCloseOutputStream() throws Exception {
    ByteArrayOutputStream outputStream = spy( new ByteArrayOutputStream() );
    Object model = runner.readFromString( json );

    runner.writeToOutputStream( model, outputStream );

    verify( outputStream, never() ).close();
  }

  @Test
  public void testWriteToOutputStream_doesNotFlushOutputStream() throws Exception {
    ByteArrayOutputStream outputStream = spy( new ByteArrayOutputStream() );
    Object model = runner.readFromString( json );

    runner.writeToOutputStream( model, outputStream );

    verify( outputStream, never() ).flush();
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
