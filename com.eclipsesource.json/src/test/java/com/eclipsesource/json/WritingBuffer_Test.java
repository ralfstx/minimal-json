package com.eclipsesource.json;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;


public class WritingBuffer_Test {

  private static final int BUFFER_SIZE = 16;
  private StringWriter wrapped;
  private WritingBuffer writer;

  @Before
  public void setUp() {
    wrapped = new StringWriter();
    writer = new WritingBuffer( wrapped, BUFFER_SIZE );
  }

  @Test
  public void testWriteChar() throws IOException {
    writer.write( 'x' );
    writer.flush();

    assertEquals( "x", wrapped.toString() );
  }

  @Test
  public void testWriteChar_exceeding() throws IOException {
    writer.write( createString( BUFFER_SIZE ) );
    writer.write( 'x' );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE + 1 ), wrapped.toString() );
  }

  @Test
  public void testWriteCharArray() throws IOException {
    writer.write( "foobar".toCharArray(), 1, 3 );
    writer.flush();

    assertEquals( "oob", wrapped.toString() );
  }

  @Test
  public void testWriteCharArray_fit() throws IOException {
    writer.write( createString( BUFFER_SIZE - 3 ) );
    writer.write( "foobar".toCharArray(), 1, 3 );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE - 3 ) + "oob", wrapped.toString() );
  }

  @Test
  public void testWriteCharArray_exceeding() throws IOException {
    writer.write( createString( BUFFER_SIZE - 2 ) );
    writer.write( "foobar".toCharArray(), 1, 3 );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE - 2 ) + "oob", wrapped.toString() );
  }

  @Test
  public void testWriteCharArray_exceedingBuffer() throws IOException {
    writer.write( createChars( BUFFER_SIZE + 1 ) );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE + 1 ), wrapped.toString() );
  }

  @Test
  public void testWriteString() throws IOException {
    writer.write( "foobar", 1, 3 );
    writer.flush();

    assertEquals( "oob", wrapped.toString() );
  }

  @Test
  public void testWriteString_fit() throws IOException {
    writer.write( createString( BUFFER_SIZE - 3 ) );
    writer.write( "foobar", 1, 3 );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE - 3 ) + "oob", wrapped.toString() );
  }

  @Test
  public void testWriteString_exceeding() throws IOException {
    writer.write( createString( BUFFER_SIZE - 2 ) );
    writer.write( "foobar", 1, 3 );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE - 2 ) + "oob", wrapped.toString() );
  }

  @Test
  public void testWriteString_exceedingBuffer() throws IOException {
    writer.write( createString( BUFFER_SIZE + 1 ) );
    writer.flush();

    assertEquals( createString( BUFFER_SIZE + 1 ), wrapped.toString() );
  }

  private static String createString( int length  ) {
    return new String( createChars( length ) );
  }

  private static char[] createChars( int length  ) {
    char[] array = new char[length];
    Arrays.fill( array, 'x' );
    return array;
  }

}
