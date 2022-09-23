// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;

public final class InputStreamUtil
{
    private static final int SKIP_FRAGMENT_SIZE = Integer.MAX_VALUE;
    
    public static int readUnsignedByte(final InputStream inputStream) throws IOException {
        final int read = inputStream.read();
        if (read < 0) {
            throw new EOFException();
        }
        return read;
    }
    
    public static void readFully(final InputStream inputStream, final byte[] b, int off, int i) throws IOException {
        do {
            final int read = inputStream.read(b, off, i);
            if (read < 0) {
                throw new EOFException();
            }
            i -= read;
            off += read;
        } while (i != 0);
    }
    
    public static int readLoop(final InputStream inputStream, final byte[] b, int off, int i) throws IOException {
        final int n = off;
        do {
            final int read = inputStream.read(b, off, i);
            if (read <= 0) {
                break;
            }
            i -= read;
            off += read;
        } while (i != 0);
        return off - n;
    }
    
    public static long skipUntilEOF(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        long n = 0L;
        long skipPersistent;
        do {
            skipPersistent = skipPersistent(inputStream, 2147483647L);
            n += skipPersistent;
        } while (skipPersistent >= 2147483647L);
        return n;
    }
    
    public static void skipFully(final InputStream inputStream, final long n) throws IOException {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        if (n <= 0L) {
            return;
        }
        if (skipPersistent(inputStream, n) < n) {
            throw new EOFException();
        }
    }
    
    public static final long skipPersistent(final InputStream inputStream, final long n) throws IOException {
        long n2;
        long skip;
        for (n2 = 0L; n2 < n; n2 += skip) {
            skip = inputStream.skip(n - n2);
            if (skip == 0L) {
                if (inputStream.read() == -1) {
                    break;
                }
                skip = 1L;
            }
        }
        return n2;
    }
}
