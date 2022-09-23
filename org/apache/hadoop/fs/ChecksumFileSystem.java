// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.nio.channels.ClosedChannelException;
import java.io.EOFException;
import java.util.zip.Checksum;
import org.apache.hadoop.util.DataChecksum;
import java.util.Arrays;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import java.io.IOException;
import java.io.InputStream;
import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class ChecksumFileSystem extends FilterFileSystem
{
    private static final byte[] CHECKSUM_VERSION;
    private int bytesPerChecksum;
    private boolean verifyChecksum;
    private boolean writeChecksum;
    private static final PathFilter DEFAULT_FILTER;
    
    public static double getApproxChkSumLength(final long size) {
        return 0.01f * size;
    }
    
    public ChecksumFileSystem(final FileSystem fs) {
        super(fs);
        this.bytesPerChecksum = 512;
        this.verifyChecksum = true;
        this.writeChecksum = true;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        if (conf != null) {
            this.bytesPerChecksum = conf.getInt("file.bytes-per-checksum", 512);
            Preconditions.checkState(this.bytesPerChecksum > 0, "bytes per checksum should be positive but was %s", this.bytesPerChecksum);
        }
    }
    
    @Override
    public void setVerifyChecksum(final boolean verifyChecksum) {
        this.verifyChecksum = verifyChecksum;
    }
    
    @Override
    public void setWriteChecksum(final boolean writeChecksum) {
        this.writeChecksum = writeChecksum;
    }
    
    @Override
    public FileSystem getRawFileSystem() {
        return this.fs;
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
        return this.bytesPerChecksum;
    }
    
    private int getSumBufferSize(final int bytesPerSum, final int bufferSize) {
        final int defaultBufferSize = this.getConf().getInt("file.stream-buffer-size", 4096);
        final int proportionalBufferSize = bufferSize / bytesPerSum;
        return Math.max(bytesPerSum, Math.max(proportionalBufferSize, defaultBufferSize));
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        FileSystem fs;
        InputStream in;
        if (this.verifyChecksum) {
            fs = this;
            in = new ChecksumFSInputChecker(this, f, bufferSize);
        }
        else {
            fs = this.getRawFileSystem();
            in = fs.open(f, bufferSize);
        }
        return new FSDataBoundedInputStream(fs, f, in);
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        throw new UnsupportedOperationException("Append is not supported by ChecksumFileSystem");
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        throw new UnsupportedOperationException("Truncate is not supported by ChecksumFileSystem");
    }
    
    public static long getChecksumLength(final long size, final int bytesPerSum) {
        return (size + bytesPerSum - 1L) / bytesPerSum * 4L + ChecksumFileSystem.CHECKSUM_VERSION.length + 4L;
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.create(f, permission, overwrite, true, bufferSize, replication, blockSize, progress);
    }
    
    private FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final boolean createParent, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        final Path parent = f.getParent();
        if (parent != null) {
            if (!createParent && !this.exists(parent)) {
                throw new FileNotFoundException("Parent directory doesn't exist: " + parent);
            }
            if (!this.mkdirs(parent)) {
                throw new IOException("Mkdirs failed to create " + parent + " (exists=" + this.exists(parent) + ", cwd=" + this.getWorkingDirectory() + ")");
            }
        }
        FSDataOutputStream out;
        if (this.writeChecksum) {
            out = new FSDataOutputStream(new ChecksumFSOutputSummer(this, f, overwrite, bufferSize, replication, blockSize, progress, permission), null);
        }
        else {
            out = this.fs.create(f, permission, overwrite, bufferSize, replication, blockSize, progress);
            final Path checkFile = this.getChecksumFile(f);
            if (this.fs.exists(checkFile)) {
                this.fs.delete(checkFile, true);
            }
        }
        return out;
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.create(f, permission, overwrite, false, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public void setPermission(final Path src, final FsPermission permission) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.setPermission(p, permission);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public void setOwner(final Path src, final String username, final String groupname) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.setOwner(p, username, groupname);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public void setAcl(final Path src, final List<AclEntry> aclSpec) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.setAcl(p, aclSpec);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public void modifyAclEntries(final Path src, final List<AclEntry> aclSpec) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.modifyAclEntries(p, aclSpec);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public void removeAcl(final Path src) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.removeAcl(p);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public void removeAclEntries(final Path src, final List<AclEntry> aclSpec) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.removeAclEntries(p, aclSpec);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public void removeDefaultAcl(final Path src) throws IOException {
        new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                ChecksumFileSystem.this.fs.removeDefaultAcl(p);
                return true;
            }
        }.run(src);
    }
    
    @Override
    public boolean setReplication(final Path src, final short replication) throws IOException {
        return new FsOperation() {
            @Override
            boolean apply(final Path p) throws IOException {
                return ChecksumFileSystem.this.fs.setReplication(p, replication);
            }
        }.run(src);
    }
    
    @Override
    public boolean rename(final Path src, Path dst) throws IOException {
        if (this.fs.isDirectory(src)) {
            return this.fs.rename(src, dst);
        }
        if (this.fs.isDirectory(dst)) {
            dst = new Path(dst, src.getName());
        }
        boolean value = this.fs.rename(src, dst);
        if (!value) {
            return false;
        }
        final Path srcCheckFile = this.getChecksumFile(src);
        final Path dstCheckFile = this.getChecksumFile(dst);
        if (this.fs.exists(srcCheckFile)) {
            value = this.fs.rename(srcCheckFile, dstCheckFile);
        }
        else if (this.fs.exists(dstCheckFile)) {
            value = this.fs.delete(dstCheckFile, true);
        }
        return value;
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        FileStatus fstatus = null;
        try {
            fstatus = this.fs.getFileStatus(f);
        }
        catch (FileNotFoundException e) {
            return false;
        }
        if (fstatus.isDirectory()) {
            return this.fs.delete(f, recursive);
        }
        final Path checkFile = this.getChecksumFile(f);
        if (this.fs.exists(checkFile)) {
            this.fs.delete(checkFile, true);
        }
        return this.fs.delete(f, true);
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        return this.fs.listStatus(f, ChecksumFileSystem.DEFAULT_FILTER);
    }
    
    @Override
    public RemoteIterator<FileStatus> listStatusIterator(final Path p) throws IOException {
        return new DirListingIterator<FileStatus>(p);
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws IOException {
        return this.fs.listLocatedStatus(f, ChecksumFileSystem.DEFAULT_FILTER);
    }
    
    @Override
    public boolean mkdirs(final Path f) throws IOException {
        return this.fs.mkdirs(f);
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        final Configuration conf = this.getConf();
        FileUtil.copy(FileSystem.getLocal(conf), src, this, dst, delSrc, conf);
    }
    
    @Override
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        final Configuration conf = this.getConf();
        FileUtil.copy(this, src, FileSystem.getLocal(conf), dst, delSrc, conf);
    }
    
    public void copyToLocalFile(final Path src, Path dst, final boolean copyCrc) throws IOException {
        if (!this.fs.isDirectory(src)) {
            this.fs.copyToLocalFile(src, dst);
            final FileSystem localFs = FileSystem.getLocal(this.getConf()).getRawFileSystem();
            if (localFs.isDirectory(dst)) {
                dst = new Path(dst, src.getName());
            }
            dst = this.getChecksumFile(dst);
            if (localFs.exists(dst)) {
                localFs.delete(dst, true);
            }
            final Path checksumFile = this.getChecksumFile(src);
            if (copyCrc && this.fs.exists(checksumFile)) {
                this.fs.copyToLocalFile(checksumFile, dst);
            }
        }
        else {
            final FileStatus[] listStatus;
            final FileStatus[] srcs = listStatus = this.listStatus(src);
            for (final FileStatus srcFile : listStatus) {
                this.copyToLocalFile(srcFile.getPath(), new Path(dst, srcFile.getPath().getName()), copyCrc);
            }
        }
    }
    
    @Override
    public Path startLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        return tmpLocalFile;
    }
    
    @Override
    public void completeLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        this.moveFromLocalFile(tmpLocalFile, fsOutputFile);
    }
    
    public boolean reportChecksumFailure(final Path f, final FSDataInputStream in, final long inPos, final FSDataInputStream sums, final long sumsPos) {
        return false;
    }
    
    static {
        CHECKSUM_VERSION = new byte[] { 99, 114, 99, 0 };
        DEFAULT_FILTER = new PathFilter() {
            @Override
            public boolean accept(final Path file) {
                return !ChecksumFileSystem.isChecksumFile(file);
            }
        };
    }
    
    private static class ChecksumFSInputChecker extends FSInputChecker
    {
        private ChecksumFileSystem fs;
        private FSDataInputStream datas;
        private FSDataInputStream sums;
        private static final int HEADER_LENGTH = 8;
        private int bytesPerSum;
        
        public ChecksumFSInputChecker(final ChecksumFileSystem fs, final Path file) throws IOException {
            this(fs, file, fs.getConf().getInt("file.stream-buffer-size", 4096));
        }
        
        public ChecksumFSInputChecker(final ChecksumFileSystem fs, final Path file, final int bufferSize) throws IOException {
            super(file, fs.getFileStatus(file).getReplication());
            this.bytesPerSum = 1;
            this.datas = fs.getRawFileSystem().open(file, bufferSize);
            this.fs = fs;
            final Path sumFile = fs.getChecksumFile(file);
            try {
                final int sumBufferSize = fs.getSumBufferSize(fs.getBytesPerSum(), bufferSize);
                this.sums = fs.getRawFileSystem().open(sumFile, sumBufferSize);
                final byte[] version = new byte[ChecksumFileSystem.CHECKSUM_VERSION.length];
                this.sums.readFully(version);
                if (!Arrays.equals(version, ChecksumFileSystem.CHECKSUM_VERSION)) {
                    throw new IOException("Not a checksum file: " + sumFile);
                }
                this.bytesPerSum = this.sums.readInt();
                this.set(fs.verifyChecksum, DataChecksum.newCrc32(), this.bytesPerSum, 4);
            }
            catch (IOException e) {
                if (!(e instanceof FileNotFoundException) || e.getMessage().endsWith(" (Permission denied)")) {
                    ChecksumFSInputChecker.LOG.warn("Problem opening checksum file: " + file + ".  Ignoring exception: ", e);
                }
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
        public int read(final long position, final byte[] b, final int off, final int len) throws IOException {
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
                    throw new ChecksumException("Checksum file not a length multiple of checksum size in " + this.file + " at " + pos + " checksumpos: " + checksumPos + " sumLenread: " + sumLenRead, pos);
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
    }
    
    private static class FSDataBoundedInputStream extends FSDataInputStream
    {
        private FileSystem fs;
        private Path file;
        private long fileLen;
        
        FSDataBoundedInputStream(final FileSystem fs, final Path file, final InputStream in) {
            super(in);
            this.fileLen = -1L;
            this.fs = fs;
            this.file = file;
        }
        
        @Override
        public boolean markSupported() {
            return false;
        }
        
        private long getFileLength() throws IOException {
            if (this.fileLen == -1L) {
                this.fileLen = this.fs.getContentSummary(this.file).getLength();
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
                throw new EOFException("Cannot seek after EOF");
            }
            super.seek(pos);
        }
    }
    
    private static class ChecksumFSOutputSummer extends FSOutputSummer
    {
        private FSDataOutputStream datas;
        private FSDataOutputStream sums;
        private static final float CHKSUM_AS_FRACTION = 0.01f;
        private boolean isClosed;
        
        public ChecksumFSOutputSummer(final ChecksumFileSystem fs, final Path file, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final FsPermission permission) throws IOException {
            super(DataChecksum.newDataChecksum(DataChecksum.Type.CRC32, fs.getBytesPerSum()));
            this.isClosed = false;
            final int bytesPerSum = fs.getBytesPerSum();
            this.datas = fs.getRawFileSystem().create(file, permission, overwrite, bufferSize, replication, blockSize, progress);
            final int sumBufferSize = fs.getSumBufferSize(bytesPerSum, bufferSize);
            (this.sums = fs.getRawFileSystem().create(fs.getChecksumFile(file), permission, true, sumBufferSize, replication, blockSize, null)).write(ChecksumFileSystem.CHECKSUM_VERSION, 0, ChecksumFileSystem.CHECKSUM_VERSION.length);
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
    
    abstract class FsOperation
    {
        boolean run(final Path p) throws IOException {
            final boolean status = this.apply(p);
            if (status) {
                final Path checkFile = ChecksumFileSystem.this.getChecksumFile(p);
                if (ChecksumFileSystem.this.fs.exists(checkFile)) {
                    this.apply(checkFile);
                }
            }
            return status;
        }
        
        abstract boolean apply(final Path p0) throws IOException;
    }
}
