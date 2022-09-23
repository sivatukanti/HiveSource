// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ECChunk
{
    private ByteBuffer chunkBuffer;
    private boolean allZero;
    
    public ECChunk(final ByteBuffer buffer) {
        this.allZero = false;
        this.chunkBuffer = buffer;
    }
    
    public ECChunk(final ByteBuffer buffer, final int offset, final int len) {
        this.allZero = false;
        final ByteBuffer tmp = buffer.duplicate();
        tmp.position(offset);
        tmp.limit(offset + len);
        this.chunkBuffer = tmp.slice();
    }
    
    public ECChunk(final byte[] buffer) {
        this.allZero = false;
        this.chunkBuffer = ByteBuffer.wrap(buffer);
    }
    
    public ECChunk(final byte[] buffer, final int offset, final int len) {
        this.allZero = false;
        this.chunkBuffer = ByteBuffer.wrap(buffer, offset, len);
    }
    
    public boolean isAllZero() {
        return this.allZero;
    }
    
    public void setAllZero(final boolean allZero) {
        this.allZero = allZero;
    }
    
    public ByteBuffer getBuffer() {
        return this.chunkBuffer;
    }
    
    public static ByteBuffer[] toBuffers(final ECChunk[] chunks) {
        final ByteBuffer[] buffers = new ByteBuffer[chunks.length];
        for (int i = 0; i < chunks.length; ++i) {
            final ECChunk chunk = chunks[i];
            if (chunk == null) {
                buffers[i] = null;
            }
            else {
                buffers[i] = chunk.getBuffer();
            }
        }
        return buffers;
    }
    
    public byte[] toBytesArray() {
        final byte[] bytesArr = new byte[this.chunkBuffer.remaining()];
        this.chunkBuffer.mark();
        this.chunkBuffer.get(bytesArr);
        this.chunkBuffer.reset();
        return bytesArr;
    }
}
