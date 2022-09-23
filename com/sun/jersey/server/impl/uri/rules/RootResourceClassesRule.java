// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.server.impl.uri.PathPattern;
import java.util.Map;
import com.sun.jersey.spi.uri.rules.UriRules;
import com.sun.jersey.spi.uri.rules.UriRule;

public final class RootResourceClassesRule implements UriRule
{
    private final UriRules<UriRule> rules;
    
    public RootResourceClassesRule(final Map<PathPattern, UriRule> rulesMap) {
        this.rules = UriRulesFactory.create(rulesMap);
    }
    
    @Override
    public boolean accept(final CharSequence path, final Object resource, final UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(RootResourceClassesRule.class.getSimpleName(), path, resource);
        if (context.isTracingEnabled()) {
            context.trace("accept root resource classes: \"" + (Object)path + "\"");
        }
        final Iterator<UriRule> matches = this.rules.match(path, context);
        while (matches.hasNext()) {
            if (matches.next().accept(path, resource, context)) {
                return true;
            }
        }
        return false;
    }
}
