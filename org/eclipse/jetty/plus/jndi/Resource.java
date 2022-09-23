// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jndi;

import javax.naming.NamingException;

public class Resource extends NamingEntry
{
    public Resource(final Object scope, final String jndiName, final Object objToBind) throws NamingException {
        super(scope, jndiName);
        this.save(objToBind);
    }
    
    public Resource(final String jndiName, final Object objToBind) throws NamingException {
        super(jndiName);
        this.save(objToBind);
    }
}
