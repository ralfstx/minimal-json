/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
