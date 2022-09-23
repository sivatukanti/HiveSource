// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.api;

import parquet.io.api.RecordConsumer;
import org.apache.hadoop.conf.Configuration;

public class DelegatingWriteSupport<T> extends WriteSupport<T>
{
    private final WriteSupport<T> delegate;
    
    public DelegatingWriteSupport(final WriteSupport<T> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public WriteContext init(final Configuration configuration) {
        return this.delegate.init(configuration);
    }
    
    @Override
    public void prepareForWrite(final RecordConsumer recordConsumer) {
        this.delegate.prepareForWrite(recordConsumer);
    }
    
    @Override
    public void write(final T record) {
        this.delegate.write(record);
    }
    
    @Override
    public FinalizedWriteContext finalizeWrite() {
        return this.delegate.finalizeWrite();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(" + this.delegate.toString() + ")";
    }
}
