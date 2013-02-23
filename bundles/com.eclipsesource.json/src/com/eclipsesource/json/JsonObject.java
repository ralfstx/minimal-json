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
import java.util.Iterator;
import java.util.List;


public final class JsonObject extends JsonValue implements Iterable<String> {

  private final List<String> names;
  private final List<JsonValue> values;

  public JsonObject() {
    names = new ArrayList<String>();
    values = new ArrayList<JsonValue>();
  }

  public void append( String name, long value ) {
    append( name, valueOf( value ) );
  }

  public void append( String name, float value ) {
    append( name, valueOf( value ) );
  }

  public void append( String name, double value ) {
    append( name, valueOf( value ) );
  }

  public void append( String name, boolean value ) {
    append( name, valueOf( value ) );
  }

  public void append( String name, String value ) {
    append( name, valueOf( value ) );
  }

  public void append( String name, JsonValue value ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    if( value == null ) {
      throw new NullPointerException( "value is null" );
    }
    names.add( name );
    values.add( value );
  }

  public int size() {
    return names.size();
  }

  public boolean isEmpty() {
    return names.isEmpty();
  }

  public String[] getNames() {
    return names.toArray( new String[ names.size() ] );
  }

  public JsonValue getValue( String name ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    int index = names.indexOf( name );
    return index != -1 ? values.get( index ) : null;
  }

  public Iterator<String> iterator() {
    return names.iterator();
  }

  @Override
  public void write( JsonWriter writer ) throws IOException {
    writer.writeBeginObject();
    String[] names = getNames();
    JsonValue[] values = getValues();
    for( int i = 0; i < names.length; i++ ) {
      if( i != 0 ) {
        writer.writeObjectValueSeparator();
      }
      writer.writeString( names[ i ] );
      writer.writeNameValueSeparator();
      values[ i ].write( writer );
    }
    writer.writeEndObject();
  }

  @Override
  public boolean isObject() {
    return true;
  }

  @Override
  public JsonObject asObject() {
    return this;
  }

  private JsonValue[] getValues() {
    return values.toArray( new JsonValue[ values.size() ] );
  }

}
