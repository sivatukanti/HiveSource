// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import org.eclipse.jetty.util.log.Log;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Logger;

public class InjectionCollection
{
    private static final Logger LOG;
    public static final String INJECTION_COLLECTION = "org.eclipse.jetty.injectionCollection";
    private HashMap<String, List<Injection>> _injectionMap;
    
    public InjectionCollection() {
        this._injectionMap = new HashMap<String, List<Injection>>();
    }
    
    public void add(final Injection injection) {
        if (injection == null || injection.getTargetClass() == null) {
            return;
        }
        if (InjectionCollection.LOG.isDebugEnabled()) {
            InjectionCollection.LOG.debug("Adding injection for class=" + injection.getTargetClass() + " on a " + injection.getTarget().getName(), new Object[0]);
        }
        List<Injection> injections = this._injectionMap.get(injection.getTargetClass().getCanonicalName());
        if (injections == null) {
            injections = new ArrayList<Injection>();
            this._injectionMap.put(injection.getTargetClass().getCanonicalName(), injections);
        }
        injections.add(injection);
    }
    
    public List<Injection> getInjections(final String className) {
        if (className == null) {
            return null;
        }
        return this._injectionMap.get(className);
    }
    
    public Injection getInjection(final String jndiName, final Class<?> clazz, final Field field) {
        if (field == null || clazz == null) {
            return null;
        }
        final List<Injection> injections = this.getInjections(clazz.getCanonicalName());
        if (injections == null) {
            return null;
        }
        Iterator<Injection> itor;
        Injection injection;
        Injection i;
        for (itor = injections.iterator(), injection = null; itor.hasNext() && injection == null; injection = i) {
            i = itor.next();
            if (i.isField() && field.getName().equals(i.getTarget().getName())) {}
        }
        return injection;
    }
    
    public Injection getInjection(final String jndiName, final Class<?> clazz, final Method method, final Class<?> paramClass) {
        if (clazz == null || method == null || paramClass == null) {
            return null;
        }
        final List<Injection> injections = this.getInjections(clazz.getCanonicalName());
        if (injections == null) {
            return null;
        }
        Iterator<Injection> itor;
        Injection injection;
        Injection i;
        for (itor = injections.iterator(), injection = null; itor.hasNext() && injection == null; injection = i) {
            i = itor.next();
            if (i.isMethod() && i.getTarget().getName().equals(method.getName()) && paramClass.equals(i.getParamClass())) {}
        }
        return injection;
    }
    
    public void inject(final Object injectable) {
        if (injectable == null) {
            return;
        }
        for (Class<?> clazz = injectable.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            final List<Injection> injections = this._injectionMap.get(clazz.getCanonicalName());
            if (injections != null) {
                for (final Injection i : injections) {
                    i.inject(injectable);
                }
            }
        }
    }
    
    static {
        LOG = Log.getLogger(InjectionCollection.class);
    }
}
