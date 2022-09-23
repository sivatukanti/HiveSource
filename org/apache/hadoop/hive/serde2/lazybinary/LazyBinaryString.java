// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableStringObjectInspector;

public class LazyBinaryString extends LazyBinaryPrimitive<WritableStringObjectInspector, Text>
{
    LazyBinaryString(final WritableStringObjectInspector OI) {
        super(OI);
        this.data = (T)new Text();
    }
    
    public LazyBinaryString(final LazyBinaryString copy) {
        super(copy);
        this.data = (T)new Text((Text)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        assert length > -1;
        ((Text)this.data).set(bytes.getData(), start, length);
    }
}
