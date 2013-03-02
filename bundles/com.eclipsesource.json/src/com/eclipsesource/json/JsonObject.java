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
 * Represents a JSON object.
 * <p>
 * <strong>Note:</strong> This class is not supposed to be extended by clients.
 * </p>
 */
public class JsonObject extends JsonValue implements Iterable<String> {

  private final List<String> names;
  private final List<JsonValue> values;

  public JsonObject() {
    this.names = new ArrayList<String>();
    this.values = new ArrayList<JsonValue>();
  }

  public JsonObject( JsonObject object ) {
    if( object == null ) {
      throw new NullPointerException( "object is null" );
    }
    this.names = new ArrayList<String>( object.names );
    this.values = new ArrayList<JsonValue>( object.values );
  }

  private JsonObject( List<String> names, List<JsonValue> values ) {
    this.names = names;
    this.values = values;
  }

  public static JsonObject readFrom( Reader reader ) throws IOException {
    return JsonValue.readFrom( reader ).asObject();
  }

  public static JsonObject readFrom( String text ) {
    return JsonValue.readFrom( text ).asObject();
  }

  public static JsonObject unmodifiableObject( JsonObject object ) {
    return new JsonObject( Collections.unmodifiableList( object.names ),
                           Collections.unmodifiableList( object.values ) );
  }

  public JsonObject append( String name, long value ) {
    append( name, valueOf( value ) );
    return this;
  }

  public JsonObject append( String name, float value ) {
    append( name, valueOf( value ) );
    return this;
  }

  public JsonObject append( String name, double value ) {
    append( name, valueOf( value ) );
    return this;
  }

  public JsonObject append( String name, boolean value ) {
    append( name, valueOf( value ) );
    return this;
  }

  public JsonObject append( String name, String value ) {
    append( name, valueOf( value ) );
    return this;
  }

  public JsonObject append( String name, JsonValue value ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    if( value == null ) {
      throw new NullPointerException( "value is null" );
    }
    names.add( name );
    values.add( value );
    return this;
  }

  public int size() {
    return names.size();
  }

  public boolean isEmpty() {
    return names.isEmpty();
  }

  public JsonValue get( String name ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    int index = names.indexOf( name );
    return index != -1 ? values.get( index ) : null;
  }

  public List<String> names() {
    return Collections.unmodifiableList( names );
  }

  public Iterator<String> iterator() {
    return Collections.unmodifiableList( names ).iterator();
  }

  @Override
  protected void write( JsonWriter writer ) throws IOException {
    writer.writeBeginObject();
    Object[] names = this.names.toArray();
    Object[] values = this.values.toArray();
    for( int i = 0; i < names.length; i++ ) {
      if( i != 0 ) {
        writer.writeObjectValueSeparator();
      }
      writer.writeString( (String)names[ i ] );
      writer.writeNameValueSeparator();
      ( (JsonValue)values[ i ] ).write( writer );
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

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + names.hashCode();
    result = 31 * result + values.hashCode();
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj ) {
      return true;
    }
    if( obj == null ) {
      return false;
    }
    if( getClass() != obj.getClass() ) {
      return false;
    }
    JsonObject other = (JsonObject)obj;
    return names.equals( other.names ) && values.equals( other.values );
  }

}
