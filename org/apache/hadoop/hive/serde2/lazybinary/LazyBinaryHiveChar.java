// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveCharWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveCharObjectInspector;

public class LazyBinaryHiveChar extends LazyBinaryPrimitive<WritableHiveCharObjectInspector, HiveCharWritable>
{
    protected int maxLength;
    
    LazyBinaryHiveChar(final WritableHiveCharObjectInspector oi) {
        super(oi);
        this.maxLength = -1;
        this.maxLength = ((CharTypeInfo)oi.getTypeInfo()).getLength();
        this.data = (T)new HiveCharWritable();
    }
    
    LazyBinaryHiveChar(final LazyBinaryHiveChar copy) {
        super(copy);
        this.maxLength = -1;
        this.maxLength = copy.maxLength;
        this.data = (T)new HiveCharWritable((HiveCharWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        final Text textValue = ((HiveCharWritable)this.data).getTextValue();
        textValue.set(bytes.getData(), start, length);
        ((HiveCharWritable)this.data).enforceMaxLength(this.maxLength);
    }
}
