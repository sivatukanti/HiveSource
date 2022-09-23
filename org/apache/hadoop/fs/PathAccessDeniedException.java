// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathAccessDeniedException extends PathIOException
{
    static final long serialVersionUID = 0L;
    
    public PathAccessDeniedException(final String path) {
        super(path, "Permission denied");
    }
    
    public PathAccessDeniedException(final String path, final Throwable cause) {
        super(path, cause);
    }
    
    public PathAccessDeniedException(final String path, final String error, final Throwable cause) {
        super(path, error, cause);
    }
}
