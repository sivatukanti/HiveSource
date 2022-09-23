// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.cache;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

public class ClassSize
{
    public static final int refSize;
    private static final int objectOverhead = 2;
    private static final int booleanSize = 4;
    private static final int charSize = 4;
    private static final int shortSize = 4;
    private static final int intSize = 4;
    private static final int longSize = 8;
    private static final int floatSize = 4;
    private static final int doubleSize = 8;
    private static final int minObjectSize;
    private static boolean dummyCatalog;
    static boolean noGuess;
    static boolean unitTest;
    private static final int[] wildGuess;
    private static Hashtable catalog;
    
    public static void setDummyCatalog() {
        ClassSize.dummyCatalog = true;
    }
    
    public static int getRefSize() {
        return ClassSize.refSize;
    }
    
    public static int getIntSize() {
        return 4;
    }
    
    public static int[] getSizeCoefficients(Class superclass) {
        final int[] array = { 0, 2 };
        while (null != superclass) {
            final Field[] declaredFields = superclass.getDeclaredFields();
            if (null != declaredFields) {
                for (int i = 0; i < declaredFields.length; ++i) {
                    if (!Modifier.isStatic(declaredFields[i].getModifiers())) {
                        final Class<?> type = declaredFields[i].getType();
                        if (type.isArray() || !type.isPrimitive()) {
                            final int[] array2 = array;
                            final int n = 1;
                            ++array2[n];
                        }
                        else {
                            final String name = type.getName();
                            if (name.equals("int") || name.equals("I")) {
                                final int[] array3 = array;
                                final int n2 = 0;
                                array3[n2] += 4;
                            }
                            else if (name.equals("long") || name.equals("J")) {
                                final int[] array4 = array;
                                final int n3 = 0;
                                array4[n3] += 8;
                            }
                            else if (name.equals("boolean") || name.equals("Z")) {
                                final int[] array5 = array;
                                final int n4 = 0;
                                array5[n4] += 4;
                            }
                            else if (name.equals("short") || name.equals("S")) {
                                final int[] array6 = array;
                                final int n5 = 0;
                                array6[n5] += 4;
                            }
                            else if (name.equals("byte") || name.equals("B")) {
                                final int[] array7 = array;
                                final int n6 = 0;
                                ++array7[n6];
                            }
                            else if (name.equals("char") || name.equals("C")) {
                                final int[] array8 = array;
                                final int n7 = 0;
                                array8[n7] += 4;
                            }
                            else if (name.equals("float") || name.equals("F")) {
                                final int[] array9 = array;
                                final int n8 = 0;
                                array9[n8] += 4;
                            }
                            else if (name.equals("double") || name.equals("D")) {
                                final int[] array10 = array;
                                final int n9 = 0;
                                array10[n9] += 8;
                            }
                            else {
                                final int[] array11 = array;
                                final int n10 = 1;
                                ++array11[n10];
                            }
                        }
                    }
                }
            }
            superclass = superclass.getSuperclass();
        }
        return array;
    }
    
    public static int estimateBaseFromCoefficients(final int[] array) {
        final int n = (array[0] + array[1] * ClassSize.refSize + 7) / 8 * 8;
        return (n < ClassSize.minObjectSize) ? ClassSize.minObjectSize : n;
    }
    
    public static int estimateBaseFromCatalog(final Class clazz) {
        return estimateBaseFromCatalog(clazz, false);
    }
    
    private static int estimateBaseFromCatalog(final Class clazz, final boolean b) {
        if (ClassSize.dummyCatalog) {
            return 0;
        }
        int[] value = ClassSize.catalog.get(clazz.getName());
        if (value == null) {
            try {
                value = getSizeCoefficients(clazz);
            }
            catch (Throwable t) {
                if (ClassSize.noGuess) {
                    return -2;
                }
                value = ClassSize.wildGuess;
            }
            if (b) {
                ClassSize.catalog.put(clazz.getName(), value);
            }
        }
        return estimateBaseFromCoefficients(value);
    }
    
    public static int estimateAndCatalogBase(final Class clazz) {
        return estimateBaseFromCatalog(clazz, true);
    }
    
    public static int estimateBase(final Class clazz) {
        return estimateBaseFromCoefficients(getSizeCoefficients(clazz));
    }
    
    public static int estimateArrayOverhead() {
        return ClassSize.minObjectSize;
    }
    
    public static int estimateHashEntrySize() {
        return 2 + 3 * ClassSize.refSize;
    }
    
    public static int estimateMemoryUsage(final String s) {
        if (null == s) {
            return 0;
        }
        return 2 * s.length();
    }
    
    private static final int fetchRefSizeFromSystemProperties() {
        final String systemProperty = getSystemProperty("sun.arch.data.model");
        try {
            return new Integer(systemProperty) / 8;
        }
        catch (NumberFormatException ex) {
            final String systemProperty2 = getSystemProperty("os.arch");
            if (systemProperty2 != null) {
                if (Arrays.asList("i386", "x86", "sparc").contains(systemProperty2)) {
                    return 4;
                }
                if (Arrays.asList("amd64", "x86_64", "sparcv9").contains(systemProperty2)) {
                    return 8;
                }
            }
            return -1;
        }
    }
    
    private static final String getSystemProperty(final String s) {
        try {
            return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty(s, null);
                }
            });
        }
        catch (SecurityException ex) {
            return null;
        }
    }
    
    static {
        ClassSize.dummyCatalog = false;
        ClassSize.noGuess = false;
        ClassSize.unitTest = false;
        wildGuess = new int[] { 0, 16 };
        try {
            ClassSize.catalog = (Hashtable)Class.forName("org.apache.derby.iapi.services.cache.ClassSizeCatalog").newInstance();
        }
        catch (Exception ex) {}
        int fetchRefSizeFromSystemProperties = fetchRefSizeFromSystemProperties();
        if (fetchRefSizeFromSystemProperties < 4) {
            final Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            runtime.runFinalization();
            final long n = runtime.totalMemory() - runtime.freeMemory();
            final Object[] array = new Object[10000];
            runtime.gc();
            runtime.runFinalization();
            final int n2 = (int)((runtime.totalMemory() - runtime.freeMemory() - n + array.length / 2) / array.length);
            fetchRefSizeFromSystemProperties = ((4 > n2) ? 4 : n2);
        }
        refSize = fetchRefSizeFromSystemProperties;
        minObjectSize = 4 * ClassSize.refSize;
    }
}
