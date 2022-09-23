// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.util.Arrays;
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
                final List l = new ArrayList();
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
            final List l = new ArrayList();
            l.add(list);
            l.add(item);
            return l;
        }
    }
    
    public static Object add(final Object list, final int index, final Object item) {
        if (list == null) {
            if (index > 0 || item instanceof List || item == null) {
                final List l = new ArrayList();
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
            final List l = new ArrayList();
            l.add(list);
            l.add(index, item);
            return l;
        }
    }
    
    public static Object addCollection(Object list, final Collection collection) {
        final Iterator i = collection.iterator();
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
            final List l = new ArrayList(initialSize);
            l.add(list);
            return l;
        }
        final ArrayList ol = (ArrayList)list;
        if (ol.size() > initialSize) {
            return ol;
        }
        final ArrayList nl = new ArrayList(initialSize);
        nl.addAll(ol);
        return nl;
    }
    
    public static Object remove(final Object list, final Object o) {
        if (list == null) {
            return null;
        }
        if (list instanceof List) {
            final List l = (List)list;
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
            final List l = (List)list;
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
    
    public static List getList(final Object list) {
        return getList(list, false);
    }
    
    public static List getList(final Object list, final boolean nullForEmpty) {
        if (list == null) {
            return nullForEmpty ? null : Collections.EMPTY_LIST;
        }
        if (list instanceof List) {
            return (List)list;
        }
        final List l = new ArrayList(1);
        l.add(list);
        return l;
    }
    
    public static String[] toStringArray(final Object list) {
        if (list == null) {
            return LazyList.__EMTPY_STRING_ARRAY;
        }
        if (list instanceof List) {
            final List l = (List)list;
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
    
    public static Object toArray(final Object list, final Class aClass) {
        if (list == null) {
            return Array.newInstance(aClass, 0);
        }
        if (!(list instanceof List)) {
            final Object a = Array.newInstance(aClass, 1);
            Array.set(a, 0, list);
            return a;
        }
        final List l = (List)list;
        if (aClass.isPrimitive()) {
            final Object a2 = Array.newInstance(aClass, l.size());
            for (int i = 0; i < l.size(); ++i) {
                Array.set(a2, i, l.get(i));
            }
            return a2;
        }
        return l.toArray((Object[])Array.newInstance(aClass, l.size()));
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
    
    public static Object get(final Object list, final int i) {
        if (list == null) {
            throw new IndexOutOfBoundsException();
        }
        if (list instanceof List) {
            return ((List)list).get(i);
        }
        if (i == 0) {
            return list;
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
            return ((List)list).toString();
        }
        return "[" + list + "]";
    }
    
    public static Iterator iterator(final Object list) {
        if (list == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        if (list instanceof List) {
            return ((List)list).iterator();
        }
        return getList(list).iterator();
    }
    
    public static ListIterator listIterator(final Object list) {
        if (list == null) {
            return Collections.EMPTY_LIST.listIterator();
        }
        if (list instanceof List) {
            return ((List)list).listIterator();
        }
        return getList(list).listIterator();
    }
    
    public static List array2List(final Object[] array) {
        if (array == null || array.length == 0) {
            return new ArrayList();
        }
        return new ArrayList(Arrays.asList(array));
    }
    
    public static Object[] addToArray(final Object[] array, final Object item, Class type) {
        if (array == null) {
            if (type == null && item != null) {
                type = item.getClass();
            }
            final Object[] na = (Object[])Array.newInstance(type, 1);
            na[0] = item;
            return na;
        }
        final Class c = array.getClass().getComponentType();
        final Object[] na2 = (Object[])Array.newInstance(c, Array.getLength(array) + 1);
        System.arraycopy(array, 0, na2, 0, array.length);
        na2[array.length] = item;
        return na2;
    }
    
    public static Object[] removeFromArray(final Object[] array, final Object item) {
        if (item == null || array == null) {
            return array;
        }
        int i = array.length;
        while (i-- > 0) {
            if (item.equals(array[i])) {
                final Class c = (array == null) ? item.getClass() : array.getClass().getComponentType();
                final Object[] na = (Object[])Array.newInstance(c, Array.getLength(array) - 1);
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
    
    static {
        __EMTPY_STRING_ARRAY = new String[0];
    }
}
