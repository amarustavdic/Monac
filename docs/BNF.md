```BNF
<translation-unit> ::= {<function-definition>}*

<function-definition> ::= <nodeType-specifier> <identifier> "(" ")" <compound-statement>

<nodeType-specifier> ::= "void" | "char" | "int"

<compound-statement> ::= "{" {<statement>}* "}"

<statement> ::= <expression-statement>
              | <compound-statement>
              | <selection-statement>
              | <iteration-statement>
              | <jump-statement>

<expression-statement> ::= {<expression>}? ";"

<expression> ::= <assignment-expression>
               | <expression> "," <assignment-expression>

<assignment-expression> ::= <identifier> "=" <expression>

<additive-expression> ::= <multiplicative-expression>
                        | <additive-expression> "+" <multiplicative-expression>
                        | <additive-expression> "-" <multiplicative-expression>

<multiplicative-expression> ::= <unary-expression>
                              | <multiplicative-expression> "*" <unary-expression>
                              | <multiplicative-expression> "/" <unary-expression>

<unary-expression> ::= <postfix-expression>
                     | "++" <unary-expression>
                     | "--" <unary-expression>
                     | <unary-operator> <cast-expression>

<postfix-expression> ::= <primary-expression>
                       | <postfix-expression> "(" ")"

<primary-expression> ::= <identifier>
                       | <constant>
                       | "(" <expression> ")"

<constant> ::= <integer-constant>
             | <character-constant>

<unary-operator> ::= "&" | "*" | "+" | "-" | "~" | "!"

<selection-statement> ::= "if" "(" <expression> ")" <statement>
                        | "if" "(" <expression> ")" <statement> "else" <statement>

<iteration-statement> ::= "while" "(" <expression> ")" <statement>
                        | "for" "(" {<expression>}? ";" {<expression>}? ";" {<expression>}? ")" <statement>

<jump-statement> ::= "return" {<expression>}? ";"

<identifier> ::= [a-zA-Z_][a-zA-Z0-9_]*
<integer-constant> ::= [0-9]+
<character-constant> ::= "'" . "'"
```