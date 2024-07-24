import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {
    private String input;
    private int position;
    private int lineNumber;
    private Map<String, TokenType> symbolTable;
    
    public LexicalAnalyzer(String input) {
        this.input = input;
        this.position = 0;
        this.lineNumber = 1;
        this.symbolTable = new HashMap<>();
        prepopulateSymbolTable();
    }
    
    public List<Token> analyze() {
        List<Token> tokens = new ArrayList<>();
        
        while (position < input.length()) {
            char currentChar = input.charAt(position);
            
             if (currentChar == '\n') {
                lineNumber++;
                position++;
            } else if (Character.isWhitespace(currentChar)) {
                // Skip whitespace
                position++;
            } else if (currentChar == '%') {
                // Skip the rest of the line for comments
                while (position < input.length() && input.charAt(position) != '\n') {
                    position++;
                }
            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                // Identifier or keyword
                StringBuilder identifier = new StringBuilder();
                while (position < input.length() &&
                        (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                    identifier.append(input.charAt(position));
                    position++;
                }
                String tokenValue = identifier.toString();
                if (isKeyword(tokenValue)) {
                    System.out.println(TokenType.KEYWORD);
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, tokenValue));
                    symbolTable.putIfAbsent(tokenValue, TokenType.IDENTIFIER);
                }
            } else if (Character.isDigit(currentChar)) {
                // Integer or float constant
                StringBuilder constant = new StringBuilder();
                while (position < input.length() && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
                    constant.append(input.charAt(position));
                    position++;
                }
                System.out.println(TokenType.CONSTANT);
            } else {
                // Operator or other character
                switch (currentChar) {
                    case ':':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                            System.out.println(TokenType.ASSIGN_OP);
                            position += 2;
                        } else {
                            System.err.println("Invalid character: " + currentChar + " at line " + lineNumber);
                            return tokens;
                        }
                        break;
                    case '=':
                    case '>':
                    case '<':
                    case '!':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                            System.out.println(TokenType.RELOP);
                            position += 2;
                        } else {
                            System.out.println(TokenType.RELOP);
                            position++;
                        }
                        break;
                    case '+':
                    case '-':
			     System.out.println(TokenType.ADDOP);
			     position++;
			     break;
                    case '|':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '|') {
                            System.out.println(TokenType.ADDOP);
                            position += 2;
                        } else {
                            System.err.println("Invalid character: " + currentChar + " at line " + lineNumber);
                            return tokens;
                        }
                        break;
                    case '*':
                    case '/':
			     System.out.println(TokenType.MULOP);
			     position++;
			     break;
                    case '&':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '&') {
                            System.out.println(TokenType.MULOP);
                            position += 2;
                        } else {
                            System.err.println("Invalid character: " + currentChar + " at line " + lineNumber);
                            return tokens;
                        }
                        break;
                    case '(':
                    case ')':
                    case ';':
                    case ',':
                        System.out.println(TokenType.SYMBOL);
                        position++;
                        break;
                    case '{':
                        // Start of literal
                        StringBuilder literal = new StringBuilder();
                        literal.append(currentChar);
                        position++;
                        // Continue adding characters until the end of the literal is found
                        while (position < input.length() && input.charAt(position) != '}') {
                            char nextChar = input.charAt(position);
				   if (nextChar == '\n') {
                                System.err.println("Unclosed literal at line " + lineNumber);
                                return tokens;
				   }
                            if (nextChar >= 0 && nextChar <= 255) { // ASCII printable characters
                                literal.append(nextChar);
                                position++;
                            } else {
                                System.err.println("Non-ASCII character in literal at line " + lineNumber);
                                return tokens;
                            }
                        }
                        if (position < input.length() && input.charAt(position) == '}') {
                            literal.append(input.charAt(position)); // Include the closing '}'
                            System.out.println(TokenType.LITERAL);
                            position++;
                        } else {
                            System.err.println("Unclosed literal at line " + lineNumber);
                            return tokens;
                        }
                        break;
                    default:
                        System.err.println("Invalid character: " + currentChar + " at line " + lineNumber);
                        return tokens;
                }
            }
        }
        
        return tokens;
    }
    
    private void prepopulateSymbolTable() {
        // Keywords
        symbolTable.put("app", TokenType.KEYWORD);
        symbolTable.put("var", TokenType.KEYWORD);
        symbolTable.put("integer", TokenType.KEYWORD);
        symbolTable.put("real", TokenType.KEYWORD);
        symbolTable.put("init", TokenType.KEYWORD);
        symbolTable.put("return", TokenType.KEYWORD);
        symbolTable.put("if", TokenType.KEYWORD);
        symbolTable.put("then", TokenType.KEYWORD);
        symbolTable.put("else", TokenType.KEYWORD);
        symbolTable.put("end", TokenType.KEYWORD);
        symbolTable.put("repeat", TokenType.KEYWORD);
        symbolTable.put("until", TokenType.KEYWORD);
        symbolTable.put("read", TokenType.KEYWORD);
        symbolTable.put("write", TokenType.KEYWORD);
        // Relational operators
        symbolTable.put("=", TokenType.RELOP);
        symbolTable.put(">", TokenType.RELOP);
        symbolTable.put(">=", TokenType.RELOP);
        symbolTable.put("<", TokenType.RELOP);
        symbolTable.put("<=", TokenType.RELOP);
        symbolTable.put("!=", TokenType.RELOP);
        // Additive operators
        symbolTable.put("+", TokenType.ADDOP);
        symbolTable.put("-", TokenType.ADDOP);
        symbolTable.put("||", TokenType.ADDOP);
        // Multiplicative operators
        symbolTable.put("*", TokenType.MULOP);
        symbolTable.put("/", TokenType.MULOP);
        symbolTable.put("&&", TokenType.MULOP);
    }
    
    private boolean isKeyword(String tokenValue) {
        return symbolTable.containsKey(tokenValue) && symbolTable.get(tokenValue) == TokenType.KEYWORD;
    }
}

class Token {
    private TokenType type;
    private String value;
    
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public TokenType getType() {
        return type;
    }
    
    public String getValue() {
        return value;
    }
}

enum TokenType {
    KEYWORD,
    IDENTIFIER,
    CONSTANT,
    SYMBOL,
    OPERATOR,
    RELOP,
    ADDOP,
    MULOP,
    ASSIGN_OP,
    LITERAL
}
