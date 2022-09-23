// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.transport.TTransport;

public class TProcessorFactory
{
    private final TProcessor processor_;
    
    public TProcessorFactory(final TProcessor processor) {
        this.processor_ = processor;
    }
    
    public TProcessor getProcessor(final TTransport trans) {
        return this.processor_;
    }
    
    public boolean isAsyncProcessor() {
        return this.processor_ instanceof TBaseAsyncProcessor;
    }
}
