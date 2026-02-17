public class ErrorHandler {
    private int errorCount = 0;

    public void reportError(String type, int line, int col, String lexeme, String reason) {
        System.err.printf("Error: %s [Line: %d, Col: %d] Lexeme: '%s' - Reason: %s\n",
                type, line, col, lexeme, reason);
        errorCount++;
    }

    public int getErrorCount() {
        return errorCount;
    }
}
