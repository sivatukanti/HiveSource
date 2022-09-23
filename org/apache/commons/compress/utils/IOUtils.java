// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public final class IOUtils
{
    private IOUtils() {
    }
    
    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, 8024);
    }
    
    public static long copy(final InputStream input, final OutputStream output, final int buffersize) throws IOException {
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        long count = 0L;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
