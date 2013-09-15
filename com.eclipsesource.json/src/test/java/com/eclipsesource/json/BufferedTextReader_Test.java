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
import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class BufferedTextReader_Test {

  private BufferedTextReader reader;

  @Test
  public void construct_failsWithNegativeBuffersize() {
    String message = "Illegal buffersize: -1";
    TestUtil.assertException( IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        new BufferedTextReader( new StringReader( "" ), -1 );
      }
    } );
  }

  @Test
  public void construct_failsWithZeroBuffersize() {
    String message = "Illegal buffersize: 0";
    TestUtil.assertException( IllegalArgumentException.class, message, new Runnable() {
      public void run() {
        new BufferedTextReader( new StringReader( "" ), 0 );
      }
    } );
  }

  @Test
  public void getIndex_initiallyZero() {
    reader = new BufferedTextReader( new StringReader( "" ) );

    assertEquals( 0, reader.getIndex() );
  }

  @Test
  public void getIndex_afterReadEmpty() throws IOException {
    reader = new BufferedTextReader( new StringReader( "" ) );

    reader.read();

    assertEquals( 0, reader.getIndex() );
  }

  @Test
  public void getIndex_afterRead() throws IOException {
    reader = new BufferedTextReader( new StringReader( "x" ) );

    reader.read();

    assertEquals( 1, reader.getIndex() );
  }

  @Test
  public void getIndex_afterMultipleReads() throws IOException {
    reader = new BufferedTextReader( new StringReader( "xxx" ) );

    reader.read();
    reader.read();

    assertEquals( 2, reader.getIndex() );
  }

  @Test
  public void getIndex_afterBufferRefill() throws IOException {
    reader = new BufferedTextReader( new StringReader( "0123456789abc" ), 10 );

    readTimes( 12 );

    assertEquals( 12, reader.getIndex() );
  }

  @Test
  public void getIndex_afterReadAfterEnd() throws IOException {
    reader = new BufferedTextReader( new StringReader( "foo" ) );

    readTimes( 4 );

    assertEquals( 3, reader.getIndex() );
  }

  @Test
  public void getPosition_atStart() {
    reader = new BufferedTextReader( new StringReader( "" ) );

    assertEquals( "1:0", getPosition() );
  }

  @Test
  public void getPosition_afterReadEmpty() throws IOException {
    reader = new BufferedTextReader( new StringReader( "" ) );

    reader.read();

    assertEquals( "1:0", getPosition() );
  }

  @Test
  public void getPosition_afterRead() throws IOException {
    reader = new BufferedTextReader( new StringReader( "x" ) );

    reader.read();

    assertEquals( "1:1", getPosition() );
  }

  @Test
  public void getPosition_afterReadNewLine() throws IOException {
    reader = new BufferedTextReader( new StringReader( "\n" ) );

    reader.read();

    assertEquals( "1:1", getPosition() );
  }

  @Test
  public void getPosition_afterReadWindowsNewLine() throws IOException {
    reader = new BufferedTextReader( new StringReader( "\r\n" ) );

    reader.read();
    reader.read();

    assertEquals( "1:2", getPosition() );
  }

  @Test
  public void getPosition_afterReadFirstCharAfterNewLine() throws IOException {
    reader = new BufferedTextReader( new StringReader( "\nx" ) );

    reader.read();
    reader.read();

    assertEquals( "2:1", getPosition() );
  }

  @Test
  public void getPosition_afterReadFirstCharAfterWindowsNewLine() throws IOException {
    reader = new BufferedTextReader( new StringReader( "\r\nx" ) );

    reader.read();
    reader.read();
    reader.read();

    assertEquals( "2:1", reader.getLine() + ":" + reader.getColumn() );
  }

  @Test
  public void getPosition_afterReadMultipleNewLines() throws IOException {
    reader = new BufferedTextReader( new StringReader( "\n\n" ) );

    reader.read();
    reader.read();

    assertEquals( "2:1", getPosition() );
  }

  @Test
  public void read() throws IOException {
    reader = new BufferedTextReader( new StringReader( "foo" ) );

    assertEquals( 'f', reader.read() );
  }

  @Test
  public void read_twice() throws IOException {
    reader = new BufferedTextReader( new StringReader( "bar" ) );

    reader.read();

    assertEquals( 'a', reader.read() );
  }

  @Test
  public void read_afterBufferRefill() throws IOException {
    reader = new BufferedTextReader( new StringReader( "foobar" ), 3 );
    readTimes( 3 );

    assertEquals( 'b', reader.read() );
  }

  @Test
  public void read_returnsNegativeAtEndOfInput() throws IOException {
    reader = new BufferedTextReader( new StringReader( "foo" ) );
    readTimes( 3 );

    assertEquals( -1, reader.read() );
  }

  @Test
  public void read_multipleTimesAtEndOfInput() throws IOException {
    reader = new BufferedTextReader( new StringReader( "foo" ) );
    readTimes( 5 );

    assertEquals( -1, reader.read() );
  }

  @Test
  public void read_returnsNegativeWhenEmpty() throws IOException {
    reader = new BufferedTextReader( new StringReader( "" ) );

    assertEquals( -1, reader.read() );
  }

  @Test
  public void read_withRestrainedReader() throws IOException {
    reader = new BufferedTextReader( new RestrainedStringReader( "rama rama ding dong", 3 ), 7 );
    reader.read();
    reader.startCapture();
    readTimes( 20 );
    String result = reader.endCapture();

    assertEquals( "rama rama ding dong", result );
  }

  @Test
  public void capture_singleChar() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abc" ) );
    readTimes( 2 );
    reader.startCapture();
    readTimes( 1 );

    assertEquals( "b", reader.endCapture() );
  }

  @Test
  public void capture_firstChar() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abc" ) );
    readTimes( 1 );
    reader.startCapture();
    readTimes( 1 );

    assertEquals( "a", reader.endCapture() );
  }

  @Test
  public void capture_lastChar() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abc" ) );
    readTimes( 3 );
    reader.startCapture();
    readTimes( 1 );

    assertEquals( "c", reader.endCapture() );
  }

  @Test
  public void capture_range() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abcde" ) );
    readTimes( 2 );
    reader.startCapture();
    readTimes( 3 );

    assertEquals( "bcd", reader.endCapture() );
  }

  @Test
  public void capture_rangeAtStart() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abcde" ) );
    readTimes( 1 );
    reader.startCapture();
    readTimes( 3 );

    assertEquals( "abc", reader.endCapture() );
  }

  @Test
  public void capture_rangeAtEnd() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abcde" ) );
    readTimes( 3 );
    reader.startCapture();
    readTimes( 3 );

    assertEquals( "cde", reader.endCapture() );
  }

  @Test
  public void capture_emptyRange() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abc" ) );
    readTimes( 2 );
    reader.startCapture();

    assertEquals( "", reader.endCapture() );
  }

  @Test
  public void capture_endWithoutStart() {
    reader = new BufferedTextReader( new StringReader( "abc" ) );

    assertEquals( "", reader.endCapture() );
  }

  @Test
  public void capture_startBeforeRead() throws IOException {
    reader = new BufferedTextReader( new StringReader( "abc" ) );
    reader.startCapture();
    readTimes( 2 );

    assertEquals( "", reader.endCapture() );
  }

  @Test
  public void capture_withBufferShift() throws IOException {
    reader = new BufferedTextReader( new StringReader( "0123456789abcd" ), 10 );
    readTimes( 7 );
    reader.startCapture();
    readTimes( 6 );
    String recorded = reader.endCapture();

    assertEquals( "6789ab", recorded );
  }

  @Test
  public void capture_withBufferShift_atEnd() throws IOException {
    reader = new BufferedTextReader( new StringReader( "0123456789abcd" ), 10 );
    readTimes( 5 );
    reader.startCapture();
    readTimes( 10 );
    String recorded = reader.endCapture();

    assertEquals( "456789abcd", recorded );
  }

  @Test
  public void capture_withBufferExtension() throws IOException {
    reader = new BufferedTextReader( new StringReader( "0123456789----------0123456789" ), 10 );
    readTimes( 5 );
    reader.startCapture();
    readTimes( 20 );
    String recorded = reader.endCapture();

    assertEquals( "456789----------0123", recorded );
  }

  @Test
  public void capture_withMinimalBufferSize() throws IOException {
    reader = new BufferedTextReader( new StringReader( "0123456789" ), 1 );
    readTimes( 2 );
    reader.startCapture();
    readTimes( 5 );
    String recorded = reader.endCapture();

    assertEquals( "12345", recorded );
  }

  @Test
  public void capture_twice() throws IOException {
    reader = new BufferedTextReader( new StringReader( "0123456789abcde" ), 10 );
    readTimes( 5 );
    reader.startCapture();
    readTimes( 8 );
    reader.endCapture();
    readTimes( 1 );
    reader.startCapture();
    readTimes( 1 );
    String recorded = reader.endCapture();

    assertEquals( "d", recorded );
  }

  private String getPosition() {
    return reader.getLine() + ":" + reader.getColumn();
  }

  private void readTimes( int times ) throws IOException {
    for( int i = 0; i < times; i++ ) {
      reader.read();
    }
  }

  /*
   * A StringReader that returns less chars than requested, even when more characters are available.
   */
  static class RestrainedStringReader extends StringReader {

    private final int max;

    public RestrainedStringReader( String string, int max ) {
      super( string );
      this.max = max;
    }

    @Override
    public int read( char[] cbuf, int off, int len ) throws IOException {
      return super.read( cbuf, off, Math.min( len, max ) );
    }

  }

}
