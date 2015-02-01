/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
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

import static com.eclipsesource.json.TestUtil.assertException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;


public class JsonValue_Test {

  @Test
  public void testConstantsAreLiterals() {
    assertSame( JsonLiteral.NULL, JsonValue.NULL );
    assertSame( JsonLiteral.TRUE, JsonValue.TRUE );
    assertSame( JsonLiteral.FALSE, JsonValue.FALSE );
  }

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
    assertEquals( "23.5", JsonValue.valueOf( 23.5f ).toString() );
    assertEquals( "-3.1416", JsonValue.valueOf( -3.1416f ).toString() );
    assertEquals( "1.23E-6", JsonValue.valueOf( 0.00000123f ).toString() );
    assertEquals( "-1.23E7", JsonValue.valueOf( -12300000f ).toString() );
  }

  @Test
  public void valueOf_float_cutsOffPointZero() {
    assertEquals( "0", JsonValue.valueOf( 0f ).toString() );
    assertEquals( "-1", JsonValue.valueOf( -1f ).toString() );
    assertEquals( "10", JsonValue.valueOf( 10f ).toString() );
  }

  @Test
  public void valueOf_float_failsWithInfinity() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException( IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        JsonValue.valueOf( Float.POSITIVE_INFINITY );
      }
    } );
  }

  @Test
  public void valueOf_float_failsWithNaN() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException( IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        JsonValue.valueOf( Float.NaN );
      }
    } );
  }

  @Test
  public void valueOf_double() {
    assertEquals( "23.5", JsonValue.valueOf( 23.5d ).toString() );
    assertEquals( "3.1416", JsonValue.valueOf( 3.1416d ).toString() );
    assertEquals( "1.23E-6", JsonValue.valueOf( 0.00000123d ).toString() );
    assertEquals( "1.7976931348623157E308", JsonValue.valueOf( 1.7976931348623157E308d ).toString() );
  }

  @Test
  public void valueOf_double_cutsOffPointZero() {
    assertEquals( "0", JsonValue.valueOf( 0d ).toString() );
    assertEquals( "-1", JsonValue.valueOf( -1d ).toString() );
    assertEquals( "10", JsonValue.valueOf( 10d ).toString() );
  }

  @Test
  public void valueOf_double_failsWithInfinity() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException( IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        JsonValue.valueOf( Double.POSITIVE_INFINITY );
      }
    } );
  }

  @Test
  public void valueOf_double_failsWithNaN() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException( IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        JsonValue.valueOf( Double.NaN );
      }
    } );
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
  public void valueOf_string_toleratesNull() {
    assertSame( JsonValue.NULL, JsonValue.valueOf( null ) );
  }

  @Test
  public void readFrom_string() {
    assertEquals( new JsonArray(), JsonValue.readFrom( "[]" ) );
    assertEquals( new JsonObject(), JsonValue.readFrom( "{}" ) );
    assertEquals( JsonValue.valueOf( "foo" ), JsonValue.readFrom( "\"foo\"" ) );
    assertEquals( JsonValue.valueOf( 23 ), JsonValue.readFrom( "23" ) );
    assertSame( JsonValue.NULL, JsonValue.readFrom( "null" ) );
  }

  @Test
  public void readFrom_reader() throws IOException {
    assertEquals( new JsonArray(), JsonValue.readFrom( new StringReader( "[]" ) ) );
    assertEquals( new JsonObject(), JsonValue.readFrom( new StringReader( "{}" ) ) );
    assertEquals( JsonValue.valueOf( "foo" ), JsonValue.readFrom( new StringReader( "\"foo\"" ) ) );
    assertEquals( JsonValue.valueOf( 23 ), JsonValue.readFrom( new StringReader( "23" ) ) );
    assertSame( JsonValue.NULL, JsonValue.readFrom( new StringReader( "null" ) ) );
  }

  @Test
  public void readFrom_reader_doesNotCloseReader() throws IOException {
    Reader reader = spy( new StringReader( "{}" ) );

    JsonValue.readFrom( reader );

    verify( reader, never() ).close();
  }

  @Test
  public void writeTo() throws IOException {
    JsonValue value = new JsonObject();
    Writer writer = new StringWriter();

    value.writeTo( writer );

    assertEquals( "{}", writer.toString() );
  }

  @Test
  public void writeTo_doesNotCloseWriter() throws IOException {
    JsonValue value = new JsonObject();
    Writer writer = spy( new StringWriter() );

    value.writeTo( writer );

    verify( writer, never() ).close();
  }

  @Test
  public void asObject_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not an object: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asObject();
      }
    } );
  }

  @Test
  public void asArray_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not an array: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asArray();
      }
    } );
  }

  @Test
  public void asString_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not a string: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asString();
      }
    } );
  }

  @Test
  public void asInt_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not a number: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asInt();
      }
    } );
  }

  @Test
  public void asLong_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not a number: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asLong();
      }
    } );
  }

  @Test
  public void asFloat_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not a number: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asFloat();
      }
    } );
  }

  @Test
  public void asDouble_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not a number: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asDouble();
      }
    } );
  }

  @Test
  public void asBoolean_failsOnIncompatibleType() {
    assertException( UnsupportedOperationException.class, "Not a boolean: null", new Runnable() {
      public void run() {
        JsonValue.NULL.asBoolean();
      }
    } );
  }

  @Test
  public void isXxx_returnsFalseForIncompatibleType() {
    JsonValue jsonValue = new JsonValue() {
      @Override
      void write( JsonWriter writer ) throws IOException {
      }
    };

    assertFalse( jsonValue.isArray() );
    assertFalse( jsonValue.isObject() );
    assertFalse( jsonValue.isString() );
    assertFalse( jsonValue.isNumber() );
    assertFalse( jsonValue.isBoolean() );
    assertFalse( jsonValue.isNull() );
    assertFalse( jsonValue.isTrue() );
    assertFalse( jsonValue.isFalse() );
  }

}
