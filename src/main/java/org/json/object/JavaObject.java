package org.json.object;

interface JavaObject {

    enum Type{
        POJO,
        ARRAY,
        LIST,
        STRING, INTEGER, BOOLEAN, DOUBLE, LONG, TERMINAL
    }

    Type getObjectType();
}
