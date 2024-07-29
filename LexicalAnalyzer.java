
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LexicalAnalyzer {
    private String input;
    private int position;
    private int lineNumber;
    private HashMap<String, TokenType> symbolTable;
    
    public LexicalAnalyzer(String input) {
        this.input = input;
        this.position = 0;
        this.lineNumber = 1;
        this.symbolTable = new HashMap<>();
        prepopulateSymbolTable();
    }
    
    public Token analyze() throws Exception {
        while (position < input.length()) {
            char currentChar = input.charAt(position);
            
            if (currentChar == '\n') {
                lineNumber++;
                position++;
            } else if (Character.isWhitespace(currentChar)) {
                position++;
            } else if (currentChar == '%') {
                while (position < input.length() && input.charAt(position) != '\n') {
                    position++;
                }
            } else if (Character.isLetter(currentChar) || currentChar == '_') {
                // ID ou KW
                StringBuilder identifier = new StringBuilder();
                while (position < input.length() &&
                        (Character.isLetterOrDigit(input.charAt(position)) || input.charAt(position) == '_')) {
                    identifier.append(input.charAt(position));
                    position++;
                }
                String tokenValue = identifier.toString();
                if (isKeyword(tokenValue)) {
                    System.out.println(TokenType.KEYWORD + ", "+ tokenValue);
                } else {
                    symbolTable.putIfAbsent(tokenValue, TokenType.IDENTIFIER);
                    System.out.println(TokenType.IDENTIFIER + ", "+ tokenValue);
                }
            } else if (Character.isDigit(currentChar)) {
                // integer ou real
                StringBuilder constant = new StringBuilder();
                while (position < input.length() && (Character.isDigit(input.charAt(position)) || input.charAt(position) == '.')) {
                    constant.append(input.charAt(position));
                    position++;
                }
                System.out.println(TokenType.CONSTANT + ", "+ constant.toString());
            } else {
                // operadores
                switch (currentChar) {
                    case ':':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                            System.out.println(TokenType.ASSIGN_OP + ", "+ ":=");
                            position += 2;
                        } else {
                            printSymbolTable();
                            throw new Exception("Invalid character: " + currentChar + " at line " + lineNumber);
                        }
                        break;
                    case '=':
			System.out.println(TokenType.RELOP + ", "+ currentChar);
                        position++;
			break;
                    case '>':
                    case '<':
                    case '!':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                            System.out.println(TokenType.RELOP + ", "+ currentChar + "=");
                            position += 2;
                        } else {
                            System.out.println(TokenType.RELOP + ", "+ currentChar);
                            position++;
                        }
                        break;
                    case '+':
                    case '-':
			     System.out.println(TokenType.ADDOP + ", "+ currentChar);
			     position++;
			     break;
                    case '|':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '|') {
                            System.out.println(TokenType.ADDOP + ", "+ currentChar + "|");
                            position += 2;
                        } else {
                            printSymbolTable();
                            throw new Exception("Invalid character: " + currentChar + " at line " + lineNumber);
                        }
                        break;
                    case '*':
                    case '/':
			     System.out.println(TokenType.MULOP + ", "+ currentChar);
			     position++;
			     break;
                    case '&':
                        if (position + 1 < input.length() && input.charAt(position + 1) == '&') {
                            System.out.println(TokenType.MULOP + ", "+ currentChar + "&");
                            position += 2;
                        } else {
                            printSymbolTable();
                            throw new Exception("Invalid character: " + currentChar + " at line " + lineNumber);
                        }
                        break;
                    case '(':
                    case ')':
                    case ';':
                    case ',':
                        System.out.println(TokenType.SYMBOL + ", "+ currentChar);
                        position++;
                        break;
                    case '{':
                        StringBuilder literal = new StringBuilder();
                        literal.append(currentChar);
                        position++;
                        
                        while (position < input.length() && input.charAt(position) != '}') {
                            char nextChar = input.charAt(position);
                            if (nextChar == '\n') {
                                printSymbolTable();
                                throw new Exception("Unclosed literal at line " + lineNumber);
                            }
                            if (nextChar >= 0 && nextChar <= 255) {
                                literal.append(nextChar);
                                position++;
                            } else {
                                printSymbolTable();
                                throw new Exception("Non-ASCII character in literal at line " + lineNumber);
                            }
                        }
                        if (position < input.length() && input.charAt(position) == '}') {
                            literal.append(input.charAt(position));
                            System.out.println(TokenType.LITERAL + ", "+ literal.toString());
                            position++;
                        } else {
                            printSymbolTable();
                            throw new Exception("Unclosed literal at line " + lineNumber);
                        }
                        break;
                    default:
                        printSymbolTable();
                        throw new Exception("Invalid character: " + currentChar + " at line " + lineNumber);
                }
            }
        }
        
        printSymbolTable();
	System.out.println("");
        System.out.println("UHUUUUUUULL!!!🤩(Código fonte lido com sucesso ✅)");
        return null;
    }
    
    private void printSymbolTable()
    {
        System.out.println("");
        System.out.println("Tabela de simbolos");
        
        symbolTable.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
    }
    
    private void prepopulateSymbolTable() {
        // keywords
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
        // relops
        symbolTable.put("=", TokenType.RELOP);
        symbolTable.put(">", TokenType.RELOP);
        symbolTable.put(">=", TokenType.RELOP);
        symbolTable.put("<", TokenType.RELOP);
        symbolTable.put("<=", TokenType.RELOP);
        symbolTable.put("!=", TokenType.RELOP);
        // addops
        symbolTable.put("+", TokenType.ADDOP);
        symbolTable.put("-", TokenType.ADDOP);
        symbolTable.put("||", TokenType.ADDOP);
        // mulops
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
