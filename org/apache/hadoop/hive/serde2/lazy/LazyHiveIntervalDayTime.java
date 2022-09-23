// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.io.Writable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import org.apache.hadoop.hive.common.type.HiveIntervalDayTime;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.io.HiveIntervalDayTimeWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveIntervalDayTimeObjectInspector;

public class LazyHiveIntervalDayTime extends LazyPrimitive<LazyHiveIntervalDayTimeObjectInspector, HiveIntervalDayTimeWritable>
{
    public LazyHiveIntervalDayTime(final LazyHiveIntervalDayTimeObjectInspector oi) {
        super(oi);
        this.data = (T)new HiveIntervalDayTimeWritable();
    }
    
    public LazyHiveIntervalDayTime(final LazyHiveIntervalDayTime copy) {
        super(copy);
        this.data = (T)new HiveIntervalDayTimeWritable((HiveIntervalDayTimeWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String s = null;
        try {
            s = Text.decode(bytes.getData(), start, length);
            ((HiveIntervalDayTimeWritable)this.data).set(HiveIntervalDayTime.valueOf(s));
            this.isNull = false;
        }
        catch (Exception e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "INTERVAL_DAY_TIME");
        }
    }
    
    public static void writeUTF8(final OutputStream out, final HiveIntervalDayTimeWritable i) throws IOException {
        final ByteBuffer b = Text.encode(i.toString());
        out.write(b.array(), 0, b.limit());
    }
    
    @Override
    public HiveIntervalDayTimeWritable getWritableObject() {
        return (HiveIntervalDayTimeWritable)this.data;
    }
}
