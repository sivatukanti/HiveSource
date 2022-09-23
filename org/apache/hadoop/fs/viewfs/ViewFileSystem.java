// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.fs.permission.AclUtil;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FsStatus;
import java.util.ArrayList;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FsServerDefaults;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.fs.XAttrSetFlag;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.CreateFlag;
import java.util.EnumSet;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.FsConstants;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.FileSystem;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ViewFileSystem extends FileSystem
{
    private static final Path ROOT_PATH;
    final long creationTime;
    final UserGroupInformation ugi;
    URI myUri;
    private Path workingDir;
    Configuration config;
    InodeTree<FileSystem> fsState;
    Path homeDir;
    private RenameStrategy renameStrategy;
    
    static AccessControlException readOnlyMountTable(final String operation, final String p) {
        return new AccessControlException("InternalDir of ViewFileSystem is readonly; operation=" + operation + "Path=" + p);
    }
    
    static AccessControlException readOnlyMountTable(final String operation, final Path p) {
        return readOnlyMountTable(operation, p.toString());
    }
    
    String getUriPath(final Path p) {
        this.checkPath(p);
        return this.makeAbsolute(p).toUri().getPath();
    }
    
    private Path makeAbsolute(final Path f) {
        return f.isAbsolute() ? f : new Path(this.workingDir, f);
    }
    
    public ViewFileSystem() throws IOException {
        this.homeDir = null;
        this.renameStrategy = RenameStrategy.SAME_MOUNTPOINT;
        this.ugi = UserGroupInformation.getCurrentUser();
        this.creationTime = Time.now();
    }
    
    @Override
    public String getScheme() {
        return "viewfs";
    }
    
    @Override
    public void initialize(final URI theUri, final Configuration conf) throws IOException {
        super.initialize(theUri, conf);
        this.setConf(conf);
        this.config = conf;
        final String authority = theUri.getAuthority();
        try {
            this.myUri = new URI("viewfs", authority, "/", null, null);
            this.fsState = new InodeTree<FileSystem>(conf, authority) {
                @Override
                protected FileSystem getTargetFileSystem(final URI uri) throws URISyntaxException, IOException {
                    return new ChRootedFileSystem(uri, ViewFileSystem.this.config);
                }
                
                @Override
                protected FileSystem getTargetFileSystem(final INodeDir<FileSystem> dir) throws URISyntaxException {
                    return new InternalDirOfViewFs(dir, ViewFileSystem.this.creationTime, ViewFileSystem.this.ugi, ViewFileSystem.this.myUri, ViewFileSystem.this.config);
                }
                
                @Override
                protected FileSystem getTargetFileSystem(final String settings, final URI[] uris) throws URISyntaxException, IOException {
                    return NflyFSystem.createFileSystem(uris, ViewFileSystem.this.config, settings);
                }
            };
            this.workingDir = this.getHomeDirectory();
            this.renameStrategy = RenameStrategy.valueOf(conf.get("fs.viewfs.rename.strategy", RenameStrategy.SAME_MOUNTPOINT.toString()));
        }
        catch (URISyntaxException e) {
            throw new IOException("URISyntax exception: " + theUri);
        }
    }
    
    ViewFileSystem(final URI theUri, final Configuration conf) throws IOException {
        this();
        this.initialize(theUri, conf);
    }
    
    public ViewFileSystem(final Configuration conf) throws IOException {
        this(FsConstants.VIEWFS_URI, conf);
    }
    
    @Override
    public URI getUri() {
        return this.myUri;
    }
    
    @Override
    public Path resolvePath(final Path f) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        if (res.isInternalDir()) {
            return f;
        }
        return res.targetFileSystem.resolvePath(res.remainingPath);
    }
    
    @Override
    public Path getHomeDirectory() {
        if (this.homeDir == null) {
            String base = this.fsState.getHomeDirPrefixValue();
            if (base == null) {
                base = "/user";
            }
            this.homeDir = (base.equals("/") ? this.makeQualified(new Path(base + this.ugi.getShortUserName())) : this.makeQualified(new Path(base + "/" + this.ugi.getShortUserName())));
        }
        return this.homeDir;
    }
    
    @Override
    public Path getWorkingDirectory() {
        return this.workingDir;
    }
    
    @Override
    public void setWorkingDirectory(final Path new_dir) {
        this.getUriPath(new_dir);
        this.workingDir = this.makeAbsolute(new_dir);
    }
    
    @Override
    public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.append(res.remainingPath, bufferSize, progress);
    }
    
    @Override
    public FSDataOutputStream createNonRecursive(final Path f, final FsPermission permission, final EnumSet<CreateFlag> flags, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        InodeTree.ResolveResult<FileSystem> res;
        try {
            res = this.fsState.resolve(this.getUriPath(f), false);
        }
        catch (FileNotFoundException e) {
            throw readOnlyMountTable("create", f);
        }
        assert res.remainingPath != null;
        return res.targetFileSystem.createNonRecursive(res.remainingPath, permission, flags, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws IOException {
        InodeTree.ResolveResult<FileSystem> res;
        try {
            res = this.fsState.resolve(this.getUriPath(f), false);
        }
        catch (FileNotFoundException e) {
            throw readOnlyMountTable("create", f);
        }
        assert res.remainingPath != null;
        return res.targetFileSystem.create(res.remainingPath, permission, overwrite, bufferSize, replication, blockSize, progress);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        if (res.isInternalDir() || res.remainingPath == InodeTree.SlashPath) {
            throw readOnlyMountTable("delete", f);
        }
        return res.targetFileSystem.delete(res.remainingPath, recursive);
    }
    
    @Override
    public boolean delete(final Path f) throws AccessControlException, FileNotFoundException, IOException {
        return this.delete(f, true);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final FileStatus fs, final long start, final long len) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(fs.getPath()), true);
        return res.targetFileSystem.getFileBlockLocations(new ViewFsFileStatus(fs, res.remainingPath), start, len);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.getFileChecksum(res.remainingPath);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f, final long length) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.getFileChecksum(res.remainingPath, length);
    }
    
    private static FileStatus fixFileStatus(FileStatus orig, final Path qualified) throws IOException {
        if ("file".equals(orig.getPath().toUri().getScheme())) {
            orig = wrapLocalFileStatus(orig, qualified);
        }
        orig.setPath(qualified);
        return orig;
    }
    
    private static FileStatus wrapLocalFileStatus(final FileStatus orig, final Path qualified) {
        return (orig instanceof LocatedFileStatus) ? new ViewFsLocatedFileStatus((LocatedFileStatus)orig, qualified) : new ViewFsFileStatus(orig, qualified);
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final FileStatus status = res.targetFileSystem.getFileStatus(res.remainingPath);
        return fixFileStatus(status, this.makeQualified(f));
    }
    
    @Override
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.access(res.remainingPath, mode);
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final FileStatus[] statusLst = res.targetFileSystem.listStatus(res.remainingPath);
        if (!res.isInternalDir()) {
            int i = 0;
            for (final FileStatus status : statusLst) {
                statusLst[i++] = fixFileStatus(status, this.getChrootedPath(res, status, f));
            }
        }
        return statusLst;
    }
    
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f, final PathFilter filter) throws FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final RemoteIterator<LocatedFileStatus> statusIter = res.targetFileSystem.listLocatedStatus(res.remainingPath);
        if (res.isInternalDir()) {
            return statusIter;
        }
        return new RemoteIterator<LocatedFileStatus>() {
            @Override
            public boolean hasNext() throws IOException {
                return statusIter.hasNext();
            }
            
            @Override
            public LocatedFileStatus next() throws IOException {
                final LocatedFileStatus status = statusIter.next();
                return (LocatedFileStatus)fixFileStatus(status, ViewFileSystem.this.getChrootedPath(res, status, f));
            }
        };
    }
    
    private Path getChrootedPath(final InodeTree.ResolveResult<FileSystem> res, final FileStatus status, final Path f) throws IOException {
        String suffix;
        if (res.targetFileSystem instanceof ChRootedFileSystem) {
            suffix = ((ChRootedFileSystem)res.targetFileSystem).stripOutRoot(status.getPath());
        }
        else {
            suffix = ((NflyFSystem.NflyStatus)status).stripRoot();
        }
        return this.makeQualified((suffix.length() == 0) ? f : new Path(res.resolvedPath, suffix));
    }
    
    @Override
    public boolean mkdirs(final Path dir, final FsPermission permission) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(dir), false);
        return res.targetFileSystem.mkdirs(res.remainingPath, permission);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.open(res.remainingPath, bufferSize);
    }
    
    @Override
    public boolean rename(final Path src, final Path dst) throws IOException {
        final InodeTree.ResolveResult<FileSystem> resSrc = this.fsState.resolve(this.getUriPath(src), false);
        if (resSrc.isInternalDir()) {
            throw readOnlyMountTable("rename", src);
        }
        final InodeTree.ResolveResult<FileSystem> resDst = this.fsState.resolve(this.getUriPath(dst), false);
        if (resDst.isInternalDir()) {
            throw readOnlyMountTable("rename", dst);
        }
        final URI srcUri = resSrc.targetFileSystem.getUri();
        final URI dstUri = resDst.targetFileSystem.getUri();
        verifyRenameStrategy(srcUri, dstUri, resSrc.targetFileSystem == resDst.targetFileSystem, this.renameStrategy);
        if (resSrc.targetFileSystem instanceof ChRootedFileSystem && resDst.targetFileSystem instanceof ChRootedFileSystem) {
            final ChRootedFileSystem srcFS = (ChRootedFileSystem)resSrc.targetFileSystem;
            final ChRootedFileSystem dstFS = (ChRootedFileSystem)resDst.targetFileSystem;
            return srcFS.getMyFs().rename(srcFS.fullPath(resSrc.remainingPath), dstFS.fullPath(resDst.remainingPath));
        }
        return resSrc.targetFileSystem.rename(resSrc.remainingPath, resDst.remainingPath);
    }
    
    static void verifyRenameStrategy(final URI srcUri, final URI dstUri, final boolean isSrcDestSame, final RenameStrategy renameStrategy) throws IOException {
        switch (renameStrategy) {
            case SAME_FILESYSTEM_ACROSS_MOUNTPOINT: {
                if (srcUri.getAuthority() != null && (!srcUri.getScheme().equals(dstUri.getScheme()) || !srcUri.getAuthority().equals(dstUri.getAuthority()))) {
                    throw new IOException("Renames across Mount points not supported");
                }
                break;
            }
            case SAME_TARGET_URI_ACROSS_MOUNTPOINT: {
                if (!srcUri.equals(dstUri)) {
                    throw new IOException("Renames across Mount points not supported");
                }
                break;
            }
            case SAME_MOUNTPOINT: {
                if (!isSrcDestSame) {
                    throw new IOException("Renames across Mount points not supported");
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unexpected rename strategy");
            }
        }
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.truncate(res.remainingPath, newLength);
    }
    
    @Override
    public void setOwner(final Path f, final String username, final String groupname) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        res.targetFileSystem.setOwner(res.remainingPath, username, groupname);
    }
    
    @Override
    public void setPermission(final Path f, final FsPermission permission) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        res.targetFileSystem.setPermission(res.remainingPath, permission);
    }
    
    @Override
    public boolean setReplication(final Path f, final short replication) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.setReplication(res.remainingPath, replication);
    }
    
    @Override
    public void setTimes(final Path f, final long mtime, final long atime) throws AccessControlException, FileNotFoundException, IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        res.targetFileSystem.setTimes(res.remainingPath, mtime, atime);
    }
    
    @Override
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.modifyAclEntries(res.remainingPath, aclSpec);
    }
    
    @Override
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeAclEntries(res.remainingPath, aclSpec);
    }
    
    @Override
    public void removeDefaultAcl(final Path path) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeDefaultAcl(res.remainingPath);
    }
    
    @Override
    public void removeAcl(final Path path) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeAcl(res.remainingPath);
    }
    
    @Override
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.setAcl(res.remainingPath, aclSpec);
    }
    
    @Override
    public AclStatus getAclStatus(final Path path) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getAclStatus(res.remainingPath);
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.setXAttr(res.remainingPath, name, value, flag);
    }
    
    @Override
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getXAttr(res.remainingPath, name);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getXAttrs(res.remainingPath);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getXAttrs(res.remainingPath, names);
    }
    
    @Override
    public List<String> listXAttrs(final Path path) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.listXAttrs(res.remainingPath);
    }
    
    @Override
    public void removeXAttr(final Path path, final String name) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeXAttr(res.remainingPath, name);
    }
    
    @Override
    public void setVerifyChecksum(final boolean verifyChecksum) {
        final List<InodeTree.MountPoint<FileSystem>> mountPoints = this.fsState.getMountPoints();
        for (final InodeTree.MountPoint<FileSystem> mount : mountPoints) {
            mount.target.targetFileSystem.setVerifyChecksum(verifyChecksum);
        }
    }
    
    @Override
    public long getDefaultBlockSize() {
        throw new NotInMountpointException("getDefaultBlockSize");
    }
    
    @Override
    public short getDefaultReplication() {
        throw new NotInMountpointException("getDefaultReplication");
    }
    
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        throw new NotInMountpointException("getServerDefaults");
    }
    
    @Override
    public long getDefaultBlockSize(final Path f) {
        try {
            final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
            return res.targetFileSystem.getDefaultBlockSize(res.remainingPath);
        }
        catch (FileNotFoundException e) {
            throw new NotInMountpointException(f, "getDefaultBlockSize");
        }
    }
    
    @Override
    public short getDefaultReplication(final Path f) {
        try {
            final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
            return res.targetFileSystem.getDefaultReplication(res.remainingPath);
        }
        catch (FileNotFoundException e) {
            throw new NotInMountpointException(f, "getDefaultReplication");
        }
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        try {
            final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
            return res.targetFileSystem.getServerDefaults(res.remainingPath);
        }
        catch (FileNotFoundException e) {
            throw new NotInMountpointException(f, "getServerDefaults");
        }
    }
    
    @Override
    public ContentSummary getContentSummary(final Path f) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.getContentSummary(res.remainingPath);
    }
    
    @Override
    public QuotaUsage getQuotaUsage(final Path f) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.getQuotaUsage(res.remainingPath);
    }
    
    @Override
    public void setWriteChecksum(final boolean writeChecksum) {
        final List<InodeTree.MountPoint<FileSystem>> mountPoints = this.fsState.getMountPoints();
        for (final InodeTree.MountPoint<FileSystem> mount : mountPoints) {
            mount.target.targetFileSystem.setWriteChecksum(writeChecksum);
        }
    }
    
    @Override
    public FileSystem[] getChildFileSystems() {
        final List<InodeTree.MountPoint<FileSystem>> mountPoints = this.fsState.getMountPoints();
        final Set<FileSystem> children = new HashSet<FileSystem>();
        for (final InodeTree.MountPoint<FileSystem> mountPoint : mountPoints) {
            final FileSystem targetFs = mountPoint.target.targetFileSystem;
            children.addAll(Arrays.asList(targetFs.getChildFileSystems()));
        }
        return children.toArray(new FileSystem[0]);
    }
    
    public MountPoint[] getMountPoints() {
        final List<InodeTree.MountPoint<FileSystem>> mountPoints = this.fsState.getMountPoints();
        final MountPoint[] result = new MountPoint[mountPoints.size()];
        for (int i = 0; i < mountPoints.size(); ++i) {
            result[i] = new MountPoint(new Path(mountPoints.get(i).src), mountPoints.get(i).target.targetDirLinkList);
        }
        return result;
    }
    
    @Override
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.createSnapshot(res.remainingPath, snapshotName);
    }
    
    @Override
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.renameSnapshot(res.remainingPath, snapshotOldName, snapshotNewName);
    }
    
    @Override
    public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.deleteSnapshot(res.remainingPath, snapshotName);
    }
    
    @Override
    public void setStoragePolicy(final Path src, final String policyName) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(src), true);
        res.targetFileSystem.setStoragePolicy(res.remainingPath, policyName);
    }
    
    @Override
    public void unsetStoragePolicy(final Path src) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(src), true);
        res.targetFileSystem.unsetStoragePolicy(res.remainingPath);
    }
    
    @Override
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(src), true);
        return res.targetFileSystem.getStoragePolicy(res.remainingPath);
    }
    
    @Override
    public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
        final Collection<BlockStoragePolicySpi> allPolicies = new HashSet<BlockStoragePolicySpi>();
        for (final FileSystem fs : this.getChildFileSystems()) {
            try {
                final Collection<? extends BlockStoragePolicySpi> policies = fs.getAllStoragePolicies();
                allPolicies.addAll(policies);
            }
            catch (UnsupportedOperationException ex) {}
        }
        return allPolicies;
    }
    
    @Override
    public Path getTrashRoot(final Path path) {
        try {
            final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
            return res.targetFileSystem.getTrashRoot(res.remainingPath);
        }
        catch (Exception e) {
            throw new NotInMountpointException(path, "getTrashRoot");
        }
    }
    
    @Override
    public Collection<FileStatus> getTrashRoots(final boolean allUsers) {
        final List<FileStatus> trashRoots = new ArrayList<FileStatus>();
        for (final FileSystem fs : this.getChildFileSystems()) {
            trashRoots.addAll(fs.getTrashRoots(allUsers));
        }
        return trashRoots;
    }
    
    @Override
    public FsStatus getStatus() throws IOException {
        return this.getStatus(null);
    }
    
    @Override
    public FsStatus getStatus(Path p) throws IOException {
        if (p == null) {
            p = InodeTree.SlashPath;
        }
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(p), true);
        return res.targetFileSystem.getStatus(p);
    }
    
    @Override
    public long getUsed() throws IOException {
        final InodeTree.ResolveResult<FileSystem> res = this.fsState.resolve(this.getUriPath(InodeTree.SlashPath), true);
        if (res.isInternalDir()) {
            throw new NotInMountpointException(InodeTree.SlashPath, "getUsed");
        }
        return res.targetFileSystem.getUsed();
    }
    
    @Override
    public Path getLinkTarget(final Path path) throws IOException {
        InodeTree.ResolveResult<FileSystem> res;
        try {
            res = this.fsState.resolve(this.getUriPath(path), true);
        }
        catch (FileNotFoundException e) {
            throw new NotInMountpointException(path, "getLinkTarget");
        }
        return res.targetFileSystem.getLinkTarget(res.remainingPath);
    }
    
    static {
        ROOT_PATH = new Path("/");
    }
    
    public static class MountPoint
    {
        private final Path mountedOnPath;
        private final URI[] targetFileSystemURIs;
        
        MountPoint(final Path srcPath, final URI[] targetFs) {
            this.mountedOnPath = srcPath;
            this.targetFileSystemURIs = targetFs;
        }
        
        public Path getMountedOnPath() {
            return this.mountedOnPath;
        }
        
        public URI[] getTargetFileSystemURIs() {
            return this.targetFileSystemURIs;
        }
    }
    
    static class InternalDirOfViewFs extends FileSystem
    {
        final InodeTree.INodeDir<FileSystem> theInternalDir;
        final long creationTime;
        final UserGroupInformation ugi;
        final URI myUri;
        
        public InternalDirOfViewFs(final InodeTree.INodeDir<FileSystem> dir, final long cTime, final UserGroupInformation ugi, final URI uri, final Configuration config) throws URISyntaxException {
            this.myUri = uri;
            try {
                this.initialize(this.myUri, config);
            }
            catch (IOException e) {
                throw new RuntimeException("Cannot occur");
            }
            this.theInternalDir = dir;
            this.creationTime = cTime;
            this.ugi = ugi;
        }
        
        private static void checkPathIsSlash(final Path f) throws IOException {
            if (f != InodeTree.SlashPath) {
                throw new IOException("Internal implementation error: expected file name to be /");
            }
        }
        
        @Override
        public URI getUri() {
            return this.myUri;
        }
        
        @Override
        public Path getWorkingDirectory() {
            throw new RuntimeException("Internal impl error: getWorkingDir should not have been called");
        }
        
        @Override
        public void setWorkingDirectory(final Path new_dir) {
            throw new RuntimeException("Internal impl error: getWorkingDir should not have been called");
        }
        
        @Override
        public FSDataOutputStream append(final Path f, final int bufferSize, final Progressable progress) throws IOException {
            throw ViewFileSystem.readOnlyMountTable("append", f);
        }
        
        @Override
        public FSDataOutputStream create(final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress) throws AccessControlException {
            throw ViewFileSystem.readOnlyMountTable("create", f);
        }
        
        @Override
        public boolean delete(final Path f, final boolean recursive) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFileSystem.readOnlyMountTable("delete", f);
        }
        
        @Override
        public boolean delete(final Path f) throws AccessControlException, IOException {
            return this.delete(f, true);
        }
        
        @Override
        public BlockLocation[] getFileBlockLocations(final FileStatus fs, final long start, final long len) throws FileNotFoundException, IOException {
            checkPathIsSlash(fs.getPath());
            throw new FileNotFoundException("Path points to dir not a file");
        }
        
        @Override
        public FileChecksum getFileChecksum(final Path f) throws FileNotFoundException, IOException {
            checkPathIsSlash(f);
            throw new FileNotFoundException("Path points to dir not a file");
        }
        
        @Override
        public FileStatus getFileStatus(final Path f) throws IOException {
            checkPathIsSlash(f);
            return new FileStatus(0L, true, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getPrimaryGroupName(), new Path(this.theInternalDir.fullPath).makeQualified(this.myUri, ViewFileSystem.ROOT_PATH));
        }
        
        @Override
        public FileStatus[] listStatus(final Path f) throws AccessControlException, FileNotFoundException, IOException {
            checkPathIsSlash(f);
            final FileStatus[] result = new FileStatus[this.theInternalDir.getChildren().size()];
            int i = 0;
            for (final Map.Entry<String, InodeTree.INode<FileSystem>> iEntry : this.theInternalDir.getChildren().entrySet()) {
                final InodeTree.INode<FileSystem> inode = iEntry.getValue();
                if (inode.isLink()) {
                    final InodeTree.INodeLink<FileSystem> link = (InodeTree.INodeLink<FileSystem>)(InodeTree.INodeLink)inode;
                    result[i++] = new FileStatus(0L, false, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getPrimaryGroupName(), link.getTargetLink(), new Path(inode.fullPath).makeQualified(this.myUri, null));
                }
                else {
                    result[i++] = new FileStatus(0L, true, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getGroupNames()[0], new Path(inode.fullPath).makeQualified(this.myUri, null));
                }
            }
            return result;
        }
        
        @Override
        public boolean mkdirs(final Path dir, final FsPermission permission) throws AccessControlException, FileAlreadyExistsException {
            if (this.theInternalDir.isRoot() && dir == null) {
                throw new FileAlreadyExistsException("/ already exits");
            }
            if (this.theInternalDir.getChildren().containsKey(dir.toString().substring(1))) {
                return true;
            }
            throw ViewFileSystem.readOnlyMountTable("mkdirs", dir);
        }
        
        @Override
        public FSDataInputStream open(final Path f, final int bufferSize) throws AccessControlException, FileNotFoundException, IOException {
            checkPathIsSlash(f);
            throw new FileNotFoundException("Path points to dir not a file");
        }
        
        @Override
        public boolean rename(final Path src, final Path dst) throws AccessControlException, IOException {
            checkPathIsSlash(src);
            checkPathIsSlash(dst);
            throw ViewFileSystem.readOnlyMountTable("rename", src);
        }
        
        @Override
        public boolean truncate(final Path f, final long newLength) throws IOException {
            throw ViewFileSystem.readOnlyMountTable("truncate", f);
        }
        
        @Override
        public void setOwner(final Path f, final String username, final String groupname) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFileSystem.readOnlyMountTable("setOwner", f);
        }
        
        @Override
        public void setPermission(final Path f, final FsPermission permission) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFileSystem.readOnlyMountTable("setPermission", f);
        }
        
        @Override
        public boolean setReplication(final Path f, final short replication) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFileSystem.readOnlyMountTable("setReplication", f);
        }
        
        @Override
        public void setTimes(final Path f, final long mtime, final long atime) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFileSystem.readOnlyMountTable("setTimes", f);
        }
        
        @Override
        public void setVerifyChecksum(final boolean verifyChecksum) {
        }
        
        @Override
        public FsServerDefaults getServerDefaults(final Path f) throws IOException {
            throw new NotInMountpointException(f, "getServerDefaults");
        }
        
        @Override
        public long getDefaultBlockSize(final Path f) {
            throw new NotInMountpointException(f, "getDefaultBlockSize");
        }
        
        @Override
        public short getDefaultReplication(final Path f) {
            throw new NotInMountpointException(f, "getDefaultReplication");
        }
        
        @Override
        public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("modifyAclEntries", path);
        }
        
        @Override
        public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("removeAclEntries", path);
        }
        
        @Override
        public void removeDefaultAcl(final Path path) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("removeDefaultAcl", path);
        }
        
        @Override
        public void removeAcl(final Path path) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("removeAcl", path);
        }
        
        @Override
        public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("setAcl", path);
        }
        
        @Override
        public AclStatus getAclStatus(final Path path) throws IOException {
            checkPathIsSlash(path);
            return new AclStatus.Builder().owner(this.ugi.getShortUserName()).group(this.ugi.getPrimaryGroupName()).addEntries(AclUtil.getMinimalAcl(Constants.PERMISSION_555)).stickyBit(false).build();
        }
        
        @Override
        public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("setXAttr", path);
        }
        
        @Override
        public byte[] getXAttr(final Path path, final String name) throws IOException {
            throw new NotInMountpointException(path, "getXAttr");
        }
        
        @Override
        public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
            throw new NotInMountpointException(path, "getXAttrs");
        }
        
        @Override
        public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
            throw new NotInMountpointException(path, "getXAttrs");
        }
        
        @Override
        public List<String> listXAttrs(final Path path) throws IOException {
            throw new NotInMountpointException(path, "listXAttrs");
        }
        
        @Override
        public void removeXAttr(final Path path, final String name) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("removeXAttr", path);
        }
        
        @Override
        public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("createSnapshot", path);
        }
        
        @Override
        public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("renameSnapshot", path);
        }
        
        @Override
        public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
            checkPathIsSlash(path);
            throw ViewFileSystem.readOnlyMountTable("deleteSnapshot", path);
        }
        
        @Override
        public QuotaUsage getQuotaUsage(final Path f) throws IOException {
            throw new NotInMountpointException(f, "getQuotaUsage");
        }
        
        @Override
        public void setStoragePolicy(final Path src, final String policyName) throws IOException {
            checkPathIsSlash(src);
            throw ViewFileSystem.readOnlyMountTable("setStoragePolicy", src);
        }
        
        @Override
        public void unsetStoragePolicy(final Path src) throws IOException {
            checkPathIsSlash(src);
            throw ViewFileSystem.readOnlyMountTable("unsetStoragePolicy", src);
        }
        
        @Override
        public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
            throw new NotInMountpointException(src, "getStoragePolicy");
        }
        
        @Override
        public Collection<? extends BlockStoragePolicySpi> getAllStoragePolicies() throws IOException {
            final Collection<BlockStoragePolicySpi> allPolicies = new HashSet<BlockStoragePolicySpi>();
            for (final FileSystem fs : this.getChildFileSystems()) {
                try {
                    final Collection<? extends BlockStoragePolicySpi> policies = fs.getAllStoragePolicies();
                    allPolicies.addAll(policies);
                }
                catch (UnsupportedOperationException ex) {}
            }
            return allPolicies;
        }
    }
    
    enum RenameStrategy
    {
        SAME_MOUNTPOINT, 
        SAME_TARGET_URI_ACROSS_MOUNTPOINT, 
        SAME_FILESYSTEM_ACROSS_MOUNTPOINT;
    }
}
