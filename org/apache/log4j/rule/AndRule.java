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

public class AndRule extends AbstractRule
{
    private final Rule firstRule;
    private final Rule secondRule;
    static final long serialVersionUID = -8233444426923854651L;
    
    private AndRule(final Rule first, final Rule second) {
        this.firstRule = first;
        this.secondRule = second;
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Invalid AND rule - expected two rules but received " + stack.size());
        }
        final Object o2 = stack.pop();
        final Object o3 = stack.pop();
        if (o2 instanceof Rule && o3 instanceof Rule) {
            final Rule p2 = (Rule)o2;
            final Rule p3 = (Rule)o3;
            return new AndRule(p3, p2);
        }
        throw new IllegalArgumentException("Invalid AND rule: " + o2 + "..." + o3);
    }
    
    public static Rule getRule(final Rule firstParam, final Rule secondParam) {
        return new AndRule(firstParam, secondParam);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        if (matches == null) {
            return this.firstRule.evaluate(event, null) && this.secondRule.evaluate(event, null);
        }
        final Map tempMatches1 = new HashMap();
        final Map tempMatches2 = new HashMap();
        final boolean result = this.firstRule.evaluate(event, tempMatches1) && this.secondRule.evaluate(event, tempMatches2);
        if (result) {
            for (final Map.Entry entry : tempMatches1.entrySet()) {
                final Object key = entry.getKey();
                final Set value = entry.getValue();
                Set mainSet = matches.get(key);
                if (mainSet == null) {
                    mainSet = new HashSet();
                    matches.put(key, mainSet);
                }
                mainSet.addAll(value);
            }
            for (final Map.Entry entry : tempMatches2.entrySet()) {
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
