/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ralf - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.json;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.eclipsesource.json.JsonParser.JsonHandler;


public class ParserHandlerExample {

  public static void main(String[] args) throws IOException {
    parse("[ true, false, null, 23, \"foo\", { \"a\": 23, \"b\": 42 } ]");
    parse("{ \"a\": true, \"b\": false, \"c\": [ 23, 42 ] }");
  }

  private static void parse(String text) throws IOException {
    System.out.println("Parsing: " + text);
    System.out.println("JavaParserHandler");
    System.out.println("Result: " + new JsonParser(new StringReader(text)).parse(new JavaParserHandler()));
    System.out.println("LoggingParserHandler");
    System.out.println("Result: " + new JsonParser(new StringReader(text)).parse(new LoggingParserHandler()));
  }

  /**
   * An example of a handler that just prints events to system out. All methods return null.
   */
  @SuppressWarnings("boxing")
  static class LoggingParserHandler implements JsonHandler {

    public JsonValue handleNull(int begin) {
      log(begin, "null");
      return null;
    }

    public JsonValue handleTrue(int begin) {
      log(begin, "true");
      return null;
    }

    public JsonValue handleFalse(int begin) {
      log(begin, "false");
      return null;
    }

    public JsonValue handleString(int begin, int end, String string) {
      log(begin, "string", end, string);
      return null;
    }

    public JsonValue handleNumber(int begin, int end, String string) {
      log(begin, "number", end, string);
      return null;
    }

    public JsonValue handleArrayStart(int begin) {
      log(begin, "array start");
      return null;
    }

    public JsonValue handleArrayEnd(int begin, int end, Object data) {
      log(begin, end, "array end");
      return null;
    }

    public void handleArrayElement(Object data, Object value) {
    }

    public Object handleObjectStart(int begin) {
      log(begin, "object start");
      return null;
    }

    public void handleObjectName(int begin, int end, String name) {
      log(begin, "object name", name, end);
    }

    public void handleObjectMember(Object data, String name, Object value) {
    }

    public JsonValue handleObjectEnd(int begin, int end, Object data) {
      log(begin, end, "object end");
      return null;
    }

  }

  /**
   * An example of a handler that creates a custom data structure out of Json.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  static class JavaParserHandler implements JsonHandler {

    public Object handleNull(int begin) {
      return null;
    }

    public Object handleTrue(int begin) {
      return Boolean.TRUE;
    }

    public Object handleFalse(int begin) {
      return Boolean.FALSE;
    }

    public String handleString(int begin, int end, String string) {
      return string;
    }

    public BigDecimal handleNumber(int begin, int end, String string) {
      return new BigDecimal(string);
    }

    public ArrayList handleArrayStart(int begin) {
      return new ArrayList();
    }

    public Object handleArrayEnd(int begin, int end, Object data) {
      return data;
    }

    public void handleArrayElement(Object data, Object value) {
      ((ArrayList)data).add(value);
    }

    public Object handleObjectStart(int begin) {
      return new HashMap();
    }

    public void handleObjectName(int begin, int end, String name) {
    }

    public void handleObjectMember(Object data, String name, Object value) {
      ((HashMap)data).put(name, value);
    }

    public Object handleObjectEnd(int begin, int end, Object data) {
      return data;
    }

  }

  private static void log(Object... params) {
    System.out.print(">");
    for (Object param : params) {
      System.out.print(" ");
      System.out.print(param);
    }
    System.out.println();
  }

}
