package org.json.token.terminal;

import org.json.token.Token;
import org.json.token.TokenType;

public class TokenNumber extends Token {
    private final String string;

    public TokenNumber(String string, TokenType tokenType) {
        super(tokenType);
        this.string = string;
    }

    public String getString(){
        return string;
    }
}
