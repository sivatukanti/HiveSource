// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.hive.serde2.io.ByteWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyByteObjectInspector;

public class LazyByte extends LazyPrimitive<LazyByteObjectInspector, ByteWritable>
{
    public LazyByte(final LazyByteObjectInspector oi) {
        super(oi);
        this.data = (T)new ByteWritable();
    }
    
    public LazyByte(final LazyByte copy) {
        super(copy);
        this.data = (T)new ByteWritable(((ByteWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        try {
            ((ByteWritable)this.data).set(parseByte(bytes.getData(), start, length, 10));
            this.isNull = false;
        }
        catch (NumberFormatException e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "TINYINT");
        }
    }
    
    public static byte parseByte(final byte[] bytes, final int start, final int length) {
        return parseByte(bytes, start, length, 10);
    }
    
    public static byte parseByte(final byte[] bytes, final int start, final int length, final int radix) {
        final int intValue = LazyInteger.parseInt(bytes, start, length, radix);
        final byte result = (byte)intValue;
        if (result == intValue) {
            return result;
        }
        throw new NumberFormatException();
    }
}
