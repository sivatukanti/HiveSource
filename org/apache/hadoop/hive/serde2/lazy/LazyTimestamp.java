// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Writable;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyTimestampObjectInspector;

public class LazyTimestamp extends LazyPrimitive<LazyTimestampObjectInspector, TimestampWritable>
{
    private static final Log LOG;
    private static final String nullTimestamp = "NULL";
    
    public LazyTimestamp(final LazyTimestampObjectInspector oi) {
        super(oi);
        this.data = (T)new TimestampWritable();
    }
    
    public LazyTimestamp(final LazyTimestamp copy) {
        super(copy);
        this.data = (T)new TimestampWritable((TimestampWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        String s = null;
        try {
            s = new String(bytes.getData(), start, length, "US-ASCII");
        }
        catch (UnsupportedEncodingException e) {
            LazyTimestamp.LOG.error(e);
            s = "";
        }
        Timestamp t = null;
        if (s.compareTo("NULL") == 0) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "TIMESTAMP");
        }
        else {
            try {
                t = ((LazyTimestampObjectInspector)this.oi).getTimestampParser().parseTimestamp(s);
                this.isNull = false;
            }
            catch (IllegalArgumentException e2) {
                this.isNull = true;
                this.logExceptionMessage(bytes, start, length, "TIMESTAMP");
            }
        }
        ((TimestampWritable)this.data).set(t);
    }
    
    public static void writeUTF8(final OutputStream out, final TimestampWritable i) throws IOException {
        if (i == null) {
            out.write(TimestampWritable.nullBytes);
        }
        else {
            out.write(i.toString().getBytes("US-ASCII"));
        }
    }
    
    @Override
    public TimestampWritable getWritableObject() {
        return (TimestampWritable)this.data;
    }
    
    static {
        LOG = LogFactory.getLog(LazyTimestamp.class);
    }
}
