// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.tar;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.tools.zip.ZipEncodingHelper;
import java.io.InputStream;
import org.apache.tools.zip.ZipEncoding;
import java.io.FilterInputStream;

public class TarInputStream extends FilterInputStream
{
    private static final int SMALL_BUFFER_SIZE = 256;
    private static final int BUFFER_SIZE = 8192;
    private static final int LARGE_BUFFER_SIZE = 32768;
    private static final int BYTE_MASK = 255;
    private final byte[] SKIP_BUF;
    private final byte[] SMALL_BUF;
    protected boolean debug;
    protected boolean hasHitEOF;
    protected long entrySize;
    protected long entryOffset;
    protected byte[] readBuf;
    protected TarBuffer buffer;
    protected TarEntry currEntry;
    protected byte[] oneBuf;
    private final ZipEncoding encoding;
    
    public TarInputStream(final InputStream is) {
        this(is, 10240, 512);
    }
    
    public TarInputStream(final InputStream is, final String encoding) {
        this(is, 10240, 512, encoding);
    }
    
    public TarInputStream(final InputStream is, final int blockSize) {
        this(is, blockSize, 512);
    }
    
    public TarInputStream(final InputStream is, final int blockSize, final String encoding) {
        this(is, blockSize, 512, encoding);
    }
    
    public TarInputStream(final InputStream is, final int blockSize, final int recordSize) {
        this(is, blockSize, recordSize, null);
    }
    
    public TarInputStream(final InputStream is, final int blockSize, final int recordSize, final String encoding) {
        super(is);
        this.SKIP_BUF = new byte[8192];
        this.SMALL_BUF = new byte[256];
        this.buffer = new TarBuffer(is, blockSize, recordSize);
        this.readBuf = null;
        this.oneBuf = new byte[1];
        this.debug = false;
        this.hasHitEOF = false;
        this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
        this.buffer.setDebug(debug);
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
        long skip;
        int numRead;
        for (skip = numToSkip; skip > 0L; skip -= numRead) {
            final int realSkip = (int)((skip > this.SKIP_BUF.length) ? this.SKIP_BUF.length : skip);
            numRead = this.read(this.SKIP_BUF, 0, realSkip);
            if (numRead == -1) {
                break;
            }
        }
        return numToSkip - skip;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int markLimit) {
    }
    
    @Override
    public void reset() {
    }
    
    public TarEntry getNextEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }
        if (this.currEntry != null) {
            long numToSkip = this.entrySize - this.entryOffset;
            if (this.debug) {
                System.err.println("TarInputStream: SKIP currENTRY '" + this.currEntry.getName() + "' SZ " + this.entrySize + " OFF " + this.entryOffset + "  skipping " + numToSkip + " bytes");
            }
            while (numToSkip > 0L) {
                final long skipped = this.skip(numToSkip);
                if (skipped <= 0L) {
                    throw new RuntimeException("failed to skip current tar entry");
                }
                numToSkip -= skipped;
            }
            this.readBuf = null;
        }
        final byte[] headerBuf = this.getRecord();
        if (this.hasHitEOF) {
            return this.currEntry = null;
        }
        try {
            this.currEntry = new TarEntry(headerBuf, this.encoding);
        }
        catch (IllegalArgumentException e) {
            final IOException ioe = new IOException("Error detected parsing the header");
            ioe.initCause(e);
            throw ioe;
        }
        if (this.debug) {
            System.err.println("TarInputStream: SET CURRENTRY '" + this.currEntry.getName() + "' size = " + this.currEntry.getSize());
        }
        this.entryOffset = 0L;
        this.entrySize = this.currEntry.getSize();
        if (this.currEntry.isGNULongNameEntry()) {
            final ByteArrayOutputStream longName = new ByteArrayOutputStream();
            int length = 0;
            while ((length = this.read(this.SMALL_BUF)) >= 0) {
                longName.write(this.SMALL_BUF, 0, length);
            }
            this.getNextEntry();
            if (this.currEntry == null) {
                return null;
            }
            byte[] longNameData;
            for (longNameData = longName.toByteArray(), length = longNameData.length; length > 0 && longNameData[length - 1] == 0; --length) {}
            if (length != longNameData.length) {
                final byte[] l = new byte[length];
                System.arraycopy(longNameData, 0, l, 0, length);
                longNameData = l;
            }
            this.currEntry.setName(this.encoding.decode(longNameData));
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
            if (this.debug) {
                System.err.println("READ NULL RECORD");
            }
            this.hasHitEOF = true;
        }
        else if (this.buffer.isEOFRecord(headerBuf)) {
            if (this.debug) {
                System.err.println("READ EOF RECORD");
            }
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
    public int read() throws IOException {
        final int num = this.read(this.oneBuf, 0, 1);
        return (num == -1) ? -1 : (this.oneBuf[0] & 0xFF);
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
                throw new IOException("unexpected EOF with " + numToRead + " bytes unread");
            }
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
    
    public void copyEntryContents(final OutputStream out) throws IOException {
        final byte[] buf = new byte[32768];
        while (true) {
            final int numRead = this.read(buf, 0, buf.length);
            if (numRead == -1) {
                break;
            }
            out.write(buf, 0, numRead);
        }
    }
    
    public boolean canReadEntryData(final TarEntry te) {
        return !te.isGNUSparse();
    }
}
