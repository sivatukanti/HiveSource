// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.FetchPlan;
import java.util.Iterator;
import javax.jdo.PersistenceManager;
import javax.jdo.Extent;

public class JDOExtent implements Extent
{
    PersistenceManager pm;
    org.datanucleus.store.Extent extent;
    JDOFetchPlan fetchPlan;
    
    public JDOExtent(final PersistenceManager pm, final org.datanucleus.store.Extent extent) {
        this.fetchPlan = null;
        this.pm = pm;
        this.extent = extent;
        this.fetchPlan = new JDOFetchPlan(extent.getFetchPlan());
    }
    
    public void close(final Iterator iterator) {
        this.extent.close(iterator);
    }
    
    public void closeAll() {
        this.extent.closeAll();
    }
    
    public Class getCandidateClass() {
        return this.extent.getCandidateClass();
    }
    
    public boolean hasSubclasses() {
        return this.extent.hasSubclasses();
    }
    
    public FetchPlan getFetchPlan() {
        return this.fetchPlan;
    }
    
    public PersistenceManager getPersistenceManager() {
        return this.pm;
    }
    
    public org.datanucleus.store.Extent getExtent() {
        return this.extent;
    }
    
    public Iterator iterator() {
        return this.extent.iterator();
    }
}
