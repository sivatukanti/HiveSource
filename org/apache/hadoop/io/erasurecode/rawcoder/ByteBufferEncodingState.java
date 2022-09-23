// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
class ByteBufferEncodingState extends EncodingState
{
    ByteBuffer[] inputs;
    ByteBuffer[] outputs;
    boolean usingDirectBuffer;
    
    ByteBufferEncodingState(final RawErasureEncoder encoder, final ByteBuffer[] inputs, final ByteBuffer[] outputs) {
        this.encoder = encoder;
        final ByteBuffer validInput = CoderUtil.findFirstValidInput(inputs);
        this.encodeLength = validInput.remaining();
        this.usingDirectBuffer = validInput.isDirect();
        this.checkParameters(this.inputs = inputs, this.outputs = outputs);
        this.checkBuffers(inputs);
        this.checkBuffers(outputs);
    }
    
    ByteBufferEncodingState(final RawErasureEncoder encoder, final int encodeLength, final ByteBuffer[] inputs, final ByteBuffer[] outputs) {
        this.encoder = encoder;
        this.encodeLength = encodeLength;
        this.inputs = inputs;
        this.outputs = outputs;
    }
    
    ByteArrayEncodingState convertToByteArrayState() {
        final int[] inputOffsets = new int[this.inputs.length];
        final int[] outputOffsets = new int[this.outputs.length];
        final byte[][] newInputs = new byte[this.inputs.length][];
        final byte[][] newOutputs = new byte[this.outputs.length][];
        for (int i = 0; i < this.inputs.length; ++i) {
            final ByteBuffer buffer = this.inputs[i];
            inputOffsets[i] = buffer.arrayOffset() + buffer.position();
            newInputs[i] = buffer.array();
        }
        for (int i = 0; i < this.outputs.length; ++i) {
            final ByteBuffer buffer = this.outputs[i];
            outputOffsets[i] = buffer.arrayOffset() + buffer.position();
            newOutputs[i] = buffer.array();
        }
        final ByteArrayEncodingState baeState = new ByteArrayEncodingState(this.encoder, this.encodeLength, newInputs, inputOffsets, newOutputs, outputOffsets);
        return baeState;
    }
    
    void checkBuffers(final ByteBuffer[] buffers) {
        for (final ByteBuffer buffer : buffers) {
            if (buffer == null) {
                throw new HadoopIllegalArgumentException("Invalid buffer found, not allowing null");
            }
            if (buffer.remaining() != this.encodeLength) {
                throw new HadoopIllegalArgumentException("Invalid buffer, not of length " + this.encodeLength);
            }
            if (buffer.isDirect() != this.usingDirectBuffer) {
                throw new HadoopIllegalArgumentException("Invalid buffer, isDirect should be " + this.usingDirectBuffer);
            }
        }
    }
}
