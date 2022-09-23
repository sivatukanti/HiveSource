// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public abstract class HttpMessageEncoder extends OneToOneEncoder
{
    private static final byte[] CRLF;
    private static final ChannelBuffer LAST_CHUNK;
    private volatile boolean transferEncodingChunked;
    
    protected HttpMessageEncoder() {
    }
    
    @Override
    protected Object encode(final ChannelHandlerContext ctx, final Channel channel, final Object msg) throws Exception {
        if (msg instanceof HttpMessage) {
            final HttpMessage m = (HttpMessage)msg;
            boolean contentMustBeEmpty;
            if (m.isChunked()) {
                if (HttpCodecUtil.isContentLengthSet(m)) {
                    contentMustBeEmpty = false;
                    this.transferEncodingChunked = false;
                    HttpCodecUtil.removeTransferEncodingChunked(m);
                }
                else {
                    if (!HttpCodecUtil.isTransferEncodingChunked(m)) {
                        m.headers().add("Transfer-Encoding", "chunked");
                    }
                    contentMustBeEmpty = true;
                    this.transferEncodingChunked = true;
                }
            }
            else {
                contentMustBeEmpty = (this.transferEncodingChunked = HttpCodecUtil.isTransferEncodingChunked(m));
            }
            final ChannelBuffer header = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
            this.encodeInitialLine(header, m);
            encodeHeaders(header, m);
            header.writeByte(13);
            header.writeByte(10);
            final ChannelBuffer content = m.getContent();
            if (!content.readable()) {
                return header;
            }
            if (contentMustBeEmpty) {
                throw new IllegalArgumentException("HttpMessage.content must be empty if Transfer-Encoding is chunked.");
            }
            return ChannelBuffers.wrappedBuffer(header, content);
        }
        else {
            if (!(msg instanceof HttpChunk)) {
                return msg;
            }
            final HttpChunk chunk = (HttpChunk)msg;
            if (!this.transferEncodingChunked) {
                return chunk.getContent();
            }
            if (!chunk.isLast()) {
                final ChannelBuffer content2 = chunk.getContent();
                final int contentLength = content2.readableBytes();
                return ChannelBuffers.wrappedBuffer(ChannelBuffers.copiedBuffer(Integer.toHexString(contentLength), CharsetUtil.US_ASCII), ChannelBuffers.wrappedBuffer(HttpMessageEncoder.CRLF), content2.slice(content2.readerIndex(), contentLength), ChannelBuffers.wrappedBuffer(HttpMessageEncoder.CRLF));
            }
            this.transferEncodingChunked = false;
            if (chunk instanceof HttpChunkTrailer) {
                final ChannelBuffer trailer = ChannelBuffers.dynamicBuffer(channel.getConfig().getBufferFactory());
                trailer.writeByte(48);
                trailer.writeByte(13);
                trailer.writeByte(10);
                encodeTrailingHeaders(trailer, (HttpChunkTrailer)chunk);
                trailer.writeByte(13);
                trailer.writeByte(10);
                return trailer;
            }
            return HttpMessageEncoder.LAST_CHUNK.duplicate();
        }
    }
    
    private static void encodeHeaders(final ChannelBuffer buf, final HttpMessage message) {
        try {
            for (final Map.Entry<String, String> h : message.headers()) {
                encodeHeader(buf, h.getKey(), h.getValue());
            }
        }
        catch (UnsupportedEncodingException e) {
            throw (Error)new Error().initCause(e);
        }
    }
    
    private static void encodeTrailingHeaders(final ChannelBuffer buf, final HttpChunkTrailer trailer) {
        try {
            for (final Map.Entry<String, String> h : trailer.trailingHeaders()) {
                encodeHeader(buf, h.getKey(), h.getValue());
            }
        }
        catch (UnsupportedEncodingException e) {
            throw (Error)new Error().initCause(e);
        }
    }
    
    private static void encodeHeader(final ChannelBuffer buf, final String header, final String value) throws UnsupportedEncodingException {
        encodeAscii(header, buf);
        buf.writeByte(58);
        buf.writeByte(32);
        encodeAscii(value, buf);
        buf.writeByte(13);
        buf.writeByte(10);
    }
    
    protected static void encodeAscii(final String s, final ChannelBuffer buf) {
        for (int i = 0; i < s.length(); ++i) {
            buf.writeByte(c2b(s.charAt(i)));
        }
    }
    
    private static byte c2b(final char c) {
        if (c > '\u00ff') {
            return 63;
        }
        return (byte)c;
    }
    
    protected abstract void encodeInitialLine(final ChannelBuffer p0, final HttpMessage p1) throws Exception;
    
    static {
        CRLF = new byte[] { 13, 10 };
        LAST_CHUNK = ChannelBuffers.copiedBuffer("0\r\n\r\n", CharsetUtil.US_ASCII);
    }
}
