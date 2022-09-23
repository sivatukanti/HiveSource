// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import java.lang.reflect.Method;
import java.lang.annotation.Annotation;

public class AnnotationUtils
{
    public static <T extends Annotation> T getAnnotation(final Class<?> clazz, final Class<T> annotationClass) {
        synchronized (annotationClass) {
            return clazz.getAnnotation(annotationClass);
        }
    }
    
    public static <T extends Annotation> T getAnnotation(final Method method, final Class<T> annotationClass) {
        synchronized (annotationClass) {
            return method.getAnnotation(annotationClass);
        }
    }
}
