// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class DecoratedObjectFactory implements Iterable<Decorator>
{
    private static final Logger LOG;
    public static final String ATTR;
    private List<Decorator> decorators;
    
    public DecoratedObjectFactory() {
        this.decorators = new ArrayList<Decorator>();
    }
    
    public void addDecorator(final Decorator decorator) {
        DecoratedObjectFactory.LOG.debug("Adding Decorator: {}", decorator);
        this.decorators.add(decorator);
    }
    
    public void clear() {
        this.decorators.clear();
    }
    
    public <T> T createInstance(final Class<T> clazz) throws InstantiationException, IllegalAccessException {
        if (DecoratedObjectFactory.LOG.isDebugEnabled()) {
            DecoratedObjectFactory.LOG.debug("Creating Instance: " + clazz, new Object[0]);
        }
        final T o = clazz.newInstance();
        return this.decorate(o);
    }
    
    public <T> T decorate(final T obj) {
        T f = obj;
        for (int i = this.decorators.size() - 1; i >= 0; --i) {
            f = this.decorators.get(i).decorate(f);
        }
        return f;
    }
    
    public void destroy(final Object obj) {
        for (final Decorator decorator : this.decorators) {
            decorator.destroy(obj);
        }
    }
    
    public List<Decorator> getDecorators() {
        return Collections.unmodifiableList((List<? extends Decorator>)this.decorators);
    }
    
    @Override
    public Iterator<Decorator> iterator() {
        return this.decorators.iterator();
    }
    
    public void setDecorators(final List<? extends Decorator> decorators) {
        this.decorators.clear();
        if (decorators != null) {
            this.decorators.addAll(decorators);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(this.getClass().getName()).append("[decorators=");
        str.append(Integer.toString(this.decorators.size()));
        str.append("]");
        return str.toString();
    }
    
    static {
        LOG = Log.getLogger(DecoratedObjectFactory.class);
        ATTR = DecoratedObjectFactory.class.getName();
    }
}
