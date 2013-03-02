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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Represents a JSON array.
 * <p>
 * <strong>Note:</strong> This class is not supposed to be extended by clients.
 * </p>
 */
public class JsonArray extends JsonValue implements Iterable<JsonValue> {

  private final List<JsonValue> values;

  public JsonArray() {
    this.values = new ArrayList<JsonValue>();
  }

  public JsonArray( JsonArray array ) {
    if( array == null ) {
      throw new NullPointerException( "array is null" );
    }
    this.values = new ArrayList<JsonValue>( array.values );
  }

  private JsonArray( List<JsonValue> values ) {
    this.values = values;
  }

  public static JsonArray readFrom( Reader reader ) throws IOException {
    return JsonValue.readFrom( reader ).asArray();
  }

  public static JsonArray readFrom( String text ) {
    return JsonValue.readFrom( text ).asArray();
  }

  public static JsonArray unmodifiableArray( JsonArray array ) {
    return new JsonArray( Collections.unmodifiableList( array.values ) );
  }

  public JsonArray append( long value ) {
    values.add( valueOf( value ) );
    return this;
  }

  public JsonArray append( float value ) {
    values.add( valueOf( value ) );
    return this;
  }

  public JsonArray append( double value ) {
    values.add( valueOf( value ) );
    return this;
  }

  public JsonArray append( boolean value ) {
    values.add( valueOf( value ) );
    return this;
  }

  public JsonArray append( String value ) {
    values.add( valueOf( value ) );
    return this;
  }

  public JsonArray append( JsonValue value ) {
    if( value == null ) {
      throw new NullPointerException( "value is null" );
    }
    values.add( value );
    return this;
  }

  public int size() {
    return values.size();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public JsonValue get( int index ) {
    return values.get( index );
  }

  public List<JsonValue> values() {
    return Collections.unmodifiableList( values );
  }

  public Iterator<JsonValue> iterator() {
    return Collections.unmodifiableList( values ).iterator();
  }

  @Override
  protected void write( JsonWriter writer ) throws IOException {
    writer.writeBeginArray();
    JsonValue[] elements = values.toArray( new JsonValue[ values.size() ] );
    for( int i = 0; i < elements.length; i++ ) {
      if( i != 0 ) {
        writer.writeArrayValueSeparator();
      }
      elements[ i ].write( writer );
    }
    writer.writeEndArray();
  }

  @Override
  public boolean isArray() {
    return true;
  }

  @Override
  public JsonArray asArray() {
    return this;
  }

  @Override
  public int hashCode() {
    return values.hashCode();
  }

  @Override
  public boolean equals( Object object ) {
    if( this == object ) {
      return true;
    }
    if( object == null ) {
      return false;
    }
    if( getClass() != object.getClass() ) {
      return false;
    }
    JsonArray other = (JsonArray)object;
    return values.equals( other.values );
  }

}
