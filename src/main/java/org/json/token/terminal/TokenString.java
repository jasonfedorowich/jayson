package org.json.token;

public class TokenString extends Token {
    private final String string;

    public TokenString(String string, TokenType tokenType) {
        super(tokenType);
        this.string = string;
    }

    public String getString(){
        return string;
    }
}
