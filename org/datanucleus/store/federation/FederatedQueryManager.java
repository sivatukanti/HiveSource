// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.federation;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.query.Query;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.NucleusContext;
import org.datanucleus.store.query.QueryManager;

public class FederatedQueryManager extends QueryManager
{
    public FederatedQueryManager(final NucleusContext nucleusContext, final StoreManager storeMgr) {
        super(nucleusContext, storeMgr);
    }
    
    @Override
    protected void initialiseQueryCaches() {
    }
    
    @Override
    public Query newQuery(final String language, final ExecutionContext ec, final Object query) {
        if (language == null) {
            return null;
        }
        final String languageImpl = language;
        if (query == null) {
            throw new NucleusException("Not yet supported for queries with unknown candidate");
        }
        if (query instanceof String) {
            final String queryString = (String)query;
            String candidateName = null;
            if (languageImpl.equalsIgnoreCase("JDOQL")) {
                final int candidateStart = queryString.toUpperCase().indexOf(" FROM ") + 6;
                final int candidateEnd = queryString.indexOf(" ", candidateStart + 1);
                candidateName = queryString.substring(candidateStart, candidateEnd);
            }
            if (candidateName != null) {
                final ClassLoaderResolver clr = this.nucleusCtx.getClassLoaderResolver(null);
                final AbstractClassMetaData cmd = this.nucleusCtx.getMetaDataManager().getMetaDataForClass(candidateName, clr);
                final StoreManager classStoreMgr = ((FederatedStoreManager)this.storeMgr).getStoreManagerForClass(cmd);
                return classStoreMgr.getQueryManager().newQuery(languageImpl, ec, query);
            }
            throw new NucleusException("Not yet supported for single-string queries");
        }
        else {
            if (query instanceof Query) {
                final StoreManager storeMgr = ((Query)query).getStoreManager();
                return storeMgr.getQueryManager().newQuery(languageImpl, ec, query);
            }
            if (query instanceof Class) {
                final Class cls = (Class)query;
                final ClassLoaderResolver clr2 = this.nucleusCtx.getClassLoaderResolver(cls.getClassLoader());
                final AbstractClassMetaData cmd2 = this.nucleusCtx.getMetaDataManager().getMetaDataForClass(cls, clr2);
                final StoreManager classStoreMgr2 = ((FederatedStoreManager)this.storeMgr).getStoreManagerForClass(cmd2);
                return classStoreMgr2.getQueryManager().newQuery(languageImpl, ec, query);
            }
            throw new NucleusException("Not yet supported for queries taking in object of type " + query.getClass());
        }
    }
}
