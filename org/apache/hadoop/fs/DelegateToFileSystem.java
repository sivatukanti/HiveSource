// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Arrays;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import java.util.List;
import java.io.FileNotFoundException;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.EnumSet;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class DelegateToFileSystem extends AbstractFileSystem
{
    private static final int DELEGATE_TO_FS_DEFAULT_PORT = -1;
    protected final FileSystem fsImpl;
    
    protected DelegateToFileSystem(final URI theUri, final FileSystem theFsImpl, final Configuration conf, final String supportedScheme, final boolean authorityRequired) throws IOException, URISyntaxException {
        super(theUri, supportedScheme, authorityRequired, getDefaultPortIfDefined(theFsImpl));
        (this.fsImpl = theFsImpl).initialize(theUri, conf);
        this.fsImpl.statistics = this.getStatistics();
    }
    
    private static int getDefaultPortIfDefined(final FileSystem theFsImpl) {
        final int defaultPort = theFsImpl.getDefaultPort();
        return (defaultPort != 0) ? defaultPort : -1;
    }
    
    @Override
    public Path getInitialWorkingDirectory() {
        return this.fsImpl.getInitialWorkingDirectory();
    }
    
    @Override
    public FSDataOutputStream createInternal(final Path f, final EnumSet<CreateFlag> flag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws IOException {
        this.checkPath(f);
        if (!createParent) {
            final FileStatus stat = this.getFileStatus(f.getParent());
            if (stat == null) {
                throw new FileNotFoundException("Missing parent:" + f);
            }
            if (!stat.isDirectory()) {
                throw new ParentNotDirectoryException("parent is not a dir:" + f);
            }
        }
        return this.fsImpl.primitiveCreate(f, absolutePermission, flag, bufferSize, replication, blockSize, progress, checksumOpt);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        this.checkPath(f);
        return this.fsImpl.delete(f, recursive);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final Path f, final long start, final long len) throws IOException {
        this.checkPath(f);
        return this.fsImpl.getFileBlockLocations(f, start, len);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws IOException {
        this.checkPath(f);
        return this.fsImpl.getFileChecksum(f);
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        this.checkPath(f);
        return this.fsImpl.getFileStatus(f);
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws IOException {
        final FileStatus status = this.fsImpl.getFileLinkStatus(f);
        if (status.isSymlink()) {
            status.setSymlink(this.fsImpl.getLinkTarget(f));
        }
        return status;
    }
    
    @Override
    public FsStatus getFsStatus() throws IOException {
        return this.fsImpl.getStatus();
    }
    
    @Override
    public FsStatus getFsStatus(final Path f) throws IOException {
        return this.fsImpl.getStatus(f);
    }
    
    @Deprecated
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return this.fsImpl.getServerDefaults();
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return this.fsImpl.getServerDefaults(f);
    }
    
    @Override
    public Path getHomeDirectory() {
        return this.fsImpl.getHomeDirectory();
    }
    
    @Override
    public int getUriDefaultPort() {
        return getDefaultPortIfDefined(this.fsImpl);
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        this.checkPath(f);
        return this.fsImpl.listStatus(f);
    }
    
    @Override
    public void mkdir(final Path dir, final FsPermission permission, final boolean createParent) throws IOException {
        this.checkPath(dir);
        this.fsImpl.primitiveMkdir(dir, permission, createParent);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        this.checkPath(f);
        return this.fsImpl.open(f, bufferSize);
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        this.checkPath(f);
        return this.fsImpl.truncate(f, newLength);
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst) throws IOException {
        this.checkPath(src);
        this.checkPath(dst);
        this.fsImpl.rename(src, dst, Options.Rename.NONE);
    }
    
    @Override
    public void setOwner(final Path f, final String username, final String groupname) throws IOException {
        this.checkPath(f);
        this.fsImpl.setOwner(f, username, groupname);
    }
    
    @Override
    public void setPermission(final Path f, final FsPermission permission) throws IOException {
        this.checkPath(f);
        this.fsImpl.setPermission(f, permission);
    }
    
    @Override
    public boolean setReplication(final Path f, final short replication) throws IOException {
        this.checkPath(f);
        return this.fsImpl.setReplication(f, replication);
    }
    
    @Override
    public void setTimes(final Path f, final long mtime, final long atime) throws IOException {
        this.checkPath(f);
        this.fsImpl.setTimes(f, mtime, atime);
    }
    
    @Override
    public void setVerifyChecksum(final boolean verifyChecksum) throws IOException {
        this.fsImpl.setVerifyChecksum(verifyChecksum);
    }
    
    @Override
    public boolean supportsSymlinks() {
        return this.fsImpl.supportsSymlinks();
    }
    
    @Override
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws IOException {
        this.fsImpl.createSymlink(target, link, createParent);
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        return this.fsImpl.getLinkTarget(f);
    }
    
    @Override
    public String getCanonicalServiceName() {
        return this.fsImpl.getCanonicalServiceName();
    }
    
    @Override
    public List<Token<?>> getDelegationTokens(final String renewer) throws IOException {
        return Arrays.asList(this.fsImpl.addDelegationTokens(renewer, null));
    }
}
