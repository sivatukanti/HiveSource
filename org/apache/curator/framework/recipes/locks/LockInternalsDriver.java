// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.locks;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;

public interface LockInternalsDriver extends LockInternalsSorter
{
    PredicateResults getsTheLock(final CuratorFramework p0, final List<String> p1, final String p2, final int p3) throws Exception;
    
    String createsTheLock(final CuratorFramework p0, final String p1, final byte[] p2) throws Exception;
}
