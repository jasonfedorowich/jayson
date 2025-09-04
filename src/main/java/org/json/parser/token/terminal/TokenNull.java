package org.json.parser.token.terminal;

import org.json.parser.token.Token;
import org.json.parser.token.TokenType;

public class TokenNull extends Token {
    public TokenNull() {
        super(TokenType.NULL);
    }
}
