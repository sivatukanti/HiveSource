// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
class ByteBufferDecodingState extends DecodingState
{
    ByteBuffer[] inputs;
    ByteBuffer[] outputs;
    int[] erasedIndexes;
    boolean usingDirectBuffer;
    
    ByteBufferDecodingState(final RawErasureDecoder decoder, final ByteBuffer[] inputs, final int[] erasedIndexes, final ByteBuffer[] outputs) {
        this.decoder = decoder;
        this.inputs = inputs;
        this.outputs = outputs;
        this.erasedIndexes = erasedIndexes;
        final ByteBuffer validInput = CoderUtil.findFirstValidInput(inputs);
        this.decodeLength = validInput.remaining();
        this.usingDirectBuffer = validInput.isDirect();
        this.checkParameters(inputs, erasedIndexes, outputs);
        this.checkInputBuffers(inputs);
        this.checkOutputBuffers(outputs);
    }
    
    ByteBufferDecodingState(final RawErasureDecoder decoder, final int decodeLength, final int[] erasedIndexes, final ByteBuffer[] inputs, final ByteBuffer[] outputs) {
        this.decoder = decoder;
        this.decodeLength = decodeLength;
        this.erasedIndexes = erasedIndexes;
        this.inputs = inputs;
        this.outputs = outputs;
    }
    
    ByteArrayDecodingState convertToByteArrayState() {
        final int[] inputOffsets = new int[this.inputs.length];
        final int[] outputOffsets = new int[this.outputs.length];
        final byte[][] newInputs = new byte[this.inputs.length][];
        final byte[][] newOutputs = new byte[this.outputs.length][];
        for (int i = 0; i < this.inputs.length; ++i) {
            final ByteBuffer buffer = this.inputs[i];
            if (buffer != null) {
                inputOffsets[i] = buffer.arrayOffset() + buffer.position();
                newInputs[i] = buffer.array();
            }
        }
        for (int i = 0; i < this.outputs.length; ++i) {
            final ByteBuffer buffer = this.outputs[i];
            outputOffsets[i] = buffer.arrayOffset() + buffer.position();
            newOutputs[i] = buffer.array();
        }
        final ByteArrayDecodingState baeState = new ByteArrayDecodingState(this.decoder, this.decodeLength, this.erasedIndexes, newInputs, inputOffsets, newOutputs, outputOffsets);
        return baeState;
    }
    
    void checkInputBuffers(final ByteBuffer[] buffers) {
        int validInputs = 0;
        for (final ByteBuffer buffer : buffers) {
            if (buffer != null) {
                if (buffer.remaining() != this.decodeLength) {
                    throw new HadoopIllegalArgumentException("Invalid buffer, not of length " + this.decodeLength);
                }
                if (buffer.isDirect() != this.usingDirectBuffer) {
                    throw new HadoopIllegalArgumentException("Invalid buffer, isDirect should be " + this.usingDirectBuffer);
                }
                ++validInputs;
            }
        }
        if (validInputs < this.decoder.getNumDataUnits()) {
            throw new HadoopIllegalArgumentException("No enough valid inputs are provided, not recoverable");
        }
    }
    
    void checkOutputBuffers(final ByteBuffer[] buffers) {
        for (final ByteBuffer buffer : buffers) {
            if (buffer == null) {
                throw new HadoopIllegalArgumentException("Invalid buffer found, not allowing null");
            }
            if (buffer.remaining() != this.decodeLength) {
                throw new HadoopIllegalArgumentException("Invalid buffer, not of length " + this.decodeLength);
            }
            if (buffer.isDirect() != this.usingDirectBuffer) {
                throw new HadoopIllegalArgumentException("Invalid buffer, isDirect should be " + this.usingDirectBuffer);
            }
        }
    }
}
