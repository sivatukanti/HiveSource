// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class NodeBase implements Node
{
    public static final char PATH_SEPARATOR = '/';
    public static final String PATH_SEPARATOR_STR = "/";
    public static final String ROOT = "";
    protected String name;
    protected String location;
    protected int level;
    protected Node parent;
    
    public NodeBase() {
    }
    
    public NodeBase(String path) {
        path = normalize(path);
        final int index = path.lastIndexOf(47);
        if (index == -1) {
            this.set("", path);
        }
        else {
            this.set(path.substring(index + 1), path.substring(0, index));
        }
    }
    
    public NodeBase(final String name, final String location) {
        this.set(name, normalize(location));
    }
    
    public NodeBase(final String name, final String location, final Node parent, final int level) {
        this.set(name, normalize(location));
        this.parent = parent;
        this.level = level;
    }
    
    private void set(final String name, final String location) {
        if (name != null && name.contains("/")) {
            throw new IllegalArgumentException("Network location name contains /: " + name);
        }
        this.name = ((name == null) ? "" : name);
        this.location = location;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getNetworkLocation() {
        return this.location;
    }
    
    @Override
    public void setNetworkLocation(final String location) {
        this.location = location;
    }
    
    public static String getPath(final Node node) {
        return node.getNetworkLocation() + "/" + node.getName();
    }
    
    public static String[] getPathComponents(final Node node) {
        return getPath(node).split("/");
    }
    
    @Override
    public boolean equals(final Object to) {
        return this == to || (to instanceof NodeBase && getPath(this).equals(getPath((Node)to)));
    }
    
    @Override
    public int hashCode() {
        return getPath(this).hashCode();
    }
    
    @Override
    public String toString() {
        return getPath(this);
    }
    
    public static String normalize(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("Network Location is null ");
        }
        if (path.length() == 0) {
            return "";
        }
        if (path.charAt(0) != '/') {
            throw new IllegalArgumentException("Network Location path does not start with /: " + path);
        }
        final int len = path.length();
        if (path.charAt(len - 1) == '/') {
            return path.substring(0, len - 1);
        }
        return path;
    }
    
    @Override
    public Node getParent() {
        return this.parent;
    }
    
    @Override
    public void setParent(final Node parent) {
        this.parent = parent;
    }
    
    @Override
    public int getLevel() {
        return this.level;
    }
    
    @Override
    public void setLevel(final int level) {
        this.level = level;
    }
    
    public static int locationToDepth(final String location) {
        final String normalizedLocation = normalize(location);
        final int length = normalizedLocation.length();
        int depth = 0;
        for (int i = 0; i < length; ++i) {
            if (normalizedLocation.charAt(i) == '/') {
                ++depth;
            }
        }
        return depth;
    }
}
