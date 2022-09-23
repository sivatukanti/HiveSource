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

public class ExistsRule extends AbstractRule
{
    static final long serialVersionUID = -5386265224649967464L;
    private static final LoggingEventFieldResolver RESOLVER;
    private final String field;
    
    private ExistsRule(final String fld) {
        if (!ExistsRule.RESOLVER.isField(fld)) {
            throw new IllegalArgumentException("Invalid EXISTS rule - " + fld + " is not a supported field");
        }
        this.field = fld;
    }
    
    public static Rule getRule(final String field) {
        return new ExistsRule(field);
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 1) {
            throw new IllegalArgumentException("Invalid EXISTS rule - expected one parameter but received " + stack.size());
        }
        return new ExistsRule(stack.pop().toString());
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        final Object p2 = ExistsRule.RESOLVER.getValue(this.field, event);
        final boolean result = p2 != null && !p2.toString().equals("");
        if (result && matches != null) {
            Set entries = matches.get(this.field.toUpperCase());
            if (entries == null) {
                entries = new HashSet();
                matches.put(this.field.toUpperCase(), entries);
            }
            entries.add(p2);
        }
        return result;
    }
    
    static {
        RESOLVER = LoggingEventFieldResolver.getInstance();
    }
}
