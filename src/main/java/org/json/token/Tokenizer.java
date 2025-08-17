package org.json.token;

import org.json.error.InvalidTokenException;
import org.json.token.terminal.TokenBoolean;
import org.json.token.terminal.TokenNumber;
import org.json.token.terminal.TokenString;

import java.util.LinkedList;

public class Tokenizer {

    private final String json;

    private final LinkedList<Token> tokens = new LinkedList<>();

    private int current = 0;

    public Tokenizer(String json){
        this.json = json;
    }

    public LinkedList<Token> scan(){
        while(isNotEnd()){
            scanToken();
        }
        return tokens;
    }



    private void scanToken() {
        char t = advance();
        switch (t){
            case '{':
                addToken(TokenType.OPEN_CURLY_BRACE);
                break;
            case '}':
                addToken(TokenType.CLOSED_CURLY_BRACE);
                break;
            case '[':
                addToken(TokenType.OPEN_SQUARE_BRACE);
                break;
            case ']':
                addToken(TokenType.CLOSED_SQUARE_BRACE);
                break;
            case ':':
                addToken(TokenType.COLON);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '"':
                addString();
                break;
            case 't', 'f':
                addBoolean(t);
                break;
            default:
               if(Character.isDigit(t)){
                   addNumber(t);
                   break;
               }
               else if(Character.isWhitespace(t)) break;
               else throw new InvalidTokenException("Invalid token: " + t);
        }

    }

    private void addBoolean(char t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t);
        String booleanString = null;
        if(t == 't'){
            sb.append(validateAdvance());
            sb.append(validateAdvance());
            sb.append(validateAdvance());
            booleanString = sb.toString();
            if(!booleanString.equals("true")) throw new InvalidTokenException("Expected value true");

        }else if(t == 'f'){
            sb.append(validateAdvance());
            sb.append(validateAdvance());
            sb.append(validateAdvance());
            sb.append(validateAdvance());
            booleanString = sb.toString();
            if(!booleanString.equals("false")) throw new InvalidTokenException("Expected value false");
        }
        tokens.add(new TokenBoolean(Boolean.parseBoolean(booleanString)));
    }

    private void addNumber(char t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t);
        while(isNotEnd() && Character.isDigit(peek())){
            sb.append(advance());
        }

        if(peek() == '.'){
            do {
                sb.append(advance());
            } while (isNotEnd() && Character.isDigit(peek()));
            tokens.add(new TokenNumber.TokenDouble(Double.valueOf(sb.toString())));
        }else{
            tokens.add(new TokenNumber.TokenLong(Long.valueOf(sb.toString())));
        }
    }

    private void addString() {
        addToken(TokenType.QUOTE);

        StringBuilder sb = new StringBuilder();

        while(isNotEnd() && peek() != '"'){
            sb.append(advance());
        }

        char c = advance();
        if(c != '"') throw new InvalidTokenException(c);
        tokens.add(new TokenString(sb.toString()));
        addToken(TokenType.QUOTE);
    }

    private char peek(){
        return json.charAt(current);
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type));
    }

    private char advance() {
        return json.charAt(current++);
    }

    private char validateAdvance(){
        if(isEnd()) throw new InvalidTokenException("Invalid token did not expect end");
        return advance();
    }

    private boolean isNotEnd() {
        return current < json.length();
    }

    private boolean isEnd(){
        return current >= json.length();
    }

}
