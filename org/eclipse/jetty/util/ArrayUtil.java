// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.io.Serializable;

public class ArrayUtil implements Cloneable, Serializable
{
    public static <T> T[] removeFromArray(final T[] array, final Object item) {
        if (item == null || array == null) {
            return array;
        }
        int i = array.length;
        while (i-- > 0) {
            if (item.equals(array[i])) {
                final Class<?> c = (array == null) ? item.getClass() : array.getClass().getComponentType();
                final T[] na = (T[])Array.newInstance(c, Array.getLength(array) - 1);
                if (i > 0) {
                    System.arraycopy(array, 0, na, 0, i);
                }
                if (i + 1 < array.length) {
                    System.arraycopy(array, i + 1, na, i, array.length - (i + 1));
                }
                return na;
            }
        }
        return array;
    }
    
    public static <T> T[] add(final T[] array1, final T[] array2) {
        if (array1 == null || array1.length == 0) {
            return array2;
        }
        if (array2 == null || array2.length == 0) {
            return array1;
        }
        final T[] na = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, na, array1.length, array2.length);
        return na;
    }
    
    public static <T> T[] addToArray(final T[] array, final T item, Class<?> type) {
        if (array == null) {
            if (type == null && item != null) {
                type = item.getClass();
            }
            final T[] na = (T[])Array.newInstance(type, 1);
            na[0] = item;
            return na;
        }
        final T[] na = Arrays.copyOf(array, array.length + 1);
        na[array.length] = item;
        return na;
    }
    
    public static <T> T[] prependToArray(final T item, final T[] array, Class<?> type) {
        if (array == null) {
            if (type == null && item != null) {
                type = item.getClass();
            }
            final T[] na = (T[])Array.newInstance(type, 1);
            na[0] = item;
            return na;
        }
        final Class<?> c = array.getClass().getComponentType();
        final T[] na2 = (T[])Array.newInstance(c, Array.getLength(array) + 1);
        System.arraycopy(array, 0, na2, 1, array.length);
        na2[0] = item;
        return na2;
    }
    
    public static <E> List<E> asMutableList(final E[] array) {
        if (array == null || array.length == 0) {
            return new ArrayList<E>();
        }
        return new ArrayList<E>((Collection<? extends E>)Arrays.asList(array));
    }
    
    public static <T> T[] removeNulls(final T[] array) {
        for (final T t : array) {
            if (t == null) {
                final List<T> list = new ArrayList<T>();
                for (final T t2 : array) {
                    if (t2 != null) {
                        list.add(t2);
                    }
                }
                return list.toArray((T[])Arrays.copyOf((T[])array, list.size()));
            }
        }
        return array;
    }
}
