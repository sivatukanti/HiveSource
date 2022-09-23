// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import javax.servlet.ServletInputStream;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.io.BufferUtil;
import org.mortbay.log.Log;
import java.io.IOException;
import org.mortbay.io.BufferCache;
import org.mortbay.io.View;
import org.mortbay.io.Buffer;
import org.mortbay.io.EndPoint;
import org.mortbay.io.Buffers;

public class HttpParser implements Parser
{
    public static final int STATE_START = -13;
    public static final int STATE_FIELD0 = -12;
    public static final int STATE_SPACE1 = -11;
    public static final int STATE_FIELD1 = -10;
    public static final int STATE_SPACE2 = -9;
    public static final int STATE_END0 = -8;
    public static final int STATE_END1 = -7;
    public static final int STATE_FIELD2 = -6;
    public static final int STATE_HEADER = -5;
    public static final int STATE_HEADER_NAME = -4;
    public static final int STATE_HEADER_IN_NAME = -3;
    public static final int STATE_HEADER_VALUE = -2;
    public static final int STATE_HEADER_IN_VALUE = -1;
    public static final int STATE_END = 0;
    public static final int STATE_EOF_CONTENT = 1;
    public static final int STATE_CONTENT = 2;
    public static final int STATE_CHUNKED_CONTENT = 3;
    public static final int STATE_CHUNK_SIZE = 4;
    public static final int STATE_CHUNK_PARAMS = 5;
    public static final int STATE_CHUNK = 6;
    private Buffers _buffers;
    private EndPoint _endp;
    private Buffer _header;
    private Buffer _body;
    private Buffer _buffer;
    private View _contentView;
    private int _headerBufferSize;
    private int _contentBufferSize;
    private EventHandler _handler;
    private BufferCache.CachedBuffer _cached;
    private View.CaseInsensitive _tok0;
    private View.CaseInsensitive _tok1;
    private String _multiLineValue;
    private int _responseStatus;
    private boolean _forceContentBuffer;
    private Input _input;
    protected int _state;
    protected byte _eol;
    protected int _length;
    protected long _contentLength;
    protected long _contentPosition;
    protected int _chunkLength;
    protected int _chunkPosition;
    
    public HttpParser(final Buffer buffer, final EventHandler handler) {
        this._contentView = new View();
        this._state = -13;
        this._header = buffer;
        this._buffer = buffer;
        this._handler = handler;
        if (buffer != null) {
            this._tok0 = new View.CaseInsensitive(buffer);
            this._tok1 = new View.CaseInsensitive(buffer);
            this._tok0.setPutIndex(this._tok0.getIndex());
            this._tok1.setPutIndex(this._tok1.getIndex());
        }
    }
    
    public HttpParser(final Buffers buffers, final EndPoint endp, final EventHandler handler, final int headerBufferSize, final int contentBufferSize) {
        this._contentView = new View();
        this._state = -13;
        this._buffers = buffers;
        this._endp = endp;
        this._handler = handler;
        this._headerBufferSize = headerBufferSize;
        this._contentBufferSize = contentBufferSize;
    }
    
    public long getContentLength() {
        return this._contentLength;
    }
    
    public long getContentRead() {
        return this._contentPosition;
    }
    
    public int getState() {
        return this._state;
    }
    
    public boolean inContentState() {
        return this._state > 0;
    }
    
    public boolean inHeaderState() {
        return this._state < 0;
    }
    
    public boolean isChunking() {
        return this._contentLength == -2L;
    }
    
    public boolean isIdle() {
        return this.isState(-13);
    }
    
    public boolean isComplete() {
        return this.isState(0);
    }
    
    public boolean isMoreInBuffer() throws IOException {
        return (this._header != null && this._header.hasContent()) || (this._body != null && this._body.hasContent());
    }
    
    public boolean isState(final int state) {
        return this._state == state;
    }
    
    public void parse() throws IOException {
        if (this._state == 0) {
            this.reset(false);
        }
        if (this._state != -13) {
            throw new IllegalStateException("!START");
        }
        while (this._state != 0) {
            this.parseNext();
        }
    }
    
    public long parseAvailable() throws IOException {
        long len = this.parseNext();
        long total = (len > 0L) ? len : 0L;
        while (!this.isComplete() && this._buffer != null && this._buffer.length() > 0) {
            len = this.parseNext();
            if (len > 0L) {
                total += len;
            }
        }
        return total;
    }
    
    public long parseNext() throws IOException {
        long total_filled = -1L;
        if (this._state == 0) {
            return -1L;
        }
        if (this._buffer == null) {
            if (this._header == null) {
                this._header = this._buffers.getBuffer(this._headerBufferSize);
            }
            this._buffer = this._header;
            this._tok0 = new View.CaseInsensitive(this._header);
            this._tok1 = new View.CaseInsensitive(this._header);
            this._tok0.setPutIndex(this._tok0.getIndex());
            this._tok1.setPutIndex(this._tok1.getIndex());
        }
        if (this._state == 2 && this._contentPosition == this._contentLength) {
            this._state = 0;
            this._handler.messageComplete(this._contentPosition);
            return total_filled;
        }
        int length = this._buffer.length();
        if (length == 0) {
            int filled = -1;
            if (this._body != null && this._buffer != this._body) {
                this._buffer = this._body;
                filled = this._buffer.length();
            }
            if (this._buffer.markIndex() == 0 && this._buffer.putIndex() == this._buffer.capacity()) {
                throw new HttpException(413, "FULL");
            }
            IOException ioex = null;
            if (this._endp != null && filled <= 0) {
                if (this._buffer == this._body) {
                    this._buffer.compact();
                }
                if (this._buffer.space() == 0) {
                    throw new HttpException(413, "FULL " + ((this._buffer == this._body) ? "body" : "head"));
                }
                try {
                    if (total_filled < 0L) {
                        total_filled = 0L;
                    }
                    filled = this._endp.fill(this._buffer);
                    if (filled > 0) {
                        total_filled += filled;
                    }
                }
                catch (IOException e) {
                    Log.debug(e);
                    ioex = e;
                    filled = -1;
                }
            }
            if (filled < 0) {
                if (this._state == 1) {
                    if (this._buffer.length() > 0) {
                        final Buffer chunk = this._buffer.get(this._buffer.length());
                        this._contentPosition += chunk.length();
                        this._contentView.update(chunk);
                        this._handler.content(chunk);
                    }
                    this._state = 0;
                    this._handler.messageComplete(this._contentPosition);
                    return total_filled;
                }
                this.reset(true);
                throw new EofException(ioex);
            }
            else {
                length = this._buffer.length();
            }
        }
        final byte[] array = this._buffer.array();
        while (this._state < 0 && length-- > 0) {
            final byte ch = this._buffer.get();
            if (this._eol == 13 && ch == 10) {
                this._eol = 10;
            }
            else {
                this._eol = 0;
                switch (this._state) {
                    case -13: {
                        this._contentLength = -3L;
                        this._cached = null;
                        if (ch > 32 || ch < 0) {
                            this._buffer.mark();
                            this._state = -12;
                            continue;
                        }
                        continue;
                    }
                    case -12: {
                        if (ch == 32) {
                            this._tok0.update(this._buffer.markIndex(), this._buffer.getIndex() - 1);
                            this._state = -11;
                            continue;
                        }
                        if (ch < 32 && ch >= 0) {
                            throw new HttpException(400);
                        }
                        continue;
                    }
                    case -11: {
                        if (ch > 32 || ch < 0) {
                            this._buffer.mark();
                            this._state = -10;
                            continue;
                        }
                        if (ch < 32) {
                            throw new HttpException(400);
                        }
                        continue;
                    }
                    case -10: {
                        if (ch == 32) {
                            this._tok1.update(this._buffer.markIndex(), this._buffer.getIndex() - 1);
                            this._state = -9;
                            continue;
                        }
                        if (ch < 32 && ch >= 0) {
                            this._handler.startRequest(HttpMethods.CACHE.lookup(this._tok0), this._buffer.sliceFromMark(), null);
                            this._state = 0;
                            this._handler.headerComplete();
                            this._handler.messageComplete(this._contentPosition);
                            return total_filled;
                        }
                        continue;
                    }
                    case -9: {
                        if (ch > 32 || ch < 0) {
                            this._buffer.mark();
                            this._state = -6;
                            continue;
                        }
                        if (ch < 32) {
                            this._handler.startRequest(HttpMethods.CACHE.lookup(this._tok0), this._tok1, null);
                            this._state = 0;
                            this._handler.headerComplete();
                            this._handler.messageComplete(this._contentPosition);
                            return total_filled;
                        }
                        continue;
                    }
                    case -6: {
                        if (ch == 13 || ch == 10) {
                            final Buffer method = HttpMethods.CACHE.lookup(this._tok0);
                            if (method == this._tok0 && this._tok1.length() == 3 && Character.isDigit((char)this._tok1.peek())) {
                                this._responseStatus = BufferUtil.toInt(this._tok1);
                                this._handler.startResponse(HttpVersions.CACHE.lookup(this._tok0), this._responseStatus, this._buffer.sliceFromMark());
                            }
                            else {
                                this._handler.startRequest(method, this._tok1, HttpVersions.CACHE.lookup(this._buffer.sliceFromMark()));
                            }
                            this._eol = ch;
                            this._state = -5;
                            this._tok0.setPutIndex(this._tok0.getIndex());
                            this._tok1.setPutIndex(this._tok1.getIndex());
                            this._multiLineValue = null;
                            continue;
                        }
                        continue;
                    }
                    case -5: {
                        switch (ch) {
                            case 9:
                            case 32:
                            case 58: {
                                this._length = -1;
                                this._state = -2;
                                continue;
                            }
                            default: {
                                if (this._cached != null || this._tok0.length() > 0 || this._tok1.length() > 0 || this._multiLineValue != null) {
                                    final Buffer header = (this._cached != null) ? this._cached : HttpHeaders.CACHE.lookup(this._tok0);
                                    this._cached = null;
                                    Buffer value = (this._multiLineValue == null) ? this._tok1 : new ByteArrayBuffer(this._multiLineValue);
                                    final int ho = HttpHeaders.CACHE.getOrdinal(header);
                                    if (ho >= 0) {
                                        int vo = -1;
                                        switch (ho) {
                                            case 12: {
                                                if (this._contentLength == -2L) {
                                                    break;
                                                }
                                                try {
                                                    this._contentLength = BufferUtil.toLong(value);
                                                }
                                                catch (NumberFormatException e2) {
                                                    Log.ignore(e2);
                                                    throw new HttpException(400);
                                                }
                                                if (this._contentLength <= 0L) {
                                                    this._contentLength = 0L;
                                                    break;
                                                }
                                                break;
                                            }
                                            case 5: {
                                                value = HttpHeaderValues.CACHE.lookup(value);
                                                vo = HttpHeaderValues.CACHE.getOrdinal(value);
                                                if (2 == vo) {
                                                    this._contentLength = -2L;
                                                    break;
                                                }
                                                final String c = value.toString();
                                                if (c.endsWith("chunked")) {
                                                    this._contentLength = -2L;
                                                    break;
                                                }
                                                if (c.indexOf("chunked") >= 0) {
                                                    throw new HttpException(400, null);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    this._handler.parsedHeader(header, value);
                                    this._tok0.setPutIndex(this._tok0.getIndex());
                                    this._tok1.setPutIndex(this._tok1.getIndex());
                                    this._multiLineValue = null;
                                }
                                if (ch == 13 || ch == 10) {
                                    if (this._contentLength == -3L) {
                                        if (this._responseStatus == 0 || this._responseStatus == 304 || this._responseStatus == 204 || this._responseStatus < 200) {
                                            this._contentLength = 0L;
                                        }
                                        else {
                                            this._contentLength = -1L;
                                        }
                                    }
                                    this._contentPosition = 0L;
                                    this._eol = ch;
                                    switch ((this._contentLength > 2147483647L) ? Integer.MAX_VALUE : ((int)this._contentLength)) {
                                        case -1: {
                                            this._state = 1;
                                            if (this._body == null && this._buffers != null) {
                                                this._body = this._buffers.getBuffer(this._contentBufferSize);
                                            }
                                            this._handler.headerComplete();
                                            break;
                                        }
                                        case -2: {
                                            this._state = 3;
                                            if (this._body == null && this._buffers != null) {
                                                this._body = this._buffers.getBuffer(this._contentBufferSize);
                                            }
                                            this._handler.headerComplete();
                                            break;
                                        }
                                        case 0: {
                                            this._state = 0;
                                            this._handler.headerComplete();
                                            this._handler.messageComplete(this._contentPosition);
                                            break;
                                        }
                                        default: {
                                            this._state = 2;
                                            if (this._forceContentBuffer || (this._buffers != null && this._body == null && this._buffer == this._header && this._contentLength >= this._header.capacity() - this._header.getIndex())) {
                                                this._body = this._buffers.getBuffer(this._contentBufferSize);
                                            }
                                            this._handler.headerComplete();
                                            break;
                                        }
                                    }
                                    return total_filled;
                                }
                                this._length = 1;
                                this._buffer.mark();
                                this._state = -4;
                                if (array == null) {
                                    continue;
                                }
                                this._cached = HttpHeaders.CACHE.getBest(array, this._buffer.markIndex(), length + 1);
                                if (this._cached != null) {
                                    this._length = this._cached.length();
                                    this._buffer.setGetIndex(this._buffer.markIndex() + this._length);
                                    length = this._buffer.length();
                                    continue;
                                }
                                continue;
                            }
                        }
                        break;
                    }
                    case -4: {
                        switch (ch) {
                            case 10:
                            case 13: {
                                if (this._length > 0) {
                                    this._tok0.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                }
                                this._eol = ch;
                                this._state = -5;
                                continue;
                            }
                            case 58: {
                                if (this._length > 0 && this._cached == null) {
                                    this._tok0.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                }
                                this._length = -1;
                                this._state = -2;
                                continue;
                            }
                            case 9:
                            case 32: {
                                continue;
                            }
                            default: {
                                this._cached = null;
                                if (this._length == -1) {
                                    this._buffer.mark();
                                }
                                this._length = this._buffer.getIndex() - this._buffer.markIndex();
                                this._state = -3;
                                continue;
                            }
                        }
                        break;
                    }
                    case -3: {
                        switch (ch) {
                            case 10:
                            case 13: {
                                if (this._length > 0) {
                                    this._tok0.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                }
                                this._eol = ch;
                                this._state = -5;
                                continue;
                            }
                            case 58: {
                                if (this._length > 0 && this._cached == null) {
                                    this._tok0.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                }
                                this._length = -1;
                                this._state = -2;
                                continue;
                            }
                            case 9:
                            case 32: {
                                this._state = -4;
                                continue;
                            }
                            default: {
                                this._cached = null;
                                ++this._length;
                                continue;
                            }
                        }
                        break;
                    }
                    case -2: {
                        switch (ch) {
                            case 10:
                            case 13: {
                                if (this._length > 0) {
                                    if (this._tok1.length() == 0) {
                                        this._tok1.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                    }
                                    else {
                                        if (this._multiLineValue == null) {
                                            this._multiLineValue = this._tok1.toString();
                                        }
                                        this._tok1.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                        this._multiLineValue = this._multiLineValue + " " + this._tok1.toString();
                                    }
                                }
                                this._eol = ch;
                                this._state = -5;
                                continue;
                            }
                            case 9:
                            case 32: {
                                continue;
                            }
                            default: {
                                if (this._length == -1) {
                                    this._buffer.mark();
                                }
                                this._length = this._buffer.getIndex() - this._buffer.markIndex();
                                this._state = -1;
                                continue;
                            }
                        }
                        break;
                    }
                    case -1: {
                        switch (ch) {
                            case 10:
                            case 13: {
                                if (this._length > 0) {
                                    if (this._tok1.length() == 0) {
                                        this._tok1.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                    }
                                    else {
                                        if (this._multiLineValue == null) {
                                            this._multiLineValue = this._tok1.toString();
                                        }
                                        this._tok1.update(this._buffer.markIndex(), this._buffer.markIndex() + this._length);
                                        this._multiLineValue = this._multiLineValue + " " + this._tok1.toString();
                                    }
                                }
                                this._eol = ch;
                                this._state = -5;
                                continue;
                            }
                            case 9:
                            case 32: {
                                this._state = -2;
                                continue;
                            }
                            default: {
                                ++this._length;
                                continue;
                            }
                        }
                        break;
                    }
                }
            }
        }
        for (length = this._buffer.length(); this._state > 0 && length > 0; length = this._buffer.length()) {
            if (this._eol == 13 && this._buffer.peek() == 10) {
                this._eol = this._buffer.get();
            }
            else {
                this._eol = 0;
                switch (this._state) {
                    case 1: {
                        final Buffer chunk = this._buffer.get(this._buffer.length());
                        this._contentPosition += chunk.length();
                        this._contentView.update(chunk);
                        this._handler.content(chunk);
                        return total_filled;
                    }
                    case 2: {
                        final long remaining = this._contentLength - this._contentPosition;
                        if (remaining == 0L) {
                            this._state = 0;
                            this._handler.messageComplete(this._contentPosition);
                            return total_filled;
                        }
                        if (length > remaining) {
                            length = (int)remaining;
                        }
                        final Buffer chunk = this._buffer.get(length);
                        this._contentPosition += chunk.length();
                        this._contentView.update(chunk);
                        this._handler.content(chunk);
                        if (this._contentPosition == this._contentLength) {
                            this._state = 0;
                            this._handler.messageComplete(this._contentPosition);
                        }
                        return total_filled;
                    }
                    case 3: {
                        final byte ch = this._buffer.peek();
                        if (ch == 13 || ch == 10) {
                            this._eol = this._buffer.get();
                            break;
                        }
                        if (ch <= 32) {
                            this._buffer.get();
                            break;
                        }
                        this._chunkLength = 0;
                        this._chunkPosition = 0;
                        this._state = 4;
                        break;
                    }
                    case 4: {
                        final byte ch = this._buffer.get();
                        if (ch == 13 || ch == 10) {
                            this._eol = ch;
                            if (this._chunkLength == 0) {
                                this._state = 0;
                                this._handler.messageComplete(this._contentPosition);
                                return total_filled;
                            }
                            this._state = 6;
                            break;
                        }
                        else {
                            if (ch <= 32 || ch == 59) {
                                this._state = 5;
                                break;
                            }
                            if (ch >= 48 && ch <= 57) {
                                this._chunkLength = this._chunkLength * 16 + (ch - 48);
                                break;
                            }
                            if (ch >= 97 && ch <= 102) {
                                this._chunkLength = this._chunkLength * 16 + (10 + ch - 97);
                                break;
                            }
                            if (ch >= 65 && ch <= 70) {
                                this._chunkLength = this._chunkLength * 16 + (10 + ch - 65);
                                break;
                            }
                            throw new IOException("bad chunk char: " + ch);
                        }
                        break;
                    }
                    case 5: {
                        final byte ch = this._buffer.get();
                        if (ch != 13 && ch != 10) {
                            break;
                        }
                        this._eol = ch;
                        if (this._chunkLength == 0) {
                            this._state = 0;
                            this._handler.messageComplete(this._contentPosition);
                            return total_filled;
                        }
                        this._state = 6;
                        break;
                    }
                    case 6: {
                        final int remaining2 = this._chunkLength - this._chunkPosition;
                        if (remaining2 == 0) {
                            this._state = 3;
                            break;
                        }
                        if (length > remaining2) {
                            length = remaining2;
                        }
                        final Buffer chunk = this._buffer.get(length);
                        this._contentPosition += chunk.length();
                        this._chunkPosition += chunk.length();
                        this._contentView.update(chunk);
                        this._handler.content(chunk);
                        return total_filled;
                    }
                }
            }
        }
        return total_filled;
    }
    
    public long fill() throws IOException {
        if (this._buffer == null) {
            final Buffer headerBuffer = this.getHeaderBuffer();
            this._header = headerBuffer;
            this._buffer = headerBuffer;
            this._tok0 = new View.CaseInsensitive(this._buffer);
            this._tok1 = new View.CaseInsensitive(this._buffer);
        }
        if (this._body != null && this._buffer != this._body) {
            this._buffer = this._body;
        }
        if (this._buffer == this._body) {
            this._buffer.compact();
        }
        final int space = this._buffer.space();
        if (space == 0) {
            throw new HttpException(413, "FULL " + ((this._buffer == this._body) ? "body" : "head"));
        }
        int filled = -1;
        if (this._endp != null) {
            try {
                filled = this._endp.fill(this._buffer);
            }
            catch (IOException e) {
                Log.debug(e);
                this.reset(true);
                throw (e instanceof EofException) ? e : new EofException(e);
            }
        }
        return filled;
    }
    
    public void skipCRLF() {
        while (this._header != null && this._header.length() > 0) {
            final byte ch = this._header.peek();
            if (ch != 13 && ch != 10) {
                break;
            }
            this._eol = ch;
            this._header.skip(1);
        }
        while (this._body != null && this._body.length() > 0) {
            final byte ch = this._body.peek();
            if (ch != 13 && ch != 10) {
                break;
            }
            this._eol = ch;
            this._body.skip(1);
        }
    }
    
    public void reset(final boolean returnBuffers) {
        synchronized (this) {
            this._contentView.setGetIndex(this._contentView.putIndex());
            this._state = -13;
            this._contentLength = -3L;
            this._contentPosition = 0L;
            this._length = 0;
            this._responseStatus = 0;
            if (this._buffer != null && this._buffer.length() > 0 && this._eol == 13 && this._buffer.peek() == 10) {
                this._buffer.skip(1);
                this._eol = 10;
            }
            if (this._body != null) {
                if (this._body.hasContent()) {
                    this._header.setMarkIndex(-1);
                    this._header.compact();
                    int take = this._header.space();
                    if (take > this._body.length()) {
                        take = this._body.length();
                    }
                    this._body.peek(this._body.getIndex(), take);
                    this._body.skip(this._header.put(this._body.peek(this._body.getIndex(), take)));
                }
                if (this._body.length() == 0) {
                    if (this._buffers != null && returnBuffers) {
                        this._buffers.returnBuffer(this._body);
                    }
                    this._body = null;
                }
                else {
                    this._body.setMarkIndex(-1);
                    this._body.compact();
                }
            }
            if (this._header != null) {
                this._header.setMarkIndex(-1);
                if (!this._header.hasContent() && this._buffers != null && returnBuffers) {
                    this._buffers.returnBuffer(this._header);
                    this._header = null;
                    this._buffer = null;
                }
                else {
                    this._header.compact();
                    this._tok0.update(this._header);
                    this._tok0.update(0, 0);
                    this._tok1.update(this._header);
                    this._tok1.update(0, 0);
                }
            }
            this._buffer = this._header;
        }
    }
    
    public void setState(final int state) {
        this._state = state;
        this._contentLength = -3L;
    }
    
    public String toString(final Buffer buf) {
        return "state=" + this._state + " length=" + this._length + " buf=" + buf.hashCode();
    }
    
    public String toString() {
        return "state=" + this._state + " length=" + this._length + " len=" + this._contentLength;
    }
    
    public Buffer getHeaderBuffer() {
        if (this._header == null) {
            this._header = this._buffers.getBuffer(this._headerBufferSize);
        }
        return this._header;
    }
    
    public Buffer getBodyBuffer() {
        return this._body;
    }
    
    public void setForceContentBuffer(final boolean force) {
        this._forceContentBuffer = force;
    }
    
    public abstract static class EventHandler
    {
        public abstract void content(final Buffer p0) throws IOException;
        
        public void headerComplete() throws IOException {
        }
        
        public void messageComplete(final long contentLength) throws IOException {
        }
        
        public void parsedHeader(final Buffer name, final Buffer value) throws IOException {
        }
        
        public abstract void startRequest(final Buffer p0, final Buffer p1, final Buffer p2) throws IOException;
        
        public abstract void startResponse(final Buffer p0, final int p1, final Buffer p2) throws IOException;
    }
    
    public static class Input extends ServletInputStream
    {
        protected HttpParser _parser;
        protected EndPoint _endp;
        protected long _maxIdleTime;
        protected Buffer _content;
        
        public Input(final HttpParser parser, final long maxIdleTime) {
            this._parser = parser;
            this._endp = parser._endp;
            this._maxIdleTime = maxIdleTime;
            this._content = this._parser._contentView;
            this._parser._input = this;
        }
        
        public int read() throws IOException {
            int c = -1;
            if (this.blockForContent()) {
                c = (0xFF & this._content.get());
            }
            return c;
        }
        
        public int read(final byte[] b, final int off, final int len) throws IOException {
            int l = -1;
            if (this.blockForContent()) {
                l = this._content.get(b, off, len);
            }
            return l;
        }
        
        private boolean blockForContent() throws IOException {
            if (this._content.length() > 0) {
                return true;
            }
            if (this._parser.getState() <= 0) {
                return false;
            }
            if (this._endp == null) {
                this._parser.parseNext();
            }
            else {
                if (this._endp.isBlocking()) {
                    try {
                        this._parser.parseNext();
                        while (this._content.length() == 0 && !this._parser.isState(0) && this._endp.isOpen()) {
                            this._parser.parseNext();
                        }
                        return this._content.length() > 0;
                    }
                    catch (IOException e) {
                        this._endp.close();
                        throw e;
                    }
                }
                this._parser.parseNext();
                while (this._content.length() == 0 && !this._parser.isState(0) && this._endp.isOpen()) {
                    if (this._endp.isBufferingInput() && this._parser.parseNext() > 0L) {
                        continue;
                    }
                    if (!this._endp.blockReadable(this._maxIdleTime)) {
                        this._endp.close();
                        throw new EofException("timeout");
                    }
                    this._parser.parseNext();
                }
            }
            return this._content.length() > 0;
        }
        
        public int available() throws IOException {
            if (this._content != null && this._content.length() > 0) {
                return this._content.length();
            }
            if (!this._endp.isBlocking()) {
                this._parser.parseNext();
            }
            return (this._content == null) ? 0 : this._content.length();
        }
    }
}
