// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.Loader;
import java.lang.reflect.Array;
import org.objectweb.asm.Type;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.Filter;
import javax.servlet.Servlet;

public class Util
{
    private static Class[] __envEntryClassTypes;
    private static String[] __envEntryTypes;
    
    public static boolean isServletType(final Class c) {
        boolean isServlet = false;
        if (Servlet.class.isAssignableFrom(c) || Filter.class.isAssignableFrom(c) || ServletContextListener.class.isAssignableFrom(c) || ServletContextAttributeListener.class.isAssignableFrom(c) || ServletRequestListener.class.isAssignableFrom(c) || ServletRequestAttributeListener.class.isAssignableFrom(c) || HttpSessionListener.class.isAssignableFrom(c) || HttpSessionAttributeListener.class.isAssignableFrom(c)) {
            isServlet = true;
        }
        return isServlet;
    }
    
    public static boolean isEnvEntryType(final Class type) {
        boolean result = false;
        for (int i = 0; i < Util.__envEntryClassTypes.length && !result; result = type.equals(Util.__envEntryClassTypes[i]), ++i) {}
        return result;
    }
    
    public static boolean isEnvEntryType(final String desc) {
        boolean result = false;
        for (int i = 0; i < Util.__envEntryTypes.length && !result; result = desc.equals(Util.__envEntryTypes[i]), ++i) {}
        return result;
    }
    
    public static String normalizePattern(final String p) {
        if (p != null && p.length() > 0 && !p.startsWith("/") && !p.startsWith("*")) {
            return "/" + p;
        }
        return p;
    }
    
    public static Class[] convertTypes(final String params) throws Exception {
        return convertTypes(Type.getArgumentTypes(params));
    }
    
    public static Class[] convertTypes(final Type[] types) throws Exception {
        if (types == null) {
            return new Class[0];
        }
        final Class[] classArray = new Class[types.length];
        for (int i = 0; i < types.length; ++i) {
            classArray[i] = convertType(types[i]);
        }
        return classArray;
    }
    
    public static Class convertType(final Type t) throws Exception {
        if (t == null) {
            return null;
        }
        switch (t.getSort()) {
            case 1: {
                return Boolean.TYPE;
            }
            case 9: {
                final Class clazz = convertType(t.getElementType());
                return Array.newInstance(clazz, 0).getClass();
            }
            case 3: {
                return Byte.TYPE;
            }
            case 2: {
                return Character.TYPE;
            }
            case 8: {
                return Double.TYPE;
            }
            case 6: {
                return Float.TYPE;
            }
            case 5: {
                return Integer.TYPE;
            }
            case 7: {
                return Long.TYPE;
            }
            case 10: {
                return Loader.loadClass(null, t.getClassName());
            }
            case 4: {
                return Short.TYPE;
            }
            case 0: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    public static String asCanonicalName(final Type t) {
        if (t == null) {
            return null;
        }
        switch (t.getSort()) {
            case 1: {
                return TypeUtil.toName(Boolean.TYPE);
            }
            case 9: {
                return t.getElementType().getClassName();
            }
            case 3: {
                return TypeUtil.toName(Byte.TYPE);
            }
            case 2: {
                return TypeUtil.toName(Character.TYPE);
            }
            case 8: {
                return TypeUtil.toName(Double.TYPE);
            }
            case 6: {
                return TypeUtil.toName(Float.TYPE);
            }
            case 5: {
                return TypeUtil.toName(Integer.TYPE);
            }
            case 7: {
                return TypeUtil.toName(Long.TYPE);
            }
            case 10: {
                return t.getClassName();
            }
            case 4: {
                return TypeUtil.toName(Short.TYPE);
            }
            case 0: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        Util.__envEntryClassTypes = new Class[] { String.class, Character.class, Integer.class, Boolean.class, Double.class, Byte.class, Short.class, Long.class, Float.class };
        Util.__envEntryTypes = new String[] { Type.getDescriptor(String.class), Type.getDescriptor(Character.class), Type.getDescriptor(Integer.class), Type.getDescriptor(Boolean.class), Type.getDescriptor(Double.class), Type.getDescriptor(Byte.class), Type.getDescriptor(Short.class), Type.getDescriptor(Long.class), Type.getDescriptor(Float.class) };
    }
}
