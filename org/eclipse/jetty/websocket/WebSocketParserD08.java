// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;

public class WebSocketParserD08 implements WebSocketParser
{
    private static final Logger LOG;
    private final WebSocketBuffers _buffers;
    private final EndPoint _endp;
    private final FrameHandler _handler;
    private final boolean _shouldBeMasked;
    private State _state;
    private Buffer _buffer;
    private byte _flags;
    private byte _opcode;
    private int _bytesNeeded;
    private long _length;
    private boolean _masked;
    private final byte[] _mask;
    private int _m;
    private boolean _skip;
    private boolean _fakeFragments;
    
    public WebSocketParserD08(final WebSocketBuffers buffers, final EndPoint endp, final FrameHandler handler, final boolean shouldBeMasked) {
        this._mask = new byte[4];
        this._fakeFragments = true;
        this._buffers = buffers;
        this._endp = endp;
        this._handler = handler;
        this._shouldBeMasked = shouldBeMasked;
        this._state = State.START;
    }
    
    public boolean isFakeFragments() {
        return this._fakeFragments;
    }
    
    public void setFakeFragments(final boolean fakeFragments) {
        this._fakeFragments = fakeFragments;
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
                    if (this._fakeFragments && this._state == State.DATA) {
                        Buffer data = this._buffer.get(4 * (available / 4));
                        this._buffer.compact();
                        if (this._masked) {
                            if (data.array() == null) {
                                data = this._buffer.asMutableBuffer();
                            }
                            final byte[] array = data.array();
                            for (int end = data.putIndex(), i = data.getIndex(); i < end; ++i) {
                                final byte[] array3 = array;
                                final int n = i;
                                array3[n] ^= this._mask[this._m++ % 4];
                            }
                        }
                        ++events;
                        this._bytesNeeded -= data.length();
                        this._handler.onFrame((byte)(this._flags & 0xF7), this._opcode, data);
                        this._opcode = 0;
                    }
                    if (this._buffer.space() == 0) {
                        throw new IllegalStateException("FULL: " + this._state + " " + this._bytesNeeded + ">" + this._buffer.capacity());
                    }
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
                    WebSocketParserD08.LOG.debug(e);
                    return (total_filled + events > 0) ? (total_filled + events) : -1;
                }
                break;
            }
            while (this._state != State.DATA && available >= ((this._state == State.SKIP) ? 1 : this._bytesNeeded)) {
                switch (this._state) {
                    case START: {
                        this._skip = false;
                        this._state = State.OPCODE;
                        this._bytesNeeded = this._state.getNeeds();
                        continue;
                    }
                    case OPCODE: {
                        final byte b = this._buffer.get();
                        --available;
                        this._opcode = (byte)(b & 0xF);
                        this._flags = (byte)(0xF & b >> 4);
                        if (WebSocketConnectionD08.isControlFrame(this._opcode) && !WebSocketConnectionD08.isLastFrame(this._flags)) {
                            ++events;
                            WebSocketParserD08.LOG.warn("Fragmented Control from " + this._endp, new Object[0]);
                            this._handler.close(1002, "Fragmented control");
                            this._skip = true;
                        }
                        this._state = State.LENGTH_7;
                        this._bytesNeeded = this._state.getNeeds();
                        continue;
                    }
                    case LENGTH_7: {
                        byte b = this._buffer.get();
                        --available;
                        this._masked = ((b & 0x80) != 0x0);
                        b &= 0x7F;
                        switch (b) {
                            case Byte.MAX_VALUE: {
                                this._length = 0L;
                                this._state = State.LENGTH_63;
                                break;
                            }
                            case 126: {
                                this._length = 0L;
                                this._state = State.LENGTH_16;
                                break;
                            }
                            default: {
                                this._length = (0x7F & b);
                                this._state = (this._masked ? State.MASK : State.PAYLOAD);
                                break;
                            }
                        }
                        this._bytesNeeded = this._state.getNeeds();
                        continue;
                    }
                    case LENGTH_16: {
                        final byte b = this._buffer.get();
                        --available;
                        this._length = this._length * 256L + (0xFF & b);
                        final int bytesNeeded = this._bytesNeeded - 1;
                        this._bytesNeeded = bytesNeeded;
                        if (bytesNeeded == 0) {
                            if (this._length > this._buffer.capacity() && !this._fakeFragments) {
                                ++events;
                                this._handler.close(1003, "frame size " + this._length + ">" + this._buffer.capacity());
                                this._skip = true;
                            }
                            this._state = (this._masked ? State.MASK : State.PAYLOAD);
                            this._bytesNeeded = this._state.getNeeds();
                            continue;
                        }
                        continue;
                    }
                    case LENGTH_63: {
                        final byte b = this._buffer.get();
                        --available;
                        this._length = this._length * 256L + (0xFF & b);
                        final int bytesNeeded2 = this._bytesNeeded - 1;
                        this._bytesNeeded = bytesNeeded2;
                        if (bytesNeeded2 == 0) {
                            this._bytesNeeded = (int)this._length;
                            if (this._length >= this._buffer.capacity() && !this._fakeFragments) {
                                ++events;
                                this._handler.close(1003, "frame size " + this._length + ">" + this._buffer.capacity());
                                this._skip = true;
                            }
                            this._state = (this._masked ? State.MASK : State.PAYLOAD);
                            this._bytesNeeded = this._state.getNeeds();
                            continue;
                        }
                        continue;
                    }
                    case MASK: {
                        this._buffer.get(this._mask, 0, 4);
                        this._m = 0;
                        available -= 4;
                        this._state = State.PAYLOAD;
                        this._bytesNeeded = this._state.getNeeds();
                        continue;
                    }
                    case PAYLOAD: {
                        this._bytesNeeded = (int)this._length;
                        this._state = (this._skip ? State.SKIP : State.DATA);
                    }
                    case DATA: {
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
                if (this._masked != this._shouldBeMasked) {
                    this._buffer.skip(this._bytesNeeded);
                    this._state = State.START;
                    ++events;
                    this._handler.close(1002, "bad mask");
                }
                else {
                    Buffer data2 = this._buffer.get(this._bytesNeeded);
                    if (this._masked) {
                        if (data2.array() == null) {
                            data2 = this._buffer.asMutableBuffer();
                        }
                        final byte[] array2 = data2.array();
                        for (int end2 = data2.putIndex(), j = data2.getIndex(); j < end2; ++j) {
                            final byte[] array4 = array2;
                            final int n2 = j;
                            array4[n2] ^= this._mask[this._m++ % 4];
                        }
                    }
                    ++events;
                    this._handler.onFrame(this._flags, this._opcode, data2);
                    this._bytesNeeded = 0;
                    this._state = State.START;
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
    
    public void returnBuffer() {
        if (this._buffer != null && this._buffer.length() == 0) {
            this._buffers.returnBuffer(this._buffer);
            this._buffer = null;
        }
    }
    
    @Override
    public String toString() {
        final Buffer buffer = this._buffer;
        return WebSocketParserD08.class.getSimpleName() + "@" + Integer.toHexString(this.hashCode()) + "|" + this._state + "|" + ((buffer == null) ? "<>" : buffer.toDetailString());
    }
    
    static {
        LOG = Log.getLogger(WebSocketParserD08.class);
    }
    
    public enum State
    {
        START(0), 
        OPCODE(1), 
        LENGTH_7(1), 
        LENGTH_16(2), 
        LENGTH_63(8), 
        MASK(4), 
        PAYLOAD(0), 
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
