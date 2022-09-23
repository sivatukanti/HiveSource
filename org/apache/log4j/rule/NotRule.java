// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rule;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Stack;

public class NotRule extends AbstractRule
{
    static final long serialVersionUID = -6827159473117969306L;
    private final Rule rule;
    
    private NotRule(final Rule rule) {
        this.rule = rule;
    }
    
    public static Rule getRule(final Rule rule) {
        return new NotRule(rule);
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 1) {
            throw new IllegalArgumentException("Invalid NOT rule - expected one rule but received " + stack.size());
        }
        final Object o1 = stack.pop();
        if (o1 instanceof Rule) {
            final Rule p1 = (Rule)o1;
            return new NotRule(p1);
        }
        throw new IllegalArgumentException("Invalid NOT rule: - expected rule but received " + o1);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        if (matches == null) {
            return !this.rule.evaluate(event, null);
        }
        final Map tempMatches = new HashMap();
        final boolean result = !this.rule.evaluate(event, tempMatches);
        if (result) {
            for (final Map.Entry entry : tempMatches.entrySet()) {
                final Object key = entry.getKey();
                final Set value = entry.getValue();
                Set mainSet = matches.get(key);
                if (mainSet == null) {
                    mainSet = new HashSet();
                    matches.put(key, mainSet);
                }
                mainSet.addAll(value);
            }
        }
        return result;
    }
}
