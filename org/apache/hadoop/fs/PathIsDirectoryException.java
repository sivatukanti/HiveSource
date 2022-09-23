// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathIsDirectoryException extends PathExistsException
{
    static final long serialVersionUID = 0L;
    
    public PathIsDirectoryException(final String path) {
        super(path, "Is a directory");
    }
}
