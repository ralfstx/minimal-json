package com.eclipsesource.json;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;


@SuppressWarnings("boxing")
public class JsonBuilder_Test {
  @Test
  public void convertsNull() {
    JsonValue value = JsonBuilder.toJsonValue((String)null);
    assertEquals("null", value.toString());
  }

  @Test
  public void convertsBooleanTrue() {
    JsonValue value = JsonBuilder.toJsonValue(true);
    assertEquals("true", value.toString());
  }

  @Test
  public void convertsBooleanFalse() {
    JsonValue value = JsonBuilder.toJsonValue(false);
    assertEquals("false", value.toString());
  }

  @Test
  public void convertsZeroInt() {
    JsonValue value = JsonBuilder.toJsonValue(0);
    assertEquals("0", value.toString());
  }

  @Test
  public void convertsPositiveInt() {
    JsonValue value = JsonBuilder.toJsonValue(44);
    assertEquals("44", value.toString());
  }

  @Test
  public void convertsNegativeInt() {
    JsonValue value = JsonBuilder.toJsonValue(-10);
    assertEquals("-10", value.toString());
  }

  @Test
  public void convertsByte() {
    JsonValue value = JsonBuilder.toJsonValue((byte)12);
    assertEquals("12", value.toString());
  }

  @Test
  public void convertsShort() {
    JsonValue value = JsonBuilder.toJsonValue((short)23);
    assertEquals("23", value.toString());
  }

  @Test
  public void convertsLong() {
    JsonValue value = JsonBuilder.toJsonValue(12340L);
    assertEquals("12340", value.toString());
  }

  @Test
  public void convertsFloat() {
    JsonValue value = JsonBuilder.toJsonValue(3.141f);
    assertEquals("3.141", value.toString());
  }

  @Test
  public void convertsDouble() {
    JsonValue value = JsonBuilder.toJsonValue(1e5d);
    assertEquals("100000", value.toString());
  }

  @Test
  public void convertsChar() {
    JsonValue value = JsonBuilder.toJsonValue('%');
    assertEquals("\"%\"", value.toString());
  }

  @Test
  public void convertsCharArray() {
    JsonValue value = JsonBuilder.toJsonValue("bla".toCharArray());
    assertEquals("\"bla\"", value.toString());
  }

  @Test
  public void convertsString() {
    JsonValue value = JsonBuilder.toJsonValue("blubber\"Schnitzel\nmit\nPommes\"".toCharArray());
    assertEquals("\"blubber\\\"Schnitzel\\nmit\\nPommes\\\"\"", value.toString());
  }

  @Test
  public void convertsArbitraryObjectToString() {
    JsonValue value = JsonBuilder.toJsonValue(new Object() {
      @Override
      public String toString() {
        return "toString";
      }
    });
    assertEquals("\"toString\"", value.toString());
  }

  @Test
  public void convertsList() {
    List<Object> list = new ArrayList<Object>();
    list.add("banane");
    list.add(3.141);
    list.add(false);

    JsonValue value = JsonBuilder.toJsonValue(list);
    assertEquals("[\"banane\",3.141,false]", value.toString());
  }

  @Test
  public void convertsListWithNullEntries() {
    List<Object> list = new ArrayList<Object>();
    list.add("banane");
    list.add(null);
    list.add(null);

    JsonValue value = JsonBuilder.toJsonValue(list);
    assertEquals("[\"banane\",null,null]", value.toString());
  }

  @Test
  public void convertsNullList() {
    List<Object> list = null;
    JsonValue value = JsonBuilder.toJsonValue(list);
    assertEquals("null", value.toString());
  }

  @Test
  public void convertsEmptyList() {
    List<Object> list = new ArrayList<Object>();
    JsonValue value = JsonBuilder.toJsonValue(list);
    assertEquals("[]", value.toString());
  }

  @Test
  public void convertsSet() {
    Set<Object> set = new TreeSet<Object>();
    set.add("banane");
    set.add("23");
    set.add("banane");

    JsonValue value = JsonBuilder.toJsonValue(set);
    assertEquals("[\"23\",\"banane\"]", value.toString());
  }

  @Test
  public void convertsNullSet() {
    Set<Object> set = null;
    JsonValue value = JsonBuilder.toJsonValue(set);
    assertEquals("null", value.toString());
  }

  @Test
  public void convertsMap() {
    Map<Object, Object> map = new LinkedHashMap<Object, Object>();
    map.put("PI", 3.141);
    map.put(true, "wahr");
    map.put("bla", "blubb");
    JsonValue value = JsonBuilder.toJsonValue(map);
    assertEquals("{\"PI\":3.141,\"true\":\"wahr\",\"bla\":\"blubb\"}", value.toString());
  }

  @Test
  public void convertsNullMap() {
    Map<Object, Object> map = null;
    JsonValue value = JsonBuilder.toJsonValue(map);
    assertEquals("null", value.toString());
  }

  @Test
  public void convertsEmptyMap() {
    Map<Object, Object> map = new HashMap<Object, Object>();
    JsonValue value = JsonBuilder.toJsonValue(map);
    assertEquals("{}", value.toString());
  }

  @Test
  public void convertsMapSetCombination() {
    final Map<String, Set<String>> map = new LinkedHashMap<String, Set<String>>();
    final Set<String> set = new TreeSet<String>();
    set.add("A");
    set.add("B");
    set.add("C");
    map.put("ALL_TAGS", set);
    map.put("NO_TAG", null);

    JsonValue value = JsonBuilder.toJsonValue(map);
    assertEquals("{\"ALL_TAGS\":[\"A\",\"B\",\"C\"],\"NO_TAG\":null}", value.toString());
  }

  @Test
  public void convertsArrayOfStrings() {
    String[] array = new String[] {"A", "B", "C"};

    JsonValue value = JsonBuilder.toJsonValue(array);
    assertEquals("[\"A\",\"B\",\"C\"]", value.toString());
  }

  @Test
  public void convertsArrayOfObjects() {
    Object[] array = new Object[] {1, "B", 'c'};

    JsonValue value = JsonBuilder.toJsonValue(array);
    assertEquals("[1,\"B\",\"c\"]", value.toString());
  }

  @Test
  public void convertsMultiDimensionalArray() {
    Object[][] array = new Object[][] {new Object[] {1, 2, 3}, new Object[] {}};

    JsonValue value = JsonBuilder.toJsonValue(array);
    assertEquals("[[1,2,3],[]]", value.toString());
  }

  @Test
  public void convertsNullArray() {
    Object[] array = null;

    JsonValue value = JsonBuilder.toJsonValue(array);
    assertEquals("null", value.toString());
  }

  @Test
  public void convertsEmptyArray() {
    Object[] array = new Object[] {};

    JsonValue value = JsonBuilder.toJsonValue(array);
    assertEquals("[]", value.toString());
  }
}
