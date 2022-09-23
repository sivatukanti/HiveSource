// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.websocketx;

import org.jboss.netty.util.CharsetUtil;
import org.jboss.netty.handler.codec.base64.Base64;
import java.security.NoSuchAlgorithmException;
import org.jboss.netty.buffer.ChannelBuffers;
import java.security.MessageDigest;
import org.jboss.netty.buffer.ChannelBuffer;

final class WebSocketUtil
{
    static ChannelBuffer md5(final ChannelBuffer buffer) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            if (buffer.hasArray()) {
                final int offset = buffer.arrayOffset() + buffer.readerIndex();
                final int length = buffer.readableBytes();
                md.update(buffer.array(), offset, length);
            }
            else {
                md.update(buffer.toByteBuffer());
            }
            return ChannelBuffers.wrappedBuffer(md.digest());
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not supported on this platform");
        }
    }
    
    static ChannelBuffer sha1(final ChannelBuffer buffer) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            if (buffer.hasArray()) {
                final int offset = buffer.arrayOffset() + buffer.readerIndex();
                final int length = buffer.readableBytes();
                md.update(buffer.array(), offset, length);
            }
            else {
                md.update(buffer.toByteBuffer());
            }
            return ChannelBuffers.wrappedBuffer(md.digest());
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalError("SHA-1 not supported on this platform");
        }
    }
    
    static String base64(final ChannelBuffer buffer) {
        return Base64.encode(buffer).toString(CharsetUtil.UTF_8);
    }
    
    static byte[] randomBytes(final int size) {
        final byte[] bytes = new byte[size];
        for (int i = 0; i < size; ++i) {
            bytes[i] = (byte)randomNumber(0, 255);
        }
        return bytes;
    }
    
    static int randomNumber(final int min, final int max) {
        return (int)(Math.random() * max + min);
    }
    
    private WebSocketUtil() {
    }
}
