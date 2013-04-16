minimal-json
============

A fast and minimal JSON parser and writer for Java.
It has been written for (and is used in) the [Eclipse RAP project](http://eclipse.org/rap).
However, I hope that it is useful for others as well, therefore I keep this repository up-to-date.

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

Below is the result of a rough performance comparison with other parsers, namely
[org.json](http://www.json.org/java/index.html),
[Gson](http://code.google.com/p/google-gson/),
[Jackson](http://wiki.fasterxml.com/JacksonHome), and
[JSON.simple](1.1.1).
In this benchmark, an example JSON text (~30kB) is parsed into a Java object and then serialized to JSON again.

Disclaimer: This benchmark is restricted to a single use case and to my limited knowledge on the other libraries.
It may be unfair as it ignores other use cases and perhaps better ways to use these libraries.
Most JSON parsers have more advanced functions and may be more suitable for other use cases.
The purpose of this benchmark is only to ensure a reasonable reading and writing performance compared to other state-of-the-art parsers.

It seems that reading performance is good average, and writing performance is very good.

![Read/Write performance compared to other parsers](https://raw.github.com/ralfstx/minimal-json/master/tests/com.eclipsesource.json.performancetest/performance.png "Read/Write performance compared to other parsers")

License
-------

The code is available under the terms of the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html).

