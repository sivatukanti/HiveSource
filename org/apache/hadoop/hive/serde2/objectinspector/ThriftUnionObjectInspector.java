// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ReflectionUtils;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.lang.reflect.Field;
import org.apache.thrift.meta_data.FieldMetaData;
import java.util.ArrayList;
import java.util.Map;
import org.apache.thrift.TFieldIdEnum;
import com.google.common.primitives.UnsignedBytes;
import org.apache.thrift.TUnion;
import java.util.List;

public class ThriftUnionObjectInspector extends ReflectionStructObjectInspector implements UnionObjectInspector
{
    private static final String FIELD_METADATA_MAP = "metaDataMap";
    private List<ObjectInspector> ois;
    
    @Override
    public boolean shouldIgnoreField(final String name) {
        return name.startsWith("__isset");
    }
    
    @Override
    public List<ObjectInspector> getObjectInspectors() {
        return this.ois;
    }
    
    @Override
    public byte getTag(final Object o) {
        if (o == null) {
            return -1;
        }
        final TFieldIdEnum setField = ((TUnion)o).getSetField();
        return UnsignedBytes.checkedCast(setField.getThriftFieldId() - 1);
    }
    
    @Override
    public Object getField(final Object o) {
        if (o == null) {
            return null;
        }
        return ((TUnion)o).getFieldValue();
    }
    
    @Override
    protected void init(final Class<?> objectClass, final ObjectInspectorFactory.ObjectInspectorOptions options) {
        this.verifyObjectClassType(objectClass);
        this.objectClass = objectClass;
        Field fieldMetaData;
        try {
            fieldMetaData = objectClass.getDeclaredField("metaDataMap");
            assert Map.class.isAssignableFrom(fieldMetaData.getType());
            fieldMetaData.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("Unable to find field metadata for thrift union field ", e);
        }
        try {
            final Map<? extends TFieldIdEnum, FieldMetaData> fieldMap = (Map<? extends TFieldIdEnum, FieldMetaData>)fieldMetaData.get(null);
            this.ois = new ArrayList<ObjectInspector>();
            for (final Map.Entry<? extends TFieldIdEnum, FieldMetaData> metadata : fieldMap.entrySet()) {
                final Type fieldType = ThriftObjectInspectorUtils.getFieldType(objectClass, metadata.getValue().fieldName);
                final ObjectInspector reflectionObjectInspector = ObjectInspectorFactory.getReflectionObjectInspector(fieldType, options);
                this.ois.add(reflectionObjectInspector);
            }
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException("Unable to find field metadata for thrift union field ", e2);
        }
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.UNION;
    }
    
    @Override
    public List<? extends StructField> getAllStructFieldRefs() {
        return this.fields;
    }
    
    @Override
    public String getTypeName() {
        return ObjectInspectorUtils.getStandardUnionTypeName(this);
    }
    
    @Override
    public Object create() {
        return ReflectionUtils.newInstance(this.objectClass, null);
    }
}
