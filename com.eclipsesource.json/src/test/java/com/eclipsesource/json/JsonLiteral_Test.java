/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource.
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
package com.eclipsesource.json;

import static com.eclipsesource.json.TestUtil.serializeAndDeserialize;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;


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
    new JsonLiteral( "foo" ).asBoolean();
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
  public void equals_trueForSameInstance() {
    JsonLiteral literal = new JsonLiteral( "foo" );
    assertTrue( literal.equals( literal ) );
  }

  @Test
  public void equals_trueForEqualObjects() {
    assertTrue( new JsonLiteral( "foo" ).equals( new JsonLiteral( "foo" ) ) );
  }

  @Test
  public void equals_falseForDifferentArrays() {
    assertFalse( new JsonLiteral( "foo" ).equals( new JsonLiteral( "bar" ) ) );
  }

  @Test
  public void equals_falseForNull() {
    assertFalse( new JsonLiteral( "foo" ).equals( null ) );
  }

  @Test
  public void equals_falseForSubclass() {
    assertFalse( new JsonLiteral( "foo" ).equals( new JsonLiteral( "foo" ) {} ) );
  }

  @Test
  public void hashCode_equalsForEqualObjects() {
    assertTrue( new JsonLiteral( "foo" ).hashCode() == new JsonLiteral( "foo" ).hashCode() );
  }

  @Test
  public void hashCode_differsForDifferingObjects() {
    assertFalse( new JsonLiteral( "foo" ).hashCode() == new JsonLiteral( "bar" ).hashCode() );
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    assertEquals( JsonValue.NULL, serializeAndDeserialize( JsonValue.NULL ) );
    assertEquals( JsonValue.TRUE, serializeAndDeserialize( JsonValue.TRUE ) );
    assertEquals( JsonValue.FALSE, serializeAndDeserialize( JsonValue.FALSE ) );
  }

}
