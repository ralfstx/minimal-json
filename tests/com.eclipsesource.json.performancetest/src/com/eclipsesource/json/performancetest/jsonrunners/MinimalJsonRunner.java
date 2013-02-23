package com.eclipsesource.json.performancetest.jsonrunners;

import com.eclipsesource.json.JsonObject;


public class MinimalJsonRunner implements JsonRunner {

  @Override
  public Object read( String json ) {
    return JsonObject.readFrom( json );
  }

  @Override
  public String write( Object model ) {
    return model.toString();
  }

}
