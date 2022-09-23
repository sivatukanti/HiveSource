// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.io.DerbyIOException;
import java.io.IOException;
import java.io.EOFException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.io.InputStream;
import org.apache.derby.iapi.services.io.LimitInputStream;

public final class RawToBinaryFormatStream extends LimitInputStream
{
    private int encodedOffset;
    private byte[] encodedLength;
    private boolean eof;
    private final int length;
    private final int maximumLength;
    private final String typeName;
    
    public RawToBinaryFormatStream(final InputStream inputStream, final int limit) {
        super(inputStream);
        this.eof = false;
        if (limit < 0) {
            throw new IllegalArgumentException("Stream length cannot be negative: " + limit);
        }
        this.length = limit;
        this.maximumLength = -1;
        this.typeName = null;
        this.setLimit(limit);
        if (limit <= 31) {
            (this.encodedLength = new byte[1])[0] = (byte)(0x80 | (limit & 0xFF));
        }
        else if (limit <= 65535) {
            (this.encodedLength = new byte[3])[0] = -96;
            this.encodedLength[1] = (byte)(limit >> 8);
            this.encodedLength[2] = (byte)limit;
        }
        else {
            (this.encodedLength = new byte[5])[0] = -64;
            this.encodedLength[1] = (byte)(limit >> 24);
            this.encodedLength[2] = (byte)(limit >> 16);
            this.encodedLength[3] = (byte)(limit >> 8);
            this.encodedLength[4] = (byte)limit;
        }
    }
    
    public RawToBinaryFormatStream(final InputStream inputStream, final int limit, final String typeName) {
        super(inputStream);
        this.eof = false;
        if (limit < 0) {
            throw new IllegalArgumentException("Maximum length for a capped stream cannot be negative: " + limit);
        }
        if (typeName == null) {
            throw new IllegalArgumentException("Type name cannot be null");
        }
        this.length = -1;
        this.maximumLength = limit;
        this.typeName = typeName;
        this.encodedLength = new byte[4];
        this.setLimit(limit);
    }
    
    public int read() throws IOException {
        if (this.eof) {
            throw new EOFException(MessageService.getTextMessage("XJ085.S"));
        }
        if (this.encodedOffset < this.encodedLength.length) {
            return this.encodedLength[this.encodedOffset++] & 0xFF;
        }
        final int read = super.read();
        if (read == -1) {
            this.checkSufficientData();
        }
        return read;
    }
    
    private void checkSufficientData() throws IOException {
        this.eof = true;
        if (!this.limitInPlace) {
            return;
        }
        final int clearLimit = this.clearLimit();
        if (this.length > -1 && clearLimit > 0) {
            throw new DerbyIOException(MessageService.getTextMessage("XJ023.S"), "XJ023.S");
        }
        if (clearLimit == 0) {
            int read;
            try {
                read = super.read();
            }
            catch (IOException ex) {
                read = -1;
            }
            if (read != -1) {
                if (this.length > -1) {
                    throw new DerbyIOException(MessageService.getTextMessage("XJ023.S"), "XJ023.S");
                }
                throw new DerbyIOException(MessageService.getTextMessage("22001", this.typeName, "XXXX", String.valueOf(this.maximumLength)), "22001");
            }
        }
    }
    
    public int read(final byte[] array, int n, int n2) throws IOException {
        if (this.eof) {
            throw new EOFException(MessageService.getTextMessage("XJ085.S"));
        }
        int n3 = this.encodedLength.length - this.encodedOffset;
        if (n3 != 0) {
            if (n2 < n3) {
                n3 = n2;
            }
            System.arraycopy(this.encodedLength, this.encodedOffset, array, n, n3);
            this.encodedOffset += n3;
            n += n3;
            n2 -= n3;
            if (n2 == 0) {
                return n3;
            }
        }
        final int read = super.read(array, n, n2);
        if (read >= 0) {
            return n3 + read;
        }
        if (n3 != 0) {
            return n3;
        }
        this.checkSufficientData();
        return read;
    }
}
