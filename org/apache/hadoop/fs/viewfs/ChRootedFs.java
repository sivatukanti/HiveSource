// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.security.token.Token;
import java.util.Collection;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import java.util.Map;
import org.apache.hadoop.fs.XAttrSetFlag;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.UnresolvedLinkException;
import java.io.IOException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.CreateFlag;
import java.util.EnumSet;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import org.apache.hadoop.fs.Path;
import java.net.URI;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.AbstractFileSystem;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class ChRootedFs extends AbstractFileSystem
{
    private final AbstractFileSystem myFs;
    private final URI myUri;
    private final Path chRootPathPart;
    private final String chRootPathPartString;
    
    protected AbstractFileSystem getMyFs() {
        return this.myFs;
    }
    
    protected Path fullPath(final Path path) {
        super.checkPath(path);
        return new Path((this.chRootPathPart.isRoot() ? "" : this.chRootPathPartString) + path.toUri().getPath());
    }
    
    @Override
    public boolean isValidName(final String src) {
        return this.myFs.isValidName(this.fullPath(new Path(src)).toUri().toString());
    }
    
    public ChRootedFs(final AbstractFileSystem fs, final Path theRoot) throws URISyntaxException {
        super(fs.getUri(), fs.getUri().getScheme(), false, fs.getUriDefaultPort());
        (this.myFs = fs).checkPath(theRoot);
        this.chRootPathPart = new Path(this.myFs.getUriPath(theRoot));
        this.chRootPathPartString = this.chRootPathPart.toUri().getPath();
        this.myUri = new URI(this.myFs.getUri().toString() + ((this.myFs.getUri().getAuthority() == null) ? "" : "/") + this.chRootPathPart.toUri().getPath().substring(1));
        super.checkPath(theRoot);
    }
    
    @Override
    public URI getUri() {
        return this.myUri;
    }
    
    public String stripOutRoot(final Path p) {
        try {
            this.checkPath(p);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException("Internal Error - path " + p + " should have been with URI" + this.myUri);
        }
        final String pathPart = p.toUri().getPath();
        return (pathPart.length() == this.chRootPathPartString.length()) ? "" : pathPart.substring(this.chRootPathPartString.length() + (this.chRootPathPart.isRoot() ? 0 : 1));
    }
    
    @Override
    public Path getHomeDirectory() {
        return this.myFs.getHomeDirectory();
    }
    
    @Override
    public Path getInitialWorkingDirectory() {
        return null;
    }
    
    public Path getResolvedQualifiedPath(final Path f) throws FileNotFoundException {
        return this.myFs.makeQualified(new Path(this.chRootPathPartString + f.toUri().toString()));
    }
    
    @Override
    public FSDataOutputStream createInternal(final Path f, final EnumSet<CreateFlag> flag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws IOException, UnresolvedLinkException {
        return this.myFs.createInternal(this.fullPath(f), flag, absolutePermission, bufferSize, replication, blockSize, progress, checksumOpt, createParent);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException, UnresolvedLinkException {
        return this.myFs.delete(this.fullPath(f), recursive);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final Path f, final long start, final long len) throws IOException, UnresolvedLinkException {
        return this.myFs.getFileBlockLocations(this.fullPath(f), start, len);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws IOException, UnresolvedLinkException {
        return this.myFs.getFileChecksum(this.fullPath(f));
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException, UnresolvedLinkException {
        return this.myFs.getFileStatus(this.fullPath(f));
    }
    
    @Override
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        this.myFs.access(this.fullPath(path), mode);
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws IOException, UnresolvedLinkException {
        return this.myFs.getFileLinkStatus(this.fullPath(f));
    }
    
    @Override
    public FsStatus getFsStatus() throws IOException {
        return this.myFs.getFsStatus();
    }
    
    @Deprecated
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return this.myFs.getServerDefaults();
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        return this.myFs.getServerDefaults(this.fullPath(f));
    }
    
    @Override
    public int getUriDefaultPort() {
        return this.myFs.getUriDefaultPort();
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException, UnresolvedLinkException {
        return this.myFs.listStatus(this.fullPath(f));
    }
    
    @Override
    public RemoteIterator<FileStatus> listStatusIterator(final Path f) throws IOException, UnresolvedLinkException {
        return this.myFs.listStatusIterator(this.fullPath(f));
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws IOException, UnresolvedLinkException {
        return this.myFs.listLocatedStatus(this.fullPath(f));
    }
    
    @Override
    public void mkdir(final Path dir, final FsPermission permission, final boolean createParent) throws IOException, UnresolvedLinkException {
        this.myFs.mkdir(this.fullPath(dir), permission, createParent);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException, UnresolvedLinkException {
        return this.myFs.open(this.fullPath(f), bufferSize);
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException, UnresolvedLinkException {
        return this.myFs.truncate(this.fullPath(f), newLength);
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst) throws IOException, UnresolvedLinkException {
        this.myFs.renameInternal(this.fullPath(src), this.fullPath(dst));
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst, final boolean overwrite) throws IOException, UnresolvedLinkException {
        this.myFs.renameInternal(this.fullPath(src), this.fullPath(dst), overwrite);
    }
    
    @Override
    public void setOwner(final Path f, final String username, final String groupname) throws IOException, UnresolvedLinkException {
        this.myFs.setOwner(this.fullPath(f), username, groupname);
    }
    
    @Override
    public void setPermission(final Path f, final FsPermission permission) throws IOException, UnresolvedLinkException {
        this.myFs.setPermission(this.fullPath(f), permission);
    }
    
    @Override
    public boolean setReplication(final Path f, final short replication) throws IOException, UnresolvedLinkException {
        return this.myFs.setReplication(this.fullPath(f), replication);
    }
    
    @Override
    public void setTimes(final Path f, final long mtime, final long atime) throws IOException, UnresolvedLinkException {
        this.myFs.setTimes(this.fullPath(f), mtime, atime);
    }
    
    @Override
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.myFs.modifyAclEntries(this.fullPath(path), aclSpec);
    }
    
    @Override
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.myFs.removeAclEntries(this.fullPath(path), aclSpec);
    }
    
    @Override
    public void removeDefaultAcl(final Path path) throws IOException {
        this.myFs.removeDefaultAcl(this.fullPath(path));
    }
    
    @Override
    public void removeAcl(final Path path) throws IOException {
        this.myFs.removeAcl(this.fullPath(path));
    }
    
    @Override
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.myFs.setAcl(this.fullPath(path), aclSpec);
    }
    
    @Override
    public AclStatus getAclStatus(final Path path) throws IOException {
        return this.myFs.getAclStatus(this.fullPath(path));
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        this.myFs.setXAttr(this.fullPath(path), name, value, flag);
    }
    
    @Override
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        return this.myFs.getXAttr(this.fullPath(path), name);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        return this.myFs.getXAttrs(this.fullPath(path));
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        return this.myFs.getXAttrs(this.fullPath(path), names);
    }
    
    @Override
    public List<String> listXAttrs(final Path path) throws IOException {
        return this.myFs.listXAttrs(this.fullPath(path));
    }
    
    @Override
    public void removeXAttr(final Path path, final String name) throws IOException {
        this.myFs.removeXAttr(this.fullPath(path), name);
    }
    
    @Override
    public Path createSnapshot(final Path path, final String name) throws IOException {
        return this.myFs.createSnapshot(this.fullPath(path), name);
    }
    
    @Override
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        this.myFs.renameSnapshot(this.fullPath(path), snapshotOldName, snapshotNewName);
    }
    
    @Override
    public void deleteSnapshot(final Path snapshotDir, final String snapshotName) throws IOException {
        this.myFs.deleteSnapshot(this.fullPath(snapshotDir), snapshotName);
    }
    
    @Override
    public void setStoragePolicy(final Path path, final String policyName) throws IOException {
        this.myFs.setStoragePolicy(this.fullPath(path), policyName);
    }
    
    @Override
    public void unsetStoragePolicy(final Path src) throws IOException {
        this.myFs.unsetStoragePolicy(this.fullPath(src));
    }
    
    @Override
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        return this.myFs.getStoragePolicy(src);
    }
    
    @Override
    public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
        return this.myFs.getAllStoragePolicies();
    }
    
    @Override
    public void setVerifyChecksum(final boolean verifyChecksum) throws IOException, UnresolvedLinkException {
        this.myFs.setVerifyChecksum(verifyChecksum);
    }
    
    @Override
    public boolean supportsSymlinks() {
        return this.myFs.supportsSymlinks();
    }
    
    @Override
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws IOException, UnresolvedLinkException {
        this.myFs.createSymlink(this.fullPath(target), link, createParent);
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        return this.myFs.getLinkTarget(this.fullPath(f));
    }
    
    @Override
    public List<Token<?>> getDelegationTokens(final String renewer) throws IOException {
        return this.myFs.getDelegationTokens(renewer);
    }
}
