package org.json.object;

import org.json.error.InvalidObjectMapperException;
import org.json.format.Formatter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ObjectMapper {


    private static final Map<Class<?>, JavaObject> JAVA_TYPES = new HashMap<Class<?>, JavaObject>();
    static {
        JAVA_TYPES.put(String.class, new StringObject(String.class));
        JAVA_TYPES.put(Integer.class, new IntegerObject(Integer.class));
        JAVA_TYPES.put(Boolean.class, new BooleanObject(Boolean.class));
        JAVA_TYPES.put(Double.class, new DoubleObject(Double.class));
        JAVA_TYPES.put(Long.class, new LongObject(Long.class));
    }

    private static final Map<String, JavaObject> PRIMITIVES = new HashMap<>();
    static {
        PRIMITIVES.put("int", new PrimitiveIntegerObject(Integer.class));
        PRIMITIVES.put("boolean", new PrimitiveBooleanObject(Boolean.class));
        PRIMITIVES.put("double", new PrimitiveDoubleObject(Double.class));
        PRIMITIVES.put("long", new PrimitiveLongObject(Long.class));
    }

    private final JavaObject root;


    ObjectMapper(Class<?> type) {
        try {
            root = reflect(type);
        } catch (NoSuchFieldException e) {
            throw new InvalidObjectMapperException(e);
        }
    }



    public JavaObject getJavaObject(){
        return root;
    }

    private JavaObject reflect(Class<?> type) throws NoSuchFieldException {
        if(JAVA_TYPES.containsKey(type)){
            return JAVA_TYPES.get(type);
        }else if(PRIMITIVES.containsKey(type.getName())){
            return PRIMITIVES.get(type.getName());
        }

        Field[] fields = type.getDeclaredFields();
        POJO javaClass = new POJO(type, fields);

        for (Field field : fields) {
            //todo need to check for array type
            Type fieldType = field.getGenericType();
            if(fieldType instanceof ParameterizedType parameterizedType){
                javaClass.addObject(reflectParameterizedType(parameterizedType));
            }else if(fieldType instanceof Class<?> f){
                if(f.isArray()){
                    javaClass.addObject(reflectArray(f));
                }else{
                    javaClass.addObject(reflect(f));
                }

            }
        }
        JAVA_TYPES.put(type, javaClass);
        return javaClass;
    }

    private JavaObject reflectArray(Class<?> f) throws NoSuchFieldException {
        Class<?> arrayType = f.getComponentType();

        if(arrayType.isArray()){
            return new ArrayObject(arrayType, reflectArray(arrayType));
        }else{
            JavaObject javaObject = reflect(arrayType);
            if(javaObject instanceof TerminalObject to){
           //     return new ArrayObject(to.getType(), javaObject);
            }
            return new ArrayObject(arrayType, reflect(arrayType));
        }
    }

    private JavaObject reflectParameterizedType(ParameterizedType parameterizedType) throws NoSuchFieldException {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if(typeArguments[0] instanceof ParameterizedType){
           return new ParamObject(parameterizedType, reflectParameterizedType((ParameterizedType) typeArguments[0]));
        }else if(typeArguments[0] instanceof Class){
            return new ParamObject(parameterizedType, reflect((Class<?>) typeArguments[0]));
        }else{
            return null;
        }
    }


    static class POJO implements JavaObject {

        private final Class<?> clazz;
        private final Field[] fields;
        private final List<JavaObject> javaObjects;

        public POJO(Class<?> clazz, Field[] fields){
            this.clazz = clazz;
            this.fields = fields;
            this.javaObjects = new ArrayList<>();
        }

        private void addObject(JavaObject javaObject){
            javaObjects.add(javaObject);
        }

        public Class<?> getType() {
            return clazz;
        }

        public Field[] getFields() {
            return fields;
        }

        public List<JavaObject> getJavaObjects() {
            return javaObjects;
        }

        @Override
        public Type getObjectType() {
            return Type.POJO;
        }
    }

    static abstract class EmbeddedJavaObject implements JavaObject {
        protected final JavaObject javaObject;

        EmbeddedJavaObject(JavaObject javaObject){
            this.javaObject = javaObject;
        }

        public JavaObject javaObject(){
            return javaObject;
        }
    }

    static class ArrayObject extends EmbeddedJavaObject {

        private final Class<?> componentType;

        ArrayObject(Class<?> componentType, JavaObject javaObject) {
            super(javaObject);
            this.componentType = componentType;
        }

        @Override
        public Type getObjectType() {
            return Type.ARRAY;
        }

        public Class<?> componentType() {
            return componentType;
        }
    }


    static class ParamObject extends EmbeddedJavaObject {

        private final ParameterizedType parameterizedType;

        ParamObject(ParameterizedType parameterizedType, JavaObject javaObject) {
            super(javaObject);
            this.parameterizedType = parameterizedType;
        }

        @Override
        public Type getObjectType() {
            return Type.LIST;
        }

        public ParameterizedType parameterizedType() {
            return parameterizedType;
        }
    }

    static abstract class TerminalObject implements JavaObject {

        private final Class<?> type;

        public TerminalObject(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }

        @Override
        public Type getObjectType() {
            return Type.TERMINAL;
        }

        public abstract Type getTerminalType();
    }

    static class StringObject extends TerminalObject {

        public StringObject(Class<?> type) {
            super(type);
        }

        @Override
        public Type getTerminalType() {
            return Type.STRING;
        }
    }

    static class IntegerObject extends TerminalObject {
        public IntegerObject(Class<?> type) {
            super(type);
        }

        @Override
        public Type getTerminalType() {
            return Type.INTEGER;
        }

    }

    static class PrimitiveIntegerObject extends IntegerObject{

        public PrimitiveIntegerObject(Class<?> type) {
            super(type);
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    }

    static class BooleanObject extends TerminalObject {
        public BooleanObject(Class<?> type) {
            super(type);
        }

        @Override
        public Type getTerminalType() {
            return Type.BOOLEAN;
        }
    }

    static class PrimitiveBooleanObject extends BooleanObject{
        public PrimitiveBooleanObject(Class<?> type){
            super(type);
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }

    }
    static class DoubleObject extends TerminalObject {
        public DoubleObject(Class<?> type) {
            super(type);
        }

        @Override
        public Type getTerminalType() {
            return Type.DOUBLE;
        }
    }

    static class PrimitiveDoubleObject extends DoubleObject{

        public PrimitiveDoubleObject(Class<?> type) {
            super(type);
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    }
    static class LongObject extends TerminalObject {
        public LongObject(Class<?> type) {
            super(type);
        }

        @Override
        public Type getTerminalType() {
            return Type.LONG;
        }
    }

    static class PrimitiveLongObject extends LongObject{
        public PrimitiveLongObject(Class<?> type) {
            super(type);
        }
        @Override
        public boolean isPrimitive() {
            return true;
        }

    }

    public static <T> ObjectDeserializer<T> getDeserializer(String json, Class<T> type) {
        return new ObjectDeserializer<T>(json, type);
    }

    public static ObjectSerializer getSerializer(Object src, Formatter formatter) {
        return new ObjectSerializer(src, formatter);
    }
}
