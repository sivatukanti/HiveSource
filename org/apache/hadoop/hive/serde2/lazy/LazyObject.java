// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public abstract class LazyObject<OI extends ObjectInspector> implements LazyObjectBase
{
    protected OI oi;
    protected boolean isNull;
    
    protected LazyObject(final OI oi) {
        this.oi = oi;
    }
    
    @Override
    public abstract int hashCode();
    
    protected OI getInspector() {
        return this.oi;
    }
    
    protected void setInspector(final OI oi) {
        this.oi = oi;
    }
    
    @Override
    public void init(final ByteArrayRef bytes, final int start, final int length) {
        if (bytes == null) {
            throw new RuntimeException("bytes cannot be null!");
        }
        this.isNull = false;
    }
    
    @Override
    public void setNull() {
        this.isNull = true;
    }
    
    @Override
    public Object getObject() {
        return this.isNull ? null : this;
    }
}
