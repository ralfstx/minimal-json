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
 * Represents a JSON array. A JSON array is a sequence of elements, which are JSON values (see
 * {@link JsonValue}).
 * <p>
 * Elements can be added using one of the different <code>add(...)</code> methods. Accepted values
 * are either instances of {@link JsonValue}, or strings, primitive numbers, or boolean values.
 * </p>
 * <p>
 * Elements can be accessed by their index using {@link #get(int)}. This class also supports
 * iterating over the elements in document order using an {@link #iterator()} or an enhanced for
 * loop:
 * </p>
 *
 * <pre>
 * for( JsonValue value : jsonArray ) {
 *   ...
 * }
 * </pre>
 * <p>
 * A n equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a
 * <code>JsonArray</code> instance concurrently, while at least one of these threads modifies the
 * contents of this array, access to the instance must be synchronized externally. Failure to do so
 * may lead to an inconsistent state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings( "serial" ) // use default serial UID
public class JsonArray extends JsonValue implements Iterable<JsonValue> {

  private final List<JsonValue> values;

  /**
   * Creates a new empty JsonArray.
   */
  public JsonArray() {
    values = new ArrayList<JsonValue>();
  }

  /**
   * Creates a new JsonArray with the contents of the specified JSON array.
   *
   * @param array
   *          the JsonArray to get the initial contents from, must not be <code>null</code>
   */
  public JsonArray( JsonArray array ) {
    this( array, false );
  }

  private JsonArray( JsonArray array, boolean unmodifiable ) {
    if( array == null ) {
      throw new NullPointerException( "array is null" );
    }
    if( unmodifiable ) {
      values = Collections.unmodifiableList( array.values );
    } else {
      values = new ArrayList<JsonValue>( array.values );
    }
  }

  /**
   * Reads a JSON array from the given reader.
   *
   * @param reader
   *          the reader to read the JSON array from
   * @return the JSON array that has been read
   * @throws IOException
   *           if an I/O error occurs in the reader
   * @throws ParseException
   *           if the input is not valid JSON
   * @throws UnsupportedOperationException
   *           if the input does not contain a JSON array
   */
  public static JsonArray readFrom( Reader reader ) throws IOException {
    return JsonValue.readFrom( reader ).asArray();
  }

  /**
   * Reads a JSON array from the given string.
   *
   * @param string
   *          the string that contains the JSON array
   * @return the JSON array that has been read
   * @throws ParseException
   *           if the input is not valid JSON
   * @throws UnsupportedOperationException
   *           if the input does not contain a JSON array
   */
  public static JsonArray readFrom( String string ) {
    return JsonValue.readFrom( string ).asArray();
  }

  /**
   * Returns an unmodifiable wrapper for the specified JsonArray. This method allows to provide
   * read-only access to a JsonArray.
   * <p>
   * The returned JsonArray is backed by the given array and reflects subsequent changes. Attempts
   * to modify the returned JsonArray result in an <code>UnsupportedOperationException</code>.
   * </p>
   *
   * @param array
   *          the JsonArray for which an unmodifiable JsonArray is to be returned
   * @return an unmodifiable view of the specified JsonArray
   */
  public static JsonArray unmodifiableArray( JsonArray array ) {
    return new JsonArray( array, true );
  }

  /**
   * Adds the JSON representation of the specified <code>long</code> value to the array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add( long value ) {
    values.add( valueOf( value ) );
    return this;
  }

  /**
   * Adds the JSON representation of the specified <code>float</code> value to the array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add( float value ) {
    values.add( valueOf( value ) );
    return this;
  }

  /**
   * Adds the JSON representation of the specified <code>double</code> value to the array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add( double value ) {
    values.add( valueOf( value ) );
    return this;
  }

  /**
   * Adds the JSON representation of the specified <code>boolean</code> value to the array.
   *
   * @param value
   *          the value to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add( boolean value ) {
    values.add( valueOf( value ) );
    return this;
  }

  /**
   * Adds the JSON representation of the specified string to the array.
   *
   * @param value
   *          the string to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add( String value ) {
    values.add( valueOf( value ) );
    return this;
  }

  /**
   * Adds the specified JsonValue to the array.
   *
   * @param value
   *          the JsonValue to add to the array
   * @return the array itself, to enable method chaining
   */
  public JsonArray add( JsonValue value ) {
    if( value == null ) {
      throw new NullPointerException( "value is null" );
    }
    values.add( value );
    return this;
  }

  /**
   * Returns the number of elements in this array.
   *
   * @return the number of elements in this array
   */
  public int size() {
    return values.size();
  }

  /**
   * Returns <code>true</code> if this array contains no elements.
   *
   * @return <code>true</code> if this array contains no elements
   */
  public boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns the value of the element at the specified position in this array.
   *
   * @param index
   *          the index of the array element to return
   * @return the value of the element at the specified position
   * @throws IndexOutOfBoundsException
   *           if the index is out of range (index &lt; 0 || index &gt;= size()).
   */
  public JsonValue get( int index ) {
    return values.get( index );
  }

  /**
   * Returns a list of the values in this array in document order. The returned list is backed by
   * this array and will reflect subsequent changes. It cannot be used to modify this array.
   * Attempts to modify the returned list will result in an exception.
   *
   * @return a list of the values in this array
   */
  public List<JsonValue> values() {
    return Collections.unmodifiableList( values );
  }

  /**
   * Returns an iterator over the values of this array in document order. The returned iterator
   * cannot be used to modify this array.
   *
   * @return an iterator over the values of this array
   */
  public Iterator<JsonValue> iterator() {
    return Collections.unmodifiableList( values ).iterator();
  }

  @Override
  protected void write( JsonWriter writer ) throws IOException {
    writer.writeBeginArray();
    int length = values.size();
    for( int i = 0; i < length; i++ ) {
      if( i != 0 ) {
        writer.writeArrayValueSeparator();
      }
      values.get( i ).write( writer );
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
