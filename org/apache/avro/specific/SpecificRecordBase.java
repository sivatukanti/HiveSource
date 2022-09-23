// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

public abstract class SpecificRecordBase implements SpecificRecord, Comparable<SpecificRecord>, GenericRecord
{
    @Override
    public abstract Schema getSchema();
    
    @Override
    public abstract Object get(final int p0);
    
    @Override
    public abstract void put(final int p0, final Object p1);
    
    @Override
    public void put(final String fieldName, final Object value) {
        this.put(this.getSchema().getField(fieldName).pos(), value);
    }
    
    @Override
    public Object get(final String fieldName) {
        return this.get(this.getSchema().getField(fieldName).pos());
    }
    
    @Override
    public boolean equals(final Object that) {
        return that == this || (that instanceof SpecificRecord && this.getClass() == that.getClass() && SpecificData.get().compare(this, that, this.getSchema(), true) == 0);
    }
    
    @Override
    public int hashCode() {
        return SpecificData.get().hashCode(this, this.getSchema());
    }
    
    @Override
    public int compareTo(final SpecificRecord that) {
        return SpecificData.get().compare(this, that, this.getSchema());
    }
    
    @Override
    public String toString() {
        return SpecificData.get().toString(this);
    }
}
