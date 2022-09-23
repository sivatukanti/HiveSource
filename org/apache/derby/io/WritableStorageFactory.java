// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.io;

import java.io.SyncFailedException;
import java.io.IOException;
import java.io.OutputStream;

public interface WritableStorageFactory extends StorageFactory
{
    void sync(final OutputStream p0, final boolean p1) throws IOException, SyncFailedException;
    
    boolean supportsWriteSync();
}
