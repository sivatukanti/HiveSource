// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.Iterator;
import org.datanucleus.util.StringUtils;
import java.util.Map;
import org.datanucleus.store.Extent;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import java.util.Collection;

public abstract class AbstractJavaQuery extends Query
{
    protected transient Collection candidateCollection;
    protected String singleString;
    
    public AbstractJavaQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
        this.candidateCollection = null;
        this.singleString = null;
    }
    
    @Override
    public void setCandidates(final Extent pcs) {
        this.discardCompiled();
        this.assertIsModifiable();
        if (pcs != null) {
            this.setCandidateClass(pcs.getCandidateClass());
            this.setSubclasses(pcs.hasSubclasses());
        }
        this.candidateCollection = null;
    }
    
    @Override
    public void setCandidates(final Collection pcs) {
        this.discardCompiled();
        this.assertIsModifiable();
        this.candidateCollection = pcs;
    }
    
    @Override
    protected void discardCompiled() {
        super.discardCompiled();
        this.singleString = null;
    }
    
    @Override
    protected long performDeletePersistentAll(final Map parameters) {
        if (this.candidateCollection != null && this.candidateCollection.isEmpty()) {
            return 0L;
        }
        return super.performDeletePersistentAll(parameters);
    }
    
    public abstract String getSingleStringQuery();
    
    @Override
    public String toString() {
        return this.getSingleStringQuery();
    }
    
    protected boolean evaluateInMemory() {
        return this.getBooleanExtensionProperty("datanucleus.query.evaluateInMemory", false);
    }
    
    protected String dereferenceFilter(final String input) {
        if (this.subqueries == null) {
            return input;
        }
        String output = input;
        for (final Map.Entry<String, SubqueryDefinition> entry : this.subqueries.entrySet()) {
            final SubqueryDefinition subqueryDefinition = entry.getValue();
            final AbstractJavaQuery subquery = (AbstractJavaQuery)subqueryDefinition.getQuery();
            output = StringUtils.replaceAll(output, entry.getKey(), "(" + subquery.getSingleStringQuery() + ")");
        }
        return output;
    }
}
