// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.View;
import java.io.IOException;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.util.log.Logger;

public abstract class AbstractGenerator implements Generator
{
    private static final Logger LOG;
    public static final int STATE_HEADER = 0;
    public static final int STATE_CONTENT = 2;
    public static final int STATE_FLUSHING = 3;
    public static final int STATE_END = 4;
    public static final byte[] NO_BYTES;
    protected final Buffers _buffers;
    protected final EndPoint _endp;
    protected int _state;
    protected int _status;
    protected int _version;
    protected Buffer _reason;
    protected Buffer _method;
    protected String _uri;
    protected long _contentWritten;
    protected long _contentLength;
    protected boolean _last;
    protected boolean _head;
    protected boolean _noContent;
    protected Boolean _persistent;
    protected Buffer _header;
    protected Buffer _buffer;
    protected Buffer _content;
    protected Buffer _date;
    private boolean _sendServerVersion;
    
    public AbstractGenerator(final Buffers buffers, final EndPoint io) {
        this._state = 0;
        this._status = 0;
        this._version = 11;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        this._last = false;
        this._head = false;
        this._noContent = false;
        this._persistent = null;
        this._buffers = buffers;
        this._endp = io;
    }
    
    public abstract boolean isRequest();
    
    public abstract boolean isResponse();
    
    public boolean isOpen() {
        return this._endp.isOpen();
    }
    
    public void reset() {
        this._state = 0;
        this._status = 0;
        this._version = 11;
        this._reason = null;
        this._last = false;
        this._head = false;
        this._noContent = false;
        this._persistent = null;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        this._date = null;
        this._content = null;
        this._method = null;
    }
    
    public void returnBuffers() {
        if (this._buffer != null && this._buffer.length() == 0) {
            this._buffers.returnBuffer(this._buffer);
            this._buffer = null;
        }
        if (this._header != null && this._header.length() == 0) {
            this._buffers.returnBuffer(this._header);
            this._header = null;
        }
    }
    
    public void resetBuffer() {
        if (this._state >= 3) {
            throw new IllegalStateException("Flushed");
        }
        this._last = false;
        this._persistent = null;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        this._content = null;
        if (this._buffer != null) {
            this._buffer.clear();
        }
    }
    
    public int getContentBufferSize() {
        if (this._buffer == null) {
            this._buffer = this._buffers.getBuffer();
        }
        return this._buffer.capacity();
    }
    
    public void increaseContentBufferSize(final int contentBufferSize) {
        if (this._buffer == null) {
            this._buffer = this._buffers.getBuffer();
        }
        if (contentBufferSize > this._buffer.capacity()) {
            final Buffer nb = this._buffers.getBuffer(contentBufferSize);
            nb.put(this._buffer);
            this._buffers.returnBuffer(this._buffer);
            this._buffer = nb;
        }
    }
    
    public Buffer getUncheckedBuffer() {
        return this._buffer;
    }
    
    public boolean getSendServerVersion() {
        return this._sendServerVersion;
    }
    
    public void setSendServerVersion(final boolean sendServerVersion) {
        this._sendServerVersion = sendServerVersion;
    }
    
    public int getState() {
        return this._state;
    }
    
    public boolean isState(final int state) {
        return this._state == state;
    }
    
    public boolean isComplete() {
        return this._state == 4;
    }
    
    public boolean isIdle() {
        return this._state == 0 && this._method == null && this._status == 0;
    }
    
    public boolean isCommitted() {
        return this._state != 0;
    }
    
    public boolean isHead() {
        return this._head;
    }
    
    public void setContentLength(final long value) {
        if (value < 0L) {
            this._contentLength = -3L;
        }
        else {
            this._contentLength = value;
        }
    }
    
    public void setHead(final boolean head) {
        this._head = head;
    }
    
    public boolean isPersistent() {
        return (this._persistent != null) ? this._persistent : (this.isRequest() || this._version > 10);
    }
    
    public void setPersistent(final boolean persistent) {
        this._persistent = persistent;
    }
    
    public void setVersion(final int version) {
        if (this._state != 0) {
            throw new IllegalStateException("STATE!=START " + this._state);
        }
        this._version = version;
        if (this._version == 9 && this._method != null) {
            this._noContent = true;
        }
    }
    
    public int getVersion() {
        return this._version;
    }
    
    public void setDate(final Buffer timeStampBuffer) {
        this._date = timeStampBuffer;
    }
    
    public void setRequest(final String method, final String uri) {
        if (method == null || "GET".equals(method)) {
            this._method = HttpMethods.GET_BUFFER;
        }
        else {
            this._method = HttpMethods.CACHE.lookup(method);
        }
        this._uri = uri;
        if (this._version == 9) {
            this._noContent = true;
        }
    }
    
    public void setResponse(final int status, final String reason) {
        if (this._state != 0) {
            throw new IllegalStateException("STATE!=START");
        }
        this._method = null;
        this._status = status;
        if (reason != null) {
            int len = reason.length();
            if (len > 1024) {
                len = 1024;
            }
            this._reason = new ByteArrayBuffer(len);
            for (int i = 0; i < len; ++i) {
                final char ch = reason.charAt(i);
                if (ch != '\r' && ch != '\n') {
                    this._reason.put((byte)ch);
                }
                else {
                    this._reason.put((byte)32);
                }
            }
        }
    }
    
    public abstract int prepareUncheckedAddContent() throws IOException;
    
    void uncheckedAddContent(final int b) {
        this._buffer.put((byte)b);
    }
    
    public void completeUncheckedAddContent() {
        if (this._noContent) {
            if (this._buffer != null) {
                this._buffer.clear();
            }
        }
        else {
            this._contentWritten += this._buffer.length();
            if (this._head) {
                this._buffer.clear();
            }
        }
    }
    
    public boolean isBufferFull() {
        if (this._buffer != null && this._buffer.space() == 0) {
            if (this._buffer.length() == 0 && !this._buffer.isImmutable()) {
                this._buffer.compact();
            }
            return this._buffer.space() == 0;
        }
        return this._content != null && this._content.length() > 0;
    }
    
    public boolean isWritten() {
        return this._contentWritten > 0L;
    }
    
    public boolean isAllContentWritten() {
        return this._contentLength >= 0L && this._contentWritten >= this._contentLength;
    }
    
    public abstract void completeHeader(final HttpFields p0, final boolean p1) throws IOException;
    
    public void complete() throws IOException {
        if (this._state == 0) {
            throw new IllegalStateException("State==HEADER");
        }
        if (this._contentLength >= 0L && this._contentLength != this._contentWritten && !this._head) {
            if (AbstractGenerator.LOG.isDebugEnabled()) {
                AbstractGenerator.LOG.debug("ContentLength written==" + this._contentWritten + " != contentLength==" + this._contentLength, new Object[0]);
            }
            this._persistent = false;
        }
    }
    
    public abstract int flushBuffer() throws IOException;
    
    public void flush(final long maxIdleTime) throws IOException {
        long now = System.currentTimeMillis();
        final long end = now + maxIdleTime;
        final Buffer content = this._content;
        final Buffer buffer = this._buffer;
        if ((content != null && content.length() > 0) || (buffer != null && buffer.length() > 0) || this.isBufferFull()) {
            this.flushBuffer();
            while (now < end && ((content != null && content.length() > 0) || (buffer != null && buffer.length() > 0)) && this._endp.isOpen() && !this._endp.isOutputShutdown()) {
                this.blockForOutput(end - now);
                now = System.currentTimeMillis();
            }
        }
    }
    
    public void sendError(final int code, final String reason, final String content, final boolean close) throws IOException {
        if (close) {
            this._persistent = false;
        }
        if (this.isCommitted()) {
            AbstractGenerator.LOG.debug("sendError on committed: {} {}", code, reason);
        }
        else {
            AbstractGenerator.LOG.debug("sendError: {} {}", code, reason);
            this.setResponse(code, reason);
            if (content != null) {
                this.completeHeader(null, false);
                this.addContent(new View(new ByteArrayBuffer(content)), true);
            }
            else {
                this.completeHeader(null, true);
            }
            this.complete();
        }
    }
    
    public long getContentWritten() {
        return this._contentWritten;
    }
    
    public void blockForOutput(final long maxIdleTime) throws IOException {
        if (this._endp.isBlocking()) {
            try {
                this.flushBuffer();
                return;
            }
            catch (IOException e) {
                this._endp.close();
                throw e;
            }
        }
        if (!this._endp.blockWritable(maxIdleTime)) {
            this._endp.close();
            throw new EofException("timeout");
        }
        this.flushBuffer();
    }
    
    static {
        LOG = Log.getLogger(AbstractGenerator.class);
        NO_BYTES = new byte[0];
    }
}
