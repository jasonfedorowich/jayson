package org.json;

import org.json.format.BeautifyFormatter;
import org.json.format.DefaultFormatter;
import org.json.format.Formatter;
import org.json.object.*;

public class Jayson {

    //todo use the class template to find fields not matching exactly
    //todo support search language jsonpath

    public static <T> T fromString(String json, Class<T> type) {
        ObjectDeserializer<T> deserializer = ObjectMapper.getDeserializer(json, type);
        return deserializer.deserialize();
    }

    public static String toString(Object src){
        return toString(src, new DefaultFormatter());
    }

    public static String toString(Object src, Formatter formatter){
        ObjectSerializer serializer = ObjectMapper.getSerializer(src, formatter);
        return serializer.serialize();
    }
    

}
