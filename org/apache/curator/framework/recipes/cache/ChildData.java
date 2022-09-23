// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.cache;

import java.util.Arrays;
import org.apache.curator.utils.PathUtils;
import org.apache.zookeeper.data.Stat;

public class ChildData implements Comparable<ChildData>
{
    private final String path;
    private final Stat stat;
    private final byte[] data;
    
    public ChildData(final String path, final Stat stat, final byte[] data) {
        this.path = PathUtils.validatePath(path);
        this.stat = stat;
        this.data = data;
    }
    
    @Override
    public int compareTo(final ChildData rhs) {
        if (this == rhs) {
            return 0;
        }
        if (rhs == null || this.getClass() != rhs.getClass()) {
            return -1;
        }
        return this.path.compareTo(rhs.path);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ChildData childData = (ChildData)o;
        if (!Arrays.equals(this.data, childData.data)) {
            return false;
        }
        Label_0078: {
            if (this.path != null) {
                if (this.path.equals(childData.path)) {
                    break Label_0078;
                }
            }
            else if (childData.path == null) {
                break Label_0078;
            }
            return false;
        }
        if (this.stat != null) {
            if (this.stat.equals(childData.stat)) {
                return true;
            }
        }
        else if (childData.stat == null) {
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.path != null) ? this.path.hashCode() : 0;
        result = 31 * result + ((this.stat != null) ? this.stat.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(this.data);
        return result;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public Stat getStat() {
        return this.stat;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    @Override
    public String toString() {
        return "ChildData{path='" + this.path + '\'' + ", stat=" + this.stat + ", data=" + Arrays.toString(this.data) + '}';
    }
}
