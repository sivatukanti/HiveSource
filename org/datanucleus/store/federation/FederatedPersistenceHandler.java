// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.federation;

import org.datanucleus.store.PersistenceBatchType;
import org.datanucleus.ExecutionContext;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.StorePersistenceHandler;

public class FederatedPersistenceHandler implements StorePersistenceHandler
{
    FederatedStoreManager storeMgr;
    
    public FederatedPersistenceHandler(final StoreManager storeMgr) {
        this.storeMgr = (FederatedStoreManager)storeMgr;
    }
    
    @Override
    public void close() {
    }
    
    public boolean useReferentialIntegrity() {
        return false;
    }
    
    @Override
    public void insertObjects(final ObjectProvider... ops) {
        for (int i = 0; i < ops.length; ++i) {
            this.insertObject(ops[i]);
        }
    }
    
    @Override
    public void deleteObjects(final ObjectProvider... ops) {
        for (int i = 0; i < ops.length; ++i) {
            this.deleteObject(ops[i]);
        }
    }
    
    @Override
    public void batchStart(final ExecutionContext ec, final PersistenceBatchType batchType) {
    }
    
    @Override
    public void batchEnd(final ExecutionContext ec, final PersistenceBatchType type) {
    }
    
    @Override
    public void insertObject(final ObjectProvider op) {
        final StoreManager classStoreMgr = this.storeMgr.getStoreManagerForClass(op.getClassMetaData());
        classStoreMgr.getPersistenceHandler().insertObject(op);
    }
    
    @Override
    public void updateObject(final ObjectProvider op, final int[] fieldNumbers) {
        final StoreManager classStoreMgr = this.storeMgr.getStoreManagerForClass(op.getClassMetaData());
        classStoreMgr.getPersistenceHandler().updateObject(op, fieldNumbers);
    }
    
    @Override
    public void deleteObject(final ObjectProvider op) {
        final StoreManager classStoreMgr = this.storeMgr.getStoreManagerForClass(op.getClassMetaData());
        classStoreMgr.getPersistenceHandler().deleteObject(op);
    }
    
    @Override
    public void fetchObject(final ObjectProvider op, final int[] fieldNumbers) {
        final StoreManager classStoreMgr = this.storeMgr.getStoreManagerForClass(op.getClassMetaData());
        classStoreMgr.getPersistenceHandler().fetchObject(op, fieldNumbers);
    }
    
    @Override
    public void locateObject(final ObjectProvider op) {
        final StoreManager classStoreMgr = this.storeMgr.getStoreManagerForClass(op.getClassMetaData());
        classStoreMgr.getPersistenceHandler().locateObject(op);
    }
    
    @Override
    public void locateObjects(final ObjectProvider[] ops) {
        final StoreManager classStoreMgr = this.storeMgr.getStoreManagerForClass(ops[0].getClassMetaData());
        classStoreMgr.getPersistenceHandler().locateObjects(ops);
    }
    
    @Override
    public Object findObject(final ExecutionContext ec, final Object id) {
        return null;
    }
    
    @Override
    public Object[] findObjects(final ExecutionContext ec, final Object[] ids) {
        return null;
    }
}
