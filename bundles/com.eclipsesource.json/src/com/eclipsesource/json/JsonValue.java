/*******************************************************************************
 * Copyright (c) 2008, 2013 EclipseSource.
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


/**
 * Represents a JSON value. Can be a <code>JsonArray</code>, a <code>JsonObject</code>, or a
 * primitive value. Primitive values can be created using one of the <code>valueOf()</code> methods.
 */
public abstract class JsonValue {

  public static final JsonValue NULL = new JsonPrimitive( "null" );
  public static final JsonValue TRUE = new JsonPrimitive( "true" );
  public static final JsonValue FALSE = new JsonPrimitive( "false" );

  JsonValue() {
    // prevent subclasses from outside this package
  }

  public static JsonValue valueOf( long value ) {
    return new JsonNumber( Long.toString( value, 10 ) );
  }

  public static JsonValue valueOf( float value ) {
    if( Float.isInfinite( value ) || Float.isNaN( value ) ) {
      throw new IllegalArgumentException( "Infinite and NaN values not permitted in JSON" );
    }
    return new JsonNumber( Float.toString( value ) );
  }

  public static JsonValue valueOf( double value ) {
    if( Double.isInfinite( value ) || Double.isNaN( value ) ) {
      throw new IllegalArgumentException( "Infinite and NaN values not permitted in JSON" );
    }
    return new JsonNumber( Double.toString( value ) );
  }

  public static JsonValue valueOf( boolean value ) {
    return value ? TRUE : FALSE;
  }

  public static JsonValue valueOf( String value ) {
    return value == null ? NULL : new JsonString( value );
  }

  public JsonObject asObject() {
    throw new UnsupportedOperationException( "Not an object: " + toString() );
  }

  public JsonArray asArray() {
    throw new UnsupportedOperationException( "Not an array: " + toString() );
  }

  public String asString() {
    throw new UnsupportedOperationException( "Not a string: " + toString() );
  }

  public int asInt() {
    throw new UnsupportedOperationException( "Not a number: " + toString() );
  }

  public long asLong() {
    throw new UnsupportedOperationException( "Not a number: " + toString() );
  }

  public float asFloat() {
    throw new UnsupportedOperationException( "Not a number: " + toString() );
  }

  public double asDouble() {
    throw new UnsupportedOperationException( "Not a number: " + toString() );
  }

  public boolean asBoolean() {
    throw new UnsupportedOperationException( "Not a boolean: " + toString() );
  }

  public boolean isArray() {
    return false;
  }

  public boolean isObject() {
    return false;
  }

  public boolean isString() {
    return false;
  }

  public boolean isNumber() {
    return false;
  }

  public boolean isBoolean() {
    return false;
  }

  public boolean isNull() {
    return false;
  }

  public boolean isTrue() {
    return false;
  }

  public boolean isFalse() {
    return false;
  }

  @Override
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter( stringWriter );
    try {
      write( jsonWriter );
    } catch( IOException exception ) {
      // StringWriter does not throw IOExceptions
      throw new RuntimeException( exception );
    }
    return stringWriter.toString();
  }

  public abstract void write( JsonWriter jsonWriter ) throws IOException;

}
