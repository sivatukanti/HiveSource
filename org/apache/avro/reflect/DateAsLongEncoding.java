// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import org.apache.avro.io.Decoder;
import java.io.IOException;
import org.apache.avro.io.Encoder;
import org.apache.avro.Schema;
import java.util.Date;

public class DateAsLongEncoding extends CustomEncoding<Date>
{
    public DateAsLongEncoding() {
        (this.schema = Schema.create(Schema.Type.LONG)).addProp("CustomEncoding", "DateAsLongEncoding");
    }
    
    @Override
    protected final void write(final Object datum, final Encoder out) throws IOException {
        out.writeLong(((Date)datum).getTime());
    }
    
    @Override
    protected final Date read(final Object reuse, final Decoder in) throws IOException {
        if (reuse != null && reuse instanceof Date) {
            ((Date)reuse).setTime(in.readLong());
            return (Date)reuse;
        }
        return new Date(in.readLong());
    }
}
