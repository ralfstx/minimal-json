minimal-json
============

A minimal, but complete JSON parser and writer for Java.

Reading JSON from a Reader
--------------------------

    JsonObject object = JsonObject.readFrom( reader );
    String name = object.get( "name" ).asString();
    int age = object.get( "age" ).asInt();

Reading JSON from a String
--------------------------

    JsonObject object = JsonObject.readFrom( json );

Accessing nested arrays
-----------------------

    JsonArray results = object.getValue( "results" ).asArray();
    double first = results.get( 0 ).asDouble();

Writing JSON to a Writer
------------------------

    JsonArray array = new JsonArray();
    array.append( 23 );
    array.append( "foo" );
    array.writeTo( writer );

Writing JSON to a String
------------------------

    String json = array.toString();
