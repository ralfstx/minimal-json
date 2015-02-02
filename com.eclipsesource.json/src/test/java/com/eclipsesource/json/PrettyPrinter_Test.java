/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;


public class PrettyPrinter_Test {

  private StringWriter output;
  private PrettyPrinter printer;

  @Before
  public void setUp() {
    output = new StringWriter();
    printer = new PrettyPrinter( output );
  }

  @Test
  public void testEmptyArray() throws IOException {
    new JsonArray().write( printer );

    assertEquals( "[\n  \n]", output.toString() );
  }

  @Test
  public void testEmptyObject() throws IOException {
    new JsonObject().write( printer );

    assertEquals( "{\n  \n}", output.toString() );
  }

  @Test
  public void testArray() throws IOException {
    new JsonArray().add( 23 ).add( 42 ).write( printer );

    assertEquals( "[\n  23,\n  42\n]", output.toString() );
  }

  @Test
  public void testNestedArray() throws IOException {
    new JsonArray().add( 23 ).add( new JsonArray().add( 42 ) ).write( printer );

    assertEquals( "[\n  23,\n  [\n    42\n  ]\n]", output.toString() );
  }

  @Test
  public void testObject() throws IOException {
    new JsonObject().add( "a", 23 ).add( "b", 42 ).write( printer );

    assertEquals( "{\n  \"a\": 23,\n  \"b\": 42\n}", output.toString() );
  }

  @Test
  public void testNestedObject() throws IOException {
    new JsonObject().add( "a", 23 ).add( "b", new JsonObject().add( "c", 42 ) ).write( printer );

    assertEquals( "{\n  \"a\": 23,\n  \"b\": {\n    \"c\": 42\n  }\n}", output.toString() );
  }

}
