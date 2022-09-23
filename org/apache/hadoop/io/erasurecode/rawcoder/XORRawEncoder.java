// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class XORRawEncoder extends RawErasureEncoder
{
    public XORRawEncoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
    }
    
    @Override
    protected void doEncode(final ByteBufferEncodingState encodingState) {
        CoderUtil.resetOutputBuffers(encodingState.outputs, encodingState.encodeLength);
        final ByteBuffer output = encodingState.outputs[0];
        for (int iIdx = encodingState.inputs[0].position(), oIdx = output.position(); iIdx < encodingState.inputs[0].limit(); ++iIdx, ++oIdx) {
            output.put(oIdx, encodingState.inputs[0].get(iIdx));
        }
        for (int i = 1; i < encodingState.inputs.length; ++i) {
            for (int iIdx = encodingState.inputs[i].position(), oIdx = output.position(); iIdx < encodingState.inputs[i].limit(); ++iIdx, ++oIdx) {
                output.put(oIdx, (byte)(output.get(oIdx) ^ encodingState.inputs[i].get(iIdx)));
            }
        }
    }
    
    @Override
    protected void doEncode(final ByteArrayEncodingState encodingState) {
        final int dataLen = encodingState.encodeLength;
        CoderUtil.resetOutputBuffers(encodingState.outputs, encodingState.outputOffsets, dataLen);
        final byte[] output = encodingState.outputs[0];
        for (int iIdx = encodingState.inputOffsets[0], oIdx = encodingState.outputOffsets[0]; iIdx < encodingState.inputOffsets[0] + dataLen; ++iIdx, ++oIdx) {
            output[oIdx] = encodingState.inputs[0][iIdx];
        }
        for (int i = 1; i < encodingState.inputs.length; ++i) {
            for (int iIdx = encodingState.inputOffsets[i], oIdx = encodingState.outputOffsets[0]; iIdx < encodingState.inputOffsets[i] + dataLen; ++iIdx, ++oIdx) {
                final byte[] array = output;
                final int n = oIdx;
                array[n] ^= encodingState.inputs[i][iIdx];
            }
        }
    }
}
