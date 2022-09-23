// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;

public abstract class BaseStructObjectInspector extends StructObjectInspector
{
    protected final List<MyField> fields;
    
    protected BaseStructObjectInspector() {
        this.fields = new ArrayList<MyField>();
    }
    
    public BaseStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        this.fields = new ArrayList<MyField>();
        this.init(structFieldNames, structFieldObjectInspectors, null);
    }
    
    public BaseStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        this.fields = new ArrayList<MyField>();
        this.init(structFieldNames, structFieldObjectInspectors, structFieldComments);
    }
    
    protected void init(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        assert structFieldNames.size() == structFieldObjectInspectors.size();
        assert structFieldNames.size() == structFieldComments.size();
        for (int i = 0; i < structFieldNames.size(); ++i) {
            this.fields.add(this.createField(i, structFieldNames.get(i), structFieldObjectInspectors.get(i), (structFieldComments == null) ? null : structFieldComments.get(i)));
        }
    }
    
    protected void init(final List<StructField> structFields) {
        for (int i = 0; i < structFields.size(); ++i) {
            this.fields.add(new MyField(i, structFields.get(i)));
        }
    }
    
    protected MyField createField(final int index, final String fieldName, final ObjectInspector fieldOI, final String comment) {
        return new MyField(index, fieldName, fieldOI, comment);
    }
    
    @Override
    public String getTypeName() {
        return ObjectInspectorUtils.getStandardStructTypeName(this);
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.STRUCT;
    }
    
    @Override
    public StructField getStructFieldRef(final String fieldName) {
        return ObjectInspectorUtils.getStandardStructFieldRef(fieldName, this.fields);
    }
    
    @Override
    public List<? extends StructField> getAllStructFieldRefs() {
        return this.fields;
    }
    
    protected static class MyField implements StructField
    {
        protected final int fieldID;
        protected final String fieldName;
        protected final String fieldComment;
        protected final ObjectInspector fieldObjectInspector;
        
        public MyField(final int fieldID, final String fieldName, final ObjectInspector fieldObjectInspector, final String fieldComment) {
            this.fieldID = fieldID;
            this.fieldName = fieldName.toLowerCase();
            this.fieldObjectInspector = fieldObjectInspector;
            this.fieldComment = fieldComment;
        }
        
        public MyField(final int fieldID, final StructField field) {
            this.fieldID = fieldID;
            this.fieldName = field.getFieldName().toLowerCase();
            this.fieldObjectInspector = field.getFieldObjectInspector();
            this.fieldComment = field.getFieldComment();
        }
        
        @Override
        public int getFieldID() {
            return this.fieldID;
        }
        
        @Override
        public String getFieldName() {
            return this.fieldName;
        }
        
        @Override
        public ObjectInspector getFieldObjectInspector() {
            return this.fieldObjectInspector;
        }
        
        @Override
        public String getFieldComment() {
            return this.fieldComment;
        }
        
        @Override
        public String toString() {
            return this.fieldID + ":" + this.fieldName;
        }
    }
}
