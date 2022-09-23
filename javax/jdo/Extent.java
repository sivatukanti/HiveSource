// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.util.Iterator;

public interface Extent<E> extends Iterable<E>
{
    Iterator<E> iterator();
    
    boolean hasSubclasses();
    
    Class<E> getCandidateClass();
    
    PersistenceManager getPersistenceManager();
    
    void closeAll();
    
    void close(final Iterator<E> p0);
    
    FetchPlan getFetchPlan();
}
