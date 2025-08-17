package org.json.error;

public class InvalidJsonException extends RuntimeException {
    public InvalidJsonException(String s) {
        super(s);
    }
}
