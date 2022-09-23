// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.nio.channels.ClosedChannelException;
import org.slf4j.LoggerFactory;
import java.io.EOFException;
import java.util.zip.Checksum;
import org.apache.hadoop.util.DataChecksum;
import java.util.Arrays;
import org.slf4j.Logger;
import org.apache.hadoop.security.AccessControlException;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.EnumSet;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class ChecksumFs extends FilterFs
{
    private static final byte[] CHECKSUM_VERSION;
    private int defaultBytesPerChecksum;
    private boolean verifyChecksum;
    
    public static double getApproxChkSumLength(final long size) {
        return 0.01f * size;
    }
    
    public ChecksumFs(final AbstractFileSystem theFs) throws IOException, URISyntaxException {
        super(theFs);
        this.defaultBytesPerChecksum = 512;
        this.verifyChecksum = true;
        this.defaultBytesPerChecksum = this.getMyFs().getServerDefaults(new Path("/")).getBytesPerChecksum();
    }
    
    @Override
    public void setVerifyChecksum(final boolean inVerifyChecksum) {
        this.verifyChecksum = inVerifyChecksum;
    }
    
    public AbstractFileSystem getRawFs() {
        return this.getMyFs();
    }
    
    public Path getChecksumFile(final Path file) {
        return new Path(file.getParent(), "." + file.getName() + ".crc");
    }
    
    public static boolean isChecksumFile(final Path file) {
        final String name = file.getName();
        return name.startsWith(".") && name.endsWith(".crc");
    }
    
    public long getChecksumFileLength(final Path file, final long fileSize) {
        return getChecksumLength(fileSize, this.getBytesPerSum());
    }
    
    public int getBytesPerSum() {
        return this.defaultBytesPerChecksum;
    }
    
    private int getSumBufferSize(final int bytesPerSum, final int bufferSize, final Path file) throws IOException {
        final int defaultBufferSize = this.getMyFs().getServerDefaults(file).getFileBufferSize();
        final int proportionalBufferSize = bufferSize / bytesPerSum;
        return Math.max(bytesPerSum, Math.max(proportionalBufferSize, defaultBufferSize));
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        throw new UnsupportedOperationException("Truncate is not supported by ChecksumFs");
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException, UnresolvedLinkException {
        return new FSDataInputStream(new ChecksumFSInputChecker(this, f, bufferSize));
    }
    
    public static long getChecksumLength(final long size, final int bytesPerSum) {
        return (size + bytesPerSum - 1L) / bytesPerSum * 4L + ChecksumFs.CHECKSUM_VERSION.length + 4L;
    }
    
    @Override
    public FSDataOutputStream createInternal(final Path f, final EnumSet<CreateFlag> createFlag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws IOException {
        final FSDataOutputStream out = new FSDataOutputStream(new ChecksumFSOutputSummer(this, f, createFlag, absolutePermission, bufferSize, replication, blockSize, progress, checksumOpt, createParent), null);
        return out;
    }
    
    private boolean exists(final Path f) throws IOException, UnresolvedLinkException {
        try {
            return this.getMyFs().getFileStatus(f) != null;
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    private boolean isDirectory(final Path f) throws IOException, UnresolvedLinkException {
        try {
            return this.getMyFs().getFileStatus(f).isDirectory();
        }
        catch (FileNotFoundException e) {
            return false;
        }
    }
    
    @Override
    public boolean setReplication(final Path src, final short replication) throws IOException, UnresolvedLinkException {
        final boolean value = this.getMyFs().setReplication(src, replication);
        if (!value) {
            return false;
        }
        final Path checkFile = this.getChecksumFile(src);
        if (this.exists(checkFile)) {
            this.getMyFs().setReplication(checkFile, replication);
        }
        return true;
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst) throws IOException, UnresolvedLinkException {
        if (this.isDirectory(src)) {
            this.getMyFs().rename(src, dst, new Options.Rename[0]);
        }
        else {
            this.getMyFs().rename(src, dst, new Options.Rename[0]);
            final Path checkFile = this.getChecksumFile(src);
            if (this.exists(checkFile)) {
                if (this.isDirectory(dst)) {
                    this.getMyFs().rename(checkFile, dst, new Options.Rename[0]);
                }
                else {
                    this.getMyFs().rename(checkFile, this.getChecksumFile(dst), new Options.Rename[0]);
                }
            }
        }
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException, UnresolvedLinkException {
        FileStatus fstatus = null;
        try {
            fstatus = this.getMyFs().getFileStatus(f);
        }
        catch (FileNotFoundException e) {
            return false;
        }
        if (fstatus.isDirectory()) {
            return this.getMyFs().delete(f, recursive);
        }
        final Path checkFile = this.getChecksumFile(f);
        if (this.exists(checkFile)) {
            this.getMyFs().delete(checkFile, true);
        }
        return this.getMyFs().delete(f, true);
    }
    
    public boolean reportChecksumFailure(final Path f, final FSDataInputStream in, final long inPos, final FSDataInputStream sums, final long sumsPos) {
        return false;
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException, UnresolvedLinkException {
        final ArrayList<FileStatus> results = new ArrayList<FileStatus>();
        final FileStatus[] listing = this.getMyFs().listStatus(f);
        if (listing != null) {
            for (int i = 0; i < listing.length; ++i) {
                if (!isChecksumFile(listing[i].getPath())) {
                    results.add(listing[i]);
                }
            }
        }
        return results.toArray(new FileStatus[results.size()]);
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final RemoteIterator<LocatedFileStatus> iter = this.getMyFs().listLocatedStatus(f);
        return new RemoteIterator<LocatedFileStatus>() {
            private LocatedFileStatus next = null;
            
            @Override
            public boolean hasNext() throws IOException {
                while (this.next == null && iter.hasNext()) {
                    final LocatedFileStatus unfilteredNext = iter.next();
                    if (!ChecksumFs.isChecksumFile(unfilteredNext.getPath())) {
                        this.next = unfilteredNext;
                    }
                }
                return this.next != null;
            }
            
            @Override
            public LocatedFileStatus next() throws IOException {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                final LocatedFileStatus tmp = this.next;
                this.next = null;
                return tmp;
            }
        };
    }
    
    static {
        CHECKSUM_VERSION = new byte[] { 99, 114, 99, 0 };
    }
    
    private static class ChecksumFSInputChecker extends FSInputChecker
    {
        public static final Logger LOG;
        private static final int HEADER_LENGTH = 8;
        private ChecksumFs fs;
        private FSDataInputStream datas;
        private FSDataInputStream sums;
        private int bytesPerSum;
        private long fileLen;
        
        public ChecksumFSInputChecker(final ChecksumFs fs, final Path file) throws IOException, UnresolvedLinkException {
            this(fs, file, fs.getServerDefaults(file).getFileBufferSize());
        }
        
        public ChecksumFSInputChecker(final ChecksumFs fs, final Path file, final int bufferSize) throws IOException, UnresolvedLinkException {
            super(file, fs.getFileStatus(file).getReplication());
            this.bytesPerSum = 1;
            this.fileLen = -1L;
            this.datas = fs.getRawFs().open(file, bufferSize);
            this.fs = fs;
            final Path sumFile = fs.getChecksumFile(file);
            try {
                final int sumBufferSize = fs.getSumBufferSize(fs.getBytesPerSum(), bufferSize, file);
                this.sums = fs.getRawFs().open(sumFile, sumBufferSize);
                final byte[] version = new byte[ChecksumFs.CHECKSUM_VERSION.length];
                this.sums.readFully(version);
                if (!Arrays.equals(version, ChecksumFs.CHECKSUM_VERSION)) {
                    throw new IOException("Not a checksum file: " + sumFile);
                }
                this.bytesPerSum = this.sums.readInt();
                this.set(fs.verifyChecksum, DataChecksum.newCrc32(), this.bytesPerSum, 4);
            }
            catch (FileNotFoundException e2) {
                this.set(fs.verifyChecksum, null, 1, 0);
            }
            catch (IOException e) {
                ChecksumFSInputChecker.LOG.warn("Problem opening checksum file: " + file + ".  Ignoring exception: ", e);
                this.set(fs.verifyChecksum, null, 1, 0);
            }
        }
        
        private long getChecksumFilePos(final long dataPos) {
            return 8L + 4L * (dataPos / this.bytesPerSum);
        }
        
        @Override
        protected long getChunkPosition(final long dataPos) {
            return dataPos / this.bytesPerSum * this.bytesPerSum;
        }
        
        @Override
        public int available() throws IOException {
            return this.datas.available() + super.available();
        }
        
        @Override
        public int read(final long position, final byte[] b, final int off, final int len) throws IOException, UnresolvedLinkException {
            this.validatePositionedReadArgs(position, b, off, len);
            if (len == 0) {
                return 0;
            }
            int nread;
            try (final ChecksumFSInputChecker checker = new ChecksumFSInputChecker(this.fs, this.file)) {
                checker.seek(position);
                nread = checker.read(b, off, len);
            }
            return nread;
        }
        
        @Override
        public void close() throws IOException {
            this.datas.close();
            if (this.sums != null) {
                this.sums.close();
            }
            this.set(this.fs.verifyChecksum, null, 1, 0);
        }
        
        @Override
        public boolean seekToNewSource(final long targetPos) throws IOException {
            final long sumsPos = this.getChecksumFilePos(targetPos);
            this.fs.reportChecksumFailure(this.file, this.datas, targetPos, this.sums, sumsPos);
            final boolean newDataSource = this.datas.seekToNewSource(targetPos);
            return this.sums.seekToNewSource(sumsPos) || newDataSource;
        }
        
        @Override
        protected int readChunk(final long pos, final byte[] buf, final int offset, int len, final byte[] checksum) throws IOException {
            boolean eof = false;
            if (this.needChecksum()) {
                assert checksum != null;
                assert checksum.length % 4 == 0;
                assert len >= this.bytesPerSum;
                final int checksumsToRead = Math.min(len / this.bytesPerSum, checksum.length / 4);
                final long checksumPos = this.getChecksumFilePos(pos);
                if (checksumPos != this.sums.getPos()) {
                    this.sums.seek(checksumPos);
                }
                final int sumLenRead = this.sums.read(checksum, 0, 4 * checksumsToRead);
                if (sumLenRead >= 0 && sumLenRead % 4 != 0) {
                    throw new EOFException("Checksum file not a length multiple of checksum size in " + this.file + " at " + pos + " checksumpos: " + checksumPos + " sumLenread: " + sumLenRead);
                }
                if (sumLenRead <= 0) {
                    eof = true;
                }
                else {
                    len = Math.min(len, this.bytesPerSum * (sumLenRead / 4));
                }
            }
            if (pos != this.datas.getPos()) {
                this.datas.seek(pos);
            }
            final int nread = FSInputChecker.readFully(this.datas, buf, offset, len);
            if (eof && nread > 0) {
                throw new ChecksumException("Checksum error: " + this.file + " at " + pos, pos);
            }
            return nread;
        }
        
        private long getFileLength() throws IOException, UnresolvedLinkException {
            if (this.fileLen == -1L) {
                this.fileLen = this.fs.getFileStatus(this.file).getLen();
            }
            return this.fileLen;
        }
        
        @Override
        public synchronized long skip(long n) throws IOException {
            final long curPos = this.getPos();
            final long fileLength = this.getFileLength();
            if (n + curPos > fileLength) {
                n = fileLength - curPos;
            }
            return super.skip(n);
        }
        
        @Override
        public synchronized void seek(final long pos) throws IOException {
            if (pos > this.getFileLength()) {
                throw new IOException("Cannot seek after EOF");
            }
            super.seek(pos);
        }
        
        static {
            LOG = LoggerFactory.getLogger(FSInputChecker.class);
        }
    }
    
    private static class ChecksumFSOutputSummer extends FSOutputSummer
    {
        private FSDataOutputStream datas;
        private FSDataOutputStream sums;
        private static final float CHKSUM_AS_FRACTION = 0.01f;
        private boolean isClosed;
        
        public ChecksumFSOutputSummer(final ChecksumFs fs, final Path file, final EnumSet<CreateFlag> createFlag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws IOException {
            super(DataChecksum.newDataChecksum(DataChecksum.Type.CRC32, fs.getBytesPerSum()));
            this.isClosed = false;
            this.datas = fs.getRawFs().createInternal(file, createFlag, absolutePermission, bufferSize, replication, blockSize, progress, checksumOpt, createParent);
            final int bytesPerSum = fs.getBytesPerSum();
            final int sumBufferSize = fs.getSumBufferSize(bytesPerSum, bufferSize, file);
            (this.sums = fs.getRawFs().createInternal(fs.getChecksumFile(file), EnumSet.of(CreateFlag.CREATE, CreateFlag.OVERWRITE), absolutePermission, sumBufferSize, replication, blockSize, progress, checksumOpt, createParent)).write(ChecksumFs.CHECKSUM_VERSION, 0, ChecksumFs.CHECKSUM_VERSION.length);
            this.sums.writeInt(bytesPerSum);
        }
        
        @Override
        public void close() throws IOException {
            try {
                this.flushBuffer();
                this.sums.close();
                this.datas.close();
            }
            finally {
                this.isClosed = true;
            }
        }
        
        @Override
        protected void writeChunk(final byte[] b, final int offset, final int len, final byte[] checksum, final int ckoff, final int cklen) throws IOException {
            this.datas.write(b, offset, len);
            this.sums.write(checksum, ckoff, cklen);
        }
        
        @Override
        protected void checkClosed() throws IOException {
            if (this.isClosed) {
                throw new ClosedChannelException();
            }
        }
    }
}
