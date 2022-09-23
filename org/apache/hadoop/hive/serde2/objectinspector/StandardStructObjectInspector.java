// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.commons.logging.LogFactory;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;

public class StandardStructObjectInspector extends SettableStructObjectInspector
{
    public static final Log LOG;
    protected List<MyField> fields;
    boolean warned;
    
    protected StandardStructObjectInspector() {
        this.warned = false;
    }
    
    protected StandardStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors) {
        this.warned = false;
        this.init(structFieldNames, structFieldObjectInspectors, null);
    }
    
    protected StandardStructObjectInspector(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        this.warned = false;
        this.init(structFieldNames, structFieldObjectInspectors, structFieldComments);
    }
    
    protected void init(final List<String> structFieldNames, final List<ObjectInspector> structFieldObjectInspectors, final List<String> structFieldComments) {
        this.fields = new ArrayList<MyField>(structFieldNames.size());
        for (int i = 0; i < structFieldNames.size(); ++i) {
            this.fields.add(new MyField(i, structFieldNames.get(i), structFieldObjectInspectors.get(i), (structFieldComments == null) ? null : structFieldComments.get(i)));
        }
    }
    
    protected StandardStructObjectInspector(final List<StructField> fields) {
        this.warned = false;
        this.init(fields);
    }
    
    protected void init(final List<StructField> fields) {
        this.fields = new ArrayList<MyField>(fields.size());
        for (int i = 0; i < fields.size(); ++i) {
            this.fields.add(new MyField(i, fields.get(i).getFieldName(), fields.get(i).getFieldObjectInspector()));
        }
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
    
    @Override
    public Object getStructFieldData(final Object data, final StructField fieldRef) {
        if (data == null) {
            return null;
        }
        final boolean isArray = !(data instanceof List);
        if (!isArray && !(data instanceof List)) {
            return data;
        }
        final int listSize = isArray ? ((Object[])data).length : ((List)data).size();
        final MyField f = (MyField)fieldRef;
        if (this.fields.size() != listSize && !this.warned) {
            this.warned = true;
            StandardStructObjectInspector.LOG.warn("Trying to access " + this.fields.size() + " fields inside a list of " + listSize + " elements: " + (isArray ? Arrays.asList((Object[])data) : data));
            StandardStructObjectInspector.LOG.warn("ignoring similar errors.");
        }
        final int fieldID = f.getFieldID();
        if (fieldID >= listSize) {
            return null;
        }
        if (isArray) {
            return ((Object[])data)[fieldID];
        }
        return ((List)data).get(fieldID);
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
        return list;
    }
    
    @Override
    public Object create() {
        final ArrayList<Object> a = new ArrayList<Object>(this.fields.size());
        for (int i = 0; i < this.fields.size(); ++i) {
            a.add(null);
        }
        return a;
    }
    
    @Override
    public Object setStructFieldData(final Object struct, final StructField field, final Object fieldValue) {
        final ArrayList<Object> a = (ArrayList<Object>)struct;
        final MyField myField = (MyField)field;
        a.set(myField.fieldID, fieldValue);
        return a;
    }
    
    static {
        LOG = LogFactory.getLog(StandardStructObjectInspector.class.getName());
    }
    
    protected static class MyField implements StructField
    {
        protected int fieldID;
        protected String fieldName;
        protected ObjectInspector fieldObjectInspector;
        protected String fieldComment;
        
        protected MyField() {
        }
        
        public MyField(final int fieldID, final String fieldName, final ObjectInspector fieldObjectInspector) {
            this.fieldID = fieldID;
            this.fieldName = fieldName.toLowerCase();
            this.fieldObjectInspector = fieldObjectInspector;
        }
        
        public MyField(final int fieldID, final String fieldName, final ObjectInspector fieldObjectInspector, final String fieldComment) {
            this(fieldID, fieldName, fieldObjectInspector);
            this.fieldComment = fieldComment;
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
            return "" + this.fieldID + ":" + this.fieldName;
        }
    }
}
