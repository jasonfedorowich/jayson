package org.json.parser.token.terminal;

import org.json.parser.token.Token;
import org.json.parser.token.TokenType;

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
