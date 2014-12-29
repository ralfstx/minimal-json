/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource.
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

import java.io.IOException;


@SuppressWarnings( "serial" ) // use default serial UID
class JsonLiteral extends JsonValue {

  private final String value;

  JsonLiteral( String value ) {
    this.value = value;
  }

  @Override
  protected void write( JsonWriter writer ) throws IOException {
    writer.write( value );
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean asBoolean() {
    return isBoolean() ? isTrue() : super.asBoolean();
  }

  @Override
  public boolean isNull() {
    return this == NULL;
  }

  @Override
  public boolean isBoolean() {
    return this == TRUE || this == FALSE;
  }

  @Override
  public boolean isTrue() {
    return this == TRUE;
  }

  @Override
  public boolean isFalse() {
    return this == FALSE;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public boolean equals( Object object ) {
    if( this == object ) {
      return true;
    }
    if( object == null ) {
      return false;
    }
    if( getClass() != object.getClass() ) {
      return false;
    }
    JsonLiteral other = (JsonLiteral)object;
    return value.equals( other.value );
  }

}
