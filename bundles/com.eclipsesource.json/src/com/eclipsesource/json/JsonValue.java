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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;


/**
 * Represents a JSON value.
 * <p>
 * <strong>Note:</strong> This class is not supposed to be extended by clients.
 * </p>
 */
public abstract class JsonValue {

  public static final JsonValue NULL = new JsonPrimitive( "null" );
  public static final JsonValue TRUE = new JsonPrimitive( "true" );
  public static final JsonValue FALSE = new JsonPrimitive( "false" );

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

  public static JsonValue readFrom( Reader reader ) throws IOException {
    return new JsonParser( reader ).parse();
  }

  public static JsonValue readFrom( String text ) {
    try {
      return new JsonParser( new StringReader( text ) ).parse();
    } catch( IOException exception ) {
      // StringReader does not throw IOException
      throw new RuntimeException( exception );
    }
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

  /**
   * Indicates whether some other object is "equal to" this one according to the contract specified
   * in {@link Object#equals(Object)}.
   * <p>
   * Two JsonValues are considered equal if and only if they represent the same JSON text. As a
   * consequence, two given JsonObjects may be different even though they contain the same set of
   * names with the same values, but in a different order.
   * </p>
   *
   * @param object
   *          the reference object with which to compare
   * @return true if this object is the same as the obj argument; false otherwise
   */
  @Override
  public boolean equals( Object object ) {
    return super.equals( object );
  }

  public void writeTo( Writer writer ) throws IOException {
    write( new JsonWriter( writer ) );
  }

  protected abstract void write( JsonWriter writer ) throws IOException;

}
