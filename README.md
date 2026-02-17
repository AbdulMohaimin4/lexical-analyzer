# Assignment 01 - Lexical Analyzer

## Team Details
- **Shazil Rehman** | 23i-0095
- **Abdul Mohaimin** | 23i-0652
- **Section:** G

## Language Overview
- **Language Name:** CC-Lang
- **File Extension:** `.lang`

---

## Keywords with Meanings

| Keyword | Meaning |
|---------|---------|
| `start` | Marks the beginning of the program |
| `finish` | Marks the end of the program |
| `loop` | Loop construct for iteration |
| `condition` | Conditional statement |
| `declare` | Variable declaration |
| `output` | Print/output statement |
| `input` | Input statement to read data |
| `function` | Function definition |
| `return` | Return statement from function |
| `break` | Break from loop |
| `continue` | Continue to next iteration |
| `else` | Else clause for conditionals |

---

## Identifiers
- **Rules:** Must start with uppercase letter (A-Z), followed by lowercase letters, digits, or underscores
- **Maximum Length:** 31 characters
- **Valid Examples:** `Count`, `Variable_name`, `X`, `Total_sum_2024`
- **Invalid Examples:** `count`, `Variable`, `2Count`, `myVariable`

---

## Literals

### Integer Literals
- **Format:** `[+-]?[0-9]+`
- **Examples:** `42`, `+100`, `-567`, `0`

### Floating-Point Literals
- **Format:** `[+-]?[0-9]+\.[0-9]{1,6}([eE][+-]?[0-9]+)?`
- **Rules:** Up to 6 decimal places, supports scientific notation
- **Examples:** `3.14`, `+2.5`, `-0.123456`, `1.5e10`, `2.0E-3`

### String Literals
- **Format:** `"([^"\\\n]|\\["\\\ntr])*"`
- **Escape Sequences:** `\"`, `\\`, `\n`, `\t`, `\r`
- **Example:** `"Hello\nWorld"`

### Character Literals
- **Format:** `'([^'\\\n]|\\['\\\ntr])'`
- **Examples:** `'a'`, `'\n'`, `'\t'`

### Boolean Literals
- **Values:** `true`, `false` (case-sensitive)

---

## Operators

### Operator Precedence (Highest to Lowest)

| Precedence | Operators | Description |
|------------|-----------|-------------|
| 1 | `**` | Exponentiation |
| 2 | `++`, `--` | Increment/Decrement |
| 3 | `*`, `/`, `%` | Multiplicative |
| 4 | `+`, `-` | Additive |
| 5 | `<`, `>`, `<=`, `>=` | Relational |
| 6 | `==`, `!=` | Equality |
| 7 | `&&` | Logical AND |
| 8 | `||` | Logical OR |
| 9 | `!` | Logical NOT |
| 10 | `=`, `+=`, `-=`, `*=`, `/=` | Assignment |

### Operator Categories
- **Arithmetic:** `+`, `-`, `*`, `/`, `%`, `**`
- **Relational:** `==`, `!=`, `<`, `>`, `<=`, `>=`
- **Logical:** `&&`, `||`, `!`
- **Assignment:** `=`, `+=`, `-=`, `*=`, `/=`
- **Increment/Decrement:** `++`, `--`

---

## Punctuators
`(`, `)`, `{`, `}`, `[`, `]`, `,`, `;`, `:`

---

## Comments

### Single-Line Comments
- **Syntax:** `## comment text`
- **Example:** `## This is a comment`

### Multi-Line Comments
- **Syntax:** `#* comment text *#`
- **Nested Support:** Yes (Bonus feature)
- **Example:**
```
#*
  This is a multi-line comment
  #* Nested comment *#
*#
```

---

## Sample Programs

### Sample 1: Simple Calculator
```lang
start
    declare Num1 = 10;
    declare Num2 = 20;
    declare Sum = Num1 + Num2;
    output "Sum is: ";
    output Sum;
finish
```

### Sample 2: Loop Example
```lang
start
    declare Counter = 0;
    loop (condition Counter < 5) {
        output Counter;
        Counter = Counter + 1;
    }
    output "Loop finished";
finish
```

### Sample 3: Function with Exponentiation
```lang
start
    function Power(Base, Exp) {
        declare Result = Base ** Exp;
        return Result;
    }
    
    declare Answer = 2 ** 3;
    output "2 to the power 3 is: ";
    output Answer;
    
    declare Val = true;
    condition (Val == true) {
        output "Boolean test passed";
    }
finish
```

---

## Compilation and Execution

### Prerequisites
- JDK 8 or higher
- JFlex (for JFlex scanner)

### Running the Manual Scanner

1. **Compile:**
```bash
javac src/*.java -d bin
```

2. **Run:**
```bash
java -cp bin ManualScanner tests/test1.lang
```

### Running the JFlex Scanner

1. **Generate Scanner (one-time):**
```bash
jflex src/Scanner.flex
```

2. **Compile:**
```bash
javac src/*.java -d bin
```

3. **Run:**
```bash
java -cp bin JFlexScanner tests/test1.lang
```

---

## Features

### Manual Scanner
- DFA-based token recognition
- Longest match principle
- Accurate line and column tracking
- Comprehensive error handling

### JFlex Scanner
- Automatically generated from specification
- Optimized DFA transitions
- Same token output format as manual scanner

### Error Handling
- Invalid characters detection
- Malformed literals (trailing dots, excessive decimals)
- Invalid identifiers (wrong starting character, exceeding length)
- Unterminated strings and unclosed comments
- Error recovery: continues scanning after errors

### Bonus Features
- ✅ Nested multi-line comments support
- ✅ GitHub repository with commit history
- ✅ Symbol table with identifier tracking
- ✅ Comprehensive statistics display

---

## Project Structure

```
i230652-i230095-G/
├── src/                    # Source code
│   ├── ManualScanner.java  # Manual DFA-based scanner
│   ├── JFlexScanner.java   # JFlex scanner driver
│   ├── Scanner.flex        # JFlex specification
│   ├── Scanner.java        # Generated by JFlex
│   ├── Token.java          # Token class
│   ├── TokenType.java      # Token type enum
│   ├── SymbolTable.java    # Symbol table
│   └── ErrorHandler.java   # Error handler
├── docs/                   # Documentation
│   ├── Automata_Design.pdf # NFA/DFA diagrams
│   ├── Comparison.pdf      # Scanner comparison
│   ├── README.md           # Detailed documentation
│   └── LanguageGrammar.txt # Grammar specification
├── tests/                  # Test files
│   ├── test1.lang          # All valid tokens
│   ├── test2.lang          # Complex expressions
│   ├── test3.lang          # Strings/chars with escapes
│   ├── test4.lang          # Lexical errors
│   ├── test5.lang          # Comments
│   └── TestResults.txt     # Test results
└── README.md               # This file
```

---

## Testing

Run all test files:
```bash
java -cp bin ManualScanner tests/test1.lang
java -cp bin ManualScanner tests/test2.lang
java -cp bin ManualScanner tests/test3.lang
java -cp bin ManualScanner tests/test4.lang
java -cp bin ManualScanner tests/test5.lang
```

---

## Documentation

For detailed information, see:
- `docs/Automata_Design.pdf` - NFA/DFA diagrams and transition tables
- `docs/Comparison.pdf` - Manual vs JFlex scanner comparison
- `docs/LanguageGrammar.txt` - Complete lexical grammar
- `docs/README.md` - Additional documentation

---

## Authors

This lexical analyzer was developed as part of CS4031 - Compiler Construction course.

**Team Members:**
- Shazil Rehman (23i-0095)
- Abdul Mohaimin (23i-0652)

**Section:** G  
**Semester:** Spring 2026
