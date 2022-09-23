// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.QueryMetaData;
import javax.jdo.metadata.QueryMetadata;

public class QueryMetadataImpl extends AbstractMetadataImpl implements QueryMetadata
{
    public QueryMetadataImpl(final QueryMetaData querymd) {
        super(querymd);
    }
    
    public QueryMetaData getInternal() {
        return (QueryMetaData)this.internalMD;
    }
    
    public String getFetchPlan() {
        return this.getInternal().getFetchPlanName();
    }
    
    public String getLanguage() {
        return this.getInternal().getLanguage();
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public String getQuery() {
        return this.getInternal().getQuery();
    }
    
    public String getResultClass() {
        return this.getInternal().getResultClass();
    }
    
    public Boolean getUnique() {
        return this.getInternal().isUnique();
    }
    
    public boolean getUnmodifiable() {
        return this.getInternal().isUnmodifiable();
    }
    
    public QueryMetadata setFetchPlan(final String fpName) {
        this.getInternal().setFetchPlanName(fpName);
        return this;
    }
    
    public QueryMetadata setLanguage(final String lang) {
        this.getInternal().setLanguage(lang);
        return this;
    }
    
    public QueryMetadata setQuery(final String query) {
        this.getInternal().setQuery(query);
        return this;
    }
    
    public QueryMetadata setResultClass(final String resultClass) {
        this.getInternal().setResultClass(resultClass);
        return this;
    }
    
    public QueryMetadata setUnique(final boolean unique) {
        this.getInternal().setUnique(unique);
        return this;
    }
    
    public QueryMetadata setUnmodifiable() {
        this.getInternal().setUnmodifiable(true);
        return this;
    }
}
