// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.LinkedList;
import java.util.Stack;
import java.util.Locale;
import java.util.Collection;

public final class RuleFactory
{
    private static final RuleFactory FACTORY;
    private static final Collection RULES;
    private static final String AND_RULE = "&&";
    private static final String OR_RULE = "||";
    private static final String NOT_RULE = "!";
    private static final String NOT_EQUALS_RULE = "!=";
    private static final String EQUALS_RULE = "==";
    private static final String PARTIAL_TEXT_MATCH_RULE = "~=";
    private static final String LIKE_RULE = "like";
    private static final String EXISTS_RULE = "exists";
    private static final String LESS_THAN_RULE = "<";
    private static final String GREATER_THAN_RULE = ">";
    private static final String LESS_THAN_EQUALS_RULE = "<=";
    private static final String GREATER_THAN_EQUALS_RULE = ">=";
    
    private RuleFactory() {
    }
    
    public static RuleFactory getInstance() {
        return RuleFactory.FACTORY;
    }
    
    public boolean isRule(final String symbol) {
        return symbol != null && RuleFactory.RULES.contains(symbol.toLowerCase(Locale.ENGLISH));
    }
    
    public Rule getRule(final String symbol, final Stack stack) {
        if ("&&".equals(symbol)) {
            return AndRule.getRule(stack);
        }
        if ("||".equals(symbol)) {
            return OrRule.getRule(stack);
        }
        if ("!".equals(symbol)) {
            return NotRule.getRule(stack);
        }
        if ("!=".equals(symbol)) {
            return NotEqualsRule.getRule(stack);
        }
        if ("==".equals(symbol)) {
            return EqualsRule.getRule(stack);
        }
        if ("~=".equals(symbol)) {
            return PartialTextMatchRule.getRule(stack);
        }
        if (RuleFactory.RULES.contains("like") && "like".equalsIgnoreCase(symbol)) {
            return LikeRule.getRule(stack);
        }
        if ("exists".equalsIgnoreCase(symbol)) {
            return ExistsRule.getRule(stack);
        }
        if ("<".equals(symbol)) {
            return InequalityRule.getRule("<", stack);
        }
        if (">".equals(symbol)) {
            return InequalityRule.getRule(">", stack);
        }
        if ("<=".equals(symbol)) {
            return InequalityRule.getRule("<=", stack);
        }
        if (">=".equals(symbol)) {
            return InequalityRule.getRule(">=", stack);
        }
        throw new IllegalArgumentException("Invalid rule: " + symbol);
    }
    
    static {
        FACTORY = new RuleFactory();
        (RULES = new LinkedList()).add("&&");
        RuleFactory.RULES.add("||");
        RuleFactory.RULES.add("!");
        RuleFactory.RULES.add("!=");
        RuleFactory.RULES.add("==");
        RuleFactory.RULES.add("~=");
        RuleFactory.RULES.add("like");
        RuleFactory.RULES.add("exists");
        RuleFactory.RULES.add("<");
        RuleFactory.RULES.add(">");
        RuleFactory.RULES.add("<=");
        RuleFactory.RULES.add(">=");
    }
}
