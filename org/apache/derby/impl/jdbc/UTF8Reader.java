// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.EOFException;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import java.io.UTFDataFormatException;
import java.io.IOException;
import java.io.BufferedInputStream;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;
import org.apache.derby.iapi.types.PositionedStream;
import java.io.InputStream;
import java.io.Reader;

public final class UTF8Reader extends Reader
{
    private static final String READER_CLOSED = "Reader closed";
    private static final int MAXIMUM_BUFFER_SIZE = 8192;
    private InputStream in;
    private final PositionedStream positionedIn;
    private long rawStreamPos;
    private long utfCount;
    private long readerCharCount;
    private final char[] buffer;
    private int charactersInBuffer;
    private int readPositionInBuffer;
    private boolean noMoreReads;
    private ConnectionChild parent;
    private final CharacterStreamDescriptor csd;
    
    public UTF8Reader(final CharacterStreamDescriptor csd, final ConnectionChild parent, final Object lock) throws IOException {
        super(lock);
        this.rawStreamPos = 0L;
        this.csd = csd;
        this.positionedIn = (csd.isPositionAware() ? csd.getPositionedStream() : null);
        this.parent = parent;
        final int calculateBufferSize = this.calculateBufferSize(csd);
        this.buffer = new char[calculateBufferSize];
        if (csd.isPositionAware()) {
            this.rawStreamPos = this.positionedIn.getPosition();
            if (this.rawStreamPos < csd.getDataOffset()) {
                this.rawStreamPos = csd.getDataOffset();
            }
        }
        else if (csd.getCurBytePos() < csd.getDataOffset()) {
            csd.getStream().skip(csd.getDataOffset() - csd.getCurBytePos());
        }
        if (csd.isBufferable()) {
            this.in = new BufferedInputStream(csd.getStream(), calculateBufferSize);
        }
        else {
            this.in = csd.getStream();
        }
        this.utfCount = csd.getDataOffset();
    }
    
    public int read() throws IOException {
        synchronized (this.lock) {
            if (this.noMoreReads) {
                throw new IOException("Reader closed");
            }
            if (this.readPositionInBuffer >= this.charactersInBuffer && this.fillBuffer()) {
                return -1;
            }
            return this.buffer[this.readPositionInBuffer++];
        }
    }
    
    public int read(final char[] array, final int n, int n2) throws IOException {
        synchronized (this.lock) {
            if (this.noMoreReads) {
                throw new IOException("Reader closed");
            }
            if (this.readPositionInBuffer >= this.charactersInBuffer && this.fillBuffer()) {
                return -1;
            }
            final int n3 = this.charactersInBuffer - this.readPositionInBuffer;
            if (n2 > n3) {
                n2 = n3;
            }
            System.arraycopy(this.buffer, this.readPositionInBuffer, array, n, n2);
            this.readPositionInBuffer += n2;
            return n2;
        }
    }
    
    public long skip(long lng) throws IOException {
        if (lng < 0L) {
            throw new IllegalArgumentException("Number of characters to skip must be positive: " + lng);
        }
        synchronized (this.lock) {
            if (this.noMoreReads) {
                throw new IOException("Reader closed");
            }
            if (this.readPositionInBuffer >= this.charactersInBuffer && this.fillBuffer()) {
                return 0L;
            }
            final int n = this.charactersInBuffer - this.readPositionInBuffer;
            if (lng > n) {
                lng = n;
            }
            this.readPositionInBuffer += (int)lng;
            return lng;
        }
    }
    
    public void close() {
        synchronized (this.lock) {
            this.closeIn();
            this.parent = null;
            this.noMoreReads = true;
        }
    }
    
    public int readInto(final StringBuffer sb, int len) throws IOException {
        synchronized (this.lock) {
            if (this.readPositionInBuffer >= this.charactersInBuffer && this.fillBuffer()) {
                return -1;
            }
            final int n = this.charactersInBuffer - this.readPositionInBuffer;
            if (len > n) {
                len = n;
            }
            sb.append(this.buffer, this.readPositionInBuffer, len);
            this.readPositionInBuffer += len;
            return len;
        }
    }
    
    int readAsciiInto(final byte[] array, final int n, int n2) throws IOException {
        synchronized (this.lock) {
            if (this.readPositionInBuffer >= this.charactersInBuffer && this.fillBuffer()) {
                return -1;
            }
            final int n3 = this.charactersInBuffer - this.readPositionInBuffer;
            if (n2 > n3) {
                n2 = n3;
            }
            final char[] buffer = this.buffer;
            for (int i = 0; i < n2; ++i) {
                final char c = buffer[this.readPositionInBuffer + i];
                byte b;
                if (c <= '\u00ff') {
                    b = (byte)c;
                }
                else {
                    b = 63;
                }
                array[n + i] = b;
            }
            this.readPositionInBuffer += n2;
            return n2;
        }
    }
    
    private void closeIn() {
        if (this.in != null) {
            try {
                this.in.close();
            }
            catch (IOException ex) {}
            finally {
                this.in = null;
            }
        }
    }
    
    private IOException utfFormatException(final String s) {
        this.noMoreReads = true;
        this.closeIn();
        return new UTFDataFormatException(s);
    }
    
    private boolean fillBuffer() throws IOException {
        if (this.in == null) {
            return true;
        }
        this.charactersInBuffer = 0;
        this.readPositionInBuffer = 0;
        try {
            try {
                this.parent.setupContextStack();
                if (this.positionedIn != null) {
                    try {
                        this.positionedIn.reposition(this.rawStreamPos);
                    }
                    catch (StandardException ex) {
                        throw Util.generateCsSQLException(ex);
                    }
                }
                final long byteLength = this.csd.getByteLength();
                final long maxCharLength = this.csd.getMaxCharLength();
            Label_0758:
                while (this.charactersInBuffer < this.buffer.length && (this.utfCount < byteLength || byteLength == 0L) && (maxCharLength == 0L || this.readerCharCount < maxCharLength)) {
                    final int read = this.in.read();
                    if (read == -1) {
                        if (byteLength != 0L) {
                            throw this.utfFormatException("Reached EOF prematurely, read " + this.utfCount + " out of " + byteLength + " bytes");
                        }
                        if (!this.csd.isPositionAware()) {
                            this.closeIn();
                            break;
                        }
                        break;
                    }
                    else {
                        int n = 0;
                        switch (read >> 4) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7: {
                                ++this.utfCount;
                                n = read;
                                break;
                            }
                            case 12:
                            case 13: {
                                this.utfCount += 2L;
                                final int read2 = this.in.read();
                                if (read2 == -1) {
                                    throw this.utfFormatException("Reached EOF when reading second byte in a two byte character encoding; byte/char position " + this.utfCount + "/" + this.readerCharCount);
                                }
                                if ((read2 & 0xC0) != 0x80) {
                                    throw this.utfFormatException("Second byte in a two bytecharacter encoding invalid: (int)" + read2 + ", byte/char pos " + this.utfCount + "/" + this.readerCharCount);
                                }
                                n = ((read & 0x1F) << 6 | (read2 & 0x3F));
                                break;
                            }
                            case 14: {
                                this.utfCount += 3L;
                                final int read3 = this.in.read();
                                final int read4 = this.in.read();
                                if (read3 == -1 || read4 == -1) {
                                    throw this.utfFormatException("Reached EOF when reading second/third byte in a three byte character encoding; byte/char position " + this.utfCount + "/" + this.readerCharCount);
                                }
                                if (read == 224 && read3 == 0 && read4 == 0) {
                                    if (byteLength != 0L) {
                                        throw this.utfFormatException("Internal error: Derby-specific EOF marker read");
                                    }
                                    if (!this.csd.isPositionAware()) {
                                        this.closeIn();
                                        break Label_0758;
                                    }
                                    break Label_0758;
                                }
                                else {
                                    if ((read3 & 0xC0) != 0x80 || (read4 & 0xC0) != 0x80) {
                                        throw this.utfFormatException("Second/third byte in a three byte character encoding invalid: (int)" + read3 + "/" + read4 + ", byte/char pos " + this.utfCount + "/" + this.readerCharCount);
                                    }
                                    n = ((read & 0xF) << 12 | (read3 & 0x3F) << 6 | (read4 & 0x3F) << 0);
                                    break;
                                }
                                break;
                            }
                            default: {
                                throw this.utfFormatException("Invalid UTF encoding at byte/char position " + this.utfCount + "/" + this.readerCharCount + ": (int)" + read);
                            }
                        }
                        this.buffer[this.charactersInBuffer++] = (char)n;
                        ++this.readerCharCount;
                    }
                }
                if (byteLength != 0L && this.utfCount > byteLength) {
                    throw this.utfFormatException("Incorrect encoded length in stream, expected " + byteLength + ", have " + this.utfCount + " bytes");
                }
                if (this.charactersInBuffer != 0) {
                    if (this.positionedIn != null) {
                        this.rawStreamPos = this.positionedIn.getPosition();
                    }
                    return false;
                }
                if (!this.csd.isPositionAware()) {
                    this.closeIn();
                }
                return true;
            }
            finally {
                ConnectionChild.restoreIntrFlagIfSeen(true, this.parent.getEmbedConnection());
                this.parent.restoreContextStack();
            }
        }
        catch (SQLException ex2) {
            throw Util.newIOException(ex2);
        }
    }
    
    private void resetUTF8Reader() throws IOException, StandardException {
        this.positionedIn.reposition(this.csd.getDataOffset());
        final long position = this.positionedIn.getPosition();
        this.rawStreamPos = position;
        this.utfCount = position;
        if (this.csd.isBufferable()) {
            this.in = new BufferedInputStream(this.csd.getStream(), this.buffer.length);
        }
        this.readerCharCount = 0L;
        final int n = 0;
        this.readPositionInBuffer = n;
        this.charactersInBuffer = n;
    }
    
    void reposition(final long n) throws IOException, StandardException {
        if (n <= this.readerCharCount - this.charactersInBuffer) {
            this.resetUTF8Reader();
        }
        final long n2 = n - 1L - (this.readerCharCount - this.charactersInBuffer + this.readPositionInBuffer);
        if (n2 <= 0L) {
            this.readPositionInBuffer += (int)n2;
        }
        else {
            this.persistentSkip(n2);
        }
    }
    
    private final int calculateBufferSize(final CharacterStreamDescriptor characterStreamDescriptor) {
        int n = 8192;
        long n2 = characterStreamDescriptor.getCharLength();
        final long maxCharLength = characterStreamDescriptor.getMaxCharLength();
        if (n2 < 1L) {
            n2 = characterStreamDescriptor.getByteLength();
        }
        if (n2 > 0L && n2 < n) {
            n = (int)n2;
        }
        if (maxCharLength > 0L && maxCharLength < n) {
            n = (int)maxCharLength;
        }
        return n;
    }
    
    private final void persistentSkip(final long n) throws IOException {
        long skip;
        for (long n2 = n; n2 > 0L; n2 -= skip) {
            skip = this.skip(n2);
            if (skip == 0L) {
                throw new EOFException();
            }
        }
    }
}
