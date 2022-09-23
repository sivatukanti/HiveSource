// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public abstract class LazyBinaryPrimitive<OI extends ObjectInspector, T extends Writable> extends LazyBinaryObject<OI>
{
    T data;
    
    LazyBinaryPrimitive(final OI oi) {
        super(oi);
    }
    
    LazyBinaryPrimitive(final LazyBinaryPrimitive<OI, T> copy) {
        super(copy.oi);
    }
    
    @Override
    public Object getObject() {
        return this.data;
    }
    
    public T getWritableObject() {
        return this.data;
    }
    
    @Override
    public String toString() {
        return this.data.toString();
    }
    
    @Override
    public int hashCode() {
        return (this.data == null) ? 0 : this.data.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof LazyBinaryPrimitive && (this.data == obj || (this.data != null && obj != null && this.data.equals(((LazyBinaryPrimitive)obj).getWritableObject())));
    }
}
