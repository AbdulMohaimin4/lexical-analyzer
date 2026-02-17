/**
 * JFlex Specification for Assignment 01
 */

import java.io.*;

%%

%class Scanner
%unicode
%line
%column
%type Token

%{
  private ErrorHandler errorHandler;
  private SymbolTable symbolTable;

  public Scanner(Reader in, ErrorHandler errorHandler, SymbolTable symbolTable) {
    this(in);
    this.errorHandler = errorHandler;
    this.symbolTable = symbolTable;
  }

  private Token token(TokenType type) {
    return new Token(type, yytext(), yyline + 1, yycolumn + 1);
  }

  private void reportError(String type, String reason) {
    errorHandler.reportError(type, yyline + 1, yycolumn + 1, yytext(), reason);
  }
%}

/* Macro Definitions */
LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]

Identifier     = [A-Z][a-z0-9_]{0,30}
IntegerLiteral = [+-]?[0-9]+
FloatLiteral   = [+-]?[0-9]+\.[0-9]{1,6}([eE][+-]?[0-9]+)?

StringLiteral  = \"([^\r\n\"\\]|\\([\"\\ntr]))*\"
CharLiteral    = '([^\r\n'\\]|\\(['\\ntr]))'

SingleLineComment = ##[^\r\n]*
MultiLineComment  = #\* ([^*] | \*+ [^#])* \*+#

%%

<YYINITIAL> {
  /* Keywords */
  "start"      { return token(TokenType.KEYWORD); }
  "finish"     { return token(TokenType.KEYWORD); }
  "loop"       { return token(TokenType.KEYWORD); }
  "condition"  { return token(TokenType.KEYWORD); }
  "declare"    { return token(TokenType.KEYWORD); }
  "output"     { return token(TokenType.KEYWORD); }
  "input"      { return token(TokenType.KEYWORD); }
  "function"   { return token(TokenType.KEYWORD); }
  "return"     { return token(TokenType.KEYWORD); }
  "break"      { return token(TokenType.KEYWORD); }
  "continue"   { return token(TokenType.KEYWORD); }
  "else"       { return token(TokenType.KEYWORD); }

  /* Booleans */
  "true"       { return token(TokenType.BOOLEAN_LITERAL); }
  "false"      { return token(TokenType.BOOLEAN_LITERAL); }

  /* Operators */
  "**" | "==" | "!=" | "<=" | ">=" | "&&" | "||" | "++" | "--" | "+=" | "-=" | "*=" | "/=" |
  "+" | "-" | "*" | "/" | "%" | "=" | "<" | ">" | "!" 
               { return token(TokenType.OPERATOR); }

  /* Punctuators */
  "(" | ")" | "{" | "}" | "[" | "]" | "," | ";" | ":"
               { return token(TokenType.PUNCTUATOR); }

  /* Identifiers */
  {Identifier} { 
      symbolTable.insert(yytext(), "Identifier", yyline + 1, yycolumn + 1);
      return token(TokenType.IDENTIFIER); 
  }

  /* Literals */
  {IntegerLiteral} { return token(TokenType.INTEGER_LITERAL); }
  {FloatLiteral}   { return token(TokenType.FLOAT_LITERAL); }
  {StringLiteral}  { return token(TokenType.STRING_LITERAL); }
  {CharLiteral}    { return token(TokenType.CHARACTER_LITERAL); }

  /* Whitespace & Comments */
  {WhiteSpace}        { /* skip */ }
  {SingleLineComment} { /* skip */ }
  {MultiLineComment}  { /* skip */ }

  /* Errors */
  \"([^\r\n\"\\]|\\([\"\\ntr]))* { reportError("Unterminated String", "String reaches end of line"); }
  #\* ([^*] | \*+ [^#])*         { reportError("Unclosed Comment", "Multi-line comment was not closed"); }
  [a-z][a-z0-9_]*                { reportError("Invalid Identifier", "Must start with uppercase"); }
  [+-]?[0-9]*\.[0-9]{7,}         { reportError("Malformed Float", "Too many decimal places"); }
  [+-]?[0-9]*\.                  { reportError("Malformed Float", "Trailing dot"); }
  
  .                              { reportError("Invalid Character", "Character not recognized"); }
}

<<EOF>>                          { return token(TokenType.EOF); }
