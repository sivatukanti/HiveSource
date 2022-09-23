// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

public class AdaptiveReceiveBufferSizePredictorFactory implements ReceiveBufferSizePredictorFactory
{
    private final int minimum;
    private final int initial;
    private final int maximum;
    
    public AdaptiveReceiveBufferSizePredictorFactory() {
        this(64, 1024, 65536);
    }
    
    public AdaptiveReceiveBufferSizePredictorFactory(final int minimum, final int initial, final int maximum) {
        if (minimum <= 0) {
            throw new IllegalArgumentException("minimum: " + minimum);
        }
        if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        }
        if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        }
        this.minimum = minimum;
        this.initial = initial;
        this.maximum = maximum;
    }
    
    public ReceiveBufferSizePredictor getPredictor() throws Exception {
        return new AdaptiveReceiveBufferSizePredictor(this.minimum, this.initial, this.maximum);
    }
}
