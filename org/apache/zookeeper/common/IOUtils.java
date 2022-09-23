// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.common;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import java.io.Closeable;

public class IOUtils
{
    public static void closeStream(final Closeable stream) {
        cleanup(null, stream);
    }
    
    public static void cleanup(final Logger log, final Closeable... closeables) {
        for (final Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                }
                catch (IOException e) {
                    if (log != null) {
                        log.warn("Exception in closing " + c, e);
                    }
                }
            }
        }
    }
    
    public static void copyBytes(InputStream in, OutputStream out, final int buffSize, final boolean close) throws IOException {
        try {
            copyBytes(in, out, buffSize);
            if (close) {
                out.close();
                out = null;
                in.close();
                in = null;
            }
        }
        finally {
            if (close) {
                closeStream(out);
                closeStream(in);
            }
        }
    }
    
    public static void copyBytes(final InputStream in, final OutputStream out, final int buffSize) throws IOException {
        final PrintStream ps = (out instanceof PrintStream) ? ((PrintStream)out) : null;
        final byte[] buf = new byte[buffSize];
        for (int bytesRead = in.read(buf); bytesRead >= 0; bytesRead = in.read(buf)) {
            out.write(buf, 0, bytesRead);
            if (ps != null && ps.checkError()) {
                throw new IOException("Unable to write to output stream.");
            }
        }
    }
}
