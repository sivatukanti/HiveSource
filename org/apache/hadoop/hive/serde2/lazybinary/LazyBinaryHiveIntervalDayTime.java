// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveIntervalDayTimeObjectInspector;

public class LazyBinaryHiveIntervalDayTime extends LazyBinaryPrimitive<WritableHiveIntervalDayTimeObjectInspector, HiveIntervalDayTimeWritable>
{
    static final Log LOG;
    LazyBinaryUtils.VInt vInt;
    LazyBinaryUtils.VLong vLong;
    
    LazyBinaryHiveIntervalDayTime(final WritableHiveIntervalDayTimeObjectInspector oi) {
        super(oi);
        this.vInt = new LazyBinaryUtils.VInt();
        this.vLong = new LazyBinaryUtils.VLong();
        this.data = (T)new HiveIntervalDayTimeWritable();
    }
    
    LazyBinaryHiveIntervalDayTime(final LazyBinaryHiveIntervalDayTime copy) {
        super(copy);
        this.vInt = new LazyBinaryUtils.VInt();
        this.vLong = new LazyBinaryUtils.VLong();
        this.data = (T)new HiveIntervalDayTimeWritable((HiveIntervalDayTimeWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        ((HiveIntervalDayTimeWritable)this.data).setFromBytes(bytes.getData(), start, length, this.vInt, this.vLong);
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinaryHiveIntervalDayTime.class);
    }
}
