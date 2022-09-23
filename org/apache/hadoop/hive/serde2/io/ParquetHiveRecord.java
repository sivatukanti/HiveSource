// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.io.Writable;

public class ParquetHiveRecord implements Writable
{
    public Object value;
    public StructObjectInspector inspector;
    
    public ParquetHiveRecord() {
        this(null, null);
    }
    
    public ParquetHiveRecord(final Object o, final StructObjectInspector oi) {
        this.value = o;
        this.inspector = oi;
    }
    
    public StructObjectInspector getObjectInspector() {
        return this.inspector;
    }
    
    public Object getObject() {
        return this.value;
    }
    
    @Override
    public void write(final DataOutput dataOutput) throws IOException {
        throw new UnsupportedOperationException("Unsupported method call.");
    }
    
    @Override
    public void readFields(final DataInput dataInput) throws IOException {
        throw new UnsupportedOperationException("Unsupported method call.");
    }
}
