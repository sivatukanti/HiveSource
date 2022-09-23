// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.archivers.cpio;

import org.apache.commons.compress.archivers.ArchiveEntry;
import java.io.EOFException;
import org.apache.commons.compress.utils.ArchiveUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class CpioArchiveInputStream extends ArchiveInputStream implements CpioConstants
{
    private boolean closed;
    private CpioArchiveEntry entry;
    private long entryBytesRead;
    private boolean entryEOF;
    private final byte[] tmpbuf;
    private long crc;
    private final InputStream in;
    
    public CpioArchiveInputStream(final InputStream in) {
        this.closed = false;
        this.entryBytesRead = 0L;
        this.entryEOF = false;
        this.tmpbuf = new byte[4096];
        this.crc = 0L;
        this.in = in;
    }
    
    @Override
    public int available() throws IOException {
        this.ensureOpen();
        if (this.entryEOF) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.in.close();
            this.closed = true;
        }
    }
    
    private void closeEntry() throws IOException {
        this.ensureOpen();
        while (this.read(this.tmpbuf, 0, this.tmpbuf.length) != -1) {}
        this.entryEOF = true;
    }
    
    private void ensureOpen() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }
    
    public CpioArchiveEntry getNextCPIOEntry() throws IOException {
        this.ensureOpen();
        if (this.entry != null) {
            this.closeEntry();
        }
        final byte[] magic = new byte[2];
        this.readFully(magic, 0, magic.length);
        if (CpioUtil.byteArray2long(magic, false) == 29127L) {
            this.entry = this.readOldBinaryEntry(false);
        }
        else if (CpioUtil.byteArray2long(magic, true) == 29127L) {
            this.entry = this.readOldBinaryEntry(true);
        }
        else {
            final byte[] more_magic = new byte[4];
            this.readFully(more_magic, 0, more_magic.length);
            final byte[] tmp = new byte[6];
            System.arraycopy(magic, 0, tmp, 0, magic.length);
            System.arraycopy(more_magic, 0, tmp, magic.length, more_magic.length);
            final String magicString = ArchiveUtils.toAsciiString(tmp);
            if (magicString.equals("070701")) {
                this.entry = this.readNewEntry(false);
            }
            else if (magicString.equals("070702")) {
                this.entry = this.readNewEntry(true);
            }
            else {
                if (!magicString.equals("070707")) {
                    throw new IOException("Unknown magic [" + magicString + "]. Occured at byte: " + this.getBytesRead());
                }
                this.entry = this.readOldAsciiEntry();
            }
        }
        this.entryBytesRead = 0L;
        this.entryEOF = false;
        this.crc = 0L;
        if (this.entry.getName().equals("TRAILER!!!")) {
            this.entryEOF = true;
            return null;
        }
        return this.entry;
    }
    
    private void skip(final int bytes) throws IOException {
        final byte[] buff = new byte[4];
        if (bytes > 0) {
            this.readFully(buff, 0, bytes);
        }
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.ensureOpen();
        if (off < 0 || len < 0 || off > b.length - len) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.entry == null || this.entryEOF) {
            return -1;
        }
        if (this.entryBytesRead == this.entry.getSize()) {
            this.skip(this.entry.getDataPadCount());
            this.entryEOF = true;
            if (this.entry.getFormat() == 2 && this.crc != this.entry.getChksum()) {
                throw new IOException("CRC Error. Occured at byte: " + this.getBytesRead());
            }
            return -1;
        }
        else {
            final int tmplength = (int)Math.min(len, this.entry.getSize() - this.entryBytesRead);
            if (tmplength < 0) {
                return -1;
            }
            final int tmpread = this.readFully(b, off, tmplength);
            if (this.entry.getFormat() == 2) {
                for (int pos = 0; pos < tmpread; ++pos) {
                    this.crc += (b[pos] & 0xFF);
                }
            }
            this.entryBytesRead += tmpread;
            return tmpread;
        }
    }
    
    private final int readFully(final byte[] b, final int off, final int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n;
        int count;
        for (n = 0; n < len; n += count) {
            count = this.in.read(b, off + n, len - n);
            this.count(count);
            if (count < 0) {
                throw new EOFException();
            }
        }
        return n;
    }
    
    private long readBinaryLong(final int length, final boolean swapHalfWord) throws IOException {
        final byte[] tmp = new byte[length];
        this.readFully(tmp, 0, tmp.length);
        return CpioUtil.byteArray2long(tmp, swapHalfWord);
    }
    
    private long readAsciiLong(final int length, final int radix) throws IOException {
        final byte[] tmpBuffer = new byte[length];
        this.readFully(tmpBuffer, 0, tmpBuffer.length);
        return Long.parseLong(ArchiveUtils.toAsciiString(tmpBuffer), radix);
    }
    
    private CpioArchiveEntry readNewEntry(final boolean hasCrc) throws IOException {
        CpioArchiveEntry ret;
        if (hasCrc) {
            ret = new CpioArchiveEntry((short)2);
        }
        else {
            ret = new CpioArchiveEntry((short)1);
        }
        ret.setInode(this.readAsciiLong(8, 16));
        final long mode = this.readAsciiLong(8, 16);
        if (mode != 0L) {
            ret.setMode(mode);
        }
        ret.setUID(this.readAsciiLong(8, 16));
        ret.setGID(this.readAsciiLong(8, 16));
        ret.setNumberOfLinks(this.readAsciiLong(8, 16));
        ret.setTime(this.readAsciiLong(8, 16));
        ret.setSize(this.readAsciiLong(8, 16));
        ret.setDeviceMaj(this.readAsciiLong(8, 16));
        ret.setDeviceMin(this.readAsciiLong(8, 16));
        ret.setRemoteDeviceMaj(this.readAsciiLong(8, 16));
        ret.setRemoteDeviceMin(this.readAsciiLong(8, 16));
        final long namesize = this.readAsciiLong(8, 16);
        ret.setChksum(this.readAsciiLong(8, 16));
        final String name = this.readCString((int)namesize);
        ret.setName(name);
        if (mode == 0L && !name.equals("TRAILER!!!")) {
            throw new IOException("Mode 0 only allowed in the trailer. Found entry name: " + name + " Occured at byte: " + this.getBytesRead());
        }
        this.skip(ret.getHeaderPadCount());
        return ret;
    }
    
    private CpioArchiveEntry readOldAsciiEntry() throws IOException {
        final CpioArchiveEntry ret = new CpioArchiveEntry((short)4);
        ret.setDevice(this.readAsciiLong(6, 8));
        ret.setInode(this.readAsciiLong(6, 8));
        final long mode = this.readAsciiLong(6, 8);
        if (mode != 0L) {
            ret.setMode(mode);
        }
        ret.setUID(this.readAsciiLong(6, 8));
        ret.setGID(this.readAsciiLong(6, 8));
        ret.setNumberOfLinks(this.readAsciiLong(6, 8));
        ret.setRemoteDevice(this.readAsciiLong(6, 8));
        ret.setTime(this.readAsciiLong(11, 8));
        final long namesize = this.readAsciiLong(6, 8);
        ret.setSize(this.readAsciiLong(11, 8));
        final String name = this.readCString((int)namesize);
        ret.setName(name);
        if (mode == 0L && !name.equals("TRAILER!!!")) {
            throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + name + " Occured at byte: " + this.getBytesRead());
        }
        return ret;
    }
    
    private CpioArchiveEntry readOldBinaryEntry(final boolean swapHalfWord) throws IOException {
        final CpioArchiveEntry ret = new CpioArchiveEntry((short)8);
        ret.setDevice(this.readBinaryLong(2, swapHalfWord));
        ret.setInode(this.readBinaryLong(2, swapHalfWord));
        final long mode = this.readBinaryLong(2, swapHalfWord);
        if (mode != 0L) {
            ret.setMode(mode);
        }
        ret.setUID(this.readBinaryLong(2, swapHalfWord));
        ret.setGID(this.readBinaryLong(2, swapHalfWord));
        ret.setNumberOfLinks(this.readBinaryLong(2, swapHalfWord));
        ret.setRemoteDevice(this.readBinaryLong(2, swapHalfWord));
        ret.setTime(this.readBinaryLong(4, swapHalfWord));
        final long namesize = this.readBinaryLong(2, swapHalfWord);
        ret.setSize(this.readBinaryLong(4, swapHalfWord));
        final String name = this.readCString((int)namesize);
        ret.setName(name);
        if (mode == 0L && !name.equals("TRAILER!!!")) {
            throw new IOException("Mode 0 only allowed in the trailer. Found entry: " + name + "Occured at byte: " + this.getBytesRead());
        }
        this.skip(ret.getHeaderPadCount());
        return ret;
    }
    
    private String readCString(final int length) throws IOException {
        final byte[] tmpBuffer = new byte[length];
        this.readFully(tmpBuffer, 0, tmpBuffer.length);
        return new String(tmpBuffer, 0, tmpBuffer.length - 1);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("negative skip length");
        }
        this.ensureOpen();
        int max;
        int total;
        int len;
        for (max = (int)Math.min(n, 2147483647L), total = 0; total < max; total += len) {
            len = max - total;
            if (len > this.tmpbuf.length) {
                len = this.tmpbuf.length;
            }
            len = this.read(this.tmpbuf, 0, len);
            if (len == -1) {
                this.entryEOF = true;
                break;
            }
        }
        return total;
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextCPIOEntry();
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 6 && ((signature[0] == 113 && (signature[1] & 0xFF) == 0xC7) || (signature[1] == 113 && (signature[0] & 0xFF) == 0xC7) || (signature[0] == 48 && signature[1] == 55 && signature[2] == 48 && signature[3] == 55 && signature[4] == 48 && (signature[5] == 49 || signature[5] == 50 || signature[5] == 55)));
    }
}
