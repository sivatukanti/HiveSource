// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.Collections;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.Iterator;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import java.util.List;
import org.apache.curator.shaded.com.google.common.base.Splitter;
import org.apache.zookeeper.CreateMode;

public class ZKPaths
{
    public static final String PATH_SEPARATOR = "/";
    private static final CreateMode NON_CONTAINER_MODE;
    private static final Splitter PATH_SPLITTER;
    
    public static CreateMode getContainerCreateMode() {
        return CreateModeHolder.containerCreateMode;
    }
    
    public static boolean hasContainerSupport() {
        return getContainerCreateMode() != ZKPaths.NON_CONTAINER_MODE;
    }
    
    public static String fixForNamespace(final String namespace, final String path) {
        return fixForNamespace(namespace, path, false);
    }
    
    public static String fixForNamespace(final String namespace, final String path, final boolean isSequential) {
        PathUtils.validatePath(path, isSequential);
        if (namespace != null) {
            return makePath(namespace, path);
        }
        return path;
    }
    
    public static String getNodeFromPath(final String path) {
        PathUtils.validatePath(path);
        final int i = path.lastIndexOf("/");
        if (i < 0) {
            return path;
        }
        if (i + 1 >= path.length()) {
            return "";
        }
        return path.substring(i + 1);
    }
    
    public static PathAndNode getPathAndNode(final String path) {
        PathUtils.validatePath(path);
        final int i = path.lastIndexOf("/");
        if (i < 0) {
            return new PathAndNode(path, "");
        }
        if (i + 1 >= path.length()) {
            return new PathAndNode("/", "");
        }
        final String node = path.substring(i + 1);
        final String parentPath = (i > 0) ? path.substring(0, i) : "/";
        return new PathAndNode(parentPath, node);
    }
    
    public static List<String> split(final String path) {
        PathUtils.validatePath(path);
        return ZKPaths.PATH_SPLITTER.splitToList(path);
    }
    
    public static void mkdirs(final ZooKeeper zookeeper, final String path) throws InterruptedException, KeeperException {
        mkdirs(zookeeper, path, true, null, false);
    }
    
    public static void mkdirs(final ZooKeeper zookeeper, final String path, final boolean makeLastNode) throws InterruptedException, KeeperException {
        mkdirs(zookeeper, path, makeLastNode, null, false);
    }
    
    public static void mkdirs(final ZooKeeper zookeeper, final String path, final boolean makeLastNode, final InternalACLProvider aclProvider) throws InterruptedException, KeeperException {
        mkdirs(zookeeper, path, makeLastNode, aclProvider, false);
    }
    
    public static void mkdirs(final ZooKeeper zookeeper, final String path, final boolean makeLastNode, final InternalACLProvider aclProvider, final boolean asContainers) throws InterruptedException, KeeperException {
        PathUtils.validatePath(path);
        int pos = 1;
        do {
            pos = path.indexOf("/", pos + 1);
            if (pos == -1) {
                if (!makeLastNode) {
                    break;
                }
                pos = path.length();
            }
            final String subPath = path.substring(0, pos);
            if (zookeeper.exists(subPath, false) == null) {
                try {
                    List<ACL> acl = null;
                    if (aclProvider != null) {
                        acl = aclProvider.getAclForPath(subPath);
                        if (acl == null) {
                            acl = aclProvider.getDefaultAcl();
                        }
                    }
                    if (acl == null) {
                        acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
                    }
                    zookeeper.create(subPath, new byte[0], acl, getCreateMode(asContainers));
                }
                catch (KeeperException.NodeExistsException ex) {}
            }
        } while (pos < path.length());
    }
    
    public static void deleteChildren(final ZooKeeper zookeeper, final String path, final boolean deleteSelf) throws InterruptedException, KeeperException {
        PathUtils.validatePath(path);
        final List<String> children = zookeeper.getChildren(path, null);
        for (final String child : children) {
            final String fullPath = makePath(path, child);
            deleteChildren(zookeeper, fullPath, true);
        }
        if (deleteSelf) {
            try {
                zookeeper.delete(path, -1);
            }
            catch (KeeperException.NotEmptyException e) {
                deleteChildren(zookeeper, path, true);
            }
            catch (KeeperException.NoNodeException ex) {}
        }
    }
    
    public static List<String> getSortedChildren(final ZooKeeper zookeeper, final String path) throws InterruptedException, KeeperException {
        final List<String> children = zookeeper.getChildren(path, false);
        final List<String> sortedList = (List<String>)Lists.newArrayList((Iterable<?>)children);
        Collections.sort(sortedList);
        return sortedList;
    }
    
    public static String makePath(final String parent, final String child) {
        final StringBuilder path = new StringBuilder();
        joinPath(path, parent, child);
        return path.toString();
    }
    
    public static String makePath(final String parent, final String firstChild, final String... restChildren) {
        final StringBuilder path = new StringBuilder();
        joinPath(path, parent, firstChild);
        if (restChildren == null) {
            return path.toString();
        }
        for (final String child : restChildren) {
            joinPath(path, "", child);
        }
        return path.toString();
    }
    
    private static void joinPath(final StringBuilder path, final String parent, String child) {
        if (parent != null && parent.length() > 0) {
            if (!parent.startsWith("/")) {
                path.append("/");
            }
            if (parent.endsWith("/")) {
                path.append(parent.substring(0, parent.length() - 1));
            }
            else {
                path.append(parent);
            }
        }
        if (child == null || child.length() == 0 || child.equals("/")) {
            if (path.length() == 0) {
                path.append("/");
            }
            return;
        }
        path.append("/");
        if (child.startsWith("/")) {
            child = child.substring(1);
        }
        if (child.endsWith("/")) {
            child = child.substring(0, child.length() - 1);
        }
        path.append(child);
    }
    
    private ZKPaths() {
    }
    
    private static CreateMode getCreateMode(final boolean asContainers) {
        return asContainers ? getContainerCreateMode() : CreateMode.PERSISTENT;
    }
    
    static {
        NON_CONTAINER_MODE = CreateMode.PERSISTENT;
        PATH_SPLITTER = Splitter.on("/").omitEmptyStrings();
    }
    
    private static class CreateModeHolder
    {
        private static final Logger log;
        private static final CreateMode containerCreateMode;
        
        static {
            log = LoggerFactory.getLogger(ZKPaths.class);
            CreateMode localCreateMode;
            try {
                localCreateMode = CreateMode.valueOf("CONTAINER");
            }
            catch (IllegalArgumentException ignore) {
                localCreateMode = ZKPaths.NON_CONTAINER_MODE;
                CreateModeHolder.log.warn("The version of ZooKeeper being used doesn't support Container nodes. CreateMode.PERSISTENT will be used instead.");
            }
            containerCreateMode = localCreateMode;
        }
    }
    
    public static class PathAndNode
    {
        private final String path;
        private final String node;
        
        public PathAndNode(final String path, final String node) {
            this.path = path;
            this.node = node;
        }
        
        public String getPath() {
            return this.path;
        }
        
        public String getNode() {
            return this.node;
        }
    }
}
