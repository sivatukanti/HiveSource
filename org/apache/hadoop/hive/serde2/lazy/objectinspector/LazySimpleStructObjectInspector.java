// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.avro.AvroLazyObjectInspector;
import org.apache.hadoop.hive.serde2.StructObject;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParametersImpl;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.BaseStructObjectInspector;

public class LazySimpleStructObjectInspector extends BaseStructObjectInspector
{
    private byte separator;
    private LazyObjectInspectorParameters lazyParams;
    
    protected LazySimpleStructObjectInspector() {
    }
    
    @Deprecated
    protected LazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) {
        this.init(structFieldNames, structFieldObjectInspectors, null, separator, nullSequence, lastColumnTakesRest, escaped, escapeChar);
    }
    
    @Deprecated
    public LazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) {
        this.init(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, nullSequence, lastColumnTakesRest, escaped, escapeChar);
    }
    
    public LazySimpleStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        this.init(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, lazyParams);
    }
    
    protected void init(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final Text nullSequence, final boolean lastColumnTakesRest, final boolean escaped, final byte escapeChar) {
        final LazyObjectInspectorParameters lazyParams = new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, null, nullSequence, lastColumnTakesRest);
        this.init(structFieldNames, structFieldObjectInspectors, structFieldComments, separator, lazyParams);
    }
    
    protected void init(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        this.init(structFieldNames, structFieldObjectInspectors, structFieldComments);
        this.separator = separator;
        this.lazyParams = lazyParams;
    }
    
    @Override
    public Object getStructFieldData(final Object data, final StructField fieldRef) {
        if (data == null) {
            return null;
        }
        final StructObject struct = (StructObject)data;
        final MyField f = (MyField)fieldRef;
        final int fieldID = f.getFieldID();
        assert fieldID >= 0 && fieldID < this.fields.size();
        final ObjectInspector oi = f.getFieldObjectInspector();
        if (oi instanceof AvroLazyObjectInspector) {
            return ((AvroLazyObjectInspector)oi).getStructFieldData(data, fieldRef);
        }
        if (oi instanceof MapObjectInspector) {
            final ObjectInspector valueOI = ((MapObjectInspector)oi).getMapValueObjectInspector();
            if (valueOI instanceof AvroLazyObjectInspector) {
                return ((AvroLazyObjectInspector)valueOI).getStructFieldData(data, fieldRef);
            }
        }
        return struct.getField(fieldID);
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(final Object data) {
        if (data == null) {
            return null;
        }
        final List<Object> result = new ArrayList<Object>(this.fields.size());
        for (final MyField myField : this.fields) {
            result.add(this.getStructFieldData(data, myField));
        }
        return result;
    }
    
    public byte getSeparator() {
        return this.separator;
    }
    
    public Text getNullSequence() {
        return this.lazyParams.getNullSequence();
    }
    
    public boolean getLastColumnTakesRest() {
        return this.lazyParams.isLastColumnTakesRest();
    }
    
    public boolean isEscaped() {
        return this.lazyParams.isEscaped();
    }
    
    public byte getEscapeChar() {
        return this.lazyParams.getEscapeChar();
    }
    
    public LazyObjectInspectorParameters getLazyParams() {
        return this.lazyParams;
    }
}
