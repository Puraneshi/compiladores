import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {
    private String input;
    private int position;
    
    public LexicalAnalyzer(String input) {
        this.input = input;
        this.position = 0;
    }
    
    public List<Token> analyze() {
        List<Token> tokens = new ArrayList<>();
        
        while (position < input.length()) {
            char currentChar = input.charAt(position);
            
            if (Character.isWhitespace(currentChar)) {
                // Skip whitespace
                position++;
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
                    tokens.add(new Token(TokenType.KEYWORD, tokenValue));
                } else {
                    tokens.add(new Token(TokenType.IDENTIFIER, tokenValue));
                }
            } else if (Character.isDigit(currentChar)) {
                // Integer or float constant
                StringBuilder constant = new StringBuilder();
                while (position < input.length() && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
                    constant.append(input.charAt(position));
                    position++;
                }
                tokens.add(new Token(TokenType.CONSTANT, constant.toString()));
            } else {
                // Operator or other character
                switch (currentChar) {
                    case ':':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                            tokens.add(new Token(TokenType.ASSIGN_OP, ":="));
                            position += 2;
                        } else {
                            tokens.add(new Token(TokenType.OPERATOR, Character.toString(currentChar)));
                            position++;
                        }
                        break;
                    case '=':
                    case '>':
                    case '<':
                    case '!':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                            tokens.add(new Token(TokenType.RELOP, Character.toString(currentChar) + "="));
                            position += 2;
                        } else {
                            tokens.add(new Token(TokenType.RELOP, Character.toString(currentChar)));
                            position++;
                        }
                        break;
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                        tokens.add(new Token(TokenType.ADDOP_MULOP, Character.toString(currentChar)));
                        position++;
                        break;
                    case '(':
                    case ')':
                    case ';':
                    case ',':
                        tokens.add(new Token(TokenType.SYMBOL, Character.toString(currentChar)));
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
                            if (nextChar >= 0 && nextChar <= 255) { // ASCII printable characters
                                literal.append(nextChar);
                                position++;
                            } else {
                                throw new IllegalArgumentException("Non-ASCII character in literal.");
                            }
                        }
                        if (position < input.length() && input.charAt(position) == '}') {
                            literal.append(input.charAt(position)); // Include the closing '}'
                            tokens.add(new Token(TokenType.LITERAL, literal.toString()));
                            position++;
                        } else {
                            throw new IllegalArgumentException("Unclosed literal.");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid character: " + currentChar);
                }
            }
        }
        
        return tokens;
    }
    
    private boolean isKeyword(String tokenValue) {
        switch (tokenValue) {
            case "app":
            case "var":
            case "integer":
            case "real":
            case "init":
            case "return":
            case "if":
            case "then":
            case "else":
            case "end":
            case "repeat":
            case "until":
            case "read":
            case "write":
                return true;
            default:
                return false;
        }
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
    ADDOP_MULOP,
    ASSIGN_OP,
    LITERAL
}
