import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private static class Entry {
        String name;
        String type; // For now it could be just "Identifier"
        int firstOccurrenceLine;
        int firstOccurrenceCol;
        int frequency;

        Entry(String name, String type, int line, int col) {
            this.name = name;
            this.type = type;
            this.firstOccurrenceLine = line;
            this.firstOccurrenceCol = col;
            this.frequency = 1;
        }
    }

    private Map<String, Entry> table = new HashMap<>();

    public void insert(String name, String type, int line, int col) {
        if (table.containsKey(name)) {
            table.get(name).frequency++;
        } else {
            table.put(name, new Entry(name, type, line, col));
        }
    }

    public void display() {
        System.out.println("\n--- Symbol Table ---");
        System.out.printf("%-20s | %-15s | %-10s | %-10s\n", "Name", "Type", "First Occ.", "Freq.");
        System.out.println("------------------------------------------------------------------");
        for (Entry entry : table.values()) {
            System.out.printf("%-20s | %-15s | L%d, C%d    | %-10d\n",
                    entry.name, entry.type, entry.firstOccurrenceLine, entry.firstOccurrenceCol, entry.frequency);
        }
    }
}
