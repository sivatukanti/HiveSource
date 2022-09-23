// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.sql.Date;
import org.apache.hadoop.io.Text;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.DateWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyDateObjectInspector;

public class LazyDate extends LazyPrimitive<LazyDateObjectInspector, DateWritable>
{
    private static final Log LOG;
    
    public LazyDate(final LazyDateObjectInspector oi) {
        super(oi);
        this.data = (T)new DateWritable();
    }
    
    public LazyDate(final LazyDate copy) {
        super(copy);
        this.data = (T)new DateWritable((DateWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String s = null;
        try {
            s = Text.decode(bytes.getData(), start, length);
            ((DateWritable)this.data).set(Date.valueOf(s));
            this.isNull = false;
        }
        catch (Exception e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "DATE");
        }
    }
    
    public static void writeUTF8(final OutputStream out, final DateWritable d) throws IOException {
        final ByteBuffer b = Text.encode(d.toString());
        out.write(b.array(), 0, b.limit());
    }
    
    static {
        LOG = LogFactory.getLog(LazyDate.class);
    }
}
