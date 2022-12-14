// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.AbstractClassGenerator;
import java.util.Collections;
import java.util.HashMap;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.ClassesKey;
import java.util.Map;

public abstract class Mixin
{
    private static final MixinKey KEY_FACTORY;
    private static final Map ROUTE_CACHE;
    public static final int STYLE_INTERFACES = 0;
    public static final int STYLE_BEANS = 1;
    public static final int STYLE_EVERYTHING = 2;
    
    public abstract Mixin newInstance(final Object[] p0);
    
    public static Mixin create(final Object[] delegates) {
        final Generator gen = new Generator();
        gen.setDelegates(delegates);
        return gen.create();
    }
    
    public static Mixin create(final Class[] interfaces, final Object[] delegates) {
        final Generator gen = new Generator();
        gen.setClasses(interfaces);
        gen.setDelegates(delegates);
        return gen.create();
    }
    
    public static Mixin createBean(final Object[] beans) {
        return createBean(null, beans);
    }
    
    public static Mixin createBean(final ClassLoader loader, final Object[] beans) {
        final Generator gen = new Generator();
        gen.setStyle(1);
        gen.setDelegates(beans);
        gen.setClassLoader(loader);
        return gen.create();
    }
    
    public static Class[] getClasses(final Object[] delegates) {
        return route(delegates).classes.clone();
    }
    
    private static Route route(final Object[] delegates) {
        final Object key = ClassesKey.create(delegates);
        Route route = Mixin.ROUTE_CACHE.get(key);
        if (route == null) {
            Mixin.ROUTE_CACHE.put(key, route = new Route(delegates));
        }
        return route;
    }
    
    static {
        KEY_FACTORY = (MixinKey)KeyFactory.create(MixinKey.class, KeyFactory.CLASS_BY_NAME);
        ROUTE_CACHE = Collections.synchronizedMap(new HashMap<Object, Object>());
    }
    
    public static class Generator extends AbstractClassGenerator
    {
        private static final Source SOURCE;
        private Class[] classes;
        private Object[] delegates;
        private int style;
        private int[] route;
        
        public Generator() {
            super(Generator.SOURCE);
            this.style = 0;
        }
        
        protected ClassLoader getDefaultClassLoader() {
            return this.classes[0].getClassLoader();
        }
        
        public void setStyle(final int style) {
            switch (style) {
                case 0:
                case 1:
                case 2: {
                    this.style = style;
                }
                default: {
                    throw new IllegalArgumentException("Unknown mixin style: " + style);
                }
            }
        }
        
        public void setClasses(final Class[] classes) {
            this.classes = classes;
        }
        
        public void setDelegates(final Object[] delegates) {
            this.delegates = delegates;
        }
        
        public Mixin create() {
            if (this.classes == null && this.delegates == null) {
                throw new IllegalStateException("Either classes or delegates must be set");
            }
            switch (this.style) {
                case 0: {
                    if (this.classes == null) {
                        final Route r = route(this.delegates);
                        this.classes = r.classes;
                        this.route = r.route;
                        break;
                    }
                    break;
                }
                case 1:
                case 2: {
                    if (this.classes == null) {
                        this.classes = ReflectUtils.getClasses(this.delegates);
                        break;
                    }
                    if (this.delegates == null) {
                        break;
                    }
                    final Class[] temp = ReflectUtils.getClasses(this.delegates);
                    if (this.classes.length != temp.length) {
                        throw new IllegalStateException("Specified classes are incompatible with delegates");
                    }
                    for (int i = 0; i < this.classes.length; ++i) {
                        if (!this.classes[i].isAssignableFrom(temp[i])) {
                            throw new IllegalStateException("Specified class " + this.classes[i] + " is incompatible with delegate class " + temp[i] + " (index " + i + ")");
                        }
                    }
                    break;
                }
            }
            this.setNamePrefix(this.classes[ReflectUtils.findPackageProtected(this.classes)].getName());
            return (Mixin)super.create(Mixin.KEY_FACTORY.newInstance(this.style, ReflectUtils.getNames(this.classes), this.route));
        }
        
        public void generateClass(final ClassVisitor v) {
            switch (this.style) {
                case 0: {
                    new MixinEmitter(v, this.getClassName(), this.classes, this.route);
                    break;
                }
                case 1: {
                    new MixinBeanEmitter(v, this.getClassName(), this.classes);
                    break;
                }
                case 2: {
                    new MixinEverythingEmitter(v, this.getClassName(), this.classes);
                    break;
                }
            }
        }
        
        protected Object firstInstance(final Class type) {
            return ((Mixin)ReflectUtils.newInstance(type)).newInstance(this.delegates);
        }
        
        protected Object nextInstance(final Object instance) {
            return ((Mixin)instance).newInstance(this.delegates);
        }
        
        static {
            SOURCE = new Source(Mixin.class.getName());
        }
    }
    
    private static class Route
    {
        private Class[] classes;
        private int[] route;
        
        Route(final Object[] delegates) {
            final Map map = new HashMap();
            final ArrayList collect = new ArrayList();
            for (int i = 0; i < delegates.length; ++i) {
                final Class delegate = delegates[i].getClass();
                collect.clear();
                ReflectUtils.addAllInterfaces(delegate, collect);
                for (final Class iface : collect) {
                    if (!map.containsKey(iface)) {
                        map.put(iface, new Integer(i));
                    }
                }
            }
            this.classes = new Class[map.size()];
            this.route = new int[map.size()];
            int index = 0;
            for (final Class key : map.keySet()) {
                this.classes[index] = key;
                this.route[index] = map.get(key);
                ++index;
            }
        }
    }
    
    interface MixinKey
    {
        Object newInstance(final int p0, final String[] p1, final int[] p2);
    }
}
