// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
class ByteArrayDecodingState extends DecodingState
{
    byte[][] inputs;
    int[] inputOffsets;
    int[] erasedIndexes;
    byte[][] outputs;
    int[] outputOffsets;
    
    ByteArrayDecodingState(final RawErasureDecoder decoder, final byte[][] inputs, final int[] erasedIndexes, final byte[][] outputs) {
        this.decoder = decoder;
        this.inputs = inputs;
        this.outputs = outputs;
        this.erasedIndexes = erasedIndexes;
        final byte[] validInput = CoderUtil.findFirstValidInput(inputs);
        this.decodeLength = validInput.length;
        this.checkParameters(inputs, erasedIndexes, outputs);
        this.checkInputBuffers(inputs);
        this.checkOutputBuffers(outputs);
        this.inputOffsets = new int[inputs.length];
        this.outputOffsets = new int[outputs.length];
    }
    
    ByteArrayDecodingState(final RawErasureDecoder decoder, final int decodeLength, final int[] erasedIndexes, final byte[][] inputs, final int[] inputOffsets, final byte[][] outputs, final int[] outputOffsets) {
        this.decoder = decoder;
        this.decodeLength = decodeLength;
        this.erasedIndexes = erasedIndexes;
        this.inputs = inputs;
        this.outputs = outputs;
        this.inputOffsets = inputOffsets;
        this.outputOffsets = outputOffsets;
    }
    
    ByteBufferDecodingState convertToByteBufferState() {
        final ByteBuffer[] newInputs = new ByteBuffer[this.inputs.length];
        final ByteBuffer[] newOutputs = new ByteBuffer[this.outputs.length];
        for (int i = 0; i < this.inputs.length; ++i) {
            newInputs[i] = CoderUtil.cloneAsDirectByteBuffer(this.inputs[i], this.inputOffsets[i], this.decodeLength);
        }
        for (int i = 0; i < this.outputs.length; ++i) {
            newOutputs[i] = ByteBuffer.allocateDirect(this.decodeLength);
        }
        final ByteBufferDecodingState bbdState = new ByteBufferDecodingState(this.decoder, this.decodeLength, this.erasedIndexes, newInputs, newOutputs);
        return bbdState;
    }
    
    void checkInputBuffers(final byte[][] buffers) {
        int validInputs = 0;
        for (final byte[] buffer : buffers) {
            if (buffer != null) {
                if (buffer.length != this.decodeLength) {
                    throw new HadoopIllegalArgumentException("Invalid buffer, not of length " + this.decodeLength);
                }
                ++validInputs;
            }
        }
        if (validInputs < this.decoder.getNumDataUnits()) {
            throw new HadoopIllegalArgumentException("No enough valid inputs are provided, not recoverable");
        }
    }
    
    void checkOutputBuffers(final byte[][] buffers) {
        for (final byte[] buffer : buffers) {
            if (buffer == null) {
                throw new HadoopIllegalArgumentException("Invalid buffer found, not allowing null");
            }
            if (buffer.length != this.decodeLength) {
                throw new HadoopIllegalArgumentException("Invalid buffer not of length " + this.decodeLength);
            }
        }
    }
}
