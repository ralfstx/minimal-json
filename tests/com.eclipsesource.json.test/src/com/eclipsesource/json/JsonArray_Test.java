/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource.
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

import static org.junit.Assert.*;


public class JsonArray_Test {

  private JsonArray array;
  private StringWriter output;
  private JsonWriter writer;

  @Before
  public void setUp() {
    array = new JsonArray();
    output = new StringWriter();
    writer = new JsonWriter( output );
  }

  @Test
  public void unmodifiableArray_hasSameValues() {
    array.append( 23 );
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray( array );

    assertArrayEquals( array.getValues(), unmodifiableArray.getValues() );
  }

  @Test
  public void unmodifiableArray_followsChanges() {
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray( array );
    array.append( 23 );

    assertArrayEquals( array.getValues(), unmodifiableArray.getValues() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void unmodifiableArray_preventsModification() {
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray( array );

    unmodifiableArray.append( 23 );
  }

  @Test
  public void isEmpty_isTrueAfterCreation() {
    assertTrue( array.isEmpty() );
  }

  @Test
  public void isEmpty_isFalseAfterAppend() {
    array.append( true );

    assertFalse( array.isEmpty() );
  }

  @Test
  public void size_isZeroAfterCreation() {
    assertEquals( 0, array.size() );
  }

  @Test
  public void size_isOneAfterAppend() {
    array.append( true );

    assertEquals( 1, array.size() );
  }

  @Test
  public void iterator_isEmptyAfterCreation() {
    assertFalse( array.iterator().hasNext() );
  }

  @Test
  public void iterator_hasNextAfterAppend() {
    array.append( true );

    Iterator<JsonValue> iterator = array.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( JsonValue.TRUE, iterator.next() );
    assertFalse( iterator.hasNext() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void iterator_doesNotAllowModification() {
    array.append( 23 );
    Iterator<JsonValue> iterator = array.iterator();
    iterator.next();
    iterator.remove();
  }

  @Test
  public void getValues_isEmptyAfterCreation() {
    assertEquals( 0, array.getValues().length );
  }

  @Test
  public void getValues_containsValueAfterAppend() {
    array.append( true );

    assertEquals( 1, array.getValues().length );
    assertEquals( JsonValue.TRUE, array.getValues()[ 0 ] );
  }

  @Test
  public void getValues_createsSafeCopy() {
    JsonValue[] values = array.getValues();

    array.append( true );

    assertEquals( 0, values.length );
  }

  @Test
  public void append_int() {
    array.append( 23 );

    assertEquals( "[23]", array.toString() );
  }

  @Test
  public void append_long() {
    array.append( 23l );

    assertEquals( "[23]", array.toString() );
  }

  @Test
  public void append_float() {
    array.append( 3.14f );

    assertEquals( "[3.14]", array.toString() );
  }

  @Test
  public void append_double() {
    array.append( 3.14d );

    assertEquals( "[3.14]", array.toString() );
  }

  @Test
  public void append_boolean() {
    array.append( false );

    assertEquals( "[false]", array.toString() );
  }

  @Test
  public void append_string() {
    array.append( "foo" );

    assertEquals( "[\"foo\"]", array.toString() );
  }

  @Test
  public void append_string_toleratesNull() {
    array.append( (String)null );

    assertEquals( "[null]", array.toString() );
  }

  @Test
  public void append_jsonNull() {
    array.append( JsonValue.NULL );

    assertEquals( "[null]", array.toString() );
  }

  @Test
  public void append_jsonArray() {
    array.append( new JsonArray() );

    assertEquals( "[[]]", array.toString() );
  }

  @Test
  public void append_jsonObject() {
    array.append( new JsonObject() );

    assertEquals( "[{}]", array.toString() );
  }

  @Test( expected = NullPointerException.class )
  public void append_json_failsWithNull() {
    array.append( (JsonValue)null );
  }

  @Test
  public void append_json_nestedArray() {
    JsonArray innerArray = new JsonArray();
    innerArray.append( 23 );

    array.append( innerArray );

    assertEquals( "[[23]]", array.toString() );
  }

  @Test
  public void append_json_nestedArray_modifiedAfterAppend() {
    JsonArray innerArray = new JsonArray();
    array.append( innerArray );

    innerArray.append( 23 );

    assertEquals( "[[23]]", array.toString() );
  }

  @Test
  public void append_json_nestedObject() {
    JsonObject innerObject = new JsonObject();
    innerObject.append( "a", 23 );

    array.append( innerObject );

    assertEquals( "[{\"a\":23}]", array.toString() );
  }

  @Test
  public void append_json_nestedObject_modifiedAfterAppend() {
    JsonObject innerObject = new JsonObject();
    array.append( innerObject );

    innerObject.append( "a", 23 );

    assertEquals( "[{\"a\":23}]", array.toString() );
  }

  @Test
  public void write_whenEmpty() throws IOException {
    array.write( writer );

    assertEquals( "[]", output.toString() );
  }

  @Test
  public void write_withSingleValue() throws IOException {
    array.append( 23 );

    array.write( writer );

    assertEquals( "[23]", output.toString() );
  }

  @Test
  public void write_withMultipleValues() throws IOException {
    array.append( 23 );
    array.append( "foo" );
    array.append( false );

    array.write( writer );

    assertEquals( "[23,\"foo\",false]", output.toString() );
  }

  @Test
  public void isArray() {
    assertTrue( array.isArray() );
  }

  @Test
  public void asArray() {
    assertSame( array, array.asArray() );
  }

}
