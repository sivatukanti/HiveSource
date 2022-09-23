// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import java.lang.reflect.Method;

class DynamicClassLoader extends ClassLoader
{
    private static final String BEAN_AC;
    private static final Class<?>[] DEF_CLASS_SIG;
    
    static {
        BEAN_AC = BeansAccess.class.getName();
        DEF_CLASS_SIG = new Class[] { String.class, byte[].class, Integer.TYPE, Integer.TYPE };
    }
    
    DynamicClassLoader(final ClassLoader parent) {
        super(parent);
    }
    
    public static <T> Class<T> directLoad(final Class<? extends T> parent, final String clsName, final byte[] clsData) {
        final DynamicClassLoader loader = new DynamicClassLoader(parent.getClassLoader());
        final Class<T> clzz = (Class<T>)loader.defineClass(clsName, clsData);
        return clzz;
    }
    
    public static <T> T directInstance(final Class<? extends T> parent, final String clsName, final byte[] clsData) throws InstantiationException, IllegalAccessException {
        final Class<T> clzz = directLoad(parent, clsName, clsData);
        return clzz.newInstance();
    }
    
    @Override
    protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        if (name.equals(DynamicClassLoader.BEAN_AC)) {
            return BeansAccess.class;
        }
        return super.loadClass(name, resolve);
    }
    
    Class<?> defineClass(final String name, final byte[] bytes) throws ClassFormatError {
        try {
            final Method method = ClassLoader.class.getDeclaredMethod("defineClass", DynamicClassLoader.DEF_CLASS_SIG);
            method.setAccessible(true);
            return (Class<?>)method.invoke(this.getParent(), name, bytes, 0, bytes.length);
        }
        catch (Exception ex) {
            return this.defineClass(name, bytes, 0, bytes.length);
        }
    }
}
