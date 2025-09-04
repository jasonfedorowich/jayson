package org.json.parser.token.terminal;

import org.json.parser.token.Token;
import org.json.parser.token.TokenType;

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
