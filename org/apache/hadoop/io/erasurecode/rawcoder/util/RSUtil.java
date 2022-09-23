// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder.util;

import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class RSUtil
{
    public static GaloisField GF;
    public static final int PRIMITIVE_ROOT = 2;
    
    private RSUtil() {
    }
    
    public static int[] getPrimitivePower(final int numDataUnits, final int numParityUnits) {
        final int[] primitivePower = new int[numDataUnits + numParityUnits];
        for (int i = 0; i < numDataUnits + numParityUnits; ++i) {
            primitivePower[i] = RSUtil.GF.power(2, i);
        }
        return primitivePower;
    }
    
    public static void initTables(final int k, final int rows, final byte[] codingMatrix, final int matrixOffset, final byte[] gfTables) {
        int offset = 0;
        int idx = matrixOffset;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < k; ++j) {
                GF256.gfVectMulInit(codingMatrix[idx++], gfTables, offset);
                offset += 32;
            }
        }
    }
    
    public static void genCauchyMatrix(final byte[] a, final int m, final int k) {
        for (int i = 0; i < k; ++i) {
            a[k * i + i] = 1;
        }
        int pos = k * k;
        for (int j = k; j < m; ++j) {
            for (int l = 0; l < k; ++l) {
                a[pos++] = GF256.gfInv((byte)(j ^ l));
            }
        }
    }
    
    public static void encodeData(final byte[] gfTables, final int dataLen, final byte[][] inputs, final int[] inputOffsets, final byte[][] outputs, final int[] outputOffsets) {
        final int numInputs = inputs.length;
        final int numOutputs = outputs.length;
        final int times = dataLen / 8;
        final int extra = dataLen - dataLen % 8;
        for (int l = 0; l < numOutputs; ++l) {
            final byte[] output = outputs[l];
            for (int j = 0; j < numInputs; ++j) {
                final byte[] input = inputs[j];
                int iPos = inputOffsets[j];
                int oPos = outputOffsets[l];
                final byte s = gfTables[j * 32 + l * numInputs * 32 + 1];
                final byte[] tableLine = GF256.gfMulTab()[s & 0xFF];
                for (int i = 0; i < times; ++i, iPos += 8, oPos += 8) {
                    final byte[] array = output;
                    final int n = oPos + 0;
                    array[n] ^= tableLine[0xFF & input[iPos + 0]];
                    final byte[] array2 = output;
                    final int n2 = oPos + 1;
                    array2[n2] ^= tableLine[0xFF & input[iPos + 1]];
                    final byte[] array3 = output;
                    final int n3 = oPos + 2;
                    array3[n3] ^= tableLine[0xFF & input[iPos + 2]];
                    final byte[] array4 = output;
                    final int n4 = oPos + 3;
                    array4[n4] ^= tableLine[0xFF & input[iPos + 3]];
                    final byte[] array5 = output;
                    final int n5 = oPos + 4;
                    array5[n5] ^= tableLine[0xFF & input[iPos + 4]];
                    final byte[] array6 = output;
                    final int n6 = oPos + 5;
                    array6[n6] ^= tableLine[0xFF & input[iPos + 5]];
                    final byte[] array7 = output;
                    final int n7 = oPos + 6;
                    array7[n7] ^= tableLine[0xFF & input[iPos + 6]];
                    final byte[] array8 = output;
                    final int n8 = oPos + 7;
                    array8[n8] ^= tableLine[0xFF & input[iPos + 7]];
                }
                for (int i = extra; i < dataLen; ++i, ++iPos, ++oPos) {
                    final byte[] array9 = output;
                    final int n9 = oPos;
                    array9[n9] ^= tableLine[0xFF & input[iPos]];
                }
            }
        }
    }
    
    public static void encodeData(final byte[] gfTables, final ByteBuffer[] inputs, final ByteBuffer[] outputs) {
        final int numInputs = inputs.length;
        final int numOutputs = outputs.length;
        final int dataLen = inputs[0].remaining();
        final int times = dataLen / 8;
        final int extra = dataLen - dataLen % 8;
        for (int l = 0; l < numOutputs; ++l) {
            final ByteBuffer output = outputs[l];
            for (int j = 0; j < numInputs; ++j) {
                final ByteBuffer input = inputs[j];
                int iPos = input.position();
                int oPos = output.position();
                final byte s = gfTables[j * 32 + l * numInputs * 32 + 1];
                final byte[] tableLine = GF256.gfMulTab()[s & 0xFF];
                for (int i = 0; i < times; ++i, iPos += 8, oPos += 8) {
                    output.put(oPos + 0, (byte)(output.get(oPos + 0) ^ tableLine[0xFF & input.get(iPos + 0)]));
                    output.put(oPos + 1, (byte)(output.get(oPos + 1) ^ tableLine[0xFF & input.get(iPos + 1)]));
                    output.put(oPos + 2, (byte)(output.get(oPos + 2) ^ tableLine[0xFF & input.get(iPos + 2)]));
                    output.put(oPos + 3, (byte)(output.get(oPos + 3) ^ tableLine[0xFF & input.get(iPos + 3)]));
                    output.put(oPos + 4, (byte)(output.get(oPos + 4) ^ tableLine[0xFF & input.get(iPos + 4)]));
                    output.put(oPos + 5, (byte)(output.get(oPos + 5) ^ tableLine[0xFF & input.get(iPos + 5)]));
                    output.put(oPos + 6, (byte)(output.get(oPos + 6) ^ tableLine[0xFF & input.get(iPos + 6)]));
                    output.put(oPos + 7, (byte)(output.get(oPos + 7) ^ tableLine[0xFF & input.get(iPos + 7)]));
                }
                for (int i = extra; i < dataLen; ++i, ++iPos, ++oPos) {
                    output.put(oPos, (byte)(output.get(oPos) ^ tableLine[0xFF & input.get(iPos)]));
                }
            }
        }
    }
    
    static {
        RSUtil.GF = GaloisField.getInstance();
    }
}
