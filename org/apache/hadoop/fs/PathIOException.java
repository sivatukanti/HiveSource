// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.IOException;

public class PathIOException extends IOException
{
    static final long serialVersionUID = 0L;
    private static final String EIO = "Input/output error";
    private String operation;
    private String path;
    private String fullyQualifiedPath;
    private String targetPath;
    
    public PathIOException(final String path) {
        this(path, "Input/output error");
    }
    
    public PathIOException(final String path, final Throwable cause) {
        this(path, "Input/output error", cause);
    }
    
    public PathIOException(final String path, final String error) {
        super(error);
        this.path = path;
    }
    
    protected PathIOException(final String path, final String error, final Throwable cause) {
        super(error, cause);
        this.path = path;
    }
    
    public PathIOException withFullyQualifiedPath(final String fqPath) {
        this.fullyQualifiedPath = fqPath;
        return this;
    }
    
    @Override
    public String getMessage() {
        final StringBuilder message = new StringBuilder();
        if (this.operation != null) {
            message.append(this.operation + " ");
        }
        message.append(this.formatPath(this.path));
        if (this.targetPath != null) {
            message.append(" to " + this.formatPath(this.targetPath));
        }
        message.append(": " + super.getMessage());
        if (this.getCause() != null) {
            message.append(": " + this.getCause().getMessage());
        }
        if (this.fullyQualifiedPath != null && !this.fullyQualifiedPath.equals(this.path)) {
            message.append(": ").append(this.formatPath(this.fullyQualifiedPath));
        }
        return message.toString();
    }
    
    public Path getPath() {
        return new Path(this.path);
    }
    
    public Path getTargetPath() {
        return (this.targetPath != null) ? new Path(this.targetPath) : null;
    }
    
    public void setOperation(final String operation) {
        this.operation = operation;
    }
    
    public void setTargetPath(final String targetPath) {
        this.targetPath = targetPath;
    }
    
    private String formatPath(final String path) {
        return "`" + path + "'";
    }
}
