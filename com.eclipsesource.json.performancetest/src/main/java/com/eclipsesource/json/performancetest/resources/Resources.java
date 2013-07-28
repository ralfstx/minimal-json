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
package com.eclipsesource.json.performancetest.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Resources {

  public static String readResource( String name ) throws IOException {
    InputStream inputStream = Resources.class.getResourceAsStream( name );
    if( inputStream == null ) {
      return null;
    }
    StringBuilder stringBuilder = new StringBuilder();
    char[] buffer = new char[ 1024 ];
    try {
      BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF-8" ) );
      int read;
      while( ( read = reader.read( buffer ) ) != -1 ) {
        stringBuilder.append( buffer, 0, read );
      }
    } finally {
      inputStream.close();
    }
    return stringBuilder.toString();
  }

}
