package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.IOException;
import java.io.StringReader;

import com.eclipsesource.json.JsonParser;


public class MiniJsonRunner implements JsonRunner {

  @Override
  public Object read( String json ) {
    try {
      return new JsonParser( new StringReader( json ) ).parse();
    } catch( IOException exception ) {
      throw new RuntimeException( exception );
    }
  }

  @Override
  public String write( Object model ) {
    return model.toString();
  }

}
