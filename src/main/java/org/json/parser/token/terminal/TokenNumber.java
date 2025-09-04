package org.json.parser.token.terminal;

import org.json.parser.token.Token;
import org.json.parser.token.TokenType;

public abstract class TokenNumber extends Token {

    public TokenNumber(TokenType tokenType) {
        super(tokenType);
    }

    public abstract Number getNumber();


    public static class TokenDouble extends TokenNumber{

        private final Double number;

        public TokenDouble(Double number) {
            super(TokenType.NUMBER);
            this.number = number;
        }

        public Double getNumber() {
            return number;
        }
    }

    public static class TokenLong extends TokenNumber{

        private final Long number;

        public TokenLong(Long number) {
            super(TokenType.NUMBER);
            this.number = number;
        }

        public Long getNumber() {
            return number;
        }
    }
}
