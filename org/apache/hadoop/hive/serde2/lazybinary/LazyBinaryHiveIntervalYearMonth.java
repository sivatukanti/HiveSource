// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveIntervalYearMonthObjectInspector;

public class LazyBinaryHiveIntervalYearMonth extends LazyBinaryPrimitive<WritableHiveIntervalYearMonthObjectInspector, HiveIntervalYearMonthWritable>
{
    static final Log LOG;
    LazyBinaryUtils.VInt vInt;
    
    LazyBinaryHiveIntervalYearMonth(final WritableHiveIntervalYearMonthObjectInspector oi) {
        super(oi);
        this.vInt = new LazyBinaryUtils.VInt();
        this.data = (T)new HiveIntervalYearMonthWritable();
    }
    
    LazyBinaryHiveIntervalYearMonth(final LazyBinaryHiveIntervalYearMonth copy) {
        super(copy);
        this.vInt = new LazyBinaryUtils.VInt();
        this.data = (T)new HiveIntervalYearMonthWritable((HiveIntervalYearMonthWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        ((HiveIntervalYearMonthWritable)this.data).setFromBytes(bytes.getData(), start, length, this.vInt);
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinaryHiveIntervalYearMonth.class);
    }
}
