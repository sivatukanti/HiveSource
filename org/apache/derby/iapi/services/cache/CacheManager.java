// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.cache;

import java.util.Collection;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.error.StandardException;

public interface CacheManager
{
    Cacheable find(final Object p0) throws StandardException;
    
    Cacheable findCached(final Object p0) throws StandardException;
    
    Cacheable create(final Object p0, final Object p1) throws StandardException;
    
    void release(final Cacheable p0);
    
    void remove(final Cacheable p0) throws StandardException;
    
    void cleanAll() throws StandardException;
    
    void clean(final Matchable p0) throws StandardException;
    
    void ageOut();
    
    void shutdown() throws StandardException;
    
    void useDaemonService(final DaemonService p0);
    
    boolean discard(final Matchable p0);
    
    Collection values();
}
