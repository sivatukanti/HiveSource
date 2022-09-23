// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import org.apache.hadoop.io.ByteBufferPool;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public final class ByteBufferUtil
{
    private static boolean streamHasByteBufferRead(final InputStream stream) {
        return stream instanceof ByteBufferReadable && (!(stream instanceof FSDataInputStream) || ((FSDataInputStream)stream).getWrappedStream() instanceof ByteBufferReadable);
    }
    
    public static ByteBuffer fallbackRead(final InputStream stream, final ByteBufferPool bufferPool, int maxLength) throws IOException {
        if (bufferPool == null) {
            throw new UnsupportedOperationException("zero-copy reads were not available, and you did not provide a fallback ByteBufferPool.");
        }
        final boolean useDirect = streamHasByteBufferRead(stream);
        ByteBuffer buffer = bufferPool.getBuffer(useDirect, maxLength);
        if (buffer == null) {
            throw new UnsupportedOperationException("zero-copy reads were not available, and the ByteBufferPool did not provide us with " + (useDirect ? "a direct" : "an indirect") + "buffer.");
        }
        Preconditions.checkState(buffer.capacity() > 0);
        Preconditions.checkState(buffer.isDirect() == useDirect);
        maxLength = Math.min(maxLength, buffer.capacity());
        boolean success = false;
        try {
            if (useDirect) {
                buffer.clear();
                buffer.limit(maxLength);
                final ByteBufferReadable readable = (ByteBufferReadable)stream;
                while (true) {
                    int nRead;
                    for (int totalRead = 0; totalRead < maxLength; totalRead += nRead) {
                        nRead = readable.read(buffer);
                        if (nRead < 0) {
                            if (totalRead > 0) {
                                success = true;
                            }
                            buffer.flip();
                            return buffer;
                        }
                    }
                    success = true;
                    continue;
                }
            }
            buffer.clear();
            final int nRead2 = stream.read(buffer.array(), buffer.arrayOffset(), maxLength);
            if (nRead2 >= 0) {
                buffer.limit(nRead2);
                success = true;
            }
        }
        finally {
            if (!success) {
                bufferPool.putBuffer(buffer);
                buffer = null;
            }
        }
        return buffer;
    }
}
