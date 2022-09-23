// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jndi;

import javax.naming.NamingException;

public class Link extends NamingEntry
{
    private final String _link;
    
    public Link(final Object scope, final String jndiName, final String link) throws NamingException {
        super(scope, jndiName);
        this.save(link);
        this._link = link;
    }
    
    public Link(final String jndiName, final String link) throws NamingException {
        super(jndiName);
        this.save(link);
        this._link = link;
    }
    
    @Override
    public void bindToENC(final String localName) throws NamingException {
        throw new UnsupportedOperationException("Method not supported for Link objects");
    }
    
    public String getLink() {
        return this._link;
    }
}
