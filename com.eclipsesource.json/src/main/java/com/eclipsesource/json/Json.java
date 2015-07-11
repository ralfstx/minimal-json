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

/**
 * Utility class that provides JSON constants.
 */
public abstract class Json {

  private Json() {
    // not meant to be instantiated
  }

  /**
   * Represents the JSON literal <code>null</code>.
   */
  public static final JsonValue NULL = new JsonLiteral("null");

  /**
   * Represents the JSON literal <code>true</code>.
   */
  public static final JsonValue TRUE = new JsonLiteral("true");

  /**
   * Represents the JSON literal <code>false</code>.
   */
  public static final JsonValue FALSE = new JsonLiteral("false");

  /**
   * Returns a JsonValue instance that represents the given <code>int</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(int value) {
    return new JsonNumber(Integer.toString(value, 10));
  }

  /**
   * Returns a JsonValue instance that represents the given <code>long</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(long value) {
    return new JsonNumber(Long.toString(value, 10));
  }

  /**
   * Returns a JsonValue instance that represents the given <code>float</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(float value) {
    if (Float.isInfinite(value) || Float.isNaN(value)) {
      throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
    }
    return new JsonNumber(cutOffPointZero(Float.toString(value)));
  }

  /**
   * Returns a JsonValue instance that represents the given <code>double</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(double value) {
    if (Double.isInfinite(value) || Double.isNaN(value)) {
      throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
    }
    return new JsonNumber(cutOffPointZero(Double.toString(value)));
  }

  /**
   * Returns a JsonValue instance that represents the given string.
   *
   * @param string
   *          the string to get a JSON representation for
   * @return a JSON value that represents the given string
   */
  public static JsonValue value(String string) {
    return string == null ? NULL : new JsonString(string);
  }

  /**
   * Returns a JsonValue instance that represents the given <code>boolean</code> value.
   *
   * @param value
   *          the value to get a JSON representation for
   * @return a JSON value that represents the given value
   */
  public static JsonValue value(boolean value) {
    return value ? TRUE : FALSE;
  }

  private static String cutOffPointZero(String string) {
    if (string.endsWith(".0")) {
      return string.substring(0, string.length() - 2);
    }
    return string;
  }

}
