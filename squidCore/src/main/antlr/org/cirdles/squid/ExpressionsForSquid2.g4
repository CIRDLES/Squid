/* 
 * Copyright 2006-2017 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** Simple statically-typed language with functions and variables
 *  based on  "Language Implementation Patterns" book.
 * https://media.pragprog.com/titles/tpantlr2/code/examples/Cymbol.g4
 */

grammar ExpressionsForSquid2;

@header {
    package org.cirdles.squid;
} 

/*file:   (functionDecl | varDecl)+ ;

varDecl
    :   type ID ('=' expr)? ';'
    ;
type:   'float' | 'int' | 'void' ; // user-defined types

functionDecl
    :   type FUNCTION '(' formalParameters? ')' block // "void f(int x) {...}"
    ;
formalParameters
    :   formalParameter (',' formalParameter)*
    ;
formalParameter
    :   type ID
    ;

block:  '{' stat* '}' ;   // possibly empty statement block
stat:   block
    |   varDecl
    |   'if' expr 'then' stat ('else' stat)?
    |   'return' expr? ';' 
    |   expr '=' expr ';' // assignment
    |   expr ';'          // func call
    ;
*/

// http://meri-stuff.blogspot.com/2011/09/antlr-tutorial-expression-language.html

expr:   FUNCTION '(' exprList+ ')'    // func call like f(), f(x), f(1,2) switched ? to + to require 1 arg min
    |   '(' expr ')'
//    |   ID '[' expr ']'         // array index like a[i], a[i][j]
    |   '-' expr                // unary minus
    |   '!' expr                // boolean not
    |   expr ('*'|'/') expr
    |   expr ('+'|'-') expr
    |   expr ('^') expr
    |   expr '==' expr          // equality comparison (lowest priority op)
    |   expr '<' expr          // less than comparison (lowest priority op)
    |   ARRAY_CALL
    |   NAMED_EXPRESSION
    |   ID                      // variable reference
    |   INT
    |   FLOAT
    
    ;
exprList : expr (',' expr)* ;   // arg list

FUNCTION : 
    'AgePb76' | 'agePb76' | 
    'And' | 'and' | 
    'ConcordiaTW' | 'concordiaTW' |
    'Exp' | 'exp' |  
    'If' | 'if' | 
    'Ln' | 'ln' | 
    'Sqrt' | 'sqrt' | 
    'RobReg' | 'robReg' | 'robreg' |
    'SqBiweight' | 'sqBiweight'
    'SqWtAvg' | 'sqWtAvg';

ARRAY_CALL : (ID | NAMED_EXPRESSION) ('[' INT '][' INT ']');       // array index like a[i], a[i][j]

NAMED_EXPRESSION : '[' '"' ID (ID | '/' | ' ')* '"' ']' ;

ID  :   (LETTER | [0-9]) (LETTER | [0-9])* ;
fragment
LETTER : [a-zA-Z_] ;


INT :   [0-9]+ ;

INTEGER:                '0' | ([1-9][0-9]*);

FLOAT :              ('0' | ([1-9][0-9]*)) ('.' [0-9]*)? Exponent? ;
 
fragment
Exponent : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;


WS  :   [ \t\n\r]+ -> skip ;

SL_COMMENT
    :   '//' .*? '\n' -> skip
    ;