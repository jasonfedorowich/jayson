package org.json.parser.token;

import org.json.error.InvalidTokenException;
import org.json.parser.token.terminal.TokenNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    public void testTokenizeEmptyJson(){
        String s = "{}";
        Tokenizer tokenizer = new Tokenizer(s);
        List<Token> tokens =  tokenizer.scan();
        Assertions.assertEquals(2, tokens.size());
    }

    @Test
    public void testTokenizeEmptyArray(){
        String s = "[]";
        Tokenizer tokenizer = new Tokenizer(s);
        List<Token> tokens =  tokenizer.scan();
        Assertions.assertEquals(2, tokens.size());
    }

    @Test
    public void testTokenizeJson(){
        String json = "{\n" +
                "    \"hello\": \"world\",\n" +
                "    \"boolean1\": true,\n" +
                "    \"boolean2\": false,\n" +
                "    \"inner\": {\n" +
                "        \"something here\": [\n" +
                "            1123213,\n" +
                "          \t0,\n" +
                "            2,\n" +
                "            3\n" +
                "        ]\n" +
                "    },\n" +
                "    \"arr\": [\n" +
                "        \"arr1\",\n" +
                "        1\n" +
                "    ],\n" +
                "\t\"number\": 1.055\n" +
                "}";

        Tokenizer tokenizer = new Tokenizer(json);
        List<Token> tokens =  tokenizer.scan();
        Assertions.assertEquals(59, tokens.size());
    }

    @Test
    public void testInvalidJson(){
        String json = "{stuff}";

        Tokenizer tokenizer = new Tokenizer(json);
        Assertions.assertThrows(InvalidTokenException.class, tokenizer::scan);
    }

    @Test
    public void testNull(){
        String json = "{null}";
        Tokenizer tokenizer = new Tokenizer(json);
        List<Token> tokens =  tokenizer.scan();
        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenNull.class, tokens.get(1).getClass());
    }

}