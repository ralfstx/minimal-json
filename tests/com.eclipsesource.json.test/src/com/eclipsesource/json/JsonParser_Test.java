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

import static org.junit.Assert.assertEquals;


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
    assertEquals( "[]", parse( "[]" ).toString() );
  }

  @Test
  public void parse_acceptsObjects() {
    assertEquals( "{}", parse( "{}" ).toString() );
  }

  @Test
  public void parse_stripsPadding() {
    assertEquals( "[]", parse( " [ ] " ).toString() );
  }

  @Test
  public void parse_ignoresAllWhiteSpace() {
    assertEquals( "[]", parse( "\t\r\n [\t\r\n ]\t\r\n " ).toString() );
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
    assertEquals( "[]", readValue( "[]" ).toString() );
  }

  @Test
  public void arrays_singleValue() {
    assertEquals( "[23]", readValue( "[23]" ).toString() );
  }

  @Test
  public void arrays_multipleValues() {
    assertEquals( "[23,42]", readValue( "[23,42]" ).toString() );
  }

  @Test
  public void arrays_withWhitespaces() {
    assertEquals( "[23,42]", readValue( "[ 23 , 42 ]" ).toString() );
  }

  @Test
  public void arrays_nested() {
    assertEquals( "[[23],42]", readValue( "[[23],42]" ).toString() );
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
    assertEquals( "{}", readValue( "{}" ).toString() );
  }

  @Test
  public void objects_singleValue() {
    assertEquals( "{\"foo\":23}", readValue( "{\"foo\":23}" ).toString() );
  }

  @Test
  public void objects_multipleValues() {
    assertEquals( "{\"foo\":23,\"bar\":42}", readValue( "{\"foo\":23,\"bar\":42}" ).toString() );
  }

  @Test
  public void objects_whitespace() {
    assertEquals( "{\"foo\":23,\"bar\":42}", readValue( "{ \"foo\" : 23, \"bar\" : 42 }" ).toString() );
  }

  @Test
  public void objects_nested() {
    assertEquals( "{\"foo\":{\"bar\":42}}", readValue( "{\"foo\":{\"bar\":42}}" ).toString() );
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
  public void strings_emptyString_isAccepted() {
    assertEquals( "", readValue( "\"\"" ).asString() );
  }

  @Test
  public void strings_asciiCharacters_areAccepted() {
    assertEquals( " ", readValue( "\" \"" ).asString() );
    assertEquals( "a", readValue( "\"a\"" ).asString() );
    assertEquals( "foo", readValue( "\"foo\"" ).asString() );
    assertEquals( "A2-D2", readValue( "\"A2-D2\"" ).asString() );
    assertEquals( "\u007f", readValue( "\"\u007f\"" ).asString() );
  }

  @Test
  public void strings_nonAsciiCharacters_areAccepted() {
    assertEquals( "Русский", readValue( "\"Русский\"" ).asString() );
    assertEquals( "العربية", readValue( "\"العربية\"" ).asString() );
    assertEquals( "日本語", readValue( "\"日本語\"" ).asString() );
  }

  @Test
  public void strings_controlCharacters_areRejected() {
    // JSON string must not contain characters < 0x20
    assertParseExceptionInReadValue( "Expected valid string character at 1:3", "\"--\n--\"" );
    assertParseExceptionInReadValue( "Expected valid string character at 1:3", "\"--\r\n--\"" );
    assertParseExceptionInReadValue( "Expected valid string character at 1:3", "\"--\t--\"" );
    assertParseExceptionInReadValue( "Expected valid string character at 1:3", "\"--\u0000--\"" );
    assertParseExceptionInReadValue( "Expected valid string character at 1:3", "\"--\u001f--\"" );
  }

  @Test
  public void strings_validEscapes_areAccepted() {
    // valid escapes are \" \\ \/ \b \f \n \r \t and unicode escapes
    assertEquals( " \" ", readValue( "\" \\\" \"" ).asString() );
    assertEquals( " \\ ", readValue( "\" \\\\ \"" ).asString() );
    assertEquals( " / ", readValue( "\" \\/ \"" ).asString() );
    assertEquals( " \u0008 ", readValue( "\" \\b \"" ).asString() );
    assertEquals( " \u000c ", readValue( "\" \\f \"" ).asString() );
    assertEquals( " \r ", readValue( "\" \\r \"" ).asString() );
    assertEquals( " \n ", readValue( "\" \\n \"" ).asString() );
    assertEquals( " \t ", readValue( "\" \\t \"" ).asString() );
  }

  @Test
  public void strings_illegalEscapes_areRejected() {
    assertParseExceptionInReadValue( "Expected valid escape sequence at 1:2", "\"\\a\"" );
    assertParseExceptionInReadValue( "Expected valid escape sequence at 1:2", "\"\\x\"" );
    assertParseExceptionInReadValue( "Expected valid escape sequence at 1:2", "\"\\000\"" );
  }

  @Test
  public void strings_validUnicodeEscapes_areAccepted() {
    assertEquals( "\u0021", readValue( "\"\\u0021\"" ).asString() );
    assertEquals( "\u4711", readValue( "\"\\u4711\"" ).asString() );
    assertEquals( "\uffff", readValue( "\"\\uffff\"" ).asString() );
    assertEquals( "\uabcdx", readValue( "\"\\uabcdx\"" ).asString() );
  }

  @Test
  public void strings_illegalUnicodeEscapes_areRejected() {
    assertParseExceptionInReadValue( "Expected hexadecimal digit at 1:3", "\"\\u \"" );
    assertParseExceptionInReadValue( "Expected hexadecimal digit at 1:3", "\"\\ux\"" );
    assertParseExceptionInReadValue( "Expected hexadecimal digit at 1:5", "\"\\u20 \"" );
    assertParseExceptionInReadValue( "Expected hexadecimal digit at 1:6", "\"\\u000x\"" );
  }

  @Test
  public void strings_incompleteStrings_areRejected() {
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
    assertEquals( "0", readValue( "0" ).toString() );
    assertEquals( "-0", readValue( "-0" ).toString() );
    assertEquals( "1", readValue( "1" ).toString() );
    assertEquals( "-1", readValue( "-1" ).toString() );
    assertEquals( "23", readValue( "23" ).toString() );
    assertEquals( "-23", readValue( "-23" ).toString() );
    assertEquals( "1234567890", readValue( "1234567890" ).toString() );
    assertEquals( "123456789012345678901234567890",
                  readValue( "123456789012345678901234567890" ).toString() );
  }

  @Test
  public void numbers_minusZero() {
    // allowed by JSON, allowed by Java
    JsonValue value = readValue( "-0" );

    assertEquals( 0, value.asInt() );
    assertEquals( 0l, value.asLong() );
    assertEquals( 0f, value.asFloat(), 0 );
    assertEquals( 0d, value.asDouble(), 0 );
  }

  @Test
  public void numbers_decimal() {
    assertEquals( "0.23", readValue( "0.23" ).toString() );
    assertEquals( "-0.23", readValue( "-0.23" ).toString() );
    assertEquals( "1234567890.12345678901234567890", readValue( "1234567890.12345678901234567890" ).toString() );
  }

  @Test
  public void numbers_withExponent() {
    assertEquals( "0.1e9", readValue( "0.1e9" ).toString() );
    assertEquals( "0.1e9", readValue( "0.1e9" ).toString() );
    assertEquals( "0.1E9", readValue( "0.1E9" ).toString() );
    assertEquals( "-0.23e9", readValue( "-0.23e9" ).toString() );
    assertEquals( "0.23e9", readValue( "0.23e9" ).toString() );
    assertEquals( "0.23e+9", readValue( "0.23e+9" ).toString() );
    assertEquals( "0.23e-9", readValue( "0.23e-9" ).toString() );
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
    assertEquals( "null", readValue( "null" ).toString() );
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
    assertEquals( "true", readValue( "true" ).toString() );
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
    assertEquals( "false", readValue( "false" ).toString() );
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

  private static void assertParseException( String expectedMessage, final String json ) {
    TestUtil.assertException( ParseException.class, expectedMessage, new Runnable() {
      public void run() {
        parse( json );
      }
    } );
  }

  private static JsonValue parse( String json ) {
    try {
      return new JsonParser( new StringReader( json ) ).parse();
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

  private static void assertParseExceptionInReadValue( String expectedMessage, final String json ) {
    TestUtil.assertException( ParseException.class, expectedMessage, new Runnable() {
      public void run() {
        readValue( json );
      }
    } );
  }

  private static JsonValue readValue( String json ) {
    try {
      return new JsonParser( new StringReader( json ) ).parseValue();
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

}
