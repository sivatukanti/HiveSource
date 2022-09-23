// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import javax.naming.NamingException;
import javax.naming.Binding;
import java.util.Iterator;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

public class NameEnumeration implements NamingEnumeration<NameClassPair>
{
    Iterator<Binding> _delegate;
    
    public NameEnumeration(final Iterator<Binding> e) {
        this._delegate = e;
    }
    
    public void close() throws NamingException {
    }
    
    public boolean hasMore() throws NamingException {
        return this._delegate.hasNext();
    }
    
    public NameClassPair next() throws NamingException {
        final Binding b = this._delegate.next();
        return new NameClassPair(b.getName(), b.getClassName(), true);
    }
    
    public boolean hasMoreElements() {
        return this._delegate.hasNext();
    }
    
    public NameClassPair nextElement() {
        final Binding b = this._delegate.next();
        return new NameClassPair(b.getName(), b.getClassName(), true);
    }
}
