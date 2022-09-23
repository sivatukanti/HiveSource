// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary.objectinspector;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class LazyBinaryObjectInspectorFactory
{
    static ConcurrentHashMap<ArrayList<Object>, LazyBinaryStructObjectInspector> cachedLazyBinaryStructObjectInspector;
    static ConcurrentHashMap<ArrayList<Object>, LazyBinaryUnionObjectInspector> cachedLazyBinaryUnionObjectInspector;
    static ConcurrentHashMap<ArrayList<Object>, LazyBinaryListObjectInspector> cachedLazyBinaryListObjectInspector;
    static ConcurrentHashMap<ArrayList<Object>, LazyBinaryMapObjectInspector> cachedLazyBinaryMapObjectInspector;
    
    public static LazyBinaryStructObjectInspector getLazyBinaryStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        return getLazyBinaryStructObjectInspector(structFieldNames, structFieldObjectInspectors, null);
    }
    
    public static LazyBinaryStructObjectInspector getLazyBinaryStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        final ArrayList<Object> signature = new ArrayList<Object>(3);
        signature.add(structFieldNames);
        signature.add(structFieldObjectInspectors);
        if (structFieldComments != null) {
            signature.add(structFieldComments);
        }
        LazyBinaryStructObjectInspector result = LazyBinaryObjectInspectorFactory.cachedLazyBinaryStructObjectInspector.get(signature);
        if (result == null) {
            result = new LazyBinaryStructObjectInspector(structFieldNames, structFieldObjectInspectors, structFieldComments);
            final LazyBinaryStructObjectInspector prev = LazyBinaryObjectInspectorFactory.cachedLazyBinaryStructObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static LazyBinaryUnionObjectInspector getLazyBinaryUnionObjectInspector(final List<ObjectInspector> unionFieldObjectInspectors) {
        final ArrayList<Object> signature = new ArrayList<Object>(1);
        signature.add(unionFieldObjectInspectors);
        LazyBinaryUnionObjectInspector result = LazyBinaryObjectInspectorFactory.cachedLazyBinaryUnionObjectInspector.get(signature);
        if (result == null) {
            result = new LazyBinaryUnionObjectInspector(unionFieldObjectInspectors);
            final LazyBinaryUnionObjectInspector prev = LazyBinaryObjectInspectorFactory.cachedLazyBinaryUnionObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static LazyBinaryListObjectInspector getLazyBinaryListObjectInspector(final ObjectInspector listElementObjectInspector) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(listElementObjectInspector);
        LazyBinaryListObjectInspector result = LazyBinaryObjectInspectorFactory.cachedLazyBinaryListObjectInspector.get(signature);
        if (result == null) {
            result = new LazyBinaryListObjectInspector(listElementObjectInspector);
            final LazyBinaryListObjectInspector prev = LazyBinaryObjectInspectorFactory.cachedLazyBinaryListObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    public static LazyBinaryMapObjectInspector getLazyBinaryMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(mapKeyObjectInspector);
        signature.add(mapValueObjectInspector);
        LazyBinaryMapObjectInspector result = LazyBinaryObjectInspectorFactory.cachedLazyBinaryMapObjectInspector.get(signature);
        if (result == null) {
            result = new LazyBinaryMapObjectInspector(mapKeyObjectInspector, mapValueObjectInspector);
            final LazyBinaryMapObjectInspector prev = LazyBinaryObjectInspectorFactory.cachedLazyBinaryMapObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    private LazyBinaryObjectInspectorFactory() {
    }
    
    static {
        LazyBinaryObjectInspectorFactory.cachedLazyBinaryStructObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazyBinaryStructObjectInspector>();
        LazyBinaryObjectInspectorFactory.cachedLazyBinaryUnionObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazyBinaryUnionObjectInspector>();
        LazyBinaryObjectInspectorFactory.cachedLazyBinaryListObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazyBinaryListObjectInspector>();
        LazyBinaryObjectInspectorFactory.cachedLazyBinaryMapObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazyBinaryMapObjectInspector>();
    }
}
