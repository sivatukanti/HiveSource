// 
// Decompiled by Procyon v0.5.36
// 

package org.iq80.leveldb;

import java.io.IOException;
import java.io.File;

public interface DBFactory
{
    DB open(final File p0, final Options p1) throws IOException;
    
    void destroy(final File p0, final Options p1) throws IOException;
    
    void repair(final File p0, final Options p1) throws IOException;
}
