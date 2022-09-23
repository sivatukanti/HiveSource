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

public final class ResourceClassRule extends BaseRule
{
    private final Class resourceClass;
    
    public ResourceClassRule(final UriTemplate template, final Class resourceClass) {
        super(template);
        this.resourceClass = resourceClass;
    }
    
    @Override
    public boolean accept(final CharSequence path, Object resource, final UriRuleContext context) {
        this.pushMatch(context);
        resource = context.getResource(this.resourceClass);
        context.pushResource(resource);
        if (context.isTracingEnabled()) {
            context.trace(String.format("accept resource: \"%s\" -> @Path(\"%s\") %s", context.getUriInfo().getMatchedURIs().get(0), this.getTemplate().getTemplate(), ReflectionHelper.objectToString(resource)));
        }
        UriRuleProbeProvider.ruleAccept(ResourceClassRule.class.getSimpleName(), path, resource);
        final Iterator<UriRule> matches = context.getRules(this.resourceClass).match(path, context);
        while (matches.hasNext()) {
            if (matches.next().accept(path, resource, context)) {
                return true;
            }
        }
        return false;
    }
}
