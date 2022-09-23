// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.erasurecode.ECChunk;
import org.apache.hadoop.io.erasurecode.coder.util.HHUtil;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HHXORErasureDecodingStep extends HHErasureCodingStep
{
    private int pbIndex;
    private int[] piggyBackIndex;
    private int[] piggyBackFullIndex;
    private int[] erasedIndexes;
    private RawErasureDecoder rsRawDecoder;
    private RawErasureEncoder xorRawEncoder;
    
    public HHXORErasureDecodingStep(final ECBlock[] inputBlocks, final int[] erasedIndexes, final ECBlock[] outputBlocks, final RawErasureDecoder rawDecoder, final RawErasureEncoder rawEncoder) {
        super(inputBlocks, outputBlocks);
        this.pbIndex = rawDecoder.getNumParityUnits() - 1;
        this.erasedIndexes = erasedIndexes;
        this.rsRawDecoder = rawDecoder;
        this.xorRawEncoder = rawEncoder;
        this.piggyBackIndex = HHUtil.initPiggyBackIndexWithoutPBVec(rawDecoder.getNumDataUnits(), rawDecoder.getNumParityUnits());
        this.piggyBackFullIndex = HHUtil.initPiggyBackFullIndexVec(rawDecoder.getNumDataUnits(), this.piggyBackIndex);
    }
    
    @Override
    public void performCoding(final ECChunk[] inputChunks, final ECChunk[] outputChunks) throws IOException {
        if (this.erasedIndexes.length == 0) {
            return;
        }
        final ByteBuffer[] inputBuffers = ECChunk.toBuffers(inputChunks);
        final ByteBuffer[] outputBuffers = ECChunk.toBuffers(outputChunks);
        this.performCoding(inputBuffers, outputBuffers);
    }
    
    private void performCoding(final ByteBuffer[] inputs, final ByteBuffer[] outputs) throws IOException {
        final int numDataUnits = this.rsRawDecoder.getNumDataUnits();
        final int numParityUnits = this.rsRawDecoder.getNumParityUnits();
        final int numTotalUnits = numDataUnits + numParityUnits;
        final int subPacketSize = this.getSubPacketSize();
        final ByteBuffer fisrtValidInput = HHUtil.findFirstValidInput(inputs);
        final int bufSize = fisrtValidInput.remaining();
        if (inputs.length != numTotalUnits * this.getSubPacketSize()) {
            throw new IllegalArgumentException("Invalid inputs length");
        }
        if (outputs.length != this.erasedIndexes.length * this.getSubPacketSize()) {
            throw new IllegalArgumentException("Invalid outputs length");
        }
        final ByteBuffer[][] newIn = new ByteBuffer[subPacketSize][numTotalUnits];
        for (int i = 0; i < subPacketSize; ++i) {
            for (int j = 0; j < numTotalUnits; ++j) {
                newIn[i][j] = inputs[i * numTotalUnits + j];
            }
        }
        final ByteBuffer[][] newOut = new ByteBuffer[subPacketSize][this.erasedIndexes.length];
        for (int k = 0; k < subPacketSize; ++k) {
            for (int l = 0; l < this.erasedIndexes.length; ++l) {
                newOut[k][l] = outputs[k * this.erasedIndexes.length + l];
            }
        }
        if (this.erasedIndexes.length == 1 && this.erasedIndexes[0] < numDataUnits) {
            this.doDecodeSingle(newIn, newOut, this.erasedIndexes[0], bufSize, fisrtValidInput.isDirect());
        }
        else {
            this.doDecodeMultiAndParity(newIn, newOut, this.erasedIndexes, bufSize);
        }
    }
    
    private void doDecodeSingle(final ByteBuffer[][] inputs, final ByteBuffer[][] outputs, final int erasedLocationToFix, final int bufSize, final boolean isDirect) throws IOException {
        final int numDataUnits = this.rsRawDecoder.getNumDataUnits();
        final int numParityUnits = this.rsRawDecoder.getNumParityUnits();
        final int subPacketSize = this.getSubPacketSize();
        final int[][] inputPositions = new int[subPacketSize][inputs[0].length];
        for (int i = 0; i < subPacketSize; ++i) {
            for (int j = 0; j < inputs[i].length; ++j) {
                if (inputs[i][j] != null) {
                    inputPositions[i][j] = inputs[i][j].position();
                }
            }
        }
        final ByteBuffer[] tempInputs = new ByteBuffer[numDataUnits + numParityUnits];
        for (int k = 0; k < tempInputs.length; ++k) {
            tempInputs[k] = inputs[1][k];
        }
        final ByteBuffer[][] tmpOutputs = new ByteBuffer[subPacketSize][numParityUnits];
        for (int l = 0; l < this.getSubPacketSize(); ++l) {
            for (int m = 0; m < this.erasedIndexes.length; ++m) {
                tmpOutputs[l][m] = outputs[l][m];
            }
            for (int m2 = this.erasedIndexes.length; m2 < numParityUnits; ++m2) {
                tmpOutputs[l][m2] = HHUtil.allocateByteBuffer(isDirect, bufSize);
            }
        }
        final int[] erasedLocation = new int[numParityUnits];
        erasedLocation[0] = erasedLocationToFix;
        for (int i2 = 1; i2 < numParityUnits; ++i2) {
            tempInputs[erasedLocation[i2] = numDataUnits + i2] = null;
        }
        this.rsRawDecoder.decode(tempInputs, erasedLocation, tmpOutputs[1]);
        final int piggyBackParityIndex = this.piggyBackFullIndex[erasedLocationToFix];
        final ByteBuffer piggyBack = HHUtil.getPiggyBackForDecode(inputs, tmpOutputs, piggyBackParityIndex, numDataUnits, numParityUnits, this.pbIndex);
        if (isDirect) {
            final int idxToWrite = 0;
            this.doDecodeByPiggyBack(inputs[0], tmpOutputs[0][idxToWrite], piggyBack, erasedLocationToFix);
        }
        else {
            final byte[][][] newInputs = new byte[this.getSubPacketSize()][inputs[0].length][];
            final int[][] inputOffsets = new int[this.getSubPacketSize()][inputs[0].length];
            final byte[][][] newOutputs = new byte[this.getSubPacketSize()][numParityUnits][];
            final int[][] outOffsets = new int[this.getSubPacketSize()][numParityUnits];
            for (int i3 = 0; i3 < this.getSubPacketSize(); ++i3) {
                for (int j2 = 0; j2 < inputs[0].length; ++j2) {
                    final ByteBuffer buffer = inputs[i3][j2];
                    if (buffer != null) {
                        inputOffsets[i3][j2] = buffer.arrayOffset() + buffer.position();
                        newInputs[i3][j2] = buffer.array();
                    }
                }
            }
            for (int i3 = 0; i3 < this.getSubPacketSize(); ++i3) {
                for (int j2 = 0; j2 < numParityUnits; ++j2) {
                    final ByteBuffer buffer = tmpOutputs[i3][j2];
                    if (buffer != null) {
                        outOffsets[i3][j2] = buffer.arrayOffset() + buffer.position();
                        newOutputs[i3][j2] = buffer.array();
                    }
                }
            }
            final byte[] newPiggyBack = piggyBack.array();
            final int idxToWrite2 = 0;
            this.doDecodeByPiggyBack(newInputs[0], inputOffsets[0], newOutputs[0][idxToWrite2], outOffsets[0][idxToWrite2], newPiggyBack, erasedLocationToFix, bufSize);
        }
        for (int i4 = 0; i4 < subPacketSize; ++i4) {
            for (int j3 = 0; j3 < inputs[i4].length; ++j3) {
                if (inputs[i4][j3] != null) {
                    inputs[i4][j3].position(inputPositions[i4][j3] + bufSize);
                }
            }
        }
    }
    
    private void doDecodeByPiggyBack(final ByteBuffer[] inputs, final ByteBuffer outputs, final ByteBuffer piggyBack, final int erasedLocationToFix) {
        final int thisPiggyBackSetIdx = this.piggyBackFullIndex[erasedLocationToFix];
        final int startIndex = this.piggyBackIndex[thisPiggyBackSetIdx - 1];
        final int endIndex = this.piggyBackIndex[thisPiggyBackSetIdx];
        for (int bufSize = piggyBack.remaining(), i = piggyBack.position(); i < piggyBack.position() + bufSize; ++i) {
            for (int j = startIndex; j < endIndex; ++j) {
                if (inputs[j] != null) {
                    piggyBack.put(i, (byte)(piggyBack.get(i) ^ inputs[j].get(inputs[j].position() + i)));
                }
            }
            outputs.put(outputs.position() + i, piggyBack.get(i));
        }
    }
    
    private void doDecodeByPiggyBack(final byte[][] inputs, final int[] inputOffsets, final byte[] outputs, final int outOffset, final byte[] piggyBack, final int erasedLocationToFix, final int bufSize) {
        final int thisPiggyBackSetIdx = this.piggyBackFullIndex[erasedLocationToFix];
        final int startIndex = this.piggyBackIndex[thisPiggyBackSetIdx - 1];
        final int endIndex = this.piggyBackIndex[thisPiggyBackSetIdx];
        for (int i = 0; i < bufSize; ++i) {
            for (int j = startIndex; j < endIndex; ++j) {
                if (inputs[j] != null) {
                    piggyBack[i] ^= inputs[j][i + inputOffsets[j]];
                }
            }
            outputs[i + outOffset] = piggyBack[i];
        }
    }
    
    private void doDecodeMultiAndParity(final ByteBuffer[][] inputs, final ByteBuffer[][] outputs, final int[] erasedLocationToFix, final int bufSize) throws IOException {
        final int numDataUnits = this.rsRawDecoder.getNumDataUnits();
        final int numParityUnits = this.rsRawDecoder.getNumParityUnits();
        final int numTotalUnits = numDataUnits + numParityUnits;
        final int[] parityToFixFlag = new int[numTotalUnits];
        for (int i = 0; i < erasedLocationToFix.length; ++i) {
            if (erasedLocationToFix[i] >= numDataUnits) {
                parityToFixFlag[erasedLocationToFix[i]] = 1;
            }
        }
        final int[] inputPositions = new int[inputs[0].length];
        for (int j = 0; j < inputPositions.length; ++j) {
            if (inputs[0][j] != null) {
                inputPositions[j] = inputs[0][j].position();
            }
        }
        this.rsRawDecoder.decode(inputs[0], erasedLocationToFix, outputs[0]);
        for (int j = 0; j < inputs[0].length; ++j) {
            if (inputs[0][j] != null) {
                inputs[0][j].position(inputPositions[j]);
            }
        }
        final ByteBuffer[] tempInput = new ByteBuffer[numDataUnits];
        for (int k = 0; k < numDataUnits; ++k) {
            tempInput[k] = inputs[0][k];
        }
        for (int k = 0; k < erasedLocationToFix.length; ++k) {
            if (erasedLocationToFix[k] < numDataUnits) {
                tempInput[erasedLocationToFix[k]] = outputs[0][k];
            }
        }
        final ByteBuffer[] piggyBack = HHUtil.getPiggyBacksFromInput(tempInput, this.piggyBackIndex, numParityUnits, 0, this.xorRawEncoder);
        for (int l = numDataUnits + 1; l < numTotalUnits; ++l) {
            if (parityToFixFlag[l] == 0 && inputs[1][l] != null) {
                for (int m = inputs[1][l].position(), m2 = piggyBack[l - numDataUnits - 1].position(); m < inputs[1][l].limit(); ++m, ++m2) {
                    inputs[1][l].put(m, (byte)(inputs[1][l].get(m) ^ piggyBack[l - numDataUnits - 1].get(m2)));
                }
            }
        }
        this.rsRawDecoder.decode(inputs[1], erasedLocationToFix, outputs[1]);
        for (int l = 0; l < erasedLocationToFix.length; ++l) {
            if (erasedLocationToFix[l] < numTotalUnits && erasedLocationToFix[l] > numDataUnits) {
                final int parityIndex = erasedLocationToFix[l] - numDataUnits - 1;
                for (int k2 = outputs[1][l].position(), m3 = piggyBack[parityIndex].position(); k2 < outputs[1][l].limit(); ++k2, ++m3) {
                    outputs[1][l].put(k2, (byte)(outputs[1][l].get(k2) ^ piggyBack[parityIndex].get(m3)));
                }
            }
        }
        for (int i2 = 0; i2 < inputs[0].length; ++i2) {
            if (inputs[0][i2] != null) {
                inputs[0][i2].position(inputPositions[i2] + bufSize);
            }
        }
    }
}
