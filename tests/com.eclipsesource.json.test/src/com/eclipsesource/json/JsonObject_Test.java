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
  public void copyConstructor_failsWithNull() {
    assertException( NullPointerException.class, "object is null", new Runnable() {
      public void run() {
        new JsonObject( null );
      }
    } );
  }

  @Test
  public void copyConstructor_hasSameValues() {
    object.append( "foo", 23 );
    JsonObject copy = new JsonObject( object );

    assertArrayEquals( object.getNames(), copy.getNames() );
    assertSame( object.get( "foo" ), copy.get( "foo" ) );
  }

  @Test
  public void copyConstructor_worksOnSafeCopy() {
    JsonObject copy = new JsonObject( object );
    object.append( "foo", 23 );

    assertTrue( copy.isEmpty() );
  }

  @Test
  public void unmodifiableArray_hasSameValues() {
    object.append( "foo", 23 );
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject( object );

    assertArrayEquals( object.getNames(), unmodifiableObject.getNames() );
    assertSame( object.get( "foo" ), unmodifiableObject.get( "foo" ) );
  }

  @Test
  public void unmodifiableArray_followsChanges() {
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject( object );
    object.append( "foo", 23 );

    assertArrayEquals( object.getNames(), unmodifiableObject.getNames() );
    assertSame( object.get( "foo" ), unmodifiableObject.get( "foo" ) );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void unmodifiableArray_preventsModification() {
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject( object );

    unmodifiableObject.append( "foo", 23 );
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

  @Test( expected = UnsupportedOperationException.class )
  public void iterator_doesNotAllowModification() {
    object.append( "foo", true );
    Iterator<String> iterator = object.iterator();
    iterator.next();
    iterator.remove();
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
  public void get_failsWithNullName() {
    assertException( NullPointerException.class, "name is null", new Runnable() {
      public void run() {
        object.get( null );
      }
    } );
  }

  @Test
  public void get_returnsExistingValue() {
    object.append( "foo", true );

    assertSame( JsonValue.TRUE, object.get( "foo" ) );
  }

  @Test
  public void get_returnsNullForNonExistingValue() {
    assertSame( null, object.get( "foo" ) );
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
  public void append_int() {
    object.append( "a", 23 );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void append_int_enablesChaining() {
    assertSame( object, object.append( "a", 23 ) );
  }

  @Test
  public void append_long() {
    object.append( "a", 23l );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void append_long_enablesChaining() {
    assertSame( object, object.append( "a", 23l ) );
  }

  @Test
  public void append_float() {
    object.append( "a", 3.14f );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void append_float_enablesChaining() {
    assertSame( object, object.append( "a", 3.14f ) );
  }

  @Test
  public void append_double() {
    object.append( "a", 3.14d );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void append_double_enablesChaining() {
    assertSame( object, object.append( "a", 3.14d ) );
  }

  @Test
  public void append_boolean() {
    object.append( "a", true );

    assertEquals( "{\"a\":true}", object.toString() );
  }

  @Test
  public void append_boolean_enablesChaining() {
    assertSame( object, object.append( "a", true ) );
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
  public void append_string_enablesChaining() {
    assertSame( object, object.append( "a", "foo" ) );
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
  public void append_json_enablesChaining() {
    assertSame( object, object.append( "a", JsonValue.NULL ) );
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

  @Test
  public void equals_trueForSameInstance() {
    assertTrue( object.equals( object ) );
  }

  @Test
  public void equals_trueForEqualObjects() {
    assertTrue( object().equals( object() ) );
    assertTrue( object( "a", "1", "b", "2" ).equals( object( "a", "1", "b", "2" ) ) );
  }

  @Test
  public void equals_falseForDifferentObjects() {
    assertFalse( object( "a", "1" ).equals( object( "a", "2" ) ) );
    assertFalse( object( "a", "1" ).equals( object( "b", "1" ) ) );
    assertFalse( object( "a", "1", "b", "2" ).equals( object( "b", "2", "a", "1" ) ) );
  }

  @Test
  public void equals_falseForNull() {
    assertFalse( new JsonObject().equals( null ) );
  }

  @Test
  public void equals_falseForSubclass() {
    JsonObject jsonObject = new JsonObject();

    assertFalse( jsonObject.equals( new JsonObject( jsonObject ) {} ) );
  }

  @Test
  public void hashCode_equalsForEqualObjects() {
    assertTrue( object().hashCode() == object().hashCode() );
    assertTrue( object( "a", "1" ).hashCode() == object( "a", "1" ).hashCode() );
  }

  @Test
  public void hashCode_differsForDifferentObjects() {
    assertFalse( object().hashCode() == object( "a", "1" ).hashCode() );
    assertFalse( object( "a", "1" ).hashCode() == object( "a", "2" ).hashCode() );
    assertFalse( object( "a", "1" ).hashCode() == object( "b", "1" ).hashCode() );
  }

  private static JsonObject object( String... namesAndValues ) {
    JsonObject object = new JsonObject();
    for( int i = 0; i < namesAndValues.length; i += 2 ) {
      object.append( namesAndValues[i], namesAndValues[i + 1] );
    }
    return object;
  }

}
