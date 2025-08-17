package org.json.token;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(char c) {
        super("Invalid token: " + c);
    }
}
