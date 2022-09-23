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

public class OrRule extends AbstractRule
{
    static final long serialVersionUID = 2088765995061413165L;
    private final Rule rule1;
    private final Rule rule2;
    
    private OrRule(final Rule firstParam, final Rule secondParam) {
        this.rule1 = firstParam;
        this.rule2 = secondParam;
    }
    
    public static Rule getRule(final Rule firstParam, final Rule secondParam) {
        return new OrRule(firstParam, secondParam);
    }
    
    public static Rule getRule(final Stack stack) {
        if (stack.size() < 2) {
            throw new IllegalArgumentException("Invalid OR rule - expected two rules but received " + stack.size());
        }
        final Object o2 = stack.pop();
        final Object o3 = stack.pop();
        if (o2 instanceof Rule && o3 instanceof Rule) {
            final Rule p2 = (Rule)o2;
            final Rule p3 = (Rule)o3;
            return new OrRule(p3, p2);
        }
        throw new IllegalArgumentException("Invalid OR rule: " + o2 + "..." + o3);
    }
    
    public boolean evaluate(final LoggingEvent event, final Map matches) {
        if (matches == null) {
            return this.rule1.evaluate(event, null) || this.rule2.evaluate(event, null);
        }
        final Map tempMatches1 = new HashMap();
        final Map tempMatches2 = new HashMap();
        final boolean result1 = this.rule1.evaluate(event, tempMatches1);
        final boolean result2 = this.rule2.evaluate(event, tempMatches2);
        final boolean result3 = result1 || result2;
        if (result3) {
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
        return result3;
    }
}
