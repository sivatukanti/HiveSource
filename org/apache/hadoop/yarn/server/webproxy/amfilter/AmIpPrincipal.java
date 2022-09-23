// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy.amfilter;

import java.security.Principal;

public class AmIpPrincipal implements Principal
{
    private final String name;
    
    public AmIpPrincipal(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
