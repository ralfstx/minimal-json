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
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


/**
 * A dummy JsonRunner that only returns fixed values. Used to determine the overhead of a benchmark.
 */
public class NullRunner extends JsonRunner {

  @Override
  public Object readFromString( String string ) throws Exception {
    return new Object();
  }

  @Override
  public Object readFromReader( Reader reader ) throws Exception {
    return new Object();
  }

  @Override
  public String writeToString( Object model ) throws Exception {
    return "x";
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    writer.write( 'x' );
  }

  @Override
  public Object readFromByteArray( byte[] input ) throws Exception {
    return new Object();
  }

  @Override
  public Object readFromInputStream( InputStream in ) throws Exception {
    return new Object();
  }

  private static final byte[] X_BYTE = {'x'};

  @Override
  public byte[] writeToByteArray( Object model ) throws Exception {
    return X_BYTE;
  }

  @Override
  public void writeToOutputStream( Object model, OutputStream out ) throws Exception {
    out.write( 'x' );
  }
}
