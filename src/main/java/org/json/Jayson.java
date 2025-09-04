package org.json;

import org.json.error.InvalidClassSerdException;
import org.json.object.*;
import org.json.parser.Parser;
import org.json.parser.token.Tokenizer;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Jayson {

    //todo handle primitives
    //todo use the class template to find fields not matching exactly
    //todo support primitives?

    public static <T> T fromString(String json, Class<T> type) {
        ObjectDeserializer<T> deserializer = ObjectMapper.getDeserializer(json, type);
        return deserializer.deserialize();
    }

    public static String toString(Object src){
        ObjectSerializer serializer = ObjectMapper.getSerializer(src);
        return serializer.serialize();
    }
    

}
