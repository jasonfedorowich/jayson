package org.json.token.terminal;

import org.json.token.Token;
import org.json.token.TokenType;

public class TokenString extends Token {
    private final String string;

    public TokenString(String string) {
        super(TokenType.STRING);
        this.string = string;
    }

    public String getString(){
        return string;
    }
}
