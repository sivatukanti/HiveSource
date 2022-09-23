// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.fs.BlockStoragePolicySpi;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.fs.ContentSummary;
import java.util.Map;
import org.apache.hadoop.fs.XAttrSetFlag;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.CreateFlag;
import java.util.EnumSet;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.FilterFileSystem;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class ChRootedFileSystem extends FilterFileSystem
{
    private final URI myUri;
    private final Path chRootPathPart;
    private final String chRootPathPartString;
    private Path workingDir;
    private static Path rootPath;
    
    protected FileSystem getMyFs() {
        return this.getRawFileSystem();
    }
    
    protected Path fullPath(final Path path) {
        super.checkPath(path);
        return path.isAbsolute() ? new Path((this.chRootPathPart.isRoot() ? "" : this.chRootPathPartString) + path.toUri().getPath()) : new Path(this.chRootPathPartString + this.workingDir.toUri().getPath(), path);
    }
    
    public ChRootedFileSystem(final URI uri, final Configuration conf) throws IOException {
        super(FileSystem.get(uri, conf));
        String pathString = uri.getPath();
        if (pathString.isEmpty()) {
            pathString = "/";
        }
        this.chRootPathPart = new Path(pathString);
        this.chRootPathPartString = this.chRootPathPart.toUri().getPath();
        this.myUri = uri;
        this.workingDir = this.getHomeDirectory();
    }
    
    @Override
    public void initialize(final URI name, final Configuration conf) throws IOException {
        super.initialize(name, conf);
        this.setConf(conf);
    }
    
    @Override
    public URI getUri() {
        return this.myUri;
    }
    
    String stripOutRoot(final Path p) throws IOException {
        try {
            this.checkPath(p);
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Internal Error - path " + p + " should have been with URI: " + this.myUri);
        }
        final String pathPart = p.toUri().getPath();
        return (pathPart.length() == this.chRootPathPartString.length()) ? "" : pathPart.substring(this.chRootPathPartString.length() + (this.chRootPathPart.isRoot() ? 0 : 1));
    }
    
    @Override
    protected Path getInitialWorkingDirectory() {
        return null;
    }
    
    public Path getResolvedQualifiedPath(final Path f) throws FileNotFoundException {
        return this.makeQualified(new Path(this.chRootPathPartString + f.toUri().toString()));
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.workingDir;
    }
    
    @Override
    public void setWorkingDirectory(final Path new_dir) {
        this.workingDir = (new_dir.isAbsolute() ? new_dir : new Path(this.workingDir, new_dir));
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return super.create(this.fullPath(f), permission, overwrite, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        return super.createNonRecursive(this.fullPath(f), permission, flags, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException {
        return super.delete(this.fullPath(f), recursive);
    }
    
    @Override
    public boolean delete(final Path f) throws IOException {
        return this.delete(f, true);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final FileStatus fs, final long start, final long len) throws IOException {
        return super.getFileBlockLocations(new ViewFsFileStatus(fs, this.fullPath(fs.getPath())), start, len);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws IOException {
        return super.getFileChecksum(this.fullPath(f));
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f, final long length) throws IOException {
        return super.getFileChecksum(this.fullPath(f), length);
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException {
        return super.getFileStatus(this.fullPath(f));
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        return super.getLinkTarget(this.fullPath(f));
    }
    
    @Override
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, IOException {
        super.access(this.fullPath(path), mode);
    }
    
    @Override
    public FsStatus getStatus(final Path p) throws IOException {
        return super.getStatus(this.fullPath(p));
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException {
        return super.listStatus(this.fullPath(f));
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws IOException {
        return super.listLocatedStatus(this.fullPath(f));
    }
    
    @Override
    public boolean mkdirs(final Path f, final FsPermission permission) throws IOException {
        return super.mkdirs(this.fullPath(f), permission);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException {
        return super.open(this.fullPath(f), bufferSize);
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        return super.append(this.fullPath(f), bufferSize, progress);
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        return super.rename(this.fullPath(src), this.fullPath(dst));
    }
    
    @Override
    public void setOwner(final Path f, final String username, final String groupname) throws IOException {
        super.setOwner(this.fullPath(f), username, groupname);
    }
    
    @Override
    public void setPermission(final Path f, final FsPermission permission) throws IOException {
        super.setPermission(this.fullPath(f), permission);
    }
    
    @Override
    public boolean setReplication(final Path f, final short replication) throws IOException {
        return super.setReplication(this.fullPath(f), replication);
    }
    
    @Override
    public void setTimes(final Path f, final long mtime, final long atime) throws IOException {
        super.setTimes(this.fullPath(f), mtime, atime);
    }
    
    @Override
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        super.modifyAclEntries(this.fullPath(path), aclSpec);
    }
    
    @Override
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        super.removeAclEntries(this.fullPath(path), aclSpec);
    }
    
    @Override
    public void removeDefaultAcl(final Path path) throws IOException {
        super.removeDefaultAcl(this.fullPath(path));
    }
    
    @Override
    public void removeAcl(final Path path) throws IOException {
        super.removeAcl(this.fullPath(path));
    }
    
    @Override
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        super.setAcl(this.fullPath(path), aclSpec);
    }
    
    @Override
    public AclStatus getAclStatus(final Path path) throws IOException {
        return super.getAclStatus(this.fullPath(path));
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        super.setXAttr(this.fullPath(path), name, value, flag);
    }
    
    @Override
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        return super.getXAttr(this.fullPath(path), name);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        return super.getXAttrs(this.fullPath(path));
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        return super.getXAttrs(this.fullPath(path), names);
    }
    
    @Override
    public boolean truncate(final Path path, final long newLength) throws IOException {
        return super.truncate(this.fullPath(path), newLength);
    }
    
    @Override
    public List<String> listXAttrs(final Path path) throws IOException {
        return super.listXAttrs(this.fullPath(path));
    }
    
    @Override
    public void removeXAttr(final Path path, final String name) throws IOException {
        super.removeXAttr(this.fullPath(path), name);
    }
    
    @Override
    public Path createSnapshot(final Path path, final String name) throws IOException {
        return super.createSnapshot(this.fullPath(path), name);
    }
    
    @Override
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        super.renameSnapshot(this.fullPath(path), snapshotOldName, snapshotNewName);
    }
    
    @Override
    public void deleteSnapshot(final Path snapshotDir, final String snapshotName) throws IOException {
        super.deleteSnapshot(this.fullPath(snapshotDir), snapshotName);
    }
    
    @Override
    public Path resolvePath(final Path p) throws IOException {
        return super.resolvePath(this.fullPath(p));
    }
    
    @Override
    public ContentSummary getContentSummary(final Path f) throws IOException {
        return this.fs.getContentSummary(this.fullPath(f));
    }
    
    @Override
    public QuotaUsage getQuotaUsage(final Path f) throws IOException {
        return this.fs.getQuotaUsage(this.fullPath(f));
    }
    
    @Override
    public long getDefaultBlockSize() {
        return this.getDefaultBlockSize(this.fullPath(ChRootedFileSystem.rootPath));
    }
    
    @Override
    public long getDefaultBlockSize(final Path f) {
        return super.getDefaultBlockSize(this.fullPath(f));
    }
    
    @Override
    public short getDefaultReplication() {
        return this.getDefaultReplication(this.fullPath(ChRootedFileSystem.rootPath));
    }
    
    @Override
    public short getDefaultReplication(final Path f) {
        return super.getDefaultReplication(this.fullPath(f));
    }
    
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return this.getServerDefaults(this.fullPath(ChRootedFileSystem.rootPath));
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return super.getServerDefaults(this.fullPath(f));
    }
    
    @Override
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        return super.getStoragePolicy(this.fullPath(src));
    }
    
    @Override
    public void setStoragePolicy(final Path src, final String policyName) throws IOException {
        super.setStoragePolicy(this.fullPath(src), policyName);
    }
    
    @Override
    public void unsetStoragePolicy(final Path src) throws IOException {
        super.unsetStoragePolicy(this.fullPath(src));
    }
    
    static {
        ChRootedFileSystem.rootPath = new Path("/");
    }
}
