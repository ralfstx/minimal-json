minimal-json
============

A minimal, but complete JSON parser and writer for Java.

Reading JSON
------------

Read a JSON object or array from a Reader or a String:
```java
JsonObject jsonObject = JsonObject.readFrom( jsonString );
```
```java
JsonArray jsonArray = JsonArray.readFrom( jsonReader );
```

Access the contents of a JSON object:

```java
String name = jsonObject.get( "name" ).asString();
int age = jsonObject.get( "age" ).asInt(); // asLong(), asFloat(), asDouble(), ...
```

Access the contents of a JSON array:

```java
String name = jsonArray.get( 0 ).asString();
int age = jsonArray.get( 1 ).asInt(); // asLong(), asFloat(), asDouble(), ...
```

Writing JSON
------------

Create a JSON object and add some values:

```java
new JsonObject().add( "name", "John" ).add( "age", 23 );
```

Create a JSON array and add some values:

```java
new JsonArray().add( "John" ).add( 23 );
```

Write JSON to a Writer:

```java
jsonObject.writeTo( writer );
```

Create JSON as a String:

```java
jsonArray.toString();
```

Performance
-----------

This implementation is used to read a complete JSON text into an object model and to create a JSON text from such a model.
Below is the result of a rough performance comparison with other parsers.
In this benchmark, an example JSON text (~30kB) is read into a JsonObject and then serialized again.
The purpose of this benchmark is only to ensure a reasonable reading and writing performance compared to other state-of-the-art parsers.

It seems that reading performance is good average, while writing performance is very good.

![Read/Write performance compared to other parsers](https://raw.github.com/ralfstx/minimal-json/master/tests/com.eclipsesource.json.performancetest/performance.png "Read/Write performance compared to other parsers")
