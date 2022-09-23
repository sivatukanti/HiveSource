// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class UnionStructObjectInspector extends StructObjectInspector
{
    private List<StructObjectInspector> unionObjectInspectors;
    private List<MyField> fields;
    
    protected UnionStructObjectInspector() {
    }
    
    protected UnionStructObjectInspector(final List<StructObjectInspector> unionObjectInspectors) {
        this.init(unionObjectInspectors);
    }
    
    void init(final List<StructObjectInspector> unionObjectInspectors) {
        this.unionObjectInspectors = unionObjectInspectors;
        int totalSize = 0;
        for (int i = 0; i < unionObjectInspectors.size(); ++i) {
            totalSize += unionObjectInspectors.get(i).getAllStructFieldRefs().size();
        }
        this.fields = new ArrayList<MyField>(totalSize);
        for (int i = 0; i < unionObjectInspectors.size(); ++i) {
            final StructObjectInspector oi = unionObjectInspectors.get(i);
            for (final StructField sf : oi.getAllStructFieldRefs()) {
                this.fields.add(new MyField(i, sf));
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof UnionStructObjectInspector && this.unionObjectInspectors.equals(((UnionStructObjectInspector)o).unionObjectInspectors));
    }
    
    @Override
    public int hashCode() {
        return this.unionObjectInspectors.hashCode();
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.STRUCT;
    }
    
    @Override
    public String getTypeName() {
        return ObjectInspectorUtils.getStandardStructTypeName(this);
    }
    
    @Override
    public StructField getStructFieldRef(final String fieldName) {
        return ObjectInspectorUtils.getStandardStructFieldRef(fieldName, this.fields);
    }
    
    @Override
    public List<? extends StructField> getAllStructFieldRefs() {
        return this.fields;
    }
    
    @Override
    public Object getStructFieldData(final Object data, final StructField fieldRef) {
        if (data == null) {
            return null;
        }
        final MyField f = (MyField)fieldRef;
        Object fieldData;
        if (!(data instanceof List)) {
            final Object[] list = (Object[])data;
            assert list.length == this.unionObjectInspectors.size();
            fieldData = list[f.structID];
        }
        else {
            final List<Object> list2 = (List<Object>)data;
            assert list2.size() == this.unionObjectInspectors.size();
            fieldData = list2.get(f.structID);
        }
        return this.unionObjectInspectors.get(f.structID).getStructFieldData(fieldData, f.structField);
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(Object data) {
        if (data == null) {
            return null;
        }
        if (!(data instanceof List)) {
            data = Arrays.asList((Object[])data);
        }
        final List<Object> list = (List<Object>)data;
        assert list.size() == this.unionObjectInspectors.size();
        final ArrayList<Object> result = new ArrayList<Object>(this.fields.size());
        for (int i = 0; i < this.unionObjectInspectors.size(); ++i) {
            result.addAll(this.unionObjectInspectors.get(i).getStructFieldsDataAsList(list.get(i)));
        }
        return result;
    }
    
    public static class MyField implements StructField
    {
        protected int structID;
        protected StructField structField;
        
        protected MyField() {
        }
        
        public MyField(final int structID, final StructField structField) {
            this.structID = structID;
            this.structField = structField;
        }
        
        @Override
        public String getFieldName() {
            return this.structField.getFieldName();
        }
        
        @Override
        public ObjectInspector getFieldObjectInspector() {
            return this.structField.getFieldObjectInspector();
        }
        
        @Override
        public int getFieldID() {
            return this.structID;
        }
        
        @Override
        public String getFieldComment() {
            return this.structField.getFieldComment();
        }
    }
}
