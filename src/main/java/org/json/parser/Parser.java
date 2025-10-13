package org.json.parser;

import org.json.error.InvalidJsonException;
import org.json.error.InvalidTokenException;
import org.json.parser.token.Token;
import org.json.parser.token.terminal.TokenBoolean;
import org.json.parser.token.terminal.TokenNumber;
import org.json.parser.token.terminal.TokenString;
import org.json.parser.token.TokenType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Parser {

    private final LinkedList<Token> tokens;


    public Parser(LinkedList<Token> tokens){
        this.tokens = tokens;
    }

    public Object parse(){
        if(tokens == null || tokens.isEmpty()) return null;
        Object json = switch (tokens.peekFirst().getType()) {
            case OPEN_SQUARE_BRACE -> parseArray();
            case OPEN_CURLY_BRACE -> parseObject();
            case NULL -> null;
            default -> throw new InvalidTokenException("Unexpected token type: " + tokens.peekFirst().getType());
        };
        expectEnd();
       return json;
    }

    private LinkedHashMap<String, Object> parseObject() {
        expectCurlyBraceOpen();
        Stack<LinkedHashMap<String, Object>> stack = new Stack<>();
        LinkedList<String> keys = new LinkedList<>();

        LinkedHashMap<String, Object> object = new LinkedHashMap<>();
        stack.add(object);

        while(!stack.isEmpty()){

            TokenType type = peekFirst().getType();
            switch(type){
                case QUOTE:
                    if(keys.isEmpty()){
                        String key = expectKey();
                        expectColon();
                        if(object.containsKey(key)) throw new InvalidJsonException("Got duplicated key: " + key);
                        keys.add(key);
                        continue;
                    }else{
                        object.put(keys.pop(), parseString());
                    }
                    break;
                case OPEN_SQUARE_BRACE:
                    object.put(keys.pop(), parseArray());
                    break;
                case OPEN_CURLY_BRACE:
                    expectCurlyBraceOpen();
                    LinkedHashMap<String, Object> newObject = new LinkedHashMap<>();
                    object.put(keys.pop(), newObject);
                    stack.push(newObject);
                    object = newObject;
                    continue;
                case BOOLEAN:
                    object.put(keys.pop(), parseBoolean());
                    break;
                case NUMBER:
                    object.put(keys.pop(), parseNumber());
                    break;
                case NULL:
                    object.put(keys.pop(), parseNull());
                    break;
                case CLOSED_CURLY_BRACE:
                    expectClosedCurlyBrace();
                    stack.pop();
                    if(stack.isEmpty()){
                        return object;
                    }else{
                        object = stack.peek();
                    }
                    break;
                default:
                    throw new InvalidJsonException("Unknown token: " + type);
            }

            expectCommaOrEnd(TokenType.CLOSED_CURLY_BRACE);
        }

        expectEndObject(stack);

        return object;
    }




    private Object parseArray() {
        LinkedList<Object> array = new LinkedList<>();
        Stack<LinkedList<Object>> stack = new Stack<>();
        stack.push(array);

        expectOpenArray();

        while(!stack.isEmpty()){

            TokenType type = peekFirst().getType();
            switch(type){
                case NULL:
                    array.add(parseNull());
                    break;
                case QUOTE:
                    array.add(parseString());
                    break;
                case OPEN_SQUARE_BRACE:
                    expectCurlyBraceOpen();
                    LinkedList<Object> newArray = new LinkedList<>();
                    array.add(newArray);
                    stack.push(newArray);
                    array = newArray;
                    continue;
                case OPEN_CURLY_BRACE:
                    array.add(parseObject());
                    break;
                case BOOLEAN:
                    array.add(parseBoolean());
                    break;
                case NUMBER:
                    array.add(parseNumber());
                    break;
                case CLOSED_SQUARE_BRACE:
                    expectClosedSquareBrace();
                    stack.pop();
                    if(stack.isEmpty()){
                        return array;
                    }else{
                        array = stack.peek();
                    }
                    break;
                default:
                    throw new InvalidJsonException("Unknown token: " + type);
            }

            expectCommaOrEnd(TokenType.CLOSED_SQUARE_BRACE);

        }

        expectEndArray(stack);

        return array;

    }



    private Object parseNull() {
        pollFirst();
        return null;
    }

    private Object parseNumber() {
        Token value = pollFirst();
        if(!(value instanceof TokenNumber)) throw new InvalidJsonException("Expect number got: " + value.getType().toString());
        return ((TokenNumber)value).getNumber();
    }

    private Object parseBoolean() {
        Token value = pollFirst();
        if(!(value instanceof TokenBoolean)) throw new InvalidJsonException("Expect boolean got: " + value.getType().toString());
        return ((TokenBoolean)value).getBoolean();
    }

    private Object parseString() {
        expectQuote();
        Token value = pollFirst();
        if(!(value instanceof TokenString)) throw new InvalidJsonException("Expected value got: " + value.getType().toString());
        expectQuote();
        return ((TokenString)value).getString();
    }

    private void expectEnd() {
        if(!this.tokens.isEmpty()) throw new InvalidJsonException("Invalid json object expected end got: " + tokens);
    }

    private void expectClosedSquareBrace() {
        Token token = pollFirst();
        if(!token.getType().equals(TokenType.CLOSED_SQUARE_BRACE)) throw new InvalidJsonException("Expected closed array got: " + token.getType());

    }

    private void expectEndObject(Stack<LinkedHashMap<String, Object>> stack) {
        Token token = pollFirst();
        stack.pop();
        if(!token.getType().equals(TokenType.CLOSED_CURLY_BRACE)) throw new InvalidJsonException("Expected closed object got: " + token.getType());
        if(!stack.isEmpty()) throw new InvalidJsonException("Invalid json object still not empty");
    }

    private void expectClosedCurlyBrace() {
        Token token = pollFirst();
        if(!token.getType().equals(TokenType.CLOSED_CURLY_BRACE)) throw new InvalidJsonException("Expected closed object got: " + token.getType());
    }

    private void expectEndArray(Stack<LinkedList<Object>> stack) {
        Token token = pollFirst();
        stack.pop();
        if(!token.getType().equals(TokenType.CLOSED_SQUARE_BRACE)) throw new InvalidJsonException("Expected closed array got: " + token.getType());
        if(!stack.isEmpty()) throw new InvalidJsonException("Invalid array object still not empty");
    }

    private void expectOpenArray() {
        Token token = pollFirst();
        if(!token.getType().equals(TokenType.OPEN_SQUARE_BRACE)) throw new InvalidJsonException("Expected open array got: " + token.getType());
    }


    private void expectCommaOrEnd(TokenType end) {
        Token next = peekFirst();
        if(next.getType().equals(TokenType.COMMA)){
            pollFirst();
        }
        else if(next.getType().equals(end)) return;
        else{
            throw new InvalidJsonException("Unknown token when expected comma or end brace: "+ next);
        }
    }


    private void expectCurlyBraceOpen() {
        Token root = pollFirst();
        if(!root.getType().equals(TokenType.OPEN_CURLY_BRACE)) throw new InvalidJsonException("Must have '{'");
    }


    private void expectQuote() {
        Token token = pollFirst();
        if(!token.getType().equals(TokenType.QUOTE)) throw new InvalidJsonException("Expected quote got: " + token.getType().toString());
    }

    private void expectColon() {
        Token token = pollFirst();
        if(!token.getType().equals(TokenType.COLON)) throw new InvalidJsonException("Expected colon got: " + token.getType().toString());
    }

    private String expectKey() {
        expectQuote();
        Token key = pollFirst();
        if(!(key instanceof TokenString)) throw new InvalidJsonException("Expected key got: " + key.getType().toString());
        expectQuote();
        return ((TokenString)key).getString();
    }

    private Token pollFirst(){
        if(tokens.isEmpty()) throw new InvalidJsonException("Empty json");
        return tokens.pollFirst();
    }

    private Token peekFirst(){
        if(tokens.isEmpty()) throw new InvalidJsonException("Empty json");
        return tokens.peekFirst();
    }


}
