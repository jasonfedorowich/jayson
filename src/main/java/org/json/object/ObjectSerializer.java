package org.json.object;

import org.json.error.InvalidClassSerdException;
import org.json.format.Formatter;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

public class ObjectSerializer {

    private final Object src;
    private final JavaObject root;
    private final Formatter formatter;

    ObjectSerializer(Object src, Formatter formatter) {
        this.src = src;
        ObjectMapper mapper = new ObjectMapper(src.getClass());
        this.root = mapper.getJavaObject();
        this.formatter = formatter;
    }

    public String serialize() {
        return switch (root.getObjectType()){
            case POJO -> traverse(src, (ObjectMapper.POJO)root);
            default -> throw new InvalidClassSerdException("Cannot convert object of type " + root.getObjectType());
        };
    }

    private String traverse(Object src, ObjectMapper.POJO pojo) {
        formatter.nextObject();
        if(src == null) {
            return "null";
        }
        StringJoiner javaObject = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatObjectBegin("{"),
                formatter.formatObjectEnd("}"));

        List<JavaObject> objects = new LinkedList<>(pojo.getJavaObjects());
        LinkedList<Field> fields = Stream.of(pojo.getFields()).collect(LinkedList::new, List::add,
                LinkedList::addAll);

        for(JavaObject obj : objects){
            Field field = fields.removeFirst();
            StringJoiner fieldJoiner = new StringJoiner(formatter.formatColon(":"));
            fieldJoiner.add(addName(field));
            switch (obj.getObjectType()){
                case POJO -> fieldJoiner.add(traverse(getObject(field, src), (ObjectMapper.POJO)obj));
                case LIST -> fieldJoiner.add(traverseList(getObject(field, src), (ObjectMapper.EmbeddedJavaObject) obj));
                case ARRAY -> fieldJoiner.add(traverseArray(getObject(field, src), (ObjectMapper.EmbeddedJavaObject) obj));
                case TERMINAL -> fieldJoiner.add(traverseTerm(getObject(field, src), (ObjectMapper.TerminalObject)obj));
            }

            javaObject.add(formatter.formatObjectMember(fieldJoiner.toString()));
        }
        formatter.finishObject();
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
        formatter.nextObject();
        if(object == null) {
            return "null";
        }
        StringJoiner array = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatArrayBegin("["),
                formatter.formatArrayEnd("]"));

        List<Object> objects = (List<Object>) object;
        for(Object o : objects){
            switch(obj.javaObject().getObjectType()){
                case POJO -> {
                    array.add(
                            formatter.formatArrayMember(traverse(o, (ObjectMapper.POJO) obj.javaObject())));
                }
                case LIST -> {
                    array.add(
                            formatter.formatArrayMember(traverseList(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject())));
                }
                case ARRAY -> {
                    array.add(
                            formatter.formatArrayMember(traverseArray(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject())));
                }
                case TERMINAL -> {
                    array.add(
                            formatter.formatArrayMember(traverseTerm(o, (ObjectMapper.TerminalObject)obj.javaObject())));
                }
            }
        }
        formatter.finishObject();
        return array.toString();

    }

    private String traverseArray(Object object, ObjectMapper.EmbeddedJavaObject obj) {
        if(object == null) {
            return "null";
        }


        if(obj.javaObject().isPrimitive()){
            ObjectMapper.TerminalObject terminalObject= (ObjectMapper.TerminalObject)obj.javaObject();
            return switch(terminalObject.getTerminalType()){
                case INTEGER -> traverseIntArray(object);
                case LONG -> traverseLongArray(object);
                case DOUBLE -> traverseDoubleArray(object);
                case BOOLEAN -> traverseBooleanArray(object);
                default -> throw new InvalidClassSerdException("Cannot serialize object of type " + obj.javaObject().getObjectType());
            };
        }

        formatter.nextObject();


        StringJoiner array = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatArrayBegin("["),
                formatter.formatArrayEnd("]"));

        Object[] objects = (Object[]) object;
        for(Object o : objects){
            switch(obj.javaObject().getObjectType()){
                case POJO -> array.add(
                        formatter.formatArrayMember(traverse(o, (ObjectMapper.POJO) obj.javaObject())));
                case LIST -> array.add(
                        formatter.formatArrayMember(traverseList(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject())));
                case ARRAY -> array.add(
                        formatter.formatArrayMember(traverseArray(o, (ObjectMapper.EmbeddedJavaObject) obj.javaObject())));
                case TERMINAL -> array.add(
                        formatter.formatArrayMember(traverseTerm(o, (ObjectMapper.TerminalObject)obj.javaObject())));
            }
        }
        formatter.finishObject();

        return array.toString();
    }

    private String traverseBooleanArray(Object object) {
        formatter.nextObject();
        StringJoiner array = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatArrayBegin("["),
                formatter.formatArrayEnd("]"));

        boolean[] objects = (boolean[]) object;
        for (boolean i : objects) {
            array.add(
                    formatter.formatArrayMember(String.valueOf(i)
                    ));
        }
        formatter.finishObject();

        return array.toString();
    }

    private String traverseDoubleArray(Object object) {
        formatter.nextObject();

        StringJoiner array = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatArrayBegin("["),
                formatter.formatArrayEnd("]"));

        double[] objects = (double[]) object;
        for (double i : objects) {
            array.add(
                    formatter.formatArrayMember(String.valueOf(i)));
        }
        formatter.finishObject();
        return array.toString();
    }

    private String traverseLongArray(Object object) {
        formatter.nextObject();
        StringJoiner array = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatArrayBegin("["),
                formatter.formatArrayEnd("]"));

        long[] objects = (long[]) object;
        for(long i : objects){
            array.add(
                    formatter.formatArrayMember(String.valueOf(i)));
        }
        formatter.finishObject();
        return array.toString();
    }

    private String traverseIntArray(Object object) {
        formatter.nextObject();
        StringJoiner array = new StringJoiner(
                formatter.formatComma(","),
                formatter.formatArrayBegin("["),
                formatter.formatArrayEnd("]"));

        int[] objects = (int[]) object;
        for(int i : objects){
            array.add(
                    formatter.formatArrayMember(String.valueOf(i)));
        }
        formatter.finishObject();
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
