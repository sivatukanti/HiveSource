// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.AvroRemoteException;

public abstract class SpecificExceptionBase extends AvroRemoteException implements SpecificRecord
{
    public SpecificExceptionBase() {
    }
    
    public SpecificExceptionBase(final Throwable value) {
        super(value);
    }
    
    public SpecificExceptionBase(final Object value) {
        super(value);
    }
    
    public SpecificExceptionBase(final Object value, final Throwable cause) {
        super(value, cause);
    }
    
    @Override
    public abstract Schema getSchema();
    
    @Override
    public abstract Object get(final int p0);
    
    @Override
    public abstract void put(final int p0, final Object p1);
    
    @Override
    public boolean equals(final Object that) {
        return that == this || (that instanceof SpecificExceptionBase && this.getClass() == that.getClass() && SpecificData.get().compare(this, that, this.getSchema()) == 0);
    }
    
    @Override
    public int hashCode() {
        return SpecificData.get().hashCode(this, this.getSchema());
    }
}
