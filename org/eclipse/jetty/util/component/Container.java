// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.component;

import java.util.Collection;

public interface Container
{
    boolean addBean(final Object p0);
    
    Collection<Object> getBeans();
    
     <T> Collection<T> getBeans(final Class<T> p0);
    
     <T> T getBean(final Class<T> p0);
    
    boolean removeBean(final Object p0);
    
    void addEventListener(final Listener p0);
    
    void removeEventListener(final Listener p0);
    
    public interface InheritedListener extends Listener
    {
    }
    
    public interface Listener
    {
        void beanAdded(final Container p0, final Object p1);
        
        void beanRemoved(final Container p0, final Object p1);
    }
}
