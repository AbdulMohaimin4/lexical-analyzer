import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ManualScanner {
    private String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;
    private ErrorHandler errorHandler;
    private SymbolTable symbolTable;

    // Statistics
    private int totalTokens = 0;
    private int linesProcessed = 0;
    private int commentsRemoved = 0;
    private java.util.Map<TokenType, Integer> tokenCounts = new java.util.HashMap<>();

    private static final Set<String> KEYWORDS = new HashSet<>(java.util.Arrays.asList(
            "start", "finish", "loop", "condition", "declare", "output", "input", "function", "return", "break",
            "continue", "else"));

    public ManualScanner(String filePath, ErrorHandler errorHandler, SymbolTable symbolTable) throws IOException {
        this.input = new String(Files.readAllBytes(Paths.get(filePath)));
        this.errorHandler = errorHandler;
        this.symbolTable = symbolTable;
        for (TokenType type : TokenType.values()) {
            tokenCounts.put(type, 0);
        }
    }

    private char peek() {
        if (pos >= input.length())
            return '\0';
        return input.charAt(pos);
    }

    private char advance() {
        if (pos >= input.length())
            return '\0';
        char ch = input.charAt(pos++);
        if (ch == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        return ch;
    }

    private boolean isAtEnd() {
        return pos >= input.length();
    }

    public Token nextToken() {
        while (!isAtEnd()) {
            int startLine = line;
            int startCol = col;
            char ch = peek();

            // 12. Whitespace
            if (Character.isWhitespace(ch)) {
                advance();
                continue;
            }

            // 1 & 2. Comments
            if (ch == '#') {
                advance();
                if (peek() == '#') { // Single line ##
                    advance();
                    while (!isAtEnd() && peek() != '\n') {
                        advance();
                    }
                    commentsRemoved++;
                    continue;
                } else if (peek() == '*') { // Multi-line #* ... *#
                    advance();
                    int depth = 1; // For nested comments bonus
                    boolean closed = false;
                    while (!isAtEnd()) {
                        char c = advance();
                        if (c == '#' && peek() == '*') {
                            advance();
                            depth++;
                        } else if (c == '*' && peek() == '#') {
                            advance();
                            depth--;
                            if (depth == 0) {
                                closed = true;
                                break;
                            }
                        }
                    }
                    if (!closed) {
                        errorHandler.reportError("Unclosed Comment", startLine, startCol, "#*",
                                "Multi-line comment was not closed.");
                    }
                    commentsRemoved++;
                    continue;
                } else {
                    errorHandler.reportError("Invalid Character", startLine, startCol, "#",
                            "Single # is not a valid token.");
                    continue;
                }
            }

            // 9. String Literals
            if (ch == '"') {
                Token t = scanString(startLine, startCol);
                if (t != null)
                    return t;
                continue;
            }

            // 9. Char Literals
            if (ch == '\'') {
                Token t = scanChar(startLine, startCol);
                if (t != null)
                    return t;
                continue;
            }

            // 3, 10. Operators
            Token op = scanOperator(startLine, startCol);
            if (op != null)
                return op;

            // 11. Punctuators
            Token punct = scanPunctuator(startLine, startCol);
            if (punct != null)
                return punct;

            // 7, 8. Numbers (Integers & Floats)
            if (Character.isDigit(ch) || ((ch == '+' || ch == '-') && pos + 1 < input.length()
                    && Character.isDigit(input.charAt(pos + 1)))) {
                Token t = scanNumber(startLine, startCol);
                if (t != null)
                    return t;
                continue;
            }

            // 4, 5, 6. Keywords, Booleans, Identifiers
            if (Character.isLetter(ch)) {
                Token t = scanWord(startLine, startCol);
                if (t != null)
                    return t;
                continue;
            }

            // Error
            char errorChar = advance();
            errorHandler.reportError("Invalid Character", startLine, startCol, String.valueOf(errorChar),
                    "Character not recognized.");
        }
        return new Token(TokenType.EOF, "EOF", line, col);
    }

    private Token scanString(int startLine, int startCol) {
        StringBuilder sb = new StringBuilder();
        advance(); // "
        while (!isAtEnd() && peek() != '"' && peek() != '\n') {
            char c = advance();
            if (c == '\\') {
                if (isAtEnd())
                    break;
                char next = advance();
                switch (next) {
                    case 'n':
                        sb.append('\n');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    default:
                        errorHandler.reportError("Malformed String", startLine, startCol, "\\" + next,
                                "Invalid escape sequence.");
                        sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        if (peek() == '"') {
            advance();
            return createToken(TokenType.STRING_LITERAL, sb.toString(), startLine, startCol);
        } else {
            errorHandler.reportError("Unterminated String", startLine, startCol, sb.toString(),
                    "String reaches end of line or file.");
            return null; // Recovery: skip
        }
    }

    private Token scanChar(int startLine, int startCol) {
        advance(); // '
        StringBuilder sb = new StringBuilder();
        if (!isAtEnd() && peek() != '\'') {
            char c = advance();
            if (c == '\\') {
                char next = advance();
                sb.append('\\').append(next);
            } else {
                sb.append(c);
            }
        }
        if (peek() == '\'') {
            advance();
            return createToken(TokenType.CHARACTER_LITERAL, sb.toString(), startLine, startCol);
        } else {
            errorHandler.reportError("Malformed Char", startLine, startCol, sb.toString(), "Char literal error.");
            return null;
        }
    }

    private Token scanNumber(int startLine, int startCol) {
        StringBuilder sb = new StringBuilder();
        if (peek() == '+' || peek() == '-')
            sb.append(advance());

        while (Character.isDigit(peek())) {
            sb.append(advance());
        }

        if (peek() == '.') {
            sb.append(advance());
            int decimalCount = 0;
            while (Character.isDigit(peek())) {
                sb.append(advance());
                decimalCount++;
            }
            if (decimalCount == 0) {
                errorHandler.reportError("Malformed Float", startLine, startCol, sb.toString(),
                        "Trailing dot in float.");
            }
            if (decimalCount > 6) {
                errorHandler.reportError("Malformed Float", startLine, startCol, sb.toString(),
                        "More than 6 decimal places.");
            }

            if (peek() == 'e' || peek() == 'E') {
                sb.append(advance());
                if (peek() == '+' || peek() == '-')
                    sb.append(advance());
                while (Character.isDigit(peek())) {
                    sb.append(advance());
                }
            }
            return createToken(TokenType.FLOAT_LITERAL, sb.toString(), startLine, startCol);
        }
        return createToken(TokenType.INTEGER_LITERAL, sb.toString(), startLine, startCol);
    }

    private Token scanWord(int startLine, int startCol) {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            sb.append(advance());
        }
        String word = sb.toString();

        if (KEYWORDS.contains(word)) {
            return createToken(TokenType.KEYWORD, word, startLine, startCol);
        }
        if (word.equals("true") || word.equals("false")) {
            return createToken(TokenType.BOOLEAN_LITERAL, word, startLine, startCol);
        }

        // Identifier Rules: Must start with uppercase
        if (Character.isUpperCase(word.charAt(0))) {
            if (word.length() > 31) {
                errorHandler.reportError("Invalid Identifier", startLine, startCol, word,
                        "Identifier exceeds 31 characters.");
            }
            symbolTable.insert(word, "Identifier", startLine, startCol);
            return createToken(TokenType.IDENTIFIER, word, startLine, startCol);
        } else {
            errorHandler.reportError("Invalid Identifier", startLine, startCol, word,
                    "Identifier must start with uppercase.");
            return null;
        }
    }

    private Token scanOperator(int startLine, int startCol) {
        char c1 = peek();
        if ("+-*/%=!<>|&".indexOf(c1) == -1)
            return null;

        String lexeme = String.valueOf(advance());
        char c2 = peek();

        // Multi-char operators: **, ==, !=, <=, >=, &&, ||, +=, -=, *=, /=, ++, --
        String combined = lexeme + c2;
        if (java.util.Arrays.asList("**", "==", "!=", "<=", ">=", "&&", "||", "+=", "-=", "*=", "/=", "++", "--")
                .contains(combined)) {
            advance();
            return createToken(TokenType.OPERATOR, combined, startLine, startCol);
        }

        // Single char operators
        if ("+-*/%=!<>".indexOf(c1) != -1) {
            return createToken(TokenType.OPERATOR, lexeme, startLine, startCol);
        }

        return null;
    }

    private Token scanPunctuator(int startLine, int startCol) {
        char c = peek();
        if ("(){}[],;:".indexOf(c) != -1) {
            return createToken(TokenType.PUNCTUATOR, String.valueOf(advance()), startLine, startCol);
        }
        return null;
    }

    private Token createToken(TokenType type, String lexeme, int l, int c) {
        totalTokens++;
        tokenCounts.put(type, tokenCounts.get(type) + 1);
        return new Token(type, lexeme, l, c);
    }

    public void displayStatistics() {
        System.out.println("\n--- Statistics ---");
        System.out.println("Total Tokens: " + totalTokens);
        System.out.println("Lines Processed: " + (line - 1));
        System.out.println("Comments Removed: " + commentsRemoved);
        System.out.println("Token counts by type:");
        for (java.util.Map.Entry<TokenType, Integer> entry : tokenCounts.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java ManualScanner <input_file>");
            return;
        }
        try {
            ErrorHandler eh = new ErrorHandler();
            SymbolTable st = new SymbolTable();
            ManualScanner scanner = new ManualScanner(args[0], eh, st);

            Token t;
            while ((t = scanner.nextToken()) != null && t.getType() != TokenType.EOF) {
                System.out.println(t);
            }

            scanner.displayStatistics();
            st.display();
            if (eh.getErrorCount() > 0) {
                System.out.println("\nTotal Errors: " + eh.getErrorCount());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
