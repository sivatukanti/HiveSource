// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

public class PathIsNotEmptyDirectoryException extends PathExistsException
{
    public PathIsNotEmptyDirectoryException(final String path) {
        super(path, "Directory is not empty");
    }
}
