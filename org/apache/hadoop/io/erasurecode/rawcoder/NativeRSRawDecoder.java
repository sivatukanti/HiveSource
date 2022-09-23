// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCodeNative;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class NativeRSRawDecoder extends AbstractNativeRawDecoder
{
    public NativeRSRawDecoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        this.decoderLock.writeLock().lock();
        try {
            this.initImpl(coderOptions.getNumDataUnits(), coderOptions.getNumParityUnits());
        }
        finally {
            this.decoderLock.writeLock().unlock();
        }
    }
    
    @Override
    protected void performDecodeImpl(final ByteBuffer[] inputs, final int[] inputOffsets, final int dataLen, final int[] erased, final ByteBuffer[] outputs, final int[] outputOffsets) throws IOException {
        this.decodeImpl(inputs, inputOffsets, dataLen, erased, outputs, outputOffsets);
    }
    
    @Override
    public void release() {
        this.decoderLock.writeLock().lock();
        try {
            this.destroyImpl();
        }
        finally {
            this.decoderLock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return true;
    }
    
    private native void initImpl(final int p0, final int p1);
    
    private native void decodeImpl(final ByteBuffer[] p0, final int[] p1, final int p2, final int[] p3, final ByteBuffer[] p4, final int[] p5) throws IOException;
    
    private native void destroyImpl();
    
    static {
        ErasureCodeNative.checkNativeCodeLoaded();
    }
}
