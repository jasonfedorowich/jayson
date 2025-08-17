import org.json.error.InvalidJsonException;
import org.json.error.InvalidTokenException;
import org.json.parser.Parser;
import org.json.token.Token;
import org.json.token.Tokenizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class TestParser {

    @Test
    public void testParseEmptyJson(){
        String s = "{}";
        Tokenizer tokenizer = new Tokenizer(s);
        LinkedList<Token> tokens =  tokenizer.scan();
        Parser parser = new Parser(tokens);
        LinkedHashMap<String, Object> json = parser.parse();

        Assertions.assertTrue(json.isEmpty());

    }

    @Test
    public void testParseEmptyArray(){
        String s = "[]";
        Tokenizer tokenizer = new Tokenizer(s);
        LinkedList<Token> tokens =  tokenizer.scan();

        Parser parser = new Parser(tokens);

        Assertions.assertThrows(InvalidJsonException.class, ()->{
            LinkedHashMap<String, Object> json = parser.parse();
        });
    }

    @Test
    public void testParseJson(){
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
        LinkedList<Token> tokens =  tokenizer.scan();

        Parser parser = new Parser(tokens);

        LinkedHashMap<String, Object> map = parser.parse();

        Assertions.assertEquals(6, map.size());

        Assertions.assertTrue((Boolean)map.get("boolean1"));
        Assertions.assertFalse((Boolean)map.get("boolean2"));
        LinkedHashMap<String, Object> inner = (LinkedHashMap<String, Object>) map.get("inner");
        LinkedList<Object> somethingHere = (LinkedList<Object>) inner.get("something here");
        Assertions.assertEquals(4, somethingHere.size());
        long value = (Long)somethingHere.get(0);

        Assertions.assertEquals(1123213, value);
    }

    @Test
    public void testInvalidJson(){
        String json = "{\"s\": \"t\"}[]";

        Tokenizer tokenizer = new Tokenizer(json);
        LinkedList<Token> tokens =  tokenizer.scan();

        Parser parser = new Parser(tokens);
        Assertions.assertThrows(InvalidJsonException.class, parser::parse);
    }
}
