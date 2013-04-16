/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.json.performancetest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * Used to evaluate memory footprint of data structures using a profiler
 */
@SuppressWarnings( { "unused", "unchecked", "rawtypes" } )
public class MemorySampler {

  private static MemorySampler instance;
  private final Object[] objects;
  private final Object byteArray100;
  private final Object shortArray100;
  private final Object intArray100;
  private final Object array10;
  private final Object array100;
  private final Object arrayList10;
  private final Object arrayList100;
  private final Object hashMap10;
  private final Object hashMap100;

  public static void main( String[] args ) throws InterruptedException {
    instance = new MemorySampler();
    Thread.sleep( 1000 * 60 * 10 );
  }

  public MemorySampler() {
    // Create reference objects to be used in measured data structures. Using only objects from this
    // array, the size of the objects itself will not be included in the retained size of the
    // measured data structures.
    objects = createObjectPool( 100 );
    byteArray100 = createByteArray( 100 );
    shortArray100 = createShortArray( 100 );
    intArray100 = createIntArray( 100 );
    array10 = createObjectArray( 10 );
    array100 = createObjectArray( 100 );
    arrayList10 = createArrayList( 10 );
    arrayList100 = createArrayList( 100 );
    hashMap10 = createHashMap( 10 );
    hashMap100 = createHashMap( 100 );
  }

  private Object[] createObjectPool( int size ) {
    Object[] objects = new Object[ size ];
    for( int i = 0; i < size; i++ ) {
      objects[ i ] = new Object();
    }
    return objects;
  }

  private Object createByteArray( int size ) {
    byte[] array = new byte[ size ];
    for( int i = 0; i < size; i++ ) {
      array[ i ] = (byte)i;
    }
    return array;
  }

  private Object createShortArray( int size ) {
    short[] array = new short[ size ];
    for( int i = 0; i < size; i++ ) {
      array[ i ] = (short)i;
    }
    return array;
  }

  private Object createIntArray( int size ) {
    int[] array = new int[ size ];
    for( int i = 0; i < size; i++ ) {
      array[ i ] = i;
    }
    return array;
  }

  private Object createObjectArray( int size ) {
    Object[] array = new Object[ size ];
    for( int i = 0; i < size; i++ ) {
      array[ i ] = objects[ i ];
    }
    return array;
  }

  private Object createArrayList( int size ) {
    List list = new ArrayList();
    for( int i = 0; i < size; i++ ) {
      list.add( objects[ i ] );
    }
    return list;
  }

  private Object createHashMap( int size ) {
    Map map = new HashMap();
    for( int i = 0; i < size; i++ ) {
      map.put( objects[ i ], objects[ 0 ] );
    }
    return map;
  }

}
