// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import java.util.Arrays;
import org.apache.hadoop.io.erasurecode.ECChunk;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public final class CoderUtil
{
    private static byte[] emptyChunk;
    
    private CoderUtil() {
    }
    
    static byte[] getEmptyChunk(final int leastLength) {
        if (CoderUtil.emptyChunk.length >= leastLength) {
            return CoderUtil.emptyChunk;
        }
        synchronized (CoderUtil.class) {
            CoderUtil.emptyChunk = new byte[leastLength];
        }
        return CoderUtil.emptyChunk;
    }
    
    static ByteBuffer resetBuffer(final ByteBuffer buffer, final int len) {
        final int pos = buffer.position();
        buffer.put(getEmptyChunk(len), 0, len);
        buffer.position(pos);
        return buffer;
    }
    
    static byte[] resetBuffer(final byte[] buffer, final int offset, final int len) {
        final byte[] empty = getEmptyChunk(len);
        System.arraycopy(empty, 0, buffer, offset, len);
        return buffer;
    }
    
    static void resetOutputBuffers(final ByteBuffer[] buffers, final int dataLen) {
        for (final ByteBuffer buffer : buffers) {
            resetBuffer(buffer, dataLen);
        }
    }
    
    static void resetOutputBuffers(final byte[][] buffers, final int[] offsets, final int dataLen) {
        for (int i = 0; i < buffers.length; ++i) {
            resetBuffer(buffers[i], offsets[i], dataLen);
        }
    }
    
    static ByteBuffer[] toBuffers(final ECChunk[] chunks) {
        final ByteBuffer[] buffers = new ByteBuffer[chunks.length];
        for (int i = 0; i < chunks.length; ++i) {
            final ECChunk chunk = chunks[i];
            if (chunk == null) {
                buffers[i] = null;
            }
            else {
                buffers[i] = chunk.getBuffer();
                if (chunk.isAllZero()) {
                    resetBuffer(buffers[i], buffers[i].remaining());
                }
            }
        }
        return buffers;
    }
    
    static ByteBuffer cloneAsDirectByteBuffer(final byte[] input, final int offset, final int len) {
        if (input == null) {
            return null;
        }
        final ByteBuffer directBuffer = ByteBuffer.allocateDirect(len);
        directBuffer.put(input, offset, len);
        directBuffer.flip();
        return directBuffer;
    }
    
    static <T> int[] getNullIndexes(final T[] inputs) {
        final int[] nullIndexes = new int[inputs.length];
        int idx = 0;
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] == null) {
                nullIndexes[idx++] = i;
            }
        }
        return Arrays.copyOf(nullIndexes, idx);
    }
    
    static <T> T findFirstValidInput(final T[] inputs) {
        for (final T input : inputs) {
            if (input != null) {
                return input;
            }
        }
        throw new HadoopIllegalArgumentException("Invalid inputs are found, all being null");
    }
    
    static <T> int[] getValidIndexes(final T[] inputs) {
        final int[] validIndexes = new int[inputs.length];
        int idx = 0;
        for (int i = 0; i < inputs.length; ++i) {
            if (inputs[i] != null) {
                validIndexes[idx++] = i;
            }
        }
        return Arrays.copyOf(validIndexes, idx);
    }
    
    static {
        CoderUtil.emptyChunk = new byte[4096];
    }
}
