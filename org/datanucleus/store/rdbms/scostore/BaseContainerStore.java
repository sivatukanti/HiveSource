// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.ClassConstants;
import org.datanucleus.store.StoreManager;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.OIDMapping;
import org.datanucleus.store.rdbms.mapping.java.InterfaceMapping;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.scostore.Store;

public abstract class BaseContainerStore implements Store
{
    protected static final Localiser LOCALISER;
    protected RDBMSStoreManager storeMgr;
    protected DatastoreAdapter dba;
    protected JavaTypeMapping ownerMapping;
    protected AbstractMemberMetaData ownerMemberMetaData;
    protected RelationType relationType;
    protected boolean allowNulls;
    protected ClassLoaderResolver clr;
    
    protected BaseContainerStore(final RDBMSStoreManager storeMgr, final ClassLoaderResolver clr) {
        this.allowNulls = false;
        this.storeMgr = storeMgr;
        this.dba = this.storeMgr.getDatastoreAdapter();
        this.clr = clr;
    }
    
    protected void setOwner(final AbstractMemberMetaData mmd) {
        this.ownerMemberMetaData = mmd;
        if (Boolean.TRUE.equals(this.ownerMemberMetaData.getContainer().allowNulls())) {
            this.allowNulls = true;
        }
        this.relationType = this.ownerMemberMetaData.getRelationType(this.clr);
    }
    
    @Override
    public RDBMSStoreManager getStoreManager() {
        return this.storeMgr;
    }
    
    public JavaTypeMapping getOwnerMapping() {
        return this.ownerMapping;
    }
    
    public RelationType getRelationType() {
        return this.relationType;
    }
    
    @Override
    public AbstractMemberMetaData getOwnerMemberMetaData() {
        return this.ownerMemberMetaData;
    }
    
    public DatastoreAdapter getDatastoreAdapter() {
        return this.dba;
    }
    
    protected boolean isEmbeddedMapping(final JavaTypeMapping mapping) {
        return !InterfaceMapping.class.isAssignableFrom(mapping.getClass()) && !OIDMapping.class.isAssignableFrom(mapping.getClass()) && !PersistableMapping.class.isAssignableFrom(mapping.getClass());
    }
    
    public ObjectProvider getObjectProviderForEmbeddedPCObject(final ObjectProvider op, final Object obj, final AbstractMemberMetaData ownerMmd, final short pcType) {
        final ExecutionContext ec = op.getExecutionContext();
        ObjectProvider objOP = ec.findObjectProvider(obj);
        if (objOP == null) {
            objOP = ec.newObjectProviderForEmbedded(obj, false, op, ownerMmd.getAbsoluteFieldNumber());
        }
        objOP.setPcObjectType(pcType);
        return objOP;
    }
    
    protected boolean allowsBatching() {
        return this.storeMgr.allowsBatching();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
