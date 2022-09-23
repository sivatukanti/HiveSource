// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.util;

import java.io.UTFDataFormatException;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class UTF8Util
{
    private UTF8Util() {
    }
    
    public static final long skipUntilEOF(final InputStream inputStream) throws IOException {
        return internalSkip(inputStream, Long.MAX_VALUE).charsSkipped();
    }
    
    public static final long skipFully(final InputStream inputStream, final long lng) throws EOFException, IOException {
        final SkipCount internalSkip = internalSkip(inputStream, lng);
        if (internalSkip.charsSkipped() != lng) {
            throw new EOFException("Reached end-of-stream prematurely at character/byte position " + internalSkip.charsSkipped() + "/" + internalSkip.bytesSkipped() + ", trying to skip " + lng);
        }
        return internalSkip.bytesSkipped();
    }
    
    private static final SkipCount internalSkip(final InputStream inputStream, final long n) throws IOException {
        long n2 = 0L;
        long n3 = 0L;
        while (n2 < n) {
            final int read = inputStream.read();
            if (read == -1) {
                break;
            }
            ++n2;
            if ((read & 0x80) == 0x0) {
                ++n3;
            }
            else if ((read & 0x60) == 0x40) {
                if (InputStreamUtil.skipPersistent(inputStream, 1L) != 1L) {
                    throw new UTFDataFormatException("Second byte in two byte character missing; byte pos " + n3 + " ; char pos " + n2);
                }
                n3 += 2L;
            }
            else {
                if ((read & 0x70) != 0x60) {
                    throw new UTFDataFormatException("Invalid UTF-8 encoding encountered: (decimal) " + read);
                }
                int n4 = 0;
                if (read == 224) {
                    final int read2 = inputStream.read();
                    final int read3 = inputStream.read();
                    if (read2 == 0 && read3 == 0) {
                        --n2;
                        break;
                    }
                    if (read2 != -1 && read3 != -1) {
                        n4 = 2;
                    }
                }
                else {
                    n4 = (int)InputStreamUtil.skipPersistent(inputStream, 2L);
                }
                if (n4 != 2) {
                    throw new UTFDataFormatException("Second or third byte in three byte character missing; byte pos " + n3 + " ; char pos " + n2);
                }
                n3 += 3L;
            }
        }
        return new SkipCount(n2, n3);
    }
    
    private static final class SkipCount
    {
        private final long byteCount;
        private final long charCount;
        
        SkipCount(final long charCount, final long byteCount) {
            if (byteCount < 0L || charCount < 0L) {
                throw new IllegalArgumentException("charCount/byteCount cannot be negative: " + charCount + "/" + byteCount);
            }
            if (byteCount < charCount) {
                throw new IllegalArgumentException("Number of bytes cannot beless than number of chars: " + byteCount + " < " + charCount);
            }
            this.byteCount = byteCount;
            this.charCount = charCount;
        }
        
        long charsSkipped() {
            return this.charCount;
        }
        
        long bytesSkipped() {
            return this.byteCount;
        }
    }
}
