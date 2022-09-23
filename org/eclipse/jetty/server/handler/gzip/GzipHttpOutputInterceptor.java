// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler.gzip;

import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.IteratingNestedCallback;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.http.GzipHttpContent;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.HttpHeader;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.util.Callback;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import org.eclipse.jetty.server.HttpChannel;
import java.util.zip.CRC32;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.HttpOutput;

public class GzipHttpOutputInterceptor implements HttpOutput.Interceptor
{
    public static Logger LOG;
    private static final byte[] GZIP_HEADER;
    public static final HttpField VARY_ACCEPT_ENCODING_USER_AGENT;
    public static final HttpField VARY_ACCEPT_ENCODING;
    private final AtomicReference<GZState> _state;
    private final CRC32 _crc;
    private final GzipFactory _factory;
    private final HttpOutput.Interceptor _interceptor;
    private final HttpChannel _channel;
    private final HttpField _vary;
    private final int _bufferSize;
    private final boolean _syncFlush;
    private Deflater _deflater;
    private ByteBuffer _buffer;
    
    public GzipHttpOutputInterceptor(final GzipFactory factory, final HttpChannel channel, final HttpOutput.Interceptor next, final boolean syncFlush) {
        this(factory, GzipHttpOutputInterceptor.VARY_ACCEPT_ENCODING_USER_AGENT, channel.getHttpConfiguration().getOutputBufferSize(), channel, next, syncFlush);
    }
    
    public GzipHttpOutputInterceptor(final GzipFactory factory, final HttpField vary, final HttpChannel channel, final HttpOutput.Interceptor next, final boolean syncFlush) {
        this(factory, vary, channel.getHttpConfiguration().getOutputBufferSize(), channel, next, syncFlush);
    }
    
    public GzipHttpOutputInterceptor(final GzipFactory factory, final HttpField vary, final int bufferSize, final HttpChannel channel, final HttpOutput.Interceptor next, final boolean syncFlush) {
        this._state = new AtomicReference<GZState>(GZState.MIGHT_COMPRESS);
        this._crc = new CRC32();
        this._factory = factory;
        this._channel = channel;
        this._interceptor = next;
        this._vary = vary;
        this._bufferSize = bufferSize;
        this._syncFlush = syncFlush;
    }
    
    @Override
    public HttpOutput.Interceptor getNextInterceptor() {
        return this._interceptor;
    }
    
    @Override
    public boolean isOptimizedForDirectBuffers() {
        return false;
    }
    
    @Override
    public void write(final ByteBuffer content, final boolean complete, final Callback callback) {
        switch (this._state.get()) {
            case MIGHT_COMPRESS: {
                this.commit(content, complete, callback);
                break;
            }
            case NOT_COMPRESSING: {
                this._interceptor.write(content, complete, callback);
            }
            case COMMITTING: {
                callback.failed(new WritePendingException());
                break;
            }
            case COMPRESSING: {
                this.gzip(content, complete, callback);
                break;
            }
            default: {
                callback.failed(new IllegalStateException("state=" + this._state.get()));
                break;
            }
        }
    }
    
    private void addTrailer() {
        int i = this._buffer.limit();
        this._buffer.limit(i + 8);
        int v = (int)this._crc.getValue();
        this._buffer.put(i++, (byte)(v & 0xFF));
        this._buffer.put(i++, (byte)(v >>> 8 & 0xFF));
        this._buffer.put(i++, (byte)(v >>> 16 & 0xFF));
        this._buffer.put(i++, (byte)(v >>> 24 & 0xFF));
        v = this._deflater.getTotalIn();
        this._buffer.put(i++, (byte)(v & 0xFF));
        this._buffer.put(i++, (byte)(v >>> 8 & 0xFF));
        this._buffer.put(i++, (byte)(v >>> 16 & 0xFF));
        this._buffer.put(i++, (byte)(v >>> 24 & 0xFF));
    }
    
    private void gzip(final ByteBuffer content, final boolean complete, final Callback callback) {
        if (content.hasRemaining() || complete) {
            new GzipBufferCB(content, complete, callback).iterate();
        }
        else {
            callback.succeeded();
        }
    }
    
    protected void commit(final ByteBuffer content, final boolean complete, final Callback callback) {
        final Response response = this._channel.getResponse();
        final int sc = response.getStatus();
        if (sc > 0 && (sc < 200 || sc == 204 || sc == 205 || sc >= 300)) {
            GzipHttpOutputInterceptor.LOG.debug("{} exclude by status {}", this, sc);
            this.noCompression();
            if (sc == 304) {
                final String request_etags = (String)this._channel.getRequest().getAttribute("o.e.j.s.h.gzip.GzipHandler.etag");
                final String response_etag = response.getHttpFields().get(HttpHeader.ETAG);
                if (request_etags != null && response_etag != null) {
                    final String response_etag_gzip = this.etagGzip(response_etag);
                    if (request_etags.contains(response_etag_gzip)) {
                        response.getHttpFields().put(HttpHeader.ETAG, response_etag_gzip);
                    }
                }
            }
            this._interceptor.write(content, complete, callback);
            return;
        }
        String ct = response.getContentType();
        if (ct != null) {
            ct = MimeTypes.getContentTypeWithoutCharset(ct);
            if (!this._factory.isMimeTypeGzipable(StringUtil.asciiToLowerCase(ct))) {
                GzipHttpOutputInterceptor.LOG.debug("{} exclude by mimeType {}", this, ct);
                this.noCompression();
                this._interceptor.write(content, complete, callback);
                return;
            }
        }
        final HttpFields fields = response.getHttpFields();
        final String ce = fields.get(HttpHeader.CONTENT_ENCODING);
        if (ce != null) {
            GzipHttpOutputInterceptor.LOG.debug("{} exclude by content-encoding {}", this, ce);
            this.noCompression();
            this._interceptor.write(content, complete, callback);
            return;
        }
        if (this._state.compareAndSet(GZState.MIGHT_COMPRESS, GZState.COMMITTING)) {
            if (this._vary != null) {
                if (fields.contains(HttpHeader.VARY)) {
                    fields.addCSV(HttpHeader.VARY, this._vary.getValues());
                }
                else {
                    fields.add(this._vary);
                }
            }
            long content_length = response.getContentLength();
            if (content_length < 0L && complete) {
                content_length = content.remaining();
            }
            this._deflater = this._factory.getDeflater(this._channel.getRequest(), content_length);
            if (this._deflater == null) {
                GzipHttpOutputInterceptor.LOG.debug("{} exclude no deflater", this);
                this._state.set(GZState.NOT_COMPRESSING);
                this._interceptor.write(content, complete, callback);
                return;
            }
            fields.put(GzipHttpContent.CONTENT_ENCODING_GZIP);
            this._crc.reset();
            BufferUtil.fill(this._buffer = this._channel.getByteBufferPool().acquire(this._bufferSize, false), GzipHttpOutputInterceptor.GZIP_HEADER, 0, GzipHttpOutputInterceptor.GZIP_HEADER.length);
            response.setContentLength(-1);
            final String etag = fields.get(HttpHeader.ETAG);
            if (etag != null) {
                fields.put(HttpHeader.ETAG, this.etagGzip(etag));
            }
            GzipHttpOutputInterceptor.LOG.debug("{} compressing {}", this, this._deflater);
            this._state.set(GZState.COMPRESSING);
            this.gzip(content, complete, callback);
        }
        else {
            callback.failed(new WritePendingException());
        }
    }
    
    private String etagGzip(final String etag) {
        final int end = etag.length() - 1;
        return (etag.charAt(end) == '\"') ? (etag.substring(0, end) + "--gzip" + '\"') : (etag + "--gzip");
    }
    
    public void noCompression() {
        do {
            switch (this._state.get()) {
                case NOT_COMPRESSING: {}
                case MIGHT_COMPRESS: {
                    continue;
                }
                default: {
                    throw new IllegalStateException(this._state.get().toString());
                }
            }
        } while (!this._state.compareAndSet(GZState.MIGHT_COMPRESS, GZState.NOT_COMPRESSING));
    }
    
    public void noCompressionIfPossible() {
        do {
            switch (this._state.get()) {
                case NOT_COMPRESSING:
                case COMPRESSING: {}
                case MIGHT_COMPRESS: {
                    continue;
                }
                default: {
                    throw new IllegalStateException(this._state.get().toString());
                }
            }
        } while (!this._state.compareAndSet(GZState.MIGHT_COMPRESS, GZState.NOT_COMPRESSING));
    }
    
    public boolean mightCompress() {
        return this._state.get() == GZState.MIGHT_COMPRESS;
    }
    
    static {
        GzipHttpOutputInterceptor.LOG = Log.getLogger(GzipHttpOutputInterceptor.class);
        GZIP_HEADER = new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 };
        VARY_ACCEPT_ENCODING_USER_AGENT = new PreEncodedHttpField(HttpHeader.VARY, HttpHeader.ACCEPT_ENCODING + ", " + HttpHeader.USER_AGENT);
        VARY_ACCEPT_ENCODING = new PreEncodedHttpField(HttpHeader.VARY, HttpHeader.ACCEPT_ENCODING.asString());
    }
    
    private enum GZState
    {
        MIGHT_COMPRESS, 
        NOT_COMPRESSING, 
        COMMITTING, 
        COMPRESSING, 
        FINISHED;
    }
    
    private class GzipBufferCB extends IteratingNestedCallback
    {
        private ByteBuffer _copy;
        private final ByteBuffer _content;
        private final boolean _last;
        
        public GzipBufferCB(final ByteBuffer content, final boolean complete, final Callback callback) {
            super(callback);
            this._content = content;
            this._last = complete;
        }
        
        @Override
        protected Action process() throws Exception {
            if (GzipHttpOutputInterceptor.this._deflater == null) {
                return Action.SUCCEEDED;
            }
            if (GzipHttpOutputInterceptor.this._deflater.needsInput()) {
                if (BufferUtil.isEmpty(this._content)) {
                    if (GzipHttpOutputInterceptor.this._deflater.finished()) {
                        GzipHttpOutputInterceptor.this._factory.recycle(GzipHttpOutputInterceptor.this._deflater);
                        GzipHttpOutputInterceptor.this._deflater = null;
                        GzipHttpOutputInterceptor.this._channel.getByteBufferPool().release(GzipHttpOutputInterceptor.this._buffer);
                        GzipHttpOutputInterceptor.this._buffer = null;
                        if (this._copy != null) {
                            GzipHttpOutputInterceptor.this._channel.getByteBufferPool().release(this._copy);
                            this._copy = null;
                        }
                        return Action.SUCCEEDED;
                    }
                    if (!this._last) {
                        return Action.SUCCEEDED;
                    }
                    GzipHttpOutputInterceptor.this._deflater.finish();
                }
                else if (this._content.hasArray()) {
                    final byte[] array = this._content.array();
                    final int off = this._content.arrayOffset() + this._content.position();
                    final int len = this._content.remaining();
                    BufferUtil.clear(this._content);
                    GzipHttpOutputInterceptor.this._crc.update(array, off, len);
                    GzipHttpOutputInterceptor.this._deflater.setInput(array, off, len);
                    if (this._last) {
                        GzipHttpOutputInterceptor.this._deflater.finish();
                    }
                }
                else {
                    if (this._copy == null) {
                        this._copy = GzipHttpOutputInterceptor.this._channel.getByteBufferPool().acquire(GzipHttpOutputInterceptor.this._bufferSize, false);
                    }
                    BufferUtil.clearToFill(this._copy);
                    final int took = BufferUtil.put(this._content, this._copy);
                    BufferUtil.flipToFlush(this._copy, 0);
                    if (took == 0) {
                        throw new IllegalStateException();
                    }
                    final byte[] array2 = this._copy.array();
                    final int off2 = this._copy.arrayOffset() + this._copy.position();
                    final int len2 = this._copy.remaining();
                    GzipHttpOutputInterceptor.this._crc.update(array2, off2, len2);
                    GzipHttpOutputInterceptor.this._deflater.setInput(array2, off2, len2);
                    if (this._last && BufferUtil.isEmpty(this._content)) {
                        GzipHttpOutputInterceptor.this._deflater.finish();
                    }
                }
            }
            BufferUtil.compact(GzipHttpOutputInterceptor.this._buffer);
            final int off3 = GzipHttpOutputInterceptor.this._buffer.arrayOffset() + GzipHttpOutputInterceptor.this._buffer.limit();
            final int len3 = GzipHttpOutputInterceptor.this._buffer.capacity() - GzipHttpOutputInterceptor.this._buffer.limit() - (this._last ? 8 : 0);
            if (len3 > 0) {
                final int produced = GzipHttpOutputInterceptor.this._deflater.deflate(GzipHttpOutputInterceptor.this._buffer.array(), off3, len3, GzipHttpOutputInterceptor.this._syncFlush ? 2 : 0);
                GzipHttpOutputInterceptor.this._buffer.limit(GzipHttpOutputInterceptor.this._buffer.limit() + produced);
            }
            final boolean finished = GzipHttpOutputInterceptor.this._deflater.finished();
            if (finished) {
                GzipHttpOutputInterceptor.this.addTrailer();
            }
            GzipHttpOutputInterceptor.this._interceptor.write(GzipHttpOutputInterceptor.this._buffer, finished, this);
            return Action.SCHEDULED;
        }
    }
}
