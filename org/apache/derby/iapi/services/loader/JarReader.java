// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.io.StorageFile;

public interface JarReader
{
    StorageFile getJarFile(final String p0, final String p1) throws StandardException;
}
