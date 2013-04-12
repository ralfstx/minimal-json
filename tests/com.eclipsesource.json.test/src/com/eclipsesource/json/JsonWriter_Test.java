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

import static org.junit.Assert.assertEquals;


public class JsonWriter_Test {

  private StringWriter output;
  private JsonWriter writer;

  @Before
  public void setUp() {
    output = new StringWriter();
    writer = new JsonWriter( output );
  }

  @Test
  public void write_passesThrough() throws IOException {
    writer.write( "foo" );

    assertEquals( "foo", output.toString() );
  }

  @Test
  public void writeString_empty() throws IOException {
    writer.writeString( "" );

    assertEquals( "\"\"", output.toString() );
  }

  @Test
  public void writeSting_escapesBackslashes() throws IOException {
    writer.writeString( "foo\\bar" );

    assertEquals( "\"foo\\\\bar\"", output.toString() );
  }

  @Test
  public void escapesQuotes() throws IOException {
    writer.writeString( "a\"b" );

    assertEquals( "\"a\\\"b\"", output.toString() );
  }

  @Test
  public void escapesEscapedQuotes() throws IOException {
    writer.writeString( "foo\\\"bar" );

    assertEquals( "\"foo\\\\\\\"bar\"", output.toString() );
  }

  @Test
  public void escapesNewLine() throws IOException {
    writer.writeString( "foo\nbar" );

    assertEquals( "\"foo\\nbar\"", output.toString() );
  }

  @Test
  public void escapesWindowsNewLine() throws IOException {
    writer.writeString( "foo\r\nbar" );

    assertEquals( "\"foo\\r\\nbar\"", output.toString() );
  }

  @Test
  public void escapesTabs() throws IOException {
    writer.writeString( "foo\tbar" );

    assertEquals( "\"foo\\tbar\"", output.toString() );
  }

  @Test
  public void escapesSpecialCharacters() throws IOException {
    writer.writeString( "foo\u2028bar\u2029" );

    assertEquals( "\"foo\\u2028bar\\u2029\"", output.toString() );
  }

  @Test
  public void escapesZeroCharacter() throws IOException {
    writer.writeString( string( 'f', 'o', 'o', (char)0, 'b', 'a', 'r' ) );

    assertEquals( "\"foo\\u0000bar\"", output.toString() );
  }

  @Test
  public void escapesEscapeCharacter() throws IOException {
    writer.writeString( string( 'f', 'o', 'o', (char)27, 'b', 'a', 'r' ) );

    assertEquals( "\"foo\\u001bbar\"", output.toString() );
  }

  @Test
  public void escapesControlCharacter8() throws IOException {
    writer.writeString( string( 'f', 'o', 'o', (char)8, 'b', 'a', 'r' ) );

    assertEquals( "\"foo\\u0008bar\"", output.toString() );
  }

  @Test
  public void escapesControlCharacter15() throws IOException {
    writer.writeString( string( 'f', 'o', 'o', (char)15, 'b', 'a', 'r' ) );

    assertEquals( "\"foo\\u000fbar\"", output.toString() );
  }

  @Test
  public void writeObjectParts() throws IOException {
    writer.writeBeginObject();
    writer.writeNameValueSeparator();
    writer.writeObjectValueSeparator();
    writer.writeEndObject();

    assertEquals( "{:,}", output.toString() );
  }

  @Test
  public void writeArrayParts() throws IOException {
    writer.writeBeginArray();
    writer.writeArrayValueSeparator();
    writer.writeEndArray();

    assertEquals( "[,]", output.toString() );
  }

  private static String string( char ... chars ) {
    String string = String.valueOf( chars );
    return string;
  }

}
