# Lexical Analyzer Implementation - Assignment 01

## Team Details
- **Shazil Rehman** | 23i-0095
- **Abdul Mohaimin** |  23i-0652
- **Section:** G

## Language Overview
- **Language Name:** CC-Lang
- **File Extension:** `.lang`

## Features
- **Manual Scanner:** Implemented using a transition-logic based DFA in Java.
- **JFlex Scanner:** Specification provided in `Scanner.flex`.
- **Error Handling:** Robust detection and recovery for:
  - Invalid characters
  - Malformed literals (trailing dots, excessive decimals)
  - Invalid identifiers (lowercase start, too long)
  - Unterminated strings and unclosed comments.
- **Bonus Features:**
  - **Nested Multi-line Comments:** Supported in the manual scanner.
  - **DFA Statistics:** Displays counts and statistics after scanning.
  - **Symbol Table:** Tracks identifiers, their first occurrence, and frequency.

## Compilation and Execution

### Prerequisites
- JDK 8 or higher.
- JFlex (optional for Part 2).

### Running the Manual Scanner
1. Compile the source files:
   ```bash
   javac src/*.java -d bin
   ```
2. Run the scanner on a test file:
   ```bash
   java -cp bin ManualScanner tests/test1.lang
   ```

## Lexical Rules
Detailed rules can be found in `docs/LanguageGrammar.txt`.

### Identifiers
- Must start with an uppercase letter.
- Max length: 31 characters.
- Example: `My_Variable1` (Valid), `myVariable` (Invalid).

### Literals
- **Integers:** `123`, `-45`, `+67`.
- **Floats:** `3.14`, `-0.001`, `1.2e5`.
- **Strings:** `"Hello\nWorld"`.

### Operators & Punctuators
- Comprehensive support for arithmetic, logical, relational, and assignment operators.
- Punctuators: `(`, `)`, `{`, `}`, `[`, `]`, `,`, `;`, `:`.

