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
public class NativeXORRawEncoder extends AbstractNativeRawEncoder
{
    public NativeXORRawEncoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        this.encoderLock.writeLock().lock();
        try {
            this.initImpl(coderOptions.getNumDataUnits(), coderOptions.getNumParityUnits());
        }
        finally {
            this.encoderLock.writeLock().unlock();
        }
    }
    
    @Override
    protected void performEncodeImpl(final ByteBuffer[] inputs, final int[] inputOffsets, final int dataLen, final ByteBuffer[] outputs, final int[] outputOffsets) throws IOException {
        this.encodeImpl(inputs, inputOffsets, dataLen, outputs, outputOffsets);
    }
    
    @Override
    public void release() {
        this.encoderLock.writeLock().lock();
        try {
            this.destroyImpl();
        }
        finally {
            this.encoderLock.writeLock().unlock();
        }
    }
    
    private native void initImpl(final int p0, final int p1);
    
    private native void encodeImpl(final ByteBuffer[] p0, final int[] p1, final int p2, final ByteBuffer[] p3, final int[] p4) throws IOException;
    
    private native void destroyImpl();
    
    static {
        ErasureCodeNative.checkNativeCodeLoaded();
    }
}
