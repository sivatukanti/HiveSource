// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.lazy.ByteArrayRef;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.hive.serde2.io.HiveVarcharWritable;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveVarcharObjectInspector;

public class LazyBinaryHiveVarchar extends LazyBinaryPrimitive<WritableHiveVarcharObjectInspector, HiveVarcharWritable>
{
    protected int maxLength;
    
    LazyBinaryHiveVarchar(final WritableHiveVarcharObjectInspector oi) {
        super(oi);
        this.maxLength = -1;
        this.maxLength = ((VarcharTypeInfo)oi.getTypeInfo()).getLength();
        this.data = (T)new HiveVarcharWritable();
    }
    
    LazyBinaryHiveVarchar(final LazyBinaryHiveVarchar copy) {
        super(copy);
        this.maxLength = -1;
        this.maxLength = copy.maxLength;
        this.data = (T)new HiveVarcharWritable((HiveVarcharWritable)copy.data);
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        final Text textValue = ((HiveVarcharWritable)this.data).getTextValue();
        textValue.set(bytes.getData(), start, length);
        ((HiveVarcharWritable)this.data).enforceMaxLength(this.maxLength);
    }
}
