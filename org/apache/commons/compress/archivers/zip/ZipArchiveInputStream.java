// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.io.IOException;
import java.io.EOFException;
import java.io.PushbackInputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class ZipArchiveInputStream extends ArchiveInputStream
{
    private final ZipEncoding zipEncoding;
    private final boolean useUnicodeExtraFields;
    private final InputStream in;
    private final Inflater inf;
    private final CRC32 crc;
    private final Buffer buf;
    private CurrentEntry current;
    private boolean closed;
    private boolean hitCentralDirectory;
    private ByteArrayInputStream lastStoredEntry;
    private boolean allowStoredEntriesWithDataDescriptor;
    private static final int LFH_LEN = 30;
    private static final long TWO_EXP_32 = 4294967296L;
    private static final byte[] LFH;
    private static final byte[] CFH;
    private static final byte[] DD;
    
    public ZipArchiveInputStream(final InputStream inputStream) {
        this(inputStream, "UTF8", true);
    }
    
    public ZipArchiveInputStream(final InputStream inputStream, final String encoding, final boolean useUnicodeExtraFields) {
        this(inputStream, encoding, useUnicodeExtraFields, false);
    }
    
    public ZipArchiveInputStream(final InputStream inputStream, final String encoding, final boolean useUnicodeExtraFields, final boolean allowStoredEntriesWithDataDescriptor) {
        this.inf = new Inflater(true);
        this.crc = new CRC32();
        this.buf = new Buffer();
        this.current = null;
        this.closed = false;
        this.hitCentralDirectory = false;
        this.lastStoredEntry = null;
        this.allowStoredEntriesWithDataDescriptor = false;
        this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
        this.useUnicodeExtraFields = useUnicodeExtraFields;
        this.in = new PushbackInputStream(inputStream, this.buf.buf.length);
        this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor;
    }
    
    public ZipArchiveEntry getNextZipEntry() throws IOException {
        if (this.closed || this.hitCentralDirectory) {
            return null;
        }
        if (this.current != null) {
            this.closeEntry();
        }
        final byte[] lfh = new byte[30];
        try {
            this.readFully(lfh);
        }
        catch (EOFException e) {
            return null;
        }
        final ZipLong sig = new ZipLong(lfh);
        if (sig.equals(ZipLong.CFH_SIG)) {
            this.hitCentralDirectory = true;
            return null;
        }
        if (!sig.equals(ZipLong.LFH_SIG)) {
            return null;
        }
        int off = 4;
        this.current = new CurrentEntry();
        final int versionMadeBy = ZipShort.getValue(lfh, off);
        off += 2;
        this.current.entry.setPlatform(versionMadeBy >> 8 & 0xF);
        final GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(lfh, off);
        final boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
        final ZipEncoding entryEncoding = hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : this.zipEncoding;
        this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
        this.current.entry.setGeneralPurposeBit(gpFlag);
        off += 2;
        this.current.entry.setMethod(ZipShort.getValue(lfh, off));
        off += 2;
        final long time = ZipUtil.dosToJavaTime(ZipLong.getValue(lfh, off));
        this.current.entry.setTime(time);
        off += 4;
        ZipLong size = null;
        ZipLong cSize = null;
        if (!this.current.hasDataDescriptor) {
            this.current.entry.setCrc(ZipLong.getValue(lfh, off));
            off += 4;
            cSize = new ZipLong(lfh, off);
            off += 4;
            size = new ZipLong(lfh, off);
            off += 4;
        }
        else {
            off += 12;
        }
        final int fileNameLen = ZipShort.getValue(lfh, off);
        off += 2;
        final int extraLen = ZipShort.getValue(lfh, off);
        off += 2;
        final byte[] fileName = new byte[fileNameLen];
        this.readFully(fileName);
        this.current.entry.setName(entryEncoding.decode(fileName), fileName);
        final byte[] extraData = new byte[extraLen];
        this.readFully(extraData);
        this.current.entry.setExtra(extraData);
        if (!hasUTF8Flag && this.useUnicodeExtraFields) {
            ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, null);
        }
        this.processZip64Extra(size, cSize);
        return this.current.entry;
    }
    
    private void processZip64Extra(final ZipLong size, final ZipLong cSize) {
        final Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
        this.current.usesZip64 = (z64 != null);
        if (!this.current.hasDataDescriptor) {
            if (this.current.usesZip64 && (cSize.equals(ZipLong.ZIP64_MAGIC) || size.equals(ZipLong.ZIP64_MAGIC))) {
                this.current.entry.setCompressedSize(z64.getCompressedSize().getLongValue());
                this.current.entry.setSize(z64.getSize().getLongValue());
            }
            else {
                this.current.entry.setCompressedSize(cSize.getValue());
                this.current.entry.setSize(size.getValue());
            }
        }
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextZipEntry();
    }
    
    @Override
    public boolean canReadEntryData(final ArchiveEntry ae) {
        if (ae instanceof ZipArchiveEntry) {
            final ZipArchiveEntry ze = (ZipArchiveEntry)ae;
            return ZipUtil.canHandleEntryData(ze) && this.supportsDataDescriptorFor(ze);
        }
        return false;
    }
    
    @Override
    public int read(final byte[] buffer, final int start, final int length) throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        }
        if (this.inf.finished() || this.current == null) {
            return -1;
        }
        if (start > buffer.length || length < 0 || start < 0 || buffer.length - start < length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        ZipUtil.checkRequestedFeatures(this.current.entry);
        if (!this.supportsDataDescriptorFor(this.current.entry)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.DATA_DESCRIPTOR, this.current.entry);
        }
        if (this.current.entry.getMethod() == 0) {
            return this.readStored(buffer, start, length);
        }
        return this.readDeflated(buffer, start, length);
    }
    
    private int readStored(final byte[] buffer, final int start, final int length) throws IOException {
        if (this.current.hasDataDescriptor) {
            if (this.lastStoredEntry == null) {
                this.readStoredEntry();
            }
            return this.lastStoredEntry.read(buffer, start, length);
        }
        final long csize = this.current.entry.getSize();
        if (this.current.bytesRead >= csize) {
            return -1;
        }
        if (this.buf.offsetInBuffer >= this.buf.lengthOfLastRead) {
            this.buf.offsetInBuffer = 0;
            if ((this.buf.lengthOfLastRead = this.in.read(this.buf.buf)) == -1) {
                return -1;
            }
            this.count(this.buf.lengthOfLastRead);
            this.current.bytesReadFromStream += this.buf.lengthOfLastRead;
        }
        int toRead = (length > this.buf.lengthOfLastRead) ? (this.buf.lengthOfLastRead - this.buf.offsetInBuffer) : length;
        if (csize - this.current.bytesRead < toRead) {
            toRead = (int)(csize - this.current.bytesRead);
        }
        System.arraycopy(this.buf.buf, this.buf.offsetInBuffer, buffer, start, toRead);
        this.buf.offsetInBuffer += toRead;
        this.current.bytesRead += toRead;
        this.crc.update(buffer, start, toRead);
        return toRead;
    }
    
    private int readDeflated(final byte[] buffer, final int start, final int length) throws IOException {
        if (this.inf.needsInput()) {
            this.fill();
            if (this.buf.lengthOfLastRead > 0) {
                this.current.bytesReadFromStream += this.buf.lengthOfLastRead;
            }
        }
        int read = 0;
        try {
            read = this.inf.inflate(buffer, start, length);
        }
        catch (DataFormatException e) {
            throw new ZipException(e.getMessage());
        }
        if (read == 0) {
            if (this.inf.finished()) {
                return -1;
            }
            if (this.buf.lengthOfLastRead == -1) {
                throw new IOException("Truncated ZIP file");
            }
        }
        this.crc.update(buffer, start, read);
        return read;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.in.close();
            this.inf.end();
        }
    }
    
    @Override
    public long skip(final long value) throws IOException {
        if (value >= 0L) {
            long skipped = 0L;
            final byte[] b = new byte[1024];
            while (skipped < value) {
                final long rem = value - skipped;
                final int x = this.read(b, 0, (int)((b.length > rem) ? rem : b.length));
                if (x == -1) {
                    return skipped;
                }
                skipped += x;
            }
            return skipped;
        }
        throw new IllegalArgumentException();
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= ZipArchiveOutputStream.LFH_SIG.length && (checksig(signature, ZipArchiveOutputStream.LFH_SIG) || checksig(signature, ZipArchiveOutputStream.EOCD_SIG));
    }
    
    private static boolean checksig(final byte[] signature, final byte[] expected) {
        for (int i = 0; i < expected.length; ++i) {
            if (signature[i] != expected[i]) {
                return false;
            }
        }
        return true;
    }
    
    private void closeEntry() throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        }
        if (this.current == null) {
            return;
        }
        if (this.current.bytesReadFromStream <= this.current.entry.getCompressedSize() && !this.current.hasDataDescriptor) {
            this.drainCurrentEntryData();
        }
        else {
            this.skip(Long.MAX_VALUE);
            final long inB = (this.current.entry.getMethod() == 8) ? this.getBytesInflated() : this.current.bytesRead;
            final int diff = (int)(this.current.bytesReadFromStream - inB);
            if (diff > 0) {
                this.pushback(this.buf.buf, this.buf.lengthOfLastRead - diff, diff);
            }
        }
        if (this.lastStoredEntry == null && this.current.hasDataDescriptor) {
            this.readDataDescriptor();
        }
        this.inf.reset();
        this.buf.reset();
        this.crc.reset();
        this.current = null;
        this.lastStoredEntry = null;
    }
    
    private void drainCurrentEntryData() throws IOException {
        long n;
        for (long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream; remaining > 0L; remaining -= n) {
            n = this.in.read(this.buf.buf, 0, (int)Math.min(this.buf.buf.length, remaining));
            if (n < 0L) {
                throw new EOFException("Truncated ZIP entry: " + this.current.entry.getName());
            }
            this.count(n);
        }
    }
    
    private long getBytesInflated() {
        long inB = this.inf.getBytesRead();
        if (this.current.bytesReadFromStream >= 4294967296L) {
            while (inB + 4294967296L <= this.current.bytesReadFromStream) {
                inB += 4294967296L;
            }
        }
        return inB;
    }
    
    private void fill() throws IOException {
        if (this.closed) {
            throw new IOException("The stream is closed");
        }
        if ((this.buf.lengthOfLastRead = this.in.read(this.buf.buf)) > 0) {
            this.count(this.buf.lengthOfLastRead);
            this.inf.setInput(this.buf.buf, 0, this.buf.lengthOfLastRead);
        }
    }
    
    private void readFully(final byte[] b) throws IOException {
        int count = 0;
        int x = 0;
        while (count != b.length) {
            count += (x = this.in.read(b, count, b.length - count));
            if (x == -1) {
                throw new EOFException();
            }
            this.count(x);
        }
    }
    
    private void readDataDescriptor() throws IOException {
        byte[] b = new byte[4];
        this.readFully(b);
        ZipLong val = new ZipLong(b);
        if (ZipLong.DD_SIG.equals(val)) {
            this.readFully(b);
            val = new ZipLong(b);
        }
        this.current.entry.setCrc(val.getValue());
        b = new byte[16];
        this.readFully(b);
        final ZipLong potentialSig = new ZipLong(b, 8);
        if (potentialSig.equals(ZipLong.CFH_SIG) || potentialSig.equals(ZipLong.LFH_SIG)) {
            this.pushback(b, 8, 8);
            this.current.entry.setCompressedSize(ZipLong.getValue(b));
            this.current.entry.setSize(ZipLong.getValue(b, 4));
        }
        else {
            this.current.entry.setCompressedSize(ZipEightByteInteger.getLongValue(b));
            this.current.entry.setSize(ZipEightByteInteger.getLongValue(b, 8));
        }
    }
    
    private boolean supportsDataDescriptorFor(final ZipArchiveEntry entry) {
        return this.allowStoredEntriesWithDataDescriptor || !entry.getGeneralPurposeBit().usesDataDescriptor() || entry.getMethod() == 8;
    }
    
    private void readStoredEntry() throws IOException {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int off = 0;
        boolean done = false;
        final int ddLen = this.current.usesZip64 ? 20 : 12;
        while (!done) {
            final int r = this.in.read(this.buf.buf, off, 512 - off);
            if (r <= 0) {
                throw new IOException("Truncated ZIP file");
            }
            if (r + off < 4) {
                off += r;
            }
            else {
                done = this.bufferContainsSignature(bos, off, r, ddLen);
                if (done) {
                    continue;
                }
                off = this.cacheBytesRead(bos, off, r, ddLen);
            }
        }
        final byte[] b = bos.toByteArray();
        this.lastStoredEntry = new ByteArrayInputStream(b);
    }
    
    private boolean bufferContainsSignature(final ByteArrayOutputStream bos, final int offset, final int lastRead, final int expectedDDLen) throws IOException {
        boolean done = false;
        int readTooMuch = 0;
        for (int i = 0; !done && i < lastRead - 4; ++i) {
            if (this.buf.buf[i] == ZipArchiveInputStream.LFH[0] && this.buf.buf[i + 1] == ZipArchiveInputStream.LFH[1]) {
                if ((this.buf.buf[i + 2] == ZipArchiveInputStream.LFH[2] && this.buf.buf[i + 3] == ZipArchiveInputStream.LFH[3]) || (this.buf.buf[i] == ZipArchiveInputStream.CFH[2] && this.buf.buf[i + 3] == ZipArchiveInputStream.CFH[3])) {
                    readTooMuch = offset + lastRead - i - expectedDDLen;
                    done = true;
                }
                else if (this.buf.buf[i + 2] == ZipArchiveInputStream.DD[2] && this.buf.buf[i + 3] == ZipArchiveInputStream.DD[3]) {
                    readTooMuch = offset + lastRead - i;
                    done = true;
                }
                if (done) {
                    this.pushback(this.buf.buf, offset + lastRead - readTooMuch, readTooMuch);
                    bos.write(this.buf.buf, 0, i);
                    this.readDataDescriptor();
                }
            }
        }
        return done;
    }
    
    private int cacheBytesRead(final ByteArrayOutputStream bos, int offset, final int lastRead, final int expecteDDLen) {
        final int cacheable = offset + lastRead - expecteDDLen - 3;
        if (cacheable > 0) {
            bos.write(this.buf.buf, 0, cacheable);
            System.arraycopy(this.buf.buf, cacheable, this.buf.buf, 0, expecteDDLen + 3);
            offset = expecteDDLen + 3;
        }
        else {
            offset += lastRead;
        }
        return offset;
    }
    
    private void pushback(final byte[] buf, final int offset, final int length) throws IOException {
        ((PushbackInputStream)this.in).unread(buf, offset, length);
        this.pushedBackBytes(length);
    }
    
    static {
        LFH = ZipLong.LFH_SIG.getBytes();
        CFH = ZipLong.CFH_SIG.getBytes();
        DD = ZipLong.DD_SIG.getBytes();
    }
    
    private static final class CurrentEntry
    {
        private final ZipArchiveEntry entry;
        private boolean hasDataDescriptor;
        private boolean usesZip64;
        private long bytesRead;
        private long bytesReadFromStream;
        
        private CurrentEntry() {
            this.entry = new ZipArchiveEntry();
        }
    }
    
    private static final class Buffer
    {
        private final byte[] buf;
        private int offsetInBuffer;
        private int lengthOfLastRead;
        
        private Buffer() {
            this.buf = new byte[512];
            this.offsetInBuffer = 0;
            this.lengthOfLastRead = 0;
        }
        
        private void reset() {
            final int n = 0;
            this.lengthOfLastRead = n;
            this.offsetInBuffer = n;
        }
    }
}
