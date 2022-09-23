// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import java.util.Collection;
import java.util.HashMap;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.query.QueryResult;
import java.util.Iterator;
import java.util.Map;
import org.datanucleus.store.query.Query;
import org.datanucleus.FetchPlan;

public class DefaultCandidateExtent extends AbstractExtent
{
    private FetchPlan fetchPlan;
    private Query query;
    protected Map<Iterator, QueryResult> queryResultsByIterator;
    
    public DefaultCandidateExtent(final ExecutionContext ec, final Class cls, final boolean subclasses, final AbstractClassMetaData cmd) {
        super(ec, cls, subclasses, cmd);
        this.fetchPlan = null;
        this.queryResultsByIterator = new HashMap<Iterator, QueryResult>();
        this.query = ec.newQuery();
        this.fetchPlan = this.query.getFetchPlan();
        this.query.setCandidateClass(cls);
        this.query.setSubclasses(subclasses);
    }
    
    @Override
    public Iterator iterator() {
        final Object results = this.query.execute();
        Iterator iter = null;
        if (results instanceof QueryResult) {
            final QueryResult qr = (QueryResult)results;
            iter = qr.iterator();
            this.queryResultsByIterator.put(iter, qr);
        }
        else {
            iter = ((Collection)results).iterator();
        }
        return iter;
    }
    
    @Override
    public boolean hasSubclasses() {
        return this.subclasses;
    }
    
    @Override
    public ExecutionContext getExecutionContext() {
        return this.ec;
    }
    
    @Override
    public FetchPlan getFetchPlan() {
        return this.fetchPlan;
    }
    
    @Override
    public void closeAll() {
        this.queryResultsByIterator.clear();
        this.query.closeAll();
        this.fetchPlan.clearGroups().addGroup("default");
    }
    
    @Override
    public void close(final Iterator iterator) {
        final QueryResult qr = this.queryResultsByIterator.remove(iterator);
        if (qr != null) {
            this.query.close(qr);
        }
    }
}
