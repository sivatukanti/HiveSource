// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathOperationException extends PathExistsException
{
    static final long serialVersionUID = 0L;
    
    public PathOperationException(final String path) {
        super(path, "Operation not supported");
    }
}
