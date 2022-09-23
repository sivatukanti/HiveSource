// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import org.jboss.netty.util.internal.SystemPropertyUtil;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.util.internal.StringUtil;
import org.jboss.netty.handler.codec.compression.ZlibWrapper;
import org.jboss.netty.handler.codec.compression.JdkZlibEncoder;
import org.jboss.netty.handler.codec.compression.ZlibEncoder;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.util.internal.DetectionUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.embedder.EncoderEmbedder;
import org.jboss.netty.logging.InternalLogger;

public class HttpContentCompressor extends HttpContentEncoder
{
    private static final InternalLogger logger;
    private static final int DEFAULT_JDK_WINDOW_SIZE = 15;
    private static final int DEFAULT_JDK_MEM_LEVEL = 8;
    private static final boolean noJdkZlibEncoder;
    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;
    
    public HttpContentCompressor() {
        this(6);
    }
    
    public HttpContentCompressor(final int compressionLevel) {
        this(compressionLevel, 15, 8);
    }
    
    public HttpContentCompressor(final int compressionLevel, final int windowBits, final int memLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        if (windowBits < 9 || windowBits > 15) {
            throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
        }
        if (memLevel < 1 || memLevel > 9) {
            throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
        }
        this.compressionLevel = compressionLevel;
        this.windowBits = windowBits;
        this.memLevel = memLevel;
    }
    
    @Override
    protected EncoderEmbedder<ChannelBuffer> newContentEncoder(final HttpMessage msg, final String acceptEncoding) throws Exception {
        final String contentEncoding = msg.headers().get("Content-Encoding");
        if (contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding)) {
            return null;
        }
        final ZlibWrapper wrapper = determineWrapper(acceptEncoding);
        if (wrapper == null) {
            return null;
        }
        if (DetectionUtil.javaVersion() < 7 || HttpContentCompressor.noJdkZlibEncoder || this.windowBits != 15 || this.memLevel != 8) {
            return new EncoderEmbedder<ChannelBuffer>(new ChannelDownstreamHandler[] { new ZlibEncoder(wrapper, this.compressionLevel, this.windowBits, this.memLevel) });
        }
        return new EncoderEmbedder<ChannelBuffer>(new ChannelDownstreamHandler[] { new JdkZlibEncoder(wrapper, this.compressionLevel) });
    }
    
    @Override
    protected String getTargetContentEncoding(final String acceptEncoding) throws Exception {
        final ZlibWrapper wrapper = determineWrapper(acceptEncoding);
        if (wrapper == null) {
            return null;
        }
        switch (wrapper) {
            case GZIP: {
                return "gzip";
            }
            case ZLIB: {
                return "deflate";
            }
            default: {
                throw new Error();
            }
        }
    }
    
    private static ZlibWrapper determineWrapper(final String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (final String encoding : StringUtil.split(acceptEncoding, ',')) {
            float q = 1.0f;
            final int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.valueOf(encoding.substring(equalsPos + 1));
                }
                catch (NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.indexOf(42) >= 0) {
                starQ = q;
            }
            else if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
            }
            else if (encoding.contains("deflate") && q > deflateQ) {
                deflateQ = q;
            }
        }
        if (gzipQ <= 0.0f && deflateQ <= 0.0f) {
            if (starQ > 0.0f) {
                if (gzipQ == -1.0f) {
                    return ZlibWrapper.GZIP;
                }
                if (deflateQ == -1.0f) {
                    return ZlibWrapper.ZLIB;
                }
            }
            return null;
        }
        if (gzipQ >= deflateQ) {
            return ZlibWrapper.GZIP;
        }
        return ZlibWrapper.ZLIB;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(HttpContentCompressor.class);
        noJdkZlibEncoder = SystemPropertyUtil.getBoolean("io.netty.noJdkZlibEncoder", false);
        if (HttpContentCompressor.logger.isDebugEnabled()) {
            HttpContentCompressor.logger.debug("-Dio.netty.noJdkZlibEncoder: " + HttpContentCompressor.noJdkZlibEncoder);
        }
    }
}
