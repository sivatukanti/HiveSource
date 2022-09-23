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

public class NotEqualsRule extends AbstractRule
{
    static final long serialVersionUID = -1135478467213793211L;
    private static final LoggingEventFieldResolver RESOLVER;
    private final String field;
    private final String value;
    
    private NotEqualsRule(final String field, final String value) {
        if (!NotEqualsRule.RESOLVER.isField(field)) {
            throw new IllegalArgumentException("Invalid NOT EQUALS rule - " + field + " is not a supported field");
        }
        this.field = field;
        this.value = value;
    }
    
    public static Rule getRule(final String field, final String value) {
        if (field.equalsIgnoreCase("LEVEL")) {
            return NotLevelEqualsRule.getRule(value);
        }
        return new NotEqualsRule(field, value);
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Invalid NOT EQUALS rule - expected two parameters but received " + stack.size());
        }
        final String p2 = stack.pop().toString();
        final String p3 = stack.pop().toString();
        if (p3.equalsIgnoreCase("LEVEL")) {
            return NotLevelEqualsRule.getRule(p2);
        }
        return new NotEqualsRule(p3, p2);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final Object p2 = NotEqualsRule.RESOLVER.getValue(this.field, event);
        final boolean result = p2 != null && !p2.toString().equals(this.value);
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
