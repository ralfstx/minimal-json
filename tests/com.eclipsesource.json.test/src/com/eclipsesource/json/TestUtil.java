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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.Assert.*;


public class TestUtil {

  public static void assertException( Class<? extends Exception> type,
                                      String message,
                                      Runnable runnable )
  {
    try {
      runnable.run();
      fail( "Expected exception: " + type.getName() );
    } catch( Exception exception ) {
      assertSame( "type", type, exception.getClass() );
      assertEquals( "message", message, exception.getMessage() );
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
