// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.Transaction;
import org.datanucleus.FetchPlan;
import java.util.Set;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.cache.CachedPC;
import org.datanucleus.store.FieldValues;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;

public interface ObjectProvider
{
    public static final String ORIGINAL_FIELD_VALUE_KEY_PREFIX = "FIELD_VALUE.ORIGINAL.";
    public static final short PC = 0;
    public static final short EMBEDDED_PC = 1;
    public static final short EMBEDDED_COLLECTION_ELEMENT_PC = 2;
    public static final short EMBEDDED_MAP_KEY_PC = 3;
    public static final short EMBEDDED_MAP_VALUE_PC = 4;
    
    void connect(final ExecutionContext p0, final AbstractClassMetaData p1);
    
    void disconnect();
    
    void initialiseForHollow(final Object p0, final FieldValues p1, final Class p2);
    
    @Deprecated
    void initialiseForHollowAppId(final FieldValues p0, final Class p1);
    
    void initialiseForHollowPreConstructed(final Object p0, final Object p1);
    
    void initialiseForPersistentClean(final Object p0, final Object p1);
    
    void initialiseForEmbedded(final Object p0, final boolean p1);
    
    void initialiseForPersistentNew(final Object p0, final FieldValues p1);
    
    void initialiseForTransactionalTransient(final Object p0);
    
    void initialiseForDetached(final Object p0, final Object p1, final Object p2);
    
    void initialiseForPNewToBeDeleted(final Object p0);
    
    void initialiseForCachedPC(final CachedPC p0, final Object p1);
    
    AbstractClassMetaData getClassMetaData();
    
    ExecutionContext getExecutionContext();
    
    Object getObject();
    
    String getObjectAsPrintable();
    
    Object getInternalObjectId();
    
    Object getExternalObjectId();
    
    LifeCycleState getLifecycleState();
    
    void replaceField(final int p0, final Object p1);
    
    void replaceFieldMakeDirty(final int p0, final Object p1);
    
    void replaceFieldValue(final int p0, final Object p1);
    
    void replaceFields(final int[] p0, final FieldManager p1);
    
    void replaceFields(final int[] p0, final FieldManager p1, final boolean p2);
    
    void replaceNonLoadedFields(final int[] p0, final FieldManager p1);
    
    void replaceAllLoadedSCOFieldsWithWrappers();
    
    void replaceAllLoadedSCOFieldsWithValues();
    
    void provideFields(final int[] p0, final FieldManager p1);
    
    Object provideField(final int p0);
    
    Object wrapSCOField(final int p0, final Object p1, final boolean p2, final boolean p3, final boolean p4);
    
    Object unwrapSCOField(final int p0, final Object p1, final boolean p2);
    
    void setAssociatedValue(final Object p0, final Object p1);
    
    Object getAssociatedValue(final Object p0);
    
    void removeAssociatedValue(final Object p0);
    
    int[] getDirtyFieldNumbers();
    
    String[] getDirtyFieldNames();
    
    boolean[] getDirtyFields();
    
    void makeDirty(final int p0);
    
    ObjectProvider[] getEmbeddedOwners();
    
    boolean isEmbedded();
    
    void copyFieldsFromObject(final Object p0, final int[] p1);
    
    void runReachability(final Set p0);
    
    void setPcObjectType(final short p0);
    
    void setStoringPC();
    
    void unsetStoringPC();
    
    boolean isFlushedToDatastore();
    
    boolean isFlushedNew();
    
    void setFlushedNew(final boolean p0);
    
    void flush();
    
    void setFlushing(final boolean p0);
    
    void markAsFlushed();
    
    void locate();
    
    boolean isWaitingToBeFlushedToDatastore();
    
    void changeActivityState(final ActivityState p0);
    
    boolean isInserting();
    
    boolean isDeleting();
    
    boolean becomingDeleted();
    
    void loadFieldValues(final FieldValues p0);
    
    Object getReferencedPC();
    
    void loadField(final int p0);
    
    void loadFieldsInFetchPlan(final FetchPlanState p0);
    
    void loadFieldFromDatastore(final int p0);
    
    void loadUnloadedFieldsInFetchPlan();
    
    void loadUnloadedFieldsOfClassInFetchPlan(final FetchPlan p0);
    
    void loadUnloadedFields();
    
    void unloadNonFetchPlanFields();
    
    void refreshLoadedFields();
    
    void clearSavedFields();
    
    void refreshFieldsInFetchPlan();
    
    void clearNonPrimaryKeyFields();
    
    void restoreFields();
    
    void saveFields();
    
    void clearFields();
    
    void registerTransactional();
    
    boolean isRestoreValues();
    
    void clearLoadedFlags();
    
    void unloadField(final String p0);
    
    void nullifyFields();
    
    boolean[] getLoadedFields();
    
    int[] getLoadedFieldNumbers();
    
    String[] getLoadedFieldNames();
    
    boolean isLoaded(final int p0);
    
    boolean getAllFieldsLoaded();
    
    boolean isFieldLoaded(final int p0);
    
    void updateFieldAfterInsert(final Object p0, final int p1);
    
    void setPostStoreNewObjectId(final Object p0);
    
    void replaceManagedPC(final Object p0);
    
    void setTransactionalVersion(final Object p0);
    
    Object getTransactionalVersion();
    
    void setVersion(final Object p0);
    
    Object getVersion();
    
    void lock(final short p0);
    
    void unlock();
    
    short getLockMode();
    
    void evictFromTransaction();
    
    void enlistInTransaction();
    
    void makeTransactional();
    
    void makeNontransactional();
    
    void makeTransient(final FetchPlanState p0);
    
    void makePersistent();
    
    void makePersistentTransactionalTransient();
    
    void deletePersistent();
    
    Object attachCopy(final Object p0, final boolean p1);
    
    void attach(final boolean p0);
    
    void attach(final Object p0);
    
    Object detachCopy(final FetchPlanState p0);
    
    void detach(final FetchPlanState p0);
    
    void validate();
    
    void markForInheritanceValidation();
    
    void evict();
    
    void refresh();
    
    void retrieve(final boolean p0);
    
    void preBegin(final Transaction p0);
    
    void postCommit(final Transaction p0);
    
    void preRollback(final Transaction p0);
    
    void resetDetachState();
    
    void retrieveDetachState(final ObjectProvider p0);
    
    @Deprecated
    void checkInheritance(final FieldValues p0);
}
