/*
 * Copyright 2002-2006 The Apache Software Foundation.
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
/* Generated By:JJTree&JavaCC: Do not edit this line. ParserConstants.java */
package org.apache.commons.jexl.parser;

public interface ParserConstants {

  int EOF = 0;
  int COMMENT = 1;
  int INTEGER_LITERAL = 7;
  int FLOAT_LITERAL = 8;
  int IDENTIFIER = 58;
  int LETTER = 59;
  int DIGIT = 60;
  int STRING_LITERAL = 61;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "<COMMENT>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<INTEGER_LITERAL>",
    "<FLOAT_LITERAL>",
    "\"{\"",
    "\"}\"",
    "\"empty\"",
    "\"(\"",
    "\")\"",
    "\"size\"",
    "\"=\"",
    "\"||\"",
    "\"or\"",
    "\"&&\"",
    "\"and\"",
    "\"|\"",
    "\"^\"",
    "\"&\"",
    "\"==\"",
    "\"eq\"",
    "\"!=\"",
    "\"ne\"",
    "\"<\"",
    "\"lt\"",
    "\">\"",
    "\"gt\"",
    "\"<=\"",
    "\"le\"",
    "\">=\"",
    "\"ge\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"div\"",
    "\"%\"",
    "\"mod\"",
    "\"~\"",
    "\"!\"",
    "\"not\"",
    "\"null\"",
    "\"true\"",
    "\"false\"",
    "\";\"",
    "\"if\"",
    "\"else\"",
    "\"while\"",
    "\"foreach\"",
    "\"in\"",
    "\",\"",
    "\"[\"",
    "\"]\"",
    "\".\"",
    "<IDENTIFIER>",
    "<LETTER>",
    "<DIGIT>",
    "<STRING_LITERAL>",
  };

}
