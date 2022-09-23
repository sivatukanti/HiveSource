// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.io.EofException;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;

public class WebSocketGeneratorD06 implements WebSocketGenerator
{
    private final WebSocketBuffers _buffers;
    private final EndPoint _endp;
    private Buffer _buffer;
    private final byte[] _mask;
    private int _m;
    private boolean _opsent;
    private final MaskGen _maskGen;
    
    public WebSocketGeneratorD06(final WebSocketBuffers buffers, final EndPoint endp) {
        this._mask = new byte[4];
        this._buffers = buffers;
        this._endp = endp;
        this._maskGen = null;
    }
    
    public WebSocketGeneratorD06(final WebSocketBuffers buffers, final EndPoint endp, final MaskGen maskGen) {
        this._mask = new byte[4];
        this._buffers = buffers;
        this._endp = endp;
        this._maskGen = maskGen;
    }
    
    public synchronized void addFrame(final byte flags, byte opcode, final byte[] content, int offset, int length) throws IOException {
        final long blockFor = this._endp.getMaxIdleTime();
        if (this._buffer == null) {
            this._buffer = ((this._maskGen != null) ? this._buffers.getBuffer() : this._buffers.getDirectBuffer());
        }
        final boolean last = WebSocketConnectionD06.isLastFrame(flags);
        opcode &= (byte)(((0xF & flags) << 4) + 15);
        final int space = (this._maskGen != null) ? 14 : 10;
        do {
            opcode = (byte)(this._opsent ? 0 : opcode);
            this._opsent = true;
            int payload = length;
            if (payload + space > this._buffer.capacity()) {
                opcode &= 0x7F;
                payload = this._buffer.capacity() - space;
            }
            else if (last) {
                opcode |= 0xFFFFFF80;
            }
            if (this._buffer.space() <= space) {
                this.expelBuffer(blockFor);
            }
            if (this._maskGen != null) {
                this._maskGen.genMask(this._mask);
                this._m = 0;
                this._buffer.put(this._mask);
            }
            if (payload > 65535) {
                this.bufferPut(new byte[] { opcode, 127, 0, 0, 0, 0, (byte)(payload >> 24 & 0xFF), (byte)(payload >> 16 & 0xFF), (byte)(payload >> 8 & 0xFF), (byte)(payload & 0xFF) });
            }
            else if (payload >= 126) {
                this.bufferPut(new byte[] { opcode, 126, (byte)(payload >> 8), (byte)(payload & 0xFF) });
            }
            else {
                this.bufferPut(opcode);
                this.bufferPut((byte)payload);
            }
            int remaining = payload;
            while (remaining > 0) {
                this._buffer.compact();
                final int chunk = (remaining < this._buffer.space()) ? remaining : this._buffer.space();
                if (this._maskGen != null) {
                    for (int i = 0; i < chunk; ++i) {
                        this.bufferPut(content[offset + (payload - remaining) + i]);
                    }
                }
                else {
                    this._buffer.put(content, offset + (payload - remaining), chunk);
                }
                remaining -= chunk;
                if (this._buffer.space() > 0) {
                    this.flushBuffer();
                }
                else {
                    this.expelBuffer(blockFor);
                    if (remaining != 0) {
                        continue;
                    }
                    this.flushBuffer();
                }
            }
            offset += payload;
            length -= payload;
        } while (length > 0);
        this._opsent = !last;
    }
    
    private synchronized void bufferPut(final byte[] data) throws IOException {
        if (this._maskGen != null) {
            for (int i = 0; i < data.length; ++i) {
                final int n = i;
                data[n] ^= this._mask[this._m++ % 4];
            }
        }
        this._buffer.put(data);
    }
    
    private synchronized void bufferPut(final byte data) throws IOException {
        this._buffer.put((byte)(data ^ this._mask[this._m++ % 4]));
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
        if (this._buffer != null) {
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
