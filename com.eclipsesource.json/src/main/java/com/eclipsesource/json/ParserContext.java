/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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
package com.eclipsesource.json;

import java.io.IOException;


public interface ParserContext {

  /**
   * @return nesting level, starting with {@code 0} for the outermost JSON array or object
   */
  public int getNesting();

  /**
   * @return field name of the current member insertion or null when adding root or element values
   */
  public String getFieldName();

  /**
   * @return line position in the input stream, starting with {@code 1} for the first line
   */
  public int getLine();

  /**
   * @return absolute position in the input stream
   */
  public int getOffset();

  /**
   * @return column position in the input stream, starting with {@code 1} for the first column
   */
  public int getColumn();

  /**
   * Skips the next element of the current JSON array.
   * <p>
   * If there is no next element to be skipped inside this array context, this method has no effect
   * and returns false. Because members of JSON objects are unordered, it is illegal to call this
   * method from inside an object context. Use {@link #skipAll()} instead if all required members
   * have been read from the current object.
   * <p>
   * @return true if the element was skipped or false if there is no next element
   * @throws IOException
   *           if an I/O error occurs in the writer
   * @throws IllegalStateException
   *           if this method was called from the context of a JSON object
   */
  public boolean skipNext() throws IOException;

  /**
   * Skips the next <em>n</em> elements of the current JSON array.
   * <p>
   * If there are fewer elements to be skipped inside this array context than specified by
   * <em>n</em>, then all elements are skipped until the end of the array is reached. The actual
   * number of elements that were skipped is indicated by the return value of this method. Because
   * members of JSON objects are unordered, it is illegal to call this method from inside an object
   * context. Use {@link #skipAll()} instead if all required members have been read from the current
   * object.
   * <p>
   * @param n the number of elements to be skipped
   * @return the number of skipped elements, can be less than <em>n</em> at the end of the array
   * @throws IOException
   *           if an I/O error occurs in the writer
   * @throws IllegalStateException
   *           if this method was called from the context of a JSON object
   */
  public int skipNext( int n ) throws IOException;

  /**
   * Skips all remaining elements or members of the current JSON array or object.
   * <p>
   * The number of elements that were skipped is indicated by the return value of this method.
   * Repeated calls to this method from within the same object or array context have the same effect
   * on the parse as calling this method once.
   * <p>
   * @return the number of skipped elements or members
   * @throws IOException
   *           if an I/O error occurs in the writer
   */
  public int skipAll() throws IOException;

}
