// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.io.IOException;
import java.nio.BufferOverflowException;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.StringUtil;
import java.util.Set;
import org.eclipse.jetty.util.log.Logger;

public class HttpGenerator
{
    private static final Logger LOG;
    public static final boolean __STRICT;
    private static final byte[] __colon_space;
    private static final HttpHeaderValue[] CLOSE;
    public static final MetaData.Response CONTINUE_100_INFO;
    public static final MetaData.Response PROGRESS_102_INFO;
    public static final MetaData.Response RESPONSE_500_INFO;
    public static final int CHUNK_SIZE = 12;
    private State _state;
    private HttpTokens.EndOfContent _endOfContent;
    private long _contentPrepared;
    private boolean _noContent;
    private Boolean _persistent;
    private final int _send;
    private static final int SEND_SERVER = 1;
    private static final int SEND_XPOWEREDBY = 2;
    private static final Set<String> __assumedContentMethods;
    private boolean _needCRLF;
    private static final byte[] LAST_CHUNK;
    private static final byte[] CONTENT_LENGTH_0;
    private static final byte[] CONNECTION_KEEP_ALIVE;
    private static final byte[] CONNECTION_CLOSE;
    private static final byte[] HTTP_1_1_SPACE;
    private static final byte[] CRLF;
    private static final byte[] TRANSFER_ENCODING_CHUNKED;
    private static final byte[][] SEND;
    private static final PreparedResponse[] __preprepared;
    
    public static void setJettyVersion(final String serverVersion) {
        HttpGenerator.SEND[1] = StringUtil.getBytes("Server: " + serverVersion + "\r\n");
        HttpGenerator.SEND[2] = StringUtil.getBytes("X-Powered-By: " + serverVersion + "\r\n");
        HttpGenerator.SEND[3] = StringUtil.getBytes("Server: " + serverVersion + "\r\nX-Powered-By: " + serverVersion + "\r\n");
    }
    
    public HttpGenerator() {
        this(false, false);
    }
    
    public HttpGenerator(final boolean sendServerVersion, final boolean sendXPoweredBy) {
        this._state = State.START;
        this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
        this._contentPrepared = 0L;
        this._noContent = false;
        this._persistent = null;
        this._needCRLF = false;
        this._send = ((sendServerVersion ? 1 : 0) | (sendXPoweredBy ? 2 : 0));
    }
    
    public void reset() {
        this._state = State.START;
        this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
        this._noContent = false;
        this._persistent = null;
        this._contentPrepared = 0L;
        this._needCRLF = false;
    }
    
    @Deprecated
    public boolean getSendServerVersion() {
        return (this._send & 0x1) != 0x0;
    }
    
    @Deprecated
    public void setSendServerVersion(final boolean sendServerVersion) {
        throw new UnsupportedOperationException();
    }
    
    public State getState() {
        return this._state;
    }
    
    public boolean isState(final State state) {
        return this._state == state;
    }
    
    public boolean isIdle() {
        return this._state == State.START;
    }
    
    public boolean isEnd() {
        return this._state == State.END;
    }
    
    public boolean isCommitted() {
        return this._state.ordinal() >= State.COMMITTED.ordinal();
    }
    
    public boolean isChunking() {
        return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
    }
    
    public boolean isNoContent() {
        return this._noContent;
    }
    
    public void setPersistent(final boolean persistent) {
        this._persistent = persistent;
    }
    
    public boolean isPersistent() {
        return Boolean.TRUE.equals(this._persistent);
    }
    
    public boolean isWritten() {
        return this._contentPrepared > 0L;
    }
    
    public long getContentPrepared() {
        return this._contentPrepared;
    }
    
    public void abort() {
        this._persistent = false;
        this._state = State.END;
        this._endOfContent = null;
    }
    
    public Result generateRequest(final MetaData.Request info, final ByteBuffer header, final ByteBuffer chunk, final ByteBuffer content, final boolean last) throws IOException {
        switch (this._state) {
            case START: {
                if (info == null) {
                    return Result.NEED_INFO;
                }
                if (header == null) {
                    return Result.NEED_HEADER;
                }
                if (this._persistent == null) {
                    this._persistent = (info.getHttpVersion().ordinal() > HttpVersion.HTTP_1_0.ordinal());
                    if (!this._persistent && HttpMethod.CONNECT.is(info.getMethod())) {
                        this._persistent = true;
                    }
                }
                final int pos = BufferUtil.flipToFill(header);
                try {
                    this.generateRequestLine(info, header);
                    if (info.getHttpVersion() == HttpVersion.HTTP_0_9) {
                        throw new BadMessageException(500, "HTTP/0.9 not supported");
                    }
                    this.generateHeaders(info, header, content, last);
                    final boolean expect100 = info.getFields().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
                    if (expect100) {
                        this._state = State.COMMITTED;
                    }
                    else {
                        final int len = BufferUtil.length(content);
                        if (len > 0) {
                            this._contentPrepared += len;
                            if (this.isChunking()) {
                                this.prepareChunk(header, len);
                            }
                        }
                        this._state = (last ? State.COMPLETING : State.COMMITTED);
                    }
                    return Result.FLUSH;
                }
                catch (Exception e) {
                    final String message = (e instanceof BufferOverflowException) ? "Request header too large" : e.getMessage();
                    throw new BadMessageException(500, message, e);
                }
                finally {
                    BufferUtil.flipToFlush(header, pos);
                }
            }
            case COMMITTED: {
                final int len2 = BufferUtil.length(content);
                if (len2 > 0) {
                    if (this.isChunking()) {
                        if (chunk == null) {
                            return Result.NEED_CHUNK;
                        }
                        BufferUtil.clearToFill(chunk);
                        this.prepareChunk(chunk, len2);
                        BufferUtil.flipToFlush(chunk, 0);
                    }
                    this._contentPrepared += len2;
                }
                if (last) {
                    this._state = State.COMPLETING;
                }
                return (len2 > 0) ? Result.FLUSH : Result.CONTINUE;
            }
            case COMPLETING: {
                if (BufferUtil.hasContent(content)) {
                    if (HttpGenerator.LOG.isDebugEnabled()) {
                        HttpGenerator.LOG.debug("discarding content in COMPLETING", new Object[0]);
                    }
                    BufferUtil.clear(content);
                }
                if (!this.isChunking()) {
                    this._state = State.END;
                    return Boolean.TRUE.equals(this._persistent) ? Result.DONE : Result.SHUTDOWN_OUT;
                }
                if (chunk == null) {
                    return Result.NEED_CHUNK;
                }
                BufferUtil.clearToFill(chunk);
                this.prepareChunk(chunk, 0);
                BufferUtil.flipToFlush(chunk, 0);
                this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
                return Result.FLUSH;
            }
            case END: {
                if (BufferUtil.hasContent(content)) {
                    if (HttpGenerator.LOG.isDebugEnabled()) {
                        HttpGenerator.LOG.debug("discarding content in COMPLETING", new Object[0]);
                    }
                    BufferUtil.clear(content);
                }
                return Result.DONE;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    public Result generateResponse(final MetaData.Response info, final ByteBuffer header, final ByteBuffer chunk, final ByteBuffer content, final boolean last) throws IOException {
        return this.generateResponse(info, false, header, chunk, content, last);
    }
    
    public Result generateResponse(final MetaData.Response info, final boolean head, final ByteBuffer header, final ByteBuffer chunk, final ByteBuffer content, final boolean last) throws IOException {
        switch (this._state) {
            case START: {
                if (info == null) {
                    return Result.NEED_INFO;
                }
                final HttpVersion version = info.getHttpVersion();
                if (version == null) {
                    throw new BadMessageException(500, "No version");
                }
                switch (version) {
                    case HTTP_1_0: {
                        if (this._persistent == null) {
                            this._persistent = Boolean.FALSE;
                            break;
                        }
                        break;
                    }
                    case HTTP_1_1: {
                        if (this._persistent == null) {
                            this._persistent = Boolean.TRUE;
                            break;
                        }
                        break;
                    }
                    default: {
                        this._persistent = false;
                        this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                        if (BufferUtil.hasContent(content)) {
                            this._contentPrepared += content.remaining();
                        }
                        this._state = (last ? State.COMPLETING : State.COMMITTED);
                        return Result.FLUSH;
                    }
                }
                if (header == null) {
                    return Result.NEED_HEADER;
                }
                final int pos = BufferUtil.flipToFill(header);
                try {
                    this.generateResponseLine(info, header);
                    final int status = info.getStatus();
                    if (status >= 100 && status < 200) {
                        this._noContent = true;
                        if (status != 101) {
                            header.put(HttpTokens.CRLF);
                            this._state = State.COMPLETING_1XX;
                            return Result.FLUSH;
                        }
                    }
                    else if (status == 204 || status == 304) {
                        this._noContent = true;
                    }
                    this.generateHeaders(info, header, content, last);
                    final int len = BufferUtil.length(content);
                    if (len > 0) {
                        this._contentPrepared += len;
                        if (this.isChunking() && !head) {
                            this.prepareChunk(header, len);
                        }
                    }
                    this._state = (last ? State.COMPLETING : State.COMMITTED);
                }
                catch (Exception e) {
                    final String message = (e instanceof BufferOverflowException) ? "Response header too large" : e.getMessage();
                    throw new BadMessageException(500, message, e);
                }
                finally {
                    BufferUtil.flipToFlush(header, pos);
                }
                return Result.FLUSH;
            }
            case COMMITTED: {
                final int len2 = BufferUtil.length(content);
                if (len2 > 0) {
                    if (this.isChunking()) {
                        if (chunk == null) {
                            return Result.NEED_CHUNK;
                        }
                        BufferUtil.clearToFill(chunk);
                        this.prepareChunk(chunk, len2);
                        BufferUtil.flipToFlush(chunk, 0);
                    }
                    this._contentPrepared += len2;
                }
                if (last) {
                    this._state = State.COMPLETING;
                    return (len2 > 0) ? Result.FLUSH : Result.CONTINUE;
                }
                return (len2 > 0) ? Result.FLUSH : Result.DONE;
            }
            case COMPLETING_1XX: {
                this.reset();
                return Result.DONE;
            }
            case COMPLETING: {
                if (BufferUtil.hasContent(content)) {
                    if (HttpGenerator.LOG.isDebugEnabled()) {
                        HttpGenerator.LOG.debug("discarding content in COMPLETING", new Object[0]);
                    }
                    BufferUtil.clear(content);
                }
                if (!this.isChunking()) {
                    this._state = State.END;
                    return Boolean.TRUE.equals(this._persistent) ? Result.DONE : Result.SHUTDOWN_OUT;
                }
                if (chunk == null) {
                    return Result.NEED_CHUNK;
                }
                BufferUtil.clearToFill(chunk);
                this.prepareChunk(chunk, 0);
                BufferUtil.flipToFlush(chunk, 0);
                this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
                return Result.FLUSH;
            }
            case END: {
                if (BufferUtil.hasContent(content)) {
                    if (HttpGenerator.LOG.isDebugEnabled()) {
                        HttpGenerator.LOG.debug("discarding content in COMPLETING", new Object[0]);
                    }
                    BufferUtil.clear(content);
                }
                return Result.DONE;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    private void prepareChunk(final ByteBuffer chunk, final int remaining) {
        if (this._needCRLF) {
            BufferUtil.putCRLF(chunk);
        }
        if (remaining > 0) {
            BufferUtil.putHexInt(chunk, remaining);
            BufferUtil.putCRLF(chunk);
            this._needCRLF = true;
        }
        else {
            chunk.put(HttpGenerator.LAST_CHUNK);
            this._needCRLF = false;
        }
    }
    
    private void generateRequestLine(final MetaData.Request request, final ByteBuffer header) {
        header.put(StringUtil.getBytes(request.getMethod()));
        header.put((byte)32);
        header.put(StringUtil.getBytes(request.getURIString()));
        header.put((byte)32);
        header.put(request.getHttpVersion().toBytes());
        header.put(HttpTokens.CRLF);
    }
    
    private void generateResponseLine(final MetaData.Response response, final ByteBuffer header) {
        final int status = response.getStatus();
        final PreparedResponse preprepared = (status < HttpGenerator.__preprepared.length) ? HttpGenerator.__preprepared[status] : null;
        final String reason = response.getReason();
        if (preprepared != null) {
            if (reason == null) {
                header.put(preprepared._responseLine);
            }
            else {
                header.put(preprepared._schemeCode);
                header.put(this.getReasonBytes(reason));
                header.put(HttpTokens.CRLF);
            }
        }
        else {
            header.put(HttpGenerator.HTTP_1_1_SPACE);
            header.put((byte)(48 + status / 100));
            header.put((byte)(48 + status % 100 / 10));
            header.put((byte)(48 + status % 10));
            header.put((byte)32);
            if (reason == null) {
                header.put((byte)(48 + status / 100));
                header.put((byte)(48 + status % 100 / 10));
                header.put((byte)(48 + status % 10));
            }
            else {
                header.put(this.getReasonBytes(reason));
            }
            header.put(HttpTokens.CRLF);
        }
    }
    
    private byte[] getReasonBytes(String reason) {
        if (reason.length() > 1024) {
            reason = reason.substring(0, 1024);
        }
        final byte[] _bytes = StringUtil.getBytes(reason);
        int i = _bytes.length;
        while (i-- > 0) {
            if (_bytes[i] == 13 || _bytes[i] == 10) {
                _bytes[i] = 63;
            }
        }
        return _bytes;
    }
    
    private void generateHeaders(final MetaData _info, final ByteBuffer header, final ByteBuffer content, final boolean last) {
        final MetaData.Request request = (_info instanceof MetaData.Request) ? ((MetaData.Request)_info) : null;
        final MetaData.Response response = (_info instanceof MetaData.Response) ? ((MetaData.Response)_info) : null;
        int send = this._send;
        HttpField transfer_encoding = null;
        boolean keep_alive = false;
        boolean close = false;
        boolean content_type = false;
        StringBuilder connection = null;
        long content_length = _info.getContentLength();
        final HttpFields fields = _info.getFields();
        if (fields != null) {
            for (int n = fields.size(), f = 0; f < n; ++f) {
                final HttpField field = fields.getField(f);
                final HttpHeader h = field.getHeader();
                if (h == null) {
                    putTo(field, header);
                }
                else {
                    switch (h) {
                        case CONTENT_LENGTH: {
                            this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
                            if (content_length < 0L) {
                                content_length = Long.valueOf(field.getValue());
                                break;
                            }
                            break;
                        }
                        case CONTENT_TYPE: {
                            content_type = true;
                            putTo(field, header);
                            break;
                        }
                        case TRANSFER_ENCODING: {
                            if (_info.getHttpVersion() == HttpVersion.HTTP_1_1) {
                                transfer_encoding = field;
                                break;
                            }
                            break;
                        }
                        case CONNECTION: {
                            if (request != null) {
                                putTo(field, header);
                            }
                            HttpHeaderValue[] values = HttpHeaderValue.CLOSE.is(field.getValue()) ? HttpGenerator.CLOSE : new HttpHeaderValue[] { HttpHeaderValue.CACHE.get(field.getValue()) };
                            String[] split = null;
                            if (values[0] == null) {
                                split = StringUtil.csvSplit(field.getValue());
                                if (split.length > 0) {
                                    values = new HttpHeaderValue[split.length];
                                    for (int i = 0; i < split.length; ++i) {
                                        values[i] = HttpHeaderValue.CACHE.get(split[i]);
                                    }
                                }
                            }
                            for (int i = 0; i < values.length; ++i) {
                                final HttpHeaderValue value = values[i];
                                switch ((value == null) ? HttpHeaderValue.UNKNOWN : value) {
                                    case UPGRADE: {
                                        header.put(HttpHeader.CONNECTION.getBytesColonSpace()).put(HttpHeader.UPGRADE.getBytes());
                                        header.put(HttpGenerator.CRLF);
                                        break;
                                    }
                                    case CLOSE: {
                                        close = true;
                                        this._persistent = false;
                                        if (response != null && this._endOfContent == HttpTokens.EndOfContent.UNKNOWN_CONTENT) {
                                            this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                                            break;
                                        }
                                        break;
                                    }
                                    case KEEP_ALIVE: {
                                        if (_info.getHttpVersion() != HttpVersion.HTTP_1_0) {
                                            break;
                                        }
                                        keep_alive = true;
                                        if (response != null) {
                                            this._persistent = true;
                                            break;
                                        }
                                        break;
                                    }
                                    default: {
                                        if (connection == null) {
                                            connection = new StringBuilder();
                                        }
                                        else {
                                            connection.append(',');
                                        }
                                        connection.append((split == null) ? field.getValue() : split[i]);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                        case SERVER: {
                            send &= 0xFFFFFFFE;
                            putTo(field, header);
                            break;
                        }
                        default: {
                            putTo(field, header);
                            break;
                        }
                    }
                }
            }
        }
        final int status = (response != null) ? response.getStatus() : -1;
        switch (this._endOfContent) {
            case UNKNOWN_CONTENT: {
                if (this._contentPrepared == 0L && response != null && this._noContent) {
                    this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                    break;
                }
                if (_info.getContentLength() > 0L) {
                    this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
                    if ((response != null || content_length > 0L || content_type) && !this._noContent) {
                        header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
                        BufferUtil.putDecLong(header, content_length);
                        header.put(HttpTokens.CRLF);
                        break;
                    }
                    break;
                }
                else if (last) {
                    this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
                    final long actual_length = this._contentPrepared + BufferUtil.length(content);
                    if (content_length >= 0L && content_length != actual_length) {
                        throw new BadMessageException(500, "Content-Length header(" + content_length + ") != actual(" + actual_length + ")");
                    }
                    this.putContentLength(header, actual_length, content_type, request, response);
                    break;
                }
                else {
                    this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
                    if (!this.isPersistent() || _info.getHttpVersion().ordinal() < HttpVersion.HTTP_1_1.ordinal()) {
                        this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                        break;
                    }
                    break;
                }
                break;
            }
            case CONTENT_LENGTH: {
                this.putContentLength(header, content_length, content_type, request, response);
                break;
            }
            case NO_CONTENT: {
                throw new BadMessageException(500);
            }
            case EOF_CONTENT: {
                this._persistent = (request != null);
            }
        }
        if (this.isChunking()) {
            if (transfer_encoding != null && !HttpHeaderValue.CHUNKED.toString().equalsIgnoreCase(transfer_encoding.getValue())) {
                final String c = transfer_encoding.getValue();
                if (!c.endsWith(HttpHeaderValue.CHUNKED.toString())) {
                    throw new BadMessageException(500, "BAD TE");
                }
                putTo(transfer_encoding, header);
            }
            else {
                header.put(HttpGenerator.TRANSFER_ENCODING_CHUNKED);
            }
        }
        if (this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT) {
            keep_alive = false;
            this._persistent = false;
        }
        if (response != null) {
            if (!this.isPersistent() && (close || _info.getHttpVersion().ordinal() > HttpVersion.HTTP_1_0.ordinal())) {
                if (connection == null) {
                    header.put(HttpGenerator.CONNECTION_CLOSE);
                }
                else {
                    header.put(HttpGenerator.CONNECTION_CLOSE, 0, HttpGenerator.CONNECTION_CLOSE.length - 2);
                    header.put((byte)44);
                    header.put(StringUtil.getBytes(connection.toString()));
                    header.put(HttpGenerator.CRLF);
                }
            }
            else if (keep_alive) {
                if (connection == null) {
                    header.put(HttpGenerator.CONNECTION_KEEP_ALIVE);
                }
                else {
                    header.put(HttpGenerator.CONNECTION_KEEP_ALIVE, 0, HttpGenerator.CONNECTION_KEEP_ALIVE.length - 2);
                    header.put((byte)44);
                    header.put(StringUtil.getBytes(connection.toString()));
                    header.put(HttpGenerator.CRLF);
                }
            }
            else if (connection != null) {
                header.put(HttpHeader.CONNECTION.getBytesColonSpace());
                header.put(StringUtil.getBytes(connection.toString()));
                header.put(HttpGenerator.CRLF);
            }
        }
        if (status > 199) {
            header.put(HttpGenerator.SEND[send]);
        }
        header.put(HttpTokens.CRLF);
    }
    
    private void putContentLength(final ByteBuffer header, final long contentLength, final boolean contentType, final MetaData.Request request, final MetaData.Response response) {
        if (contentLength > 0L) {
            header.put(HttpHeader.CONTENT_LENGTH.getBytesColonSpace());
            BufferUtil.putDecLong(header, contentLength);
            header.put(HttpTokens.CRLF);
        }
        else if (!this._noContent && (contentType || response != null || (request != null && HttpGenerator.__assumedContentMethods.contains(request.getMethod())))) {
            header.put(HttpGenerator.CONTENT_LENGTH_0);
        }
    }
    
    public static byte[] getReasonBuffer(final int code) {
        final PreparedResponse status = (code < HttpGenerator.__preprepared.length) ? HttpGenerator.__preprepared[code] : null;
        if (status != null) {
            return status._reason;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{s=%s}", this.getClass().getSimpleName(), this.hashCode(), this._state);
    }
    
    private static void putSanitisedName(final String s, final ByteBuffer buffer) {
        for (int l = s.length(), i = 0; i < l; ++i) {
            final char c = s.charAt(i);
            if (c < '\0' || c > '\u00ff' || c == '\r' || c == '\n' || c == ':') {
                buffer.put((byte)63);
            }
            else {
                buffer.put((byte)('\u00ff' & c));
            }
        }
    }
    
    private static void putSanitisedValue(final String s, final ByteBuffer buffer) {
        for (int l = s.length(), i = 0; i < l; ++i) {
            final char c = s.charAt(i);
            if (c < '\0' || c > '\u00ff' || c == '\r' || c == '\n') {
                buffer.put((byte)32);
            }
            else {
                buffer.put((byte)('\u00ff' & c));
            }
        }
    }
    
    public static void putTo(final HttpField field, final ByteBuffer bufferInFillMode) {
        if (field instanceof PreEncodedHttpField) {
            ((PreEncodedHttpField)field).putTo(bufferInFillMode, HttpVersion.HTTP_1_0);
        }
        else {
            final HttpHeader header = field.getHeader();
            if (header != null) {
                bufferInFillMode.put(header.getBytesColonSpace());
                putSanitisedValue(field.getValue(), bufferInFillMode);
            }
            else {
                putSanitisedName(field.getName(), bufferInFillMode);
                bufferInFillMode.put(HttpGenerator.__colon_space);
                putSanitisedValue(field.getValue(), bufferInFillMode);
            }
            BufferUtil.putCRLF(bufferInFillMode);
        }
    }
    
    public static void putTo(final HttpFields fields, final ByteBuffer bufferInFillMode) {
        for (final HttpField field : fields) {
            if (field != null) {
                putTo(field, bufferInFillMode);
            }
        }
        BufferUtil.putCRLF(bufferInFillMode);
    }
    
    static {
        LOG = Log.getLogger(HttpGenerator.class);
        __STRICT = Boolean.getBoolean("org.eclipse.jetty.http.HttpGenerator.STRICT");
        __colon_space = new byte[] { 58, 32 };
        CLOSE = new HttpHeaderValue[] { HttpHeaderValue.CLOSE };
        CONTINUE_100_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 100, null, null, -1L);
        PROGRESS_102_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 102, null, null, -1L);
        RESPONSE_500_INFO = new MetaData.Response(HttpVersion.HTTP_1_1, 500, null, new HttpFields() {
            {
                this.put(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE);
            }
        }, 0L);
        __assumedContentMethods = new HashSet<String>(Arrays.asList(HttpMethod.POST.asString(), HttpMethod.PUT.asString()));
        LAST_CHUNK = new byte[] { 48, 13, 10, 13, 10 };
        CONTENT_LENGTH_0 = StringUtil.getBytes("Content-Length: 0\r\n");
        CONNECTION_KEEP_ALIVE = StringUtil.getBytes("Connection: keep-alive\r\n");
        CONNECTION_CLOSE = StringUtil.getBytes("Connection: close\r\n");
        HTTP_1_1_SPACE = StringUtil.getBytes(HttpVersion.HTTP_1_1 + " ");
        CRLF = StringUtil.getBytes("\r\n");
        TRANSFER_ENCODING_CHUNKED = StringUtil.getBytes("Transfer-Encoding: chunked\r\n");
        SEND = new byte[][] { new byte[0], StringUtil.getBytes("Server: Jetty(9.x.x)\r\n"), StringUtil.getBytes("X-Powered-By: Jetty(9.x.x)\r\n"), StringUtil.getBytes("Server: Jetty(9.x.x)\r\nX-Powered-By: Jetty(9.x.x)\r\n") };
        __preprepared = new PreparedResponse[512];
        final int versionLength = HttpVersion.HTTP_1_1.toString().length();
        for (int i = 0; i < HttpGenerator.__preprepared.length; ++i) {
            final HttpStatus.Code code = HttpStatus.getCode(i);
            if (code != null) {
                final String reason = code.getMessage();
                final byte[] line = new byte[versionLength + 5 + reason.length() + 2];
                HttpVersion.HTTP_1_1.toBuffer().get(line, 0, versionLength);
                line[versionLength + 0] = 32;
                line[versionLength + 1] = (byte)(48 + i / 100);
                line[versionLength + 2] = (byte)(48 + i % 100 / 10);
                line[versionLength + 3] = (byte)(48 + i % 10);
                line[versionLength + 4] = 32;
                for (int j = 0; j < reason.length(); ++j) {
                    line[versionLength + 5 + j] = (byte)reason.charAt(j);
                }
                line[versionLength + 5 + reason.length()] = 13;
                line[versionLength + 6 + reason.length()] = 10;
                HttpGenerator.__preprepared[i] = new PreparedResponse();
                HttpGenerator.__preprepared[i]._schemeCode = Arrays.copyOfRange(line, 0, versionLength + 5);
                HttpGenerator.__preprepared[i]._reason = Arrays.copyOfRange(line, versionLength + 5, line.length - 2);
                HttpGenerator.__preprepared[i]._responseLine = line;
            }
        }
    }
    
    public enum State
    {
        START, 
        COMMITTED, 
        COMPLETING, 
        COMPLETING_1XX, 
        END;
    }
    
    public enum Result
    {
        NEED_CHUNK, 
        NEED_INFO, 
        NEED_HEADER, 
        FLUSH, 
        CONTINUE, 
        SHUTDOWN_OUT, 
        DONE;
    }
    
    private static class PreparedResponse
    {
        byte[] _reason;
        byte[] _schemeCode;
        byte[] _responseLine;
    }
}
