// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyStringObjectInspector;

public class LazyString extends LazyPrimitive<LazyStringObjectInspector, Text>
{
    public LazyString(final LazyStringObjectInspector oi) {
        super(oi);
        this.data = (T)new Text();
    }
    
    public LazyString(final LazyString copy) {
        super(copy);
        this.data = (T)new Text((Text)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        if (((LazyStringObjectInspector)this.oi).isEscaped()) {
            final byte escapeChar = ((LazyStringObjectInspector)this.oi).getEscapeChar();
            final byte[] inputBytes = bytes.getData();
            LazyUtils.copyAndEscapeStringDataToText(inputBytes, start, length, escapeChar, (Text)this.data);
        }
        else {
            ((Text)this.data).set(bytes.getData(), start, length);
        }
        this.isNull = false;
    }
}
