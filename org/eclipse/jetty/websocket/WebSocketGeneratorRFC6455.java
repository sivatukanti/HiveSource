// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import java.io.IOException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;

public class WebSocketGeneratorRFC6455 implements WebSocketGenerator
{
    private final WebSocketBuffers _buffers;
    private final EndPoint _endp;
    private Buffer _buffer;
    private final byte[] _mask;
    private int _m;
    private boolean _opsent;
    private final MaskGen _maskGen;
    private boolean _closed;
    
    public WebSocketGeneratorRFC6455(final WebSocketBuffers buffers, final EndPoint endp) {
        this._mask = new byte[4];
        this._buffers = buffers;
        this._endp = endp;
        this._maskGen = null;
    }
    
    public WebSocketGeneratorRFC6455(final WebSocketBuffers buffers, final EndPoint endp, final MaskGen maskGen) {
        this._mask = new byte[4];
        this._buffers = buffers;
        this._endp = endp;
        this._maskGen = maskGen;
    }
    
    public synchronized Buffer getBuffer() {
        return this._buffer;
    }
    
    public synchronized void addFrame(final byte flags, byte opcode, final byte[] content, int offset, int length) throws IOException {
        if (this._closed) {
            throw new EofException("Closed");
        }
        if (opcode == 8) {
            this._closed = true;
        }
        final boolean mask = this._maskGen != null;
        if (this._buffer == null) {
            this._buffer = (mask ? this._buffers.getBuffer() : this._buffers.getDirectBuffer());
        }
        final boolean last = WebSocketConnectionRFC6455.isLastFrame(flags);
        final int space = mask ? 14 : 10;
        do {
            opcode = (byte)(this._opsent ? 0 : opcode);
            opcode = (byte)(((0xF & flags) << 4) + (0xF & opcode));
            this._opsent = true;
            int payload = length;
            if (payload + space > this._buffer.capacity()) {
                opcode &= 0x7F;
                payload = this._buffer.capacity() - space;
            }
            else if (last) {
                opcode |= (byte)128;
            }
            if (this._buffer.space() <= space) {
                this.flushBuffer();
                if (this._buffer.space() <= space) {
                    this.flush();
                }
            }
            if (payload > 65535) {
                this._buffer.put(new byte[] { opcode, (byte)(mask ? -1 : 127), 0, 0, 0, 0, (byte)(payload >> 24 & 0xFF), (byte)(payload >> 16 & 0xFF), (byte)(payload >> 8 & 0xFF), (byte)(payload & 0xFF) });
            }
            else if (payload >= 126) {
                this._buffer.put(new byte[] { opcode, (byte)(mask ? -2 : 126), (byte)(payload >> 8), (byte)(payload & 0xFF) });
            }
            else {
                this._buffer.put(new byte[] { opcode, (byte)(mask ? (0x80 | payload) : payload) });
            }
            if (mask) {
                this._maskGen.genMask(this._mask);
                this._m = 0;
                this._buffer.put(this._mask);
            }
            int remaining = payload;
            while (remaining > 0) {
                this._buffer.compact();
                final int chunk = (remaining < this._buffer.space()) ? remaining : this._buffer.space();
                if (mask) {
                    for (int i = 0; i < chunk; ++i) {
                        this._buffer.put((byte)(content[offset + (payload - remaining) + i] ^ this._mask[this._m++ % 4]));
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
                    this.flush();
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
        if (this._buffer != null && this._buffer.length() == 0) {
            this._buffers.returnBuffer(this._buffer);
            this._buffer = null;
        }
    }
    
    public synchronized int flushBuffer() throws IOException {
        if (!this._endp.isOpen()) {
            throw new EofException();
        }
        if (this._buffer != null) {
            final int flushed = this._buffer.hasContent() ? this._endp.flush(this._buffer) : 0;
            if (this._closed && this._buffer.length() == 0) {
                this._endp.shutdownOutput();
            }
            return flushed;
        }
        return 0;
    }
    
    public synchronized int flush() throws IOException {
        if (this._buffer == null) {
            return 0;
        }
        int result = this.flushBuffer();
        if (!this._endp.isBlocking()) {
            long now = System.currentTimeMillis();
            final long end = now + this._endp.getMaxIdleTime();
            while (this._buffer.length() > 0) {
                final boolean ready = this._endp.blockWritable(end - now);
                if (!ready) {
                    now = System.currentTimeMillis();
                    if (now < end) {
                        continue;
                    }
                    throw new IOException("Write timeout");
                }
                else {
                    result += this.flushBuffer();
                }
            }
        }
        this._buffer.compact();
        return result;
    }
    
    public synchronized boolean isBufferEmpty() {
        return this._buffer == null || this._buffer.length() == 0;
    }
    
    public synchronized void returnBuffer() {
        if (this._buffer != null && this._buffer.length() == 0) {
            this._buffers.returnBuffer(this._buffer);
            this._buffer = null;
        }
    }
    
    @Override
    public String toString() {
        final Buffer buffer = this._buffer;
        return String.format("%s@%x closed=%b buffer=%d", this.getClass().getSimpleName(), this.hashCode(), this._closed, (buffer == null) ? -1 : buffer.length());
    }
}
