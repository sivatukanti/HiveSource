// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.compress.archivers.tar;

import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.ArchiveInputStream;

public class TarArchiveInputStream extends ArchiveInputStream
{
    private static final int SMALL_BUFFER_SIZE = 256;
    private static final int BUFFER_SIZE = 8192;
    private boolean hasHitEOF;
    private long entrySize;
    private long entryOffset;
    private byte[] readBuf;
    protected final TarBuffer buffer;
    private TarArchiveEntry currEntry;
    private final ZipEncoding encoding;
    
    public TarArchiveInputStream(final InputStream is) {
        this(is, 10240, 512);
    }
    
    public TarArchiveInputStream(final InputStream is, final String encoding) {
        this(is, 10240, 512, encoding);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize) {
        this(is, blockSize, 512);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final String encoding) {
        this(is, blockSize, 512, encoding);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize) {
        this(is, blockSize, recordSize, null);
    }
    
    public TarArchiveInputStream(final InputStream is, final int blockSize, final int recordSize, final String encoding) {
        this.buffer = new TarBuffer(is, blockSize, recordSize);
        this.readBuf = null;
        this.hasHitEOF = false;
        this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
    }
    
    @Override
    public void close() throws IOException {
        this.buffer.close();
    }
    
    public int getRecordSize() {
        return this.buffer.getRecordSize();
    }
    
    @Override
    public int available() throws IOException {
        if (this.entrySize - this.entryOffset > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)(this.entrySize - this.entryOffset);
    }
    
    @Override
    public long skip(final long numToSkip) throws IOException {
        final byte[] skipBuf = new byte[8192];
        long skip;
        int numRead;
        for (skip = numToSkip; skip > 0L; skip -= numRead) {
            final int realSkip = (int)((skip > skipBuf.length) ? skipBuf.length : skip);
            numRead = this.read(skipBuf, 0, realSkip);
            if (numRead == -1) {
                break;
            }
        }
        return numToSkip - skip;
    }
    
    @Override
    public synchronized void reset() {
    }
    
    public TarArchiveEntry getNextTarEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            long skipped;
            for (long numToSkip = this.entrySize - this.entryOffset; numToSkip > 0L; numToSkip -= skipped) {
                skipped = this.skip(numToSkip);
                if (skipped <= 0L) {
                    throw new RuntimeException("failed to skip current tar entry");
                }
            }
            this.readBuf = null;
        }
        final byte[] headerBuf = this.getRecord();
        if (this.hasHitEOF) {
            return this.currEntry = null;
        }
        try {
            this.currEntry = new TarArchiveEntry(headerBuf, this.encoding);
        }
        catch (IllegalArgumentException e) {
            final IOException ioe = new IOException("Error detected parsing the header");
            ioe.initCause(e);
            throw ioe;
        }
        this.entryOffset = 0L;
        this.entrySize = this.currEntry.getSize();
        if (this.currEntry.isGNULongNameEntry()) {
            final StringBuffer longName = new StringBuffer();
            final byte[] buf = new byte[256];
            int length = 0;
            while ((length = this.read(buf)) >= 0) {
                longName.append(new String(buf, 0, length));
            }
            this.getNextEntry();
            if (this.currEntry == null) {
                return null;
            }
            if (longName.length() > 0 && longName.charAt(longName.length() - 1) == '\0') {
                longName.deleteCharAt(longName.length() - 1);
            }
            this.currEntry.setName(longName.toString());
        }
        if (this.currEntry.isPaxHeader()) {
            this.paxHeaders();
        }
        if (this.currEntry.isGNUSparse()) {
            this.readGNUSparse();
        }
        this.entrySize = this.currEntry.getSize();
        return this.currEntry;
    }
    
    private byte[] getRecord() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        final byte[] headerBuf = this.buffer.readRecord();
        if (headerBuf == null) {
            this.hasHitEOF = true;
        }
        else if (this.buffer.isEOFRecord(headerBuf)) {
            this.hasHitEOF = true;
        }
        return (byte[])(this.hasHitEOF ? null : headerBuf);
    }
    
    private void paxHeaders() throws IOException {
        final Map<String, String> headers = this.parsePaxHeaders(this);
        this.getNextEntry();
        this.applyPaxHeadersToCurrentEntry(headers);
    }
    
    Map<String, String> parsePaxHeaders(final InputStream i) throws IOException {
        final Map<String, String> headers = new HashMap<String, String>();
        while (true) {
            int len = 0;
            int read = 0;
            int ch;
            while ((ch = i.read()) != -1) {
                ++read;
                if (ch == 32) {
                    final ByteArrayOutputStream coll = new ByteArrayOutputStream();
                    while ((ch = i.read()) != -1) {
                        ++read;
                        if (ch == 61) {
                            final String keyword = coll.toString("UTF-8");
                            final byte[] rest = new byte[len - read];
                            final int got = i.read(rest);
                            if (got != len - read) {
                                throw new IOException("Failed to read Paxheader. Expected " + (len - read) + " bytes, read " + got);
                            }
                            final String value = new String(rest, 0, len - read - 1, "UTF-8");
                            headers.put(keyword, value);
                            break;
                        }
                        else {
                            coll.write((byte)ch);
                        }
                    }
                    break;
                }
                len *= 10;
                len += ch - 48;
            }
            if (ch == -1) {
                return headers;
            }
        }
    }
    
    private void applyPaxHeadersToCurrentEntry(final Map<String, String> headers) {
        for (final Map.Entry<String, String> ent : headers.entrySet()) {
            final String key = ent.getKey();
            final String val = ent.getValue();
            if ("path".equals(key)) {
                this.currEntry.setName(val);
            }
            else if ("linkpath".equals(key)) {
                this.currEntry.setLinkName(val);
            }
            else if ("gid".equals(key)) {
                this.currEntry.setGroupId(Integer.parseInt(val));
            }
            else if ("gname".equals(key)) {
                this.currEntry.setGroupName(val);
            }
            else if ("uid".equals(key)) {
                this.currEntry.setUserId(Integer.parseInt(val));
            }
            else if ("uname".equals(key)) {
                this.currEntry.setUserName(val);
            }
            else if ("size".equals(key)) {
                this.currEntry.setSize(Long.parseLong(val));
            }
            else if ("mtime".equals(key)) {
                this.currEntry.setModTime((long)(Double.parseDouble(val) * 1000.0));
            }
            else if ("SCHILY.devminor".equals(key)) {
                this.currEntry.setDevMinor(Integer.parseInt(val));
            }
            else {
                if (!"SCHILY.devmajor".equals(key)) {
                    continue;
                }
                this.currEntry.setDevMajor(Integer.parseInt(val));
            }
        }
    }
    
    private void readGNUSparse() throws IOException {
        if (this.currEntry.isExtended()) {
            TarArchiveSparseEntry entry;
            do {
                final byte[] headerBuf = this.getRecord();
                if (this.hasHitEOF) {
                    this.currEntry = null;
                    break;
                }
                entry = new TarArchiveSparseEntry(headerBuf);
            } while (entry.isExtended());
        }
    }
    
    @Override
    public ArchiveEntry getNextEntry() throws IOException {
        return this.getNextTarEntry();
    }
    
    @Override
    public int read(final byte[] buf, int offset, int numToRead) throws IOException {
        int totalRead = 0;
        if (this.entryOffset >= this.entrySize) {
            return -1;
        }
        if (numToRead + this.entryOffset > this.entrySize) {
            numToRead = (int)(this.entrySize - this.entryOffset);
        }
        if (this.readBuf != null) {
            final int sz = (numToRead > this.readBuf.length) ? this.readBuf.length : numToRead;
            System.arraycopy(this.readBuf, 0, buf, offset, sz);
            if (sz >= this.readBuf.length) {
                this.readBuf = null;
            }
            else {
                final int newLen = this.readBuf.length - sz;
                final byte[] newBuf = new byte[newLen];
                System.arraycopy(this.readBuf, sz, newBuf, 0, newLen);
                this.readBuf = newBuf;
            }
            totalRead += sz;
            numToRead -= sz;
            offset += sz;
        }
        while (numToRead > 0) {
            final byte[] rec = this.buffer.readRecord();
            if (rec == null) {
                throw new IOException("unexpected EOF with " + numToRead + " bytes unread. Occured at byte: " + this.getBytesRead());
            }
            this.count(rec.length);
            int sz2 = numToRead;
            final int recLen = rec.length;
            if (recLen > sz2) {
                System.arraycopy(rec, 0, buf, offset, sz2);
                System.arraycopy(rec, sz2, this.readBuf = new byte[recLen - sz2], 0, recLen - sz2);
            }
            else {
                sz2 = recLen;
                System.arraycopy(rec, 0, buf, offset, recLen);
            }
            totalRead += sz2;
            numToRead -= sz2;
            offset += sz2;
        }
        this.entryOffset += totalRead;
        return totalRead;
    }
    
    @Override
    public boolean canReadEntryData(final ArchiveEntry ae) {
        if (ae instanceof TarArchiveEntry) {
            final TarArchiveEntry te = (TarArchiveEntry)ae;
            return !te.isGNUSparse();
        }
        return false;
    }
    
    protected final TarArchiveEntry getCurrentEntry() {
        return this.currEntry;
    }
    
    protected final void setCurrentEntry(final TarArchiveEntry e) {
        this.currEntry = e;
    }
    
    protected final boolean isAtEOF() {
        return this.hasHitEOF;
    }
    
    protected final void setAtEOF(final boolean b) {
        this.hasHitEOF = b;
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 265 && ((ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("00", signature, 263, 2)) || (ArchiveUtils.matchAsciiBuffer("ustar ", signature, 257, 6) && (ArchiveUtils.matchAsciiBuffer(" \u0000", signature, 263, 2) || ArchiveUtils.matchAsciiBuffer("0\u0000", signature, 263, 2))) || (ArchiveUtils.matchAsciiBuffer("ustar\u0000", signature, 257, 6) && ArchiveUtils.matchAsciiBuffer("\u0000\u0000", signature, 263, 2)));
    }
}
