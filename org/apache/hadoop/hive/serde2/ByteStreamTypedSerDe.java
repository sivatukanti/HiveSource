// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;
import java.lang.reflect.Type;

public abstract class ByteStreamTypedSerDe extends TypedSerDe
{
    protected ByteStream.Input bis;
    protected ByteStream.Output bos;
    
    public ByteStreamTypedSerDe(final Type objectType) throws SerDeException {
        super(objectType);
        this.bos = new ByteStream.Output();
        this.bis = new ByteStream.Input();
    }
    
    @Override
    public Object deserialize(final Writable field) throws SerDeException {
        final Object retObj = super.deserialize(field);
        final BytesWritable b = (BytesWritable)field;
        this.bis.reset(b.getBytes(), b.getLength());
        return retObj;
    }
}
