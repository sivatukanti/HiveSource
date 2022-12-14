// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.io.Text;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.io.InputStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.LimitedPrivate({ "MapReduce" })
@InterfaceStability.Unstable
public class LineReader implements Closeable
{
    private static final int DEFAULT_BUFFER_SIZE = 65536;
    private int bufferSize;
    private InputStream in;
    private byte[] buffer;
    private int bufferLength;
    private int bufferPosn;
    private static final byte CR = 13;
    private static final byte LF = 10;
    private final byte[] recordDelimiterBytes;
    
    public LineReader(final InputStream in) {
        this(in, 65536);
    }
    
    public LineReader(final InputStream in, final int bufferSize) {
        this.bufferSize = 65536;
        this.bufferLength = 0;
        this.bufferPosn = 0;
        this.in = in;
        this.bufferSize = bufferSize;
        this.buffer = new byte[this.bufferSize];
        this.recordDelimiterBytes = null;
    }
    
    public LineReader(final InputStream in, final Configuration conf) throws IOException {
        this(in, conf.getInt("io.file.buffer.size", 65536));
    }
    
    public LineReader(final InputStream in, final byte[] recordDelimiterBytes) {
        this.bufferSize = 65536;
        this.bufferLength = 0;
        this.bufferPosn = 0;
        this.in = in;
        this.bufferSize = 65536;
        this.buffer = new byte[this.bufferSize];
        this.recordDelimiterBytes = recordDelimiterBytes;
    }
    
    public LineReader(final InputStream in, final int bufferSize, final byte[] recordDelimiterBytes) {
        this.bufferSize = 65536;
        this.bufferLength = 0;
        this.bufferPosn = 0;
        this.in = in;
        this.bufferSize = bufferSize;
        this.buffer = new byte[this.bufferSize];
        this.recordDelimiterBytes = recordDelimiterBytes;
    }
    
    public LineReader(final InputStream in, final Configuration conf, final byte[] recordDelimiterBytes) throws IOException {
        this.bufferSize = 65536;
        this.bufferLength = 0;
        this.bufferPosn = 0;
        this.in = in;
        this.bufferSize = conf.getInt("io.file.buffer.size", 65536);
        this.buffer = new byte[this.bufferSize];
        this.recordDelimiterBytes = recordDelimiterBytes;
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    public int readLine(final Text str, final int maxLineLength, final int maxBytesToConsume) throws IOException {
        if (this.recordDelimiterBytes != null) {
            return this.readCustomLine(str, maxLineLength, maxBytesToConsume);
        }
        return this.readDefaultLine(str, maxLineLength, maxBytesToConsume);
    }
    
    protected int fillBuffer(final InputStream in, final byte[] buffer, final boolean inDelimiter) throws IOException {
        return in.read(buffer);
    }
    
    private int readDefaultLine(final Text str, final int maxLineLength, final int maxBytesToConsume) throws IOException {
        str.clear();
        int txtLength = 0;
        int newlineLength = 0;
        boolean prevCharCR = false;
        long bytesConsumed = 0L;
        do {
            int startPosn = this.bufferPosn;
            if (this.bufferPosn >= this.bufferLength) {
                final boolean bufferPosn = false;
                this.bufferPosn = (bufferPosn ? 1 : 0);
                startPosn = (bufferPosn ? 1 : 0);
                if (prevCharCR) {
                    ++bytesConsumed;
                }
                this.bufferLength = this.fillBuffer(this.in, this.buffer, prevCharCR);
                if (this.bufferLength <= 0) {
                    break;
                }
            }
            while (this.bufferPosn < this.bufferLength) {
                if (this.buffer[this.bufferPosn] == 10) {
                    newlineLength = (prevCharCR ? 2 : 1);
                    ++this.bufferPosn;
                    break;
                }
                if (prevCharCR) {
                    newlineLength = 1;
                    break;
                }
                prevCharCR = (this.buffer[this.bufferPosn] == 13);
                ++this.bufferPosn;
            }
            int readLength = this.bufferPosn - startPosn;
            if (prevCharCR && newlineLength == 0) {
                --readLength;
            }
            bytesConsumed += readLength;
            int appendLength = readLength - newlineLength;
            if (appendLength > maxLineLength - txtLength) {
                appendLength = maxLineLength - txtLength;
            }
            if (appendLength > 0) {
                str.append(this.buffer, startPosn, appendLength);
                txtLength += appendLength;
            }
        } while (newlineLength == 0 && bytesConsumed < maxBytesToConsume);
        if (bytesConsumed > 2147483647L) {
            throw new IOException("Too many bytes before newline: " + bytesConsumed);
        }
        return (int)bytesConsumed;
    }
    
    private int readCustomLine(final Text str, final int maxLineLength, final int maxBytesToConsume) throws IOException {
        str.clear();
        int txtLength = 0;
        long bytesConsumed = 0L;
        int delPosn = 0;
        int ambiguousByteCount = 0;
        do {
            int startPosn = this.bufferPosn;
            if (this.bufferPosn >= this.bufferLength) {
                final boolean bufferPosn = false;
                this.bufferPosn = (bufferPosn ? 1 : 0);
                startPosn = (bufferPosn ? 1 : 0);
                this.bufferLength = this.fillBuffer(this.in, this.buffer, ambiguousByteCount > 0);
                if (this.bufferLength <= 0) {
                    if (ambiguousByteCount > 0) {
                        str.append(this.recordDelimiterBytes, 0, ambiguousByteCount);
                        bytesConsumed += ambiguousByteCount;
                        break;
                    }
                    break;
                }
            }
            while (this.bufferPosn < this.bufferLength) {
                if (this.buffer[this.bufferPosn] == this.recordDelimiterBytes[delPosn]) {
                    if (++delPosn >= this.recordDelimiterBytes.length) {
                        ++this.bufferPosn;
                        break;
                    }
                }
                else if (delPosn != 0) {
                    this.bufferPosn -= delPosn;
                    if (this.bufferPosn < -1) {
                        this.bufferPosn = -1;
                    }
                    delPosn = 0;
                }
                ++this.bufferPosn;
            }
            final int readLength = this.bufferPosn - startPosn;
            bytesConsumed += readLength;
            int appendLength = readLength - delPosn;
            if (appendLength > maxLineLength - txtLength) {
                appendLength = maxLineLength - txtLength;
            }
            bytesConsumed += ambiguousByteCount;
            if (appendLength >= 0 && ambiguousByteCount > 0) {
                str.append(this.recordDelimiterBytes, 0, ambiguousByteCount);
                ambiguousByteCount = 0;
                this.unsetNeedAdditionalRecordAfterSplit();
            }
            if (appendLength > 0) {
                str.append(this.buffer, startPosn, appendLength);
                txtLength += appendLength;
            }
            if (this.bufferPosn >= this.bufferLength && delPosn > 0 && delPosn < this.recordDelimiterBytes.length) {
                ambiguousByteCount = delPosn;
                bytesConsumed -= ambiguousByteCount;
            }
        } while (delPosn < this.recordDelimiterBytes.length && bytesConsumed < maxBytesToConsume);
        if (bytesConsumed > 2147483647L) {
            throw new IOException("Too many bytes before delimiter: " + bytesConsumed);
        }
        return (int)bytesConsumed;
    }
    
    public int readLine(final Text str, final int maxLineLength) throws IOException {
        return this.readLine(str, maxLineLength, Integer.MAX_VALUE);
    }
    
    public int readLine(final Text str) throws IOException {
        return this.readLine(str, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    protected int getBufferPosn() {
        return this.bufferPosn;
    }
    
    protected int getBufferSize() {
        return this.bufferSize;
    }
    
    protected void unsetNeedAdditionalRecordAfterSplit() {
    }
}
