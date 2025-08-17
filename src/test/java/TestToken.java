import org.json.error.InvalidTokenException;
import org.json.token.Token;
import org.json.token.Tokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TestToken {

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

}
