// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class AnnotationIntrospector
{
    protected List<IntrospectableAnnotationHandler> _handlers;
    
    public AnnotationIntrospector() {
        this._handlers = new ArrayList<IntrospectableAnnotationHandler>();
    }
    
    public void registerHandler(final IntrospectableAnnotationHandler handler) {
        this._handlers.add(handler);
    }
    
    public void introspect(final Class clazz) {
        if (this._handlers == null) {
            return;
        }
        if (clazz == null) {
            return;
        }
        for (final IntrospectableAnnotationHandler handler : this._handlers) {
            try {
                handler.handle(clazz);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
    }
    
    public abstract static class AbstractIntrospectableAnnotationHandler implements IntrospectableAnnotationHandler
    {
        private boolean _introspectAncestors;
        
        public abstract void doHandle(final Class p0);
        
        public AbstractIntrospectableAnnotationHandler(final boolean introspectAncestors) {
            this._introspectAncestors = introspectAncestors;
        }
        
        public void handle(final Class clazz) {
            for (Class c = clazz; c != null && !c.equals(Object.class); c = c.getSuperclass()) {
                this.doHandle(c);
                if (!this._introspectAncestors) {
                    break;
                }
            }
        }
    }
    
    public interface IntrospectableAnnotationHandler
    {
        void handle(final Class p0);
    }
}
