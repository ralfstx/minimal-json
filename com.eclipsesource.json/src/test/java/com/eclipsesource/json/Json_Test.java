/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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

import org.junit.Test;


public class Json_Test {

  @Test
  public void literalConstants() {
    assertTrue(Json.NULL.isNull());
    assertTrue(Json.TRUE.isTrue());
    assertTrue(Json.FALSE.isFalse());
  }

  @Test
  public void value_int() {
    assertEquals("0", Json.value(0).toString());
    assertEquals("23", Json.value(23).toString());
    assertEquals("-1", Json.value(-1).toString());
    assertEquals("2147483647", Json.value(Integer.MAX_VALUE).toString());
    assertEquals("-2147483648", Json.value(Integer.MIN_VALUE).toString());
  }

  @Test
  public void value_long() {
    assertEquals("0", Json.value(0l).toString());
    assertEquals("9223372036854775807", Json.value(Long.MAX_VALUE).toString());
    assertEquals("-9223372036854775808", Json.value(Long.MIN_VALUE).toString());
  }

  @Test
  public void value_float() {
    assertEquals("23.5", Json.value(23.5f).toString());
    assertEquals("-3.1416", Json.value(-3.1416f).toString());
    assertEquals("1.23E-6", Json.value(0.00000123f).toString());
    assertEquals("-1.23E7", Json.value(-12300000f).toString());
  }

  @Test
  public void value_float_cutsOffPointZero() {
    assertEquals("0", Json.value(0f).toString());
    assertEquals("-1", Json.value(-1f).toString());
    assertEquals("10", Json.value(10f).toString());
  }

  @Test
  public void value_float_failsWithInfinity() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException(IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        Json.value(Float.POSITIVE_INFINITY);
      }
    });
  }

  @Test
  public void value_float_failsWithNaN() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException(IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        Json.value(Float.NaN);
      }
    });
  }

  @Test
  public void value_double() {
    assertEquals("23.5", Json.value(23.5d).toString());
    assertEquals("3.1416", Json.value(3.1416d).toString());
    assertEquals("1.23E-6", Json.value(0.00000123d).toString());
    assertEquals("1.7976931348623157E308", Json.value(1.7976931348623157E308d).toString());
  }

  @Test
  public void value_double_cutsOffPointZero() {
    assertEquals("0", Json.value(0d).toString());
    assertEquals("-1", Json.value(-1d).toString());
    assertEquals("10", Json.value(10d).toString());
  }

  @Test
  public void value_double_failsWithInfinity() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException(IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        Json.value(Double.POSITIVE_INFINITY);
      }
    });
  }

  @Test
  public void value_double_failsWithNaN() {
    String message = "Infinite and NaN values not permitted in JSON";
    assertException(IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        Json.value(Double.NaN);
      }
    });
  }

  @Test
  public void value_boolean() {
    assertSame(Json.TRUE, Json.value(true));
    assertSame(Json.FALSE, Json.value(false));
  }

  @Test
  public void value_string() {
    assertEquals("", Json.value("").asString());
    assertEquals("Hello", Json.value("Hello").asString());
    assertEquals("\"Hello\"", Json.value("\"Hello\"").asString());
  }

  @Test
  public void value_string_toleratesNull() {
    assertSame(Json.NULL, Json.value(null));
  }

}
