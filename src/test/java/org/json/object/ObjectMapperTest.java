package org.json.object;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperTest {

    static class SimpleClass {
        String string;
    }

    static class SimpleClass1 {
        String string;
        Long long1;
        SimpleClass simpleClass1;
    }

    static class SimpleClass2 {
        String string;
        List<SimpleClass> simpleClassList;
    }

    static class SimpleClass3 {
        List<List<SimpleClass>> simpleClassList;
    }

    static class SimpleClass4 {
        String[] strings;
        String[][] strings2;
    }

    @Test
    public void testObjectClassMapping() {
        ObjectMapper objectMapper = new ObjectMapper(SimpleClass1.class);
        JavaObject javaObject = objectMapper.getJavaObject();
        assertInstanceOf(ObjectMapper.POJO.class, javaObject);
        ObjectMapper.POJO pojo1 = (ObjectMapper.POJO) javaObject;
        assertEquals(3, pojo1.getJavaObjects().size());
        assertInstanceOf(ObjectMapper.POJO.class, pojo1.getJavaObjects().get(2));

    }

    @Test
    public void testObjectClassMappingWithGeneric() {
        ObjectMapper objectMapper = new ObjectMapper(SimpleClass2.class);
        JavaObject javaObject = objectMapper.getJavaObject();
        assertInstanceOf(ObjectMapper.POJO.class, javaObject);
        ObjectMapper.POJO pojo1 = (ObjectMapper.POJO) javaObject;
        assertEquals(2, pojo1.getJavaObjects().size());
        assertInstanceOf(ObjectMapper.ParamObject.class, pojo1.getJavaObjects().get(1));
        ObjectMapper.ParamObject paramObject = (ObjectMapper.ParamObject) pojo1.getJavaObjects().get(1);
        assertInstanceOf(ObjectMapper.POJO.class, paramObject.javaObject());
    }

    @Test
    public void testObjectClassMappingWithGenericNesting() {
        ObjectMapper objectMapper = new ObjectMapper(SimpleClass3.class);
        JavaObject javaObject = objectMapper.getJavaObject();
        assertInstanceOf(ObjectMapper.POJO.class, javaObject);
        ObjectMapper.POJO pojo1 = (ObjectMapper.POJO) javaObject;
        assertEquals(1, pojo1.getJavaObjects().size());
        assertInstanceOf(ObjectMapper.ParamObject.class, pojo1.getJavaObjects().get(0));
        ObjectMapper.ParamObject paramObject = (ObjectMapper.ParamObject) pojo1.getJavaObjects().get(0);
        assertInstanceOf(ObjectMapper.ParamObject.class, paramObject.javaObject());
        ObjectMapper.ParamObject paramObject1 = (ObjectMapper.ParamObject) paramObject.javaObject();
        assertInstanceOf(ObjectMapper.POJO.class, paramObject1.javaObject());

    }

    @Test
    public void testObjectClassMappingWithArrays() {
        ObjectMapper objectMapper = new ObjectMapper(SimpleClass4.class);
        JavaObject javaObject = objectMapper.getJavaObject();
        assertInstanceOf(ObjectMapper.POJO.class, javaObject);
        ObjectMapper.POJO pojo1 = (ObjectMapper.POJO) javaObject;
        assertEquals(2, pojo1.getJavaObjects().size());
        assertInstanceOf(ObjectMapper.ArrayObject.class, pojo1.getJavaObjects().get(1));
        ObjectMapper.ArrayObject arrayObject = (ObjectMapper.ArrayObject) pojo1.getJavaObjects().get(1);
        assertInstanceOf(ObjectMapper.ArrayObject.class, arrayObject.javaObject());
        ObjectMapper.ArrayObject arrayObject1 = (ObjectMapper.ArrayObject) arrayObject.javaObject();
        assertInstanceOf(ObjectMapper.TerminalObject.class, arrayObject1.javaObject());

    }
}