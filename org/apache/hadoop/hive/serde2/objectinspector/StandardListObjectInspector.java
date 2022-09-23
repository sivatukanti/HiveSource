// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class StandardListObjectInspector implements SettableListObjectInspector
{
    private ObjectInspector listElementObjectInspector;
    
    protected StandardListObjectInspector() {
    }
    
    protected StandardListObjectInspector(final ObjectInspector listElementObjectInspector) {
        this.listElementObjectInspector = listElementObjectInspector;
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.LIST;
    }
    
    @Override
    public ObjectInspector getListElementObjectInspector() {
        return this.listElementObjectInspector;
    }
    
    @Override
    public Object getListElement(Object data, final int index) {
        if (data == null) {
            return null;
        }
        if (!(data instanceof List)) {
            if (!(data instanceof Set)) {
                final Object[] list = (Object[])data;
                if (index < 0 || index >= list.length) {
                    return null;
                }
                return list[index];
            }
            else {
                data = new ArrayList((Collection<?>)data);
            }
        }
        final List<?> list2 = (List<?>)data;
        if (index < 0 || index >= list2.size()) {
            return null;
        }
        return list2.get(index);
    }
    
    @Override
    public int getListLength(final Object data) {
        if (data == null) {
            return -1;
        }
        if (data instanceof List) {
            final List<?> list = (List<?>)data;
            return list.size();
        }
        if (!(data instanceof Set)) {
            final Object[] list2 = (Object[])data;
            return list2.length;
        }
        final Set<?> set = (Set<?>)data;
        return set.size();
    }
    
    @Override
    public List<?> getList(Object data) {
        if (data == null) {
            return null;
        }
        if (!(data instanceof List)) {
            if (!(data instanceof Set)) {
                data = Arrays.asList((Object[])data);
            }
            else {
                data = new ArrayList((Collection<?>)data);
            }
        }
        final List<?> list = (List<?>)data;
        return list;
    }
    
    @Override
    public String getTypeName() {
        return "array<" + this.listElementObjectInspector.getTypeName() + ">";
    }
    
    @Override
    public Object create(final int size) {
        final List<Object> a = new ArrayList<Object>(size);
        for (int i = 0; i < size; ++i) {
            a.add(null);
        }
        return a;
    }
    
    @Override
    public Object resize(final Object list, final int newSize) {
        final List<Object> a = (List<Object>)list;
        while (a.size() < newSize) {
            a.add(null);
        }
        while (a.size() > newSize) {
            a.remove(a.size() - 1);
        }
        return a;
    }
    
    @Override
    public Object set(final Object list, final int index, final Object element) {
        final List<Object> a = (List<Object>)list;
        a.set(index, element);
        return a;
    }
}
