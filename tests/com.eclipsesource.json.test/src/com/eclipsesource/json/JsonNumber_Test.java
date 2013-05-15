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


public class JsonNumber_Test {

  private StringWriter output;
  private JsonWriter writer;

  @Before
  public void setUp() {
    output = new StringWriter();
    writer = new JsonWriter( output );
  }

  @Test
  public void write() throws IOException {
    new JsonNumber( "23" ).write( writer );

    assertEquals( "23", output.toString() );
  }

  @Test
  public void toString_returnsInputString() {
    assertEquals( "foo", new JsonNumber( "foo" ).toString() );
  }

  @Test
  public void isNumber() {
    assertTrue( new JsonNumber( "23" ).isNumber() );
  }

  @Test
  public void asInt() {
    assertEquals( 23, new JsonNumber( "23" ).asInt() );
  }

  @Test( expected = NumberFormatException.class )
  public void asInt_failsWithExceedingValues() {
    new JsonNumber( "10000000000" ).asInt();
  }

  @Test( expected = NumberFormatException.class )
  public void asInt_failsWithExponent() {
    new JsonNumber( "1e5" ).asInt();
  }

  @Test( expected = NumberFormatException.class )
  public void asInt_failsWithFractional() {
    new JsonNumber( "23.5" ).asInt();
  }

  @Test
  public void asLong() {
    assertEquals( 23, new JsonNumber( "23" ).asLong() );
  }

  @Test( expected = NumberFormatException.class )
  public void asLong_failsWithExceedingValues() {
    new JsonNumber( "10000000000000000000" ).asLong();
  }

  @Test( expected = NumberFormatException.class )
  public void asLong_failsWithExponent() {
    new JsonNumber( "1e5" ).asLong();
  }

  @Test( expected = NumberFormatException.class )
  public void asLong_failsWithFractional() {
    new JsonNumber( "23.5" ).asLong();
  }

  @Test
  public void asFloat() {
    assertEquals( 23.05f, new JsonNumber( "23.05" ).asFloat(), 0 );
  }

  @Test
  public void asFloat_returnsInfinityForExceedingValues() {
    assertEquals( Float.POSITIVE_INFINITY, new JsonNumber( "1e50" ).asFloat(), 0 );
    assertEquals( Float.NEGATIVE_INFINITY, new JsonNumber( "-1e50" ).asFloat(), 0 );
  }

  @Test
  public void asDouble() {
    double result = new JsonNumber( "23.05" ).asDouble();

    assertEquals( 23.05, result, 0 );
  }

  @Test
  public void asDouble_returnsInfinityForExceedingValues() {
    assertEquals( Double.POSITIVE_INFINITY, new JsonNumber( "1e500" ).asDouble(), 0 );
    assertEquals( Double.NEGATIVE_INFINITY, new JsonNumber( "-1e500" ).asDouble(), 0 );
  }

  @Test
  public void equals_trueForSameInstance() {
    JsonNumber number = new JsonNumber( "23" );

    assertTrue( number.equals( number ) );
  }

  @Test
  public void equals_trueForEqualNumberStrings() {
    assertTrue( new JsonNumber( "23" ).equals( new JsonNumber( "23" ) ) );
  }

  @Test
  public void equals_falseForDifferentNumberStrings() {
    assertFalse( new JsonNumber( "23" ).equals( new JsonNumber( "42" ) ) );
    assertFalse( new JsonNumber( "1e+5" ).equals( new JsonNumber( "1e5" ) ) );
  }

  @Test
  public void equals_falseForNull() {
    assertFalse( new JsonNumber( "23" ).equals( null ) );
  }

  @Test
  public void equals_falseForSubclass() {
    assertFalse( new JsonNumber( "23" ).equals( new JsonNumber( "23" ) {} ) );
  }

  @Test
  public void hashCode_equalsForEqualStrings() {
    assertTrue( new JsonNumber( "23" ).hashCode() == new JsonNumber( "23" ).hashCode() );
  }

  @Test
  public void hashCode_differsForDifferentStrings() {
    assertFalse( new JsonNumber( "23" ).hashCode() == new JsonNumber( "42" ).hashCode() );
  }

  @Test
  public void canBeSerializedAndDeserialized() throws Exception {
    JsonNumber number = new JsonNumber( "3.14" );

    assertEquals( number, serializeAndDeserialize( number ) );
  }

}
