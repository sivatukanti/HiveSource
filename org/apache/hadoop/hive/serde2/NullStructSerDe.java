// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.io.NullWritable;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public class NullStructSerDe extends AbstractSerDe
{
    private static ObjectInspector nullStructOI;
    
    @Override
    public Object deserialize(final Writable blob) throws SerDeException {
        return null;
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return NullStructSerDe.nullStructOI;
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    @Override
    public void initialize(final Configuration conf, final Properties tbl) throws SerDeException {
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return NullWritable.class;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        return NullWritable.get();
    }
    
    static {
        NullStructSerDe.nullStructOI = new NullStructSerDeObjectInspector();
    }
    
    class NullStructField implements StructField
    {
        @Override
        public String getFieldName() {
            return null;
        }
        
        @Override
        public ObjectInspector getFieldObjectInspector() {
            return null;
        }
        
        @Override
        public int getFieldID() {
            return -1;
        }
        
        @Override
        public String getFieldComment() {
            return "";
        }
    }
    
    public static class NullStructSerDeObjectInspector extends StructObjectInspector
    {
        @Override
        public String getTypeName() {
            return "null";
        }
        
        @Override
        public ObjectInspector.Category getCategory() {
            return ObjectInspector.Category.PRIMITIVE;
        }
        
        @Override
        public StructField getStructFieldRef(final String fieldName) {
            return null;
        }
        
        @Override
        public List<NullStructField> getAllStructFieldRefs() {
            return new ArrayList<NullStructField>();
        }
        
        @Override
        public Object getStructFieldData(final Object data, final StructField fieldRef) {
            return null;
        }
        
        @Override
        public List<Object> getStructFieldsDataAsList(final Object data) {
            return new ArrayList<Object>();
        }
    }
}
