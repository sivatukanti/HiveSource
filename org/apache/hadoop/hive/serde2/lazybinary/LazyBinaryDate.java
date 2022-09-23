// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableDateObjectInspector;

public class LazyBinaryDate extends LazyBinaryPrimitive<WritableDateObjectInspector, DateWritable>
{
    static final Log LOG;
    LazyBinaryUtils.VInt vInt;
    
    LazyBinaryDate(final WritableDateObjectInspector oi) {
        super(oi);
        this.vInt = new LazyBinaryUtils.VInt();
        this.data = (T)new DateWritable();
    }
    
    LazyBinaryDate(final LazyBinaryDate copy) {
        super(copy);
        this.vInt = new LazyBinaryUtils.VInt();
        this.data = (T)new DateWritable((DateWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        ((DateWritable)this.data).setFromBytes(bytes.getData(), start, length, this.vInt);
    }
    
    static {
        LOG = LogFactory.getLog(LazyBinaryDate.class);
    }
}
