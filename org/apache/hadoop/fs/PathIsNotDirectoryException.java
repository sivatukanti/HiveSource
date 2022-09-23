// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathIsNotDirectoryException extends PathExistsException
{
    static final long serialVersionUID = 0L;
    
    public PathIsNotDirectoryException(final String path) {
        super(path, "Is not a directory");
    }
}
