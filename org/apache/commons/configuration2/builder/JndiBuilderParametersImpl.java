// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import javax.naming.Context;

public class JndiBuilderParametersImpl extends BasicBuilderParameters implements JndiBuilderProperties<JndiBuilderParametersImpl>
{
    private static final String PROP_CONTEXT = "context";
    private static final String PROP_PREFIX = "prefix";
    
    @Override
    public JndiBuilderParametersImpl setContext(final Context ctx) {
        this.storeProperty("context", ctx);
        return this;
    }
    
    @Override
    public JndiBuilderParametersImpl setPrefix(final String p) {
        this.storeProperty("prefix", p);
        return this;
    }
}
