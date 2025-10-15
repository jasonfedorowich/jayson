package org.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class JaysonTest {

    public static class NestedPojo{
        Long v;
        Boolean t;

    }

    public static class NestedPojo2{
        Double v;
    }

    public static class Pojo{
        String name;
        String stuff;
        NestedPojo nested;

        List<NestedPojo2> list;

        String[] arr;

        Integer i;
    }

    public static class ArrayPojo{
        List<String> values;
    }

    public static class PrimitivesPojo {
        int x;
        double y;
        boolean bool;
        long l1;

        int[] ints;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            PrimitivesPojo that = (PrimitivesPojo) o;
            return x == that.x && Double.compare(y, that.y) == 0 && bool == that.bool && l1 == that.l1 && Objects.deepEquals(ints, that.ints);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, bool, l1, Arrays.hashCode(ints));
        }
    }


    @Test
    void testJaysonDeserializationThenSerialization() {
        String json = "{\n" +
                "    \"name\": \"jason\",\n" +
                "    \"stuff\": \"test\",\n" +
                "    \"nested\": {\n" +
                "        \"v\": 1,\n" +
                "        \"t\": true\n" +
                "    },\n" +
                "    \"list\": [{\"v\": 1.0}, {\"v\": 2.0}],\n" +
                "    \"arr\": [\"hello\", \"world\"],\n" +
                "    \"i\": 100\n" +
                "}";
        Pojo pojo = Jayson.fromString(json, Pojo.class);
        Assertions.assertInstanceOf(Pojo.class, pojo);
        String newJson = Jayson.toString(pojo);
        Assertions.assertEquals(json.replaceAll("\\s+", ""), newJson.replaceAll("\\s+", ""));
    }

    @Test
    void testJaysonDeserializationWithNullValuesThenSerialization() {
        String json = "{\n" +
                "    \"name\": \"jason\",\n" +
                "    \"stuff\": \"test\",\n" +
                "    \"nested\": null,\n" +
                "    \"list\": [{\"v\": 1.0}, {\"v\": 2.0}],\n" +
                "    \"arr\": [\"hello\", \"world\"],\n" +
                "    \"i\": 100\n" +
                "}";
        Pojo pojo = Jayson.fromString(json, Pojo.class);
        Assertions.assertInstanceOf(Pojo.class, pojo);

        String newJson = Jayson.toString(pojo);
        Assertions.assertEquals(json.replaceAll("\\s+", ""), newJson.replaceAll("\\s+", ""));

    }

    @Test
    void testJaysonDeserWithListThenSerialization() {
        String json = "[\"hello\", \"world\"]";
        ArrayPojo pojo = Jayson.fromString(json, ArrayPojo.class);
        Assertions.assertInstanceOf(ArrayPojo.class, pojo);

        String newJson = Jayson.toString(pojo);
        assertFalse(newJson.isEmpty());
    }

    @Test
    void testJaysonDeserWithPrimitives(){
        String json = "{\"x\": 1, \"y\": 2.0, \"bool\": true, \"l1\": 100, \"ints\": [1, 2, 3]}";
        PrimitivesPojo pojo = Jayson.fromString(json, PrimitivesPojo.class);
        Assertions.assertInstanceOf(PrimitivesPojo.class, pojo);

        String newJson = Jayson.toString(pojo);
        PrimitivesPojo pojo2 = Jayson.fromString(newJson, PrimitivesPojo.class);
        Assertions.assertEquals(pojo, pojo2);

    }
}