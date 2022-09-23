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
public abstract class RawErasureEncoder
{
    private final ErasureCoderOptions coderOptions;
    
    public RawErasureEncoder(final ErasureCoderOptions coderOptions) {
        this.coderOptions = coderOptions;
    }
    
    public void encode(final ByteBuffer[] inputs, final ByteBuffer[] outputs) throws IOException {
        final ByteBufferEncodingState bbeState = new ByteBufferEncodingState(this, inputs, outputs);
        final boolean usingDirectBuffer = bbeState.usingDirectBuffer;
        final int dataLen = bbeState.encodeLength;
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
            this.doEncode(bbeState);
        }
        else {
            final ByteArrayEncodingState baeState = bbeState.convertToByteArrayState();
            this.doEncode(baeState);
        }
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] != null) {
                inputs[i].position(inputPositions[i] + dataLen);
            }
        }
    }
    
    protected abstract void doEncode(final ByteBufferEncodingState p0) throws IOException;
    
    public void encode(final byte[][] inputs, final byte[][] outputs) throws IOException {
        final ByteArrayEncodingState baeState = new ByteArrayEncodingState(this, inputs, outputs);
        final int dataLen = baeState.encodeLength;
        if (dataLen == 0) {
            return;
        }
        this.doEncode(baeState);
    }
    
    protected abstract void doEncode(final ByteArrayEncodingState p0) throws IOException;
    
    public void encode(final ECChunk[] inputs, final ECChunk[] outputs) throws IOException {
        final ByteBuffer[] newInputs = ECChunk.toBuffers(inputs);
        final ByteBuffer[] newOutputs = ECChunk.toBuffers(outputs);
        this.encode(newInputs, newOutputs);
    }
    
    public int getNumDataUnits() {
        return this.coderOptions.getNumDataUnits();
    }
    
    public int getNumParityUnits() {
        return this.coderOptions.getNumParityUnits();
    }
    
    public int getNumAllUnits() {
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
