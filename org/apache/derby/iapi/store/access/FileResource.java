// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;

public interface FileResource
{
    public static final String JAR_DIRECTORY_NAME = "jar";
    
    long add(final String p0, final InputStream p1) throws StandardException;
    
    void remove(final String p0, final long p1) throws StandardException;
    
    void removeJarDir(final String p0) throws StandardException;
    
    long replace(final String p0, final long p1, final InputStream p2) throws StandardException;
    
    StorageFile getAsFile(final String p0, final long p1);
    
    char getSeparatorChar();
}
