// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class XORRawDecoder extends RawErasureDecoder
{
    public XORRawDecoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
    }
    
    @Override
    protected void doDecode(final ByteBufferDecodingState decodingState) {
        CoderUtil.resetOutputBuffers(decodingState.outputs, decodingState.decodeLength);
        final ByteBuffer output = decodingState.outputs[0];
        final int erasedIdx = decodingState.erasedIndexes[0];
        for (int i = 0; i < decodingState.inputs.length; ++i) {
            if (i != erasedIdx) {
                for (int iIdx = decodingState.inputs[i].position(), oIdx = output.position(); iIdx < decodingState.inputs[i].limit(); ++iIdx, ++oIdx) {
                    output.put(oIdx, (byte)(output.get(oIdx) ^ decodingState.inputs[i].get(iIdx)));
                }
            }
        }
    }
    
    @Override
    protected void doDecode(final ByteArrayDecodingState decodingState) {
        final byte[] output = decodingState.outputs[0];
        final int dataLen = decodingState.decodeLength;
        CoderUtil.resetOutputBuffers(decodingState.outputs, decodingState.outputOffsets, dataLen);
        final int erasedIdx = decodingState.erasedIndexes[0];
        for (int i = 0; i < decodingState.inputs.length; ++i) {
            if (i != erasedIdx) {
                for (int iIdx = decodingState.inputOffsets[i], oIdx = decodingState.outputOffsets[0]; iIdx < decodingState.inputOffsets[i] + dataLen; ++iIdx, ++oIdx) {
                    final byte[] array = output;
                    final int n = oIdx;
                    array[n] ^= decodingState.inputs[i][iIdx];
                }
            }
        }
    }
}
