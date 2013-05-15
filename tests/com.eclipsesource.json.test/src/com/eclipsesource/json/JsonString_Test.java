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
package com.eclipsesource.json;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import static com.eclipsesource.json.TestUtil.serializeAndDeserialize;
import static org.junit.Assert.*;


public class JsonString_Test {

  private StringWriter stringWriter;
  private JsonWriter jsonWriter;

  @Before
  public void setUp() {
    stringWriter = new StringWriter();
    jsonWriter = new JsonWriter( stringWriter );
  }

  @Test
  public void write() throws IOException {
    new JsonString( "foo" ).write( jsonWriter );

    assertEquals( "\"foo\"", stringWriter.toString() );
  }

  @Test
  public void write_escapesStrings() throws IOException {
    new JsonString( "foo\\bar" ).write( jsonWriter );

    assertEquals( "\"foo\\\\bar\"", stringWriter.toString() );
  }

  @Test
  public void isString() {
    assertTrue( new JsonString( "foo" ).isString() );
  }

  @Test
  public void asString() {
    assertEquals( "foo", new JsonString( "foo" ).asString() );
  }

  @Test
  public void equals_trueForSameInstance() {
    JsonString string = new JsonString( "foo" );

    assertTrue( string.equals( string ) );
  }

  @Test
  public void equals_trueForEqualStrings() {
    assertTrue( new JsonString( "foo" ).equals( new JsonString( "foo" ) ) );
  }

  @Test
  public void equals_falseForDifferentStrings() {
    assertFalse( new JsonString( "" ).equals( new JsonString( "foo" ) ) );
    assertFalse( new JsonString( "foo" ).equals( new JsonString( "bar" ) ) );
  }

  @Test
  public void equals_falseForNull() {
    assertFalse( new JsonString( "foo" ).equals( null ) );
  }

  @Test
  public void equals_falseForSubclass() {
    assertFalse( new JsonString( "foo" ).equals( new JsonString( "foo" ) {} ) );
  }

  @Test
  public void hashCode_equalsForEqualStrings() {
    assertTrue( new JsonString( "foo" ).hashCode() == new JsonString( "foo" ).hashCode() );
  }

  @Test
  public void hashCode_differsForDifferentStrings() {
    assertFalse( new JsonString( "" ).hashCode() == new JsonString( "foo" ).hashCode() );
    assertFalse( new JsonString( "foo" ).hashCode() == new JsonString( "bar" ).hashCode() );
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    JsonString string = new JsonString( "foo" );

    assertEquals( string, serializeAndDeserialize( string ) );
  }

}
