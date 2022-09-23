// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

class ImmutableConfigurationInvocationHandler implements InvocationHandler
{
    private final Configuration wrappedConfiguration;
    
    public ImmutableConfigurationInvocationHandler(final Configuration conf) {
        if (conf == null) {
            throw new NullPointerException("Wrapped configuration must not be null!");
        }
        this.wrappedConfiguration = conf;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        try {
            return handleResult(method.invoke(this.wrappedConfiguration, args));
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
    
    private static Object handleResult(final Object result) {
        if (result instanceof Iterator) {
            return new ImmutableIterator((Iterator<?>)result);
        }
        return result;
    }
    
    private static class ImmutableIterator implements Iterator<Object>
    {
        private final Iterator<?> wrappedIterator;
        
        public ImmutableIterator(final Iterator<?> it) {
            this.wrappedIterator = it;
        }
        
        @Override
        public boolean hasNext() {
            return this.wrappedIterator.hasNext();
        }
        
        @Override
        public Object next() {
            return this.wrappedIterator.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove() operation not supported!");
        }
    }
}
