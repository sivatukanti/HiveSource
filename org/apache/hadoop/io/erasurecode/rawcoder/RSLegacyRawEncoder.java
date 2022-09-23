// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import java.util.Arrays;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.rawcoder.util.RSUtil;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSLegacyRawEncoder extends RawErasureEncoder
{
    private int[] generatingPolynomial;
    
    public RSLegacyRawEncoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        assert this.getNumDataUnits() + this.getNumParityUnits() < RSUtil.GF.getFieldSize();
        final int[] primitivePower = RSUtil.getPrimitivePower(this.getNumDataUnits(), this.getNumParityUnits());
        int[] gen = { 1 };
        final int[] poly = new int[2];
        for (int i = 0; i < this.getNumParityUnits(); ++i) {
            poly[0] = primitivePower[i];
            poly[1] = 1;
            gen = RSUtil.GF.multiply(gen, poly);
        }
        this.generatingPolynomial = gen;
    }
    
    @Override
    protected void doEncode(final ByteBufferEncodingState encodingState) {
        CoderUtil.resetOutputBuffers(encodingState.outputs, encodingState.encodeLength);
        final ByteBuffer[] all = new ByteBuffer[encodingState.outputs.length + encodingState.inputs.length];
        if (this.allowChangeInputs()) {
            System.arraycopy(encodingState.outputs, 0, all, 0, encodingState.outputs.length);
            System.arraycopy(encodingState.inputs, 0, all, encodingState.outputs.length, encodingState.inputs.length);
        }
        else {
            System.arraycopy(encodingState.outputs, 0, all, 0, encodingState.outputs.length);
            for (int i = 0; i < encodingState.inputs.length; ++i) {
                final ByteBuffer tmp = ByteBuffer.allocate(encodingState.inputs[i].remaining());
                tmp.put(encodingState.inputs[i]);
                tmp.flip();
                all[encodingState.outputs.length + i] = tmp;
            }
        }
        RSUtil.GF.remainder(all, this.generatingPolynomial);
    }
    
    @Override
    protected void doEncode(final ByteArrayEncodingState encodingState) {
        final int dataLen = encodingState.encodeLength;
        CoderUtil.resetOutputBuffers(encodingState.outputs, encodingState.outputOffsets, dataLen);
        final byte[][] all = new byte[encodingState.outputs.length + encodingState.inputs.length][];
        final int[] allOffsets = new int[encodingState.outputOffsets.length + encodingState.inputOffsets.length];
        if (this.allowChangeInputs()) {
            System.arraycopy(encodingState.outputs, 0, all, 0, encodingState.outputs.length);
            System.arraycopy(encodingState.inputs, 0, all, encodingState.outputs.length, encodingState.inputs.length);
            System.arraycopy(encodingState.outputOffsets, 0, allOffsets, 0, encodingState.outputOffsets.length);
            System.arraycopy(encodingState.inputOffsets, 0, allOffsets, encodingState.outputOffsets.length, encodingState.inputOffsets.length);
        }
        else {
            System.arraycopy(encodingState.outputs, 0, all, 0, encodingState.outputs.length);
            System.arraycopy(encodingState.outputOffsets, 0, allOffsets, 0, encodingState.outputOffsets.length);
            for (int i = 0; i < encodingState.inputs.length; ++i) {
                all[encodingState.outputs.length + i] = Arrays.copyOfRange(encodingState.inputs[i], encodingState.inputOffsets[i], encodingState.inputOffsets[i] + dataLen);
            }
        }
        RSUtil.GF.remainder(all, allOffsets, dataLen, this.generatingPolynomial);
    }
}
