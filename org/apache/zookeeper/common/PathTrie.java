// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.common;

import java.util.Iterator;
import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;

public class PathTrie
{
    private static final Logger LOG;
    private final TrieNode rootNode;
    
    public PathTrie() {
        this.rootNode = new TrieNode((TrieNode)null);
    }
    
    public void addPath(final String path) {
        if (path == null) {
            return;
        }
        final String[] pathComponents = path.split("/");
        TrieNode parent = this.rootNode;
        String part = null;
        if (pathComponents.length <= 1) {
            throw new IllegalArgumentException("Invalid path " + path);
        }
        for (int i = 1; i < pathComponents.length; ++i) {
            part = pathComponents[i];
            if (parent.getChild(part) == null) {
                parent.addChild(part, new TrieNode(parent));
            }
            parent = parent.getChild(part);
        }
        parent.setProperty(true);
    }
    
    public void deletePath(final String path) {
        if (path == null) {
            return;
        }
        final String[] pathComponents = path.split("/");
        TrieNode parent = this.rootNode;
        String part = null;
        if (pathComponents.length <= 1) {
            throw new IllegalArgumentException("Invalid path " + path);
        }
        for (int i = 1; i < pathComponents.length; ++i) {
            part = pathComponents[i];
            if (parent.getChild(part) == null) {
                return;
            }
            parent = parent.getChild(part);
            PathTrie.LOG.info("{}", parent);
        }
        final TrieNode realParent = parent.getParent();
        realParent.deleteChild(part);
    }
    
    public String findMaxPrefix(final String path) {
        if (path == null) {
            return null;
        }
        if ("/".equals(path)) {
            return path;
        }
        final String[] pathComponents = path.split("/");
        TrieNode parent = this.rootNode;
        final List<String> components = new ArrayList<String>();
        if (pathComponents.length <= 1) {
            throw new IllegalArgumentException("Invalid path " + path);
        }
        int i = 1;
        String part = null;
        final StringBuilder sb = new StringBuilder();
        int lastindex = -1;
        while (i < pathComponents.length && parent.getChild(pathComponents[i]) != null) {
            part = pathComponents[i];
            parent = parent.getChild(part);
            components.add(part);
            if (parent.getProperty()) {
                lastindex = i - 1;
            }
            ++i;
        }
        for (int j = 0; j < lastindex + 1; ++j) {
            sb.append("/" + components.get(j));
        }
        return sb.toString();
    }
    
    public void clear() {
        for (final String child : this.rootNode.getChildren()) {
            this.rootNode.deleteChild(child);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(PathTrie.class);
    }
    
    static class TrieNode
    {
        boolean property;
        final HashMap<String, TrieNode> children;
        TrieNode parent;
        
        private TrieNode(final TrieNode parent) {
            this.property = false;
            this.parent = null;
            this.children = new HashMap<String, TrieNode>();
            this.parent = parent;
        }
        
        TrieNode getParent() {
            return this.parent;
        }
        
        void setParent(final TrieNode parent) {
            this.parent = parent;
        }
        
        void setProperty(final boolean prop) {
            this.property = prop;
        }
        
        boolean getProperty() {
            return this.property;
        }
        
        void addChild(final String childName, final TrieNode node) {
            synchronized (this.children) {
                if (this.children.containsKey(childName)) {
                    return;
                }
                this.children.put(childName, node);
            }
        }
        
        void deleteChild(final String childName) {
            synchronized (this.children) {
                if (!this.children.containsKey(childName)) {
                    return;
                }
                final TrieNode childNode = this.children.get(childName);
                if (childNode.getChildren().length == 1) {
                    childNode.setParent(null);
                    this.children.remove(childName);
                }
                else {
                    childNode.setProperty(false);
                }
            }
        }
        
        TrieNode getChild(final String childName) {
            synchronized (this.children) {
                if (!this.children.containsKey(childName)) {
                    return null;
                }
                return this.children.get(childName);
            }
        }
        
        String[] getChildren() {
            synchronized (this.children) {
                return this.children.keySet().toArray(new String[0]);
            }
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("Children of trienode: ");
            synchronized (this.children) {
                for (final String str : this.children.keySet()) {
                    sb.append(" " + str);
                }
            }
            return sb.toString();
        }
    }
}
