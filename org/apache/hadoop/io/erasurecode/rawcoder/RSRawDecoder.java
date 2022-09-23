// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.rawcoder.util.GF256;
import java.util.Arrays;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.rawcoder.util.DumpUtil;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.io.erasurecode.rawcoder.util.RSUtil;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSRawDecoder extends RawErasureDecoder
{
    private byte[] encodeMatrix;
    private byte[] decodeMatrix;
    private byte[] invertMatrix;
    private byte[] gfTables;
    private int[] cachedErasedIndexes;
    private int[] validIndexes;
    private int numErasedDataUnits;
    private boolean[] erasureFlags;
    
    public RSRawDecoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        final int numAllUnits = this.getNumAllUnits();
        if (this.getNumAllUnits() >= RSUtil.GF.getFieldSize()) {
            throw new HadoopIllegalArgumentException("Invalid getNumDataUnits() and numParityUnits");
        }
        RSUtil.genCauchyMatrix(this.encodeMatrix = new byte[numAllUnits * this.getNumDataUnits()], numAllUnits, this.getNumDataUnits());
        if (this.allowVerboseDump()) {
            DumpUtil.dumpMatrix(this.encodeMatrix, this.getNumDataUnits(), numAllUnits);
        }
    }
    
    @Override
    protected void doDecode(final ByteBufferDecodingState decodingState) {
        CoderUtil.resetOutputBuffers(decodingState.outputs, decodingState.decodeLength);
        this.prepareDecoding(decodingState.inputs, decodingState.erasedIndexes);
        final ByteBuffer[] realInputs = new ByteBuffer[this.getNumDataUnits()];
        for (int i = 0; i < this.getNumDataUnits(); ++i) {
            realInputs[i] = decodingState.inputs[this.validIndexes[i]];
        }
        RSUtil.encodeData(this.gfTables, realInputs, decodingState.outputs);
    }
    
    @Override
    protected void doDecode(final ByteArrayDecodingState decodingState) {
        final int dataLen = decodingState.decodeLength;
        CoderUtil.resetOutputBuffers(decodingState.outputs, decodingState.outputOffsets, dataLen);
        this.prepareDecoding(decodingState.inputs, decodingState.erasedIndexes);
        final byte[][] realInputs = new byte[this.getNumDataUnits()][];
        final int[] realInputOffsets = new int[this.getNumDataUnits()];
        for (int i = 0; i < this.getNumDataUnits(); ++i) {
            realInputs[i] = decodingState.inputs[this.validIndexes[i]];
            realInputOffsets[i] = decodingState.inputOffsets[this.validIndexes[i]];
        }
        RSUtil.encodeData(this.gfTables, dataLen, realInputs, realInputOffsets, decodingState.outputs, decodingState.outputOffsets);
    }
    
    private <T> void prepareDecoding(final T[] inputs, final int[] erasedIndexes) {
        final int[] tmpValidIndexes = CoderUtil.getValidIndexes(inputs);
        if (Arrays.equals(this.cachedErasedIndexes, erasedIndexes) && Arrays.equals(this.validIndexes, tmpValidIndexes)) {
            return;
        }
        this.cachedErasedIndexes = Arrays.copyOf(erasedIndexes, erasedIndexes.length);
        this.validIndexes = Arrays.copyOf(tmpValidIndexes, tmpValidIndexes.length);
        this.processErasures(erasedIndexes);
    }
    
    private void processErasures(final int[] erasedIndexes) {
        this.decodeMatrix = new byte[this.getNumAllUnits() * this.getNumDataUnits()];
        this.invertMatrix = new byte[this.getNumAllUnits() * this.getNumDataUnits()];
        this.gfTables = new byte[this.getNumAllUnits() * this.getNumDataUnits() * 32];
        this.erasureFlags = new boolean[this.getNumAllUnits()];
        this.numErasedDataUnits = 0;
        for (int i = 0; i < erasedIndexes.length; ++i) {
            final int index = erasedIndexes[i];
            this.erasureFlags[index] = true;
            if (index < this.getNumDataUnits()) {
                ++this.numErasedDataUnits;
            }
        }
        this.generateDecodeMatrix(erasedIndexes);
        RSUtil.initTables(this.getNumDataUnits(), erasedIndexes.length, this.decodeMatrix, 0, this.gfTables);
        if (this.allowVerboseDump()) {
            System.out.println(DumpUtil.bytesToHex(this.gfTables, -1));
        }
    }
    
    private void generateDecodeMatrix(final int[] erasedIndexes) {
        final byte[] tmpMatrix = new byte[this.getNumAllUnits() * this.getNumDataUnits()];
        for (int i = 0; i < this.getNumDataUnits(); ++i) {
            final int r = this.validIndexes[i];
            for (int j = 0; j < this.getNumDataUnits(); ++j) {
                tmpMatrix[this.getNumDataUnits() * i + j] = this.encodeMatrix[this.getNumDataUnits() * r + j];
            }
        }
        GF256.gfInvertMatrix(tmpMatrix, this.invertMatrix, this.getNumDataUnits());
        for (int i = 0; i < this.numErasedDataUnits; ++i) {
            for (int j = 0; j < this.getNumDataUnits(); ++j) {
                this.decodeMatrix[this.getNumDataUnits() * i + j] = this.invertMatrix[this.getNumDataUnits() * erasedIndexes[i] + j];
            }
        }
        for (int p = this.numErasedDataUnits; p < erasedIndexes.length; ++p) {
            for (int i = 0; i < this.getNumDataUnits(); ++i) {
                byte s = 0;
                for (int j = 0; j < this.getNumDataUnits(); ++j) {
                    s ^= GF256.gfMul(this.invertMatrix[j * this.getNumDataUnits() + i], this.encodeMatrix[this.getNumDataUnits() * erasedIndexes[p] + j]);
                }
                this.decodeMatrix[this.getNumDataUnits() * p + i] = s;
            }
        }
    }
}
