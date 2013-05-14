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
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Represents a JSON object. A JSON object contains a sequence of members, which are pairs of a name
 * and a JSON value (see {@link JsonValue}). Although JSON objects should be used for unordered
 * collections, this class stores members in document order.
 * <p>
 * Members can be added using one of the different <code>add(...)</code> methods. Accepted values
 * are either instances of {@link JsonValue}, or strings, primitive numbers, or boolean values.
 * </p>
 * <p>
 * Members can be accessed by their name using {@link #get(String)}. A list of all names can be
 * obtained from the method {@link #names()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads access a
 * <code>JsonObject</code> instance concurrently, while at least one of these threads modifies the
 * contents of this object, access to the instance must be synchronized externally. Failure to do so
 * may lead to an inconsistent state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings( "serial" ) // use default serial UID
public class JsonObject extends JsonValue {

  private final List<String> names;
  private final List<JsonValue> values;
  private transient HashIndexTable table;

  /**
   * Creates a new empty JsonObject.
   */
  public JsonObject() {
    names = new ArrayList<String>();
    values = new ArrayList<JsonValue>();
    table = new HashIndexTable();
  }

  /**
   * Creates a new JsonObject, initialized with the contents of the specified JSON object.
   *
   * @param object
   *          the JSON object to get the initial contents from, must not be <code>null</code>
   */
  public JsonObject( JsonObject object ) {
    this( object, false );
  }

  private JsonObject( JsonObject object, boolean unmodifiable ) {
    if( object == null ) {
      throw new NullPointerException( "object is null" );
    }
    if( unmodifiable ) {
      names = Collections.unmodifiableList( object.names );
      values = Collections.unmodifiableList( object.values );
    } else {
      names = new ArrayList<String>( object.names );
      values = new ArrayList<JsonValue>( object.values );
    }
    table = new HashIndexTable();
    updateHashIndex();
  }

  /**
   * Reads a JSON object from the given reader.
   *
   * @param reader
   *          the reader to read the JSON object from
   * @return the JSON object that has been read
   * @throws IOException
   *           if an I/O error occurs in the reader
   * @throws ParseException
   *           if the input is not valid JSON
   * @throws UnsupportedOperationException
   *           if the input does not contain a JSON object
   */
  public static JsonObject readFrom( Reader reader ) throws IOException {
    return JsonValue.readFrom( reader ).asObject();
  }

  /**
   * Reads a JSON object from the given string.
   *
   * @param string
   *          the string that contains the JSON object
   * @return the JSON object that has been read
   * @throws ParseException
   *           if the input is not valid JSON
   * @throws UnsupportedOperationException
   *           if the input does not contain a JSON object
   */
  public static JsonObject readFrom( String string ) {
    return JsonValue.readFrom( string ).asObject();
  }

  /**
   * Returns an unmodifiable JsonObject for the specified one. This method allows to provide
   * read-only access to a JsonObject.
   * <p>
   * The returned JsonObject is backed by the given object and reflect changes that happen to it.
   * Attempts to modify the returned JsonObject result in an
   * <code>UnsupportedOperationException</code>.
   * </p>
   *
   * @param object
   *          the JsonObject for which an unmodifiable JsonObject is to be returned
   * @return an unmodifiable view of the specified JsonObject
   */
  public static JsonObject unmodifiableObject( JsonObject object ) {
    return new JsonObject( object, true );
  }

  /**
   * Adds a new member to this object, with the specified name and the JSON representation of the
   * specified <code>long</code> value.
   * <p>
   * This method <strong>does not prevent duplicate names</strong>. Adding a member with a name that
   * is already contained in the object will add another member with the same name. In order to
   * ensure that the names are unique, the method <code>remove( String )</code> can be called before
   * calling this method. However, this practice incurs a performance penalty and should only be
   * used when the calling code can not ensure that same name won't be added more than once.
   * </p>
   *
   * @param name
   *          the name of the member to add
   * @param value
   *          the value of the member to add
   * @return the object itself, to enable method chaining
   */
  public JsonObject add( String name, long value ) {
    add( name, valueOf( value ) );
    return this;
  }

  /**
   * Adds a new member to this object, with the specified name and the JSON representation of the
   * specified <code>float</code> value.
   * <p>
   * This method <strong>does not prevent duplicate names</strong>. Adding a member with a name that
   * is already contained in the object will add another member with the same name. In order to
   * ensure that the names are unique, the method <code>remove( String )</code> can be called before
   * calling this method. However, this practice incurs a performance penalty and should only be
   * used when the calling code can not ensure that same name won't be added more than once.
   * </p>
   *
   * @param name
   *          the name of the member to add
   * @param value
   *          the value of the member to add
   * @return the object itself, to enable method chaining
   */
  public JsonObject add( String name, float value ) {
    add( name, valueOf( value ) );
    return this;
  }

  /**
   * Adds a new member to this object, with the specified name and the JSON representation of the
   * specified <code>double</code> value.
   * <p>
   * This method <strong>does not prevent duplicate names</strong>. Adding a member with a name that
   * is already contained in the object will add another member with the same name. In order to
   * ensure that the names are unique, the method <code>remove( String )</code> can be called before
   * calling this method. However, this practice incurs a performance penalty and should only be
   * used when the calling code can not ensure that same name won't be added more than once.
   * </p>
   *
   * @param name
   *          the name of the member to add
   * @param value
   *          the value of the member to add
   * @return the object itself, to enable method chaining
   */
  public JsonObject add( String name, double value ) {
    add( name, valueOf( value ) );
    return this;
  }

  /**
   * Adds a new member to this object, with the specified name and the JSON representation of the
   * specified <code>boolean</code> value.
   * <p>
   * This method <strong>does not prevent duplicate names</strong>. Adding a member with a name that
   * is already contained in the object will add another member with the same name. In order to
   * ensure that the names are unique, the method <code>remove( String )</code> can be called before
   * calling this method. However, this practice incurs a performance penalty and should only be
   * used when the calling code can not ensure that same name won't be added more than once.
   * </p>
   *
   * @param name
   *          the name of the member to add
   * @param value
   *          the value of the member to add
   * @return the object itself, to enable method chaining
   */
  public JsonObject add( String name, boolean value ) {
    add( name, valueOf( value ) );
    return this;
  }

  /**
   * Adds a new member to this object, with the specified name and the JSON representation of the
   * specified string.
   * <p>
   * This method <strong>does not prevent duplicate names</strong>. Adding a member with a name that
   * is already contained in the object will add another member with the same name. In order to
   * ensure that the names are unique, the method <code>remove( String )</code> can be called before
   * calling this method. However, this practice incurs a performance penalty and should only be
   * used when the calling code can not ensure that same name won't be added more than once.
   * </p>
   *
   * @param name
   *          the name of the member to add
   * @param value
   *          the value of the member to add
   * @return the object itself, to enable method chaining
   */
  public JsonObject add( String name, String value ) {
    add( name, valueOf( value ) );
    return this;
  }

  /**
   * Adds a new member to this object, with the specified name and JSON value.
   * <p>
   * This method <strong>does not prevent duplicate names</strong>. Adding a member with a name that
   * is already contained in the object will add another member with the same name. In order to
   * ensure that the names are unique, the method <code>remove( String )</code> can be called before
   * calling this method. However, this practice incurs a performance penalty and should only be
   * used when the calling code can not ensure that same name won't be added more than once.
   * </p>
   *
   * @param name
   *          the name of the member to add
   * @param value
   *          the value of the member to add
   * @return the object itself, to enable method chaining
   */
  public JsonObject add( String name, JsonValue value ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    if( value == null ) {
      throw new NullPointerException( "value is null" );
    }
    table.add( name, names.size() );
    names.add( name );
    values.add( value );
    return this;
  }

  /**
   * Removes a member with the specified name from this object. If this object contains multiple
   * members with the given name, only the first one is removed. If this object does not contain a
   * member with the specified name, the object is not modified.
   *
   * @param name
   *          the name of the member to remove
   * @return the object itself, to enable method chaining
   */
  public JsonObject remove( String name ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    int index = indexOf( name );
    if( index != -1 ) {
      table.remove( name );
      names.remove( index );
      values.remove( index );
    }
    return this;
  }

  /**
   * Returns the value of the member with the specified name in this object.
   *
   * @param name
   *          the name of the member whose value is to be returned
   * @return the value of the member with the specified name, or <code>null</code> if there is no
   *         member with that name in this object
   */
  public JsonValue get( String name ) {
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    int index = indexOf( name );
    return index != -1 ? values.get( index ) : null;
  }

  /**
   * Returns the number of members (i.e. name/value pairs) in this object.
   *
   * @return the number of members in this object
   */
  public int size() {
    return names.size();
  }

  /**
   * Returns <code>true</code> if this object contains no members.
   *
   * @return <code>true</code> if this object contains no members
   */
  public boolean isEmpty() {
    return names.isEmpty();
  }

  /**
   * Returns a list of the names in this object in document order. The returned list is backed by
   * this object and will reflect subsequent changes. It cannot be used to modify this object.
   * Attempts to modify the returned list will result in an exception.
   *
   * @returns a list of the names in this object
   */
  public List<String> names() {
    return Collections.unmodifiableList( names );
  }

  @Override
  protected void write( JsonWriter writer ) throws IOException {
    writer.writeBeginObject();
    int length = names.size();
    for( int i = 0; i < length; i++ ) {
      if( i != 0 ) {
        writer.writeObjectValueSeparator();
      }
      writer.writeString( names.get( i ) );
      writer.writeNameValueSeparator();
      values.get( i ).write( writer );
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

  private int indexOf( String name ) {
    int index = table.get( name );
    if( index != -1 && name.equals( names.get( index ) ) ) {
      return index;
    }
    return names.indexOf( name );
  }

  private synchronized void readObject( ObjectInputStream inputStream ) throws IOException,
      ClassNotFoundException
  {
    inputStream.defaultReadObject();
    table = new HashIndexTable();
    updateHashIndex();
  }

  private void updateHashIndex() {
    int size = names.size();
    for( int i = 0; i < size; i++ ) {
      table.add( names.get( i ), i );
    }
  }

  static class HashIndexTable {

    private final byte[] hashTable = new byte[ 32 ]; // must be a power of two

    public HashIndexTable() {
    }

    public HashIndexTable( HashIndexTable original ) {
      System.arraycopy( original.hashTable, 0, hashTable, 0, hashTable.length );
    }

    void add( String name, int index ) {
      if( index < 0xff ) {
        int slot = hashSlotFor( name );
        if( hashTable[slot] == 0 ) {
          // increment by 1, 0 stands for empty
          hashTable[slot] = (byte)( index + 1 );
        }
      }
    }

    void remove( String name ) {
      int slot = hashSlotFor( name );
      hashTable[slot] = 0;
    }

    int get( Object name ) {
      int slot = hashSlotFor( name );
      // subtract 1, 0 stands for empty
      return ( hashTable[slot] & 0xff ) - 1;
    }

    private int hashSlotFor( Object element ) {
      return element.hashCode() & hashTable.length - 1;
    }

  }

}
