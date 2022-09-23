// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.io.erasurecode.rawcoder.util.RSUtil;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSLegacyRawDecoder extends RawErasureDecoder
{
    private int[] errSignature;
    private int[] primitivePower;
    
    public RSLegacyRawDecoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        if (this.getNumAllUnits() >= RSUtil.GF.getFieldSize()) {
            throw new HadoopIllegalArgumentException("Invalid numDataUnits and numParityUnits");
        }
        this.errSignature = new int[this.getNumParityUnits()];
        this.primitivePower = RSUtil.getPrimitivePower(this.getNumDataUnits(), this.getNumParityUnits());
    }
    
    @Override
    public void decode(final ByteBuffer[] inputs, final int[] erasedIndexes, final ByteBuffer[] outputs) throws IOException {
        final ByteBuffer[] newInputs = new ByteBuffer[inputs.length];
        final int[] newErasedIndexes = new int[erasedIndexes.length];
        final ByteBuffer[] newOutputs = new ByteBuffer[outputs.length];
        this.adjustOrder(inputs, newInputs, erasedIndexes, newErasedIndexes, outputs, newOutputs);
        super.decode(newInputs, newErasedIndexes, newOutputs);
    }
    
    @Override
    public void decode(final byte[][] inputs, final int[] erasedIndexes, final byte[][] outputs) throws IOException {
        final byte[][] newInputs = new byte[inputs.length][];
        final int[] newErasedIndexes = new int[erasedIndexes.length];
        final byte[][] newOutputs = new byte[outputs.length][];
        this.adjustOrder(inputs, newInputs, erasedIndexes, newErasedIndexes, outputs, newOutputs);
        super.decode(newInputs, newErasedIndexes, newOutputs);
    }
    
    private void doDecodeImpl(final ByteBuffer[] inputs, final int[] erasedIndexes, final ByteBuffer[] outputs) {
        final ByteBuffer valid = CoderUtil.findFirstValidInput(inputs);
        final int dataLen = valid.remaining();
        for (int i = 0; i < erasedIndexes.length; ++i) {
            this.errSignature[i] = this.primitivePower[erasedIndexes[i]];
            RSUtil.GF.substitute(inputs, dataLen, outputs[i], this.primitivePower[i]);
        }
        RSUtil.GF.solveVandermondeSystem(this.errSignature, outputs, erasedIndexes.length);
    }
    
    private void doDecodeImpl(final byte[][] inputs, final int[] inputOffsets, final int dataLen, final int[] erasedIndexes, final byte[][] outputs, final int[] outputOffsets) {
        for (int i = 0; i < erasedIndexes.length; ++i) {
            this.errSignature[i] = this.primitivePower[erasedIndexes[i]];
            RSUtil.GF.substitute(inputs, inputOffsets, dataLen, outputs[i], outputOffsets[i], this.primitivePower[i]);
        }
        RSUtil.GF.solveVandermondeSystem(this.errSignature, outputs, outputOffsets, erasedIndexes.length, dataLen);
    }
    
    @Override
    protected void doDecode(final ByteArrayDecodingState decodingState) {
        final int dataLen = decodingState.decodeLength;
        CoderUtil.resetOutputBuffers(decodingState.outputs, decodingState.outputOffsets, dataLen);
        final byte[][] bytesArrayBuffers = new byte[this.getNumParityUnits()][];
        final byte[][] adjustedByteArrayOutputsParameter = new byte[this.getNumParityUnits()][];
        final int[] adjustedOutputOffsets = new int[this.getNumParityUnits()];
        final int[] erasedOrNotToReadIndexes = CoderUtil.getNullIndexes(decodingState.inputs);
        int outputIdx = 0;
        for (int i = 0; i < decodingState.erasedIndexes.length; ++i) {
            boolean found = false;
            for (int j = 0; j < erasedOrNotToReadIndexes.length; ++j) {
                if (decodingState.erasedIndexes[i] == erasedOrNotToReadIndexes[j]) {
                    found = true;
                    adjustedByteArrayOutputsParameter[j] = CoderUtil.resetBuffer(decodingState.outputs[outputIdx], decodingState.outputOffsets[outputIdx], dataLen);
                    adjustedOutputOffsets[j] = decodingState.outputOffsets[outputIdx];
                    ++outputIdx;
                }
            }
            if (!found) {
                throw new HadoopIllegalArgumentException("Inputs not fully corresponding to erasedIndexes in null places");
            }
        }
        int bufferIdx = 0;
        for (int i = 0; i < erasedOrNotToReadIndexes.length; ++i) {
            if (adjustedByteArrayOutputsParameter[i] == null) {
                adjustedByteArrayOutputsParameter[i] = CoderUtil.resetBuffer(checkGetBytesArrayBuffer(bytesArrayBuffers, bufferIdx, dataLen), 0, dataLen);
                adjustedOutputOffsets[i] = 0;
                ++bufferIdx;
            }
        }
        this.doDecodeImpl(decodingState.inputs, decodingState.inputOffsets, dataLen, erasedOrNotToReadIndexes, adjustedByteArrayOutputsParameter, adjustedOutputOffsets);
    }
    
    @Override
    protected void doDecode(final ByteBufferDecodingState decodingState) {
        final int dataLen = decodingState.decodeLength;
        CoderUtil.resetOutputBuffers(decodingState.outputs, dataLen);
        final int[] erasedOrNotToReadIndexes = CoderUtil.getNullIndexes(decodingState.inputs);
        final ByteBuffer[] directBuffers = new ByteBuffer[this.getNumParityUnits()];
        final ByteBuffer[] adjustedDirectBufferOutputsParameter = new ByteBuffer[this.getNumParityUnits()];
        int outputIdx = 0;
        for (int i = 0; i < decodingState.erasedIndexes.length; ++i) {
            boolean found = false;
            for (int j = 0; j < erasedOrNotToReadIndexes.length; ++j) {
                if (decodingState.erasedIndexes[i] == erasedOrNotToReadIndexes[j]) {
                    found = true;
                    adjustedDirectBufferOutputsParameter[j] = CoderUtil.resetBuffer(decodingState.outputs[outputIdx++], dataLen);
                }
            }
            if (!found) {
                throw new HadoopIllegalArgumentException("Inputs not fully corresponding to erasedIndexes in null places");
            }
        }
        int bufferIdx = 0;
        for (int i = 0; i < erasedOrNotToReadIndexes.length; ++i) {
            if (adjustedDirectBufferOutputsParameter[i] == null) {
                final ByteBuffer buffer = checkGetDirectBuffer(directBuffers, bufferIdx, dataLen);
                buffer.position(0);
                buffer.limit(dataLen);
                adjustedDirectBufferOutputsParameter[i] = CoderUtil.resetBuffer(buffer, dataLen);
                ++bufferIdx;
            }
        }
        this.doDecodeImpl(decodingState.inputs, erasedOrNotToReadIndexes, adjustedDirectBufferOutputsParameter);
    }
    
    private <T> void adjustOrder(final T[] inputs, final T[] inputs2, final int[] erasedIndexes, final int[] erasedIndexes2, final T[] outputs, final T[] outputs2) {
        System.arraycopy(inputs, this.getNumDataUnits(), inputs2, 0, this.getNumParityUnits());
        System.arraycopy(inputs, 0, inputs2, this.getNumParityUnits(), this.getNumDataUnits());
        int numErasedDataUnits = 0;
        int numErasedParityUnits = 0;
        int idx = 0;
        for (int i = 0; i < erasedIndexes.length; ++i) {
            if (erasedIndexes[i] >= this.getNumDataUnits()) {
                erasedIndexes2[idx++] = erasedIndexes[i] - this.getNumDataUnits();
                ++numErasedParityUnits;
            }
        }
        for (int i = 0; i < erasedIndexes.length; ++i) {
            if (erasedIndexes[i] < this.getNumDataUnits()) {
                erasedIndexes2[idx++] = erasedIndexes[i] + this.getNumParityUnits();
                ++numErasedDataUnits;
            }
        }
        System.arraycopy(outputs, numErasedDataUnits, outputs2, 0, numErasedParityUnits);
        System.arraycopy(outputs, 0, outputs2, numErasedParityUnits, numErasedDataUnits);
    }
    
    private static byte[] checkGetBytesArrayBuffer(final byte[][] bytesArrayBuffers, final int idx, final int bufferLen) {
        if (bytesArrayBuffers[idx] == null || bytesArrayBuffers[idx].length < bufferLen) {
            bytesArrayBuffers[idx] = new byte[bufferLen];
        }
        return bytesArrayBuffers[idx];
    }
    
    private static ByteBuffer checkGetDirectBuffer(final ByteBuffer[] directBuffers, final int idx, final int bufferLen) {
        if (directBuffers[idx] == null || directBuffers[idx].capacity() < bufferLen) {
            directBuffers[idx] = ByteBuffer.allocateDirect(bufferLen);
        }
        return directBuffers[idx];
    }
}
