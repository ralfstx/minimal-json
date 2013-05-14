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
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static com.eclipsesource.json.TestUtil.assertException;
import static com.eclipsesource.json.TestUtil.serializeAndDeserialize;
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
  public void copyConstructor_failsWithNull() {
    assertException( NullPointerException.class, "array is null", new Runnable() {
      public void run() {
        new JsonArray( null );
      }
    } );
  }

  @Test
  public void copyConstructor_hasSameValues() {
    array.add( 23 );
    JsonArray copy = new JsonArray( array );

    assertEquals( array.values(), copy.values() );
  }

  @Test
  public void copyConstructor_worksOnSafeCopy() {
    JsonArray copy = new JsonArray( array );
    array.add( 23 );

    assertTrue( copy.isEmpty() );
  }

  @Test
  public void unmodifiableArray_hasSameValues() {
    array.add( 23 );
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray( array );

    assertEquals( array.values(), unmodifiableArray.values() );
  }

  @Test
  public void unmodifiableArray_reflectsChanges() {
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray( array );
    array.add( 23 );

    assertEquals( array.values(), unmodifiableArray.values() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void unmodifiableArray_preventsModification() {
    JsonArray unmodifiableArray = JsonArray.unmodifiableArray( array );

    unmodifiableArray.add( 23 );
  }

  @Test
  public void isEmpty_isTrueAfterCreation() {
    assertTrue( array.isEmpty() );
  }

  @Test
  public void isEmpty_isFalseAfterAdd() {
    array.add( true );

    assertFalse( array.isEmpty() );
  }

  @Test
  public void size_isZeroAfterCreation() {
    assertEquals( 0, array.size() );
  }

  @Test
  public void size_isOneAfterAdd() {
    array.add( true );

    assertEquals( 1, array.size() );
  }

  @Test
  public void iterator_isEmptyAfterCreation() {
    assertFalse( array.iterator().hasNext() );
  }

  @Test
  public void iterator_hasNextAfterAdd() {
    array.add( true );

    Iterator<JsonValue> iterator = array.iterator();
    assertTrue( iterator.hasNext() );
    assertEquals( JsonValue.TRUE, iterator.next() );
    assertFalse( iterator.hasNext() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void iterator_doesNotAllowModification() {
    array.add( 23 );
    Iterator<JsonValue> iterator = array.iterator();
    iterator.next();
    iterator.remove();
  }

  @Test
  public void values_isEmptyAfterCreation() {
    assertTrue( array.values().isEmpty() );
  }

  @Test
  public void values_containsValueAfterAdd() {
    array.add( true );

    assertEquals( 1, array.values().size() );
    assertEquals( JsonValue.TRUE, array.values().get( 0 ) );
  }

  @Test
  public void values_reflectsChanges() {
    List<JsonValue> values = array.values();

    array.add( true );

    assertEquals( array.values(), values );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void values_preventsModification() {
    List<JsonValue> values = array.values();

    values.add( JsonValue.TRUE );
  }

  @Test
  public void get_returnsValue() {
    array.add( 23 );

    JsonValue value = array.get( 0 );

    assertEquals( JsonValue.valueOf( 23 ), value );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void get_failsWithInvalidIndex() {
    array.get( 0 );
  }

  @Test
  public void add_int() {
    array.add( 23 );

    assertEquals( "[23]", array.toString() );
  }

  @Test
  public void add_int_enablesChaining() {
    assertSame( array, array.add( 23 ) );
  }

  @Test
  public void add_long() {
    array.add( 23l );

    assertEquals( "[23]", array.toString() );
  }

  @Test
  public void add_long_enablesChaining() {
    assertSame( array, array.add( 23l ) );
  }

  @Test
  public void add_float() {
    array.add( 3.14f );

    assertEquals( "[3.14]", array.toString() );
  }

  @Test
  public void add_float_enablesChaining() {
    assertSame( array, array.add( 3.14f ) );
  }

  @Test
  public void add_double() {
    array.add( 3.14d );

    assertEquals( "[3.14]", array.toString() );
  }

  @Test
  public void add_double_enablesChaining() {
    assertSame( array, array.add( 3.14d ) );
  }

  @Test
  public void add_boolean() {
    array.add( true );

    assertEquals( "[true]", array.toString() );
  }

  @Test
  public void add_boolean_enablesChaining() {
    assertSame( array, array.add( true ) );
  }

  @Test
  public void add_string() {
    array.add( "foo" );

    assertEquals( "[\"foo\"]", array.toString() );
  }

  @Test
  public void add_string_enablesChaining() {
    assertSame( array, array.add( "foo" ) );
  }

  @Test
  public void add_string_toleratesNull() {
    array.add( (String)null );

    assertEquals( "[null]", array.toString() );
  }

  @Test
  public void add_jsonNull() {
    array.add( JsonValue.NULL );

    assertEquals( "[null]", array.toString() );
  }

  @Test
  public void add_jsonArray() {
    array.add( new JsonArray() );

    assertEquals( "[[]]", array.toString() );
  }

  @Test
  public void add_jsonObject() {
    array.add( new JsonObject() );

    assertEquals( "[{}]", array.toString() );
  }

  @Test
  public void add_json_enablesChaining() {
    assertSame( array, array.add( JsonValue.NULL ) );
  }

  @Test( expected = NullPointerException.class )
  public void add_json_failsWithNull() {
    array.add( (JsonValue)null );
  }

  @Test
  public void add_json_nestedArray() {
    JsonArray innerArray = new JsonArray();
    innerArray.add( 23 );

    array.add( innerArray );

    assertEquals( "[[23]]", array.toString() );
  }

  @Test
  public void add_json_nestedArray_modifiedAfterAdd() {
    JsonArray innerArray = new JsonArray();
    array.add( innerArray );

    innerArray.add( 23 );

    assertEquals( "[[23]]", array.toString() );
  }

  @Test
  public void add_json_nestedObject() {
    JsonObject innerObject = new JsonObject();
    innerObject.add( "a", 23 );

    array.add( innerObject );

    assertEquals( "[{\"a\":23}]", array.toString() );
  }

  @Test
  public void add_json_nestedObject_modifiedAfterAdd() {
    JsonObject innerObject = new JsonObject();
    array.add( innerObject );

    innerObject.add( "a", 23 );

    assertEquals( "[{\"a\":23}]", array.toString() );
  }

  @Test
  public void write_whenEmpty() throws IOException {
    array.write( writer );

    assertEquals( "[]", output.toString() );
  }

  @Test
  public void write_withSingleValue() throws IOException {
    array.add( 23 );

    array.write( writer );

    assertEquals( "[23]", output.toString() );
  }

  @Test
  public void write_withMultipleValues() throws IOException {
    array.add( 23 );
    array.add( "foo" );
    array.add( false );

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

  @Test
  public void equals_trueForSameInstance() {
    assertTrue( array.equals( array ) );
  }

  @Test
  public void equals_trueForEqualArrays() {
    assertTrue( array().equals( array() ) );
    assertTrue( array( "foo", "bar" ).equals( array( "foo", "bar" ) ) );
  }

  @Test
  public void equals_falseForDifferentArrays() {
    assertFalse( array( "foo", "bar" ).equals( array( "foo", "bar", "baz" ) ) );
    assertFalse( array( "foo", "bar" ).equals( array( "bar", "foo" ) ) );
  }

  @Test
  public void equals_falseForNull() {
    assertFalse( array.equals( null ) );
  }

  @Test
  public void equals_falseForSubclass() {
    assertFalse( array.equals( new JsonArray( array ) {} ) );
  }

  @Test
  public void hashCode_equalsForEqualArrays() {
    assertTrue( array().hashCode() == array().hashCode() );
    assertTrue( array( "foo" ).hashCode() == array( "foo" ).hashCode() );
  }

  @Test
  public void hashCode_differsForDifferentArrays() {
    assertFalse( array().hashCode() == array( "bar" ).hashCode() );
    assertFalse( array( "foo" ).hashCode() == array( "bar" ).hashCode() );
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    array.add( true ).add( 3.14d ).add( 23 ).add( "foo" ).add( new JsonArray().add( false ) );

    assertEquals( array, serializeAndDeserialize( array ) );
  }

  @Test
  public void deserializedArrayCanBeAccessed() throws Exception {
    array.add( 23 );

    JsonArray deserializedArray = serializeAndDeserialize( array );

    assertEquals( 23, deserializedArray.get( 0 ).asInt() );
  }

  private static JsonArray array( String... values ) {
    JsonArray array = new JsonArray();
    for( String value : values ) {
      array.add( value );
    }
    return array;
  }

}
