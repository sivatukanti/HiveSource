// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;
import org.apache.hadoop.util.StringInterner;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Serializable;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class BlockLocation implements Serializable
{
    private static final long serialVersionUID = 580415341L;
    private String[] hosts;
    private String[] cachedHosts;
    private String[] names;
    private String[] topologyPaths;
    private String[] storageIds;
    private StorageType[] storageTypes;
    private long offset;
    private long length;
    private boolean corrupt;
    private static final String[] EMPTY_STR_ARRAY;
    private static final StorageType[] EMPTY_STORAGE_TYPE_ARRAY;
    
    public BlockLocation() {
        this(BlockLocation.EMPTY_STR_ARRAY, BlockLocation.EMPTY_STR_ARRAY, 0L, 0L);
    }
    
    public BlockLocation(final BlockLocation that) {
        this.hosts = that.hosts;
        this.cachedHosts = that.cachedHosts;
        this.names = that.names;
        this.topologyPaths = that.topologyPaths;
        this.offset = that.offset;
        this.length = that.length;
        this.corrupt = that.corrupt;
        this.storageIds = that.storageIds;
        this.storageTypes = that.storageTypes;
    }
    
    public BlockLocation(final String[] names, final String[] hosts, final long offset, final long length) {
        this(names, hosts, offset, length, false);
    }
    
    public BlockLocation(final String[] names, final String[] hosts, final long offset, final long length, final boolean corrupt) {
        this(names, hosts, null, offset, length, corrupt);
    }
    
    public BlockLocation(final String[] names, final String[] hosts, final String[] topologyPaths, final long offset, final long length) {
        this(names, hosts, topologyPaths, offset, length, false);
    }
    
    public BlockLocation(final String[] names, final String[] hosts, final String[] topologyPaths, final long offset, final long length, final boolean corrupt) {
        this(names, hosts, null, topologyPaths, offset, length, corrupt);
    }
    
    public BlockLocation(final String[] names, final String[] hosts, final String[] cachedHosts, final String[] topologyPaths, final long offset, final long length, final boolean corrupt) {
        this(names, hosts, cachedHosts, topologyPaths, null, null, offset, length, corrupt);
    }
    
    public BlockLocation(final String[] names, final String[] hosts, final String[] cachedHosts, final String[] topologyPaths, final String[] storageIds, final StorageType[] storageTypes, final long offset, final long length, final boolean corrupt) {
        if (names == null) {
            this.names = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.names = StringInterner.internStringsInArray(names);
        }
        if (hosts == null) {
            this.hosts = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.hosts = StringInterner.internStringsInArray(hosts);
        }
        if (cachedHosts == null) {
            this.cachedHosts = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.cachedHosts = StringInterner.internStringsInArray(cachedHosts);
        }
        if (topologyPaths == null) {
            this.topologyPaths = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.topologyPaths = StringInterner.internStringsInArray(topologyPaths);
        }
        if (storageIds == null) {
            this.storageIds = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.storageIds = StringInterner.internStringsInArray(storageIds);
        }
        if (storageTypes == null) {
            this.storageTypes = BlockLocation.EMPTY_STORAGE_TYPE_ARRAY;
        }
        else {
            this.storageTypes = storageTypes;
        }
        this.offset = offset;
        this.length = length;
        this.corrupt = corrupt;
    }
    
    public String[] getHosts() throws IOException {
        return this.hosts;
    }
    
    public String[] getCachedHosts() {
        return this.cachedHosts;
    }
    
    public String[] getNames() throws IOException {
        return this.names;
    }
    
    public String[] getTopologyPaths() throws IOException {
        return this.topologyPaths;
    }
    
    public String[] getStorageIds() {
        return this.storageIds;
    }
    
    public StorageType[] getStorageTypes() {
        return this.storageTypes;
    }
    
    public long getOffset() {
        return this.offset;
    }
    
    public long getLength() {
        return this.length;
    }
    
    public boolean isCorrupt() {
        return this.corrupt;
    }
    
    public void setOffset(final long offset) {
        this.offset = offset;
    }
    
    public void setLength(final long length) {
        this.length = length;
    }
    
    public void setCorrupt(final boolean corrupt) {
        this.corrupt = corrupt;
    }
    
    public void setHosts(final String[] hosts) throws IOException {
        if (hosts == null) {
            this.hosts = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.hosts = StringInterner.internStringsInArray(hosts);
        }
    }
    
    public void setCachedHosts(final String[] cachedHosts) {
        if (cachedHosts == null) {
            this.cachedHosts = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.cachedHosts = StringInterner.internStringsInArray(cachedHosts);
        }
    }
    
    public void setNames(final String[] names) throws IOException {
        if (names == null) {
            this.names = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.names = StringInterner.internStringsInArray(names);
        }
    }
    
    public void setTopologyPaths(final String[] topologyPaths) throws IOException {
        if (topologyPaths == null) {
            this.topologyPaths = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.topologyPaths = StringInterner.internStringsInArray(topologyPaths);
        }
    }
    
    public void setStorageIds(final String[] storageIds) {
        if (storageIds == null) {
            this.storageIds = BlockLocation.EMPTY_STR_ARRAY;
        }
        else {
            this.storageIds = StringInterner.internStringsInArray(storageIds);
        }
    }
    
    public void setStorageTypes(final StorageType[] storageTypes) {
        if (storageTypes == null) {
            this.storageTypes = BlockLocation.EMPTY_STORAGE_TYPE_ARRAY;
        }
        else {
            this.storageTypes = storageTypes;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.offset);
        result.append(',');
        result.append(this.length);
        if (this.corrupt) {
            result.append("(corrupt)");
        }
        for (final String h : this.hosts) {
            result.append(',');
            result.append(h);
        }
        return result.toString();
    }
    
    static {
        EMPTY_STR_ARRAY = new String[0];
        EMPTY_STORAGE_TYPE_ARRAY = new StorageType[0];
    }
}
