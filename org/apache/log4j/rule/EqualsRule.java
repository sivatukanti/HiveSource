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

public class EqualsRule extends AbstractRule
{
    static final long serialVersionUID = 1712851553477517245L;
    private static final LoggingEventFieldResolver RESOLVER;
    private final String value;
    private final String field;
    
    private EqualsRule(final String field, final String value) {
        if (!EqualsRule.RESOLVER.isField(field)) {
            throw new IllegalArgumentException("Invalid EQUALS rule - " + field + " is not a supported field");
        }
        this.field = field;
        this.value = value;
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Invalid EQUALS rule - expected two parameters but received " + stack.size());
        }
        final String p2 = stack.pop().toString();
        final String p3 = stack.pop().toString();
        return getRule(p3, p2);
    }
    
    public static Rule getRule(final String p1, final String p2) {
        if (p1.equalsIgnoreCase("LEVEL")) {
            return LevelEqualsRule.getRule(p2);
        }
        if (p1.equalsIgnoreCase("TIMESTAMP")) {
            return TimestampEqualsRule.getRule(p2);
        }
        return new EqualsRule(p1, p2);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final Object p2 = EqualsRule.RESOLVER.getValue(this.field, event);
        final boolean result = p2 != null && p2.toString().equals(this.value);
        if (result && matches != null) {
            Set entries = matches.get(this.field.toUpperCase());
            if (entries == null) {
                entries = new HashSet();
                matches.put(this.field.toUpperCase(), entries);
            }
            entries.add(this.value);
        }
        return result;
    }
    
    static {
        RESOLVER = LoggingEventFieldResolver.getInstance();
    }
}
