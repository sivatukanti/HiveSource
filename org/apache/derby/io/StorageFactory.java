// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.io;

import java.io.IOException;

public interface StorageFactory
{
    public static final int VERSION_NUMBER = 1;
    
    void init(final String p0, final String p1, final String p2, final String p3) throws IOException;
    
    void shutdown();
    
    String getCanonicalName() throws IOException;
    
    StorageFile newStorageFile(final String p0);
    
    StorageFile newStorageFile(final String p0, final String p1);
    
    StorageFile newStorageFile(final StorageFile p0, final String p1);
    
    char getSeparator();
    
    StorageFile getTempDir();
    
    boolean isFast();
    
    boolean isReadOnlyDatabase();
    
    boolean supportsRandomAccess();
    
    int getStorageFactoryVersion();
    
    StorageFile createTemporaryFile(final String p0, final String p1) throws IOException;
    
    void setCanonicalName(final String p0);
}
