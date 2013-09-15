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
package com.eclipsesource.json;

import java.io.IOException;
import java.io.Reader;


/*
 * An instance of this class reads characters from a text, keeps track of the current position,
 * and captures text ranges in a buffer on request.
 *
 * index: points at the buffer index to read the next character from
 * offset: the offset of the first buffer index within the text
 * fill: points to the first buffer index after the filled region
 *
 * Start
 * -----
 *
 *  offset
 *  v
 * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u]      < input
 * [ | | | | | | | | | | ]                          < buffer
 *  ^
 *  index
 *  fill
 *
 * After first buffer fill
 * -----------------------
 *
 *  offset
 *  v
 * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u]      < input
 * [a|b|c|d|e|f|g|h|i|j|k]                          < buffer
 *  ^                     ^
 *  index                 fill
 *
 * After first read
 * ----------------
 *
 *  offset
 *  v
 * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u]      < input
 * [a|b|c|d|e|f|g|h|i|j|k]                          < buffer
 *    ^                   ^
 *    index               fill
 *
 * After buffer advance and re-fill
 * --------------------------------
 *
 *                        offset
 *                        v
 * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u]      < input
 *                       [l|m|n|o|p|q|r|s|t|u|k]    < buffer
 *                        ^                   ^
 *                        index               fill
 */
class BufferedTextReader {

  private static final int DEFAULT_BUFFERSIZE = 1024;

  private final Reader reader;
  private char[] buffer;
  private int offset;
  private int index;
  private int fill;
  private int line;
  private int lineOffset;
  private int start;
  private boolean atNewline;

  BufferedTextReader( Reader reader ) {
    this( reader, DEFAULT_BUFFERSIZE );
  }

  BufferedTextReader( Reader reader, int buffersize ) {
    if( buffersize <= 0 ) {
      throw new IllegalArgumentException( "Illegal buffersize: " + buffersize );
    }
    this.reader = reader;
    buffer = new char[buffersize];
    line = 1;
    start = -1;
  }

  public int getIndex() {
    return offset + index;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return getIndex() - lineOffset;
  }

  public int read() throws IOException {
    if( fill == -1 ) {
      return -1;
    }
    if( index == fill ) {
      if( fill == buffer.length ) {
        if( start == -1 ) {
          advanceBuffer();
        } else if( start == 0 ) {
          expandBuffer();
        } else {
          shiftBuffer();
        }
      }
      fillBuffer();
      if( fill == -1 ) {
        return -1;
      }
    }
    if( atNewline ) {
      line++;
      lineOffset = getIndex();
    }
    char current = buffer[index++];
    atNewline = current == '\n';
    return current;
  }

  public void startCapture() {
    start = index - 1;
  }

  public String endCapture() {
    if( start == -1 ) {
      return "";
    }
    int end = fill == -1 ? index : index - 1;
    String recorded = new String( buffer, start, end - start );
    start = -1;
    return recorded;
  }

  /*
   * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y]                  < input
   *                     [k|l|m|n|o|p|q|r|s|z|u]                          < buffer before advance
   *                                          ^
   *                                          fill
   *                                          index
   *
   *                                           [k|l|m|n|o|p|q|r|s|z|u]    < buffer after advance
   *                                            ^
   *                                            fill
   *                                            index
   */
  private void advanceBuffer() {
    offset += fill;
    fill = 0;
    index = 0;
  }

  /*
   *               offset before  /   after shift
   *                      v           v
   * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y]      < input
   *                     [k|l|m|n|o|p|q|r|s|t]                < buffer before shift
   *                                  ^       ^
   *                                  start   fill
   *                                          index
   *
   *                                 [q|r|s|t|o|p|q|r|s|t]    < buffer after shift
   *                                  ^       ^
   *                                  start   fill
   *                                          index
   *
   *                                 [q|r|s|t|u|v|w|x|y|t]    < buffer after fill
   *                                  ^                 ^
   *                                  start             fill
   *                                                    index
   */
  private void shiftBuffer() {
    offset += start;
    fill -= start;
    System.arraycopy( buffer, start, buffer, 0, fill );
    index = fill;
    start = 0;
  }

  /*
   * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y]              < input
   *                     [k|l|m|n|o|p|q|r|s|t]                        < buffer before expand
   *                      ^                   ^
   *                      start               fill
   *                                          index
   *
   *                     [k|l|m|n|o|p|q|r|s|t| | | | | | | | | | ]    < buffer after expand
   *                      ^                   ^
   *                      start               fill
   *                                          index
   *
   *                     [k|l|m|n|o|p|q|r|s|t|u|v|w|x|y| | | | | ]    < buffer after fill
   *                      ^                   ^         ^
   *                      start               index     fill
   */
  private void expandBuffer() {
    char[] newBuffer = new char[buffer.length * 2];
    System.arraycopy( buffer, 0, newBuffer, 0, fill );
    buffer = newBuffer;
  }

  /*
   * [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y]      < input
   *                                 [q|r|s|?|?|?|?|?|?|?]    < buffer before fill
   *                                        ^
   *                                        fill
   *                                        index
   *
   *                                 [q|r|s|t|u|v|w|x|y|?]    < buffer after fill
   *                                        ^           ^
   *                                        index       fill
   */
  private void fillBuffer() throws IOException {
    int read = reader.read( buffer, fill, buffer.length - fill );
    if( read == -1 ) {
      fill = -1;
    } else {
      fill += read;
    }
  }

}
