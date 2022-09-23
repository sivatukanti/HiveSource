// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazybinary;

import org.apache.hadoop.hive.serde2.lazy.LazyObjectBase;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public abstract class LazyBinaryObject<OI extends ObjectInspector> implements LazyObjectBase
{
    OI oi;
    
    protected LazyBinaryObject(final OI oi) {
        this.oi = oi;
    }
    
    @Override
    public void setNull() {
        throw new IllegalStateException("should not be called");
    }
    
    @Override
    public abstract int hashCode();
}
