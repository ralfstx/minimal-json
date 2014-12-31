package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * A really slow {@code OutputStreamWriter} that doesn't buffer anything.
 */
public class NoBufferOutputStreamWriter extends Writer {
  private final Charset charset;
  private final OutputStream underlying;

  public NoBufferOutputStreamWriter( OutputStream underlying, Charset charset ) {
    this.charset = charset;
    this.underlying = underlying;
  }

  @Override
  public void write( char[] cbuf, int off, int len ) throws IOException {
    CharBuffer cb = CharBuffer.wrap( cbuf, off, len );
    ByteBuffer bb = charset.encode( cb );
    underlying.write(bb.array());
  }

  @Override
  public void flush() throws IOException {
    underlying.flush();
  }

  @Override
  public void close() throws IOException {
    underlying.close();
  }
}