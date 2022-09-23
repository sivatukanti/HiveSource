// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

import java.nio.ByteBuffer;
import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;

class FileUtils
{
    private static final Charset UTF8;
    
    public static File toFile(final URL url) {
        if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
            return null;
        }
        String filename = url.getFile().replace('/', File.separatorChar);
        filename = decodeUrl(filename);
        return new File(filename);
    }
    
    static String decodeUrl(final String url) {
        String decoded = url;
        if (url != null && url.indexOf(37) >= 0) {
            final int n = url.length();
            final StringBuffer buffer = new StringBuffer();
            final ByteBuffer bytes = ByteBuffer.allocate(n);
            int i = 0;
            while (i < n) {
                if (url.charAt(i) == '%') {
                    try {
                        do {
                            final byte octet = (byte)Integer.parseInt(url.substring(i + 1, i + 3), 16);
                            bytes.put(octet);
                            i += 3;
                        } while (i < n && url.charAt(i) == '%');
                        continue;
                    }
                    catch (RuntimeException ex) {}
                    finally {
                        if (bytes.position() > 0) {
                            bytes.flip();
                            buffer.append(FileUtils.UTF8.decode(bytes).toString());
                            bytes.clear();
                        }
                    }
                }
                buffer.append(url.charAt(i++));
            }
            decoded = buffer.toString();
        }
        return decoded;
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
    }
}
