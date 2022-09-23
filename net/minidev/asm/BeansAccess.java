// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import net.minidev.asm.ex.NoSuchFieldException;
import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;

public abstract class BeansAccess<T>
{
    private HashMap<String, Accessor> map;
    private Accessor[] accs;
    private static ConcurrentHashMap<Class<?>, BeansAccess<?>> cache;
    
    static {
        BeansAccess.cache = new ConcurrentHashMap<Class<?>, BeansAccess<?>>();
    }
    
    protected void setAccessor(final Accessor[] accs) {
        int i = 0;
        this.accs = accs;
        this.map = new HashMap<String, Accessor>();
        for (final Accessor acc : accs) {
            acc.index = i++;
            this.map.put(acc.getName(), acc);
        }
    }
    
    public HashMap<String, Accessor> getMap() {
        return this.map;
    }
    
    public Accessor[] getAccessors() {
        return this.accs;
    }
    
    public static <P> BeansAccess<P> get(final Class<P> type) {
        return get(type, null);
    }
    
    public static <P> BeansAccess<P> get(final Class<P> type, final FieldFilter filter) {
        final BeansAccess<P> access = (BeansAccess<P>)BeansAccess.cache.get(type);
        if (access != null) {
            return access;
        }
        final Accessor[] accs = ASMUtil.getAccessors(type, filter);
        final String className = type.getName();
        String accessClassName;
        if (className.startsWith("java.util.")) {
            accessClassName = "net.minidev.asm." + className + "AccAccess";
        }
        else {
            accessClassName = className.concat("AccAccess");
        }
        final DynamicClassLoader loader = new DynamicClassLoader(type.getClassLoader());
        Class<?> accessClass = null;
        try {
            accessClass = loader.loadClass(accessClassName);
        }
        catch (ClassNotFoundException ex2) {}
        final LinkedList<Class<?>> parentClasses = getParents(type);
        if (accessClass == null) {
            final BeansAccessBuilder builder = new BeansAccessBuilder(type, accs, loader);
            for (final Class<?> c : parentClasses) {
                builder.addConversion(BeansAccessConfig.classMapper.get(c));
            }
            accessClass = builder.bulid();
        }
        try {
            final BeansAccess<P> access2 = (BeansAccess<P>)accessClass.newInstance();
            access2.setAccessor(accs);
            BeansAccess.cache.putIfAbsent(type, access2);
            for (final Class<?> c : parentClasses) {
                addAlias(access2, BeansAccessConfig.classFiledNameMapper.get(c));
            }
            return access2;
        }
        catch (Exception ex) {
            throw new RuntimeException("Error constructing accessor class: " + accessClassName, ex);
        }
    }
    
    private static LinkedList<Class<?>> getParents(Class<?> type) {
        final LinkedList<Class<?>> m = new LinkedList<Class<?>>();
        while (type != null && !type.equals(Object.class)) {
            m.addLast(type);
            Class<?>[] interfaces;
            for (int length = (interfaces = type.getInterfaces()).length, i = 0; i < length; ++i) {
                final Class<?> c = interfaces[i];
                m.addLast(c);
            }
            type = type.getSuperclass();
        }
        m.addLast(Object.class);
        return m;
    }
    
    private static void addAlias(final BeansAccess<?> access, final HashMap<String, String> m) {
        if (m == null) {
            return;
        }
        final HashMap<String, Accessor> changes = new HashMap<String, Accessor>();
        for (final Map.Entry<String, String> e : m.entrySet()) {
            final Accessor a1 = access.map.get(e.getValue());
            if (a1 != null) {
                changes.put(e.getValue(), a1);
            }
        }
        access.map.putAll(changes);
    }
    
    public abstract void set(final T p0, final int p1, final Object p2);
    
    public abstract Object get(final T p0, final int p1);
    
    public abstract T newInstance();
    
    public void set(final T object, final String methodName, final Object value) {
        final int i = this.getIndex(methodName);
        if (i == -1) {
            throw new NoSuchFieldException(String.valueOf(methodName) + " in " + object.getClass() + " to put value : " + value);
        }
        this.set(object, i, value);
    }
    
    public Object get(final T object, final String methodName) {
        return this.get(object, this.getIndex(methodName));
    }
    
    public int getIndex(final String name) {
        final Accessor ac = this.map.get(name);
        if (ac == null) {
            return -1;
        }
        return ac.index;
    }
}
