/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json;

import static com.eclipsesource.json.TestUtil.assertException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Test;

import com.eclipsesource.json.JsonHandler.ElementList;
import com.eclipsesource.json.JsonHandler.MemberSet;

public class JsonStreamingTest {

  @SuppressWarnings("boxing")
  private static class RouteList extends ElementList {
	@Override
	protected void addElement(JsonValue value, ParserContext context) {
	  bus_routes.add(value.asObject().get("id").asInt());
	}
	ArrayList<Integer> bus_routes = new ArrayList<Integer>();
  }

  @Test
  public void withNestedElementDistiller() throws IOException {
	final CollectionFactory factory = new CollectionFactory() {
      @Override
      public ElementList handleArrayStart(ParserContext context) {
    	if (context.getNesting() == 1 && "routes".equals(context.getFieldName())) {
    	  return new RouteList();
    	}
	    return new JsonArray();
      }
      @Override
      public MemberSet handleObjectStart(ParserContext context) {
	    return new JsonObject();
	  }
	};
	StringReader reader = new StringReader("{\"area\":\"Boston\",\"routes\":[{\"id\":39,\"to\":" +
	    "\"Back Bay\"},{\"to\":\"Dudley Sta.\",\"id\":66},{\"id\":47,\"to\":\"Central Sq.\"}]}");
    JsonValue json = JsonValue.readFrom(reader, factory);
    assertEquals("Boston", json.asObject().getString("area", null));
    RouteList mbta_routes = (RouteList) json.asObject().get("routes");
    assertEquals(3, mbta_routes.bus_routes.size());
    assertEquals(66, mbta_routes.bus_routes.get(1).intValue());
  }

  @Test
  public void withElementFilterByOverride() throws IOException {
	StringReader reader = new StringReader("{\"all\":[1,2,3,4,5],\"odd\":[1,2,3,4,5]}");
	final CollectionFactory factory = new CollectionFactory() {
      @Override
      public ElementList handleArrayStart(ParserContext context) {
    	if ("odd".equals(context.getFieldName())) {
    	  return new JsonArray() {
    		@Override
    		final protected void addElement(JsonValue value, ParserContext context) {
    		  if (value.asInt() % 2 == 1) {
    		    add(value);
    		  }
    		}
    	  };
    	}
	    return new JsonArray();
      }
	  @Override
    public MemberSet handleObjectStart(ParserContext context) {
	    return new JsonObject();
	  }
	};
	JsonObject filtered = JsonValue.readFrom(reader, factory).asObject();
	assertEquals(5, filtered.get("all").asArray().size());
	JsonArray odd = filtered.get("odd").asArray();
	assertEquals(3, odd.size());
	assertEquals(3, odd.get(1).asInt());
  }

  private static class EditorPosition {
    final int line, column;
    EditorPosition(int line, int column) {
      this.line = line;
      this.column = column;
    }
    boolean lessThan(EditorPosition other) {
      if (line < other.line) {
    	return true;
      } else if (line == other.line) {
    	return column < other.column;
      }
      return false;
    }
	boolean equals(EditorPosition other) {
      return other.line == line && other.column == column;
    }
  }

  private static class EditorContent {
	final static int ATOMIC = 0, ARRAY = 1, OBJECT = 2;
    int type = ATOMIC;
    EditorPosition last;
    EditorContent click(EditorPosition pos) {
      if (pos.lessThan(last) || pos.equals(last)) {
    	return this;
      }
      return null;
    }
  }

  private static class ComplexEditorContent extends EditorContent {
    final EditorPosition first;
	final ArrayList<EditorContent> children = new ArrayList<EditorContent>();
	ComplexEditorContent(int type, EditorPosition first) {
	  this.type = type;
	  this.first = first;
	}
	void addChild(JsonValue value, ParserContext context) {
	  EditorContent entity = null;
      if (value instanceof ElementList) {
        entity = ((ArrayAnnotator) value).array;
      } else if (value instanceof MemberSet) {
        entity = ((ObjectAnnotator) value).object;
      } else {
        entity = new EditorContent();
      }
      entity.last = new EditorPosition(context.getLine(), context.getColumn());
      if (last == null || last.lessThan(entity.last)) {
        last = entity.last;
      }
      children.add(entity);
	}
	@Override
	EditorContent click(EditorPosition pos) {
	  if (pos.lessThan(first) || last.lessThan(pos)) {
	    return null;
	  }
	  if (pos.equals(first)) {
        return this;
	  }
	  for (EditorContent child : children) {
        EditorContent content = child.click(pos);
        if (content != null) {
          return content;
        }
	  }
	  return this;
	}
  }

  private static class ArrayAnnotator extends ElementList {
    final ComplexEditorContent array;
    ArrayAnnotator(int line, int column) {
	  array = new ComplexEditorContent(EditorContent.ARRAY, new EditorPosition(line, column));
    }
    @Override
    final protected void addElement(JsonValue value, ParserContext context) {
      array.addChild(value, context);
    }
  }

  private static class ObjectAnnotator extends MemberSet {
    final ComplexEditorContent object;
    ObjectAnnotator(int line, int column) {
	  object = new ComplexEditorContent(EditorContent.OBJECT, new EditorPosition(line, column));
    }
    @Override
    final protected void addMember(String name, JsonValue value, ParserContext context) {
      object.addChild(value, context);
    }
  }

  private static ComplexEditorContent annotateContent(String content) throws IOException {
    final CollectionFactory factory = new CollectionFactory() {
      @Override
      public ElementList handleArrayStart(ParserContext context) {
        return new ArrayAnnotator(context.getLine(), context.getColumn());
  	  }
      @Override
      public MemberSet handleObjectStart(ParserContext context) {
  	    return new ObjectAnnotator(context.getLine(), context.getColumn());
  	  }
    };
	StringReader reader = new StringReader(content);
	JsonValue json = JsonValue.readFrom(reader, factory);
	if (json instanceof ArrayAnnotator) {
      return ((ArrayAnnotator) json).array;
	} else if (json instanceof ObjectAnnotator) {
	  return ((ObjectAnnotator) json).object;
	}
	return null;
  }

  @Test
  public void withPositionAnnotator() throws IOException {
	String c;
	c = "{\"id\":15, \"clients\":[4, 17, 10], \"address\":{\n" +
	    "  \"street\":\"1 Main St.\", \"town\":\"Springfield\"} }";
	ComplexEditorContent root = annotateContent(c);
	assertEquals(1, root.first.column);
	assertEquals(3, root.children.size());
	ArrayList<EditorContent> children = root.children;
	assertEquals(EditorContent.ATOMIC, children.get(0).type);
	ComplexEditorContent array = (ComplexEditorContent) children.get(1);
	assertEquals(EditorContent.ARRAY, array.type);
	assertEquals(1, array.first.line);
	assertEquals(21, array.first.column);
	assertEquals(1, array.last.line);
	assertEquals(31, array.last.column);
	ComplexEditorContent object = (ComplexEditorContent) children.get(2);
	assertEquals(EditorContent.OBJECT, object.type);
	assertEquals(1, object.first.line);
	assertEquals(44, object.first.column);
	assertEquals(2, object.last.line);
	assertEquals(46, object.last.column);
	assertEquals(EditorContent.ATOMIC, root.click(new EditorPosition(2, 1)).type);
	assertEquals(EditorContent.ARRAY, root.click(new EditorPosition(1, 21)).type);
	ComplexEditorContent address = (ComplexEditorContent) root.click(new EditorPosition(2, 46));
	assertEquals(2, address.children.size());
  }

  private static class SkippingReaderFactory extends CollectionFactory {
	int n_skip, idx;
	boolean is_object;
	void skip ( ParserContext context ) {
      try {
    	if ( n_skip == 0 ) {
          context.skipAll();
    	} else {
		  context.skipNext( n_skip );
    	}
	  } catch ( IOException e ) { }
	  idx = 0;
	}
	@Override
  public ElementList handleArrayStart( ParserContext context ) {
	  return new JsonArray() {
		@Override
		protected void addElement( JsonValue value, ParserContext context ) {
		  if ( !is_object && 0 == idx++ ) {
		    skip( context );
		  }
		  add( value );
		}
	  };
	}
	@Override
  public MemberSet handleObjectStart( ParserContext context ) {
      return new JsonObject() {
        @Override
        protected void addMember( String name, JsonValue value, ParserContext context ) {
	      if ( is_object && 0 == idx++ ) {
	        skip( context );
	      }
	      add( name, value );
	    }
      };
	}
  }

  private static JsonArray skipElements( String json, int n_skip ) throws IOException {
    SkippingReaderFactory factory = new SkippingReaderFactory();
    factory.n_skip = n_skip;
	StringReader reader = new StringReader( json );
    return JsonValue.readFrom( reader, factory ).asArray();
  }

  @Test
  public void skipNext_repeatedlyByOneAndFromLast() throws IOException {
	JsonArray array = skipElements( "[1, 4,8, 7, 9]", 1 );
	assertEquals( 1, array.get( 0 ).asInt() );
	assertEquals( 8, array.get( 1 ).asInt() );
	assertEquals( 9, array.get( 2 ).asInt() );
  }

  @Test
  public void skipNext_repeatedlyWideAndFromLast() throws IOException {
	JsonArray array = skipElements( "[1, 4,8, 7, 9]", 3 );
	assertEquals( 1, array.get( 0 ).asInt() );
	assertEquals( 9, array.get( 1 ).asInt() );
  }

  @Test
  public void skipNext_fromBeforeAndBeyondLast() throws IOException {
	JsonArray array = skipElements( "[1, 4,8, 7, 9]", 5 );
	assertEquals( 1, array.get( 0 ).asInt() );
	assertEquals( 1, array.size() );
  }

  @Test
  public void skipAll_isIdempotent() throws IOException {
    final CollectionFactory factory = new CollectionFactory() {
      @Override
      public ElementList handleArrayStart( ParserContext context ) {
        return new JsonArray();
      }
      @Override
      public MemberSet handleObjectStart( ParserContext context ) {
    	return new JsonObject() {
	      @Override
	      protected void addMember( String name, JsonValue value, ParserContext context ) {
            if ( name.equals( "id" ) ) {
              try {
                context.skipAll();
                context.skipAll();
              } catch ( IOException e ) {
                fail();
              }
            }
	    	add( name, value );
		  }
		};
      }
    };
    StringReader json = new StringReader( "[1,2 ,3, 4,2, {\"id\":42,\"color\":\"blue\"}, 10]" );
    JsonArray array = JsonValue.readFrom( json, factory ).asArray();
    assertEquals( 7, array.size() );
    assertEquals( 10, array.get( 6 ).asInt() );
    JsonObject object = array.get( 5 ).asObject();
    assertEquals( 42, object.getInt( "id", 0 ) );
    assertEquals( null, object.get("color") );
  }

  @Test
  public void skip_leavesLastDelimiterCurrent() {
    assertException( ParseException.class, new Runnable() {
	  public void run() {
        try {
          skipElements( "[1, 4,8, 7, 9 } ", 0 );
	      fail();
		} catch (IOException e) { }
	  }
	} );
  }

  @Test
  public void skipNext_failsFromObjectContext() {
	final SkippingReaderFactory factory = new SkippingReaderFactory();
	factory.is_object = true;
	factory.n_skip = 1;
	final StringReader reader = new StringReader( "[1,4,8,{\"id\":1}]" );
	assertException( IllegalStateException.class, new Runnable() {
	  public void run() {
        try {
	      JsonValue.readFrom( reader, factory );
	      fail();
		} catch (IOException e) { }
	  }
	} );
  }
}
