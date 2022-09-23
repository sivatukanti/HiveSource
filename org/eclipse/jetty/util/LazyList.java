// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.ListIterator;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class LazyList implements Cloneable, Serializable
{
    private static final String[] __EMTPY_STRING_ARRAY;
    
    private LazyList() {
    }
    
    public static Object add(final Object list, final Object item) {
        if (list == null) {
            if (item instanceof List || item == null) {
                final List<Object> l = new ArrayList<Object>();
                l.add(item);
                return l;
            }
            return item;
        }
        else {
            if (list instanceof List) {
                ((List)list).add(item);
                return list;
            }
            final List<Object> l = new ArrayList<Object>();
            l.add(list);
            l.add(item);
            return l;
        }
    }
    
    public static Object add(final Object list, final int index, final Object item) {
        if (list == null) {
            if (index > 0 || item instanceof List || item == null) {
                final List<Object> l = new ArrayList<Object>();
                l.add(index, item);
                return l;
            }
            return item;
        }
        else {
            if (list instanceof List) {
                ((List)list).add(index, item);
                return list;
            }
            final List<Object> l = new ArrayList<Object>();
            l.add(list);
            l.add(index, item);
            return l;
        }
    }
    
    public static Object addCollection(Object list, final Collection<?> collection) {
        final Iterator<?> i = collection.iterator();
        while (i.hasNext()) {
            list = add(list, i.next());
        }
        return list;
    }
    
    public static Object addArray(Object list, final Object[] array) {
        for (int i = 0; array != null && i < array.length; ++i) {
            list = add(list, array[i]);
        }
        return list;
    }
    
    public static Object ensureSize(final Object list, final int initialSize) {
        if (list == null) {
            return new ArrayList(initialSize);
        }
        if (!(list instanceof ArrayList)) {
            final List<Object> l = new ArrayList<Object>(initialSize);
            l.add(list);
            return l;
        }
        final ArrayList<?> ol = (ArrayList<?>)list;
        if (ol.size() > initialSize) {
            return ol;
        }
        final ArrayList<Object> nl = new ArrayList<Object>(initialSize);
        nl.addAll(ol);
        return nl;
    }
    
    public static Object remove(final Object list, final Object o) {
        if (list == null) {
            return null;
        }
        if (list instanceof List) {
            final List<?> l = (List<?>)list;
            l.remove(o);
            if (l.size() == 0) {
                return null;
            }
            return list;
        }
        else {
            if (list.equals(o)) {
                return null;
            }
            return list;
        }
    }
    
    public static Object remove(final Object list, final int i) {
        if (list == null) {
            return null;
        }
        if (list instanceof List) {
            final List<?> l = (List<?>)list;
            l.remove(i);
            if (l.size() == 0) {
                return null;
            }
            return list;
        }
        else {
            if (i == 0) {
                return null;
            }
            return list;
        }
    }
    
    public static <E> List<E> getList(final Object list) {
        return getList(list, false);
    }
    
    public static <E> List<E> getList(final Object list, final boolean nullForEmpty) {
        if (list == null) {
            if (nullForEmpty) {
                return null;
            }
            return Collections.emptyList();
        }
        else {
            if (list instanceof List) {
                return (List<E>)list;
            }
            return Collections.singletonList(list);
        }
    }
    
    public static boolean hasEntry(final Object list) {
        return list != null && (!(list instanceof List) || !((List)list).isEmpty());
    }
    
    public static boolean isEmpty(final Object list) {
        return list == null || (list instanceof List && ((List)list).isEmpty());
    }
    
    public static String[] toStringArray(final Object list) {
        if (list == null) {
            return LazyList.__EMTPY_STRING_ARRAY;
        }
        if (list instanceof List) {
            final List<?> l = (List<?>)list;
            final String[] a = new String[l.size()];
            int i = l.size();
            while (i-- > 0) {
                final Object o = l.get(i);
                if (o != null) {
                    a[i] = o.toString();
                }
            }
            return a;
        }
        return new String[] { list.toString() };
    }
    
    public static Object toArray(final Object list, final Class<?> clazz) {
        if (list == null) {
            return Array.newInstance(clazz, 0);
        }
        if (!(list instanceof List)) {
            final Object a = Array.newInstance(clazz, 1);
            Array.set(a, 0, list);
            return a;
        }
        final List<?> l = (List<?>)list;
        if (clazz.isPrimitive()) {
            final Object a2 = Array.newInstance(clazz, l.size());
            for (int i = 0; i < l.size(); ++i) {
                Array.set(a2, i, l.get(i));
            }
            return a2;
        }
        return l.toArray((Object[])Array.newInstance(clazz, l.size()));
    }
    
    public static int size(final Object list) {
        if (list == null) {
            return 0;
        }
        if (list instanceof List) {
            return ((List)list).size();
        }
        return 1;
    }
    
    public static <E> E get(final Object list, final int i) {
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }
        if (list instanceof List) {
            return ((List)list).get(i);
        }
        if (i == 0) {
            return (E)list;
        }
        throw new IndexOutOfBoundsException();
    }
    
    public static boolean contains(final Object list, final Object item) {
        if (list == null) {
            return false;
        }
        if (list instanceof List) {
            return ((List)list).contains(item);
        }
        return list.equals(item);
    }
    
    public static Object clone(final Object list) {
        if (list == null) {
            return null;
        }
        if (list instanceof List) {
            return new ArrayList((Collection<?>)list);
        }
        return list;
    }
    
    public static String toString(final Object list) {
        if (list == null) {
            return "[]";
        }
        if (list instanceof List) {
            return list.toString();
        }
        return "[" + list + "]";
    }
    
    public static <E> Iterator<E> iterator(final Object list) {
        if (list == null) {
            final List<E> empty = Collections.emptyList();
            return empty.iterator();
        }
        if (list instanceof List) {
            return ((List)list).iterator();
        }
        final List<E> l = getList(list);
        return l.iterator();
    }
    
    public static <E> ListIterator<E> listIterator(final Object list) {
        if (list == null) {
            final List<E> empty = Collections.emptyList();
            return empty.listIterator();
        }
        if (list instanceof List) {
            return ((List)list).listIterator();
        }
        final List<E> l = getList(list);
        return l.listIterator();
    }
    
    static {
        __EMTPY_STRING_ARRAY = new String[0];
    }
}
