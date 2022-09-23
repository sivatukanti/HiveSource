// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.uri.UriPattern;

public class PatternRulePair<R>
{
    public final UriPattern p;
    public final R r;
    
    public PatternRulePair(final UriPattern p, final R r) {
        this.p = p;
        this.r = r;
    }
}
