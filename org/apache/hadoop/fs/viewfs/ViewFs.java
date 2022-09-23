// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.fs.permission.AclUtil;
import org.apache.hadoop.fs.BlockStoragePolicySpi;
import java.util.Map;
import org.apache.hadoop.fs.XAttrSetFlag;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.AclEntry;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.hadoop.security.token.Token;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.ParentNotDirectoryException;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.CreateFlag;
import java.util.EnumSet;
import org.apache.hadoop.fs.UnresolvedLinkException;
import java.io.FileNotFoundException;
import org.apache.hadoop.fs.local.LocalConfigKeys;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.util.Time;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import org.apache.hadoop.fs.FsConstants;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.AbstractFileSystem;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ViewFs extends AbstractFileSystem
{
    final long creationTime;
    final UserGroupInformation ugi;
    final Configuration config;
    InodeTree<AbstractFileSystem> fsState;
    Path homeDir;
    private ViewFileSystem.RenameStrategy renameStrategy;
    
    static AccessControlException readOnlyMountTable(final String operation, final String p) {
        return new AccessControlException("InternalDir of ViewFileSystem is readonly; operation=" + operation + "Path=" + p);
    }
    
    static AccessControlException readOnlyMountTable(final String operation, final Path p) {
        return readOnlyMountTable(operation, p.toString());
    }
    
    public ViewFs(final Configuration conf) throws IOException, URISyntaxException {
        this(FsConstants.VIEWFS_URI, conf);
    }
    
    ViewFs(final URI theUri, final Configuration conf) throws IOException, URISyntaxException {
        super(theUri, "viewfs", false, -1);
        this.homeDir = null;
        this.renameStrategy = ViewFileSystem.RenameStrategy.SAME_MOUNTPOINT;
        this.creationTime = Time.now();
        this.ugi = UserGroupInformation.getCurrentUser();
        this.config = conf;
        final String authority = theUri.getAuthority();
        this.fsState = new InodeTree<AbstractFileSystem>(conf, authority) {
            @Override
            protected AbstractFileSystem getTargetFileSystem(final URI uri) throws URISyntaxException, UnsupportedFileSystemException {
                String pathString = uri.getPath();
                if (pathString.isEmpty()) {
                    pathString = "/";
                }
                return new ChRootedFs(AbstractFileSystem.createFileSystem(uri, ViewFs.this.config), new Path(pathString));
            }
            
            @Override
            protected AbstractFileSystem getTargetFileSystem(final INodeDir<AbstractFileSystem> dir) throws URISyntaxException {
                return new InternalDirOfViewFs(dir, ViewFs.this.creationTime, ViewFs.this.ugi, ViewFs.this.getUri());
            }
            
            @Override
            protected AbstractFileSystem getTargetFileSystem(final String settings, final URI[] mergeFsURIList) throws URISyntaxException, UnsupportedFileSystemException {
                throw new UnsupportedFileSystemException("mergefs not implemented yet");
            }
        };
        this.renameStrategy = ViewFileSystem.RenameStrategy.valueOf(conf.get("fs.viewfs.rename.strategy", ViewFileSystem.RenameStrategy.SAME_MOUNTPOINT.toString()));
    }
    
    @Deprecated
    @Override
    public FsServerDefaults getServerDefaults() throws IOException {
        return LocalConfigKeys.getServerDefaults();
    }
    
    @Override
    public FsServerDefaults getServerDefaults(final Path f) throws IOException {
        InodeTree.ResolveResult<AbstractFileSystem> res;
        try {
            res = this.fsState.resolve(this.getUriPath(f), true);
        }
        catch (FileNotFoundException fnfe) {
            return LocalConfigKeys.getServerDefaults();
        }
        return res.targetFileSystem.getServerDefaults(res.remainingPath);
    }
    
    @Override
    public int getUriDefaultPort() {
        return -1;
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
    public Path resolvePath(final Path f) throws FileNotFoundException, AccessControlException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        if (res.isInternalDir()) {
            return f;
        }
        return res.targetFileSystem.resolvePath(res.remainingPath);
    }
    
    @Override
    public FSDataOutputStream createInternal(final Path f, final EnumSet<CreateFlag> flag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, UnresolvedLinkException, IOException {
        InodeTree.ResolveResult<AbstractFileSystem> res;
        try {
            res = this.fsState.resolve(this.getUriPath(f), false);
        }
        catch (FileNotFoundException e) {
            if (createParent) {
                throw readOnlyMountTable("create", f);
            }
            throw e;
        }
        assert res.remainingPath != null;
        return res.targetFileSystem.createInternal(res.remainingPath, flag, absolutePermission, bufferSize, replication, blockSize, progress, checksumOpt, createParent);
    }
    
    @Override
    public boolean delete(final Path f, final boolean recursive) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        if (res.isInternalDir() || res.remainingPath == InodeTree.SlashPath) {
            throw new AccessControlException("Cannot delete internal mount table directory: " + f);
        }
        return res.targetFileSystem.delete(res.remainingPath, recursive);
    }
    
    @Override
    public BlockLocation[] getFileBlockLocations(final Path f, final long start, final long len) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.getFileBlockLocations(res.remainingPath, start, len);
    }
    
    @Override
    public FileChecksum getFileChecksum(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.getFileChecksum(res.remainingPath);
    }
    
    @Override
    public FileStatus getFileStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final FileStatus status = res.targetFileSystem.getFileStatus(res.remainingPath);
        return new ViewFsFileStatus(status, this.makeQualified(f));
    }
    
    @Override
    public void access(final Path path, final FsAction mode) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.access(res.remainingPath, mode);
    }
    
    @Override
    public FileStatus getFileLinkStatus(final Path f) throws AccessControlException, FileNotFoundException, UnsupportedFileSystemException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), false);
        return res.targetFileSystem.getFileLinkStatus(res.remainingPath);
    }
    
    @Override
    public FsStatus getFsStatus() throws AccessControlException, FileNotFoundException, IOException {
        return new FsStatus(0L, 0L, 0L);
    }
    
    @Override
    public RemoteIterator<FileStatus> listStatusIterator(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final RemoteIterator<FileStatus> fsIter = res.targetFileSystem.listStatusIterator(res.remainingPath);
        if (res.isInternalDir()) {
            return fsIter;
        }
        return new WrappingRemoteIterator<FileStatus>(res, fsIter, f) {
            public FileStatus getViewFsFileStatus(final FileStatus stat, final Path newPath) {
                return new ViewFsFileStatus(stat, newPath);
            }
        };
    }
    
    @Override
    public RemoteIterator<LocatedFileStatus> listLocatedStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final RemoteIterator<LocatedFileStatus> fsIter = res.targetFileSystem.listLocatedStatus(res.remainingPath);
        if (res.isInternalDir()) {
            return fsIter;
        }
        return new WrappingRemoteIterator<LocatedFileStatus>(res, fsIter, f) {
            public LocatedFileStatus getViewFsFileStatus(final LocatedFileStatus stat, final Path newPath) {
                return new ViewFsLocatedFileStatus(stat, newPath);
            }
        };
    }
    
    @Override
    public FileStatus[] listStatus(final Path f) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        final FileStatus[] statusLst = res.targetFileSystem.listStatus(res.remainingPath);
        if (!res.isInternalDir()) {
            final ChRootedFs targetFs = (ChRootedFs)res.targetFileSystem;
            int i = 0;
            for (final FileStatus status : statusLst) {
                final String suffix = targetFs.stripOutRoot(status.getPath());
                statusLst[i++] = new ViewFsFileStatus(status, this.makeQualified((suffix.length() == 0) ? f : new Path(res.resolvedPath, suffix)));
            }
        }
        return statusLst;
    }
    
    @Override
    public void mkdir(final Path dir, final FsPermission permission, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(dir), false);
        res.targetFileSystem.mkdir(res.remainingPath, permission, createParent);
    }
    
    @Override
    public FSDataInputStream open(final Path f, final int bufferSize) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.open(res.remainingPath, bufferSize);
    }
    
    @Override
    public boolean truncate(final Path f, final long newLength) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.truncate(res.remainingPath, newLength);
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst, final boolean overwrite) throws IOException, UnresolvedLinkException {
        final InodeTree.ResolveResult<AbstractFileSystem> resSrc = this.fsState.resolve(this.getUriPath(src), false);
        if (resSrc.isInternalDir()) {
            throw new AccessControlException("Cannot Rename within internal dirs of mount table: src=" + src + " is readOnly");
        }
        final InodeTree.ResolveResult<AbstractFileSystem> resDst = this.fsState.resolve(this.getUriPath(dst), false);
        if (resDst.isInternalDir()) {
            throw new AccessControlException("Cannot Rename within internal dirs of mount table: dest=" + dst + " is readOnly");
        }
        final URI srcUri = resSrc.targetFileSystem.getUri();
        final URI dstUri = resDst.targetFileSystem.getUri();
        ViewFileSystem.verifyRenameStrategy(srcUri, dstUri, resSrc.targetFileSystem == resDst.targetFileSystem, this.renameStrategy);
        final ChRootedFs srcFS = (ChRootedFs)resSrc.targetFileSystem;
        final ChRootedFs dstFS = (ChRootedFs)resDst.targetFileSystem;
        srcFS.getMyFs().renameInternal(srcFS.fullPath(resSrc.remainingPath), dstFS.fullPath(resDst.remainingPath), overwrite);
    }
    
    @Override
    public void renameInternal(final Path src, final Path dst) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnresolvedLinkException, IOException {
        this.renameInternal(src, dst, false);
    }
    
    @Override
    public boolean supportsSymlinks() {
        return true;
    }
    
    @Override
    public void createSymlink(final Path target, final Path link, final boolean createParent) throws IOException, UnresolvedLinkException {
        InodeTree.ResolveResult<AbstractFileSystem> res;
        try {
            res = this.fsState.resolve(this.getUriPath(link), false);
        }
        catch (FileNotFoundException e) {
            if (createParent) {
                throw readOnlyMountTable("createSymlink", link);
            }
            throw e;
        }
        assert res.remainingPath != null;
        res.targetFileSystem.createSymlink(target, res.remainingPath, createParent);
    }
    
    @Override
    public Path getLinkTarget(final Path f) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), false);
        return res.targetFileSystem.getLinkTarget(res.remainingPath);
    }
    
    @Override
    public void setOwner(final Path f, final String username, final String groupname) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        res.targetFileSystem.setOwner(res.remainingPath, username, groupname);
    }
    
    @Override
    public void setPermission(final Path f, final FsPermission permission) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        res.targetFileSystem.setPermission(res.remainingPath, permission);
    }
    
    @Override
    public boolean setReplication(final Path f, final short replication) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        return res.targetFileSystem.setReplication(res.remainingPath, replication);
    }
    
    @Override
    public void setTimes(final Path f, final long mtime, final long atime) throws AccessControlException, FileNotFoundException, UnresolvedLinkException, IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(f), true);
        res.targetFileSystem.setTimes(res.remainingPath, mtime, atime);
    }
    
    @Override
    public void setVerifyChecksum(final boolean verifyChecksum) throws AccessControlException, IOException {
    }
    
    public MountPoint[] getMountPoints() {
        final List<InodeTree.MountPoint<AbstractFileSystem>> mountPoints = this.fsState.getMountPoints();
        final MountPoint[] result = new MountPoint[mountPoints.size()];
        for (int i = 0; i < mountPoints.size(); ++i) {
            result[i] = new MountPoint(new Path(mountPoints.get(i).src), mountPoints.get(i).target.targetDirLinkList);
        }
        return result;
    }
    
    @Override
    public List<Token<?>> getDelegationTokens(final String renewer) throws IOException {
        final List<InodeTree.MountPoint<AbstractFileSystem>> mountPoints = this.fsState.getMountPoints();
        int initialListSize = 0;
        for (final InodeTree.MountPoint<AbstractFileSystem> im : mountPoints) {
            initialListSize += im.target.targetDirLinkList.length;
        }
        final List<Token<?>> result = new ArrayList<Token<?>>(initialListSize);
        for (int i = 0; i < mountPoints.size(); ++i) {
            final List<Token<?>> tokens = mountPoints.get(i).target.targetFileSystem.getDelegationTokens(renewer);
            if (tokens != null) {
                result.addAll(tokens);
            }
        }
        return result;
    }
    
    @Override
    public boolean isValidName(final String src) {
        return true;
    }
    
    @Override
    public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.modifyAclEntries(res.remainingPath, aclSpec);
    }
    
    @Override
    public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeAclEntries(res.remainingPath, aclSpec);
    }
    
    @Override
    public void removeDefaultAcl(final Path path) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeDefaultAcl(res.remainingPath);
    }
    
    @Override
    public void removeAcl(final Path path) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeAcl(res.remainingPath);
    }
    
    @Override
    public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.setAcl(res.remainingPath, aclSpec);
    }
    
    @Override
    public AclStatus getAclStatus(final Path path) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getAclStatus(res.remainingPath);
    }
    
    @Override
    public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.setXAttr(res.remainingPath, name, value, flag);
    }
    
    @Override
    public byte[] getXAttr(final Path path, final String name) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getXAttr(res.remainingPath, name);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getXAttrs(res.remainingPath);
    }
    
    @Override
    public Map<String, byte[]> getXAttrs(final Path path, final List<String> names) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.getXAttrs(res.remainingPath, names);
    }
    
    @Override
    public List<String> listXAttrs(final Path path) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.listXAttrs(res.remainingPath);
    }
    
    @Override
    public void removeXAttr(final Path path, final String name) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.removeXAttr(res.remainingPath, name);
    }
    
    @Override
    public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        return res.targetFileSystem.createSnapshot(res.remainingPath, snapshotName);
    }
    
    @Override
    public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.renameSnapshot(res.remainingPath, snapshotOldName, snapshotNewName);
    }
    
    @Override
    public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.deleteSnapshot(res.remainingPath, snapshotName);
    }
    
    @Override
    public void setStoragePolicy(final Path path, final String policyName) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(path), true);
        res.targetFileSystem.setStoragePolicy(res.remainingPath, policyName);
    }
    
    @Override
    public void unsetStoragePolicy(final Path src) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(src), true);
        res.targetFileSystem.unsetStoragePolicy(res.remainingPath);
    }
    
    @Override
    public BlockStoragePolicySpi getStoragePolicy(final Path src) throws IOException {
        final InodeTree.ResolveResult<AbstractFileSystem> res = this.fsState.resolve(this.getUriPath(src), true);
        return res.targetFileSystem.getStoragePolicy(res.remainingPath);
    }
    
    public static class MountPoint
    {
        private Path src;
        private URI[] targets;
        
        MountPoint(final Path srcPath, final URI[] targetURIs) {
            this.src = srcPath;
            this.targets = targetURIs;
        }
        
        Path getSrc() {
            return this.src;
        }
        
        URI[] getTargets() {
            return this.targets;
        }
    }
    
    private abstract class WrappingRemoteIterator<T extends FileStatus> implements RemoteIterator<T>
    {
        private final String resolvedPath;
        private final ChRootedFs targetFs;
        private final RemoteIterator<T> innerIter;
        private final Path originalPath;
        
        WrappingRemoteIterator(final InodeTree.ResolveResult<AbstractFileSystem> res, final RemoteIterator<T> innerIter, final Path originalPath) {
            this.resolvedPath = res.resolvedPath;
            this.targetFs = (ChRootedFs)res.targetFileSystem;
            this.innerIter = innerIter;
            this.originalPath = originalPath;
        }
        
        @Override
        public boolean hasNext() throws IOException {
            return this.innerIter.hasNext();
        }
        
        @Override
        public T next() throws IOException {
            final T status = this.innerIter.next();
            final String suffix = this.targetFs.stripOutRoot(status.getPath());
            final Path newPath = ViewFs.this.makeQualified((suffix.length() == 0) ? this.originalPath : new Path(this.resolvedPath, suffix));
            return this.getViewFsFileStatus(status, newPath);
        }
        
        protected abstract T getViewFsFileStatus(final T p0, final Path p1);
    }
    
    static class InternalDirOfViewFs extends AbstractFileSystem
    {
        final InodeTree.INodeDir<AbstractFileSystem> theInternalDir;
        final long creationTime;
        final UserGroupInformation ugi;
        final URI myUri;
        
        public InternalDirOfViewFs(final InodeTree.INodeDir<AbstractFileSystem> dir, final long cTime, final UserGroupInformation ugi, final URI uri) throws URISyntaxException {
            super(FsConstants.VIEWFS_URI, "viewfs", false, -1);
            this.theInternalDir = dir;
            this.creationTime = cTime;
            this.ugi = ugi;
            this.myUri = uri;
        }
        
        private static void checkPathIsSlash(final Path f) throws IOException {
            if (f != InodeTree.SlashPath) {
                throw new IOException("Internal implementation error: expected file name to be /");
            }
        }
        
        @Override
        public FSDataOutputStream createInternal(final Path f, final EnumSet<CreateFlag> flag, final FsPermission absolutePermission, final int bufferSize, final short replication, final long blockSize, final Progressable progress, final Options.ChecksumOpt checksumOpt, final boolean createParent) throws AccessControlException, FileAlreadyExistsException, FileNotFoundException, ParentNotDirectoryException, UnsupportedFileSystemException, UnresolvedLinkException, IOException {
            throw ViewFs.readOnlyMountTable("create", f);
        }
        
        @Override
        public boolean delete(final Path f, final boolean recursive) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFs.readOnlyMountTable("delete", f);
        }
        
        @Override
        public BlockLocation[] getFileBlockLocations(final Path f, final long start, final long len) throws FileNotFoundException, IOException {
            checkPathIsSlash(f);
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
            return new FileStatus(0L, true, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getPrimaryGroupName(), new Path(this.theInternalDir.fullPath).makeQualified(this.myUri, null));
        }
        
        @Override
        public FileStatus getFileLinkStatus(final Path f) throws IOException {
            final InodeTree.INode<AbstractFileSystem> inode = this.theInternalDir.getChildren().get(f.toUri().toString().substring(1));
            if (inode == null) {
                throw new FileNotFoundException("viewFs internal mount table - missing entry:" + f);
            }
            FileStatus result;
            if (inode.isLink()) {
                final InodeTree.INodeLink<AbstractFileSystem> inodelink = (InodeTree.INodeLink<AbstractFileSystem>)(InodeTree.INodeLink)inode;
                result = new FileStatus(0L, false, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getPrimaryGroupName(), inodelink.getTargetLink(), new Path(inode.fullPath).makeQualified(this.myUri, null));
            }
            else {
                result = new FileStatus(0L, true, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getPrimaryGroupName(), new Path(inode.fullPath).makeQualified(this.myUri, null));
            }
            return result;
        }
        
        @Override
        public FsStatus getFsStatus() {
            return new FsStatus(0L, 0L, 0L);
        }
        
        @Deprecated
        @Override
        public FsServerDefaults getServerDefaults() throws IOException {
            return LocalConfigKeys.getServerDefaults();
        }
        
        @Override
        public FsServerDefaults getServerDefaults(final Path f) throws IOException {
            return LocalConfigKeys.getServerDefaults();
        }
        
        @Override
        public int getUriDefaultPort() {
            return -1;
        }
        
        @Override
        public FileStatus[] listStatus(final Path f) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            final FileStatus[] result = new FileStatus[this.theInternalDir.getChildren().size()];
            int i = 0;
            for (final Map.Entry<String, InodeTree.INode<AbstractFileSystem>> iEntry : this.theInternalDir.getChildren().entrySet()) {
                final InodeTree.INode<AbstractFileSystem> inode = iEntry.getValue();
                if (inode.isLink()) {
                    final InodeTree.INodeLink<AbstractFileSystem> link = (InodeTree.INodeLink<AbstractFileSystem>)(InodeTree.INodeLink)inode;
                    result[i++] = new FileStatus(0L, false, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getPrimaryGroupName(), link.getTargetLink(), new Path(inode.fullPath).makeQualified(this.myUri, null));
                }
                else {
                    result[i++] = new FileStatus(0L, true, 0, 0L, this.creationTime, this.creationTime, Constants.PERMISSION_555, this.ugi.getShortUserName(), this.ugi.getGroupNames()[0], new Path(inode.fullPath).makeQualified(this.myUri, null));
                }
            }
            return result;
        }
        
        @Override
        public void mkdir(final Path dir, final FsPermission permission, final boolean createParent) throws AccessControlException, FileAlreadyExistsException {
            if (this.theInternalDir.isRoot() && dir == null) {
                throw new FileAlreadyExistsException("/ already exits");
            }
            throw ViewFs.readOnlyMountTable("mkdir", dir);
        }
        
        @Override
        public FSDataInputStream open(final Path f, final int bufferSize) throws FileNotFoundException, IOException {
            checkPathIsSlash(f);
            throw new FileNotFoundException("Path points to dir not a file");
        }
        
        @Override
        public boolean truncate(final Path f, final long newLength) throws FileNotFoundException, IOException {
            checkPathIsSlash(f);
            throw ViewFs.readOnlyMountTable("truncate", f);
        }
        
        @Override
        public void renameInternal(final Path src, final Path dst) throws AccessControlException, IOException {
            checkPathIsSlash(src);
            checkPathIsSlash(dst);
            throw ViewFs.readOnlyMountTable("rename", src);
        }
        
        @Override
        public boolean supportsSymlinks() {
            return true;
        }
        
        @Override
        public void createSymlink(final Path target, final Path link, final boolean createParent) throws AccessControlException {
            throw ViewFs.readOnlyMountTable("createSymlink", link);
        }
        
        @Override
        public Path getLinkTarget(final Path f) throws FileNotFoundException, IOException {
            return this.getFileLinkStatus(f).getSymlink();
        }
        
        @Override
        public void setOwner(final Path f, final String username, final String groupname) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFs.readOnlyMountTable("setOwner", f);
        }
        
        @Override
        public void setPermission(final Path f, final FsPermission permission) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFs.readOnlyMountTable("setPermission", f);
        }
        
        @Override
        public boolean setReplication(final Path f, final short replication) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFs.readOnlyMountTable("setReplication", f);
        }
        
        @Override
        public void setTimes(final Path f, final long mtime, final long atime) throws AccessControlException, IOException {
            checkPathIsSlash(f);
            throw ViewFs.readOnlyMountTable("setTimes", f);
        }
        
        @Override
        public void setVerifyChecksum(final boolean verifyChecksum) throws AccessControlException {
            throw ViewFs.readOnlyMountTable("setVerifyChecksum", "");
        }
        
        @Override
        public void modifyAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("modifyAclEntries", path);
        }
        
        @Override
        public void removeAclEntries(final Path path, final List<AclEntry> aclSpec) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("removeAclEntries", path);
        }
        
        @Override
        public void removeDefaultAcl(final Path path) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("removeDefaultAcl", path);
        }
        
        @Override
        public void removeAcl(final Path path) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("removeAcl", path);
        }
        
        @Override
        public void setAcl(final Path path, final List<AclEntry> aclSpec) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("setAcl", path);
        }
        
        @Override
        public AclStatus getAclStatus(final Path path) throws IOException {
            checkPathIsSlash(path);
            return new AclStatus.Builder().owner(this.ugi.getShortUserName()).group(this.ugi.getPrimaryGroupName()).addEntries(AclUtil.getMinimalAcl(Constants.PERMISSION_555)).stickyBit(false).build();
        }
        
        @Override
        public void setXAttr(final Path path, final String name, final byte[] value, final EnumSet<XAttrSetFlag> flag) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("setXAttr", path);
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
            throw ViewFs.readOnlyMountTable("removeXAttr", path);
        }
        
        @Override
        public Path createSnapshot(final Path path, final String snapshotName) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("createSnapshot", path);
        }
        
        @Override
        public void renameSnapshot(final Path path, final String snapshotOldName, final String snapshotNewName) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("renameSnapshot", path);
        }
        
        @Override
        public void deleteSnapshot(final Path path, final String snapshotName) throws IOException {
            checkPathIsSlash(path);
            throw ViewFs.readOnlyMountTable("deleteSnapshot", path);
        }
        
        @Override
        public void setStoragePolicy(final Path path, final String policyName) throws IOException {
            throw ViewFs.readOnlyMountTable("setStoragePolicy", path);
        }
    }
}
