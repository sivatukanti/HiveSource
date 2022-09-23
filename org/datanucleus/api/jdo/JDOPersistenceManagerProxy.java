// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.Map;
import javax.jdo.JDOException;
import javax.jdo.Query;
import java.util.Date;
import javax.jdo.datastore.Sequence;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.ObjectState;
import java.util.EnumSet;
import java.util.Set;
import javax.jdo.FetchPlan;
import javax.jdo.FetchGroup;
import javax.jdo.Extent;
import javax.jdo.datastore.JDOConnection;
import java.util.Collection;
import javax.jdo.Transaction;
import javax.jdo.listener.InstanceLifecycleListener;
import javax.jdo.PersistenceManager;

public class JDOPersistenceManagerProxy implements PersistenceManager
{
    protected JDOPersistenceManagerFactory pmf;
    
    public JDOPersistenceManagerProxy(final JDOPersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }
    
    protected PersistenceManager getPM() {
        return this.pmf.getPMProxyDelegate();
    }
    
    public void close() {
        this.pmf.clearPMProxyDelegate();
    }
    
    public void addInstanceLifecycleListener(final InstanceLifecycleListener listener, final Class... classes) {
        this.getPM().addInstanceLifecycleListener(listener, classes);
    }
    
    public void checkConsistency() {
        this.getPM().checkConsistency();
    }
    
    public Transaction currentTransaction() {
        return this.getPM().currentTransaction();
    }
    
    public void deletePersistent(final Object obj) {
        this.getPM().deletePersistent(obj);
    }
    
    public void deletePersistentAll(final Object... pcs) {
        this.getPM().deletePersistentAll(pcs);
    }
    
    public void deletePersistentAll(final Collection pcs) {
        this.getPM().deletePersistentAll(pcs);
    }
    
    public <T> T detachCopy(final T pc) {
        return this.getPM().detachCopy(pc);
    }
    
    public <T> Collection<T> detachCopyAll(final Collection<T> pcs) {
        return this.getPM().detachCopyAll(pcs);
    }
    
    public <T> T[] detachCopyAll(final T... pcs) {
        return this.getPM().detachCopyAll(pcs);
    }
    
    public void evict(final Object obj) {
        this.getPM().evict(obj);
    }
    
    public void evictAll() {
        this.getPM().evictAll();
    }
    
    public void evictAll(final Object... pcs) {
        this.getPM().evictAll(pcs);
    }
    
    public void evictAll(final Collection pcs) {
        this.getPM().evictAll(pcs);
    }
    
    public void evictAll(final boolean subclasses, final Class cls) {
        this.getPM().evictAll(subclasses, cls);
    }
    
    public void flush() {
        this.getPM().flush();
    }
    
    public boolean getCopyOnAttach() {
        return this.getPM().getCopyOnAttach();
    }
    
    public JDOConnection getDataStoreConnection() {
        return this.getPM().getDataStoreConnection();
    }
    
    public boolean getDetachAllOnCommit() {
        return this.getPM().getDetachAllOnCommit();
    }
    
    public <T> Extent<T> getExtent(final Class<T> pcClass) {
        return this.getPM().getExtent(pcClass);
    }
    
    public <T> Extent<T> getExtent(final Class<T> pcClass, final boolean subclasses) {
        return this.getPM().getExtent(pcClass, subclasses);
    }
    
    public FetchGroup getFetchGroup(final Class arg0, final String arg1) {
        return this.getPM().getFetchGroup(arg0, arg1);
    }
    
    public FetchPlan getFetchPlan() {
        return this.getPM().getFetchPlan();
    }
    
    public boolean getIgnoreCache() {
        return this.getPM().getIgnoreCache();
    }
    
    public Set getManagedObjects() {
        return this.getPM().getManagedObjects();
    }
    
    public Set getManagedObjects(final EnumSet<ObjectState> states) {
        return this.getPM().getManagedObjects(states);
    }
    
    public Set getManagedObjects(final Class... classes) {
        return this.getPM().getManagedObjects(classes);
    }
    
    public Set getManagedObjects(final EnumSet<ObjectState> states, final Class... classes) {
        return this.getPM().getManagedObjects(states, classes);
    }
    
    public boolean getMultithreaded() {
        return this.getPM().getMultithreaded();
    }
    
    public Object getObjectById(final Object id) {
        return this.getPM().getObjectById(id);
    }
    
    public Object getObjectById(final Object id, final boolean validate) {
        return this.getPM().getObjectById(id, validate);
    }
    
    public <T> T getObjectById(final Class<T> cls, final Object key) {
        return this.getPM().getObjectById(cls, key);
    }
    
    public Object getObjectId(final Object pc) {
        return this.getPM().getObjectId(pc);
    }
    
    public Class getObjectIdClass(final Class cls) {
        return this.getPM().getObjectIdClass(cls);
    }
    
    public Collection getObjectsById(final Collection oids) {
        return this.getPM().getObjectsById(oids);
    }
    
    public Object[] getObjectsById(final Object... oids) {
        return this.getPM().getObjectsById(oids);
    }
    
    public Collection getObjectsById(final Collection oids, final boolean validate) {
        return this.getPM().getObjectsById(oids, validate);
    }
    
    public Object[] getObjectsById(final Object[] oids, final boolean validate) {
        return this.getPM().getObjectsById(validate, oids);
    }
    
    public Object[] getObjectsById(final boolean validate, final Object... oids) {
        return this.getPM().getObjectsById(validate, oids);
    }
    
    public PersistenceManagerFactory getPersistenceManagerFactory() {
        return this.getPM().getPersistenceManagerFactory();
    }
    
    public Sequence getSequence(final String sequenceName) {
        return this.getPM().getSequence(sequenceName);
    }
    
    public Date getServerDate() {
        return this.getPM().getServerDate();
    }
    
    public Object getTransactionalObjectId(final Object pc) {
        return this.getPM().getTransactionalObjectId(pc);
    }
    
    public Object getUserObject() {
        return this.getPM().getUserObject();
    }
    
    public Object getUserObject(final Object key) {
        return this.getPM().getUserObject(key);
    }
    
    public boolean isClosed() {
        return this.getPM().isClosed();
    }
    
    public void makeNontransactional(final Object pc) {
        this.getPM().makeNontransactional(pc);
    }
    
    public void makeNontransactionalAll(final Object... pcs) {
        this.getPM().makeNontransactionalAll(pcs);
    }
    
    public void makeNontransactionalAll(final Collection arg0) {
        this.getPM().makeNontransactionalAll(arg0);
    }
    
    public <T> T makePersistent(final T obj) {
        return this.getPM().makePersistent(obj);
    }
    
    public <T> T[] makePersistentAll(final T... arg0) {
        return this.getPM().makePersistentAll(arg0);
    }
    
    public <T> Collection<T> makePersistentAll(final Collection<T> arg0) {
        return this.getPM().makePersistentAll(arg0);
    }
    
    public void makeTransactional(final Object arg0) {
        this.getPM().makeTransactional(arg0);
    }
    
    public void makeTransactionalAll(final Object... arg0) {
        this.getPM().makeTransactionalAll(arg0);
    }
    
    public void makeTransactionalAll(final Collection arg0) {
        this.getPM().makeTransactionalAll(arg0);
    }
    
    public void makeTransient(final Object pc) {
        this.getPM().makeTransient(pc);
    }
    
    public void makeTransient(final Object pc, final boolean useFetchPlan) {
        this.getPM().makeTransient(pc, useFetchPlan);
    }
    
    public void makeTransientAll(final Object... pcs) {
        this.getPM().makeTransientAll(pcs);
    }
    
    public void makeTransientAll(final Collection pcs) {
        this.getPM().makeTransientAll(pcs);
    }
    
    public void makeTransientAll(final Object[] pcs, final boolean includeFetchPlan) {
        this.getPM().makeTransientAll(includeFetchPlan, pcs);
    }
    
    public void makeTransientAll(final boolean includeFetchPlan, final Object... pcs) {
        this.getPM().makeTransientAll(includeFetchPlan, pcs);
    }
    
    public void makeTransientAll(final Collection pcs, final boolean useFetchPlan) {
        this.getPM().makeTransientAll(pcs, useFetchPlan);
    }
    
    public <T> T newInstance(final Class<T> pc) {
        return this.getPM().newInstance(pc);
    }
    
    public Query newNamedQuery(final Class cls, final String filter) {
        return this.getPM().newNamedQuery(cls, filter);
    }
    
    public Object newObjectIdInstance(final Class pcClass, final Object key) {
        return this.getPM().newObjectIdInstance(pcClass, key);
    }
    
    public Query newQuery() {
        return this.getPM().newQuery();
    }
    
    public Query newQuery(final Object obj) {
        return this.getPM().newQuery(obj);
    }
    
    public Query newQuery(final String query) {
        return this.getPM().newQuery(query);
    }
    
    public Query newQuery(final Class cls) {
        return this.getPM().newQuery(cls);
    }
    
    public Query newQuery(final Extent cln) {
        return this.getPM().newQuery(cln);
    }
    
    public Query newQuery(final String language, final Object query) {
        return this.getPM().newQuery(language, query);
    }
    
    public Query newQuery(final Class cls, final Collection cln) {
        return this.getPM().newQuery(cls, cln);
    }
    
    public Query newQuery(final Class cls, final String filter) {
        return this.getPM().newQuery(cls, filter);
    }
    
    public Query newQuery(final Extent cln, final String filter) {
        return this.getPM().newQuery(cln, filter);
    }
    
    public Query newQuery(final Class cls, final Collection cln, final String filter) {
        return this.getPM().newQuery(cls, cln, filter);
    }
    
    public Object putUserObject(final Object key, final Object value) {
        return this.getPM().putUserObject(key, value);
    }
    
    public void refresh(final Object obj) {
        this.getPM().refresh(obj);
    }
    
    public void refreshAll() {
        this.getPM().refreshAll();
    }
    
    public void refreshAll(final Object... pcs) {
        this.getPM().refreshAll(pcs);
    }
    
    public void refreshAll(final Collection pcs) {
        this.getPM().refreshAll(pcs);
    }
    
    public void refreshAll(final JDOException exc) {
        this.getPM().refreshAll(exc);
    }
    
    public void removeInstanceLifecycleListener(final InstanceLifecycleListener listener) {
        this.getPM().removeInstanceLifecycleListener(listener);
    }
    
    public Object removeUserObject(final Object key) {
        return this.getPM().removeUserObject(key);
    }
    
    public void retrieve(final Object pc) {
        this.getPM().retrieve(pc);
    }
    
    public void retrieve(final Object pc, final boolean fgOnly) {
        this.getPM().retrieve(pc, fgOnly);
    }
    
    public void retrieveAll(final Collection pcs) {
        this.getPM().retrieveAll(pcs);
    }
    
    public void retrieveAll(final Object... pcs) {
        this.getPM().retrieveAll(pcs);
    }
    
    public void retrieveAll(final Collection pcs, final boolean fgOnly) {
        this.getPM().retrieveAll(pcs, fgOnly);
    }
    
    public void retrieveAll(final Object[] pcs, final boolean fgOnly) {
        this.getPM().retrieveAll(fgOnly, pcs);
    }
    
    public void retrieveAll(final boolean fgOnly, final Object... pcs) {
        this.getPM().retrieveAll(fgOnly, pcs);
    }
    
    public void setCopyOnAttach(final boolean flag) {
        this.getPM().setCopyOnAttach(flag);
    }
    
    public void setDetachAllOnCommit(final boolean flag) {
        this.getPM().setDetachAllOnCommit(flag);
    }
    
    public void setIgnoreCache(final boolean flag) {
        this.getPM().setIgnoreCache(flag);
    }
    
    public void setMultithreaded(final boolean flag) {
        this.getPM().setMultithreaded(flag);
    }
    
    public void setUserObject(final Object userObject) {
        this.getPM().setUserObject(userObject);
    }
    
    public Integer getDatastoreReadTimeoutMillis() {
        return this.getPM().getDatastoreReadTimeoutMillis();
    }
    
    public void setDatastoreReadTimeoutMillis(final Integer intvl) {
        this.getPM().setDatastoreReadTimeoutMillis(intvl);
    }
    
    public Integer getDatastoreWriteTimeoutMillis() {
        return this.getPM().getDatastoreWriteTimeoutMillis();
    }
    
    public void setDatastoreWriteTimeoutMillis(final Integer intvl) {
        this.getPM().setDatastoreWriteTimeoutMillis(intvl);
    }
    
    public Map<String, Object> getProperties() {
        return (Map<String, Object>)this.getPM().getProperties();
    }
    
    public Set<String> getSupportedProperties() {
        return (Set<String>)this.getPM().getSupportedProperties();
    }
    
    public void setProperty(final String arg0, final Object arg1) {
        this.getPM().setProperty(arg0, arg1);
    }
}
