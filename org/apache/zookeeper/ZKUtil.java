// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Deque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.zookeeper.common.PathUtils;
import org.slf4j.Logger;

public class ZKUtil
{
    private static final Logger LOG;
    
    public static void deleteRecursive(final ZooKeeper zk, final String pathRoot) throws InterruptedException, KeeperException {
        PathUtils.validatePath(pathRoot);
        final List<String> tree = listSubTreeBFS(zk, pathRoot);
        ZKUtil.LOG.debug("Deleting " + tree);
        ZKUtil.LOG.debug("Deleting " + tree.size() + " subnodes ");
        for (int i = tree.size() - 1; i >= 0; --i) {
            zk.delete(tree.get(i), -1);
        }
    }
    
    public static void deleteRecursive(final ZooKeeper zk, final String pathRoot, final AsyncCallback.VoidCallback cb, final Object ctx) throws InterruptedException, KeeperException {
        PathUtils.validatePath(pathRoot);
        final List<String> tree = listSubTreeBFS(zk, pathRoot);
        ZKUtil.LOG.debug("Deleting " + tree);
        ZKUtil.LOG.debug("Deleting " + tree.size() + " subnodes ");
        for (int i = tree.size() - 1; i >= 0; --i) {
            zk.delete(tree.get(i), -1, cb, ctx);
        }
    }
    
    public static List<String> listSubTreeBFS(final ZooKeeper zk, final String pathRoot) throws KeeperException, InterruptedException {
        final Deque<String> queue = new LinkedList<String>();
        final List<String> tree = new ArrayList<String>();
        queue.add(pathRoot);
        tree.add(pathRoot);
        while (true) {
            final String node = queue.pollFirst();
            if (node == null) {
                break;
            }
            final List<String> children = zk.getChildren(node, false);
            for (final String child : children) {
                final String childPath = node + "/" + child;
                queue.add(childPath);
                tree.add(childPath);
            }
        }
        return tree;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZKUtil.class);
    }
}
