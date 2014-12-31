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
package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;


public abstract class JsonRunner {

  public abstract Object readFromString( String string ) throws Exception;

  public abstract Object readFromReader( Reader reader ) throws Exception;

  public abstract String writeToString( Object model ) throws Exception;

  public abstract void writeToWriter( Object model, Writer writer ) throws Exception;

  public static final Charset UTF8 = Charset.forName( "UTF-8" );

  public Object readFromByteArray( byte[] input ) throws Exception {
    return readFromString( new String( input, UTF8 ) );
  }

  public Object readFromInputStream( InputStream in ) throws Exception {
    return readFromReader( new InputStreamReader( in, UTF8 ) );
  }

  public byte[] writeToByteArray( Object model ) throws Exception {
    return writeToString( model ).getBytes( UTF8 );
  }

  public void writeToOutputStream( Object model, OutputStream out ) throws Exception {
    OutputStreamWriter writer = new OutputStreamWriter( out, UTF8 );
    writeToWriter( model, writer );
    writer.flush();  // OutputStreamWriter buffers things
  }

}
