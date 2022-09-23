// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.io.BufferCache;
import java.util.Iterator;
import org.mortbay.util.QuotedStringTokenizer;
import org.mortbay.io.BufferUtil;
import java.io.IOException;
import org.mortbay.log.Log;
import org.mortbay.io.Buffer;
import org.mortbay.io.EndPoint;
import org.mortbay.io.Buffers;
import org.mortbay.io.Portable;

public class HttpGenerator extends AbstractGenerator
{
    private static byte[] LAST_CHUNK;
    private static byte[] CONTENT_LENGTH_0;
    private static byte[] CONNECTION_KEEP_ALIVE;
    private static byte[] CONNECTION_CLOSE;
    private static byte[] CONNECTION_;
    private static byte[] CRLF;
    private static byte[] TRANSFER_ENCODING_CHUNKED;
    private static byte[] SERVER;
    private static int CHUNK_SPACE;
    private boolean _bypass;
    private boolean _needCRLF;
    private boolean _needEOC;
    private boolean _bufferChunked;
    
    public static void setServerVersion(final String version) {
        HttpGenerator.SERVER = Portable.getBytes("Server: Jetty(" + version + ")\r\n");
    }
    
    public HttpGenerator(final Buffers buffers, final EndPoint io, final int headerBufferSize, final int contentBufferSize) {
        super(buffers, io, headerBufferSize, contentBufferSize);
        this._bypass = false;
        this._needCRLF = false;
        this._needEOC = false;
        this._bufferChunked = false;
    }
    
    public void reset(final boolean returnBuffers) {
        super.reset(returnBuffers);
        this._bypass = false;
        this._needCRLF = false;
        this._needEOC = false;
        this._bufferChunked = false;
        this._method = null;
        this._uri = null;
        this._noContent = false;
    }
    
    public void addContent(final Buffer content, final boolean last) throws IOException {
        if (this._noContent) {
            throw new IllegalStateException("NO CONTENT");
        }
        if (this._last || this._state == 4) {
            Log.debug("Ignoring extra content {}", content);
            content.clear();
            return;
        }
        this._last = last;
        if ((this._content != null && this._content.length() > 0) || this._bufferChunked) {
            if (!this._endp.isOpen()) {
                throw new EofException();
            }
            this.flush();
            if ((this._content != null && this._content.length() > 0) || this._bufferChunked) {
                throw new IllegalStateException("FULL");
            }
        }
        this._content = content;
        this._contentWritten += content.length();
        if (this._head) {
            content.clear();
            this._content = null;
        }
        else if (this._endp != null && this._buffer == null && content.length() > 0 && this._last) {
            this._bypass = true;
        }
        else {
            if (this._buffer == null) {
                this._buffer = this._buffers.getBuffer(this._contentBufferSize);
            }
            final int len = this._buffer.put(this._content);
            this._content.skip(len);
            if (this._content.length() == 0) {
                this._content = null;
            }
        }
    }
    
    public void sendResponse(final Buffer response) throws IOException {
        if (this._noContent || this._state != 0 || (this._content != null && this._content.length() > 0) || this._bufferChunked || this._head) {
            throw new IllegalStateException();
        }
        this._last = true;
        this._content = response;
        this._bypass = true;
        this._state = 3;
        final long n = response.length();
        this._contentWritten = n;
        this._contentLength = n;
    }
    
    public boolean addContent(final byte b) throws IOException {
        if (this._noContent) {
            throw new IllegalStateException("NO CONTENT");
        }
        if (this._last || this._state == 4) {
            Log.debug("Ignoring extra content {}", new Byte(b));
            return false;
        }
        if ((this._content != null && this._content.length() > 0) || this._bufferChunked) {
            this.flush();
            if ((this._content != null && this._content.length() > 0) || this._bufferChunked) {
                throw new IllegalStateException("FULL");
            }
        }
        ++this._contentWritten;
        if (this._head) {
            return false;
        }
        if (this._buffer == null) {
            this._buffer = this._buffers.getBuffer(this._contentBufferSize);
        }
        this._buffer.put(b);
        return this._buffer.space() <= ((this._contentLength == -2L) ? HttpGenerator.CHUNK_SPACE : 0);
    }
    
    protected int prepareUncheckedAddContent() throws IOException {
        if (this._noContent) {
            return -1;
        }
        if (this._last || this._state == 4) {
            return -1;
        }
        final Buffer content = this._content;
        if ((content != null && content.length() > 0) || this._bufferChunked) {
            this.flush();
            if ((content != null && content.length() > 0) || this._bufferChunked) {
                throw new IllegalStateException("FULL");
            }
        }
        if (this._buffer == null) {
            this._buffer = this._buffers.getBuffer(this._contentBufferSize);
        }
        this._contentWritten -= this._buffer.length();
        if (this._head) {
            return Integer.MAX_VALUE;
        }
        return this._buffer.space() - ((this._contentLength == -2L) ? HttpGenerator.CHUNK_SPACE : 0);
    }
    
    public boolean isBufferFull() {
        final boolean full = super.isBufferFull() || this._bufferChunked || this._bypass || (this._contentLength == -2L && this._buffer != null && this._buffer.space() < HttpGenerator.CHUNK_SPACE);
        return full;
    }
    
    public void completeHeader(final HttpFields fields, final boolean allContentAdded) throws IOException {
        if (this._state != 0) {
            return;
        }
        if (this._method == null && this._status == 0) {
            throw new EofException();
        }
        if (this._last && !allContentAdded) {
            throw new IllegalStateException("last?");
        }
        this._last |= allContentAdded;
        if (this._header == null) {
            this._header = this._buffers.getBuffer(this._headerBufferSize);
        }
        boolean has_server = false;
        if (this._method != null) {
            this._close = false;
            if (this._version == 9) {
                this._contentLength = 0L;
                this._header.put(this._method);
                this._header.put((byte)32);
                this._header.put(this._uri.getBytes("utf-8"));
                this._header.put(HttpTokens.CRLF);
                this._state = 3;
                this._noContent = true;
                return;
            }
            this._header.put(this._method);
            this._header.put((byte)32);
            this._header.put(this._uri.getBytes("utf-8"));
            this._header.put((byte)32);
            this._header.put((this._version == 10) ? HttpVersions.HTTP_1_0_BUFFER : HttpVersions.HTTP_1_1_BUFFER);
            this._header.put(HttpTokens.CRLF);
        }
        else {
            if (this._version == 9) {
                this._close = true;
                this._contentLength = -1L;
                this._state = 2;
                return;
            }
            if (this._version == 10) {
                this._close = true;
            }
            final Buffer line = HttpStatus.getResponseLine(this._status);
            if (line == null) {
                if (this._reason == null) {
                    this._reason = AbstractGenerator.getReasonBuffer(this._status);
                }
                this._header.put(HttpVersions.HTTP_1_1_BUFFER);
                this._header.put((byte)32);
                this._header.put((byte)(48 + this._status / 100));
                this._header.put((byte)(48 + this._status % 100 / 10));
                this._header.put((byte)(48 + this._status % 10));
                this._header.put((byte)32);
                if (this._reason == null) {
                    this._header.put((byte)(48 + this._status / 100));
                    this._header.put((byte)(48 + this._status % 100 / 10));
                    this._header.put((byte)(48 + this._status % 10));
                }
                else {
                    this._header.put(this._reason);
                }
                this._header.put(HttpTokens.CRLF);
            }
            else if (this._reason == null) {
                this._header.put(line);
            }
            else {
                this._header.put(line.array(), 0, HttpVersions.HTTP_1_1_BUFFER.length() + 5);
                this._header.put(this._reason);
                this._header.put(HttpTokens.CRLF);
            }
            if (this._status < 200 && this._status >= 100) {
                this._noContent = true;
                this._content = null;
                if (this._buffer != null) {
                    this._buffer.clear();
                }
                this._header.put(HttpTokens.CRLF);
                this._state = 2;
                return;
            }
            if (this._status == 204 || this._status == 304) {
                this._noContent = true;
                this._content = null;
                if (this._buffer != null) {
                    this._buffer.clear();
                }
            }
        }
        HttpFields.Field content_length = null;
        HttpFields.Field transfer_encoding = null;
        boolean keep_alive = false;
        boolean close = false;
        boolean content_type = false;
        StringBuffer connection = null;
        if (fields != null) {
            final Iterator iter = fields.getFields();
            while (iter.hasNext()) {
                final HttpFields.Field field = iter.next();
                switch (field.getNameOrdinal()) {
                    case 12: {
                        content_length = field;
                        this._contentLength = field.getLongValue();
                        if (this._contentLength < this._contentWritten || (this._last && this._contentLength != this._contentWritten)) {
                            content_length = null;
                        }
                        field.put(this._header);
                        continue;
                    }
                    case 16: {
                        if (BufferUtil.isPrefix(MimeTypes.MULTIPART_BYTERANGES_BUFFER, field.getValueBuffer())) {
                            this._contentLength = -4L;
                        }
                        content_type = true;
                        field.put(this._header);
                        continue;
                    }
                    case 5: {
                        if (this._version == 11) {
                            transfer_encoding = field;
                            continue;
                        }
                        continue;
                    }
                    case 1: {
                        if (this._method != null) {
                            field.put(this._header);
                        }
                        final int connection_value = field.getValueOrdinal();
                        switch (connection_value) {
                            case -1: {
                                final QuotedStringTokenizer tok = new QuotedStringTokenizer(field.getValue(), ",");
                                while (tok.hasMoreTokens()) {
                                    final String token = tok.nextToken().trim();
                                    final BufferCache.CachedBuffer cb = HttpHeaderValues.CACHE.get(token);
                                    if (cb != null) {
                                        switch (cb.getOrdinal()) {
                                            case 1: {
                                                close = true;
                                                if (this._method == null) {
                                                    this._close = true;
                                                }
                                                keep_alive = false;
                                                if (this._close && this._method == null && this._contentLength == -3L) {
                                                    this._contentLength = -1L;
                                                    continue;
                                                }
                                                continue;
                                            }
                                            case 5: {
                                                if (this._version != 10) {
                                                    continue;
                                                }
                                                keep_alive = true;
                                                if (this._method == null) {
                                                    this._close = false;
                                                    continue;
                                                }
                                                continue;
                                            }
                                            default: {
                                                if (connection == null) {
                                                    connection = new StringBuffer();
                                                }
                                                else {
                                                    connection.append(',');
                                                }
                                                connection.append(token);
                                                continue;
                                            }
                                        }
                                    }
                                    else {
                                        if (connection == null) {
                                            connection = new StringBuffer();
                                        }
                                        else {
                                            connection.append(',');
                                        }
                                        connection.append(token);
                                    }
                                }
                                continue;
                            }
                            case 1: {
                                close = true;
                                if (this._method == null) {
                                    this._close = true;
                                }
                                if (this._close && this._method == null && this._contentLength == -3L) {
                                    this._contentLength = -1L;
                                    continue;
                                }
                                continue;
                            }
                            case 5: {
                                if (this._version != 10) {
                                    continue;
                                }
                                keep_alive = true;
                                if (this._method == null) {
                                    this._close = false;
                                    continue;
                                }
                                continue;
                            }
                            default: {
                                if (connection == null) {
                                    connection = new StringBuffer();
                                }
                                else {
                                    connection.append(',');
                                }
                                connection.append(field.getValue());
                                continue;
                            }
                        }
                        break;
                    }
                    case 48: {
                        if (this.getSendServerVersion()) {
                            has_server = true;
                            field.put(this._header);
                            continue;
                        }
                        continue;
                    }
                    default: {
                        field.put(this._header);
                        continue;
                    }
                }
            }
        }
        switch ((int)this._contentLength) {
            case -3: {
                if (this._contentWritten == 0L && this._method == null && (this._status < 200 || this._status == 204 || this._status == 304)) {
                    this._contentLength = 0L;
                    break;
                }
                if (this._last) {
                    this._contentLength = this._contentWritten;
                    if (content_length == null && (this._method == null || content_type || this._contentLength > 0L)) {
                        this._header.put(HttpHeaders.CONTENT_LENGTH_BUFFER);
                        this._header.put((byte)58);
                        this._header.put((byte)32);
                        BufferUtil.putDecLong(this._header, this._contentLength);
                        this._header.put(HttpTokens.CRLF);
                        break;
                    }
                    break;
                }
                else {
                    this._contentLength = ((this._close || this._version < 11) ? -1L : -2L);
                    if (this._method != null && this._contentLength == -1L) {
                        this._contentLength = 0L;
                        this._noContent = true;
                        break;
                    }
                    break;
                }
                break;
            }
            case 0: {
                if (content_length == null && this._method == null && this._status >= 200 && this._status != 204 && this._status != 304) {
                    this._header.put(HttpGenerator.CONTENT_LENGTH_0);
                    break;
                }
                break;
            }
            case -1: {
                this._close = (this._method == null);
            }
        }
        if (this._contentLength == -2L) {
            if (transfer_encoding != null && 2 != transfer_encoding.getValueOrdinal()) {
                final String c = transfer_encoding.getValue();
                if (!c.endsWith("chunked")) {
                    throw new IllegalArgumentException("BAD TE");
                }
                transfer_encoding.put(this._header);
            }
            else {
                this._header.put(HttpGenerator.TRANSFER_ENCODING_CHUNKED);
            }
        }
        if (this._contentLength == -1L) {
            keep_alive = false;
            this._close = true;
        }
        if (this._method == null) {
            if (this._close && (close || this._version > 10)) {
                this._header.put(HttpGenerator.CONNECTION_CLOSE);
                if (connection != null) {
                    this._header.setPutIndex(this._header.putIndex() - 2);
                    this._header.put((byte)44);
                    this._header.put(connection.toString().getBytes());
                    this._header.put(HttpGenerator.CRLF);
                }
            }
            else if (keep_alive) {
                this._header.put(HttpGenerator.CONNECTION_KEEP_ALIVE);
                if (connection != null) {
                    this._header.setPutIndex(this._header.putIndex() - 2);
                    this._header.put((byte)44);
                    this._header.put(connection.toString().getBytes());
                    this._header.put(HttpGenerator.CRLF);
                }
            }
            else if (connection != null) {
                this._header.put(HttpGenerator.CONNECTION_);
                this._header.put(connection.toString().getBytes());
                this._header.put(HttpGenerator.CRLF);
            }
        }
        if (!has_server && this._status > 100 && this.getSendServerVersion()) {
            this._header.put(HttpGenerator.SERVER);
        }
        this._header.put(HttpTokens.CRLF);
        this._state = 2;
    }
    
    public void complete() throws IOException {
        if (this._state == 4) {
            return;
        }
        super.complete();
        if (this._state < 3) {
            this._state = 3;
            if (this._contentLength == -2L) {
                this._needEOC = true;
            }
        }
        this.flush();
    }
    
    public long flush() throws IOException {
        try {
            if (this._state == 0) {
                throw new IllegalStateException("State==HEADER");
            }
            this.prepareBuffers();
            if (this._endp == null) {
                if (this._needCRLF && this._buffer != null) {
                    this._buffer.put(HttpTokens.CRLF);
                }
                if (this._needEOC && this._buffer != null && !this._head) {
                    this._buffer.put(HttpGenerator.LAST_CHUNK);
                }
                this._needCRLF = false;
                this._needEOC = false;
                return 0L;
            }
            int total = 0;
            long last_len = -1L;
        Label_0603:
            while (true) {
                int len = -1;
                final int to_flush = ((this._header != null && this._header.length() > 0) ? 4 : 0) | ((this._buffer != null && this._buffer.length() > 0) ? 2 : 0) | ((this._bypass && this._content != null && this._content.length() > 0) ? 1 : 0);
                switch (to_flush) {
                    case 7: {
                        throw new IllegalStateException();
                    }
                    case 6: {
                        len = this._endp.flush(this._header, this._buffer, null);
                        break;
                    }
                    case 5: {
                        len = this._endp.flush(this._header, this._content, null);
                        break;
                    }
                    case 4: {
                        len = this._endp.flush(this._header);
                        break;
                    }
                    case 3: {
                        throw new IllegalStateException();
                    }
                    case 2: {
                        len = this._endp.flush(this._buffer);
                        break;
                    }
                    case 1: {
                        len = this._endp.flush(this._content);
                        break;
                    }
                    case 0: {
                        if (this._header != null) {
                            this._header.clear();
                        }
                        this._bypass = false;
                        this._bufferChunked = false;
                        if (this._buffer != null) {
                            this._buffer.clear();
                            if (this._contentLength == -2L) {
                                this._buffer.setPutIndex(HttpGenerator.CHUNK_SPACE);
                                this._buffer.setGetIndex(HttpGenerator.CHUNK_SPACE);
                                if (this._content != null && this._content.length() < this._buffer.space() && this._state != 3) {
                                    this._buffer.put(this._content);
                                    this._content.clear();
                                    this._content = null;
                                    break Label_0603;
                                }
                            }
                        }
                        if (this._needCRLF || this._needEOC || (this._content != null && this._content.length() != 0)) {
                            this.prepareBuffers();
                            break;
                        }
                        if (this._state == 3) {
                            this._state = 4;
                        }
                        if (this._state == 4 && this._close && this._status != 100) {
                            this._endp.shutdownOutput();
                            break Label_0603;
                        }
                        break Label_0603;
                    }
                }
                if (len <= 0) {
                    break;
                }
                total += len;
                last_len = len;
            }
            return total;
        }
        catch (IOException e) {
            Log.ignore(e);
            throw (e instanceof EofException) ? e : new EofException(e);
        }
    }
    
    private void prepareBuffers() {
        if (!this._bufferChunked) {
            if (this._content != null && this._content.length() > 0 && this._buffer != null && this._buffer.space() > 0) {
                final int len = this._buffer.put(this._content);
                this._content.skip(len);
                if (this._content.length() == 0) {
                    this._content = null;
                }
            }
            if (this._contentLength == -2L) {
                final int size = (this._buffer == null) ? 0 : this._buffer.length();
                if (size > 0) {
                    this._bufferChunked = true;
                    if (this._buffer.getIndex() == HttpGenerator.CHUNK_SPACE) {
                        this._buffer.poke(this._buffer.getIndex() - 2, HttpTokens.CRLF, 0, 2);
                        this._buffer.setGetIndex(this._buffer.getIndex() - 2);
                        BufferUtil.prependHexInt(this._buffer, size);
                        if (this._needCRLF) {
                            this._buffer.poke(this._buffer.getIndex() - 2, HttpTokens.CRLF, 0, 2);
                            this._buffer.setGetIndex(this._buffer.getIndex() - 2);
                            this._needCRLF = false;
                        }
                    }
                    else {
                        if (this._needCRLF) {
                            if (this._header.length() > 0) {
                                throw new IllegalStateException("EOC");
                            }
                            this._header.put(HttpTokens.CRLF);
                            this._needCRLF = false;
                        }
                        BufferUtil.putHexInt(this._header, size);
                        this._header.put(HttpTokens.CRLF);
                    }
                    if (this._buffer.space() >= 2) {
                        this._buffer.put(HttpTokens.CRLF);
                    }
                    else {
                        this._needCRLF = true;
                    }
                }
                if (this._needEOC && (this._content == null || this._content.length() == 0)) {
                    if (this._needCRLF) {
                        if (this._buffer == null && this._header.space() >= 2) {
                            this._header.put(HttpTokens.CRLF);
                            this._needCRLF = false;
                        }
                        else if (this._buffer != null && this._buffer.space() >= 2) {
                            this._buffer.put(HttpTokens.CRLF);
                            this._needCRLF = false;
                        }
                    }
                    if (!this._needCRLF && this._needEOC) {
                        if (this._buffer == null && this._header.space() >= HttpGenerator.LAST_CHUNK.length) {
                            if (!this._head) {
                                this._header.put(HttpGenerator.LAST_CHUNK);
                                this._bufferChunked = true;
                            }
                            this._needEOC = false;
                        }
                        else if (this._buffer != null && this._buffer.space() >= HttpGenerator.LAST_CHUNK.length) {
                            if (!this._head) {
                                this._buffer.put(HttpGenerator.LAST_CHUNK);
                                this._bufferChunked = true;
                            }
                            this._needEOC = false;
                        }
                    }
                }
            }
        }
        if (this._content != null && this._content.length() == 0) {
            this._content = null;
        }
    }
    
    public int getBytesBuffered() {
        return ((this._header == null) ? 0 : this._header.length()) + ((this._buffer == null) ? 0 : this._buffer.length()) + ((this._content == null) ? 0 : this._content.length());
    }
    
    public boolean isEmpty() {
        return (this._header == null || this._header.length() == 0) && (this._buffer == null || this._buffer.length() == 0) && (this._content == null || this._content.length() == 0);
    }
    
    public String toString() {
        return "HttpGenerator s=" + this._state + " h=" + ((this._header == null) ? "null" : ("" + this._header.length())) + " b=" + ((this._buffer == null) ? "null" : ("" + this._buffer.length())) + " c=" + ((this._content == null) ? "null" : ("" + this._content.length()));
    }
    
    static {
        HttpGenerator.LAST_CHUNK = new byte[] { 48, 13, 10, 13, 10 };
        HttpGenerator.CONTENT_LENGTH_0 = Portable.getBytes("Content-Length: 0\r\n");
        HttpGenerator.CONNECTION_KEEP_ALIVE = Portable.getBytes("Connection: keep-alive\r\n");
        HttpGenerator.CONNECTION_CLOSE = Portable.getBytes("Connection: close\r\n");
        HttpGenerator.CONNECTION_ = Portable.getBytes("Connection: ");
        HttpGenerator.CRLF = Portable.getBytes("\r\n");
        HttpGenerator.TRANSFER_ENCODING_CHUNKED = Portable.getBytes("Transfer-Encoding: chunked\r\n");
        HttpGenerator.SERVER = Portable.getBytes("Server: Jetty(6.0.x)\r\n");
        HttpGenerator.CHUNK_SPACE = 12;
    }
}
