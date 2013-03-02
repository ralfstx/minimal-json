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
