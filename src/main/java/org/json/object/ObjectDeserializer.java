package org.json.object;

import org.json.error.InvalidClassSerdException;
import org.json.parser.Parser;
import org.json.parser.token.Tokenizer;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ObjectDeserializer<T> {

    private final Object obj;
    private final JavaObject root;
    private final ObjectInstantiator objectInstantiator;

    ObjectDeserializer(String json, Class<T> type) {
        Tokenizer tokenizer = new Tokenizer(json);
        Parser parser = new Parser(tokenizer.scan());
        this.obj = parser.parse();
        ObjectMapper mapper = new ObjectMapper(type);
        this.root = mapper.getJavaObject();
        this.objectInstantiator = new ObjectInstantiator();
    }

    public T deserialize() {
        return (T) traverse(obj, root);

    }

    private Object traverse(Object obj, JavaObject root) {
        if (obj instanceof LinkedList ll) {
            ObjectMapper.POJO pojo = (ObjectMapper.POJO) root;
            Object newObject = objectInstantiator.newInstance(pojo.getType());
            Field[] fields = newObject.getClass().getDeclaredFields();
            Field arrayField = fields[0];

            JavaObject next = pojo.getJavaObjects().getFirst();
            if(next instanceof ObjectMapper.ArrayObject) {
                setField(arrayField, newObject,  traverseArray((LinkedList<Object>) obj, next));
            }else if(next instanceof ObjectMapper.ParamObject){
                setField(arrayField, newObject, traverseList(ll, next));
            }else{
                throw new InvalidClassSerdException("Invalid object");
            }

            return newObject;
        } else if (obj instanceof LinkedHashMap) {
            return traverseObject((LinkedHashMap<String, Object>)obj, root);
        } else {
            return null;
        }
    }

    private Object traverseIntegerArray(LinkedList<Object> obj) {
        int[] array = objectInstantiator.newIntArray(obj.size());
        for(int i = 0; i < obj.size(); i++){
            switch(obj.get(i)){
                case Long j:
                    array[i] = j.intValue();
                    break;
                default:
                    throw new InvalidClassSerdException("Invalid object");
            }
        }
        return array;
    }

    private Object traverseBooleanArray(LinkedList<Object> obj) {
        boolean[] array = objectInstantiator.newBooleanArray(obj.size());
        for(int i = 0; i < obj.size(); i++){
            switch(obj.get(i)){
                case Boolean j:
                    array[i] = j;
                    break;
                default:
                    throw new InvalidClassSerdException("Invalid object");
            }
        }
        return array;
    }

    private Object traverseDoubleArray(LinkedList<Object> obj) {
        double[] array = objectInstantiator.newDoubleArray(obj.size());
        for(int i = 0; i < obj.size(); i++){
            switch(obj.get(i)){
                case Double j:
                    array[i] = j;
                    break;
                default:
                    throw new InvalidClassSerdException("Invalid object");
            }
        }
        return array;
    }

    private Object traverseLongArray(LinkedList<Object> obj) {
        long[] array = objectInstantiator.newLongArray(obj.size());
        for(int i = 0; i < obj.size(); i++){
            switch(obj.get(i)){
                case Long j:
                    array[i] = j;
                    break;
                default:
                    throw new InvalidClassSerdException("Invalid object");
            }
        }
        return array;
    }

    private Object traverseList(LinkedList<Object> obj, JavaObject next) {
        ObjectMapper.ParamObject paramObject = (ObjectMapper.ParamObject) next;
        List<Object> list = (List<Object>) objectInstantiator.newInstance(paramObject.parameterizedType().getRawType());

        Consumer<Object> addAction = list::add;

        for(Object o : obj) {
            switch(paramObject.javaObject().getObjectType()){
                case LIST:
                    addList(addAction, o, paramObject.javaObject());
                    break;
                case POJO:
                    addObj(addAction, o, paramObject.javaObject());
                    break;
                case ARRAY:
                    addArr(addAction, o, paramObject.javaObject());
                    break;
                case TERMINAL:
                    addTerm(addAction, o, paramObject.javaObject());
                    break;
                default:
                    throw new InvalidClassSerdException("Invalid object");

            }
        }
        return list;
    }



    private Object traverseArray(LinkedList<Object> obj, JavaObject javaObject) {
        ObjectMapper.ArrayObject arrayObject = (ObjectMapper.ArrayObject) javaObject;

        if(arrayObject.javaObject().isPrimitive()){
            ObjectMapper.TerminalObject terminalObject = (ObjectMapper.TerminalObject) arrayObject.javaObject();

            return switch (terminalObject.getTerminalType()){
                case BOOLEAN -> traverseBooleanArray(obj);
                case DOUBLE -> traverseDoubleArray(obj);
                case INTEGER -> traverseIntegerArray(obj);
                case LONG -> traverseLongArray(obj);
                default -> throw new InvalidClassSerdException("Invalid object expected primitive type");
            };
        }
        Object[] array = (Object[]) objectInstantiator.newArray(arrayObject.componentType(), obj.size());

        AtomicInteger index = new AtomicInteger(0);

        Consumer<Object> addAction = o -> {
            array[index.getAndIncrement()] = o;
        };

        for(Object o : obj) {
            switch(arrayObject.javaObject().getObjectType()){
                case LIST:
                    addList(addAction, o, arrayObject.javaObject());
                    break;
                case ARRAY:
                    addArr(addAction, o, arrayObject.javaObject());
                    break;
                case POJO:
                    addObj(addAction, o, arrayObject.javaObject());
                    break;
                case TERMINAL:
                    addTerm(addAction, o, arrayObject.javaObject());
                    break;
            }
        }

        return array;
    }

    private Object traverseObject(LinkedHashMap<String, Object> obj, JavaObject root) {
        ObjectMapper.POJO pojo = (ObjectMapper.POJO) root;
        Object newObject = objectInstantiator.newInstance(pojo.getType());

        if(pojo.getJavaObjects().size() != obj.size())
            throw new InvalidClassSerdException("Invalid object must match size");


        List<JavaObject> objects = new LinkedList<>(pojo.getJavaObjects());
        LinkedList<Field> fields = Stream.of(pojo.getFields()).collect(LinkedList::new, List::add,
                LinkedList::addAll);


        for(Map.Entry<String, Object> entry : obj.entrySet()){
            JavaObject next = objects.removeFirst();
            Field nextField = fields.removeFirst();

            switch(next.getObjectType()){
                case POJO:
                    setObjField(entry, next, newObject, nextField);
                    break;
                case LIST:
                    setListField(entry, next, newObject, nextField);
                    break;
                case ARRAY:
                    setArrayField(entry, next, newObject, nextField);
                    break;
                case TERMINAL:
                    setTerminalField(entry, next, newObject, nextField);
                    break;
                default:
                    throw new InvalidClassSerdException("Invalid object");
            }
        }

        return newObject;

    }

    private void addTerm(Consumer<Object> consumer, Object o, JavaObject next) {
        switch (o) {
            case Double v -> {
                assertDoubleObject(next);
                consumer.accept(o);
            }
            case Long object -> {
                if (next instanceof ObjectMapper.LongObject) {
                    consumer.accept(o);
                } else if (next instanceof ObjectMapper.IntegerObject) {
                    Integer integer = object.intValue();
                    consumer.accept(integer);
                } else {
                    throw new InvalidClassSerdException("Invalid object expected a number");
                }
            }
            case String s -> {
                assertStringObject(next);
                consumer.accept(o);
            }
            case Boolean b -> {
                assertBooleanObject(next);
                consumer.accept(o);
            }
            case null, default -> throw new InvalidClassSerdException("Invalid object expected a terminal value");
        }
    }


    private void addArr(Consumer<Object> consumer, Object o, JavaObject javaObject) {
        if(o == null){
            consumer.accept(null);
            return;
        }
        assertLinkedList(o);
        consumer.accept(traverseArray((LinkedList<Object>) o, javaObject));
    }


    private void addObj(Consumer<Object> consumer, Object o, JavaObject javaObject) {
        if(o == null){
            consumer.accept(null);
            return;
        }
        assertLinkedHashMap(o);
        consumer.accept(traverseObject((LinkedHashMap<String, Object>)o, javaObject));
    }

    private void addList(Consumer<Object> consumer, Object o, JavaObject javaObject) {
        if(o == null){
            consumer.accept(null);
            return;
        }
        assertLinkedList(o);
        consumer.accept(traverseList((LinkedList<Object>) o, javaObject));
    }

    private void setTerminalField(Map.Entry<String, Object> entry, JavaObject next, Object newObject, Field nextField) {
        if(!nextField.getName().equals(entry.getKey())) throw new InvalidClassSerdException("Invalid field name");
        switch (entry.getValue()) {
            case Double v -> {
                assertDoubleObject(next);
                setField(nextField, newObject, entry.getValue());
            }
            case Long object -> {
                if (next instanceof ObjectMapper.LongObject) {
                    setField(nextField, newObject, object);
                } else if (next instanceof ObjectMapper.IntegerObject) {
                    Integer integer = object.intValue();
                    setField(nextField, newObject, integer);
                }else{
                    throw new InvalidClassSerdException("Invalid object expected a number");
                }
            }
            case String s -> {
                assertStringObject(next);
                setField(nextField, newObject, entry.getValue());
            }
            case Boolean b -> {
                assertBooleanObject(next);
                setField(nextField, newObject, entry.getValue());
            }
            case null, default -> throw new InvalidClassSerdException("Invalid object expected a terminal value");
        }
    }

    private void setArrayField(Map.Entry<String, Object> entry, JavaObject next, Object newObject, Field nextField) {
        if(entry.getValue() == null){
            setField(nextField, newObject, null);
            return;
        }
        assertLinkedList(entry.getValue(), nextField.getName(), entry.getKey());
        setField(nextField, newObject, traverseArray((LinkedList<Object>) entry.getValue(), next));
    }

    private void setListField(Map.Entry<String, Object> entry, JavaObject next, Object newObject, Field nextField) {
        if(entry.getValue() == null){
            setField(nextField, newObject, null);
            return;
        }
        assertLinkedList(entry.getValue(), nextField.getName(), entry.getKey());
        setField(nextField, newObject, traverseList((LinkedList<Object>) entry.getValue(), next));
    }

    private void setObjField(Map.Entry<String, Object> entry, JavaObject next, Object newObject, Field nextField) {
        if(entry.getValue() == null){
            setField(nextField, newObject, null);
            return;
        }
        assertLinkedHashMap(entry.getValue(), nextField.getName(), entry.getKey());
        setField(nextField, newObject, traverseObject((LinkedHashMap<String, Object>) entry.getValue(), next));
    }

    private static void setField(Field field, Object newObject, Object value){
        try {
            field.setAccessible(true);
            field.set(newObject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertName(String expected, String actual){
        if(!expected.equals(actual)){
            throw new InvalidClassSerdException("Invalid object name expected " + expected + " but " + actual);
        }
    }

    private static void assertLinkedHashMap(Object obj){
        if(!(obj instanceof LinkedHashMap)){
            throw new InvalidClassSerdException("Invalid object expected object of type LinkedHashMap");
        }
    }

    private static void assertLinkedHashMap(Object obj, String expected, String actual){
        assertLinkedHashMap(obj);
        assertName(expected, actual);
    }

    private static void assertLinkedList(Object obj){
        if(!(obj instanceof LinkedList)){
            throw new InvalidClassSerdException("Invalid object expected object of type LinkedHashMap");
        }
    }

    private static void assertLinkedList(Object obj, String expected, String actual){
        assertLinkedList(obj);
        assertName(expected, actual);
    }

    private static void assertDoubleObject(Object obj){
        if(!(obj instanceof ObjectMapper.DoubleObject)){
            throw new InvalidClassSerdException("Invalid object expected a number");
        }
    }

    private static void assertStringObject(Object obj){
        if(!(obj instanceof ObjectMapper.StringObject)){
            throw new InvalidClassSerdException("Invalid object expected a string");
        }
    }

    private static void assertBooleanObject(Object obj){
        if(!(obj instanceof ObjectMapper.BooleanObject)){
            throw new InvalidClassSerdException("Invalid object expected a boolean");
        }
    }

}
