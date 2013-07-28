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


/*
 * An array list with a simple hash structure to speed up <code>indexOf</code> lookups.
 * The speedup is only effective for small item counts.
 * Only for benchmarks.
 * Hash size MUST be a power of 2.
 */
public class ExampleArrayListWithHash extends ArrayList<String> {

  private static final long serialVersionUID = -7174289492798161725L;

  private final HashIndexTable hashTable;

  public ExampleArrayListWithHash( int hashSize ) {
    hashTable = new HashIndexTable( hashSize );
  }

  @Override
  public boolean add( String element ) {
    hashTable.add( element, size() );
    return super.add( element );
  }

  @Override
  public int indexOf( Object element ) {
    int index = hashTable.get( element );
    if( index != -1 && element.equals( get( index ) ) ) {
      return index;
    }
    return super.indexOf( element );
  }

  static class HashIndexTable {

    private final byte[] hashTable;

    HashIndexTable( int size ) {
      hashTable = new byte[ size ]; // must be a power of two
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
