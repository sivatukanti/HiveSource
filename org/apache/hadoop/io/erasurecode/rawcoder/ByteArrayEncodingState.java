// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
class ByteArrayEncodingState extends EncodingState
{
    byte[][] inputs;
    byte[][] outputs;
    int[] inputOffsets;
    int[] outputOffsets;
    
    ByteArrayEncodingState(final RawErasureEncoder encoder, final byte[][] inputs, final byte[][] outputs) {
        this.encoder = encoder;
        final byte[] validInput = CoderUtil.findFirstValidInput(inputs);
        this.encodeLength = validInput.length;
        this.checkParameters(this.inputs = inputs, this.outputs = outputs);
        this.checkBuffers(inputs);
        this.checkBuffers(outputs);
        this.inputOffsets = new int[inputs.length];
        this.outputOffsets = new int[outputs.length];
    }
    
    ByteArrayEncodingState(final RawErasureEncoder encoder, final int encodeLength, final byte[][] inputs, final int[] inputOffsets, final byte[][] outputs, final int[] outputOffsets) {
        this.encoder = encoder;
        this.encodeLength = encodeLength;
        this.inputs = inputs;
        this.outputs = outputs;
        this.inputOffsets = inputOffsets;
        this.outputOffsets = outputOffsets;
    }
    
    ByteBufferEncodingState convertToByteBufferState() {
        final ByteBuffer[] newInputs = new ByteBuffer[this.inputs.length];
        final ByteBuffer[] newOutputs = new ByteBuffer[this.outputs.length];
        for (int i = 0; i < this.inputs.length; ++i) {
            newInputs[i] = CoderUtil.cloneAsDirectByteBuffer(this.inputs[i], this.inputOffsets[i], this.encodeLength);
        }
        for (int i = 0; i < this.outputs.length; ++i) {
            newOutputs[i] = ByteBuffer.allocateDirect(this.encodeLength);
        }
        final ByteBufferEncodingState bbeState = new ByteBufferEncodingState(this.encoder, this.encodeLength, newInputs, newOutputs);
        return bbeState;
    }
    
    void checkBuffers(final byte[][] buffers) {
        for (final byte[] buffer : buffers) {
            if (buffer == null) {
                throw new HadoopIllegalArgumentException("Invalid buffer found, not allowing null");
            }
            if (buffer.length != this.encodeLength) {
                throw new HadoopIllegalArgumentException("Invalid buffer not of length " + this.encodeLength);
            }
        }
    }
}
