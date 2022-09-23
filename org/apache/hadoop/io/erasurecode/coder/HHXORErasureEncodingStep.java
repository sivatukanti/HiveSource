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
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HHXORErasureEncodingStep extends HHErasureCodingStep
{
    private int[] piggyBackIndex;
    private RawErasureEncoder rsRawEncoder;
    private RawErasureEncoder xorRawEncoder;
    
    public HHXORErasureEncodingStep(final ECBlock[] inputBlocks, final ECBlock[] outputBlocks, final RawErasureEncoder rsRawEncoder, final RawErasureEncoder xorRawEncoder) {
        super(inputBlocks, outputBlocks);
        this.rsRawEncoder = rsRawEncoder;
        this.xorRawEncoder = xorRawEncoder;
        this.piggyBackIndex = HHUtil.initPiggyBackIndexWithoutPBVec(rsRawEncoder.getNumDataUnits(), rsRawEncoder.getNumParityUnits());
    }
    
    @Override
    public void performCoding(final ECChunk[] inputChunks, final ECChunk[] outputChunks) throws IOException {
        final ByteBuffer[] inputBuffers = ECChunk.toBuffers(inputChunks);
        final ByteBuffer[] outputBuffers = ECChunk.toBuffers(outputChunks);
        this.performCoding(inputBuffers, outputBuffers);
    }
    
    private void performCoding(final ByteBuffer[] inputs, final ByteBuffer[] outputs) throws IOException {
        final int numDataUnits = this.rsRawEncoder.getNumDataUnits();
        final int numParityUnits = this.rsRawEncoder.getNumParityUnits();
        final int subSPacketSize = this.getSubPacketSize();
        if (inputs.length != numDataUnits * subSPacketSize) {
            throw new IllegalArgumentException("Invalid inputs length");
        }
        if (outputs.length != numParityUnits * subSPacketSize) {
            throw new IllegalArgumentException("Invalid outputs length");
        }
        final ByteBuffer[][] hhInputs = new ByteBuffer[subSPacketSize][numDataUnits];
        for (int i = 0; i < subSPacketSize; ++i) {
            for (int j = 0; j < numDataUnits; ++j) {
                hhInputs[i][j] = inputs[i * numDataUnits + j];
            }
        }
        final ByteBuffer[][] hhOutputs = new ByteBuffer[subSPacketSize][numParityUnits];
        for (int k = 0; k < subSPacketSize; ++k) {
            for (int l = 0; l < numParityUnits; ++l) {
                hhOutputs[k][l] = outputs[k * numParityUnits + l];
            }
        }
        this.doEncode(hhInputs, hhOutputs);
    }
    
    private void doEncode(final ByteBuffer[][] inputs, final ByteBuffer[][] outputs) throws IOException {
        final int numParityUnits = this.rsRawEncoder.getNumParityUnits();
        final ByteBuffer[] piggyBacks = HHUtil.getPiggyBacksFromInput(inputs[0], this.piggyBackIndex, numParityUnits, 0, this.xorRawEncoder);
        for (int i = 0; i < this.getSubPacketSize(); ++i) {
            this.rsRawEncoder.encode(inputs[i], outputs[i]);
        }
        this.encodeWithPiggyBacks(piggyBacks, outputs, numParityUnits, inputs[0][0].isDirect());
    }
    
    private void encodeWithPiggyBacks(final ByteBuffer[] piggyBacks, final ByteBuffer[][] outputs, final int numParityUnits, final boolean bIsDirect) {
        if (!bIsDirect) {
            for (int i = 0; i < numParityUnits - 1; ++i) {
                final int parityIndex = i + 1;
                final int bufSize = piggyBacks[i].remaining();
                final byte[] newOut = outputs[1][parityIndex].array();
                int k;
                final int offset = k = outputs[1][parityIndex].arrayOffset() + outputs[1][parityIndex].position();
                for (int j = 0; j < bufSize; ++j) {
                    newOut[k] ^= piggyBacks[i].get(j);
                    ++k;
                }
            }
            return;
        }
        for (int i = 0; i < numParityUnits - 1; ++i) {
            final int parityIndex = i + 1;
            for (int l = piggyBacks[i].position(), m = outputs[1][parityIndex].position(); l < piggyBacks[i].limit(); ++l, ++m) {
                outputs[1][parityIndex].put(m, (byte)(outputs[1][parityIndex].get(m) ^ piggyBacks[i].get(l)));
            }
        }
    }
}
