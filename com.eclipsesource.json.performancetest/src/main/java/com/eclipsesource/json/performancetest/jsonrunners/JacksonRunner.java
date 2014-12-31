/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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
package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JacksonRunner extends JsonRunner {

  private final ObjectMapper mapper;

  public JacksonRunner() {
    // Reuse object mapper to improve performance
    // See http://wiki.fasterxml.com/JacksonBestPracticesPerformance
    mapper = new ObjectMapper();
    mapper.configure( JsonParser.Feature.AUTO_CLOSE_SOURCE, false );
    mapper.configure( JsonGenerator.Feature.AUTO_CLOSE_TARGET, false );
    mapper.configure( JsonGenerator.Feature.FLUSH_PASSED_TO_STREAM, false );
  }

  @Override
  public Object readFromString( String string ) throws Exception {
    return mapper.readTree( string );
  }

  @Override
  public Object readFromByteArray( byte[] bytes ) throws Exception {
    return mapper.readTree( bytes );
  }

  @Override
  public Object readFromReader( Reader reader ) throws Exception {
    return mapper.readTree( reader );
  }

  @Override
  public Object readFromInputStream( InputStream in ) throws Exception {
    return mapper.readTree( in );
  }

  @Override
  public String writeToString( Object model ) throws Exception {
    return mapper.writeValueAsString( model );
  }

  @Override
  public byte[] writeToByteArray( Object model ) throws Exception {
    return mapper.writeValueAsBytes( model );
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    mapper.writeValue( writer, model );
  }

  @Override
  public void writeToOutputStream( Object model, OutputStream out ) throws Exception {
    mapper.writeValue( out, model );
  }

}
