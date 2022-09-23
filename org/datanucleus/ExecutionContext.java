// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.concurrent.locks.Lock;
import org.datanucleus.state.CallbackHandler;
import java.util.List;
import org.datanucleus.state.RelationshipManager;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.flush.Operation;
import org.datanucleus.flush.OperationQueue;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.Extent;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.FieldValues;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.types.TypeManager;
import java.util.Set;
import org.datanucleus.management.ManagerStatistics;
import org.datanucleus.state.LockManager;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.store.StoreManager;
import java.util.Map;

public interface ExecutionContext
{
    public static final String OPTION_USERNAME = "user";
    public static final String OPTION_PASSWORD = "password";
    public static final String OPTION_JTA_AUTOJOIN = "jta_autojoin";
    
    void initialise(final Object p0, final Map<String, Object> p1);
    
    Object getOwner();
    
    Transaction getTransaction();
    
    StoreManager getStoreManager();
    
    MetaDataManager getMetaDataManager();
    
    NucleusContext getNucleusContext();
    
    ApiAdapter getApiAdapter();
    
    FetchPlan getFetchPlan();
    
    ClassLoaderResolver getClassLoaderResolver();
    
    LockManager getLockManager();
    
    ManagerStatistics getStatistics();
    
    void setProperties(final Map p0);
    
    void setProperty(final String p0, final Object p1);
    
    Object getProperty(final String p0);
    
    Boolean getBooleanProperty(final String p0);
    
    Integer getIntProperty(final String p0);
    
    String getStringProperty(final String p0);
    
    Map<String, Object> getProperties();
    
    Set<String> getSupportedProperties();
    
    TypeManager getTypeManager();
    
    void close();
    
    boolean isClosed();
    
    boolean getIgnoreCache();
    
    Integer getDatastoreReadTimeoutMillis();
    
    Integer getDatastoreWriteTimeoutMillis();
    
    ObjectProvider findObjectProvider(final Object p0);
    
    ObjectProvider findObjectProvider(final Object p0, final boolean p1);
    
    ObjectProvider findObjectProviderForEmbedded(final Object p0, final ObjectProvider p1, final AbstractMemberMetaData p2);
    
    ObjectProvider findObjectProviderOfOwnerForAttachingObject(final Object p0);
    
    void hereIsObjectProvider(final ObjectProvider p0, final Object p1);
    
    ObjectProvider newObjectProviderForHollow(final Class p0, final Object p1);
    
    ObjectProvider newObjectProviderForHollowPreConstructed(final Object p0, final Object p1);
    
    ObjectProvider newObjectProviderForHollowPopulated(final Class p0, final Object p1, final FieldValues p2);
    
    ObjectProvider newObjectProviderForPersistentClean(final Object p0, final Object p1);
    
    @Deprecated
    ObjectProvider newObjectProviderForHollowPopulatedAppId(final Class p0, final FieldValues p1);
    
    ObjectProvider newObjectProviderForEmbedded(final Object p0, final boolean p1, final ObjectProvider p2, final int p3);
    
    ObjectProvider newObjectProviderForEmbedded(final AbstractClassMetaData p0, final ObjectProvider p1, final int p2);
    
    ObjectProvider newObjectProviderForPersistentNew(final Object p0, final FieldValues p1);
    
    ObjectProvider newObjectProviderForTransactionalTransient(final Object p0);
    
    ObjectProvider newObjectProviderForDetached(final Object p0, final Object p1, final Object p2);
    
    ObjectProvider newObjectProviderForPNewToBeDeleted(final Object p0);
    
    void addObjectProvider(final ObjectProvider p0);
    
    void removeObjectProvider(final ObjectProvider p0);
    
    void evictObject(final Object p0);
    
    void evictObjects(final Class p0, final boolean p1);
    
    void evictAllObjects();
    
    void retrieveObject(final Object p0, final boolean p1);
    
    Object persistObject(final Object p0, final boolean p1);
    
    Object[] persistObjects(final Object[] p0);
    
    Object persistObjectInternal(final Object p0, final FieldValues p1, final ObjectProvider p2, final int p3, final int p4);
    
    Object persistObjectInternal(final Object p0, final ObjectProvider p1, final int p2, final int p3);
    
    Object persistObjectInternal(final Object p0, final FieldValues p1, final int p2);
    
    void makeObjectTransient(final Object p0, final FetchPlanState p1);
    
    void makeObjectTransactional(final Object p0);
    
    void makeObjectNontransactional(final Object p0);
    
    boolean exists(final Object p0);
    
    Set getManagedObjects();
    
    Set getManagedObjects(final Class[] p0);
    
    Set getManagedObjects(final String[] p0);
    
    Set getManagedObjects(final String[] p0, final Class[] p1);
    
    void deleteObject(final Object p0);
    
    void deleteObjects(final Object[] p0);
    
    void deleteObjectInternal(final Object p0);
    
    void detachObject(final Object p0, final FetchPlanState p1);
    
    Object detachObjectCopy(final Object p0, final FetchPlanState p1);
    
    void detachAll();
    
    void attachObject(final ObjectProvider p0, final Object p1, final boolean p2);
    
    Object attachObjectCopy(final ObjectProvider p0, final Object p1, final boolean p2);
    
    Object getAttachedObjectForId(final Object p0);
    
    void refreshObject(final Object p0);
    
    void refreshAllObjects();
    
    void enlistInTransaction(final ObjectProvider p0);
    
    boolean isEnlistedInTransaction(final Object p0);
    
    void evictFromTransaction(final ObjectProvider p0);
    
    void markDirty(final ObjectProvider p0, final boolean p1);
    
    void clearDirty(final ObjectProvider p0);
    
    void clearDirty();
    
    boolean isDelayDatastoreOperationsEnabled();
    
    void processNontransactionalUpdate();
    
    Object findObject(final Object p0, final boolean p1, final boolean p2, final String p3);
    
    Object findObject(final Object p0, final FieldValues p1, final Class p2, final boolean p3, final boolean p4);
    
    Object[] findObjects(final Object[] p0, final boolean p1);
    
    Extent getExtent(final Class p0, final boolean p1);
    
    Query newQuery();
    
    void putObjectIntoLevel1Cache(final ObjectProvider p0);
    
    Object getObjectFromCache(final Object p0);
    
    Object[] getObjectsFromCache(final Object[] p0);
    
    void removeObjectFromLevel1Cache(final Object p0);
    
    void removeObjectFromLevel2Cache(final Object p0);
    
    void markFieldsForUpdateInLevel2Cache(final Object p0, final boolean[] p1);
    
    boolean hasIdentityInCache(final Object p0);
    
    Object newObjectId(final Class p0, final Object p1);
    
    Object newObjectId(final String p0, final Object p1);
    
    boolean getSerializeReadForClass(final String p0);
    
    void assertClassPersistable(final Class p0);
    
    boolean hasPersistenceInformationForClass(final Class p0);
    
    boolean isInserting(final Object p0);
    
    boolean isFlushing();
    
    boolean isRunningDetachAllOnCommit();
    
    void flush();
    
    void flushInternal(final boolean p0);
    
    OperationQueue getOperationQueue();
    
    void addOperationToQueue(final Operation p0);
    
    void flushOperationsForBackingStore(final Store p0, final ObjectProvider p1);
    
    boolean getMultithreaded();
    
    boolean getManageRelations();
    
    RelationshipManager getRelationshipManager(final ObjectProvider p0);
    
    boolean isManagingRelations();
    
    List<ObjectProvider> getObjectsToBeFlushed();
    
    CallbackHandler getCallbackHandler();
    
    void addListener(final Object p0, final Class[] p1);
    
    void removeListener(final Object p0);
    
    void disconnectLifecycleListener();
    
    FetchGroup getInternalFetchGroup(final Class p0, final String p1);
    
    void addInternalFetchGroup(final FetchGroup p0);
    
    Set getFetchGroupsWithName(final String p0);
    
    Lock getLock();
    
    Object newInstance(final Class p0);
    
    boolean isObjectModifiedInTransaction(final Object p0);
    
    void replaceObjectId(final Object p0, final Object p1, final Object p2);
    
    Object getAttachDetachReferencedObject(final ObjectProvider p0);
    
    void setAttachDetachReferencedObject(final ObjectProvider p0, final Object p1);
    
    EmbeddedOwnerRelation registerEmbeddedRelation(final ObjectProvider p0, final int p1, final ObjectProvider p2);
    
    void deregisterEmbeddedRelation(final EmbeddedOwnerRelation p0);
    
    List<EmbeddedOwnerRelation> getOwnerInformationForEmbedded(final ObjectProvider p0);
    
    List<EmbeddedOwnerRelation> getEmbeddedInformationForOwner(final ObjectProvider p0);
    
    void removeEmbeddedOwnerRelation(final ObjectProvider p0, final int p1, final ObjectProvider p2);
    
    void setObjectProviderAssociatedValue(final ObjectProvider p0, final Object p1, final Object p2);
    
    Object getObjectProviderAssociatedValue(final ObjectProvider p0, final Object p1);
    
    void removeObjectProviderAssociatedValue(final ObjectProvider p0, final Object p1);
    
    boolean containsObjectProviderAssociatedValue(final ObjectProvider p0, final Object p1);
    
    public static class EmbeddedOwnerRelation
    {
        protected ObjectProvider ownerOP;
        protected int ownerFieldNum;
        protected ObjectProvider embOP;
        
        public EmbeddedOwnerRelation(final ObjectProvider ownerOP, final int ownerFieldNum, final ObjectProvider embOP) {
            this.ownerOP = ownerOP;
            this.ownerFieldNum = ownerFieldNum;
            this.embOP = embOP;
        }
        
        public ObjectProvider getOwnerOP() {
            return this.ownerOP;
        }
        
        public ObjectProvider getEmbeddedOP() {
            return this.embOP;
        }
        
        public int getOwnerFieldNum() {
            return this.ownerFieldNum;
        }
    }
    
    public interface LifecycleListener
    {
        void preClose(final ExecutionContext p0);
    }
}
