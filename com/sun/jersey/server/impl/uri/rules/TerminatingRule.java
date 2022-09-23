// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import javax.ws.rs.WebApplicationException;
import com.sun.jersey.server.probes.UriRuleProbeProvider;
import com.sun.jersey.spi.uri.rules.UriRuleContext;
import com.sun.jersey.spi.uri.rules.UriRule;

public class TerminatingRule implements UriRule
{
    @Override
    public final boolean accept(final CharSequence path, final Object resource, final UriRuleContext context) {
        UriRuleProbeProvider.ruleAccept(TerminatingRule.class.getSimpleName(), path, resource);
        if (context.isTracingEnabled()) {
            context.trace("accept termination (matching failure): \"" + (Object)path + "\"");
        }
        if (context.getResponse().isResponseSet()) {
            throw new WebApplicationException(context.getResponse().getResponse());
        }
        return false;
    }
}
