# Automata Design for Lexical Analyzer

## 1. Regular Expressions
- **Keywords:** `start|finish|loop|condition|declare|output|input|function|return|break|continue|else`
- **Identifiers:** `[A-Z][a-z0-9_]{0,30}`
- **Integers:** `[+-]?[0-9]+`
- **Floats:** `[+-]?[0-9]+\.[0-9]{1,6}([eE][+-]?[0-9]+)?`
- **Strings:** `"([^"\\\n]|\\.)*"`
- **Operators:** `\*\*|==|!=|<=|>=|&&|\|\||\+\+|--|\+=|-=|\*=|\/=|\+|-|\*|\/|%|=|!|<|>`

## 2. NFA/DFA Diagrams (Conceptual)

### Integer Literal
- **S0** --(+/-)--> **S1** --(digit)--> **S2 (Accepting)**
- **S0** --(digit)--> **S2 (Accepting)**
- **S2** --(digit)--> **S2 (Accepting)**

### Floating Point Literal
- **S2 (IntegerPart)** --(.)--> **S3** --(digit)--> **S4 (Accepting)**
- **S4** --(digit)--> **S4 (Accepting)**
- **S4** --(e/E)--> **S5** --(+/-)--> **S6** --(digit)--> **S7 (Accepting)**

### Identifier
- **S0** --(A-Z)--> **S1 (Accepting)**
- **S1** --(a-z/0-9/_)--> **S1 (Accepting)**
- (Transition to Error if length > 31 or first char is not uppercase)

## 3. Transition Tables
(The implementation in ManualScanner.java follows these transitions programmatically.)

| State | Input: [A-Z] | Input: [a-z] | Input: [0-9] | Input: . | Input: # | ... |
|-------|--------------|--------------|--------------|----------|----------|-----|
| START | Identifier   | Error        | Integer      | Error    | Comment  | ... |
| INT   | -            | -            | INT          | FLOAT    | -        | ... |
| ID    | ID           | ID           | ID           | -        | -        | ... |

## 4. DFA Minimization
Our DFA is designed to be minimal by directly merging states that lead to the same token type and have identical outgoing transitions.
For example, all keyword prefixes that are not themselves keywords are merged until the exact match is found or it defaults to an Identifier state.
