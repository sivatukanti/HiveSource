// 
// Decompiled by Procyon v0.5.36
// 

package parquet.filter2.recordlevel;

import parquet.io.api.Binary;
import parquet.column.Dictionary;
import parquet.Preconditions;
import parquet.io.api.PrimitiveConverter;

public class FilteringPrimitiveConverter extends PrimitiveConverter
{
    private final PrimitiveConverter delegate;
    private final IncrementallyUpdatedFilterPredicate.ValueInspector[] valueInspectors;
    
    public FilteringPrimitiveConverter(final PrimitiveConverter delegate, final IncrementallyUpdatedFilterPredicate.ValueInspector[] valueInspectors) {
        this.delegate = Preconditions.checkNotNull(delegate, "delegate");
        this.valueInspectors = Preconditions.checkNotNull(valueInspectors, "valueInspectors");
    }
    
    @Override
    public boolean hasDictionarySupport() {
        return false;
    }
    
    @Override
    public void setDictionary(final Dictionary dictionary) {
        throw new UnsupportedOperationException("FilteringPrimitiveConverter doesn't have dictionary support");
    }
    
    @Override
    public void addValueFromDictionary(final int dictionaryId) {
        throw new UnsupportedOperationException("FilteringPrimitiveConverter doesn't have dictionary support");
    }
    
    @Override
    public void addBinary(final Binary value) {
        for (final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector : this.valueInspectors) {
            valueInspector.update(value);
        }
        this.delegate.addBinary(value);
    }
    
    @Override
    public void addBoolean(final boolean value) {
        for (final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector : this.valueInspectors) {
            valueInspector.update(value);
        }
        this.delegate.addBoolean(value);
    }
    
    @Override
    public void addDouble(final double value) {
        for (final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector : this.valueInspectors) {
            valueInspector.update(value);
        }
        this.delegate.addDouble(value);
    }
    
    @Override
    public void addFloat(final float value) {
        for (final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector : this.valueInspectors) {
            valueInspector.update(value);
        }
        this.delegate.addFloat(value);
    }
    
    @Override
    public void addInt(final int value) {
        for (final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector : this.valueInspectors) {
            valueInspector.update(value);
        }
        this.delegate.addInt(value);
    }
    
    @Override
    public void addLong(final long value) {
        for (final IncrementallyUpdatedFilterPredicate.ValueInspector valueInspector : this.valueInspectors) {
            valueInspector.update(value);
        }
        this.delegate.addLong(value);
    }
}
