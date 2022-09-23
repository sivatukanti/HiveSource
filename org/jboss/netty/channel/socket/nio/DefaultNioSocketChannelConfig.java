// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictorFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.util.internal.ConversionUtil;
import java.util.Map;
import java.net.Socket;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import org.jboss.netty.channel.ReceiveBufferSizePredictorFactory;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.channel.socket.DefaultSocketChannelConfig;

class DefaultNioSocketChannelConfig extends DefaultSocketChannelConfig implements NioSocketChannelConfig
{
    private static final InternalLogger logger;
    private static final ReceiveBufferSizePredictorFactory DEFAULT_PREDICTOR_FACTORY;
    private volatile int writeBufferHighWaterMark;
    private volatile int writeBufferLowWaterMark;
    private volatile ReceiveBufferSizePredictor predictor;
    private volatile ReceiveBufferSizePredictorFactory predictorFactory;
    private volatile int writeSpinCount;
    
    DefaultNioSocketChannelConfig(final Socket socket) {
        super(socket);
        this.writeBufferHighWaterMark = 65536;
        this.writeBufferLowWaterMark = 32768;
        this.predictorFactory = DefaultNioSocketChannelConfig.DEFAULT_PREDICTOR_FACTORY;
        this.writeSpinCount = 16;
    }
    
    @Override
    public void setOptions(final Map<String, Object> options) {
        super.setOptions(options);
        if (this.getWriteBufferHighWaterMark() < this.getWriteBufferLowWaterMark()) {
            this.setWriteBufferLowWaterMark0(this.getWriteBufferHighWaterMark() >>> 1);
            if (DefaultNioSocketChannelConfig.logger.isWarnEnabled()) {
                DefaultNioSocketChannelConfig.logger.warn("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark; setting to the half of the writeBufferHighWaterMark.");
            }
        }
    }
    
    @Override
    public boolean setOption(final String key, final Object value) {
        if (super.setOption(key, value)) {
            return true;
        }
        if ("writeBufferHighWaterMark".equals(key)) {
            this.setWriteBufferHighWaterMark0(ConversionUtil.toInt(value));
        }
        else if ("writeBufferLowWaterMark".equals(key)) {
            this.setWriteBufferLowWaterMark0(ConversionUtil.toInt(value));
        }
        else if ("writeSpinCount".equals(key)) {
            this.setWriteSpinCount(ConversionUtil.toInt(value));
        }
        else if ("receiveBufferSizePredictorFactory".equals(key)) {
            this.setReceiveBufferSizePredictorFactory((ReceiveBufferSizePredictorFactory)value);
        }
        else {
            if (!"receiveBufferSizePredictor".equals(key)) {
                return false;
            }
            this.setReceiveBufferSizePredictor((ReceiveBufferSizePredictor)value);
        }
        return true;
    }
    
    public int getWriteBufferHighWaterMark() {
        return this.writeBufferHighWaterMark;
    }
    
    public void setWriteBufferHighWaterMark(final int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < this.getWriteBufferLowWaterMark()) {
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + this.getWriteBufferLowWaterMark() + "): " + writeBufferHighWaterMark);
        }
        this.setWriteBufferHighWaterMark0(writeBufferHighWaterMark);
    }
    
    private void setWriteBufferHighWaterMark0(final int writeBufferHighWaterMark) {
        if (writeBufferHighWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferHighWaterMark: " + writeBufferHighWaterMark);
        }
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }
    
    public int getWriteBufferLowWaterMark() {
        return this.writeBufferLowWaterMark;
    }
    
    public void setWriteBufferLowWaterMark(final int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark > this.getWriteBufferHighWaterMark()) {
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + this.getWriteBufferHighWaterMark() + "): " + writeBufferLowWaterMark);
        }
        this.setWriteBufferLowWaterMark0(writeBufferLowWaterMark);
    }
    
    private void setWriteBufferLowWaterMark0(final int writeBufferLowWaterMark) {
        if (writeBufferLowWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferLowWaterMark: " + writeBufferLowWaterMark);
        }
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }
    
    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }
    
    public void setWriteSpinCount(final int writeSpinCount) {
        if (writeSpinCount <= 0) {
            throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
        }
        this.writeSpinCount = writeSpinCount;
    }
    
    public ReceiveBufferSizePredictor getReceiveBufferSizePredictor() {
        ReceiveBufferSizePredictor predictor = this.predictor;
        if (predictor == null) {
            try {
                predictor = (this.predictor = this.getReceiveBufferSizePredictorFactory().getPredictor());
            }
            catch (Exception e) {
                throw new ChannelException("Failed to create a new " + ReceiveBufferSizePredictor.class.getSimpleName() + '.', e);
            }
        }
        return predictor;
    }
    
    public void setReceiveBufferSizePredictor(final ReceiveBufferSizePredictor predictor) {
        if (predictor == null) {
            throw new NullPointerException("predictor");
        }
        this.predictor = predictor;
    }
    
    public ReceiveBufferSizePredictorFactory getReceiveBufferSizePredictorFactory() {
        return this.predictorFactory;
    }
    
    public void setReceiveBufferSizePredictorFactory(final ReceiveBufferSizePredictorFactory predictorFactory) {
        if (predictorFactory == null) {
            throw new NullPointerException("predictorFactory");
        }
        this.predictorFactory = predictorFactory;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DefaultNioSocketChannelConfig.class);
        DEFAULT_PREDICTOR_FACTORY = new AdaptiveReceiveBufferSizePredictorFactory();
    }
}
