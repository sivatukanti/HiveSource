// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector;

import org.apache.hadoop.hive.serde2.avro.AvroLazyObjectInspector;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParametersImpl;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public final class LazyObjectInspectorFactory
{
    static ConcurrentHashMap<ArrayList<Object>, LazySimpleStructObjectInspector> cachedLazySimpleStructObjectInspector;
    static ConcurrentHashMap<ArrayList<Object>, LazyListObjectInspector> cachedLazySimpleListObjectInspector;
    static ConcurrentHashMap<ArrayList<Object>, LazyMapObjectInspector> cachedLazySimpleMapObjectInspector;
    static ConcurrentHashMap<List<Object>, LazyUnionObjectInspector> cachedLazyUnionObjectInspector;
    
    @Deprecated
    public static LazySimpleStructObjectInspector getLazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) {
        return getLazySimpleStructObjectInspector(structFieldNames, structFieldObjectInspectors, null, separator, nullSequence, lastColumnTakesRest, escaped, escapeChar, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    
    @Deprecated
    public static LazySimpleStructObjectInspector getLazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar, final ObjectInspectorFactory.ObjectInspectorOptions option) {
        return getLazySimpleStructObjectInspector(structFieldNames, structFieldObjectInspectors, null, separator, nullSequence, lastColumnTakesRest, escaped, escapeChar, option);
    }
    
    @Deprecated
    public static LazySimpleStructObjectInspector getLazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) {
        return getLazySimpleStructObjectInspector(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, nullSequence, lastColumnTakesRest, escaped, escapeChar, ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
    }
    
    @Deprecated
    public static LazySimpleStructObjectInspector getLazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar, final ObjectInspectorFactory.ObjectInspectorOptions option) {
        return getLazySimpleStructObjectInspector(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, null, nullSequence, lastColumnTakesRest), option);
    }
    
    public static LazySimpleStructObjectInspector getLazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final LazyObjectInspectorParameters lazyParams, final ObjectInspectorFactory.ObjectInspectorOptions option) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(structFieldNames);
        signature.add(structFieldObjectInspectors);
        signature.add(separator);
        signature.add(lazyParams.getNullSequence().toString());
        signature.add(lazyParams.isLastColumnTakesRest());
        addCommonLazyParamsToSignature(lazyParams, signature);
        signature.add(option);
        if (structFieldComments != null) {
            signature.add(structFieldComments);
        }
        LazySimpleStructObjectInspector result = LazyObjectInspectorFactory.cachedLazySimpleStructObjectInspector.get(signature);
        if (result == null) {
            switch (option) {
                case JAVA: {
                    result = new LazySimpleStructObjectInspector(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, lazyParams);
                    break;
                }
                case AVRO: {
                    result = new AvroLazyObjectInspector(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, lazyParams);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal ObjectInspector type [" + option + "]");
                }
            }
            final LazySimpleStructObjectInspector prev = LazyObjectInspectorFactory.cachedLazySimpleStructObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    @Deprecated
    public static LazyListObjectInspector getLazySimpleListObjectInspector(final ObjectInspector listElementObjectInspector, final byte separator, final Text nullSequence, final boolean escaped, final byte escapeChar) {
        return getLazySimpleListObjectInspector(listElementObjectInspector, separator, new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, null, nullSequence));
    }
    
    public static LazyListObjectInspector getLazySimpleListObjectInspector(final ObjectInspector listElementObjectInspector, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(listElementObjectInspector);
        signature.add(separator);
        signature.add(lazyParams.getNullSequence().toString());
        addCommonLazyParamsToSignature(lazyParams, signature);
        LazyListObjectInspector result = LazyObjectInspectorFactory.cachedLazySimpleListObjectInspector.get(signature);
        if (result == null) {
            result = new LazyListObjectInspector(listElementObjectInspector, separator, lazyParams);
            final LazyListObjectInspector prev = LazyObjectInspectorFactory.cachedLazySimpleListObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    @Deprecated
    public static LazyMapObjectInspector getLazySimpleMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector, final byte itemSeparator, final byte keyValueSeparator, final Text nullSequence, final boolean escaped, final byte escapeChar) {
        return getLazySimpleMapObjectInspector(mapKeyObjectInspector, mapValueObjectInspector, itemSeparator, keyValueSeparator, new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, null, nullSequence));
    }
    
    public static LazyMapObjectInspector getLazySimpleMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector, final byte itemSeparator, final byte keyValueSeparator, final LazyObjectInspectorParameters lazyParams) {
        final ArrayList<Object> signature = new ArrayList<Object>();
        signature.add(mapKeyObjectInspector);
        signature.add(mapValueObjectInspector);
        signature.add(itemSeparator);
        signature.add(keyValueSeparator);
        signature.add(lazyParams.getNullSequence().toString());
        addCommonLazyParamsToSignature(lazyParams, signature);
        LazyMapObjectInspector result = LazyObjectInspectorFactory.cachedLazySimpleMapObjectInspector.get(signature);
        if (result == null) {
            result = new LazyMapObjectInspector(mapKeyObjectInspector, mapValueObjectInspector, itemSeparator, keyValueSeparator, lazyParams);
            final LazyMapObjectInspector prev = LazyObjectInspectorFactory.cachedLazySimpleMapObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    @Deprecated
    public static LazyUnionObjectInspector getLazyUnionObjectInspector(final List<ObjectInspector> ois, final byte separator, final Text nullSequence, final boolean escaped, final byte escapeChar) {
        return getLazyUnionObjectInspector(ois, separator, new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, null, nullSequence));
    }
    
    public static LazyUnionObjectInspector getLazyUnionObjectInspector(final List<ObjectInspector> ois, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        final List<Object> signature = new ArrayList<Object>();
        signature.add(ois);
        signature.add(separator);
        signature.add(lazyParams.getNullSequence().toString());
        addCommonLazyParamsToSignature(lazyParams, signature);
        LazyUnionObjectInspector result = LazyObjectInspectorFactory.cachedLazyUnionObjectInspector.get(signature);
        if (result == null) {
            result = new LazyUnionObjectInspector(ois, separator, lazyParams);
            final LazyUnionObjectInspector prev = LazyObjectInspectorFactory.cachedLazyUnionObjectInspector.putIfAbsent(signature, result);
            if (prev != null) {
                result = prev;
            }
        }
        return result;
    }
    
    private LazyObjectInspectorFactory() {
    }
    
    private static void addCommonLazyParamsToSignature(final LazyObjectInspectorParameters lazyParams, final List<Object> signature) {
        signature.add(lazyParams.isEscaped());
        signature.add(lazyParams.getEscapeChar());
        signature.add(lazyParams.isExtendedBooleanLiteral());
        signature.add(lazyParams.getTimestampFormats());
    }
    
    static {
        LazyObjectInspectorFactory.cachedLazySimpleStructObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazySimpleStructObjectInspector>();
        LazyObjectInspectorFactory.cachedLazySimpleListObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazyListObjectInspector>();
        LazyObjectInspectorFactory.cachedLazySimpleMapObjectInspector = new ConcurrentHashMap<ArrayList<Object>, LazyMapObjectInspector>();
        LazyObjectInspectorFactory.cachedLazyUnionObjectInspector = new ConcurrentHashMap<List<Object>, LazyUnionObjectInspector>();
    }
}
