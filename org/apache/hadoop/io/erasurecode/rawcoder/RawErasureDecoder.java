// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ECChunk;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class RawErasureDecoder
{
    private final ErasureCoderOptions coderOptions;
    
    public RawErasureDecoder(final ErasureCoderOptions coderOptions) {
        this.coderOptions = coderOptions;
    }
    
    public void decode(final ByteBuffer[] inputs, final int[] erasedIndexes, final ByteBuffer[] outputs) throws IOException {
        final ByteBufferDecodingState decodingState = new ByteBufferDecodingState(this, inputs, erasedIndexes, outputs);
        final boolean usingDirectBuffer = decodingState.usingDirectBuffer;
        final int dataLen = decodingState.decodeLength;
        if (dataLen == 0) {
            return;
        }
        final int[] inputPositions = new int[inputs.length];
        for (int i = 0; i < inputPositions.length; ++i) {
            if (inputs[i] != null) {
                inputPositions[i] = inputs[i].position();
            }
        }
        if (usingDirectBuffer) {
            this.doDecode(decodingState);
        }
        else {
            final ByteArrayDecodingState badState = decodingState.convertToByteArrayState();
            this.doDecode(badState);
        }
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] != null) {
                inputs[i].position(inputPositions[i] + dataLen);
            }
        }
    }
    
    protected abstract void doDecode(final ByteBufferDecodingState p0) throws IOException;
    
    public void decode(final byte[][] inputs, final int[] erasedIndexes, final byte[][] outputs) throws IOException {
        final ByteArrayDecodingState decodingState = new ByteArrayDecodingState(this, inputs, erasedIndexes, outputs);
        if (decodingState.decodeLength == 0) {
            return;
        }
        this.doDecode(decodingState);
    }
    
    protected abstract void doDecode(final ByteArrayDecodingState p0) throws IOException;
    
    public void decode(final ECChunk[] inputs, final int[] erasedIndexes, final ECChunk[] outputs) throws IOException {
        final ByteBuffer[] newInputs = CoderUtil.toBuffers(inputs);
        final ByteBuffer[] newOutputs = CoderUtil.toBuffers(outputs);
        this.decode(newInputs, erasedIndexes, newOutputs);
    }
    
    public int getNumDataUnits() {
        return this.coderOptions.getNumDataUnits();
    }
    
    public int getNumParityUnits() {
        return this.coderOptions.getNumParityUnits();
    }
    
    protected int getNumAllUnits() {
        return this.coderOptions.getNumAllUnits();
    }
    
    public boolean preferDirectBuffer() {
        return false;
    }
    
    public boolean allowChangeInputs() {
        return this.coderOptions.allowChangeInputs();
    }
    
    public boolean allowVerboseDump() {
        return this.coderOptions.allowVerboseDump();
    }
    
    public void release() {
    }
}
