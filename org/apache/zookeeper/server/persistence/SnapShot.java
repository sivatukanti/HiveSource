// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.zookeeper.server.DataTree;

public interface SnapShot
{
    long deserialize(final DataTree p0, final Map<Long, Integer> p1) throws IOException;
    
    void serialize(final DataTree p0, final Map<Long, Integer> p1, final File p2) throws IOException;
    
    File findMostRecentSnapshot() throws IOException;
    
    void close() throws IOException;
}
