// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;

public class WebSocketParserD00 implements WebSocketParser
{
    private static final Logger LOG;
    public static final int STATE_START = 0;
    public static final int STATE_SENTINEL_DATA = 1;
    public static final int STATE_LENGTH = 2;
    public static final int STATE_DATA = 3;
    private final WebSocketBuffers _buffers;
    private final EndPoint _endp;
    private final FrameHandler _handler;
    private int _state;
    private Buffer _buffer;
    private byte _opcode;
    private int _length;
    
    public WebSocketParserD00(final WebSocketBuffers buffers, final EndPoint endp, final FrameHandler handler) {
        this._buffers = buffers;
        this._endp = endp;
        this._handler = handler;
    }
    
    public boolean isBufferEmpty() {
        return this._buffer == null || this._buffer.length() == 0;
    }
    
    public Buffer getBuffer() {
        return this._buffer;
    }
    
    public int parseNext() {
        if (this._buffer == null) {
            this._buffer = this._buffers.getBuffer();
        }
        int progress = 0;
    Label_0531:
        while (true) {
            int length = this._buffer.length();
            if (length == 0 || (this._state == 3 && length < this._length)) {
                this._buffer.compact();
                if (this._buffer.space() == 0) {
                    throw new IllegalStateException("FULL");
                }
                try {
                    final int filled = this._endp.isOpen() ? this._endp.fill(this._buffer) : -1;
                    if (filled <= 0) {
                        return progress;
                    }
                    progress += filled;
                    length = this._buffer.length();
                }
                catch (IOException e) {
                    WebSocketParserD00.LOG.debug(e);
                    return (progress > 0) ? progress : -1;
                }
            }
            while (length-- > 0) {
                switch (this._state) {
                    case 0: {
                        final byte b = this._buffer.get();
                        this._opcode = b;
                        if (this._opcode < 0) {
                            this._length = 0;
                            this._state = 2;
                            continue;
                        }
                        this._state = 1;
                        this._buffer.mark(0);
                        continue;
                    }
                    case 1: {
                        final byte b = this._buffer.get();
                        if ((b & 0xFF) == 0xFF) {
                            this._state = 0;
                            final int l = this._buffer.getIndex() - this._buffer.markIndex() - 1;
                            ++progress;
                            this._handler.onFrame((byte)0, this._opcode, this._buffer.sliceFromMark(l));
                            this._buffer.setMarkIndex(-1);
                            if (this._buffer.length() == 0) {
                                this._buffers.returnBuffer(this._buffer);
                                this._buffer = null;
                            }
                            return progress;
                        }
                        continue;
                    }
                    case 2: {
                        final byte b = this._buffer.get();
                        this._length = (this._length << 7 | (0x7F & b));
                        if (b >= 0) {
                            this._state = 3;
                            this._buffer.mark(0);
                            continue;
                        }
                        continue;
                    }
                    case 3: {
                        if (this._buffer.markIndex() < 0 && this._buffer.length() < this._length) {
                            continue Label_0531;
                        }
                        final Buffer data = this._buffer.sliceFromMark(this._length);
                        this._buffer.skip(this._length);
                        this._state = 0;
                        ++progress;
                        this._handler.onFrame((byte)0, this._opcode, data);
                        if (this._buffer.length() == 0) {
                            this._buffers.returnBuffer(this._buffer);
                            this._buffer = null;
                        }
                        return progress;
                    }
                    default: {
                        continue;
                    }
                }
            }
        }
    }
    
    public void fill(final Buffer buffer) {
        if (buffer != null && buffer.length() > 0) {
            if (this._buffer == null) {
                this._buffer = this._buffers.getBuffer();
            }
            this._buffer.put(buffer);
            buffer.clear();
        }
    }
    
    static {
        LOG = Log.getLogger(WebSocketParserD00.class);
    }
}
