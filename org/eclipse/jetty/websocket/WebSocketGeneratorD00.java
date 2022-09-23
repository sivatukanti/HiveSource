// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.io.EofException;
import java.io.IOException;
import java.math.BigInteger;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;

public class WebSocketGeneratorD00 implements WebSocketGenerator
{
    private final WebSocketBuffers _buffers;
    private final EndPoint _endp;
    private Buffer _buffer;
    
    public WebSocketGeneratorD00(final WebSocketBuffers buffers, final EndPoint endp) {
        this._buffers = buffers;
        this._endp = endp;
    }
    
    public synchronized void addFrame(final byte flags, final byte opcode, final byte[] content, final int offset, final int length) throws IOException {
        final long blockFor = this._endp.getMaxIdleTime();
        if (this._buffer == null) {
            this._buffer = this._buffers.getDirectBuffer();
        }
        if (this._buffer.space() == 0) {
            this.expelBuffer(blockFor);
        }
        this.bufferPut(opcode, blockFor);
        if (this.isLengthFrame(opcode)) {
            final int lengthBytes = new BigInteger(String.valueOf(length)).bitLength() / 7 + 1;
            for (int i = lengthBytes - 1; i > 0; --i) {
                final byte lengthByte = (byte)(0x80 | (0x7F & length >> 7 * i));
                this.bufferPut(lengthByte, blockFor);
            }
            this.bufferPut((byte)(0x7F & length), blockFor);
        }
        int remaining = length;
        while (remaining > 0) {
            final int chunk = (remaining < this._buffer.space()) ? remaining : this._buffer.space();
            this._buffer.put(content, offset + (length - remaining), chunk);
            remaining -= chunk;
            if (this._buffer.space() > 0) {
                if (!this.isLengthFrame(opcode)) {
                    this._buffer.put((byte)(-1));
                }
                this.flushBuffer();
            }
            else {
                this.expelBuffer(blockFor);
                if (remaining != 0) {
                    continue;
                }
                if (!this.isLengthFrame(opcode)) {
                    this._buffer.put((byte)(-1));
                }
                this.flushBuffer();
            }
        }
    }
    
    private synchronized boolean isLengthFrame(final byte frame) {
        return (frame & 0xFFFFFF80) == 0xFFFFFF80;
    }
    
    private synchronized void bufferPut(final byte datum, final long blockFor) throws IOException {
        if (this._buffer == null) {
            this._buffer = this._buffers.getDirectBuffer();
        }
        this._buffer.put(datum);
        if (this._buffer.space() == 0) {
            this.expelBuffer(blockFor);
        }
    }
    
    public synchronized int flush(final int blockFor) throws IOException {
        return this.expelBuffer(blockFor);
    }
    
    public synchronized int flush() throws IOException {
        final int flushed = this.flushBuffer();
        if (this._buffer != null && this._buffer.length() == 0) {
            this._buffers.returnBuffer(this._buffer);
            this._buffer = null;
        }
        return flushed;
    }
    
    private synchronized int flushBuffer() throws IOException {
        if (!this._endp.isOpen()) {
            throw new EofException();
        }
        if (this._buffer != null && this._buffer.hasContent()) {
            return this._endp.flush(this._buffer);
        }
        return 0;
    }
    
    private synchronized int expelBuffer(final long blockFor) throws IOException {
        if (this._buffer == null) {
            return 0;
        }
        int result = this.flushBuffer();
        this._buffer.compact();
        if (!this._endp.isBlocking()) {
            while (this._buffer.space() == 0) {
                final boolean ready = this._endp.blockWritable(blockFor);
                if (!ready) {
                    throw new IOException("Write timeout");
                }
                result += this.flushBuffer();
                this._buffer.compact();
            }
        }
        return result;
    }
    
    public synchronized boolean isBufferEmpty() {
        return this._buffer == null || this._buffer.length() == 0;
    }
}
