// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathExistsException extends PathIOException
{
    static final long serialVersionUID = 0L;
    
    public PathExistsException(final String path) {
        super(path, "File exists");
    }
    
    public PathExistsException(final String path, final String error) {
        super(path, error);
    }
}
