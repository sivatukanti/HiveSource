// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collection;

public class DefaultParametersManager
{
    private final Collection<DefaultHandlerData> defaultHandlers;
    
    public DefaultParametersManager() {
        this.defaultHandlers = new CopyOnWriteArrayList<DefaultHandlerData>();
    }
    
    public <T> void registerDefaultsHandler(final Class<T> paramsClass, final DefaultParametersHandler<? super T> handler) {
        this.registerDefaultsHandler(paramsClass, handler, null);
    }
    
    public <T> void registerDefaultsHandler(final Class<T> paramsClass, final DefaultParametersHandler<? super T> handler, final Class<?> startClass) {
        if (paramsClass == null) {
            throw new IllegalArgumentException("Parameters class must not be null!");
        }
        if (handler == null) {
            throw new IllegalArgumentException("DefaultParametersHandler must not be null!");
        }
        this.defaultHandlers.add(new DefaultHandlerData(handler, paramsClass, startClass));
    }
    
    public void unregisterDefaultsHandler(final DefaultParametersHandler<?> handler) {
        this.unregisterDefaultsHandler(handler, null);
    }
    
    public void unregisterDefaultsHandler(final DefaultParametersHandler<?> handler, final Class<?> startClass) {
        final Collection<DefaultHandlerData> toRemove = new LinkedList<DefaultHandlerData>();
        for (final DefaultHandlerData dhd : this.defaultHandlers) {
            if (dhd.isOccurrence(handler, startClass)) {
                toRemove.add(dhd);
            }
        }
        this.defaultHandlers.removeAll(toRemove);
    }
    
    public void initializeParameters(final BuilderParameters params) {
        if (params != null) {
            for (final DefaultHandlerData dhd : this.defaultHandlers) {
                dhd.applyHandlerIfMatching(params);
            }
        }
    }
    
    private static class DefaultHandlerData
    {
        private final DefaultParametersHandler<?> handler;
        private final Class<?> parameterClass;
        private final Class<?> startClass;
        
        public DefaultHandlerData(final DefaultParametersHandler<?> h, final Class<?> cls, final Class<?> startCls) {
            this.handler = h;
            this.parameterClass = cls;
            this.startClass = startCls;
        }
        
        public void applyHandlerIfMatching(final BuilderParameters obj) {
            if (this.parameterClass.isInstance(obj) && (this.startClass == null || this.startClass.isInstance(obj))) {
                final DefaultParametersHandler handlerUntyped = this.handler;
                handlerUntyped.initializeDefaults(obj);
            }
        }
        
        public boolean isOccurrence(final DefaultParametersHandler<?> h, final Class<?> startCls) {
            return h == this.handler && (startCls == null || startCls.equals(this.startClass));
        }
    }
}
