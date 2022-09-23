// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface QueryMetadata extends Metadata
{
    String getName();
    
    QueryMetadata setLanguage(final String p0);
    
    String getLanguage();
    
    QueryMetadata setQuery(final String p0);
    
    String getQuery();
    
    QueryMetadata setResultClass(final String p0);
    
    String getResultClass();
    
    QueryMetadata setUnique(final boolean p0);
    
    Boolean getUnique();
    
    QueryMetadata setUnmodifiable();
    
    boolean getUnmodifiable();
    
    QueryMetadata setFetchPlan(final String p0);
    
    String getFetchPlan();
}
