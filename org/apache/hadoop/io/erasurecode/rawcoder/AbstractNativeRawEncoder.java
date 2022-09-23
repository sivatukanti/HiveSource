// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.PerformanceAdvisory;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
abstract class AbstractNativeRawEncoder extends RawErasureEncoder
{
    public static Logger LOG;
    protected final ReentrantReadWriteLock encoderLock;
    private long nativeCoder;
    
    public AbstractNativeRawEncoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        this.encoderLock = new ReentrantReadWriteLock();
    }
    
    @Override
    protected void doEncode(final ByteBufferEncodingState encodingState) throws IOException {
        this.encoderLock.readLock().lock();
        try {
            if (this.nativeCoder == 0L) {
                throw new IOException(String.format("%s closed", this.getClass().getSimpleName()));
            }
            final int[] inputOffsets = new int[encodingState.inputs.length];
            final int[] outputOffsets = new int[encodingState.outputs.length];
            final int dataLen = encodingState.inputs[0].remaining();
            for (int i = 0; i < encodingState.inputs.length; ++i) {
                final ByteBuffer buffer = encodingState.inputs[i];
                inputOffsets[i] = buffer.position();
            }
            for (int i = 0; i < encodingState.outputs.length; ++i) {
                final ByteBuffer buffer = encodingState.outputs[i];
                outputOffsets[i] = buffer.position();
            }
            this.performEncodeImpl(encodingState.inputs, inputOffsets, dataLen, encodingState.outputs, outputOffsets);
        }
        finally {
            this.encoderLock.readLock().unlock();
        }
    }
    
    protected abstract void performEncodeImpl(final ByteBuffer[] p0, final int[] p1, final int p2, final ByteBuffer[] p3, final int[] p4) throws IOException;
    
    @Override
    protected void doEncode(final ByteArrayEncodingState encodingState) throws IOException {
        PerformanceAdvisory.LOG.debug("convertToByteBufferState is invoked, not efficiently. Please use direct ByteBuffer inputs/outputs");
        final ByteBufferEncodingState bbeState = encodingState.convertToByteBufferState();
        this.doEncode(bbeState);
        for (int i = 0; i < encodingState.outputs.length; ++i) {
            bbeState.outputs[i].get(encodingState.outputs[i], encodingState.outputOffsets[i], encodingState.encodeLength);
        }
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return true;
    }
    
    static {
        AbstractNativeRawEncoder.LOG = LoggerFactory.getLogger(AbstractNativeRawEncoder.class);
    }
}
