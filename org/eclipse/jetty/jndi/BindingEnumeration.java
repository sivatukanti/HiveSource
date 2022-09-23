// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi;

import javax.naming.NamingException;
import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;

public class BindingEnumeration implements NamingEnumeration<Binding>
{
    Iterator<Binding> _delegate;
    
    public BindingEnumeration(final Iterator<Binding> e) {
        this._delegate = e;
    }
    
    public void close() throws NamingException {
    }
    
    public boolean hasMore() throws NamingException {
        return this._delegate.hasNext();
    }
    
    public Binding next() throws NamingException {
        final Binding b = this._delegate.next();
        return new Binding(b.getName(), b.getClassName(), b.getObject(), true);
    }
    
    public boolean hasMoreElements() {
        return this._delegate.hasNext();
    }
    
    public Binding nextElement() {
        final Binding b = this._delegate.next();
        return new Binding(b.getName(), b.getClassName(), b.getObject(), true);
    }
}
