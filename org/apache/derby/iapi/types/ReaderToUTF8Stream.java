// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.io.DerbyIOException;
import java.io.IOException;
import java.io.EOFException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.Reader;
import org.apache.derby.iapi.services.io.LimitReader;
import java.io.InputStream;

public final class ReaderToUTF8Stream extends InputStream
{
    private LimitReader reader;
    private static final int FIRST_READ = Integer.MIN_VALUE;
    private static final int READ_BUFFER_RESERVATION = 6;
    private static final int MARK_UNSET_OR_EXCEEDED = -1;
    private byte[] buffer;
    private int boff;
    private int blen;
    private int mark;
    private int readAheadLimit;
    private boolean eof;
    private boolean multipleBuffer;
    private final StreamHeaderGenerator hdrGen;
    private int headerLength;
    private final int charsToTruncate;
    private static final char SPACE = ' ';
    private final int valueLength;
    private final String typeName;
    private int charCount;
    
    public ReaderToUTF8Stream(final Reader reader, final int n, final int charsToTruncate, final String typeName, final StreamHeaderGenerator hdrGen) {
        this.blen = -1;
        this.mark = -1;
        this.reader = new LimitReader(reader);
        this.charsToTruncate = charsToTruncate;
        this.valueLength = n;
        this.typeName = typeName;
        this.hdrGen = hdrGen;
        final int abs = Math.abs(n);
        this.reader.setLimit(abs);
        int n2 = 32768;
        if (abs < n2 / 3) {
            n2 = this.hdrGen.getMaxHeaderLength() + Math.max(6, abs * 3 + 3);
        }
        this.buffer = new byte[n2];
    }
    
    public ReaderToUTF8Stream(final Reader reader, final int i, final String s, final StreamHeaderGenerator streamHeaderGenerator) {
        this(reader, -1 * i, 0, s, streamHeaderGenerator);
        if (i < 0) {
            throw new IllegalArgumentException("Maximum length for a capped stream cannot be negative: " + i);
        }
    }
    
    public int read() throws IOException {
        if (this.buffer == null) {
            throw new EOFException(MessageService.getTextMessage("XJ085.S"));
        }
        if (this.blen < 0) {
            this.fillBuffer(Integer.MIN_VALUE);
        }
        while (this.boff == this.blen) {
            if (this.eof) {
                this.close();
                return -1;
            }
            this.fillBuffer(0);
        }
        return this.buffer[this.boff++] & 0xFF;
    }
    
    public int read(final byte[] array, int n, int i) throws IOException {
        if (this.buffer == null) {
            throw new EOFException(MessageService.getTextMessage("XJ085.S"));
        }
        if (this.blen < 0) {
            this.fillBuffer(Integer.MIN_VALUE);
        }
        int n2 = 0;
        while (i > 0) {
            int n3 = this.blen - this.boff;
            if (n3 == 0) {
                if (this.eof) {
                    if (n2 > 0) {
                        return n2;
                    }
                    this.close();
                    return -1;
                }
                else {
                    this.fillBuffer(0);
                }
            }
            else {
                if (i < n3) {
                    n3 = i;
                }
                System.arraycopy(this.buffer, this.boff, array, n, n3);
                this.boff += n3;
                i -= n3;
                n2 += n3;
                n += n3;
            }
        }
        return n2;
    }
    
    private void fillBuffer(int headerLength) throws IOException {
        if (headerLength == Integer.MIN_VALUE) {
            if (this.hdrGen.expectsCharCount() && this.valueLength >= 0) {
                this.headerLength = this.hdrGen.generateInto(this.buffer, 0, this.valueLength);
            }
            else {
                this.headerLength = this.hdrGen.generateInto(this.buffer, 0, -1L);
            }
            headerLength = this.headerLength;
        }
        int i = headerLength;
        this.boff = 0;
        if (i == 0) {
            this.multipleBuffer = true;
        }
        if (this.mark >= 0) {
            final int n = this.readAheadLimit + 6;
            if (this.mark + n > this.buffer.length) {
                if (this.blen != -1) {
                    i = (this.boff = this.blen - this.mark);
                }
                final byte[] buffer = this.buffer;
                if (n > this.buffer.length) {
                    this.buffer = new byte[n];
                }
                System.arraycopy(buffer, this.mark, this.buffer, 0, i);
                this.mark = 0;
            }
            else if (this.blen != -1) {
                this.mark = -1;
            }
        }
        while (i <= this.buffer.length - 6) {
            final int read = this.reader.read();
            if (read < 0) {
                this.eof = true;
                break;
            }
            ++this.charCount;
            if (read >= 1 && read <= 127) {
                this.buffer[i++] = (byte)read;
            }
            else if (read > 2047) {
                this.buffer[i++] = (byte)(0xE0 | (read >> 12 & 0xF));
                this.buffer[i++] = (byte)(0x80 | (read >> 6 & 0x3F));
                this.buffer[i++] = (byte)(0x80 | (read >> 0 & 0x3F));
            }
            else {
                this.buffer[i++] = (byte)(0xC0 | (read >> 6 & 0x1F));
                this.buffer[i++] = (byte)(0x80 | (read >> 0 & 0x3F));
            }
        }
        this.blen = i;
        if (this.eof) {
            this.checkSufficientData();
        }
    }
    
    private void checkSufficientData() throws IOException {
        if (this.charsToTruncate > 0) {
            this.reader.setLimit(this.charsToTruncate);
            this.truncate();
        }
        final int clearLimit = this.reader.clearLimit();
        if (clearLimit > 0 && this.valueLength > 0) {
            throw new DerbyIOException(MessageService.getTextMessage("XJ023.S"), "XJ023.S");
        }
        if (clearLimit == 0 && this.reader.read() >= 0) {
            if (this.valueLength > -1) {
                throw new DerbyIOException(MessageService.getTextMessage("XJ023.S"), "XJ023.S");
            }
            if (!this.canTruncate()) {
                throw new DerbyIOException(MessageService.getTextMessage("22001", this.typeName, "<stream-value>", String.valueOf(Math.abs(this.valueLength))), "22001");
            }
            this.truncate();
        }
        if (!this.multipleBuffer) {
            int charCount;
            if (this.hdrGen.expectsCharCount()) {
                charCount = this.charCount;
            }
            else {
                charCount = this.blen - this.headerLength;
            }
            if (this.hdrGen.generateInto(this.buffer, 0, charCount) != this.headerLength) {
                throw new IOException("Data corruption detected; user data overwritten by header bytes");
            }
            this.blen += this.hdrGen.writeEOF(this.buffer, this.blen, charCount);
        }
        else {
            this.blen += this.hdrGen.writeEOF(this.buffer, this.blen, Math.max(this.valueLength, -1));
        }
    }
    
    private boolean canTruncate() {
        return this.typeName.equals("CLOB") || this.typeName.equals("VARCHAR") || this.typeName.equals("CHAR");
    }
    
    private void truncate() throws IOException {
        int i;
        do {
            i = this.reader.read();
            if (i < 0) {
                return;
            }
        } while (i == 32);
        throw new DerbyIOException(MessageService.getTextMessage("22001", this.typeName, "<stream-value>", String.valueOf(Math.abs(this.valueLength))), "22001");
    }
    
    public void close() {
        this.buffer = null;
    }
    
    public final int available() {
        final int limit = this.reader.getLimit();
        return (this.buffer.length > limit) ? limit : this.buffer.length;
    }
    
    public void mark(final int readAheadLimit) {
        if (readAheadLimit > 0) {
            this.readAheadLimit = readAheadLimit;
            this.mark = this.boff;
        }
        else {
            final int n = -1;
            this.mark = n;
            this.readAheadLimit = n;
        }
    }
    
    public void reset() throws IOException {
        if (this.buffer == null) {
            throw new EOFException(MessageService.getTextMessage("XJ085.S"));
        }
        if (this.mark == -1) {
            throw new IOException(MessageService.getTextMessage("I027"));
        }
        this.boff = this.mark;
        final int n = -1;
        this.mark = n;
        this.readAheadLimit = n;
    }
    
    public boolean markSupported() {
        return true;
    }
}
