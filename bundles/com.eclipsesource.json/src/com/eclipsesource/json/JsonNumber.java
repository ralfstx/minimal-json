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


class JsonNumber extends JsonValue {

  private final String string;

  JsonNumber( String value ) {
    this.string = value;
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  public void write( JsonWriter writer ) throws IOException {
    writer.write( string );
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public int asInt() {
    return Integer.parseInt( string, 10 );
  }

  @Override
  public long asLong() {
    return Long.parseLong( string, 10 );
  }

  @Override
  public float asFloat() {
    return Float.parseFloat( string );
  }

  @Override
  public double asDouble() {
    return Double.parseDouble( string );
  }

}
