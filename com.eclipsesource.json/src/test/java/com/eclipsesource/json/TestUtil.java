/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class TestUtil {

  public static <T extends Exception> T assertException( Class<T> type,
                                                         String message,
                                                         Runnable runnable )
  {
    T exception = assertException( type, runnable );
    assertEquals( "message", message, exception.getMessage() );
    return exception;
  }

  @SuppressWarnings( "unchecked" )
  public static <T extends Exception> T assertException( Class<T> type, Runnable runnable ) {
    Exception exception = catchException( runnable, type );
    assertNotNull( "Expected exception: " + type.getName(), exception );
    return (T)exception;
  }

  private static Exception catchException( Runnable runnable, Class<? extends Exception> type ) {
    try {
      runnable.run();
      return null;
    } catch( Exception exception ) {
      if( !type.isAssignableFrom( exception.getClass() ) ) {
        // It must be a RuntimeException, since Runnable.run does not throw checked exceptions
        throw(RuntimeException) exception;
      }
      return exception;
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T serializeAndDeserialize( T instance ) throws Exception {
    return ( T )deserialize( serialize( instance ) );
  }

  public static byte[] serialize( Object object ) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    new ObjectOutputStream( outputStream ).writeObject( object );
    return outputStream.toByteArray();
  }

  public static Object deserialize( byte[] bytes ) throws IOException, ClassNotFoundException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream( bytes );
    return new ObjectInputStream( inputStream ).readObject();
  }

}
