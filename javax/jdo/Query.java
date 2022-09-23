// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.util.Map;
import java.util.Collection;
import java.io.Serializable;

public interface Query extends Serializable
{
    public static final String JDOQL = "javax.jdo.query.JDOQL";
    public static final String SQL = "javax.jdo.query.SQL";
    
    void setClass(final Class p0);
    
    void setCandidates(final Extent p0);
    
    void setCandidates(final Collection p0);
    
    void setFilter(final String p0);
    
    void declareImports(final String p0);
    
    void declareParameters(final String p0);
    
    void declareVariables(final String p0);
    
    void setOrdering(final String p0);
    
    void setIgnoreCache(final boolean p0);
    
    boolean getIgnoreCache();
    
    void compile();
    
    Object execute();
    
    Object execute(final Object p0);
    
    Object execute(final Object p0, final Object p1);
    
    Object execute(final Object p0, final Object p1, final Object p2);
    
    Object executeWithMap(final Map p0);
    
    Object executeWithArray(final Object... p0);
    
    PersistenceManager getPersistenceManager();
    
    void close(final Object p0);
    
    void closeAll();
    
    void setGrouping(final String p0);
    
    void setUnique(final boolean p0);
    
    void setResult(final String p0);
    
    void setResultClass(final Class p0);
    
    void setRange(final long p0, final long p1);
    
    void setRange(final String p0);
    
    void addExtension(final String p0, final Object p1);
    
    void setExtensions(final Map p0);
    
    FetchPlan getFetchPlan();
    
    long deletePersistentAll(final Object... p0);
    
    long deletePersistentAll(final Map p0);
    
    long deletePersistentAll();
    
    void setUnmodifiable();
    
    boolean isUnmodifiable();
    
    void addSubquery(final Query p0, final String p1, final String p2);
    
    void addSubquery(final Query p0, final String p1, final String p2, final String p3);
    
    void addSubquery(final Query p0, final String p1, final String p2, final String... p3);
    
    void addSubquery(final Query p0, final String p1, final String p2, final Map p3);
    
    void setDatastoreReadTimeoutMillis(final Integer p0);
    
    Integer getDatastoreReadTimeoutMillis();
    
    void setDatastoreWriteTimeoutMillis(final Integer p0);
    
    Integer getDatastoreWriteTimeoutMillis();
    
    void cancelAll();
    
    void cancel(final Thread p0);
    
    void setSerializeRead(final Boolean p0);
    
    Boolean getSerializeRead();
}
