package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class JacksonRunner implements JsonRunner {

  private final JsonFactory factory;

  public JacksonRunner() {
    factory = new JsonFactory();
  }

  @Override
  public Object readFromString( String string ) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonParser parser = factory.createJsonParser( string );
    try {
      return mapper.readTree( parser );
    } finally {
      parser.close();
    }
  }

  @Override
  public Object readFromReader( Reader reader ) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    JsonParser parser = factory.createJsonParser( reader );
    try {
      return mapper.readTree( parser );
    } finally {
      parser.close();
    }
  }

  @Override
  public String writeToString( Object model ) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString( model );
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue( writer, model );
  }

}
