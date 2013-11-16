minimal-json
============

[![Build Status](https://travis-ci.org/ralfstx/minimal-json.png?branch=master)](https://travis-ci.org/ralfstx/minimal-json)

A fast and minimal JSON parser and writer for Java.
It's not an object mapper, but a bare-bones library that aims at being minimal, fast, lightweight, and easy to use.

* minimal: only one package, fair amount of classes, no dependencies
* fast: performance comparable or better than other state-of-the-art JSON parsers (see below)
* leightweight: object representation with minimal memory footprint (e.g. no HashMaps involved)
* easy-to-use: reading, writing and modifying JSON shouldn't require lots of code (short names, fluent style)

Minimal-json is fully covered by unit tests, and field-tested by the [Eclipse RAP project](http://eclipse.org/rap).

Code Examples
-------------

### Read JSON from a String or a Reader:

Reading is buffered already, so you *don't* need to wrap your reader in a BufferedReader.

```java
JsonObject jsonObject = JsonObject.readFrom( string );
JsonArray jsonArray = JsonArray.readFrom( reader );
```

### Access the contents of a JSON object:

```java
String name = jsonObject.get( "name" ).asString();
int age = jsonObject.get( "age" ).asInt(); // asLong(), asFloat(), asDouble(), ...

// or iterate over the members:
for( Member member : jsonObject ) {
  String name = member.getName();
  JsonValue value = member.getValue();
  // ...
}
```

### Access the contents of a JSON array:

```java
String name = jsonArray.get( 0 ).asString();
int age = jsonArray.get( 1 ).asInt(); // asLong(), asFloat(), asDouble(), ...

// or iterate over the values:
for( JsonValue value : jsonArray ) {
  // ...
}
```

### Create JSON objects and arrays:

```java
JsonObject jsonObject = new JsonObject().add( "name", "John" ).add( "age", 23 );
JsonArray jsonArray = new JsonArray().add( "John" ).add( 23 );
```

### Write JSON to a Writer:

Writing is not buffered (to avoid buffering twice), so you *should* use a BufferedWriter.

```java
jsonObject.writeTo( writer );
jsonArray.writeTo( writer );
```

### Export JSON as a String:

```java
jsonObject.toString();
jsonArray.toString();
```

Build
-----

To build minimal-json on your machine, simply checkout the repository, `cd` into it and call maven:
```
cd minimal-json
mvn clean install
```
A continuous integration build is running at [Travis-CI](https://travis-ci.org/ralfstx/minimal-json).


Performance
-----------

Below is the result of a performance comparison with other parsers, namely
[org.json](http://www.json.org/java/index.html),
[Gson](http://code.google.com/p/google-gson/),
[Jackson](http://wiki.fasterxml.com/JacksonHome), and
[JSON.simple](1.1.1).
In this benchmark, an example JSON text (~30kB) is parsed into a Java object and then serialized to JSON again.
All benchmarks can be found in `com.eclipsesource.json.performancetest`.

Although it cannot outperform jackson's exceptional writing performance (which is, to my knowledge, mostly achieved by caching),
minimal-json offers a very good reading and writing performance.

![Read/Write performance compared to other parsers](https://raw.github.com/ralfstx/minimal-json/master/com.eclipsesource.json.performancetest/performance.png "Read/Write performance compared to other parsers")

Disclaimer: This benchmark is restricted to a single use case and to my limited knowledge on the other libraries.
It probably ignores better ways to use these libraries.
The purpose of this benchmark is only to ensure a reasonable reading and writing performance compared to other state-of-the-art parsers.

License
-------

The code is available under the terms of the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html).
