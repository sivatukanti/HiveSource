// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ThriftObjectInspectorUtils
{
    public static Type getFieldType(final Class<?> containingClass, final String fieldName) {
        final String suffix = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        final String[] array = { "get", "is" };
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final String prefix = array[i];
            try {
                final Method method = containingClass.getDeclaredMethod(prefix + suffix, (Class<?>[])new Class[0]);
                return method.getGenericReturnType();
            }
            catch (NoSuchMethodException ex) {
                ++i;
                continue;
            }
            break;
        }
        final String[] array2 = { "get_", "is_" };
        final int length2 = array2.length;
        int j = 0;
        while (j < length2) {
            final String prefix = array2[j];
            try {
                final Method method = containingClass.getDeclaredMethod(prefix + fieldName, (Class<?>[])new Class[0]);
                return method.getGenericReturnType();
            }
            catch (NoSuchMethodException ex2) {
                ++j;
                continue;
            }
            break;
        }
        throw new RuntimeException("Could not find type for " + fieldName + " in " + containingClass);
    }
}
