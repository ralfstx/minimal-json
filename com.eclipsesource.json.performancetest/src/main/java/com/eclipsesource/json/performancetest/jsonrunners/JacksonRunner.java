package com.eclipsesource.json.performancetest.jsonrunners;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class JacksonRunner implements JsonRunner {

  private final JsonFactory factory;

  public JacksonRunner() {
    factory = new JsonFactory();
  }

  @Override
  public Object read( String json ) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonParser parser = factory.createJsonParser( json );
      try {
        return mapper.readTree( parser );
      } finally {
        parser.close();
      }
    } catch( Exception exception ) {
      throw new RuntimeException( exception );
    }
  }

  @Override
  public String write( Object model ) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString( model );
    } catch( Exception exception ) {
      throw new RuntimeException( exception );
    }
  }

}
