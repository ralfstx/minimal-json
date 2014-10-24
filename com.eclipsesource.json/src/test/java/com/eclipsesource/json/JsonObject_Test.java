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

import com.eclipsesource.json.JsonObject.HashIndexTable;
import com.eclipsesource.json.JsonObject.Member;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.eclipsesource.json.TestUtil.assertException;
import static com.eclipsesource.json.TestUtil.serializeAndDeserialize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class JsonObject_Test {

  private JsonObject object;

  @Before
  public void setUp() {
    object = new JsonObject();
  }

  @Test
  public void copyConstructor_failsWithNull() {
    assertException( NullPointerException.class, "object is null", () -> new JsonObject( null ));
  }

  @Test
  public void copyConstructor_hasSameValues() {
    object.add( "foo", 23 );
    JsonObject copy = new JsonObject( object );

    assertEquals( object.names(), copy.names() );
    assertSame( object.get( "foo" ), copy.get( "foo" ) );
  }

  @Test
  public void copyConstructor_worksOnSafeCopy() {
    JsonObject copy = new JsonObject( object );
    object.add( "foo", 23 );

    assertTrue( copy.isEmpty() );
  }

  @Test
  public void unmodifiableObject_hasSameValues() {
    object.add( "foo", 23 );
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject( object );

    assertEquals( object.names(), unmodifiableObject.names() );
    assertSame( object.get( "foo" ), unmodifiableObject.get( "foo" ) );
  }

  @Test
  public void unmodifiableObject_reflectsChanges() {
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject( object );
    object.add( "foo", 23 );

    assertEquals( object.names(), unmodifiableObject.names() );
    assertSame( object.get( "foo" ), unmodifiableObject.get( "foo" ) );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void unmodifiableObject_preventsModification() {
    JsonObject unmodifiableObject = JsonObject.unmodifiableObject( object );

    unmodifiableObject.add( "foo", 23 );
  }

  @Test
  public void readFrom_reader() throws IOException {
    assertEquals( new JsonObject(), JsonObject.readFrom( new StringReader( "{}" ) ) );
    assertEquals( new JsonObject().add( "a", 23 ),
                  JsonObject.readFrom( new StringReader( "{ \"a\": 23 }" ) ) );
  }

  @Test
  public void readFrom_string() {
    assertEquals( new JsonObject(), JsonObject.readFrom( "{}" ) );
    assertEquals( new JsonObject().add( "a", 23 ), JsonObject.readFrom( "{ \"a\": 23 }" ) );
  }

  @Test
  public void isEmpty_trueAfterCreation() {
    assertTrue( object.isEmpty() );
  }

  @Test
  public void isEmpty_falseAfterAdd() {
    object.add( "a", true );

    assertFalse( object.isEmpty() );
  }

  @Test
  public void size_zeroAfterCreation() {
    assertEquals( 0, object.size() );
  }

  @Test
  public void size_oneAfterAdd() {
    object.add( "a", true );

    assertEquals( 1, object.size() );
  }

  @Test
  public void names_emptyAfterCreation() {
    assertTrue( object.names().isEmpty() );
  }

  @Test
  public void names_containsNameAfterAdd() {
    object.add( "foo", true );

    List<String> names = object.names();
    assertEquals( 1, names.size() );
    assertEquals( "foo", names.get( 0 ) );
  }

  @Test
  public void names_reflectsChanges() {
    List<String> names = object.names();

    object.add( "foo", true );

    assertEquals( 1, names.size() );
    assertEquals( "foo", names.get( 0 ) );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void names_preventsModification() {
    List<String> names = object.names();

    names.add( "foo" );
  }

  @Test
  public void iterator_isEmptyAfterCreation() {
    assertFalse( object.iterator().hasNext() );
  }

  @Test
  public void iterator_hasNextAfterAdd() {
    object.add( "a", true );
    Iterator<Member> iterator = object.iterator();

    assertTrue( iterator.hasNext() );
  }

  @Test
  public void iterator_nextReturnsActualValue() {
    object.add( "a", true );
    Iterator<Member> iterator = object.iterator();

    assertEquals( new Member( "a", JsonValue.TRUE ), iterator.next() );
  }

  @Test
  public void iterator_nextProgressesToNextValue() {
    object.add( "a", true );
    object.add( "b", false );
    Iterator<Member> iterator = object.iterator();

    iterator.next();
    assertTrue( iterator.hasNext() );
    assertEquals( new Member( "b", JsonValue.FALSE ), iterator.next() );
  }

  @Test( expected = NoSuchElementException.class )
  public void iterator_nextFailsAtEnd() {
    Iterator<Member> iterator = object.iterator();

    iterator.next();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void iterator_doesNotAllowModification() {
    object.add( "a", 23 );
    Iterator<Member> iterator = object.iterator();
    iterator.next();

    iterator.remove();
  }

  @Test( expected = ConcurrentModificationException.class )
  public void iterator_detectsConcurrentModification() {
    Iterator<Member> iterator = object.iterator();
    object.add( "a", 23 );
    iterator.next();
  }

  @Test
  public void get_failsWithNullName() {
    assertException( NullPointerException.class, "name is null", () -> object.get( null ));
  }

  @Test
  public void get_returnsNullForNonExistingMember() {
    assertNull( object.get( "foo" ) );
  }

  @Test
  public void get_returnsValueForName() {
    object.add( "foo", true );

    assertEquals( JsonValue.TRUE, object.get( "foo" ) );
  }

  @Test
  public void get_returnsLastValueForName() {
    object.add( "foo", false ).add( "foo", true );

    assertEquals( JsonValue.TRUE, object.get( "foo" ) );
  }

  @Test
  public void add_failsWithNullName() {
    assertException( NullPointerException.class, "name is null", () -> object.add( null, 23 ));
  }

  @Test
  public void add_int() {
    object.add( "a", 23 );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void add_int_enablesChaining() {
    assertSame( object, object.add( "a", 23 ) );
  }

  @Test
  public void add_long() {
    object.add( "a", 23l );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void add_long_enablesChaining() {
    assertSame( object, object.add( "a", 23l ) );
  }

  @Test
  public void add_float() {
    object.add( "a", 3.14f );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void add_float_enablesChaining() {
    assertSame( object, object.add( "a", 3.14f ) );
  }

  @Test
  public void add_double() {
    object.add( "a", 3.14d );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void add_double_enablesChaining() {
    assertSame( object, object.add( "a", 3.14d ) );
  }

  @Test
  public void add_boolean() {
    object.add( "a", true );

    assertEquals( "{\"a\":true}", object.toString() );
  }

  @Test
  public void add_boolean_enablesChaining() {
    assertSame( object, object.add( "a", true ) );
  }

  @Test
  public void add_string() {
    object.add( "a", "foo" );

    assertEquals( "{\"a\":\"foo\"}", object.toString() );
  }

  @Test
  public void add_string_toleratesNull() {
    object.add( "a", (String)null );

    assertEquals( "{\"a\":null}", object.toString() );
  }

  @Test
  public void add_string_enablesChaining() {
    assertSame( object, object.add( "a", "foo" ) );
  }

  @Test
  public void add_jsonNull() {
    object.add( "a", JsonValue.NULL );

    assertEquals( "{\"a\":null}", object.toString() );
  }

  @Test
  public void add_jsonArray() {
    object.add( "a", new JsonArray() );

    assertEquals( "{\"a\":[]}", object.toString() );
  }

  @Test
  public void add_jsonObject() {
    object.add( "a", new JsonObject() );

    assertEquals( "{\"a\":{}}", object.toString() );
  }

  @Test
  public void add_json_enablesChaining() {
    assertSame( object, object.add( "a", JsonValue.NULL ) );
  }

  @Test
  public void add_json_failsWithNull() {
    assertException( NullPointerException.class, "value is null", () -> object.add( "a", (JsonValue)null ));
  }

  @Test
  public void add_json_nestedArray() {
    JsonArray innerArray = new JsonArray();
    innerArray.add( 23 );

    object.add( "a", innerArray );

    assertEquals( "{\"a\":[23]}", object.toString() );
  }

  @Test
  public void add_json_nestedArray_modifiedAfterAdd() {
    JsonArray innerArray = new JsonArray();
    object.add( "a", innerArray );

    innerArray.add( 23 );

    assertEquals( "{\"a\":[23]}", object.toString() );
  }

  @Test
  public void add_json_nestedObject() {
    JsonObject innerObject = new JsonObject();
    innerObject.add( "a", 23 );

    object.add( "a", innerObject );

    assertEquals( "{\"a\":{\"a\":23}}", object.toString() );
  }

  @Test
  public void add_json_nestedObject_modifiedAfterAdd() {
    JsonObject innerObject = new JsonObject();
    object.add( "a", innerObject );

    innerObject.add( "a", 23 );

    assertEquals( "{\"a\":{\"a\":23}}", object.toString() );
  }

  @Test
  public void set_int() {
    object.set( "a", 23 );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void set_int_enablesChaining() {
    assertSame( object, object.set( "a", 23 ) );
  }

  @Test
  public void set_long() {
    object.set( "a", 23l );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void set_long_enablesChaining() {
    assertSame( object, object.set( "a", 23l ) );
  }

  @Test
  public void set_float() {
    object.set( "a", 3.14f );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void set_float_enablesChaining() {
    assertSame( object, object.set( "a", 3.14f ) );
  }

  @Test
  public void set_double() {
    object.set( "a", 3.14d );

    assertEquals( "{\"a\":3.14}", object.toString() );
  }

  @Test
  public void set_double_enablesChaining() {
    assertSame( object, object.set( "a", 3.14d ) );
  }

  @Test
  public void set_boolean() {
    object.set( "a", true );

    assertEquals( "{\"a\":true}", object.toString() );
  }

  @Test
  public void set_boolean_enablesChaining() {
    assertSame( object, object.set( "a", true ) );
  }

  @Test
  public void set_string() {
    object.set( "a", "foo" );

    assertEquals( "{\"a\":\"foo\"}", object.toString() );
  }

  @Test
  public void set_string_enablesChaining() {
    assertSame( object, object.set( "a", "foo" ) );
  }

  @Test
  public void set_jsonNull() {
    object.set( "a", JsonValue.NULL );

    assertEquals( "{\"a\":null}", object.toString() );
  }

  @Test
  public void set_jsonArray() {
    object.set( "a", new JsonArray() );

    assertEquals( "{\"a\":[]}", object.toString() );
  }

  @Test
  public void set_jsonObject() {
    object.set( "a", new JsonObject() );

    assertEquals( "{\"a\":{}}", object.toString() );
  }

  @Test
  public void set_json_enablesChaining() {
    assertSame( object, object.set( "a", JsonValue.NULL ) );
  }

  @Test
  public void set_addsElementIfMissing() {
    object.set( "a", JsonValue.TRUE );

    assertEquals( "{\"a\":true}", object.toString() );
  }

  @Test
  public void set_modifiesElementIfExisting() {
    object.add( "a", JsonValue.TRUE );

    object.set( "a", JsonValue.FALSE );

    assertEquals( "{\"a\":false}", object.toString() );
  }

  @Test
  public void set_modifiesLastElementIfMultipleExisting() {
    object.add( "a", 1 );
    object.add( "a", 2 );

    object.set( "a", JsonValue.TRUE );

    assertEquals( "{\"a\":1,\"a\":true}", object.toString() );
  }

  @Test
  public void remove_failsWithNullName() {
    assertException( NullPointerException.class, "name is null", () -> object.remove( null ));
  }

  @Test
  public void remove_removesMatchingMember() {
    object.add( "a", 23 );

    object.remove( "a" );

    assertEquals( "{}", object.toString() );
  }

  @Test
  public void remove_removesOnlyMatchingMember() {
    object.add( "a", 23 );
    object.add( "b", 42 );
    object.add( "c", true );

    object.remove( "b" );

    assertEquals( "{\"a\":23,\"c\":true}", object.toString() );
  }

  @Test
  public void remove_removesOnlyLastMatchingMember() {
    object.add( "a", 23 );
    object.add( "a", 42 );

    object.remove( "a" );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void remove_removesOnlyLastMatchingMember_afterRemove() {
    object.add( "a", 23 );
    object.remove( "a" );
    object.add( "a", 42 );
    object.add( "a", 47 );

    object.remove( "a" );

    assertEquals( "{\"a\":42}", object.toString() );
  }

  @Test
  public void remove_doesNotModifyObjectWithoutMatchingMember() {
    object.add( "a", 23 );

    object.remove( "b" );

    assertEquals( "{\"a\":23}", object.toString() );
  }

  @Test
  public void write_delegatesToJsonWriter() throws IOException {
    JsonWriter writer = mock( JsonWriter.class );

    object.write( writer );

    verify( writer ).writeObject( same( object ) );
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

  @Test
  public void indexOf_returnsNoIndexIfEmpty() {
    assertEquals( -1, object.indexOf( "a" ) );
  }

  @Test
  public void indexOf_returnsIndexOfMember() {
    object.add( "a", true );

    assertEquals( 0, object.indexOf( "a" ) );
  }

  @Test
  public void indexOf_returnsIndexOfLastMember() {
    object.add( "a", true );
    object.add( "a", true );

    assertEquals( 1, object.indexOf( "a" ) );
  }

  @Test
  public void indexOf_returnsIndexOfLastMember_afterRemove() {
    object.add( "a", true );
    object.add( "a", true );
    object.remove( "a" );

    assertEquals( 0, object.indexOf( "a" ) );
  }

  @Test
  public void indexOf_returnsUpdatedIndexAfterRemove() {
    // See issue #16
    object.add( "a", true );
    object.add( "b", true );
    object.remove( "a" );

    assertEquals( 0, object.indexOf( "b" ) );
  }

  @Test
  public void indexOf_returnsIndexOfLastMember_forBigObject() {
    object.add( "a", true );
    // for indexes above 255, the hash index table does not return a value
    for( int i = 0; i < 256; i++ ) {
      object.add( "x-" + i, 0 );
    }
    object.add( "a", true );

    assertEquals( 257, object.indexOf( "a" ) );
  }

  @Test
  public void hashIndexTable_copyConstructor() {
    HashIndexTable original = new HashIndexTable();
    original.add( "name", 23 );

    HashIndexTable copy = new HashIndexTable( original );

    assertEquals( 23, copy.get( "name" ) );
  }

  @Test
  public void hashIndexTable_add() {
    HashIndexTable indexTable = new HashIndexTable();

    indexTable.add( "name-0", 0 );
    indexTable.add( "name-1", 1 );
    indexTable.add( "name-fe", 0xfe );
    indexTable.add( "name-ff", 0xff );

    assertEquals( 0, indexTable.get( "name-0" ) );
    assertEquals( 1, indexTable.get( "name-1" ) );
    assertEquals( 0xfe, indexTable.get( "name-fe" ) );
    assertEquals( -1, indexTable.get( "name-ff" ) );
  }

  @Test
  public void hashIndexTable_add_overwritesPreviousValue() {
    HashIndexTable indexTable = new HashIndexTable();

    indexTable.add( "name", 23 );
    indexTable.add( "name", 42 );

    assertEquals( 42, indexTable.get( "name" ) );
  }

  @Test
  public void hashIndexTable_add_clearsPreviousValueIfIndexExceeds0xff() {
    HashIndexTable indexTable = new HashIndexTable();

    indexTable.add( "name", 23 );
    indexTable.add( "name", 300 );

    assertEquals( -1, indexTable.get( "name" ) );
  }

  @Test
  public void hashIndexTable_remove() {
    HashIndexTable indexTable = new HashIndexTable();

    indexTable.add( "name", 23 );
    indexTable.remove( 23 );

    assertEquals( -1, indexTable.get( "name" ) );
  }

  @Test
  public void hashIndexTable_remove_updatesSubsequentElements() {
    HashIndexTable indexTable = new HashIndexTable();

    indexTable.add( "foo", 23 );
    indexTable.add( "bar", 42 );
    indexTable.remove( 23 );

    assertEquals( 41, indexTable.get( "bar" ) );
  }

  @Test
  public void hashIndexTable_remove_doesNotChangePrecedingElements() {
    HashIndexTable indexTable = new HashIndexTable();

    indexTable.add( "foo", 23 );
    indexTable.add( "bar", 42 );
    indexTable.remove( 42 );

    assertEquals( 23, indexTable.get( "foo" ) );
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    object.add( "foo", 23 ).add( "bar", new JsonObject().add( "a", 3.14d ).add( "b", true ) );

    assertEquals( object, serializeAndDeserialize( object ) );
  }

  @Test
  public void deserializedObjectCanBeAccessed() throws Exception {
    object.add( "foo", 23 );

    JsonObject deserializedObject = serializeAndDeserialize( object );

    assertEquals( 23, deserializedObject.get( "foo" ).asInt() );
  }

  @Test
  public void member_returnsNameAndValue() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertEquals( "a", member.getName() );
    assertEquals( JsonValue.TRUE, member.getValue() );
  }

  @Test
  public void member_equals_trueForSameInstance() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertTrue( member.equals( member ) );
  }

  @Test
  public void member_equals_trueForEqualObjects() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertTrue( member.equals( new Member( "a", JsonValue.TRUE ) ) );
  }

  @Test
  public void member_equals_falseForDifferingObjects() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertFalse( member.equals( new Member( "b", JsonValue.TRUE ) ) );
    assertFalse( member.equals( new Member( "a", JsonValue.FALSE ) ) );
  }

  @Test
  public void member_equals_falseForNull() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertFalse( member.equals( null ) );
  }

  @Test
  public void member_equals_falseForSubclass() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertFalse( member.equals( new Member( "a", JsonValue.TRUE ) {} ) );
  }

  @Test
  public void member_hashCode_equalsForEqualObjects() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertTrue( member.hashCode() == new Member( "a", JsonValue.TRUE ).hashCode() );
  }

  @Test
  public void member_hashCode_differsForDifferingobjects() {
    Member member = new Member( "a", JsonValue.TRUE );

    assertFalse( member.hashCode() == new Member( "b", JsonValue.TRUE ).hashCode() );
    assertFalse( member.hashCode() == new Member( "a", JsonValue.FALSE ).hashCode() );
  }

  private static JsonObject object( String... namesAndValues ) {
    JsonObject object = new JsonObject();
    for( int i = 0; i < namesAndValues.length; i += 2 ) {
      object.add( namesAndValues[i], namesAndValues[i + 1] );
    }
    return object;
  }

}
