// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.HashMap;
import javax.jdo.FetchPlan;
import java.util.Collection;
import javax.jdo.Extent;
import javax.jdo.JDOUnsupportedOptionException;
import javax.jdo.JDOException;
import org.datanucleus.store.query.QueryInterruptedException;
import javax.jdo.JDOQueryInterruptedException;
import org.datanucleus.store.query.QueryTimeoutException;
import javax.jdo.JDODataStoreException;
import java.util.Map;
import org.datanucleus.store.query.NoQueryResultsException;
import org.datanucleus.exceptions.NucleusException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class JDOQuery implements Query
{
    transient PersistenceManager pm;
    org.datanucleus.store.query.Query query;
    String language;
    JDOFetchPlan fetchPlan;
    
    public JDOQuery(final PersistenceManager pm, final org.datanucleus.store.query.Query query, final String language) {
        this.fetchPlan = null;
        this.pm = pm;
        this.query = query;
        this.language = language;
    }
    
    public void close(final Object queryResult) {
        this.query.close(queryResult);
    }
    
    public void closeAll() {
        this.query.closeAll();
    }
    
    public void compile() {
        try {
            this.query.compile();
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void declareImports(final String imports) {
        try {
            this.query.declareImports(imports);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void declareParameters(final String parameters) {
        try {
            this.query.declareExplicitParameters(parameters);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void declareVariables(final String variables) {
        try {
            this.query.declareExplicitVariables(variables);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public long deletePersistentAll() {
        try {
            return this.query.deletePersistentAll();
        }
        catch (NoQueryResultsException nqre) {
            return 0L;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public long deletePersistentAll(final Object... parameters) {
        try {
            return this.query.deletePersistentAll(parameters);
        }
        catch (NoQueryResultsException nqre) {
            return 0L;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public long deletePersistentAll(final Map parameters) {
        try {
            return this.query.deletePersistentAll(parameters);
        }
        catch (NoQueryResultsException nqre) {
            return 0L;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Object execute() {
        try {
            return this.query.execute();
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Object execute(final Object p1) {
        try {
            return this.query.executeWithArray(new Object[] { p1 });
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Object execute(final Object p1, final Object p2) {
        try {
            return this.query.executeWithArray(new Object[] { p1, p2 });
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Object execute(final Object p1, final Object p2, final Object p3) {
        try {
            return this.query.executeWithArray(new Object[] { p1, p2, p3 });
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Object executeWithArray(final Object... parameterValues) {
        try {
            return this.query.executeWithArray(parameterValues);
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Object executeWithMap(final Map parameters) {
        try {
            return this.query.executeWithMap(parameters);
        }
        catch (NoQueryResultsException nqre) {
            return null;
        }
        catch (QueryTimeoutException qte) {
            throw new JDODataStoreException("Query has timed out : " + qte.getMessage());
        }
        catch (QueryInterruptedException qie) {
            throw new JDOQueryInterruptedException("Query has been cancelled : " + qie.getMessage());
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void cancelAll() {
        try {
            this.query.cancel();
        }
        catch (NucleusException ne) {
            throw new JDOException("Error in calling Query.cancelAll. See the nested exception", ne);
        }
        catch (UnsupportedOperationException uoe) {
            throw new JDOUnsupportedOptionException();
        }
    }
    
    public void cancel(final Thread thr) {
        try {
            this.query.cancel(thr);
        }
        catch (NucleusException ne) {
            throw new JDOException("Error in calling Query.cancelAll. See the nested exception", ne);
        }
        catch (UnsupportedOperationException uoe) {
            throw new JDOUnsupportedOptionException();
        }
    }
    
    public void setCandidates(final Extent extent) {
        try {
            if (extent == null) {
                this.query.setCandidates((org.datanucleus.store.Extent)null);
            }
            else {
                this.query.setCandidates(((JDOExtent)extent).getExtent());
            }
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setCandidates(final Collection pcs) {
        try {
            this.query.setCandidates(pcs);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setClass(final Class candidateClass) {
        try {
            this.query.setCandidateClass(candidateClass);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void addExtension(final String key, final Object value) {
        this.query.addExtension(key, value);
    }
    
    public void setExtensions(final Map extensions) {
        this.query.setExtensions(extensions);
    }
    
    public FetchPlan getFetchPlan() {
        if (this.fetchPlan == null) {
            this.fetchPlan = new JDOFetchPlan(this.query.getFetchPlan());
        }
        return this.fetchPlan;
    }
    
    public void setFilter(final String filter) {
        try {
            this.query.setFilter(filter);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setGrouping(final String grouping) {
        try {
            this.query.setGrouping(grouping);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public boolean getIgnoreCache() {
        return this.query.getIgnoreCache();
    }
    
    public void setIgnoreCache(final boolean ignoreCache) {
        this.query.setIgnoreCache(ignoreCache);
    }
    
    public void setOrdering(final String ordering) {
        try {
            this.query.setOrdering(ordering);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public PersistenceManager getPersistenceManager() {
        return this.pm;
    }
    
    public void setRange(final String range) {
        try {
            this.query.setRange(range);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setRange(final long fromIncl, final long toExcl) {
        try {
            this.query.setRange(fromIncl, toExcl);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setResult(final String result) {
        try {
            this.query.setResult(result);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setResultClass(final Class result_cls) {
        try {
            this.query.setResultClass(result_cls);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public void setDatastoreReadTimeoutMillis(final Integer timeout) {
        try {
            this.query.setDatastoreReadTimeoutMillis(timeout);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Integer getDatastoreReadTimeoutMillis() {
        return this.query.getDatastoreReadTimeoutMillis();
    }
    
    public void setDatastoreWriteTimeoutMillis(final Integer timeout) {
        try {
            this.query.setDatastoreWriteTimeoutMillis(timeout);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Integer getDatastoreWriteTimeoutMillis() {
        return this.query.getDatastoreWriteTimeoutMillis();
    }
    
    public void setUnique(final boolean unique) {
        try {
            this.query.setUnique(unique);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public boolean isUnmodifiable() {
        return this.query.isUnmodifiable();
    }
    
    public void setUnmodifiable() {
        this.query.setUnmodifiable();
    }
    
    public void addSubquery(final Query sub, final String variableDecl, final String candidateExpr) {
        this.addSubquery(sub, variableDecl, candidateExpr, (Map)null);
    }
    
    public void addSubquery(final Query sub, final String variableDecl, final String candidateExpr, final String parameter) {
        final Map paramMap = new HashMap();
        if (parameter != null) {
            paramMap.put(0, parameter);
        }
        this.addSubquery(sub, variableDecl, candidateExpr, paramMap);
    }
    
    public void addSubquery(final Query sub, final String variableDecl, final String candidateExpr, final String... parameters) {
        final Map paramMap = new HashMap();
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                paramMap.put(i, parameters[i]);
            }
        }
        this.addSubquery(sub, variableDecl, candidateExpr, paramMap);
    }
    
    public void addSubquery(final Query sub, final String variableDecl, final String candidateExpr, final Map parameters) {
        try {
            org.datanucleus.store.query.Query subquery = null;
            if (sub != null) {
                subquery = ((JDOQuery)sub).query;
            }
            this.query.addSubquery(subquery, variableDecl, candidateExpr, parameters);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
    }
    
    public Boolean getSerializeRead() {
        return this.query.getSerializeRead();
    }
    
    public void setSerializeRead(final Boolean serialize) {
        this.query.setSerializeRead(serialize);
    }
    
    public org.datanucleus.store.query.Query getInternalQuery() {
        return this.query;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    @Override
    public String toString() {
        return this.query.toString();
    }
    
    public Object getNativeQuery() {
        return this.query.getNativeQuery();
    }
}
