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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public final class JsonArray extends JsonValue implements Iterable<JsonValue> {

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

  public static JsonArray unmodifiableArray( JsonArray array ) {
    return new JsonArray( Collections.unmodifiableList( array.values ) );
  }

  public void append( long value ) {
    values.add( valueOf( value ) );
  }

  public void append( float value ) {
    values.add( valueOf( value ) );
  }

  public void append( double value ) {
    values.add( valueOf( value ) );
  }

  public void append( boolean value ) {
    values.add( valueOf( value ) );
  }

  public void append( String value ) {
    values.add( valueOf( value ) );
  }

  public void append( JsonValue value ) {
    if( value == null ) {
      throw new NullPointerException( "value is null" );
    }
    values.add( value );
  }

  public int size() {
    return values.size();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public JsonValue[] getValues() {
    return values.toArray( new JsonValue[ values.size() ] );
  }

  public Iterator<JsonValue> iterator() {
    return Collections.unmodifiableList( values ).iterator();
  }

  @Override
  public void write( JsonWriter writer ) throws IOException {
    writer.writeBeginArray();
    JsonValue[] elements = getValues();
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

}
