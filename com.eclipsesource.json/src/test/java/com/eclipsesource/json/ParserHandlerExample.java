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

    Stack<Integer> start = new Stack<Integer>();

    public JsonValue handleLiteral(JsonValue literal, ParserContext context) {
      log(context.getOffset() - 4, literal.toString());
      return null;
    }

    public JsonValue handleString(String string, int begin, ParserContext context) {
      log(begin, "string", context.getOffset(), string);
      return null;
    }

    public JsonValue handleNumber(String number, int begin, ParserContext context) {
      log(begin, "number", context.getOffset(), number);
      return null;
    }

    public ElementList handleArrayStart(ParserContext context) {
      int begin = context.getOffset();
      start.push(begin);
      log(begin, "array start");
      return null;
    }

    public ElementList handleArrayEnd(ElementList array, ParserContext context) {
      log(start.pop(), context.getOffset(), "array end");
      return null;
    }

    public MemberSet handleObjectStart(ParserContext context) {
      int begin = context.getOffset();
      start.push(begin);
      log(begin, "object start");
      return null;
    }

    public void handleMemberName(String name, int begin, ParserContext context) {
      log(begin, "object name", name, context.getOffset());
    }

    public MemberSet handleObjectEnd(MemberSet object, ParserContext context) {
      log(start.pop(), context.getOffset(), "object end");
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

    private Object addInner(String name, Object value) {
      if (name == null) {
        ((ArrayList) parent).add(value);
      } else {
        ((HashMap) parent).put(name, value);
      }
      return value;
    }

    @SuppressWarnings("boxing")
    public JsonValue handleLiteral(JsonValue literal, ParserContext context) {
      addInner(context.getFieldName(), literal.asBoolean());
      return null;
    }

    public JsonValue handleString(String string, int begin, ParserContext context) {
      addInner(context.getFieldName(), string);
      return null;
    }

    public JsonValue handleNumber(String number, int begin, ParserContext context) {
      addInner(context.getFieldName(), new BigDecimal(number));
      return null;
    }

    public ElementList handleArrayStart(ParserContext context) {
      if (parent == null) {
        parent = new ArrayList();
      } else {
        parent = addInner(context.getFieldName(), new ArrayList());
      }
      stack.push(parent);
      return null;
    }

    public ElementList handleArrayEnd(ElementList array, ParserContext context) {
      stack.pop();
      return array;
    }

    public MemberSet handleObjectStart(ParserContext context) {
      if (parent == null) {
        parent = new HashMap();
      } else {
        parent = addInner(context.getFieldName(), new HashMap());
      }
      stack.push(parent);
      return null;
    }

    public void handleMemberName(String name, int begin, ParserContext context) {
    }

    public MemberSet handleObjectEnd(MemberSet object, ParserContext context) {
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
