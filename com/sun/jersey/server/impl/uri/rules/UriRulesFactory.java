// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.server.impl.uri.rules.automata.AutomataMatchingUriTemplateRules;
import java.util.Iterator;
import java.util.Collection;
import com.sun.jersey.api.uri.UriPattern;
import java.util.ArrayList;
import java.util.List;
import com.sun.jersey.spi.uri.rules.UriRules;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.server.impl.uri.PathPattern;
import java.util.Map;

public final class UriRulesFactory
{
    private UriRulesFactory() {
    }
    
    public static UriRules<UriRule> create(final Map<PathPattern, UriRule> rulesMap) {
        return create(rulesMap, null);
    }
    
    public static UriRules<UriRule> create(final Map<PathPattern, UriRule> rulesMap, final List<PatternRulePair<UriRule>> rules) {
        final List<PatternRulePair<UriRule>> l = new ArrayList<PatternRulePair<UriRule>>();
        for (final Map.Entry<PathPattern, UriRule> e : rulesMap.entrySet()) {
            l.add(new PatternRulePair<UriRule>(e.getKey(), e.getValue()));
        }
        if (rules != null) {
            l.addAll(rules);
        }
        return create(l);
    }
    
    public static UriRules<UriRule> create(final List<PatternRulePair<UriRule>> rules) {
        if (rules.size() < Integer.MAX_VALUE) {
            return new AtomicMatchingPatterns<UriRule>(rules);
        }
        return new AutomataMatchingUriTemplateRules<UriRule>(rules);
    }
}
