// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.io.Writable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import org.apache.hadoop.hive.common.type.HiveIntervalYearMonth;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.io.HiveIntervalYearMonthWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyHiveIntervalYearMonthObjectInspector;

public class LazyHiveIntervalYearMonth extends LazyPrimitive<LazyHiveIntervalYearMonthObjectInspector, HiveIntervalYearMonthWritable>
{
    public LazyHiveIntervalYearMonth(final LazyHiveIntervalYearMonthObjectInspector oi) {
        super(oi);
        this.data = (T)new HiveIntervalYearMonthWritable();
    }
    
    public LazyHiveIntervalYearMonth(final LazyHiveIntervalYearMonth copy) {
        super(copy);
        this.data = (T)new HiveIntervalYearMonthWritable((HiveIntervalYearMonthWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String s = null;
        try {
            s = Text.decode(bytes.getData(), start, length);
            ((HiveIntervalYearMonthWritable)this.data).set(HiveIntervalYearMonth.valueOf(s));
            this.isNull = false;
        }
        catch (Exception e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "INTERVAL_YEAR_MONTH");
        }
    }
    
    public static void writeUTF8(final OutputStream out, final HiveIntervalYearMonthWritable i) throws IOException {
        final ByteBuffer b = Text.encode(i.toString());
        out.write(b.array(), 0, b.limit());
    }
    
    @Override
    public HiveIntervalYearMonthWritable getWritableObject() {
        return (HiveIntervalYearMonthWritable)this.data;
    }
}
