// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import org.apache.avro.generic.GenericData;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilderBase;

public abstract class SpecificRecordBuilderBase<T extends SpecificRecord> extends RecordBuilderBase<T>
{
    protected SpecificRecordBuilderBase(final Schema schema) {
        super(schema, SpecificData.get());
    }
    
    protected SpecificRecordBuilderBase(final SpecificRecordBuilderBase<T> other) {
        super(other, SpecificData.get());
    }
    
    protected SpecificRecordBuilderBase(final T other) {
        super(other.getSchema(), SpecificData.get());
    }
}
