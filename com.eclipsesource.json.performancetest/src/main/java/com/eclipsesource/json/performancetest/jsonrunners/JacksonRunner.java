package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;


public class JacksonRunner implements JsonRunner {

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
  public Object readFromReader( Reader reader ) throws Exception {
    return mapper.readTree( reader );
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
