package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class JacksonRunner implements JsonRunner {

  private final JsonFactory factory;
  private final ObjectMapper mapper;

  public JacksonRunner() {
    // Reuse factory and object mapper to improve performance
    // See http://wiki.fasterxml.com/JacksonBestPracticesPerformance
    factory = new JsonFactory();
    mapper = new ObjectMapper();
  }

  @Override
  public Object readFromString( String string ) throws Exception {
    JsonParser parser = factory.createJsonParser( string );
    try {
      return mapper.readTree( parser );
    } finally {
      parser.close();
    }
  }

  @Override
  public Object readFromReader( Reader reader ) throws Exception {
    JsonParser parser = factory.createJsonParser( reader );
    try {
      return mapper.readTree( parser );
    } finally {
      parser.close();
    }
  }

  @Override
  public String writeToString( Object model ) throws Exception {
    return mapper.writeValueAsString( model );
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    mapper.writeValue( writer, model );
  }

}
