import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Driver class for the JFlex-generated Scanner.
 * This class provides a main method to run the JFlex scanner
 * and display results in the same format as ManualScanner.
 */
public class JFlexScanner {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java JFlexScanner <input_file>");
            return;
        }

        try {
            ErrorHandler errorHandler = new ErrorHandler();
            SymbolTable symbolTable = new SymbolTable();

            FileReader reader = new FileReader(args[0]);
            Scanner scanner = new Scanner(reader, errorHandler, symbolTable);

            Token token;
            int totalTokens = 0;
            int commentsRemoved = 0;
            Map<TokenType, Integer> tokenCounts = new HashMap<>();

            // Initialize token counts
            for (TokenType type : TokenType.values()) {
                tokenCounts.put(type, 0);
            }

            // Scan all tokens
            while ((token = scanner.yylex()) != null && token.getType() != TokenType.EOF) {
                System.out.println(token);
                totalTokens++;
                tokenCounts.put(token.getType(), tokenCounts.get(token.getType()) + 1);
            }

            // Display statistics (matching ManualScanner format)
            System.out.println("\n--- Statistics ---");
            System.out.println("Total Tokens: " + totalTokens);
            System.out.println("Lines Processed: " + scanner.getLineCount());
            System.out.println("Comments Removed: " + commentsRemoved); // JFlex doesn't track this easily
            System.out.println("Token counts by type:");
            for (Map.Entry<TokenType, Integer> entry : tokenCounts.entrySet()) {
                if (entry.getValue() > 0) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }
            }

            // Display symbol table
            symbolTable.display();

            // Display errors if any
            if (errorHandler.getErrorCount() > 0) {
                System.out.println("\nTotal Errors: " + errorHandler.getErrorCount());
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + args[0]);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
