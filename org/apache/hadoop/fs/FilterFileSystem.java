// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Collection;
import java.util.Map;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.fs.permission.FsAction;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class FilterFileSystem extends FileSystem
{
    protected FileSystem fs;
    protected String swapScheme;
    
    public FilterFileSystem() {
    }
    
    public FilterFileSystem(final FileSystem fs) {
        this.fs = fs;
        this.statistics = fs.statistics;
    }
    
    public FileSystem getRawFileSystem() {
        return this.fs;
    }
    
    @Override
    public void initialize(final URI name, final Configuration conf) throws IOException {
        super.initialize(name, conf);
        if (this.fs.getConf() == null) {
            this.fs.initialize(name, conf);
        }
        final String scheme = name.getScheme();
        if (!scheme.equals(this.fs.getUri().getScheme())) {
            this.swapScheme = scheme;
        }
    }
    
    @Override
    public URI getUri() {
        return this.fs.getUri();
    }
    
    @Override
    protected URI getCanonicalUri() {
        return this.fs.getCanonicalUri();
    }
    
    @Override
    protected URI canonicalizeUri(final URI uri) {
        return this.fs.canonicalizeUri(uri);
    }
    
    @Override
    public Path makeQualified(final Path path) {
        Path fqPath = this.fs.makeQualified(path);
        if (this.swapScheme != null) {
            try {
                fqPath = new Path(new URI(this.swapScheme, fqPath.toUri().getSchemeSpecificPart(), null));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return fqPath;
    }
    
    @Override
    protected void checkPath(final Path path) {
        this.fs.checkPath(path);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final FileStatus file, final long start, final long len) throws IOException {
        return this.fs.getFileBlockLocations(file, start, len);
    }
    
    @Override
    public Path resolvePath(final Path p) throws IOException {
        return this.fs.resolvePath(p);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        return this.fs.open(f, bufferSize);
    }
    
    @Override
    public FSDataInputStream open(final PathHandle fd, final int bufferSize) throws IOException {
        return this.fs.open(fd, bufferSize);
    }
    
    @Override
    protected PathHandle createPathHandle(final FileStatus stat, final Options.HandleOpt... opts) {
        return this.fs.getPathHandle(stat, opts);
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        return this.fs.append(f, bufferSize, progress);
    }
    
    @Override
    public void concat(final Path f, final Path[] psrcs) throws IOException {
        this.fs.concat(f, psrcs);
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.fs.create(f, permission, overwrite, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt) throws IOException {
        return this.fs.create(f, permission, flags, bufferSize, replication, blockSize, progress, checksumOpt);
    }
    
    @Override
    protected RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f, final PathFilter filter) throws FileNotFoundException, IOException {
        return this.fs.listLocatedStatus(f, filter);
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return this.fs.createNonRecursive(f, permission, flags, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public boolean setReplication(final Path src, final short replication) throws IOException {
        return this.fs.setReplication(src, replication);
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        return this.fs.rename(src, dst);
    }
    
    @Override
    protected void rename(final Path src, final Path dst, final Options.Rename... options) throws IOException {
        this.fs.rename(src, dst, options);
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        return this.fs.truncate(f, newLength);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        return this.fs.delete(f, recursive);
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        return this.fs.listStatus(f);
    }
    
    @Override
    public RemoteIterator<Path> listCorruptFileBlocks(final Path path) throws IOException {
        return this.fs.listCorruptFileBlocks(path);
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws IOException {
        return this.fs.listLocatedStatus(f);
    }
    
    @Override
    public RemoteIterator<FileStatus> listStatusIterator(final Path f) throws IOException {
        return this.fs.listStatusIterator(f);
    }
    
    @Override
    public Path getHomeDirectory() {
        return this.fs.getHomeDirectory();
    }
    
    @Override
    public void setWorkingDirectory(final Path newDir) {
        this.fs.setWorkingDirectory(newDir);
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.fs.getWorkingDirectory();
    }
    
    @Override
    protected Path getInitialWorkingDirectory() {
        return this.fs.getInitialWorkingDirectory();
    }
    
    @Override
    public FsStatus getStatus(final Path p) throws IOException {
        return this.fs.getStatus(p);
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        return this.fs.mkdirs(f, permission);
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        this.fs.copyFromLocalFile(delSrc, src, dst);
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path[] srcs, final Path dst) throws IOException {
        this.fs.copyFromLocalFile(delSrc, overwrite, srcs, dst);
    }
    
    @Override
    public void copyFromLocalFile(final boolean delSrc, final boolean overwrite, final Path src, final Path dst) throws IOException {
        this.fs.copyFromLocalFile(delSrc, overwrite, src, dst);
    }
    
    @Override
    public void copyToLocalFile(final boolean delSrc, final Path src, final Path dst) throws IOException {
        this.fs.copyToLocalFile(delSrc, src, dst);
    }
    
    @Override
    public Path startLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        return this.fs.startLocalOutput(fsOutputFile, tmpLocalFile);
    }
    
    @Override
    public void completeLocalOutput(final Path fsOutputFile, final Path tmpLocalFile) throws IOException {
        this.fs.completeLocalOutput(fsOutputFile, tmpLocalFile);
    }
    
    @Override
    public long getUsed() throws IOException {
        return this.fs.getUsed();
    }
    
    @Override
    public long getUsed(final Path path) throws IOException {
        return this.fs.getUsed(path);
    }
    
    @Override
    public long getDefaultBlockSize() {
        return this.fs.getDefaultBlockSize();
    }
    
    @Override
    public short getDefaultReplication() {
        return this.fs.getDefaultReplication();
    }
    
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return this.fs.getServerDefaults();
    }
    
    @Override
    public long getDefaultBlockSize(final Path f) {
        return this.fs.getDefaultBlockSize(f);
    }
    
    @Override
    public short getDefaultReplication(final Path f) {
        return this.fs.getDefaultReplication(f);
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return this.fs.getServerDefaults(f);
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        return this.fs.getFileStatus(f);
    }
    
    @Override
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, IOException {
        this.fs.access(path, mode);
    }
    
    @Override
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, IOException {
        this.fs.createSymlink(target, link, createParent);
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        return this.fs.getFileLinkStatus(f);
    }
    
    @Override
    public boolean supportsSymlinks() {
        return this.fs.supportsSymlinks();
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        return this.fs.getLinkTarget(f);
    }
    
    @Override
    protected Path resolveLink(final Path f) throws IOException {
        return this.fs.resolveLink(f);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws IOException {
        return this.fs.getFileChecksum(f);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f, final long length) throws IOException {
        return this.fs.getFileChecksum(f, length);
    }
    
    @Override
    public void setVerifyChecksum(final boolean verifyChecksum) {
        this.fs.setVerifyChecksum(verifyChecksum);
    }
    
    @Override
    public void setWriteChecksum(final boolean writeChecksum) {
        this.fs.setWriteChecksum(writeChecksum);
    }
    
    @Override
    public Configuration getConf() {
        return this.fs.getConf();
    }
    
    @Override
    public void close() throws IOException {
        super.close();
        this.fs.close();
    }
    
    @Override
    public void setOwner(final Path p, final String username, final String groupname) throws IOException {
        this.fs.setOwner(p, username, groupname);
    }
    
    @Override
    public void setTimes(final Path p, final long mtime, final long atime) throws IOException {
        this.fs.setTimes(p, mtime, atime);
    }
    
    @Override
    public void setPermission(final Path p, final FsPermission permission) throws IOException {
        this.fs.setPermission(p, permission);
    }
    
    @Override
    protected FSDataOutputStream primitiveCreate(final Path f, final FsPermission absolutePermission, final EnumSet<CreateFlag> flag, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt) throws IOException {
        return this.fs.primitiveCreate(f, absolutePermission, flag, bufferSize, replication, blockSize, progress, checksumOpt);
    }
    
    @Override
    protected boolean primitiveMkdir(final Path f, final FsPermission abdolutePermission) throws IOException {
        return this.fs.primitiveMkdir(f, abdolutePermission);
    }
    
    @Override
    public FileSystem[] getChildFileSystems() {
        return new FileSystem[] { this.fs };
    }
    
    @Override
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        return this.fs.createSnapshot(path, snapshotName);
    }
    
    @Override
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        this.fs.renameSnapshot(path, snapshotOldName, snapshotNewName);
    }
    
    @Override
    public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
        this.fs.deleteSnapshot(path, snapshotName);
    }
    
    @Override
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.fs.modifyAclEntries(path, aclSpec);
    }
    
    @Override
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.fs.removeAclEntries(path, aclSpec);
    }
    
    @Override
    public void removeDefaultAcl(final Path path) throws IOException {
        this.fs.removeDefaultAcl(path);
    }
    
    @Override
    public void removeAcl(final Path path) throws IOException {
        this.fs.removeAcl(path);
    }
    
    @Override
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.fs.setAcl(path, aclSpec);
    }
    
    @Override
    public AclStatus getAclStatus(final Path path) throws IOException {
        return this.fs.getAclStatus(path);
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value) throws IOException {
        this.fs.setXAttr(path, name, value);
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        this.fs.setXAttr(path, name, value, flag);
    }
    
    @Override
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        return this.fs.getXAttr(path, name);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        return this.fs.getXAttrs(path);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        return this.fs.getXAttrs(path, names);
    }
    
    @Override
    public List<String> listXAttrs(final Path path) throws IOException {
        return this.fs.listXAttrs(path);
    }
    
    @Override
    public void removeXAttr(final Path path, final String name) throws IOException {
        this.fs.removeXAttr(path, name);
    }
    
    @Override
    public void setStoragePolicy(final Path src, final String policyName) throws IOException {
        this.fs.setStoragePolicy(src, policyName);
    }
    
    @Override
    public void unsetStoragePolicy(final Path src) throws IOException {
        this.fs.unsetStoragePolicy(src);
    }
    
    @Override
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        return this.fs.getStoragePolicy(src);
    }
    
    @Override
    public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
        return this.fs.getAllStoragePolicies();
    }
    
    @Override
    public Path getTrashRoot(final Path path) {
        return this.fs.getTrashRoot(path);
    }
    
    @Override
    public Collection<FileStatus> getTrashRoots(final boolean allUsers) {
        return this.fs.getTrashRoots(allUsers);
    }
    
    @Override
    public FSDataOutputStreamBuilder createFile(final Path path) {
        return this.fs.createFile(path);
    }
    
    @Override
    public FSDataOutputStreamBuilder appendFile(final Path path) {
        return this.fs.appendFile(path);
    }
}
