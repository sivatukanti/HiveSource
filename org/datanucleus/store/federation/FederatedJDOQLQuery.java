// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.federation;

import java.util.Map;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.query.AbstractJDOQLQuery;

public class FederatedJDOQLQuery extends AbstractJDOQLQuery
{
    public FederatedJDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec) {
        super(storeMgr, ec);
    }
    
    public FederatedJDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final FederatedJDOQLQuery q) {
        super(storeMgr, ec, q);
    }
    
    public FederatedJDOQLQuery(final StoreManager storeMgr, final ExecutionContext ec, final String query) {
        super(storeMgr, ec, query);
    }
    
    @Override
    protected Object performExecute(final Map parameters) {
        return null;
    }
}
