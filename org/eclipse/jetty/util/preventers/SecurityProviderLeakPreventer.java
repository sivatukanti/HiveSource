// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import java.security.Security;

public class SecurityProviderLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        Security.getProviders();
    }
}
