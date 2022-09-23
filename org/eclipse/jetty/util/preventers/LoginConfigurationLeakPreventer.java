// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

public class LoginConfigurationLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        try {
            Class.forName("javax.security.auth.login.Configuration", true, loader);
        }
        catch (ClassNotFoundException e) {
            LoginConfigurationLeakPreventer.LOG.warn(e);
        }
    }
}
