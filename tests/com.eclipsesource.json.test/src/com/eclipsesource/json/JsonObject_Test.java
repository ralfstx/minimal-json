/*******************************************************************************
 * Copyright (c) 2008, 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import static com.eclipsesource.json.TestUtil.assertException;
import static org.junit.Assert.*;


public class JsonObject_Test {

  private JsonObject object;
  private StringWriter output;
  private JsonWriter writer;

  @Before
  public void setUp() {
    object = new JsonObject();
    output = new StringWriter();
    writer = new JsonWriter( output );
  }

  @Test
  public void isEmpty_trueAfterCreation() {
    assertTrue( object.isEmpty() );
  }

  @Test
  public void isEmpty_falseAfterAppend() {
    object.append( "a", true );

    assertFalse( object.isEmpty() );
  }

  @Test
  public void size_zeroAfterCreation() {
    assertEquals( 0, object.size() );
  }

  @Test
  public void size_oneAfterAppend() {
    object.append( "a", true );

    assertEquals( 1, object.size() );
  }

  @Test
  public void iterator_emptyAfterCreation() {
    assertFalse( object.iterator().hasNext() );
  }

  @Test
  public void iterator_afterAppend() {
    object.append( "foo", true );

    Iterator<String> iterator = object.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( "foo", iterator.next() );
    assertFalse( iterator.hasNext() );
  }

  @Test
  public void getNames_emptyAfterCreation() {
    assertEquals( 0, object.getNames().length );
  }

  @Test
  public void getNames_afterAppend() {
    object.append( "foo", true );

    assertEquals( 1, object.getNames().length );
    assertEquals( "foo", object.getNames()[ 0 ] );
  }

  @Test
  public void getNames_createsCopy() {
    object.append( "foo", true );
    String[] names = object.getNames();

    object.append( "bar", false );

    assertEquals( 1, names.length );
    assertEquals( "foo", names[ 0 ] );
  }

  @Test
  public void getValue_failsWithNullName() {
    assertException( NullPointerException.class, "name is null", new Runnable() {
      public void run() {
        object.getValue( null );
      }
    } );
  }

  @Test
  public void getValue_returnsExistingValue() {
    object.append( "foo", true );

    assertSame( JsonValue.TRUE, object.getValue( "foo" ) );
  }

  @Test
  public void getValue_returnsNullForNonExistingValue() {
    assertSame( null, object.getValue( "foo" ) );
  }

  @Test
  public void append_failsWithNullName() {
    assertException( NullPointerException.class, "name is null", new Runnable() {
      public void run() {
        object.append( null, 23 );
      }
    } );
  }

  @Test
  public void append_long() {
    object.append( "a", 23l );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void append_float() {
    object.append( "a", 3.14f );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void append_double() {
    object.append( "a", 3.14d );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void append_boolean() {
    object.append( "a", false );

    assertEquals( "{\"a\":false}", object.toString() );
  }

  @Test
  public void append_string() {
    object.append( "a", "foo" );

    assertEquals( "{\"a\":\"foo\"}", object.toString() );
  }

  @Test
  public void append_string_toleratesNull() {
    object.append( "a", (String)null );

    assertEquals( "{\"a\":null}", object.toString() );
  }

  @Test
  public void append_jsonNull() {
    object.append( "a", JsonValue.NULL );

    assertEquals( "{\"a\":null}", object.toString() );
  }

  @Test
  public void append_jsonArray() {
    object.append( "a", new JsonArray() );

    assertEquals( "{\"a\":[]}", object.toString() );
  }

  @Test
  public void append_jsonObject() {
    object.append( "a", new JsonObject() );

    assertEquals( "{\"a\":{}}", object.toString() );
  }

  @Test
  public void append_json_failsWithNull() {
    assertException( NullPointerException.class, "value is null", new Runnable() {
      public void run() {
        object.append( "a", (JsonValue)null );
      }
    } );
  }

  @Test
  public void append_json_nestedArray() {
    JsonArray innerArray = new JsonArray();
    innerArray.append( 23 );

    object.append( "a", innerArray );

    assertEquals( "{\"a\":[23]}", object.toString() );
  }

  @Test
  public void append_json_nestedArray_modifiedAfterAppend() {
    JsonArray innerArray = new JsonArray();
    object.append( "a", innerArray );

    innerArray.append( 23 );

    assertEquals( "{\"a\":[23]}", object.toString() );
  }

  @Test
  public void append_json_nestedObject() {
    JsonObject innerObject = new JsonObject();
    innerObject.append( "a", 23 );

    object.append( "a", innerObject );

    assertEquals( "{\"a\":{\"a\":23}}", object.toString() );
  }

  @Test
  public void append_json_nestedObject_modifiedAfterAppend() {
    JsonObject innerObject = new JsonObject();
    object.append( "a", innerObject );

    innerObject.append( "a", 23 );

    assertEquals( "{\"a\":{\"a\":23}}", object.toString() );
  }

  @Test
  public void write_whenEmpty() throws IOException {
    object.write( writer );

    assertEquals( "{}", output.toString() );
  }

  @Test
  public void write_withSingleValue() throws IOException {
    object.append( "a", 23 );

    object.write( writer );

    assertEquals( "{\"a\":23}", output.toString() );
  }

  @Test
  public void write_withMultipleValues() throws IOException {
    object.append( "a", 23 );
    object.append( "b", 3.14f );
    object.append( "c", "foo" );
    object.append( "d", true );
    object.append( "e", ( String )null );

    object.write( writer );

    assertEquals( "{\"a\":23,\"b\":3.14,\"c\":\"foo\",\"d\":true,\"e\":null}",
                  output.toString() );
  }

  @Test
  public void isObject() {
    assertTrue( object.isObject() );
  }

  @Test
  public void asObject() {
    assertSame( object, object.asObject() );
  }

}
