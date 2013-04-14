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
import static org.junit.Assert.assertSame;


public class JsonParser_Test {

  @Test
  public void parse_rejectsEmptyString() {
    assertParseException( "Unexpected end of input at 1:0", "" );
  }

  @Test
  public void parse_acceptsArrays() {
    assertEquals( new JsonArray(), parse( "[]" ) );
  }

  @Test
  public void parse_acceptsObjects() {
    assertEquals( new JsonObject(), parse( "{}" ) );
  }

  @Test
  public void parse_acceptsStrings() {
    assertEquals( new JsonString( "" ), parse( "\"\"" ) );
  }
  @Test
  public void parse_acceptsLiterals() {
    assertSame( JsonValue.NULL, parse( "null" ) );
  }

  @Test
  public void parse_stripsPadding() {
    assertEquals( new JsonArray(), parse( " [ ] " ) );
  }

  @Test
  public void parse_ignoresAllWhiteSpace() {
    assertEquals( new JsonArray(), parse( "\t\r\n [\t\r\n ]\t\r\n " ) );
  }

  @Test
  public void parse_failsWithUnterminatedString() {
    assertParseException( "Unexpected end of input at 1:5", "[\"foo" );
  }

  @Test
  public void arrays_empty() {
    assertEquals( "[]", parse( "[]" ).toString() );
  }

  @Test
  public void arrays_singleValue() {
    assertEquals( "[23]", parse( "[23]" ).toString() );
  }

  @Test
  public void arrays_multipleValues() {
    assertEquals( "[23,42]", parse( "[23,42]" ).toString() );
  }

  @Test
  public void arrays_withWhitespaces() {
    assertEquals( "[23,42]", parse( "[ 23 , 42 ]" ).toString() );
  }

  @Test
  public void arrays_nested() {
    assertEquals( "[[23],42]", parse( "[[23],42]" ).toString() );
  }

  @Test
  public void arrays_illegalSyntax() {
    assertParseException( "Expected value at 1:1", "[,]" );
    assertParseException( "Expected ',' or ']' at 1:4", "[23 42]" );
    assertParseException( "Expected value at 1:4", "[23,]" );
  }

  @Test
  public void arrays_incomplete() {
    assertParseException( "Unexpected end of input at 1:1", "[" );
    assertParseException( "Unexpected end of input at 1:2", "[ " );
    assertParseException( "Unexpected end of input at 1:3", "[23" );
    assertParseException( "Unexpected end of input at 1:4", "[23 " );
    assertParseException( "Unexpected end of input at 1:4", "[23," );
    assertParseException( "Unexpected end of input at 1:5", "[23, " );
  }

  @Test
  public void objects_empty() {
    assertEquals( "{}", parse( "{}" ).toString() );
  }

  @Test
  public void objects_singleValue() {
    assertEquals( "{\"foo\":23}", parse( "{\"foo\":23}" ).toString() );
  }

  @Test
  public void objects_multipleValues() {
    assertEquals( "{\"foo\":23,\"bar\":42}", parse( "{\"foo\":23,\"bar\":42}" ).toString() );
  }

  @Test
  public void objects_whitespace() {
    assertEquals( "{\"foo\":23,\"bar\":42}", parse( "{ \"foo\" : 23, \"bar\" : 42 }" ).toString() );
  }

  @Test
  public void objects_nested() {
    assertEquals( "{\"foo\":{\"bar\":42}}", parse( "{\"foo\":{\"bar\":42}}" ).toString() );
  }

  @Test
  public void objects_illegalSyntax() {
    assertParseException( "Expected name at 1:1", "{,}" );
    assertParseException( "Expected name at 1:1", "{:}" );
    assertParseException( "Expected name at 1:1", "{23}" );
    assertParseException( "Expected ':' at 1:4", "{\"a\"}" );
    assertParseException( "Expected ':' at 1:5", "{\"a\" \"b\"}" );
    assertParseException( "Expected value at 1:5", "{\"a\":}" );
    assertParseException( "Expected name at 1:8", "{\"a\":23,}" );
    assertParseException( "Expected name at 1:8", "{\"a\":23,42" );
  }

  @Test
  public void objects_incomplete() {
    assertParseException( "Unexpected end of input at 1:1", "{" );
    assertParseException( "Unexpected end of input at 1:2", "{ " );
    assertParseException( "Unexpected end of input at 1:2", "{\"" );
    assertParseException( "Unexpected end of input at 1:4", "{\"a\"" );
    assertParseException( "Unexpected end of input at 1:5", "{\"a\" " );
    assertParseException( "Unexpected end of input at 1:5", "{\"a\":" );
    assertParseException( "Unexpected end of input at 1:6", "{\"a\": " );
    assertParseException( "Unexpected end of input at 1:7", "{\"a\":23" );
    assertParseException( "Unexpected end of input at 1:8", "{\"a\":23 " );
    assertParseException( "Unexpected end of input at 1:8", "{\"a\":23," );
    assertParseException( "Unexpected end of input at 1:9", "{\"a\":23, " );
  }

  @Test
  public void strings_emptyString_isAccepted() {
    assertEquals( "", parse( "\"\"" ).asString() );
  }

  @Test
  public void strings_asciiCharacters_areAccepted() {
    assertEquals( " ", parse( "\" \"" ).asString() );
    assertEquals( "a", parse( "\"a\"" ).asString() );
    assertEquals( "foo", parse( "\"foo\"" ).asString() );
    assertEquals( "A2-D2", parse( "\"A2-D2\"" ).asString() );
    assertEquals( "\u007f", parse( "\"\u007f\"" ).asString() );
  }

  @Test
  public void strings_nonAsciiCharacters_areAccepted() {
    assertEquals( "Русский", parse( "\"Русский\"" ).asString() );
    assertEquals( "العربية", parse( "\"العربية\"" ).asString() );
    assertEquals( "日本語", parse( "\"日本語\"" ).asString() );
  }

  @Test
  public void strings_controlCharacters_areRejected() {
    // JSON string must not contain characters < 0x20
    assertParseException( "Expected valid string character at 1:3", "\"--\n--\"" );
    assertParseException( "Expected valid string character at 1:3", "\"--\r\n--\"" );
    assertParseException( "Expected valid string character at 1:3", "\"--\t--\"" );
    assertParseException( "Expected valid string character at 1:3", "\"--\u0000--\"" );
    assertParseException( "Expected valid string character at 1:3", "\"--\u001f--\"" );
  }

  @Test
  public void strings_validEscapes_areAccepted() {
    // valid escapes are \" \\ \/ \b \f \n \r \t and unicode escapes
    assertEquals( " \" ", parse( "\" \\\" \"" ).asString() );
    assertEquals( " \\ ", parse( "\" \\\\ \"" ).asString() );
    assertEquals( " / ", parse( "\" \\/ \"" ).asString() );
    assertEquals( " \u0008 ", parse( "\" \\b \"" ).asString() );
    assertEquals( " \u000c ", parse( "\" \\f \"" ).asString() );
    assertEquals( " \r ", parse( "\" \\r \"" ).asString() );
    assertEquals( " \n ", parse( "\" \\n \"" ).asString() );
    assertEquals( " \t ", parse( "\" \\t \"" ).asString() );
  }

  @Test
  public void strings_illegalEscapes_areRejected() {
    assertParseException( "Expected valid escape sequence at 1:2", "\"\\a\"" );
    assertParseException( "Expected valid escape sequence at 1:2", "\"\\x\"" );
    assertParseException( "Expected valid escape sequence at 1:2", "\"\\000\"" );
  }

  @Test
  public void strings_validUnicodeEscapes_areAccepted() {
    assertEquals( "\u0021", parse( "\"\\u0021\"" ).asString() );
    assertEquals( "\u4711", parse( "\"\\u4711\"" ).asString() );
    assertEquals( "\uffff", parse( "\"\\uffff\"" ).asString() );
    assertEquals( "\uabcdx", parse( "\"\\uabcdx\"" ).asString() );
  }

  @Test
  public void strings_illegalUnicodeEscapes_areRejected() {
    assertParseException( "Expected hexadecimal digit at 1:3", "\"\\u \"" );
    assertParseException( "Expected hexadecimal digit at 1:3", "\"\\ux\"" );
    assertParseException( "Expected hexadecimal digit at 1:5", "\"\\u20 \"" );
    assertParseException( "Expected hexadecimal digit at 1:6", "\"\\u000x\"" );
  }

  @Test
  public void strings_incompleteStrings_areRejected() {
    assertParseException( "Unexpected end of input at 1:1", "\"" );
    assertParseException( "Unexpected end of input at 1:4", "\"foo" );
    assertParseException( "Unexpected end of input at 1:5", "\"foo\\" );
    assertParseException( "Unexpected end of input at 1:6", "\"foo\\n" );
    assertParseException( "Unexpected end of input at 1:6", "\"foo\\u" );
    assertParseException( "Unexpected end of input at 1:7", "\"foo\\u0" );
    assertParseException( "Unexpected end of input at 1:9", "\"foo\\u000" );
    assertParseException( "Unexpected end of input at 1:10", "\"foo\\u0000" );
  }

  @Test
  public void numbers_integer() {
    assertEquals( new JsonNumber( "0" ), parse( "0" ) );
    assertEquals( new JsonNumber( "-0" ), parse( "-0" ) );
    assertEquals( new JsonNumber( "1" ), parse( "1" ) );
    assertEquals( new JsonNumber( "-1" ), parse( "-1" ) );
    assertEquals( new JsonNumber( "23" ), parse( "23" ) );
    assertEquals( new JsonNumber( "-23" ), parse( "-23" ) );
    assertEquals( new JsonNumber( "1234567890" ), parse( "1234567890" ) );
    assertEquals( new JsonNumber( "123456789012345678901234567890" ),
                  parse( "123456789012345678901234567890" ) );
  }

  @Test
  public void numbers_minusZero() {
    // allowed by JSON, allowed by Java
    JsonValue value = parse( "-0" );

    assertEquals( 0, value.asInt() );
    assertEquals( 0l, value.asLong() );
    assertEquals( 0f, value.asFloat(), 0 );
    assertEquals( 0d, value.asDouble(), 0 );
  }

  @Test
  public void numbers_decimal() {
    assertEquals( new JsonNumber( "0.23" ), parse( "0.23" ) );
    assertEquals( new JsonNumber( "-0.23" ), parse( "-0.23" ) );
    assertEquals( new JsonNumber( "1234567890.12345678901234567890" ),
                  parse( "1234567890.12345678901234567890" ) );
  }

  @Test
  public void numbers_withExponent() {
    assertEquals( new JsonNumber( "0.1e9" ), parse( "0.1e9" ) );
    assertEquals( new JsonNumber( "0.1e9" ), parse( "0.1e9" ) );
    assertEquals( new JsonNumber( "0.1E9" ), parse( "0.1E9" ) );
    assertEquals( new JsonNumber( "-0.23e9" ), parse( "-0.23e9" ) );
    assertEquals( new JsonNumber( "0.23e9" ), parse( "0.23e9" ) );
    assertEquals( new JsonNumber( "0.23e+9" ), parse( "0.23e+9" ) );
    assertEquals( new JsonNumber( "0.23e-9" ), parse( "0.23e-9" ) );
  }

  @Test
  public void numbers_withInvalidFormat() {
    assertParseException( "Expected value at 1:0", "+1" );
    assertParseException( "Expected value at 1:0", ".1" );
    assertParseException( "Unexpected character at 1:1", "02" );
    assertParseException( "Unexpected character at 1:2", "-02" );
    assertParseException( "Expected digit at 1:1", "-x" );
    assertParseException( "Expected digit at 1:2", "1.x" );
    assertParseException( "Expected digit at 1:2", "1ex" );
    assertParseException( "Unexpected character at 1:3", "1e1x" );
  }

  @Test
  public void numbers_incomplete() {
    assertParseException( "Unexpected end of input at 1:1", "-" );
    assertParseException( "Unexpected end of input at 1:2", "1." );
    assertParseException( "Unexpected end of input at 1:4", "1.0e" );
    assertParseException( "Unexpected end of input at 1:5", "1.0e-" );
  }

  @Test
  public void null_complete() {
    assertEquals( JsonValue.NULL, parse( "null" ) );
  }

  @Test
  public void null_incomplete() {
    assertParseException( "Unexpected end of input at 1:1", "n" );
    assertParseException( "Unexpected end of input at 1:2", "nu" );
    assertParseException( "Unexpected end of input at 1:3", "nul" );
  }

  @Test
  public void null_withIllegalCharacter() {
    assertParseException( "Expected 'u' at 1:1", "nx" );
    assertParseException( "Expected 'l' at 1:2", "nux" );
    assertParseException( "Expected 'l' at 1:3", "nulx" );
    assertParseException( "Unexpected character at 1:4", "nullx" );
  }

  @Test
  public void true_complete() {
    assertSame( JsonValue.TRUE, parse( "true" ) );
  }

  @Test
  public void true_incomplete() {
    assertParseException( "Unexpected end of input at 1:1", "t" );
    assertParseException( "Unexpected end of input at 1:2", "tr" );
    assertParseException( "Unexpected end of input at 1:3", "tru" );
  }

  @Test
  public void true_withIllegalCharacter() {
    assertParseException( "Expected 'r' at 1:1", "tx" );
    assertParseException( "Expected 'u' at 1:2", "trx" );
    assertParseException( "Expected 'e' at 1:3", "trux" );
    assertParseException( "Unexpected character at 1:4", "truex" );
  }

  @Test
  public void false_complete() {
    assertSame( JsonValue.FALSE, parse( "false" ) );
  }

  @Test
  public void false_incomplete() {
    assertParseException( "Unexpected end of input at 1:1", "f" );
    assertParseException( "Unexpected end of input at 1:2", "fa" );
    assertParseException( "Unexpected end of input at 1:3", "fal" );
    assertParseException( "Unexpected end of input at 1:4", "fals" );
  }

  @Test
  public void false_withIllegalCharacter() {
    assertParseException( "Expected 'a' at 1:1", "fx" );
    assertParseException( "Expected 'l' at 1:2", "fax" );
    assertParseException( "Expected 's' at 1:3", "falx" );
    assertParseException( "Expected 'e' at 1:4", "falsx" );
    assertParseException( "Unexpected character at 1:5", "falsex" );
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

}
