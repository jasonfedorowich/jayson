package org.json.error;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(char c) {
        super("Invalid token: " + c);
    }
    public InvalidTokenException(String s){
        super(s);
    }
}
