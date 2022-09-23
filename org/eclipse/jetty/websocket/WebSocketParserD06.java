// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;

public class WebSocketParserD06 implements WebSocketParser
{
    private static final Logger LOG;
    private final WebSocketBuffers _buffers;
    private final EndPoint _endp;
    private final FrameHandler _handler;
    private final boolean _masked;
    private State _state;
    private Buffer _buffer;
    private byte _flags;
    private byte _opcode;
    private int _bytesNeeded;
    private long _length;
    private final byte[] _mask;
    private int _m;
    
    public WebSocketParserD06(final WebSocketBuffers buffers, final EndPoint endp, final FrameHandler handler, final boolean masked) {
        this._mask = new byte[4];
        this._buffers = buffers;
        this._endp = endp;
        this._handler = handler;
        this._masked = masked;
        this._state = State.START;
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
        int total_filled = 0;
        int events = 0;
        while (true) {
            int available = this._buffer.length();
            while (available < ((this._state == State.SKIP) ? 1 : this._bytesNeeded)) {
                this._buffer.compact();
                if (this._buffer.space() == 0) {
                    throw new IllegalStateException("FULL: " + this._state + " " + this._bytesNeeded + ">" + this._buffer.capacity());
                }
                try {
                    final int filled = this._endp.isOpen() ? this._endp.fill(this._buffer) : -1;
                    if (filled <= 0) {
                        return (total_filled + events > 0) ? (total_filled + events) : filled;
                    }
                    total_filled += filled;
                    available = this._buffer.length();
                    continue;
                }
                catch (IOException e) {
                    WebSocketParserD06.LOG.debug(e);
                    return (total_filled + events > 0) ? (total_filled + events) : -1;
                }
                break;
            }
            while (this._state != State.DATA && available >= ((this._state == State.SKIP) ? 1 : this._bytesNeeded)) {
                switch (this._state) {
                    case START: {
                        this._state = (this._masked ? State.MASK : State.OPCODE);
                        this._bytesNeeded = this._state.getNeeds();
                        continue;
                    }
                    case MASK: {
                        this._buffer.get(this._mask, 0, 4);
                        available -= 4;
                        this._state = State.OPCODE;
                        this._bytesNeeded = this._state.getNeeds();
                        this._m = 0;
                        continue;
                    }
                    case OPCODE: {
                        byte b = this._buffer.get();
                        --available;
                        if (this._masked) {
                            b ^= this._mask[this._m++ % 4];
                        }
                        this._opcode = (byte)(b & 0xF);
                        this._flags = (byte)(0xF & b >> 4);
                        if (WebSocketConnectionD06.isControlFrame(this._opcode) && !WebSocketConnectionD06.isLastFrame(this._flags)) {
                            this._state = State.SKIP;
                            ++events;
                            this._handler.close(1002, "fragmented control");
                        }
                        else {
                            this._state = State.LENGTH_7;
                        }
                        this._bytesNeeded = this._state.getNeeds();
                        continue;
                    }
                    case LENGTH_7: {
                        byte b = this._buffer.get();
                        --available;
                        if (this._masked) {
                            b ^= this._mask[this._m++ % 4];
                        }
                        switch (b) {
                            case Byte.MAX_VALUE: {
                                this._length = 0L;
                                this._state = State.LENGTH_63;
                                this._bytesNeeded = this._state.getNeeds();
                                continue;
                            }
                            case 126: {
                                this._length = 0L;
                                this._state = State.LENGTH_16;
                                this._bytesNeeded = this._state.getNeeds();
                                continue;
                            }
                            default: {
                                this._length = (0x7F & b);
                                this._bytesNeeded = (int)this._length;
                                this._state = State.DATA;
                                continue;
                            }
                        }
                        break;
                    }
                    case LENGTH_16: {
                        byte b = this._buffer.get();
                        --available;
                        if (this._masked) {
                            b ^= this._mask[this._m++ % 4];
                        }
                        this._length = this._length * 256L + (0xFF & b);
                        if (--this._bytesNeeded != 0) {
                            continue;
                        }
                        this._bytesNeeded = (int)this._length;
                        if (this._length > this._buffer.capacity()) {
                            this._state = State.SKIP;
                            ++events;
                            this._handler.close(1004, "frame size " + this._length + ">" + this._buffer.capacity());
                            continue;
                        }
                        this._state = State.DATA;
                        continue;
                    }
                    case LENGTH_63: {
                        byte b = this._buffer.get();
                        --available;
                        if (this._masked) {
                            b ^= this._mask[this._m++ % 4];
                        }
                        this._length = this._length * 256L + (0xFF & b);
                        if (--this._bytesNeeded != 0) {
                            continue;
                        }
                        this._bytesNeeded = (int)this._length;
                        if (this._length >= this._buffer.capacity()) {
                            this._state = State.SKIP;
                            ++events;
                            this._handler.close(1004, "frame size " + this._length + ">" + this._buffer.capacity());
                            continue;
                        }
                        this._state = State.DATA;
                        continue;
                    }
                    case SKIP: {
                        final int skip = Math.min(available, this._bytesNeeded);
                        this._buffer.skip(skip);
                        available -= skip;
                        this._bytesNeeded -= skip;
                        if (this._bytesNeeded == 0) {
                            this._state = State.START;
                            continue;
                        }
                        continue;
                    }
                }
            }
            if (this._state == State.DATA && available >= this._bytesNeeded) {
                Buffer data = this._buffer.get(this._bytesNeeded);
                if (this._masked) {
                    if (data.array() == null) {
                        data = this._buffer.asMutableBuffer();
                    }
                    final byte[] array = data.array();
                    for (int end = data.putIndex(), i = data.getIndex(); i < end; ++i) {
                        final byte[] array2 = array;
                        final int n = i;
                        array2[n] ^= this._mask[this._m++ % 4];
                    }
                }
                ++events;
                this._handler.onFrame(this._flags, this._opcode, data);
                this._bytesNeeded = 0;
                this._state = State.START;
                if (this._buffer.length() == 0) {
                    this._buffers.returnBuffer(this._buffer);
                    this._buffer = null;
                }
                return total_filled + events;
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
        LOG = Log.getLogger(WebSocketParserD06.class);
    }
    
    public enum State
    {
        START(0), 
        MASK(4), 
        OPCODE(1), 
        LENGTH_7(1), 
        LENGTH_16(2), 
        LENGTH_63(8), 
        DATA(0), 
        SKIP(1);
        
        int _needs;
        
        private State(final int needs) {
            this._needs = needs;
        }
        
        int getNeeds() {
            return this._needs;
        }
    }
}
