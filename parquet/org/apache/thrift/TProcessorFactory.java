// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import parquet.org.apache.thrift.transport.TTransport;

public class TProcessorFactory
{
    private final TProcessor processor_;
    
    public TProcessorFactory(final TProcessor processor) {
        this.processor_ = processor;
    }
    
    public TProcessor getProcessor(final TTransport trans) {
        return this.processor_;
    }
}
