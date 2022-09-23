// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathPermissionException extends PathIOException
{
    static final long serialVersionUID = 0L;
    
    public PathPermissionException(final String path) {
        super(path, "Operation not permitted");
    }
    
    public PathPermissionException(final String path, final Throwable cause) {
        super(path, cause);
    }
    
    public PathPermissionException(final String path, final String error) {
        super(path, error);
    }
    
    public PathPermissionException(final String path, final String error, final Throwable cause) {
        super(path, error, cause);
    }
}
