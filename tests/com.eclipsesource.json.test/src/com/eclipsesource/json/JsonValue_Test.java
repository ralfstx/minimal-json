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

import org.junit.Test;

import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.JsonWriter;

import static org.junit.Assert.*;


public class JsonValue_Test {

  @Test
  public void valueOf_int() {
    assertEquals( "0", JsonValue.valueOf( 0 ).toString() );
    assertEquals( "23", JsonValue.valueOf( 23 ).toString() );
    assertEquals( "-1", JsonValue.valueOf( -1 ).toString() );
    assertEquals( "2147483647", JsonValue.valueOf( Integer.MAX_VALUE ).toString() );
    assertEquals( "-2147483648", JsonValue.valueOf( Integer.MIN_VALUE ).toString() );
  }

  @Test
  public void valueOf_long() {
    assertEquals( "0", JsonValue.valueOf( 0l ).toString() );
    assertEquals( "9223372036854775807", JsonValue.valueOf( Long.MAX_VALUE ).toString() );
    assertEquals( "-9223372036854775808", JsonValue.valueOf( Long.MIN_VALUE ).toString() );
  }

  @Test
  public void valueOf_float() {
    assertEquals( "0.0", JsonValue.valueOf( 0f ).toString() );
    assertEquals( "-1.0", JsonValue.valueOf( -1f ).toString() );
    assertEquals( "10.0", JsonValue.valueOf( 10f ).toString() );
    assertEquals( "1.23E-6", JsonValue.valueOf( 0.00000123f ).toString() );
    assertEquals( "-1.23E7", JsonValue.valueOf( -12300000f ).toString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOf_float_failsWithInfinity() {
    JsonValue.valueOf( Float.POSITIVE_INFINITY );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOf_float_failsWithNaN() {
    JsonValue.valueOf( Float.NaN );
  }

  @Test
  public void valueOf_double() {
    assertEquals( "10.0", JsonValue.valueOf( 10d ).toString() );
    assertEquals( "1.23E-6", JsonValue.valueOf( 0.00000123d ).toString() );
    assertEquals( "1.7976931348623157E308", JsonValue.valueOf( 1.7976931348623157E308d ).toString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOf_double_failsWithInfinity() {
    JsonValue.valueOf( Double.POSITIVE_INFINITY );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOf_double_failsWithNaN() {
    JsonValue.valueOf( Double.NaN );
  }

  @Test
  public void valueOf_boolean() {
    assertSame( JsonValue.TRUE, JsonValue.valueOf( true ) );
    assertSame( JsonValue.FALSE, JsonValue.valueOf( false ) );
  }

  @Test
  public void valueOf_string() {
    assertEquals( "", JsonValue.valueOf( "" ).asString() );
    assertEquals( "Hello", JsonValue.valueOf( "Hello" ).asString() );
    assertEquals( "\"Hello\"", JsonValue.valueOf( "\"Hello\"" ).asString() );
  }

  @Test
  public void valueOf_string_withNull() {
    assertSame( JsonValue.NULL, JsonValue.valueOf( null ) );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void asObject() {
    JsonValue.NULL.asObject();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void asArray() {
    JsonValue.NULL.asArray();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void asString() {
    JsonValue.NULL.asString();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void intValue() {
    JsonValue.NULL.intValue();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void longValue() {
    JsonValue.NULL.longValue();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void floatValue() {
    JsonValue.NULL.floatValue();
  }

  @Test( expected = UnsupportedOperationException.class )
  public void doubleValue() {
    JsonValue.NULL.doubleValue();
  }

  @Test
  public void isXxx_returnsFalseForAllTypes() {
    JsonValue jsonValue = new JsonValue() {
      @Override
      public void write( JsonWriter jsonWriter ) throws IOException {
      }
    };

    assertFalse( jsonValue.isArray() );
    assertFalse( jsonValue.isObject() );
    assertFalse( jsonValue.isString() );
    assertFalse( jsonValue.isNumber() );
    assertFalse( jsonValue.isBoolean() );
    assertFalse( jsonValue.isNull() );
    assertFalse( jsonValue.isTrue() );
  }

}
