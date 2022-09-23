// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Stack;
import org.apache.log4j.spi.LoggingEventFieldResolver;

public class InequalityRule extends AbstractRule
{
    static final long serialVersionUID = -5592986598528885122L;
    private static final LoggingEventFieldResolver RESOLVER;
    private final String field;
    private final String value;
    private final String inequalitySymbol;
    
    private InequalityRule(final String inequalitySymbol, final String field, final String value) {
        this.inequalitySymbol = inequalitySymbol;
        if (!InequalityRule.RESOLVER.isField(field)) {
            throw new IllegalArgumentException("Invalid " + inequalitySymbol + " rule - " + field + " is not a supported field");
        }
        this.field = field;
        this.value = value;
    }
    
    public static Rule getRule(final String inequalitySymbol, final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Invalid " + inequalitySymbol + " rule - expected two parameters but received " + stack.size());
        }
        final String p2 = stack.pop().toString();
        final String p3 = stack.pop().toString();
        return getRule(inequalitySymbol, p3, p2);
    }
    
    public static Rule getRule(final String inequalitySymbol, final String field, final String value) {
        if (field.equalsIgnoreCase("LEVEL")) {
            return LevelInequalityRule.getRule(inequalitySymbol, value);
        }
        if (field.equalsIgnoreCase("TIMESTAMP")) {
            return TimestampInequalityRule.getRule(inequalitySymbol, value);
        }
        return new InequalityRule(inequalitySymbol, field, value);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        long first = 0L;
        try {
            first = new Long(InequalityRule.RESOLVER.getValue(this.field, event).toString());
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        long second = 0L;
        try {
            second = new Long(this.value);
        }
        catch (NumberFormatException nfe2) {
            return false;
        }
        boolean result = false;
        if ("<".equals(this.inequalitySymbol)) {
            result = (first < second);
        }
        else if (">".equals(this.inequalitySymbol)) {
            result = (first > second);
        }
        else if ("<=".equals(this.inequalitySymbol)) {
            result = (first <= second);
        }
        else if (">=".equals(this.inequalitySymbol)) {
            result = (first >= second);
        }
        if (result && matches != null) {
            Set entries = matches.get(this.field.toUpperCase());
            if (entries == null) {
                entries = new HashSet();
                matches.put(this.field.toUpperCase(), entries);
            }
            entries.add(String.valueOf(first));
        }
        return result;
    }
    
    static {
        RESOLVER = LoggingEventFieldResolver.getInstance();
    }
}
