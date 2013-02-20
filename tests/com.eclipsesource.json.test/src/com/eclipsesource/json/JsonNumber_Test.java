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

import com.eclipsesource.json.JsonNumber;
import com.eclipsesource.json.JsonWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
  public void intValue() {
    assertEquals( 23, new JsonNumber( "23" ).intValue() );
  }

  @Test( expected = NumberFormatException.class )
  public void intValue_failsWithExceedingValues() {
    new JsonNumber( "10000000000" ).intValue();
  }

  @Test( expected = NumberFormatException.class )
  public void intValue_failsWithExponent() {
    new JsonNumber( "1e5" ).intValue();
  }

  @Test( expected = NumberFormatException.class )
  public void intValue_failsWithFractional() {
    new JsonNumber( "23.5" ).intValue();
  }

  @Test
  public void longValue() {
    assertEquals( 23, new JsonNumber( "23" ).longValue() );
  }

  @Test( expected = NumberFormatException.class )
  public void longValue_failsWithExceedingValues() {
    new JsonNumber( "10000000000000000000" ).longValue();
  }

  @Test( expected = NumberFormatException.class )
  public void longValue_failsWithExponent() {
    new JsonNumber( "1e5" ).longValue();
  }

  @Test( expected = NumberFormatException.class )
  public void longValue_failsWithFractional() {
    new JsonNumber( "23.5" ).longValue();
  }

  @Test
  public void floatValue() {
    assertEquals( 23.05f, new JsonNumber( "23.05" ).floatValue(), 0 );
  }

  @Test
  public void floatValue_returnsInfinityForExceedingValues() {
    assertEquals( Float.POSITIVE_INFINITY, new JsonNumber( "1e50" ).floatValue(), 0 );
    assertEquals( Float.NEGATIVE_INFINITY, new JsonNumber( "-1e50" ).floatValue(), 0 );
  }

  @Test
  public void doubleValue() {
    double result = new JsonNumber( "23.05" ).doubleValue();

    assertEquals( 23.05, result, 0 );
  }

  @Test
  public void doubleValue_returnsInfinityForExceedingValues() {
    assertEquals( Double.POSITIVE_INFINITY, new JsonNumber( "1e500" ).doubleValue(), 0 );
    assertEquals( Double.NEGATIVE_INFINITY, new JsonNumber( "-1e500" ).doubleValue(), 0 );
  }

}
