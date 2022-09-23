// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathNotFoundException extends PathIOException
{
    static final long serialVersionUID = 0L;
    
    public PathNotFoundException(final String path) {
        super(path, "No such file or directory");
    }
    
    public PathNotFoundException(final String path, final Throwable cause) {
        super(path, cause);
    }
    
    public PathNotFoundException(final String path, final String error) {
        super(path, error);
    }
    
    public PathNotFoundException(final String path, final String error, final Throwable cause) {
        super(path, error, cause);
    }
}
