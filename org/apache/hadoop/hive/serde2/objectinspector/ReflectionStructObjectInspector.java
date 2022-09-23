// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReflectionStructObjectInspector extends SettableStructObjectInspector
{
    Class<?> objectClass;
    List<MyField> fields;
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.STRUCT;
    }
    
    @Override
    public String getTypeName() {
        final StringBuilder sb = new StringBuilder("struct<");
        boolean first = true;
        for (final StructField structField : this.getAllStructFieldRefs()) {
            if (first) {
                first = false;
            }
            else {
                sb.append(",");
            }
            sb.append(structField.getFieldName()).append(":").append(structField.getFieldObjectInspector().getTypeName());
        }
        sb.append(">");
        return sb.toString();
    }
    
    ReflectionStructObjectInspector() {
    }
    
    protected void init(final Class<?> objectClass, final ObjectInspectorFactory.ObjectInspectorOptions options) {
        this.verifyObjectClassType(objectClass);
        this.objectClass = objectClass;
        final List<? extends ObjectInspector> structFieldObjectInspectors = this.extractFieldObjectInspectors(objectClass, options);
        final Field[] reflectionFields = ObjectInspectorUtils.getDeclaredNonStaticFields(objectClass);
        this.fields = new ArrayList<MyField>(structFieldObjectInspectors.size());
        int used = 0;
        for (int i = 0; i < reflectionFields.length; ++i) {
            if (!this.shouldIgnoreField(reflectionFields[i].getName())) {
                reflectionFields[i].setAccessible(true);
                this.fields.add(new MyField(i, reflectionFields[i], (ObjectInspector)structFieldObjectInspectors.get(used++)));
            }
        }
        assert this.fields.size() == structFieldObjectInspectors.size();
    }
    
    public boolean shouldIgnoreField(final String name) {
        return false;
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
        if (!(fieldRef instanceof MyField)) {
            throw new RuntimeException("fieldRef has to be of MyField");
        }
        final MyField f = (MyField)fieldRef;
        try {
            final Object r = f.field.get(data);
            return r;
        }
        catch (Exception e) {
            throw new RuntimeException("cannot get field " + f.field + " from " + data.getClass() + " " + data, e);
        }
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(final Object data) {
        if (data == null) {
            return null;
        }
        try {
            final ArrayList<Object> result = new ArrayList<Object>(this.fields.size());
            for (int i = 0; i < this.fields.size(); ++i) {
                result.add(this.fields.get(i).field.get(data));
            }
            return result;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Object create() {
        return ReflectionUtils.newInstance(this.objectClass, null);
    }
    
    @Override
    public Object setStructFieldData(final Object struct, final StructField field, final Object fieldValue) {
        final MyField myField = (MyField)field;
        try {
            myField.field.set(struct, fieldValue);
        }
        catch (Exception e) {
            throw new RuntimeException("cannot set field " + myField.field + " of " + struct.getClass() + " " + struct, e);
        }
        return struct;
    }
    
    protected List<? extends ObjectInspector> extractFieldObjectInspectors(final Class<?> clazz, final ObjectInspectorFactory.ObjectInspectorOptions options) {
        final Field[] fields = ObjectInspectorUtils.getDeclaredNonStaticFields(clazz);
        final ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>(fields.length);
        for (int i = 0; i < fields.length; ++i) {
            if (!this.shouldIgnoreField(fields[i].getName())) {
                structFieldObjectInspectors.add(ObjectInspectorFactory.getReflectionObjectInspector(fields[i].getGenericType(), options));
            }
        }
        return structFieldObjectInspectors;
    }
    
    protected void verifyObjectClassType(final Class<?> objectClass) {
        assert !List.class.isAssignableFrom(objectClass);
        assert !Map.class.isAssignableFrom(objectClass);
    }
    
    public static class MyField implements StructField
    {
        protected int fieldID;
        protected Field field;
        protected ObjectInspector fieldObjectInspector;
        
        protected MyField() {
        }
        
        public MyField(final int fieldID, final Field field, final ObjectInspector fieldObjectInspector) {
            this.fieldID = fieldID;
            this.field = field;
            this.fieldObjectInspector = fieldObjectInspector;
        }
        
        @Override
        public String getFieldName() {
            return this.field.getName().toLowerCase();
        }
        
        @Override
        public ObjectInspector getFieldObjectInspector() {
            return this.fieldObjectInspector;
        }
        
        @Override
        public int getFieldID() {
            return this.fieldID;
        }
        
        @Override
        public String getFieldComment() {
            return null;
        }
        
        @Override
        public String toString() {
            return this.field.toString();
        }
    }
}
