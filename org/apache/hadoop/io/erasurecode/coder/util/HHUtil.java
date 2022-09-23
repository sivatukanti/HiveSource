// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder.util;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.io.erasurecode.rawcoder.util.RSUtil;
import java.io.IOException;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class HHUtil
{
    private HHUtil() {
    }
    
    public static int[] initPiggyBackIndexWithoutPBVec(final int numDataUnits, final int numParityUnits) {
        final int piggyBackSize = numDataUnits / (numParityUnits - 1);
        final int[] piggyBackIndex = new int[numParityUnits];
        for (int i = 0; i < numDataUnits; ++i) {
            if (i % piggyBackSize == 0) {
                piggyBackIndex[i / piggyBackSize] = i;
            }
        }
        piggyBackIndex[numParityUnits - 1] = numDataUnits;
        return piggyBackIndex;
    }
    
    public static int[] initPiggyBackFullIndexVec(final int numDataUnits, final int[] piggyBackIndex) {
        final int[] piggyBackFullIndex = new int[numDataUnits];
        for (int i = 1; i < piggyBackIndex.length; ++i) {
            for (int j = piggyBackIndex[i - 1]; j < piggyBackIndex[i]; ++j) {
                piggyBackFullIndex[j] = i;
            }
        }
        return piggyBackFullIndex;
    }
    
    public static ByteBuffer[] getPiggyBacksFromInput(final ByteBuffer[] inputs, final int[] piggyBackIndex, final int numParityUnits, final int pgIndex, final RawErasureEncoder encoder) throws IOException {
        final ByteBuffer[] emptyInput = new ByteBuffer[inputs.length];
        final ByteBuffer[] tempInput = new ByteBuffer[inputs.length];
        final int[] inputPositions = new int[inputs.length];
        for (int m = 0; m < inputs.length; ++m) {
            if (inputs[m] != null) {
                emptyInput[m] = allocateByteBuffer(inputs[m].isDirect(), inputs[m].remaining());
            }
        }
        final ByteBuffer[] tempOutput = new ByteBuffer[numParityUnits];
        for (int i = 0; i < numParityUnits; ++i) {
            tempOutput[i] = allocateByteBuffer(inputs[i].isDirect(), inputs[0].remaining());
        }
        final ByteBuffer[] piggyBacks = new ByteBuffer[numParityUnits - 1];
        assert piggyBackIndex.length >= numParityUnits;
        for (int j = 0; j < numParityUnits - 1; ++j) {
            for (int k = piggyBackIndex[j]; k < piggyBackIndex[j + 1]; ++k) {
                tempInput[k] = inputs[k];
                inputPositions[k] = inputs[k].position();
            }
            for (int n = 0; n < emptyInput.length; ++n) {
                if (tempInput[n] == null) {
                    tempInput[n] = emptyInput[n];
                    inputPositions[n] = emptyInput[n].position();
                }
            }
            encoder.encode(tempInput, tempOutput);
            piggyBacks[j] = cloneBufferData(tempOutput[pgIndex]);
            for (int l = 0; l < tempInput.length; ++l) {
                if (tempInput[l] != null) {
                    tempInput[l].position(inputPositions[l]);
                    tempInput[l] = null;
                }
            }
            for (int l = 0; l < tempOutput.length; ++l) {
                tempOutput[l].clear();
            }
        }
        return piggyBacks;
    }
    
    private static ByteBuffer cloneBufferData(final ByteBuffer srcBuffer) {
        final byte[] bytesArr = new byte[srcBuffer.remaining()];
        srcBuffer.mark();
        srcBuffer.get(bytesArr);
        srcBuffer.reset();
        ByteBuffer destBuffer;
        if (!srcBuffer.isDirect()) {
            destBuffer = ByteBuffer.wrap(bytesArr);
        }
        else {
            destBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining());
            destBuffer.put(bytesArr);
            destBuffer.flip();
        }
        return destBuffer;
    }
    
    public static ByteBuffer allocateByteBuffer(final boolean useDirectBuffer, final int bufSize) {
        if (useDirectBuffer) {
            return ByteBuffer.allocateDirect(bufSize);
        }
        return ByteBuffer.allocate(bufSize);
    }
    
    public static ByteBuffer getPiggyBackForDecode(final ByteBuffer[][] inputs, final ByteBuffer[][] outputs, final int pbParityIndex, final int numDataUnits, final int numParityUnits, final int pbIndex) {
        final ByteBuffer fisrtValidInput = findFirstValidInput(inputs[0]);
        final int bufSize = fisrtValidInput.remaining();
        final ByteBuffer piggybacks = allocateByteBuffer(fisrtValidInput.isDirect(), bufSize);
        if (pbParityIndex < numParityUnits) {
            final int inputIdx = numDataUnits + pbParityIndex;
            final int inputPos = inputs[1][inputIdx].position();
            final int outputPos = outputs[1][pbParityIndex].position();
            int m = 0;
            int k = inputPos;
            for (int n = outputPos; m < bufSize; ++m, ++n) {
                final int valueWithPb = 0xFF & inputs[1][inputIdx].get(k);
                final int valueWithoutPb = 0xFF & outputs[1][pbParityIndex].get(n);
                piggybacks.put(m, (byte)RSUtil.GF.add(valueWithPb, valueWithoutPb));
                ++k;
            }
        }
        else {
            int sum = 0;
            for (int i = 0; i < bufSize; ++i) {
                sum = 0;
                for (int j = 1; j < numParityUnits; ++j) {
                    final int inIdx = numDataUnits + j;
                    final int inPos = inputs[1][numDataUnits + j].position();
                    final int outPos = outputs[1][j].position();
                    sum = RSUtil.GF.add(sum, 0xFF & inputs[1][inIdx].get(inPos + i));
                    sum = RSUtil.GF.add(sum, 0xFF & outputs[1][j].get(outPos + i));
                }
                sum = RSUtil.GF.add(sum, 0xFF & inputs[0][numDataUnits + pbIndex].get(inputs[0][numDataUnits + pbIndex].position() + i));
                piggybacks.put(i, (byte)sum);
            }
        }
        return piggybacks;
    }
    
    public static <T> T findFirstValidInput(final T[] inputs) {
        for (final T input : inputs) {
            if (input != null) {
                return input;
            }
        }
        throw new HadoopIllegalArgumentException("Invalid inputs are found, all being null");
    }
}
