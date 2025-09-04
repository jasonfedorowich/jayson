package org.json.object;

import org.json.error.InvalidClassSerdException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class ObjectSerializer {

    private final Object src;
    private final JavaObject root;

    ObjectSerializer(Object src) {
        this.src = src;
        ObjectMapper mapper = new ObjectMapper(src.getClass());
        this.root = mapper.getJavaObject();
    }

    public String serialize() {
        return switch (root.getObjectType()){
            case POJO -> traverse(src, (ObjectMapper.POJO)root);
            default -> throw new InvalidClassSerdException("Cannot convert object of type " + root.getObjectType());
        };
    }

    private String traverse(Object src, ObjectMapper.POJO pojo) {
        if(src == null) {
            return "null";
        }
        StringJoiner javaObject = new StringJoiner(", ", "{", "}");

        List<JavaObject> objects = new LinkedList<>(pojo.getJavaObjects());
        LinkedList<Field> fields = Stream.of(pojo.getFields()).collect(LinkedList::new, List::add,
                LinkedList::addAll);

        for(JavaObject obj : objects){
            Field field = fields.removeFirst();
            StringJoiner fieldJoiner = new StringJoiner(":");
            fieldJoiner.add(addName(field));
            switch (obj.getObjectType()){
                case POJO -> fieldJoiner.add(traverse(getObject(field, src), (ObjectMapper.POJO)obj));
                case LIST -> fieldJoiner.add(traverseList(getObject(field, src), (ObjectMapper.EmbeddedJavaObject) obj));
                case ARRAY -> fieldJoiner.add(traverseArray(getObject(field, src), (ObjectMapper.EmbeddedJavaObject) obj));
                case TERMINAL -> fieldJoiner.add(traverseTerm(getObject(field, src), (ObjectMapper.TerminalObject)obj));
            }

            javaObject.add(fieldJoiner.toString());
        }

        return javaObject.toString();
    }

    private String traverseTerm(Object object, ObjectMapper.TerminalObject obj) {
        if(object == null) {
            return "null";
        }
        return switch(obj.getTerminalType()){
            case STRING -> String.format("\"%s\"",  object.toString());
            default -> object.toString();
        };
    }

    private String traverseList(Object object, ObjectMapper.EmbeddedJavaObject obj) {
        if(object == null) {
            return "null";
        }
        StringJoiner array = new StringJoiner(", ", "[", "]");

        List<Object> objects = (List<Object>) object;
        for(Object o : objects){
            switch(obj.javaObject().getObjectType()){
                case POJO -> array.add(traverse(o, (ObjectMapper.POJO) obj.javaObject()));
                case LIST -> array.add(traverseList(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject()));
                case ARRAY -> array.add(traverseArray(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject()));
                case TERMINAL -> array.add(traverseTerm(o, (ObjectMapper.TerminalObject)obj.javaObject()));
            }
        }

        return array.toString();

    }

    private String traverseArray(Object object, ObjectMapper.EmbeddedJavaObject obj) {
        if(object == null) {
            return "null";
        }
        StringJoiner array = new StringJoiner(", ", "[", "]");

        Object[] objects = (Object[]) object;
        for(Object o : objects){
            switch(obj.javaObject().getObjectType()){
                case POJO -> array.add(traverse(o, (ObjectMapper.POJO) obj.javaObject()));
                case LIST -> array.add(traverseList(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject()));
                case ARRAY -> array.add(traverseArray(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject()));
                case TERMINAL -> array.add(traverseTerm(o, (ObjectMapper.TerminalObject)obj.javaObject()));
            }
        }

        return array.toString();
    }

    private String addName(Field field) {
        return String.format("\"%s\"",  field.getName());
    }

    private Object getObject(Field field, Object src) {
        try {
            field.setAccessible(true);
            return field.get(src);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
