// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.util;

import java.nio.channels.WritableByteChannel;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public final class IOUtil
{
    private IOUtil() {
    }
    
    public static byte[] readInputStream(final InputStream in) throws IOException {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            in.close();
            return baos.toByteArray();
        }
    }
    
    public static void readInputStream(final InputStream in, final byte[] buf) throws IOException {
        int ret;
        for (int toRead = buf.length, off = 0; toRead > 0; toRead -= ret, off += ret) {
            ret = in.read(buf, off, toRead);
            if (ret < 0) {
                throw new IOException("Bad inputStream, premature EOF");
            }
        }
        in.close();
    }
    
    public static String readInput(final InputStream in) throws IOException {
        final byte[] content = readInputStream(in);
        return Utf8.toString(content);
    }
    
    public static String readFile(final File file) throws IOException {
        long len = 0L;
        if (file.length() >= 2147483647L) {
            throw new IOException("Too large file, unexpected!");
        }
        len = file.length();
        final byte[] buf = new byte[(int)len];
        final InputStream is = Files.newInputStream(file.toPath(), new OpenOption[0]);
        readInputStream(is, buf);
        return Utf8.toString(buf);
    }
    
    public static void writeFile(final String content, final File file) throws IOException {
        final OutputStream outputStream = Files.newOutputStream(file.toPath(), new OpenOption[0]);
        final WritableByteChannel channel = Channels.newChannel(outputStream);
        final ByteBuffer buffer = ByteBuffer.wrap(Utf8.toBytes(content));
        channel.write(buffer);
        outputStream.close();
    }
}
