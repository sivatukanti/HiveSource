// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

class PathAndBytes
{
    private final String path;
    private final byte[] data;
    
    PathAndBytes(final String path, final byte[] data) {
        this.path = path;
        this.data = data;
    }
    
    String getPath() {
        return this.path;
    }
    
    byte[] getData() {
        return this.data;
    }
}
