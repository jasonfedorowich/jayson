package org.json.error;

public class InvalidObjectMapperException extends RuntimeException {
    public InvalidObjectMapperException(Exception e) {
        super(e);
    }
}
