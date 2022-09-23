// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import org.eclipse.jetty.util.log.Log;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Logger;

public class LifeCycleCallbackCollection
{
    private static final Logger LOG;
    public static final String LIFECYCLE_CALLBACK_COLLECTION = "org.eclipse.jetty.lifecyleCallbackCollection";
    private HashMap<String, List<LifeCycleCallback>> postConstructCallbacksMap;
    private HashMap<String, List<LifeCycleCallback>> preDestroyCallbacksMap;
    
    public LifeCycleCallbackCollection() {
        this.postConstructCallbacksMap = new HashMap<String, List<LifeCycleCallback>>();
        this.preDestroyCallbacksMap = new HashMap<String, List<LifeCycleCallback>>();
    }
    
    public void add(final LifeCycleCallback callback) {
        if (callback == null || callback.getTargetClassName() == null) {
            return;
        }
        if (LifeCycleCallbackCollection.LOG.isDebugEnabled()) {
            LifeCycleCallbackCollection.LOG.debug("Adding callback for class=" + callback.getTargetClass() + " on " + callback.getTarget(), new Object[0]);
        }
        Map<String, List<LifeCycleCallback>> map = null;
        if (callback instanceof PreDestroyCallback) {
            map = this.preDestroyCallbacksMap;
        }
        if (callback instanceof PostConstructCallback) {
            map = this.postConstructCallbacksMap;
        }
        if (map == null) {
            throw new IllegalArgumentException("Unsupported lifecycle callback type: " + callback);
        }
        List<LifeCycleCallback> callbacks = map.get(callback.getTargetClassName());
        if (callbacks == null) {
            callbacks = new ArrayList<LifeCycleCallback>();
            map.put(callback.getTargetClassName(), callbacks);
        }
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }
    
    public List<LifeCycleCallback> getPreDestroyCallbacks(final Object o) {
        if (o == null) {
            return null;
        }
        final Class<?> clazz = o.getClass();
        return this.preDestroyCallbacksMap.get(clazz.getName());
    }
    
    public List<LifeCycleCallback> getPostConstructCallbacks(final Object o) {
        if (o == null) {
            return null;
        }
        final Class<?> clazz = o.getClass();
        return this.postConstructCallbacksMap.get(clazz.getName());
    }
    
    public void callPostConstructCallback(final Object o) throws Exception {
        if (o == null) {
            return;
        }
        final Class<?> clazz = o.getClass();
        final List<LifeCycleCallback> callbacks = this.postConstructCallbacksMap.get(clazz.getName());
        if (callbacks == null) {
            return;
        }
        for (int i = 0; i < callbacks.size(); ++i) {
            callbacks.get(i).callback(o);
        }
    }
    
    public void callPreDestroyCallback(final Object o) throws Exception {
        if (o == null) {
            return;
        }
        final Class<?> clazz = o.getClass();
        final List<LifeCycleCallback> callbacks = this.preDestroyCallbacksMap.get(clazz.getName());
        if (callbacks == null) {
            return;
        }
        for (int i = 0; i < callbacks.size(); ++i) {
            callbacks.get(i).callback(o);
        }
    }
    
    static {
        LOG = Log.getLogger(LifeCycleCallbackCollection.class);
    }
}
