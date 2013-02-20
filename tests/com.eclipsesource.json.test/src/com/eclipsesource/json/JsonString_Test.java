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

import com.eclipsesource.json.JsonString;
import com.eclipsesource.json.JsonWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

}
