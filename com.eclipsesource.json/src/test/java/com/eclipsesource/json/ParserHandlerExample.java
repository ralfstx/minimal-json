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
import java.util.Stack;


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

    public ElementList handleArrayStart(ParserContext context) {
      log(context.getOffset(), "array start");
      return null;
    }

    public ElementList handleArrayEnd(int begin, int end, ElementList data) {
      log(begin, end, "array end");
      return null;
    }

    public void handleArrayElement(ElementList array, JsonValue value, ParserContext context) {
    }

    public MemberSet handleObjectStart(ParserContext context) {
      log(context.getOffset(), "object start");
      return null;
    }

    public void handleMemberName(int begin, int end, String name) {
      log(begin, "object name", name, end);
    }

    public void handleMemberValue(MemberSet object, String name, JsonValue value, ParserContext context) {
    }

    public MemberSet handleObjectEnd(int begin, int end, MemberSet object) {
      log(begin, end, "object end");
      return null;
    }

  }

  /**
   * An example of a handler that creates a custom data structure out of Json.
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  static class JavaParserHandler implements JsonHandler {

    Stack<Object> stack = new Stack();
    Object parent = null;
    String name = null;

    private Object addInner(Object value) {
      if (name == null) {
        ((ArrayList) parent).add(value);
      } else {
        ((HashMap) parent).put(name, value);
      }
      return value;
    }

    public JsonValue handleNull(int begin) {
      return null;
    }

    public JsonValue handleTrue(int begin) {
      addInner(Boolean.TRUE);
      return null;
    }

    public JsonValue handleFalse(int begin) {
      addInner(Boolean.FALSE);
      return null;
    }

    public JsonValue handleString(int begin, int end, String string) {
      addInner(string);
      return null;
    }

    public JsonValue handleNumber(int begin, int end, String string) {
      addInner(new BigDecimal(string));
      return null;
    }

    public ElementList handleArrayStart(ParserContext context) {
      if (parent == null) {
        parent = new ArrayList();
      } else {
        stack.push(parent);
        parent = addInner(new ArrayList());
      }
      return null;
    }

    public ElementList handleArrayEnd(int begin, int end, ElementList array) {
      stack.pop();
      return array;
    }

    public void handleArrayElement(ElementList array, JsonValue value, ParserContext context) throws IOException {
    }

    public MemberSet handleObjectStart(ParserContext context) {
      if (parent == null) {
        parent = new HashMap();
      } else {
        stack.push(parent);
        parent = addInner(new HashMap());
      }
      return null;
    }

    public void handleMemberName(int begin, int end, String name) {
      this.name = name;
    }

    public void handleMemberValue(MemberSet object, String name, JsonValue value, ParserContext context) throws IOException {
    }

    public MemberSet handleObjectEnd(int begin, int end, MemberSet object) {
      stack.pop();
      return object;
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
