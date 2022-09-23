// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.Iterator;
import org.apache.log4j.spi.LoggingEventFieldResolver;
import java.util.LinkedList;
import java.util.Vector;
import java.util.HashMap;
import java.util.Stack;
import java.util.Locale;
import java.util.List;
import java.util.Map;

public class InFixToPostFix
{
    private static final Map precedenceMap;
    private static final List operators;
    
    public String convert(final String expression) {
        return this.infixToPostFix(new CustomTokenizer(expression));
    }
    
    public static boolean isOperand(final String s) {
        final String symbol = s.toLowerCase(Locale.ENGLISH);
        return !InFixToPostFix.operators.contains(symbol);
    }
    
    boolean precedes(final String s1, final String s2) {
        final String symbol1 = s1.toLowerCase(Locale.ENGLISH);
        final String symbol2 = s2.toLowerCase(Locale.ENGLISH);
        if (!InFixToPostFix.precedenceMap.keySet().contains(symbol1)) {
            return false;
        }
        if (!InFixToPostFix.precedenceMap.keySet().contains(symbol2)) {
            return false;
        }
        final int index1 = InFixToPostFix.precedenceMap.get(symbol1);
        final int index2 = InFixToPostFix.precedenceMap.get(symbol2);
        final boolean precedesResult = index1 < index2;
        return precedesResult;
    }
    
    String infixToPostFix(final CustomTokenizer tokenizer) {
        final String space = " ";
        final StringBuffer postfix = new StringBuffer();
        final Stack stack = new Stack();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            boolean inText = (token.startsWith("'") && !token.endsWith("'")) || (token.startsWith("\"") && !token.endsWith("\""));
            final String quoteChar = token.substring(0, 1);
            if (inText) {
                while (inText && tokenizer.hasMoreTokens()) {
                    token = token + " " + tokenizer.nextToken();
                    inText = !token.endsWith(quoteChar);
                }
            }
            if ("(".equals(token)) {
                postfix.append(this.infixToPostFix(tokenizer));
                postfix.append(" ");
            }
            else {
                if (")".equals(token)) {
                    while (stack.size() > 0) {
                        postfix.append(stack.pop().toString());
                        postfix.append(" ");
                    }
                    return postfix.toString();
                }
                if (isOperand(token)) {
                    postfix.append(token);
                    postfix.append(" ");
                }
                else if (stack.size() > 0) {
                    final String peek = stack.peek().toString();
                    if (this.precedes(peek, token)) {
                        stack.push(token);
                    }
                    else {
                        boolean bypass = false;
                        do {
                            if (stack.size() > 0 && !this.precedes(stack.peek().toString(), token)) {
                                postfix.append(stack.pop().toString());
                                postfix.append(" ");
                            }
                            else {
                                bypass = true;
                            }
                        } while (!bypass);
                        stack.push(token);
                    }
                }
                else {
                    stack.push(token);
                }
            }
        }
        while (stack.size() > 0) {
            postfix.append(stack.pop().toString());
            postfix.append(" ");
        }
        return postfix.toString();
    }
    
    static {
        precedenceMap = new HashMap();
        (operators = new Vector()).add("<=");
        InFixToPostFix.operators.add(">=");
        InFixToPostFix.operators.add("!=");
        InFixToPostFix.operators.add("==");
        InFixToPostFix.operators.add("~=");
        InFixToPostFix.operators.add("||");
        InFixToPostFix.operators.add("&&");
        InFixToPostFix.operators.add("like");
        InFixToPostFix.operators.add("exists");
        InFixToPostFix.operators.add("!");
        InFixToPostFix.operators.add("<");
        InFixToPostFix.operators.add(">");
        InFixToPostFix.precedenceMap.put("<", new Integer(3));
        InFixToPostFix.precedenceMap.put(">", new Integer(3));
        InFixToPostFix.precedenceMap.put("<=", new Integer(3));
        InFixToPostFix.precedenceMap.put(">=", new Integer(3));
        InFixToPostFix.precedenceMap.put("!", new Integer(3));
        InFixToPostFix.precedenceMap.put("!=", new Integer(3));
        InFixToPostFix.precedenceMap.put("==", new Integer(3));
        InFixToPostFix.precedenceMap.put("~=", new Integer(3));
        InFixToPostFix.precedenceMap.put("like", new Integer(3));
        InFixToPostFix.precedenceMap.put("exists", new Integer(3));
        InFixToPostFix.precedenceMap.put("||", new Integer(2));
        InFixToPostFix.precedenceMap.put("&&", new Integer(2));
    }
    
    public static class CustomTokenizer
    {
        private LinkedList linkedList;
        
        public CustomTokenizer(final String input) {
            this.parseInput(input, this.linkedList = new LinkedList());
        }
        
        public void parseInput(final String input, final LinkedList linkedList) {
            final List keywords = LoggingEventFieldResolver.KEYWORD_LIST;
            keywords.remove("PROP.");
            int pos = 0;
            while (pos < input.length()) {
                if (this.nextValueIs(input, pos, "'") || this.nextValueIs(input, pos, "\"")) {
                    pos = this.handleQuotedString(input, pos, linkedList);
                }
                if (this.nextValueIs(input, pos, "PROP.")) {
                    pos = this.handleProperty(input, pos, linkedList);
                }
                boolean operatorFound = false;
                for (final String operator : InFixToPostFix.operators) {
                    if (this.nextValueIs(input, pos, operator)) {
                        operatorFound = true;
                        pos = this.handle(pos, linkedList, operator);
                    }
                }
                boolean keywordFound = false;
                for (final String keyword : keywords) {
                    if (this.nextValueIs(input, pos, keyword)) {
                        keywordFound = true;
                        pos = this.handle(pos, linkedList, keyword);
                    }
                }
                if (!operatorFound) {
                    if (keywordFound) {
                        continue;
                    }
                    if (this.nextValueIs(input, pos, ")")) {
                        pos = this.handle(pos, linkedList, ")");
                    }
                    else if (this.nextValueIs(input, pos, "(")) {
                        pos = this.handle(pos, linkedList, "(");
                    }
                    else if (this.nextValueIs(input, pos, " ")) {
                        ++pos;
                    }
                    else {
                        pos = this.handleText(input, pos, linkedList);
                    }
                }
            }
        }
        
        private boolean nextValueIs(final String input, final int pos, final String value) {
            return input.length() >= pos + value.length() && input.substring(pos, pos + value.length()).equalsIgnoreCase(value);
        }
        
        private int handle(final int pos, final LinkedList linkedList, final String value) {
            linkedList.add(value);
            return pos + value.length();
        }
        
        private int handleQuotedString(final String input, final int pos, final LinkedList linkedList) {
            final String quoteChar = input.substring(pos, pos + 1);
            final int nextSingleQuotePos = input.indexOf(quoteChar, pos + 1);
            if (nextSingleQuotePos < 0) {
                throw new IllegalArgumentException("Missing an end quote");
            }
            final String result = input.substring(pos, nextSingleQuotePos + 1);
            linkedList.add(result);
            return nextSingleQuotePos + 1;
        }
        
        private int handleText(final String input, final int pos, final LinkedList linkedList) {
            final StringBuffer text = new StringBuffer("");
            int newPos = pos;
            while (newPos < input.length()) {
                if (this.nextValueIs(input, newPos, " ")) {
                    linkedList.add(text);
                    return newPos;
                }
                if (this.nextValueIs(input, newPos, "(")) {
                    linkedList.add(text);
                    return newPos;
                }
                if (this.nextValueIs(input, newPos, ")")) {
                    linkedList.add(text);
                    return newPos;
                }
                for (final String operator : InFixToPostFix.operators) {
                    if (this.nextValueIs(input, newPos, operator)) {
                        linkedList.add(text);
                        return newPos;
                    }
                }
                text.append(input.substring(newPos, ++newPos));
            }
            if (!text.toString().trim().equals("")) {
                linkedList.add(text);
            }
            return newPos;
        }
        
        private int handleProperty(final String input, final int pos, final LinkedList linkedList) {
            int propertyPos = pos + "PROP.".length();
            final StringBuffer propertyName = new StringBuffer("PROP.");
            while (propertyPos < input.length()) {
                if (this.nextValueIs(input, propertyPos, " ")) {
                    linkedList.add(propertyName);
                    return propertyPos;
                }
                if (this.nextValueIs(input, propertyPos, "(")) {
                    linkedList.add(propertyName);
                    return propertyPos;
                }
                if (this.nextValueIs(input, propertyPos, ")")) {
                    linkedList.add(propertyName);
                    return propertyPos;
                }
                for (final String operator : InFixToPostFix.operators) {
                    if (this.nextValueIs(input, propertyPos, operator)) {
                        linkedList.add(propertyName);
                        return propertyPos;
                    }
                }
                propertyName.append(input.substring(propertyPos, ++propertyPos));
            }
            linkedList.add(propertyName);
            return propertyPos;
        }
        
        public boolean hasMoreTokens() {
            return this.linkedList.size() > 0;
        }
        
        public String nextToken() {
            return this.linkedList.remove().toString();
        }
    }
}
