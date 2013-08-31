package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.eclipsesource.json.JsonValue;


public class MinimalJsonRunner implements JsonRunner {

  @Override
  public Object readFromString( String string ) throws IOException {
    return JsonValue.readFrom( string );
  }

  @Override
  public Object readFromReader( Reader reader ) throws IOException {
    return JsonValue.readFrom( reader );
  }

  @Override
  public String writeToString( Object model ) throws IOException {
    return model.toString();
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws IOException {
    ((JsonValue)model).writeTo( writer );
  }

}
