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


public class JsonLiteral_Test {

  private StringWriter stringWriter;
  private JsonWriter jsonWriter;

  @Before
  public void setUp() {
    stringWriter = new StringWriter();
    jsonWriter = new JsonWriter( stringWriter );
  }

  @Test
  public void write_NULL() throws IOException {
    JsonValue.NULL.write( jsonWriter );

    assertEquals( "null", stringWriter.toString() );
  }

  @Test
  public void write_TRUE() throws IOException {
    JsonValue.TRUE.write( jsonWriter );

    assertEquals( "true", stringWriter.toString() );
  }

  @Test
  public void write_FALSE() throws IOException {
    JsonValue.FALSE.write( jsonWriter );

    assertEquals( "false", stringWriter.toString() );
  }

  @Test
  public void toString_NULL() {
    assertEquals( "null", JsonValue.NULL.toString() );
  }

  @Test
  public void toString_TRUE() {
    assertEquals( "true", JsonValue.TRUE.toString() );
  }

  @Test
  public void toString_FALSE() {
    assertEquals( "false", JsonValue.FALSE.toString() );
  }

  @Test
  public void asBoolean() {
    assertTrue( JsonValue.TRUE.asBoolean() );
    assertFalse( JsonValue.FALSE.asBoolean() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void asBoolean_failsIfNotBoolean() {
    JsonValue.NULL.asBoolean();
  }

  @Test
  public void isNull() {
    assertTrue( JsonValue.NULL.isNull() );
    assertFalse( JsonValue.TRUE.isNull() );
    assertFalse( JsonValue.FALSE.isNull() );
  }

  @Test
  public void isBoolean() {
    assertFalse( JsonValue.NULL.isBoolean() );
    assertTrue( JsonValue.TRUE.isBoolean() );
    assertTrue( JsonValue.FALSE.isBoolean() );
  }

  @Test
  public void isTrue() {
    assertFalse( JsonValue.NULL.isTrue() );
    assertTrue( JsonValue.TRUE.isTrue() );
    assertFalse( JsonValue.FALSE.isTrue() );
  }

  @Test
  public void isFalse() {
    assertFalse( JsonValue.NULL.isFalse() );
    assertFalse( JsonValue.TRUE.isFalse() );
    assertTrue( JsonValue.FALSE.isFalse() );
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    assertEquals( JsonValue.NULL, serializeAndDeserialize( JsonValue.NULL ) );
    assertEquals( JsonValue.TRUE, serializeAndDeserialize( JsonValue.TRUE ) );
    assertEquals( JsonValue.FALSE, serializeAndDeserialize( JsonValue.FALSE ) );
  }

}
