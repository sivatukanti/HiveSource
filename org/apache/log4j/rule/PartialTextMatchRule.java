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

public class PartialTextMatchRule extends AbstractRule
{
    static final long serialVersionUID = 6963284773637727558L;
    private static final LoggingEventFieldResolver RESOLVER;
    private final String field;
    private final String value;
    
    private PartialTextMatchRule(final String field, final String value) {
        if (!PartialTextMatchRule.RESOLVER.isField(field)) {
            throw new IllegalArgumentException("Invalid partial text rule - " + field + " is not a supported field");
        }
        this.field = field;
        this.value = value;
    }
    
    public static Rule getRule(final String field, final String value) {
        return new PartialTextMatchRule(field, value);
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("invalid partial text rule - expected two parameters but received " + stack.size());
        }
        final String p2 = stack.pop().toString();
        final String p3 = stack.pop().toString();
        return new PartialTextMatchRule(p3, p2);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final Object p2 = PartialTextMatchRule.RESOLVER.getValue(this.field, event);
        final boolean result = p2 != null && this.value != null && p2.toString().toLowerCase().indexOf(this.value.toLowerCase()) > -1;
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
