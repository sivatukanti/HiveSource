// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

public class LDAPLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        try {
            Class.forName("com.sun.jndi.LdapPoolManager", true, loader);
        }
        catch (ClassNotFoundException e) {
            LDAPLeakPreventer.LOG.ignore(e);
        }
    }
}
