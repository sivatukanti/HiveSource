// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class FixedReceiveBufferSizePredictorFactory implements ReceiveBufferSizePredictorFactory
{
    private final ReceiveBufferSizePredictor predictor;
    
    public FixedReceiveBufferSizePredictorFactory(final int bufferSize) {
        this.predictor = new FixedReceiveBufferSizePredictor(bufferSize);
    }
    
    public ReceiveBufferSizePredictor getPredictor() throws Exception {
        return this.predictor;
    }
}
