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
import java.io.StringReader;

import org.junit.Test;

import com.eclipsesource.json.JsonParser;
import com.eclipsesource.json.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class JsonParser_Test {

  @Test
  public void parse_rejectsEmptyString() {
    assertParseException( "Unexpected end of input at 1:0", "" );
  }

  @Test
  public void parse_rejectsValuesExceptObjectsAndArrays() {
    assertParseException( "Expected '{' or '[' at 1:0", "\"foo\"" );
    assertParseException( "Expected '{' or '[' at 1:0", "23" );
    assertParseException( "Expected '{' or '[' at 1:0", "true" );
  }

  @Test
  public void parse_acceptsArrays() {
    assertEquals( "[]", parse( "[]" ) );
  }

  @Test
  public void parse_acceptsObjects() {
    assertEquals( "{}", parse( "{}" ) );
  }

  @Test
  public void parse_stripsPadding() {
    assertEquals( "[]", parse( " [ ] " ) );
  }

  @Test
  public void parse_ignoresAllWhiteSpace() {
    assertEquals( "[]", parse( "\t\r\n [\t\r\n ]\t\r\n " ) );
  }

  @Test
  public void parse_failsWithUnterminatedString() {
    assertParseException( "Unexpected end of input at 1:5", "[\"foo" );
  }

  @Test
  public void parse_failsWithUnterminatedArray() {
    assertParseException( "Unexpected end of input at 1:3", "[23" );
  }

  @Test
  public void arrays_empty() {
    assertEquals( "[]", readValue( "[]" ) );
  }

  @Test
  public void arrays_singleValue() {
    assertEquals( "[23]", readValue( "[23]" ) );
  }

  @Test
  public void arrays_multipleValues() {
    assertEquals( "[23,42]", readValue( "[23,42]" ) );
  }

  @Test
  public void arrays_withWhitespaces() {
    assertEquals( "[23,42]", readValue( "[ 23 , 42 ]" ) );
  }

  @Test
  public void arrays_nested() {
    assertEquals( "[[23],42]", readValue( "[[23],42]" ) );
  }

  @Test
  public void arrays_illegalSyntax() {
    assertParseExceptionInReadValue( "Expected value at 1:1", "[,]" );
    assertParseExceptionInReadValue( "Expected ',' or ']' at 1:4", "[23 42]" );
    assertParseExceptionInReadValue( "Expected value at 1:4", "[23,]" );
  }

  @Test
  public void arrays_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "[" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "[ " );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:3", "[23" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:4", "[23 " );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:4", "[23," );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:5", "[23, " );
  }

  @Test
  public void objects_empty() {
    assertEquals( "{}", readValue( "{}" ) );
  }

  @Test
  public void objects_singleValue() {
    assertEquals( "{\"foo\":23}", readValue( "{\"foo\":23}" ) );
  }

  @Test
  public void objects_multipleValues() {
    assertEquals( "{\"foo\":23,\"bar\":42}", readValue( "{\"foo\":23,\"bar\":42}" ) );
  }

  @Test
  public void objects_whitespace() {
    assertEquals( "{\"foo\":23,\"bar\":42}", readValue( "{ \"foo\" : 23, \"bar\" : 42 }" ) );
  }

  @Test
  public void objects_nested() {
    assertEquals( "{\"foo\":{\"bar\":42}}", readValue( "{\"foo\":{\"bar\":42}}" ) );
  }

  @Test
  public void objects_illegalSyntax() {
    assertParseExceptionInReadValue( "Expected name at 1:1", "{,}" );
    assertParseExceptionInReadValue( "Expected name at 1:1", "{:}" );
    assertParseExceptionInReadValue( "Expected name at 1:1", "{23}" );
    assertParseExceptionInReadValue( "Expected ':' at 1:4", "{\"a\"}" );
    assertParseExceptionInReadValue( "Expected ':' at 1:5", "{\"a\" \"b\"}" );
    assertParseExceptionInReadValue( "Expected value at 1:5", "{\"a\":}" );
    assertParseExceptionInReadValue( "Expected name at 1:8", "{\"a\":23,}" );
    assertParseExceptionInReadValue( "Expected name at 1:8", "{\"a\":23,42" );
  }

  @Test
  public void objects_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "{" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "{ " );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "{\"" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:4", "{\"a\"" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:5", "{\"a\" " );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:5", "{\"a\":" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:6", "{\"a\": " );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:7", "{\"a\":23" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:8", "{\"a\":23 " );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:8", "{\"a\":23," );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:9", "{\"a\":23, " );
  }

  @Test
  public void strings_empty() {
    assertEquals( "\"\"", readValue( "\"\"" ) );
  }

  @Test
  public void strings_singleLetter() {
    assertEquals( "\"a\"", readValue( "\"a\"" ) );
  }

  @Test
  public void strings_escape() {
    assertEquals( "\"\\\\\"", readValue( "\"\\\\\"" ) );
  }

  @Test
  public void strings_escapedQuote() {
    assertEquals( "\"\\\"\"", readValue( "\"\\\"\"" ) );
  }

  @Test
  public void strings_withEscapedTabs() {
    assertEquals( "\"foo\\t\\t\"", readValue( "\"foo\\t\\t\"" ) );
  }

  @Test
  public void strings_withEscapedNewlines() {
    assertEquals( "\"foo\\r\\nbar\\r\\n\"", readValue( "\"foo\\r\\nbar\\r\\n\"" ) );
  }

  @Test
  public void strings_withEscapedBackspaceAndFormFeed() {
    assertEquals( "\"foo\\u0008bar\\u000c\"", readValue( "\"foo\\bbar\\f\"" ) );
  }

  @Test
  public void strings_withUnicodeEscapes() {
    assertEquals( "\"\u4711\"", readValue( "\"\\u4711\"" ) );
  }

  @Test
  public void strings_illegalEscapes() {
    assertParseExceptionInReadValue( "Expected hexadecimal digit at 1:3", "\"\\ux\"" );
    assertParseExceptionInReadValue( "Expected hexadecimal digit at 1:6", "\"\\u000x\"" );
    assertParseExceptionInReadValue( "Expected valid escape sequence at 1:2", "\"\\x\"" );
  }

  @Test
  public void strings_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "\"" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:4", "\"foo" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:5", "\"foo\\" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:6", "\"foo\\n" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:6", "\"foo\\u" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:7", "\"foo\\u0" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:9", "\"foo\\u000" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:10", "\"foo\\u0000" );
  }

  @Test
  public void numbers_integer() {
    assertEquals( "0", readValue( "0" ) );
    assertEquals( "1", readValue( "1" ) );
    assertEquals( "-1", readValue( "-1" ) );
    assertEquals( "23", readValue( "23" ) );
    assertEquals( "-23", readValue( "-23" ) );
    assertEquals( "1234567890", readValue( "1234567890" ) );
    assertEquals( "123456789012345678901234567890", readValue( "123456789012345678901234567890" ) );
  }

  @Test
  public void numbers_decimal() {
    assertEquals( "0.23", readValue( "0.23" ) );
    assertEquals( "-0.23", readValue( "-0.23" ) );
    assertEquals( "1234567890.12345678901234567890", readValue( "1234567890.12345678901234567890" ) );
  }

  @Test
  public void numbers_withExponent() {
    assertEquals( "0.1e9", readValue( "0.1e9" ) );
    assertEquals( "0.1e9", readValue( "0.1e9" ) );
    assertEquals( "-0.23e9", readValue( "-0.23e9" ) );
    assertEquals( "0.23e9", readValue( "0.23e9" ) );
    assertEquals( "0.23e9", readValue( "0.23e+9" ) );
    assertEquals( "0.23e-9", readValue( "0.23e-9" ) );
  }

  @Test
  public void numbers_withInvalidFormat() {
    assertParseExceptionInReadValue( "Expected value at 1:0", "+1" );
    assertParseExceptionInReadValue( "Expected value at 1:0", ".1" );
    assertParseExceptionInReadValue( "Unexpected character at 1:1", "02" );
    assertParseExceptionInReadValue( "Unexpected character at 1:2", "-02" );
    assertParseExceptionInReadValue( "Expected digit at 1:1", "-x" );
    assertParseExceptionInReadValue( "Expected digit at 1:2", "1.x" );
    assertParseExceptionInReadValue( "Expected digit at 1:2", "1ex" );
    assertParseExceptionInReadValue( "Unexpected character at 1:3", "1e1x" );
  }

  @Test
  public void numbers_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "-" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "1." );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:4", "1.0e" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:5", "1.0e-" );
  }

  @Test
  public void null_complete() {
    assertEquals( "null", readValue( "null" ) );
  }

  @Test
  public void null_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "n" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "nu" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:3", "nul" );
  }

  @Test
  public void null_withIllegalCharacter() {
    assertParseExceptionInReadValue( "Expected 'u' at 1:1", "nx" );
    assertParseExceptionInReadValue( "Expected 'l' at 1:2", "nux" );
    assertParseExceptionInReadValue( "Expected 'l' at 1:3", "nulx" );
    assertParseExceptionInReadValue( "Unexpected character at 1:4", "nullx" );
  }

  @Test
  public void true_complete() {
    assertEquals( "true", readValue( "true" ) );
  }

  @Test
  public void true_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "t" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "tr" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:3", "tru" );
  }

  @Test
  public void true_withIllegalCharacter() {
    assertParseExceptionInReadValue( "Expected 'r' at 1:1", "tx" );
    assertParseExceptionInReadValue( "Expected 'u' at 1:2", "trx" );
    assertParseExceptionInReadValue( "Expected 'e' at 1:3", "trux" );
    assertParseExceptionInReadValue( "Unexpected character at 1:4", "truex" );
  }

  @Test
  public void false_complete() {
    assertEquals( "false", readValue( "false" ) );
  }

  @Test
  public void false_incomplete() {
    assertParseExceptionInReadValue( "Unexpected end of input at 1:1", "f" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:2", "fa" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:3", "fal" );
    assertParseExceptionInReadValue( "Unexpected end of input at 1:4", "fals" );
  }

  @Test
  public void false_withIllegalCharacter() {
    assertParseExceptionInReadValue( "Expected 'a' at 1:1", "fx" );
    assertParseExceptionInReadValue( "Expected 'l' at 1:2", "fax" );
    assertParseExceptionInReadValue( "Expected 's' at 1:3", "falx" );
    assertParseExceptionInReadValue( "Expected 'e' at 1:4", "falsx" );
    assertParseExceptionInReadValue( "Unexpected character at 1:5", "falsex" );
  }

  static void assertParseException( String expectedMessage, String json ) {
    try {
      parse( json );
      fail( "ParseException expected" );
    } catch( ParseException exception ) {
      assertEquals( "message", expectedMessage, exception.getMessage() );
    }
  }

  static String parse( String json ) {
    try {
      return new JsonParser( new StringReader( json ) ).parse().toString();
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

  static void assertParseExceptionInReadValue( String expectedMessage, String json ) {
    try {
      readValue( json );
      fail( "ParseException expected" );
    } catch( ParseException exception ) {
      assertEquals( "message", expectedMessage, exception.getMessage() );
    }
  }

  static String readValue( String json ) {
    try {
      return new JsonParser( new StringReader( json ) ).parseValue().toString();
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

}
