// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import org.mortbay.util.StringUtil;
import org.mortbay.util.ByteArrayOutputStream2;
import java.io.Writer;
import javax.servlet.ServletOutputStream;
import java.lang.reflect.Field;
import org.mortbay.io.View;
import org.mortbay.log.Log;
import java.io.IOException;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.util.TypeUtil;
import org.mortbay.io.EndPoint;
import org.mortbay.io.Buffers;
import org.mortbay.io.Buffer;

public abstract class AbstractGenerator implements Generator
{
    public static final int STATE_HEADER = 0;
    public static final int STATE_CONTENT = 2;
    public static final int STATE_FLUSHING = 3;
    public static final int STATE_END = 4;
    private static final byte[] NO_BYTES;
    private static int MAX_OUTPUT_CHARS;
    private static Buffer[] __reasons;
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
    protected boolean _close;
    protected Buffers _buffers;
    protected EndPoint _endp;
    protected int _headerBufferSize;
    protected int _contentBufferSize;
    protected Buffer _header;
    protected Buffer _buffer;
    protected Buffer _content;
    private boolean _sendServerVersion;
    
    protected static Buffer getReasonBuffer(final int code) {
        final Buffer reason = (code < AbstractGenerator.__reasons.length) ? AbstractGenerator.__reasons[code] : null;
        return (reason == null) ? null : reason;
    }
    
    public static String getReason(final int code) {
        final Buffer reason = (code < AbstractGenerator.__reasons.length) ? AbstractGenerator.__reasons[code] : null;
        return (reason == null) ? TypeUtil.toString(code) : reason.toString();
    }
    
    public AbstractGenerator(final Buffers buffers, final EndPoint io, final int headerBufferSize, final int contentBufferSize) {
        this._state = 0;
        this._status = 0;
        this._version = 11;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        this._last = false;
        this._head = false;
        this._noContent = false;
        this._close = false;
        this._buffers = buffers;
        this._endp = io;
        this._headerBufferSize = headerBufferSize;
        this._contentBufferSize = contentBufferSize;
    }
    
    public void reset(final boolean returnBuffers) {
        this._state = 0;
        this._status = 0;
        this._version = 11;
        this._reason = null;
        this._last = false;
        this._head = false;
        this._noContent = false;
        this._close = false;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        synchronized (this) {
            if (returnBuffers) {
                if (this._header != null) {
                    this._buffers.returnBuffer(this._header);
                }
                this._header = null;
                if (this._buffer != null) {
                    this._buffers.returnBuffer(this._buffer);
                }
                this._buffer = null;
            }
            else {
                if (this._header != null) {
                    this._header.clear();
                }
                if (this._buffer != null) {
                    this._buffers.returnBuffer(this._buffer);
                    this._buffer = null;
                }
            }
        }
        this._content = null;
        this._method = null;
    }
    
    public void resetBuffer() {
        if (this._state >= 3) {
            throw new IllegalStateException("Flushed");
        }
        this._last = false;
        this._close = false;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        this._content = null;
        if (this._buffer != null) {
            this._buffer.clear();
        }
    }
    
    public int getContentBufferSize() {
        return this._contentBufferSize;
    }
    
    public void increaseContentBufferSize(final int contentBufferSize) {
        if (contentBufferSize > this._contentBufferSize) {
            this._contentBufferSize = contentBufferSize;
            if (this._buffer != null) {
                final Buffer nb = this._buffers.getBuffer(this._contentBufferSize);
                nb.put(this._buffer);
                this._buffers.returnBuffer(this._buffer);
                this._buffer = nb;
            }
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
        return !this._close;
    }
    
    public void setPersistent(final boolean persistent) {
        this._close = !persistent;
    }
    
    public void setVersion(final int version) {
        if (this._state != 0) {
            throw new IllegalStateException("STATE!=START");
        }
        this._version = version;
        if (this._version == 9 && this._method != null) {
            this._noContent = true;
        }
    }
    
    public int getVersion() {
        return this._version;
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
        this._status = status;
        if (reason != null) {
            int len = reason.length();
            if (len > this._headerBufferSize / 2) {
                len = this._headerBufferSize / 2;
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
    
    protected abstract int prepareUncheckedAddContent() throws IOException;
    
    void uncheckedAddContent(final int b) {
        this._buffer.put((byte)b);
    }
    
    void completeUncheckedAddContent() {
        if (this._noContent) {
            if (this._buffer != null) {
                this._buffer.clear();
            }
            return;
        }
        this._contentWritten += this._buffer.length();
        if (this._head) {
            this._buffer.clear();
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
    
    public boolean isContentWritten() {
        return this._contentLength >= 0L && this._contentWritten >= this._contentLength;
    }
    
    public abstract void completeHeader(final HttpFields p0, final boolean p1) throws IOException;
    
    public void complete() throws IOException {
        if (this._state == 0) {
            throw new IllegalStateException("State==HEADER");
        }
        if (this._contentLength >= 0L && this._contentLength != this._contentWritten && !this._head) {
            if (Log.isDebugEnabled()) {
                Log.debug("ContentLength written==" + this._contentWritten + " != contentLength==" + this._contentLength);
            }
            this._close = true;
        }
    }
    
    public abstract long flush() throws IOException;
    
    public void sendError(final int code, final String reason, final String content, final boolean close) throws IOException {
        if (close) {
            this._close = close;
        }
        if (!this.isCommitted()) {
            this.setResponse(code, reason);
            this.completeHeader(null, false);
            if (content != null) {
                this.addContent(new View(new ByteArrayBuffer(content)), true);
            }
            this.complete();
        }
    }
    
    public long getContentWritten() {
        return this._contentWritten;
    }
    
    static {
        NO_BYTES = new byte[0];
        AbstractGenerator.MAX_OUTPUT_CHARS = 512;
        AbstractGenerator.__reasons = new Buffer[505];
        final Field[] fields = HttpServletResponse.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if ((fields[i].getModifiers() & 0x8) != 0x0 && fields[i].getName().startsWith("SC_")) {
                try {
                    final int code = fields[i].getInt(null);
                    if (code < AbstractGenerator.__reasons.length) {
                        AbstractGenerator.__reasons[code] = new ByteArrayBuffer(fields[i].getName().substring(3));
                    }
                }
                catch (IllegalAccessException ex) {}
            }
        }
    }
    
    public static class Output extends ServletOutputStream
    {
        protected AbstractGenerator _generator;
        protected long _maxIdleTime;
        protected ByteArrayBuffer _buf;
        protected boolean _closed;
        String _characterEncoding;
        Writer _converter;
        char[] _chars;
        ByteArrayOutputStream2 _bytes;
        
        public Output(final AbstractGenerator generator, final long maxIdleTime) {
            this._buf = new ByteArrayBuffer(AbstractGenerator.NO_BYTES);
            this._generator = generator;
            this._maxIdleTime = maxIdleTime;
        }
        
        public void close() throws IOException {
            this._closed = true;
        }
        
        void blockForOutput() throws IOException {
            if (this._generator._endp.isBlocking()) {
                try {
                    this.flush();
                    return;
                }
                catch (IOException e) {
                    this._generator._endp.close();
                    throw e;
                }
            }
            if (!this._generator._endp.blockWritable(this._maxIdleTime)) {
                this._generator._endp.close();
                throw new EofException("timeout");
            }
            this._generator.flush();
        }
        
        void reopen() {
            this._closed = false;
        }
        
        public void flush() throws IOException {
            final Buffer content = this._generator._content;
            final Buffer buffer = this._generator._buffer;
            if ((content != null && content.length() > 0) || (buffer != null && buffer.length() > 0) || this._generator.isBufferFull()) {
                this._generator.flush();
                while (((content != null && content.length() > 0) || (buffer != null && buffer.length() > 0)) && this._generator._endp.isOpen()) {
                    this.blockForOutput();
                }
            }
        }
        
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this._buf.wrap(b, off, len);
            this.write(this._buf);
            this._buf.wrap(AbstractGenerator.NO_BYTES);
        }
        
        public void write(final byte[] b) throws IOException {
            this._buf.wrap(b);
            this.write(this._buf);
            this._buf.wrap(AbstractGenerator.NO_BYTES);
        }
        
        public void write(final int b) throws IOException {
            if (this._closed) {
                throw new IOException("Closed");
            }
            if (!this._generator._endp.isOpen()) {
                throw new EofException();
            }
            while (this._generator.isBufferFull()) {
                this.blockForOutput();
                if (this._closed) {
                    throw new IOException("Closed");
                }
                if (!this._generator._endp.isOpen()) {
                    throw new EofException();
                }
            }
            if (this._generator.addContent((byte)b)) {
                this.flush();
            }
            if (this._generator.isContentWritten()) {
                this.flush();
                this.close();
            }
        }
        
        private void write(final Buffer buffer) throws IOException {
            if (this._closed) {
                throw new IOException("Closed");
            }
            if (!this._generator._endp.isOpen()) {
                throw new EofException();
            }
            while (this._generator.isBufferFull()) {
                this.blockForOutput();
                if (this._closed) {
                    throw new IOException("Closed");
                }
                if (!this._generator._endp.isOpen()) {
                    throw new EofException();
                }
            }
            this._generator.addContent(buffer, false);
            if (this._generator.isBufferFull()) {
                this.flush();
            }
            if (this._generator.isContentWritten()) {
                this.flush();
                this.close();
            }
            while (buffer.length() > 0 && this._generator._endp.isOpen()) {
                this.blockForOutput();
            }
        }
        
        public void print(final String s) throws IOException {
            this.write(s.getBytes());
        }
    }
    
    public static class OutputWriter extends Writer
    {
        private static final int WRITE_CONV = 0;
        private static final int WRITE_ISO1 = 1;
        private static final int WRITE_UTF8 = 2;
        Output _out;
        AbstractGenerator _generator;
        int _writeMode;
        int _surrogate;
        
        public OutputWriter(final Output out) {
            this._out = out;
            this._generator = this._out._generator;
        }
        
        public void setCharacterEncoding(final String encoding) {
            if (encoding == null || StringUtil.__ISO_8859_1.equalsIgnoreCase(encoding)) {
                this._writeMode = 1;
            }
            else if ("UTF-8".equalsIgnoreCase(encoding)) {
                this._writeMode = 2;
            }
            else {
                this._writeMode = 0;
                if (this._out._characterEncoding == null || !this._out._characterEncoding.equalsIgnoreCase(encoding)) {
                    this._out._converter = null;
                }
            }
            this._out._characterEncoding = encoding;
            if (this._out._bytes == null) {
                this._out._bytes = new ByteArrayOutputStream2(AbstractGenerator.MAX_OUTPUT_CHARS);
            }
        }
        
        public void close() throws IOException {
            this._out.close();
        }
        
        public void flush() throws IOException {
            this._out.flush();
        }
        
        public void write(final String s, int offset, int length) throws IOException {
            while (length > AbstractGenerator.MAX_OUTPUT_CHARS) {
                this.write(s, offset, AbstractGenerator.MAX_OUTPUT_CHARS);
                offset += AbstractGenerator.MAX_OUTPUT_CHARS;
                length -= AbstractGenerator.MAX_OUTPUT_CHARS;
            }
            if (this._out._chars == null) {
                this._out._chars = new char[AbstractGenerator.MAX_OUTPUT_CHARS];
            }
            final char[] chars = this._out._chars;
            s.getChars(offset, offset + length, chars, 0);
            this.write(chars, 0, length);
        }
        
        public void write(final char[] s, int offset, int length) throws IOException {
            final Output out = this._out;
            while (length > 0) {
                out._bytes.reset();
                int chars = (length > AbstractGenerator.MAX_OUTPUT_CHARS) ? AbstractGenerator.MAX_OUTPUT_CHARS : length;
                switch (this._writeMode) {
                    case 0: {
                        final Writer converter = this.getConverter();
                        converter.write(s, offset, chars);
                        converter.flush();
                        break;
                    }
                    case 1: {
                        final byte[] buffer = out._bytes.getBuf();
                        int bytes = out._bytes.getCount();
                        if (chars > buffer.length - bytes) {
                            chars = buffer.length - bytes;
                        }
                        for (int i = 0; i < chars; ++i) {
                            final int c = s[offset + i];
                            buffer[bytes++] = (byte)((c < 256) ? c : 63);
                        }
                        if (bytes >= 0) {
                            out._bytes.setCount(bytes);
                            break;
                        }
                        break;
                    }
                    case 2: {
                        final byte[] buffer = out._bytes.getBuf();
                        int bytes = out._bytes.getCount();
                        if (bytes + chars > buffer.length) {
                            chars = buffer.length - bytes;
                        }
                        for (int i = 0; i < chars; ++i) {
                            final int code = s[offset + i];
                            if ((code & 0xFFFFFF80) == 0x0) {
                                if (bytes + 1 > buffer.length) {
                                    chars = i;
                                    break;
                                }
                                buffer[bytes++] = (byte)code;
                            }
                            else {
                                if ((code & 0xFFFFF800) == 0x0) {
                                    if (bytes + 2 > buffer.length) {
                                        chars = i;
                                        break;
                                    }
                                    buffer[bytes++] = (byte)(0xC0 | code >> 6);
                                    buffer[bytes++] = (byte)(0x80 | (code & 0x3F));
                                }
                                else if ((code & 0xFFFF0000) == 0x0) {
                                    if (bytes + 3 > buffer.length) {
                                        chars = i;
                                        break;
                                    }
                                    buffer[bytes++] = (byte)(0xE0 | code >> 12);
                                    buffer[bytes++] = (byte)(0x80 | (code >> 6 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code & 0x3F));
                                }
                                else if ((code & 0xFF200000) == 0x0) {
                                    if (bytes + 4 > buffer.length) {
                                        chars = i;
                                        break;
                                    }
                                    buffer[bytes++] = (byte)(0xF0 | code >> 18);
                                    buffer[bytes++] = (byte)(0x80 | (code >> 12 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code >> 6 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code & 0x3F));
                                }
                                else if ((code & 0xF4000000) == 0x0) {
                                    if (bytes + 5 > buffer.length) {
                                        chars = i;
                                        break;
                                    }
                                    buffer[bytes++] = (byte)(0xF8 | code >> 24);
                                    buffer[bytes++] = (byte)(0x80 | (code >> 18 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code >> 12 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code >> 6 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code & 0x3F));
                                }
                                else if ((code & Integer.MIN_VALUE) == 0x0) {
                                    if (bytes + 6 > buffer.length) {
                                        chars = i;
                                        break;
                                    }
                                    buffer[bytes++] = (byte)(0xFC | code >> 30);
                                    buffer[bytes++] = (byte)(0x80 | (code >> 24 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code >> 18 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code >> 12 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code >> 6 & 0x3F));
                                    buffer[bytes++] = (byte)(0x80 | (code & 0x3F));
                                }
                                else {
                                    buffer[bytes++] = 63;
                                }
                                if (bytes == buffer.length) {
                                    chars = i + 1;
                                    break;
                                }
                            }
                        }
                        out._bytes.setCount(bytes);
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                out._bytes.writeTo(out);
                length -= chars;
                offset += chars;
            }
        }
        
        private Writer getConverter() throws IOException {
            if (this._out._converter == null) {
                this._out._converter = new OutputStreamWriter(this._out._bytes, this._out._characterEncoding);
            }
            return this._out._converter;
        }
    }
}
