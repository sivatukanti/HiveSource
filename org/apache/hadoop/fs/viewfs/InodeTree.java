// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import java.util.Collections;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.ArrayList;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import java.net.URISyntaxException;
import org.apache.hadoop.util.StringUtils;
import java.net.URI;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
abstract class InodeTree<T>
{
    static final Path SlashPath;
    private final INode<T> root;
    private final INodeLink<T> rootFallbackLink;
    private final String homedirPrefix;
    private List<MountPoint<T>> mountPoints;
    
    static String[] breakIntoPathComponents(final String path) {
        return (String[])((path == null) ? null : path.split("/"));
    }
    
    private void createLink(final String src, final String target, final LinkType linkType, final String settings, final UserGroupInformation aUgi, final Configuration config) throws URISyntaxException, IOException, FileAlreadyExistsException, UnsupportedFileSystemException {
        final Path srcPath = new Path(src);
        if (!srcPath.isAbsoluteAndSchemeAuthorityNull()) {
            throw new IOException("ViewFs: Non absolute mount name in config:" + src);
        }
        final String[] srcPaths = breakIntoPathComponents(src);
        Preconditions.checkState(this.root.isInternalDir());
        INodeDir<T> curInode = this.getRootDir();
        int i;
        for (i = 1; i < srcPaths.length - 1; ++i) {
            final String iPath = srcPaths[i];
            INode<T> nextInode = curInode.resolveInternal(iPath);
            if (nextInode == null) {
                final INodeDir<T> newDir = curInode.addDir(iPath, aUgi);
                newDir.setInternalDirFs(this.getTargetFileSystem(newDir));
                nextInode = newDir;
            }
            if (nextInode.isLink()) {
                throw new FileAlreadyExistsException("Path " + nextInode.fullPath + " already exists as link");
            }
            assert nextInode.isInternalDir();
            curInode = (INodeDir<T>)(INodeDir)nextInode;
        }
        final String iPath = srcPaths[i];
        if (curInode.resolveInternal(iPath) != null) {
            final StringBuilder strB = new StringBuilder(srcPaths[0]);
            for (int j = 1; j <= i; ++j) {
                strB.append('/').append(srcPaths[j]);
            }
            throw new FileAlreadyExistsException("Path " + (Object)strB + " already exists as dir; cannot create link here");
        }
        final String fullPath = curInode.fullPath + ((curInode == this.root) ? "" : "/") + iPath;
        INodeLink<T> newLink = null;
        switch (linkType) {
            case SINGLE: {
                newLink = new INodeLink<T>(fullPath, aUgi, this.getTargetFileSystem(new URI(target)), new URI(target));
                break;
            }
            case SINGLE_FALLBACK:
            case MERGE_SLASH: {
                throw new IllegalArgumentException("Unexpected linkType: " + linkType);
            }
            case MERGE:
            case NFLY: {
                final URI[] targetUris = StringUtils.stringToURI(StringUtils.getStrings(target));
                newLink = new INodeLink<T>(fullPath, aUgi, this.getTargetFileSystem(settings, targetUris), targetUris);
                break;
            }
            default: {
                throw new IllegalArgumentException(linkType + ": Infeasible linkType");
            }
        }
        curInode.addLink(iPath, newLink);
        this.mountPoints.add(new MountPoint<T>(src, newLink));
    }
    
    protected abstract T getTargetFileSystem(final URI p0) throws UnsupportedFileSystemException, URISyntaxException, IOException;
    
    protected abstract T getTargetFileSystem(final INodeDir<T> p0) throws URISyntaxException;
    
    protected abstract T getTargetFileSystem(final String p0, final URI[] p1) throws UnsupportedFileSystemException, URISyntaxException, IOException;
    
    private INodeDir<T> getRootDir() {
        Preconditions.checkState(this.root.isInternalDir());
        return (INodeDir<T>)(INodeDir)this.root;
    }
    
    private INodeLink<T> getRootLink() {
        Preconditions.checkState(this.root.isLink());
        return (INodeLink<T>)(INodeLink)this.root;
    }
    
    private boolean hasFallbackLink() {
        return this.rootFallbackLink != null;
    }
    
    private INodeLink<T> getRootFallbackLink() {
        Preconditions.checkState(this.root.isInternalDir());
        return this.rootFallbackLink;
    }
    
    protected InodeTree(final Configuration config, final String viewName) throws UnsupportedFileSystemException, URISyntaxException, FileAlreadyExistsException, IOException {
        this.mountPoints = new ArrayList<MountPoint<T>>();
        String mountTableName = viewName;
        if (mountTableName == null) {
            mountTableName = "default";
        }
        this.homedirPrefix = ConfigUtil.getHomeDirValue(config, mountTableName);
        boolean isMergeSlashConfigured = false;
        String mergeSlashTarget = null;
        final List<LinkEntry> linkEntries = new LinkedList<LinkEntry>();
        final String mountTablePrefix = "fs.viewfs.mounttable." + mountTableName + ".";
        final String linkPrefix = "link.";
        final String linkFallbackPrefix = "linkFallback";
        final String linkMergePrefix = "linkMerge.";
        final String linkMergeSlashPrefix = "linkMergeSlash";
        boolean gotMountTableEntry = false;
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        for (final Map.Entry<String, String> si : config) {
            final String key = si.getKey();
            if (key.startsWith(mountTablePrefix)) {
                gotMountTableEntry = true;
                String src = key.substring(mountTablePrefix.length());
                String settings = null;
                LinkType linkType;
                if (src.startsWith("link.")) {
                    src = src.substring("link.".length());
                    if (src.equals(InodeTree.SlashPath.toString())) {
                        throw new UnsupportedFileSystemException("Unexpected mount table link entry '" + key + "'. Use " + "linkMergeSlash" + " instead!");
                    }
                    linkType = LinkType.SINGLE;
                }
                else if (src.startsWith("linkFallback")) {
                    if (src.length() != "linkFallback".length()) {
                        throw new IOException("ViewFs: Mount points initialization error. Invalid linkFallback entry in config: " + src);
                    }
                    linkType = LinkType.SINGLE_FALLBACK;
                }
                else if (src.startsWith("linkMerge.")) {
                    src = src.substring("linkMerge.".length());
                    linkType = LinkType.MERGE;
                }
                else if (src.startsWith("linkMergeSlash")) {
                    if (src.length() != "linkMergeSlash".length()) {
                        throw new IOException("ViewFs: Mount points initialization error. Invalid linkMergeSlash entry in config: " + src);
                    }
                    linkType = LinkType.MERGE_SLASH;
                }
                else if (src.startsWith("linkNfly")) {
                    src = src.substring("linkNfly".length() + 1);
                    settings = src.substring(0, src.indexOf(46));
                    src = src.substring(settings.length() + 1);
                    linkType = LinkType.NFLY;
                }
                else {
                    if (src.startsWith("homedir")) {
                        continue;
                    }
                    throw new IOException("ViewFs: Cannot initialize: Invalid entry in Mount table in config: " + src);
                }
                final String target = si.getValue();
                if (linkType != LinkType.MERGE_SLASH) {
                    if (isMergeSlashConfigured) {
                        throw new IOException("Mount table " + mountTableName + " has already been configured with a merge slash link. A regular link should not be added.");
                    }
                    linkEntries.add(new LinkEntry(src, target, linkType, settings, ugi, config));
                }
                else {
                    if (!linkEntries.isEmpty()) {
                        throw new IOException("Mount table " + mountTableName + " has already been configured with regular links. A merge slash link should not be configured.");
                    }
                    if (isMergeSlashConfigured) {
                        throw new IOException("Mount table " + mountTableName + " has already been configured with a merge slash link. Multiple merge slash links for the same mount table is not allowed.");
                    }
                    isMergeSlashConfigured = true;
                    mergeSlashTarget = target;
                }
            }
        }
        if (isMergeSlashConfigured) {
            Preconditions.checkNotNull(mergeSlashTarget);
            this.root = new INodeLink<T>(mountTableName, ugi, this.getTargetFileSystem(new URI(mergeSlashTarget)), new URI(mergeSlashTarget));
            this.mountPoints.add(new MountPoint<T>("/", (INodeLink)this.root));
            this.rootFallbackLink = null;
        }
        else {
            this.root = new INodeDir<T>("/", UserGroupInformation.getCurrentUser());
            this.getRootDir().setInternalDirFs(this.getTargetFileSystem(this.getRootDir()));
            this.getRootDir().setRoot(true);
            INodeLink<T> fallbackLink = null;
            for (final LinkEntry le : linkEntries) {
                if (le.isLinkType(LinkType.SINGLE_FALLBACK)) {
                    if (fallbackLink != null) {
                        throw new IOException("Mount table " + mountTableName + " has already been configured with a link fallback. Multiple fallback links for the same mount table is not allowed.");
                    }
                    fallbackLink = new INodeLink<T>(mountTableName, ugi, this.getTargetFileSystem(new URI(le.getTarget())), new URI(le.getTarget()));
                }
                else {
                    this.createLink(le.getSrc(), le.getTarget(), le.getLinkType(), le.getSettings(), le.getUgi(), le.getConfig());
                }
            }
            this.rootFallbackLink = fallbackLink;
        }
        if (!gotMountTableEntry) {
            throw new IOException("ViewFs: Cannot initialize: Empty Mount table in config for viewfs://" + mountTableName + "/");
        }
    }
    
    ResolveResult<T> resolve(final String p, final boolean resolveLastComponent) throws FileNotFoundException {
        final String[] path = breakIntoPathComponents(p);
        if (path.length <= 1) {
            final T targetFs = this.root.isInternalDir() ? this.getRootDir().getInternalDirFs() : this.getRootLink().getTargetFileSystem();
            final ResolveResult<T> res = new ResolveResult<T>(ResultKind.INTERNAL_DIR, targetFs, this.root.fullPath, InodeTree.SlashPath);
            return res;
        }
        if (this.root.isLink()) {
            final StringBuilder remainingPathStr = new StringBuilder();
            for (int i = 1; i < path.length; ++i) {
                remainingPathStr.append("/").append(path[i]);
            }
            final Path remainingPath = new Path(remainingPathStr.toString());
            final ResolveResult<T> res2 = new ResolveResult<T>(ResultKind.EXTERNAL_DIR, this.getRootLink().getTargetFileSystem(), this.root.fullPath, remainingPath);
            return res2;
        }
        Preconditions.checkState(this.root.isInternalDir());
        INodeDir<T> curInode = this.getRootDir();
        int j = 1;
        while (j < path.length - (resolveLastComponent ? 0 : 1)) {
            final INode<T> nextInode = curInode.resolveInternal(path[j]);
            if (nextInode == null) {
                if (this.hasFallbackLink()) {
                    return new ResolveResult<T>(ResultKind.EXTERNAL_DIR, this.getRootFallbackLink().getTargetFileSystem(), this.root.fullPath, new Path(p));
                }
                final StringBuilder failedAt = new StringBuilder(path[0]);
                for (int k = 1; k <= j; ++k) {
                    failedAt.append('/').append(path[k]);
                }
                throw new FileNotFoundException("File/Directory does not exist: " + failedAt.toString());
            }
            else {
                if (nextInode.isLink()) {
                    final INodeLink<T> link = (INodeLink<T>)(INodeLink)nextInode;
                    Path remainingPath2;
                    if (j >= path.length - 1) {
                        remainingPath2 = InodeTree.SlashPath;
                    }
                    else {
                        final StringBuilder remainingPathStr2 = new StringBuilder("/" + path[j + 1]);
                        for (int l = j + 2; l < path.length; ++l) {
                            remainingPathStr2.append('/').append(path[l]);
                        }
                        remainingPath2 = new Path(remainingPathStr2.toString());
                    }
                    final ResolveResult<T> res3 = new ResolveResult<T>(ResultKind.EXTERNAL_DIR, link.getTargetFileSystem(), nextInode.fullPath, remainingPath2);
                    return res3;
                }
                if (nextInode.isInternalDir()) {
                    curInode = (INodeDir<T>)(INodeDir)nextInode;
                }
                ++j;
            }
        }
        Path remainingPath3;
        if (resolveLastComponent) {
            remainingPath3 = InodeTree.SlashPath;
        }
        else {
            final StringBuilder remainingPathStr3 = new StringBuilder("/" + path[j]);
            for (int k = j + 1; k < path.length; ++k) {
                remainingPathStr3.append('/').append(path[k]);
            }
            remainingPath3 = new Path(remainingPathStr3.toString());
        }
        final ResolveResult<T> res4 = new ResolveResult<T>(ResultKind.INTERNAL_DIR, curInode.getInternalDirFs(), curInode.fullPath, remainingPath3);
        return res4;
    }
    
    List<MountPoint<T>> getMountPoints() {
        return this.mountPoints;
    }
    
    String getHomeDirPrefixValue() {
        return this.homedirPrefix;
    }
    
    static {
        SlashPath = new Path("/");
    }
    
    enum ResultKind
    {
        INTERNAL_DIR, 
        EXTERNAL_DIR;
    }
    
    static class MountPoint<T>
    {
        String src;
        INodeLink<T> target;
        
        MountPoint(final String srcPath, final INodeLink<T> mountLink) {
            this.src = srcPath;
            this.target = mountLink;
        }
    }
    
    abstract static class INode<T>
    {
        final String fullPath;
        
        public INode(final String pathToNode, final UserGroupInformation aUgi) {
            this.fullPath = pathToNode;
        }
        
        abstract boolean isInternalDir();
        
        boolean isLink() {
            return !this.isInternalDir();
        }
    }
    
    static class INodeDir<T> extends INode<T>
    {
        private final Map<String, INode<T>> children;
        private T internalDirFs;
        private boolean isRoot;
        
        INodeDir(final String pathToNode, final UserGroupInformation aUgi) {
            super(pathToNode, aUgi);
            this.children = new HashMap<String, INode<T>>();
            this.internalDirFs = null;
            this.isRoot = false;
        }
        
        @Override
        boolean isInternalDir() {
            return true;
        }
        
        T getInternalDirFs() {
            return this.internalDirFs;
        }
        
        void setInternalDirFs(final T internalDirFs) {
            this.internalDirFs = internalDirFs;
        }
        
        void setRoot(final boolean root) {
            this.isRoot = root;
        }
        
        boolean isRoot() {
            return this.isRoot;
        }
        
        Map<String, INode<T>> getChildren() {
            return Collections.unmodifiableMap((Map<? extends String, ? extends INode<T>>)this.children);
        }
        
        INode<T> resolveInternal(final String pathComponent) {
            return this.children.get(pathComponent);
        }
        
        INodeDir<T> addDir(final String pathComponent, final UserGroupInformation aUgi) throws FileAlreadyExistsException {
            if (this.children.containsKey(pathComponent)) {
                throw new FileAlreadyExistsException();
            }
            final INodeDir<T> newDir = new INodeDir<T>(this.fullPath + (this.isRoot() ? "" : "/") + pathComponent, aUgi);
            this.children.put(pathComponent, newDir);
            return newDir;
        }
        
        void addLink(final String pathComponent, final INodeLink<T> link) throws FileAlreadyExistsException {
            if (this.children.containsKey(pathComponent)) {
                throw new FileAlreadyExistsException();
            }
            this.children.put(pathComponent, link);
        }
    }
    
    enum LinkType
    {
        SINGLE, 
        SINGLE_FALLBACK, 
        MERGE, 
        MERGE_SLASH, 
        NFLY;
    }
    
    static class INodeLink<T> extends INode<T>
    {
        final URI[] targetDirLinkList;
        final T targetFileSystem;
        
        INodeLink(final String pathToNode, final UserGroupInformation aUgi, final T targetMergeFs, final URI[] aTargetDirLinkList) {
            super(pathToNode, aUgi);
            this.targetFileSystem = targetMergeFs;
            this.targetDirLinkList = aTargetDirLinkList;
        }
        
        INodeLink(final String pathToNode, final UserGroupInformation aUgi, final T targetFs, final URI aTargetDirLink) {
            super(pathToNode, aUgi);
            this.targetFileSystem = targetFs;
            (this.targetDirLinkList = new URI[1])[0] = aTargetDirLink;
        }
        
        Path getTargetLink() {
            final StringBuilder result = new StringBuilder(this.targetDirLinkList[0].toString());
            for (int i = 1; i < this.targetDirLinkList.length; ++i) {
                result.append(',').append(this.targetDirLinkList[i].toString());
            }
            return new Path(result.toString());
        }
        
        @Override
        boolean isInternalDir() {
            return false;
        }
        
        public T getTargetFileSystem() {
            return this.targetFileSystem;
        }
    }
    
    private static class LinkEntry
    {
        private final String src;
        private final String target;
        private final LinkType linkType;
        private final String settings;
        private final UserGroupInformation ugi;
        private final Configuration config;
        
        LinkEntry(final String src, final String target, final LinkType linkType, final String settings, final UserGroupInformation ugi, final Configuration config) {
            this.src = src;
            this.target = target;
            this.linkType = linkType;
            this.settings = settings;
            this.ugi = ugi;
            this.config = config;
        }
        
        String getSrc() {
            return this.src;
        }
        
        String getTarget() {
            return this.target;
        }
        
        LinkType getLinkType() {
            return this.linkType;
        }
        
        boolean isLinkType(final LinkType type) {
            return this.linkType == type;
        }
        
        String getSettings() {
            return this.settings;
        }
        
        UserGroupInformation getUgi() {
            return this.ugi;
        }
        
        Configuration getConfig() {
            return this.config;
        }
    }
    
    static class ResolveResult<T>
    {
        final ResultKind kind;
        final T targetFileSystem;
        final String resolvedPath;
        final Path remainingPath;
        
        ResolveResult(final ResultKind k, final T targetFs, final String resolveP, final Path remainingP) {
            this.kind = k;
            this.targetFileSystem = targetFs;
            this.resolvedPath = resolveP;
            this.remainingPath = remainingP;
        }
        
        boolean isInternalDir() {
            return this.kind == ResultKind.INTERNAL_DIR;
        }
    }
}
