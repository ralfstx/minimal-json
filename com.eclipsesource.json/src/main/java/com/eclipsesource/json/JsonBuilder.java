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

import java.lang.reflect.Array;

import java.util.Map;
import java.util.Map.Entry;


/**
 * A simple builder which allows to create a JsonValue from a java object. <b> As this is part of
 * the minimal-json library, it is a minimal converter, and therefore does <b>not</b> resolve cyclic
 * structures.
 *
 * @author Pascal Bihler
 */
public class JsonBuilder {
  /**
   * Creates a JsonValue from a Java object. If the object implements {@link JsonSerializable}, its
   * {@link JsonSerializable#asJsonValue()} method is called and the result returned.
   * <p>
   * Converts null values to null literals and non-trivial objects to their string representation.
   *
   * @param object
   *          The object to convert to json
   * @return The Java object as {@link JsonValue}
   */
  public static JsonValue toJsonValue(final Object object) {
    if (object == null) {
      return Json.NULL;
    } else if (object instanceof JsonValue) {
      return (JsonValue) object;
    } else if (object instanceof JsonSerializable) {
      return ((JsonSerializable)object).asJsonValue();
    } else if (object instanceof Boolean) {
      return Json.value(((Boolean)object).booleanValue());
    } else if (object instanceof Byte) {
      return Json.value(((Byte)object).byteValue());
    } else if (object instanceof Short) {
      return Json.value(((Short)object).shortValue());
    } else if (object instanceof Integer) {
      return Json.value(((Integer)object).intValue());
    } else if (object instanceof Long) {
      return Json.value(((Long)object).longValue());
    } else if (object instanceof Float) {
      return Json.value(((Float)object).floatValue());
    } else if (object instanceof Double) {
      return Json.value(((Double)object).doubleValue());
    } else if (object.getClass().isArray()) {
      return arrayToJsonValue(object);
    } else if (object instanceof Iterable) {
      return iterableToJsonValue((Iterable<?>)object);
    } else if (object instanceof Map) {
      return mapToJsonValue((Map<?, ?>)object);
    } else {
      return Json.value(String.valueOf(object));
    }
  }

  /**
   * Creates a JsonArray from a collection object.
   */
  static JsonArray iterableToJsonValue(final Iterable<?> collection) {
    final JsonArray array = new JsonArray();
    for (final Object element : collection) {
      array.add(toJsonValue(element));
    }
    return array;
  }

  /**
   * Creates a JsonObject from a Java Map
   */
  static JsonObject mapToJsonValue(final Map<?, ?> map) {
    final JsonObject object = new JsonObject();
    for (final Entry<?, ?> entry : map.entrySet()) {
      object.add(String.valueOf(entry.getKey()), toJsonValue(entry.getValue()));
    }
    return object;
  }

  /**
   * Creates a JsonArray from an array.
   */
  static JsonValue arrayToJsonValue(final Object inputArray) {
    final JsonArray array = new JsonArray();
    final int arrayLength = Array.getLength(inputArray);
    for (int i = 0; i < arrayLength; i++) {
      final Object element = Array.get(inputArray, i);
      array.add(toJsonValue(element));
    }
    return array;
  }
}
