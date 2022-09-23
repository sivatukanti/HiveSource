// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class BeanUtil
{
    public static String okNameForGetter(final AnnotatedMethod am) {
        final String name = am.getName();
        String str = okNameForIsGetter(am, name);
        if (str == null) {
            str = okNameForRegularGetter(am, name);
        }
        return str;
    }
    
    public static String okNameForRegularGetter(final AnnotatedMethod am, final String name) {
        if (name.startsWith("get")) {
            if ("getCallbacks".equals(name)) {
                if (isCglibGetCallbacks(am)) {
                    return null;
                }
            }
            else if ("getMetaClass".equals(name) && isGroovyMetaClassGetter(am)) {
                return null;
            }
            return manglePropertyName(name.substring(3));
        }
        return null;
    }
    
    public static String okNameForIsGetter(final AnnotatedMethod am, final String name) {
        if (!name.startsWith("is")) {
            return null;
        }
        final Class<?> rt = am.getRawType();
        if (rt != Boolean.class && rt != Boolean.TYPE) {
            return null;
        }
        return manglePropertyName(name.substring(2));
    }
    
    public static String okNameForSetter(final AnnotatedMethod am) {
        final String name = okNameForMutator(am, "set");
        if (name == null) {
            return null;
        }
        if ("metaClass".equals(name) && isGroovyMetaClassSetter(am)) {
            return null;
        }
        return name;
    }
    
    public static String okNameForMutator(final AnnotatedMethod am, final String prefix) {
        final String name = am.getName();
        if (name.startsWith(prefix)) {
            return manglePropertyName(name.substring(prefix.length()));
        }
        return null;
    }
    
    protected static boolean isCglibGetCallbacks(final AnnotatedMethod am) {
        final Class<?> rt = am.getRawType();
        if (rt == null || !rt.isArray()) {
            return false;
        }
        final Class<?> compType = rt.getComponentType();
        final Package pkg = compType.getPackage();
        if (pkg != null) {
            final String pname = pkg.getName();
            if (pname.startsWith("net.sf.cglib") || pname.startsWith("org.hibernate.repackage.cglib")) {
                return true;
            }
        }
        return false;
    }
    
    protected static boolean isGroovyMetaClassSetter(final AnnotatedMethod am) {
        final Class<?> argType = am.getRawParameterType(0);
        final Package pkg = argType.getPackage();
        return pkg != null && pkg.getName().startsWith("groovy.lang");
    }
    
    protected static boolean isGroovyMetaClassGetter(final AnnotatedMethod am) {
        final Class<?> rt = am.getRawType();
        if (rt == null || rt.isArray()) {
            return false;
        }
        final Package pkg = rt.getPackage();
        return pkg != null && pkg.getName().startsWith("groovy.lang");
    }
    
    protected static String manglePropertyName(final String basename) {
        final int len = basename.length();
        if (len == 0) {
            return null;
        }
        StringBuilder sb = null;
        for (int i = 0; i < len; ++i) {
            final char upper = basename.charAt(i);
            final char lower = Character.toLowerCase(upper);
            if (upper == lower) {
                break;
            }
            if (sb == null) {
                sb = new StringBuilder(basename);
            }
            sb.setCharAt(i, lower);
        }
        return (sb == null) ? basename : sb.toString();
    }
}
