package org.json.token.terminal;

import org.json.token.Token;
import org.json.token.TokenType;

public class TokenBoolean extends Token {
    private final Boolean booleanValue;

    public TokenBoolean(Boolean booleanValue) {
        super(TokenType.BOOLEAN);
        this.booleanValue = booleanValue;
    }

    public Boolean getBoolean(){
        return booleanValue;
    }
}
