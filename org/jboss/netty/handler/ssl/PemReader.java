// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.ssl;

import org.jboss.netty.logging.InternalLoggerFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyException;
import java.util.regex.Matcher;
import java.util.List;
import org.jboss.netty.handler.codec.base64.Base64;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.io.IOException;
import java.security.cert.CertificateException;
import org.jboss.netty.buffer.ChannelBuffer;
import java.io.File;
import java.util.regex.Pattern;
import org.jboss.netty.logging.InternalLogger;

final class PemReader
{
    private static final InternalLogger logger;
    private static final Pattern CERT_PATTERN;
    private static final Pattern KEY_PATTERN;
    
    static ChannelBuffer[] readCertificates(final File file) throws CertificateException {
        String content;
        try {
            content = readContent(file);
        }
        catch (IOException e) {
            throw new CertificateException("failed to read a file: " + file, e);
        }
        final List<ChannelBuffer> certs = new ArrayList<ChannelBuffer>();
        final Matcher m = PemReader.CERT_PATTERN.matcher(content);
        for (int start = 0; m.find(start); start = m.end()) {
            certs.add(Base64.decode(ChannelBuffers.copiedBuffer(m.group(1), CharsetUtil.US_ASCII)));
        }
        if (certs.isEmpty()) {
            throw new CertificateException("found no certificates: " + file);
        }
        return certs.toArray(new ChannelBuffer[certs.size()]);
    }
    
    static ChannelBuffer readPrivateKey(final File file) throws KeyException {
        String content;
        try {
            content = readContent(file);
        }
        catch (IOException e) {
            throw new KeyException("failed to read a file: " + file, e);
        }
        final Matcher m = PemReader.KEY_PATTERN.matcher(content);
        if (!m.find()) {
            throw new KeyException("found no private key: " + file);
        }
        return Base64.decode(ChannelBuffers.copiedBuffer(m.group(1), CharsetUtil.US_ASCII));
    }
    
    private static String readContent(final File file) throws IOException {
        final InputStream in = new FileInputStream(file);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final byte[] buf = new byte[8192];
            while (true) {
                final int ret = in.read(buf);
                if (ret < 0) {
                    break;
                }
                out.write(buf, 0, ret);
            }
            return out.toString(CharsetUtil.US_ASCII.name());
        }
        finally {
            safeClose(in);
            safeClose(out);
        }
    }
    
    private static void safeClose(final InputStream in) {
        try {
            in.close();
        }
        catch (IOException e) {
            PemReader.logger.warn("Failed to close a stream.", e);
        }
    }
    
    private static void safeClose(final OutputStream out) {
        try {
            out.close();
        }
        catch (IOException e) {
            PemReader.logger.warn("Failed to close a stream.", e);
        }
    }
    
    private PemReader() {
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PemReader.class);
        CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
        KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);
    }
}
