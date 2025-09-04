package org.json.parser.token;

public enum TokenType {

    OPEN_CURLY_BRACE('{'),
    CLOSED_CURLY_BRACE('}'),

    OPEN_SQUARE_BRACE('['),
    CLOSED_SQUARE_BRACE(']'),

    COLON(':'),
    COMMA(','),

    QUOTE('"'),

    STRING('s'),

    NUMBER('n'),

    BOOLEAN('b'),

    NULL('n');

    final char c;
    TokenType(char c){
        this.c = c;
    }

}
