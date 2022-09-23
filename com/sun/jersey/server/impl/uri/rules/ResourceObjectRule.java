// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriRule;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.api.uri.UriTemplate;

public final class ResourceObjectRule extends BaseRule
{
    private final Object resourceObject;
    
    public ResourceObjectRule(final UriTemplate template, final Object resourceObject) {
        super(template);
        this.resourceObject = resourceObject;
    }
    
    @Override
    public boolean accept(final CharSequence path, final Object resource, final UriRuleContext context) {
        this.pushMatch(context);
        if (context.isTracingEnabled()) {
            context.trace(String.format("accept resource: \"%s\" -> @Path(\"%s\") %s", context.getUriInfo().getMatchedURIs().get(0), this.getTemplate().getTemplate(), ReflectionHelper.objectToString(this.resourceObject)));
        }
        context.pushResource(this.resourceObject);
        UriRuleProbeProvider.ruleAccept(ResourceObjectRule.class.getSimpleName(), path, this.resourceObject);
        final Iterator<UriRule> matches = context.getRules(this.resourceObject.getClass()).match(path, context);
        while (matches.hasNext()) {
            if (matches.next().accept(path, this.resourceObject, context)) {
                return true;
            }
        }
        return false;
    }
}
