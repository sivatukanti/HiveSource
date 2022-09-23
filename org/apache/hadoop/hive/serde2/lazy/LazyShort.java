// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.hive.serde2.io.ShortWritable;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyShortObjectInspector;

public class LazyShort extends LazyPrimitive<LazyShortObjectInspector, ShortWritable>
{
    public LazyShort(final LazyShortObjectInspector oi) {
        super(oi);
        this.data = (T)new ShortWritable();
    }
    
    public LazyShort(final LazyShort copy) {
        super(copy);
        this.data = (T)new ShortWritable(((ShortWritable)copy.data).get());
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        try {
            ((ShortWritable)this.data).set(parseShort(bytes.getData(), start, length));
            this.isNull = false;
        }
        catch (NumberFormatException e) {
            this.isNull = true;
            this.logExceptionMessage(bytes, start, length, "SMALLINT");
        }
    }
    
    public static short parseShort(final byte[] bytes, final int start, final int length) {
        return parseShort(bytes, start, length, 10);
    }
    
    public static short parseShort(final byte[] bytes, final int start, final int length, final int radix) {
        final int intValue = LazyInteger.parseInt(bytes, start, length, radix);
        final short result = (short)intValue;
        if (result == intValue) {
            return result;
        }
        throw new NumberFormatException();
    }
}
