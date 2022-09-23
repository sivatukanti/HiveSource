// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableTimestampObjectInspector;

public class LazyBinaryTimestamp extends LazyBinaryPrimitive<WritableTimestampObjectInspector, TimestampWritable>
{
    static final Log LOG;
    
    LazyBinaryTimestamp(final WritableTimestampObjectInspector oi) {
        super(oi);
        this.data = (T)new TimestampWritable();
    }
    
    LazyBinaryTimestamp(final LazyBinaryTimestamp copy) {
        super(copy);
        this.data = (T)new TimestampWritable((TimestampWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        ((TimestampWritable)this.data).set(bytes.getData(), start);
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinaryTimestamp.class);
    }
}
