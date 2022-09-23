// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Collection;
import java.util.Map;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.security.token.Token;
import java.util.List;
import java.net.URI;
import java.io.FileNotFoundException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.fs.permission.FsAction;
import java.io.IOException;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.util.EnumSet;
import java.net.URISyntaxException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class FilterFs extends AbstractFileSystem
{
    private final AbstractFileSystem myFs;
    
    protected AbstractFileSystem getMyFs() {
        return this.myFs;
    }
    
    protected FilterFs(final AbstractFileSystem fs) throws URISyntaxException {
        super(fs.getUri(), fs.getUri().getScheme(), false, fs.getUriDefaultPort());
        this.myFs = fs;
    }
    
    @Override
    public FileSystem.Statistics getStatistics() {
        return this.myFs.getStatistics();
    }
    
    @Override
    public Path makeQualified(final Path path) {
        return this.myFs.makeQualified(path);
    }
    
    @Override
    public Path getInitialWorkingDirectory() {
        return this.myFs.getInitialWorkingDirectory();
    }
    
    @Override
    public Path getHomeDirectory() {
        return this.myFs.getHomeDirectory();
    }
    
    @Override
    public FSDataOutputStream createInternal(final Path f, final EnumSet<CreateFlag> flag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.createInternal(f, flag, absolutePermission, bufferSize, replication, blockSize, progress, checksumOpt, createParent);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.delete(f, recursive);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final Path f, final long start, final long len) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.getFileBlockLocations(f, start, len);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.getFileChecksum(f);
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.getFileStatus(f);
    }
    
    @Override
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        this.checkPath(path);
        this.myFs.access(path, mode);
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.getFileLinkStatus(f);
    }
    
    @Override
    public FsStatus getFsStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        return this.myFs.getFsStatus(f);
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
        return this.myFs.getServerDefaults(f);
    }
    
    @Override
    public Path resolvePath(final Path p) throws FileNotFoundException, UnresolvedLinkException, AccessControlException, IOException {
        return this.myFs.resolvePath(p);
    }
    
    @Override
    public int getUriDefaultPort() {
        return this.myFs.getUriDefaultPort();
    }
    
    @Override
    public URI getUri() {
        return this.myFs.getUri();
    }
    
    @Override
    public void checkPath(final Path path) {
        this.myFs.checkPath(path);
    }
    
    @Override
    public String getUriPath(final Path p) {
        return this.myFs.getUriPath(p);
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.listStatus(f);
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        this.checkPath(f);
        return this.myFs.listLocatedStatus(f);
    }
    
    @Override
    public RemoteIterator<Path> listCorruptFileBlocks(final Path path) throws IOException {
        return this.myFs.listCorruptFileBlocks(path);
    }
    
    @Override
    public void mkdir(final Path dir, final FsPermission permission, final boolean createParent) throws IOException, UnresolvedLinkException {
        this.checkPath(dir);
        this.myFs.mkdir(dir, permission, createParent);
    }
    
    @Override
    public FSDataInputStream open(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        this.checkPath(f);
        return this.myFs.open(f);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.open(f, bufferSize);
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        this.checkPath(f);
        return this.myFs.truncate(f, newLength);
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst) throws IOException, UnresolvedLinkException {
        this.checkPath(src);
        this.checkPath(dst);
        this.myFs.rename(src, dst, Options.Rename.NONE);
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst, final boolean overwrite) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnresolvedLinkException, IOException {
        this.myFs.renameInternal(src, dst, overwrite);
    }
    
    @Override
    public void setOwner(final Path f, final String username, final String groupname) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        this.myFs.setOwner(f, username, groupname);
    }
    
    @Override
    public void setPermission(final Path f, final FsPermission permission) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        this.myFs.setPermission(f, permission);
    }
    
    @Override
    public boolean setReplication(final Path f, final short replication) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        return this.myFs.setReplication(f, replication);
    }
    
    @Override
    public void setTimes(final Path f, final long mtime, final long atime) throws IOException, UnresolvedLinkException {
        this.checkPath(f);
        this.myFs.setTimes(f, mtime, atime);
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
        this.myFs.createSymlink(target, link, createParent);
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        return this.myFs.getLinkTarget(f);
    }
    
    @Override
    public String getCanonicalServiceName() {
        return this.myFs.getCanonicalServiceName();
    }
    
    @Override
    public List<Token<?>> getDelegationTokens(final String renewer) throws IOException {
        return this.myFs.getDelegationTokens(renewer);
    }
    
    @Override
    public boolean isValidName(final String src) {
        return this.myFs.isValidName(src);
    }
    
    @Override
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.myFs.modifyAclEntries(path, aclSpec);
    }
    
    @Override
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.myFs.removeAclEntries(path, aclSpec);
    }
    
    @Override
    public void removeDefaultAcl(final Path path) throws IOException {
        this.myFs.removeDefaultAcl(path);
    }
    
    @Override
    public void removeAcl(final Path path) throws IOException {
        this.myFs.removeAcl(path);
    }
    
    @Override
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        this.myFs.setAcl(path, aclSpec);
    }
    
    @Override
    public AclStatus getAclStatus(final Path path) throws IOException {
        return this.myFs.getAclStatus(path);
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value) throws IOException {
        this.myFs.setXAttr(path, name, value);
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        this.myFs.setXAttr(path, name, value, flag);
    }
    
    @Override
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        return this.myFs.getXAttr(path, name);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        return this.myFs.getXAttrs(path);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        return this.myFs.getXAttrs(path, names);
    }
    
    @Override
    public List<String> listXAttrs(final Path path) throws IOException {
        return this.myFs.listXAttrs(path);
    }
    
    @Override
    public void removeXAttr(final Path path, final String name) throws IOException {
        this.myFs.removeXAttr(path, name);
    }
    
    @Override
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        return this.myFs.createSnapshot(path, snapshotName);
    }
    
    @Override
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        this.myFs.renameSnapshot(path, snapshotOldName, snapshotNewName);
    }
    
    @Override
    public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
        this.myFs.deleteSnapshot(path, snapshotName);
    }
    
    @Override
    public void setStoragePolicy(final Path path, final String policyName) throws IOException {
        this.myFs.setStoragePolicy(path, policyName);
    }
    
    @Override
    public void unsetStoragePolicy(final Path src) throws IOException {
        this.myFs.unsetStoragePolicy(src);
    }
    
    @Override
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        return this.myFs.getStoragePolicy(src);
    }
    
    @Override
    public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
        return this.myFs.getAllStoragePolicies();
    }
}
