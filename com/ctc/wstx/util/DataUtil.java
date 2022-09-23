// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.util;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.Collection;
import org.codehaus.stax2.ri.SingletonIterator;
import java.util.Iterator;

public final class DataUtil
{
    static final char[] EMPTY_CHAR_ARRAY;
    static final Long MAX_LONG;
    static final String NO_TYPE = "Illegal to pass null; can not determine component type";
    
    private DataUtil() {
    }
    
    public static char[] getEmptyCharArray() {
        return DataUtil.EMPTY_CHAR_ARRAY;
    }
    
    public static Integer Integer(final int i) {
        return i;
    }
    
    @Deprecated
    public static Long Long(final long l) {
        if (l == Long.MAX_VALUE) {
            return DataUtil.MAX_LONG;
        }
        return l;
    }
    
    public static <T> Iterator<T> singletonIterator(final T item) {
        return (Iterator<T>)new SingletonIterator(item);
    }
    
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>)EmptyIterator.INSTANCE;
    }
    
    public static <T> boolean anyValuesInCommon(Collection<T> c1, Collection<T> c2) {
        if (c1.size() > c2.size()) {
            final Collection<T> tmp = c1;
            c1 = c2;
            c2 = tmp;
        }
        final Iterator<T> it = c1.iterator();
        while (it.hasNext()) {
            if (c2.contains(it.next())) {
                return true;
            }
        }
        return false;
    }
    
    public static Object growArrayBy50Pct(Object arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Illegal to pass null; can not determine component type");
        }
        final Object old = arr;
        final int len = Array.getLength(arr);
        arr = Array.newInstance(arr.getClass().getComponentType(), len + (len >> 1));
        System.arraycopy(old, 0, arr, 0, len);
        return arr;
    }
    
    public static Object growArrayToAtLeast(Object arr, final int minLen) {
        if (arr == null) {
            throw new IllegalArgumentException("Illegal to pass null; can not determine component type");
        }
        final Object old = arr;
        final int oldLen = Array.getLength(arr);
        int newLen = oldLen + (oldLen + 1 >> 1);
        if (newLen < minLen) {
            newLen = minLen;
        }
        arr = Array.newInstance(arr.getClass().getComponentType(), newLen);
        System.arraycopy(old, 0, arr, 0, oldLen);
        return arr;
    }
    
    public static String[] growArrayBy(final String[] arr, final int more) {
        if (arr == null) {
            return new String[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }
    
    public static int[] growArrayBy(final int[] arr, final int more) {
        if (arr == null) {
            return new int[more];
        }
        return Arrays.copyOf(arr, arr.length + more);
    }
    
    static {
        EMPTY_CHAR_ARRAY = new char[0];
        MAX_LONG = new Long(Long.MAX_VALUE);
    }
    
    private static final class EmptyIterator implements Iterator<Object>
    {
        public static final Iterator<?> INSTANCE;
        
        @Override
        public boolean hasNext() {
            return false;
        }
        
        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new IllegalStateException();
        }
        
        static {
            INSTANCE = new EmptyIterator();
        }
    }
}
