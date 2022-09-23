// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.util.EnumSet;
import java.util.Set;
import java.util.Date;
import javax.jdo.listener.InstanceLifecycleListener;
import javax.jdo.datastore.JDOConnection;
import javax.jdo.datastore.Sequence;
import java.util.Collection;

public interface PersistenceManager
{
    boolean isClosed();
    
    void close();
    
    Transaction currentTransaction();
    
    void evict(final Object p0);
    
    void evictAll(final Object... p0);
    
    void evictAll(final Collection p0);
    
    void evictAll(final boolean p0, final Class p1);
    
    void evictAll();
    
    void refresh(final Object p0);
    
    void refreshAll(final Object... p0);
    
    void refreshAll(final Collection p0);
    
    void refreshAll();
    
    void refreshAll(final JDOException p0);
    
    Query newQuery();
    
    Query newQuery(final Object p0);
    
    Query newQuery(final String p0);
    
    Query newQuery(final String p0, final Object p1);
    
    Query newQuery(final Class p0);
    
    Query newQuery(final Extent p0);
    
    Query newQuery(final Class p0, final Collection p1);
    
    Query newQuery(final Class p0, final String p1);
    
    Query newQuery(final Class p0, final Collection p1, final String p2);
    
    Query newQuery(final Extent p0, final String p1);
    
    Query newNamedQuery(final Class p0, final String p1);
    
     <T> Extent<T> getExtent(final Class<T> p0, final boolean p1);
    
     <T> Extent<T> getExtent(final Class<T> p0);
    
    Object getObjectById(final Object p0, final boolean p1);
    
     <T> T getObjectById(final Class<T> p0, final Object p1);
    
    Object getObjectById(final Object p0);
    
    Object getObjectId(final Object p0);
    
    Object getTransactionalObjectId(final Object p0);
    
    Object newObjectIdInstance(final Class p0, final Object p1);
    
    Collection getObjectsById(final Collection p0, final boolean p1);
    
    Collection getObjectsById(final Collection p0);
    
    @Deprecated
    Object[] getObjectsById(final Object[] p0, final boolean p1);
    
    Object[] getObjectsById(final boolean p0, final Object... p1);
    
    Object[] getObjectsById(final Object... p0);
    
     <T> T makePersistent(final T p0);
    
     <T> T[] makePersistentAll(final T... p0);
    
     <T> Collection<T> makePersistentAll(final Collection<T> p0);
    
    void deletePersistent(final Object p0);
    
    void deletePersistentAll(final Object... p0);
    
    void deletePersistentAll(final Collection p0);
    
    void makeTransient(final Object p0);
    
    void makeTransientAll(final Object... p0);
    
    void makeTransientAll(final Collection p0);
    
    void makeTransient(final Object p0, final boolean p1);
    
    @Deprecated
    void makeTransientAll(final Object[] p0, final boolean p1);
    
    void makeTransientAll(final boolean p0, final Object... p1);
    
    void makeTransientAll(final Collection p0, final boolean p1);
    
    void makeTransactional(final Object p0);
    
    void makeTransactionalAll(final Object... p0);
    
    void makeTransactionalAll(final Collection p0);
    
    void makeNontransactional(final Object p0);
    
    void makeNontransactionalAll(final Object... p0);
    
    void makeNontransactionalAll(final Collection p0);
    
    void retrieve(final Object p0);
    
    void retrieve(final Object p0, final boolean p1);
    
    void retrieveAll(final Collection p0);
    
    void retrieveAll(final Collection p0, final boolean p1);
    
    void retrieveAll(final Object... p0);
    
    @Deprecated
    void retrieveAll(final Object[] p0, final boolean p1);
    
    void retrieveAll(final boolean p0, final Object... p1);
    
    void setUserObject(final Object p0);
    
    Object getUserObject();
    
    PersistenceManagerFactory getPersistenceManagerFactory();
    
    Class getObjectIdClass(final Class p0);
    
    void setMultithreaded(final boolean p0);
    
    boolean getMultithreaded();
    
    void setIgnoreCache(final boolean p0);
    
    boolean getIgnoreCache();
    
    void setDatastoreReadTimeoutMillis(final Integer p0);
    
    Integer getDatastoreReadTimeoutMillis();
    
    void setDatastoreWriteTimeoutMillis(final Integer p0);
    
    Integer getDatastoreWriteTimeoutMillis();
    
    boolean getDetachAllOnCommit();
    
    void setDetachAllOnCommit(final boolean p0);
    
    boolean getCopyOnAttach();
    
    void setCopyOnAttach(final boolean p0);
    
     <T> T detachCopy(final T p0);
    
     <T> Collection<T> detachCopyAll(final Collection<T> p0);
    
     <T> T[] detachCopyAll(final T... p0);
    
    Object putUserObject(final Object p0, final Object p1);
    
    Object getUserObject(final Object p0);
    
    Object removeUserObject(final Object p0);
    
    void flush();
    
    void checkConsistency();
    
    FetchPlan getFetchPlan();
    
     <T> T newInstance(final Class<T> p0);
    
    Sequence getSequence(final String p0);
    
    JDOConnection getDataStoreConnection();
    
    void addInstanceLifecycleListener(final InstanceLifecycleListener p0, final Class... p1);
    
    void removeInstanceLifecycleListener(final InstanceLifecycleListener p0);
    
    Date getServerDate();
    
    Set getManagedObjects();
    
    Set getManagedObjects(final EnumSet<ObjectState> p0);
    
    Set getManagedObjects(final Class... p0);
    
    Set getManagedObjects(final EnumSet<ObjectState> p0, final Class... p1);
    
    FetchGroup getFetchGroup(final Class p0, final String p1);
}
