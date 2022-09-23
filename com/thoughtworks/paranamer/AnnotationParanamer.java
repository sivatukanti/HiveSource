// 
// Decompiled by Procyon v0.5.36
// 

package com.thoughtworks.paranamer;

import javax.inject.Named;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;

public class AnnotationParanamer implements Paranamer
{
    public static final String __PARANAMER_DATA = "v1.0 \nlookupParameterNames java.lang.AccessibleObject methodOrConstructor \nlookupParameterNames java.lang.AccessibleObject,boolean methodOrCtor,throwExceptionIfMissing \n";
    private final Paranamer fallback;
    
    public AnnotationParanamer() {
        this(new NullParanamer());
    }
    
    public AnnotationParanamer(final Paranamer fallback) {
        this.fallback = fallback;
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrConstructor) {
        return this.lookupParameterNames(methodOrConstructor, true);
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrCtor, final boolean throwExceptionIfMissing) {
        Class<?>[] types = null;
        Class<?> declaringClass = null;
        String name = null;
        Annotation[][] anns = null;
        if (methodOrCtor instanceof Method) {
            final Method method = (Method)methodOrCtor;
            types = method.getParameterTypes();
            name = method.getName();
            declaringClass = method.getDeclaringClass();
            anns = method.getParameterAnnotations();
        }
        else {
            final Constructor<?> constructor = (Constructor<?>)methodOrCtor;
            types = constructor.getParameterTypes();
            declaringClass = constructor.getDeclaringClass();
            name = "<init>";
            anns = constructor.getParameterAnnotations();
        }
        if (types.length == 0) {
            return AnnotationParanamer.EMPTY_NAMES;
        }
        final String[] names = new String[types.length];
        boolean allDone = true;
        for (int i = 0; i < names.length; ++i) {
            for (int j = 0; j < anns[i].length; ++j) {
                final Annotation ann = anns[i][j];
                if (this.isNamed(ann)) {
                    names[i] = this.getNamedValue(ann);
                    break;
                }
            }
            if (names[i] == null) {
                allDone = false;
            }
        }
        if (!allDone) {
            allDone = true;
            final String[] altNames = this.fallback.lookupParameterNames(methodOrCtor, false);
            if (altNames.length > 0) {
                for (int k = 0; k < names.length; ++k) {
                    if (names[k] == null) {
                        if (altNames[k] != null) {
                            names[k] = altNames[k];
                        }
                        else {
                            allDone = false;
                        }
                    }
                }
            }
            else {
                allDone = false;
            }
        }
        if (allDone) {
            return names;
        }
        if (throwExceptionIfMissing) {
            throw new ParameterNamesNotFoundException("One or more @Named annotations missing for class '" + declaringClass.getName() + "', methodOrCtor " + name + " and parameter types " + DefaultParanamer.getParameterTypeNamesCSV(types));
        }
        return Paranamer.EMPTY_NAMES;
    }
    
    protected String getNamedValue(final Annotation ann) {
        if ("javax.inject.Named".equals(ann.annotationType().getName())) {
            return getNamedValue(ann);
        }
        return null;
    }
    
    protected boolean isNamed(final Annotation ann) {
        return "javax.inject.Named".equals(ann.annotationType().getName()) && isNamed(ann);
    }
    
    public static class Jsr330Helper
    {
        public static final String __PARANAMER_DATA = "";
        
        private static boolean isNamed(final Annotation ann) {
            return ann instanceof Named;
        }
        
        private static String getNamedValue(final Annotation ann) {
            return ((Named)ann).value();
        }
    }
}
