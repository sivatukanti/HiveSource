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
abstract class AbstractNativeRawDecoder extends RawErasureDecoder
{
    public static Logger LOG;
    protected final ReentrantReadWriteLock decoderLock;
    private long nativeCoder;
    
    public AbstractNativeRawDecoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        this.decoderLock = new ReentrantReadWriteLock();
    }
    
    @Override
    protected void doDecode(final ByteBufferDecodingState decodingState) throws IOException {
        this.decoderLock.readLock().lock();
        try {
            if (this.nativeCoder == 0L) {
                throw new IOException(String.format("%s closed", this.getClass().getSimpleName()));
            }
            final int[] inputOffsets = new int[decodingState.inputs.length];
            final int[] outputOffsets = new int[decodingState.outputs.length];
            for (int i = 0; i < decodingState.inputs.length; ++i) {
                final ByteBuffer buffer = decodingState.inputs[i];
                if (buffer != null) {
                    inputOffsets[i] = buffer.position();
                }
            }
            for (int i = 0; i < decodingState.outputs.length; ++i) {
                final ByteBuffer buffer = decodingState.outputs[i];
                outputOffsets[i] = buffer.position();
            }
            this.performDecodeImpl(decodingState.inputs, inputOffsets, decodingState.decodeLength, decodingState.erasedIndexes, decodingState.outputs, outputOffsets);
        }
        finally {
            this.decoderLock.readLock().unlock();
        }
    }
    
    protected abstract void performDecodeImpl(final ByteBuffer[] p0, final int[] p1, final int p2, final int[] p3, final ByteBuffer[] p4, final int[] p5) throws IOException;
    
    @Override
    protected void doDecode(final ByteArrayDecodingState decodingState) throws IOException {
        PerformanceAdvisory.LOG.debug("convertToByteBufferState is invoked, not efficiently. Please use direct ByteBuffer inputs/outputs");
        final ByteBufferDecodingState bbdState = decodingState.convertToByteBufferState();
        this.doDecode(bbdState);
        for (int i = 0; i < decodingState.outputs.length; ++i) {
            bbdState.outputs[i].get(decodingState.outputs[i], decodingState.outputOffsets[i], decodingState.decodeLength);
        }
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return true;
    }
    
    static {
        AbstractNativeRawDecoder.LOG = LoggerFactory.getLogger(AbstractNativeRawDecoder.class);
    }
}
