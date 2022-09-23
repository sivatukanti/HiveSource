// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import org.datanucleus.ClassConstants;
import javax.jdo.PersistenceManagerFactory;
import org.datanucleus.TransactionEventListener;
import org.datanucleus.PersistenceConfiguration;
import java.util.HashSet;
import javax.jdo.FetchGroup;
import org.datanucleus.store.NucleusConnection;
import javax.jdo.datastore.JDOConnection;
import org.datanucleus.api.jdo.exceptions.TransactionNotWritableException;
import org.datanucleus.api.jdo.exceptions.TransactionNotActiveException;
import javax.jdo.JDOFatalUserException;
import javax.jdo.listener.InstanceLifecycleListener;
import org.datanucleus.store.NucleusSequence;
import java.lang.reflect.Method;
import org.datanucleus.metadata.SequenceMetaData;
import javax.jdo.datastore.Sequence;
import javax.jdo.JDOOptimisticVerificationException;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.identity.SCOID;
import org.datanucleus.metadata.IdentityType;
import java.util.Collections;
import javax.jdo.JDONullIdentityException;
import javax.jdo.ObjectState;
import java.util.EnumSet;
import org.datanucleus.api.jdo.query.JDOTypesafeQuery;
import org.datanucleus.query.typesafe.TypesafeQuery;
import org.datanucleus.metadata.FetchGroupMetaData;
import org.datanucleus.metadata.FetchPlanMetaData;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.metadata.QueryLanguage;
import javax.jdo.Extent;
import org.datanucleus.util.StringUtils;
import javax.jdo.Query;
import java.util.List;
import org.datanucleus.state.DetachState;
import javax.jdo.spi.PersistenceCapable;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Iterator;
import javax.jdo.JDOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Date;
import javax.jdo.JDOUnsupportedOptionException;
import javax.jdo.FetchPlan;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.TransactionActiveOnCloseException;
import javax.jdo.JDOUserException;
import org.datanucleus.state.CallbackHandler;
import java.util.HashMap;
import java.util.Set;
import javax.jdo.Transaction;
import org.datanucleus.ExecutionContext;
import java.util.Map;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.Localiser;
import javax.jdo.PersistenceManager;

public class JDOPersistenceManager implements PersistenceManager
{
    protected static final Localiser LOCALISER;
    protected static final Localiser LOCALISER_JDO;
    public static final NucleusLogger LOGGER;
    private boolean closed;
    private Map userObjectMap;
    private Object userObject;
    protected ExecutionContext ec;
    protected Transaction jdotx;
    protected JDOPersistenceManagerFactory pmf;
    protected JDOFetchPlan fetchPlan;
    private Set<JDOFetchGroup> jdoFetchGroups;
    
    public JDOPersistenceManager(final JDOPersistenceManagerFactory apmf, final String userName, final String password) {
        this.closed = false;
        this.userObjectMap = null;
        this.userObject = null;
        this.fetchPlan = null;
        this.jdoFetchGroups = null;
        final Map<String, Object> options = new HashMap<String, Object>();
        options.put("user", userName);
        options.put("password", password);
        this.ec = apmf.getNucleusContext().getExecutionContext(this, options);
        this.pmf = apmf;
        this.fetchPlan = new JDOFetchPlan(this.ec.getFetchPlan());
        this.jdotx = new JDOTransaction(this, this.ec.getTransaction());
        final CallbackHandler beanValidator = apmf.getNucleusContext().getValidationHandler(this.ec);
        if (beanValidator != null) {
            this.ec.getCallbackHandler().setValidationListener(beanValidator);
        }
    }
    
    protected void internalClose() {
        if (this.closed) {
            return;
        }
        try {
            this.ec.close();
        }
        catch (TransactionActiveOnCloseException tae) {
            throw new JDOUserException(tae.getMessage(), this);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
        this.userObject = null;
        this.userObjectMap = null;
        this.closed = true;
    }
    
    public ExecutionContext getExecutionContext() {
        return this.ec;
    }
    
    public JDOPersistenceManagerFactory getPersistenceManagerFactory() {
        return this.pmf;
    }
    
    public boolean getDetachAllOnCommit() {
        this.assertIsOpen();
        return this.ec.getBooleanProperty("datanucleus.DetachAllOnCommit");
    }
    
    public boolean getCopyOnAttach() {
        this.assertIsOpen();
        return this.ec.getBooleanProperty("datanucleus.CopyOnAttach");
    }
    
    public FetchPlan getFetchPlan() {
        return this.fetchPlan;
    }
    
    public boolean getIgnoreCache() {
        this.assertIsOpen();
        return this.ec.getBooleanProperty("datanucleus.IgnoreCache");
    }
    
    public boolean getMultithreaded() {
        this.assertIsOpen();
        return this.ec.getBooleanProperty("datanucleus.Multithreaded");
    }
    
    public void setDetachAllOnCommit(final boolean flag) {
        this.assertIsOpen();
        this.ec.setProperty("datanucleus.DetachAllOnCommit", flag);
    }
    
    public void setCopyOnAttach(final boolean flag) {
        this.assertIsOpen();
        this.ec.setProperty("datanucleus.CopyOnAttach", flag);
    }
    
    public void setIgnoreCache(final boolean flag) {
        this.assertIsOpen();
        this.ec.setProperty("datanucleus.IgnoreCache", flag);
    }
    
    public void setMultithreaded(final boolean flag) {
        this.assertIsOpen();
        this.ec.setProperty("datanucleus.Multithreaded", flag);
    }
    
    public void setDatastoreReadTimeoutMillis(final Integer timeout) {
        this.assertIsOpen();
        if (!this.ec.getStoreManager().getSupportedOptions().contains("Datastore.Timeout")) {
            throw new JDOUnsupportedOptionException("This datastore doesn't support read timeouts");
        }
        this.ec.setProperty("datanucleus.datastoreReadTimeout", timeout);
    }
    
    public Integer getDatastoreReadTimeoutMillis() {
        this.assertIsOpen();
        return this.ec.getIntProperty("datanucleus.datastoreReadTimeout");
    }
    
    public void setDatastoreWriteTimeoutMillis(final Integer timeout) {
        this.assertIsOpen();
        if (!this.ec.getStoreManager().getSupportedOptions().contains("Datastore.Timeout")) {
            throw new JDOUnsupportedOptionException("This datastore doesn't support write timeouts");
        }
        this.ec.setProperty("datanucleus.datastoreWriteTimeout", timeout);
    }
    
    public Integer getDatastoreWriteTimeoutMillis() {
        this.assertIsOpen();
        return this.ec.getIntProperty("datanucleus.datastoreWriteTimeout");
    }
    
    public Date getServerDate() {
        this.assertIsOpen();
        try {
            return this.ec.getStoreManager().getDatastoreDate();
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void close() {
        this.pmf.releasePersistenceManager(this);
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public Transaction currentTransaction() {
        this.assertIsOpen();
        return this.jdotx;
    }
    
    private void jdoEvict(final Object obj) {
        try {
            this.ec.evictObject(obj);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void evict(final Object obj) {
        this.assertIsOpen();
        this.jdoEvict(obj);
    }
    
    public void evictAll(final boolean subclasses, final Class cls) {
        this.assertIsOpen();
        try {
            this.ec.evictObjects(cls, subclasses);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void evictAll(final Object... pcs) {
        this.evictAll(Arrays.asList(pcs));
    }
    
    public void evictAll(final Collection pcs) {
        this.assertIsOpen();
        final ArrayList failures = new ArrayList();
        final Iterator i = pcs.iterator();
        while (i.hasNext()) {
            try {
                this.jdoEvict(i.next());
            }
            catch (JDOException e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010036"), failures.toArray(new Exception[failures.size()]));
        }
    }
    
    public void evictAll() {
        this.assertIsOpen();
        this.ec.evictAllObjects();
    }
    
    private void jdoRefresh(final Object obj) {
        try {
            this.ec.refreshObject(obj);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void refresh(final Object obj) {
        this.assertIsOpen();
        this.jdoRefresh(obj);
    }
    
    public void refreshAll(final Object... pcs) {
        this.refreshAll(Arrays.asList(pcs));
    }
    
    public void refreshAll(final Collection pcs) {
        this.assertIsOpen();
        final ArrayList failures = new ArrayList();
        final Iterator iter = pcs.iterator();
        while (iter.hasNext()) {
            try {
                this.jdoRefresh(iter.next());
            }
            catch (JDOException e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010037"), failures.toArray(new Exception[failures.size()]));
        }
    }
    
    public void refreshAll() {
        this.assertIsOpen();
        try {
            this.ec.refreshAllObjects();
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void refreshAll(final JDOException exc) {
        final Object obj = exc.getFailedObject();
        if (obj != null) {
            this.refresh(obj);
        }
        final Throwable[] nested_excs = exc.getNestedExceptions();
        if (nested_excs != null) {
            for (int i = 0; i < nested_excs.length; ++i) {
                if (nested_excs[i] instanceof JDOException) {
                    this.refreshAll((JDOException)nested_excs[i]);
                }
            }
        }
    }
    
    private void jdoRetrieve(final Object obj, final boolean useFetchPlan) {
        try {
            this.ec.retrieveObject(obj, useFetchPlan);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void retrieve(final Object pc, final boolean useFetchPlan) {
        this.assertIsOpen();
        this.jdoRetrieve(pc, useFetchPlan);
    }
    
    public void retrieve(final Object pc) {
        this.retrieve(pc, false);
    }
    
    public void retrieveAll(final Object... pcs) {
        this.retrieveAll(Arrays.asList(pcs), false);
    }
    
    @Deprecated
    public void retrieveAll(final Object[] pcs, final boolean useFetchPlan) {
        this.retrieveAll(Arrays.asList(pcs), useFetchPlan);
    }
    
    public void retrieveAll(final boolean useFetchPlan, final Object... pcs) {
        this.retrieveAll(Arrays.asList(pcs), useFetchPlan);
    }
    
    public void retrieveAll(final Collection pcs, final boolean useFetchPlan) {
        this.assertIsOpen();
        final ArrayList failures = new ArrayList();
        final Iterator i = pcs.iterator();
        while (i.hasNext()) {
            try {
                this.jdoRetrieve(i.next(), useFetchPlan);
            }
            catch (RuntimeException e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010038"), failures.toArray(new Exception[failures.size()]));
        }
    }
    
    public void retrieveAll(final Collection pcs) {
        this.retrieveAll(pcs, false);
    }
    
    private Object jdoMakePersistent(final Object obj) {
        try {
            return this.ec.persistObject(obj, false);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Object makePersistent(final Object obj) {
        this.assertIsOpen();
        this.assertWritable();
        if (obj == null) {
            return null;
        }
        return this.jdoMakePersistent(obj);
    }
    
    public Object[] makePersistentAll(final Object... pcs) {
        return this.makePersistentAll(Arrays.asList(pcs)).toArray();
    }
    
    public Collection makePersistentAll(final Collection pcs) {
        this.assertIsOpen();
        this.assertWritable();
        try {
            final Object[] persistedPcs = this.ec.persistObjects(pcs.toArray());
            final Collection persisted = new ArrayList();
            for (int i = 0; i < persistedPcs.length; ++i) {
                persisted.add(persistedPcs[i]);
            }
            return persisted;
        }
        catch (NucleusUserException nue) {
            final Throwable[] failures = nue.getNestedExceptions();
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010039"), failures);
        }
    }
    
    private void jdoDeletePersistent(final Object obj) {
        try {
            this.ec.deleteObject(obj);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void deletePersistent(final Object obj) {
        this.assertIsOpen();
        this.assertWritable();
        this.jdoDeletePersistent(obj);
    }
    
    public void deletePersistentAll(final Object... pcs) {
        this.deletePersistentAll(Arrays.asList(pcs));
    }
    
    public void deletePersistentAll(final Collection pcs) {
        this.assertIsOpen();
        this.assertWritable();
        try {
            this.ec.deleteObjects(pcs.toArray());
        }
        catch (NucleusUserException nue) {
            final Throwable[] failures = nue.getNestedExceptions();
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010040"), failures);
        }
    }
    
    private void jdoMakeTransient(final Object pc, final FetchPlanState state) {
        try {
            this.ec.makeObjectTransient(pc, state);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void makeTransient(final Object pc, final boolean useFetchPlan) {
        this.assertIsOpen();
        FetchPlanState state = null;
        if (useFetchPlan) {
            state = new FetchPlanState();
        }
        this.jdoMakeTransient(pc, state);
    }
    
    public void makeTransient(final Object pc) {
        this.makeTransient(pc, false);
    }
    
    public void makeTransientAll(final Object... pcs) {
        this.makeTransientAll(Arrays.asList(pcs));
    }
    
    public void makeTransientAll(final Object[] pcs, final boolean includeFetchPlan) {
        this.makeTransientAll(Arrays.asList(pcs), includeFetchPlan);
    }
    
    public void makeTransientAll(final boolean includeFetchPlan, final Object... pcs) {
        this.makeTransientAll(Arrays.asList(pcs), includeFetchPlan);
    }
    
    public void makeTransientAll(final Collection pcs, final boolean useFetchPlan) {
        this.assertIsOpen();
        final ArrayList failures = new ArrayList();
        final Iterator i = pcs.iterator();
        FetchPlanState state = null;
        if (useFetchPlan) {
            state = new FetchPlanState();
        }
        while (i.hasNext()) {
            try {
                this.jdoMakeTransient(i.next(), state);
            }
            catch (RuntimeException e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010041"), failures.toArray(new Exception[failures.size()]));
        }
    }
    
    public void makeTransientAll(final Collection pcs) {
        this.makeTransientAll(pcs, false);
    }
    
    private void jdoMakeTransactional(final Object pc) {
        try {
            this.ec.makeObjectTransactional(pc);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void makeTransactional(final Object pc) {
        this.assertIsOpen();
        this.jdoMakeTransactional(pc);
    }
    
    public void makeTransactionalAll(final Object... pcs) {
        this.makeTransactionalAll(Arrays.asList(pcs));
    }
    
    public void makeTransactionalAll(final Collection pcs) {
        this.assertIsOpen();
        this.assertActiveTransaction();
        final ArrayList failures = new ArrayList();
        final Iterator i = pcs.iterator();
        while (i.hasNext()) {
            try {
                this.jdoMakeTransactional(i.next());
            }
            catch (RuntimeException e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010042"), failures.toArray(new Exception[failures.size()]));
        }
    }
    
    private void jdoMakeNontransactional(final Object obj) {
        try {
            this.ec.makeObjectNontransactional(obj);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void makeNontransactional(final Object pc) {
        this.assertIsOpen();
        if (pc == null) {
            return;
        }
        if (!((PersistenceCapable)pc).jdoIsTransactional() && !((PersistenceCapable)pc).jdoIsPersistent()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011004"));
        }
        if (!((PersistenceCapable)pc).jdoIsTransactional() && ((PersistenceCapable)pc).jdoIsPersistent()) {
            return;
        }
        this.jdoMakeNontransactional(pc);
    }
    
    public void makeNontransactionalAll(final Object... pcs) {
        this.makeNontransactionalAll(Arrays.asList(pcs));
    }
    
    public void makeNontransactionalAll(final Collection pcs) {
        this.assertIsOpen();
        this.assertActiveTransaction();
        final ArrayList failures = new ArrayList();
        final Iterator i = pcs.iterator();
        while (i.hasNext()) {
            try {
                this.jdoMakeNontransactional(i.next());
            }
            catch (RuntimeException e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("010043"), failures.toArray(new Exception[failures.size()]));
        }
    }
    
    private Object jdoDetachCopy(final Object obj, final FetchPlanState state) {
        this.ec.assertClassPersistable(obj.getClass());
        try {
            return this.ec.detachObjectCopy(obj, state);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Object detachCopy(final Object pc) {
        this.assertIsOpen();
        if (pc == null) {
            return null;
        }
        try {
            this.ec.assertClassPersistable(pc.getClass());
            this.assertReadable("detachCopy");
            return this.jdoDetachCopy(pc, new DetachState(this.ec.getApiAdapter()));
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Object[] detachCopyAll(final Object... pcs) {
        return this.detachCopyAll(Arrays.asList(pcs)).toArray();
    }
    
    public Collection detachCopyAll(final Collection pcs) {
        this.assertIsOpen();
        this.assertReadable("detachCopyAll");
        final FetchPlanState state = new DetachState(this.ec.getApiAdapter());
        final List detacheds = new ArrayList();
        for (final Object obj : pcs) {
            if (obj == null) {
                detacheds.add(null);
            }
            else {
                detacheds.add(this.jdoDetachCopy(obj, state));
            }
        }
        return detacheds;
    }
    
    public Query newQuery() {
        return this.newQuery("javax.jdo.query.JDOQL", null);
    }
    
    public Query newQuery(final Object obj) {
        if (obj != null && obj instanceof JDOQuery) {
            final String language = ((JDOQuery)obj).getLanguage();
            return this.newQuery(language, obj);
        }
        return this.newQuery(null, obj);
    }
    
    public Query newQuery(final String query) {
        return this.newQuery("javax.jdo.query.JDOQL", query);
    }
    
    public Query newQuery(final String language, final Object query) {
        this.assertIsOpen();
        String queryLanguage = language;
        if (queryLanguage == null) {
            queryLanguage = "JDOQL";
        }
        else if (queryLanguage.equals("javax.jdo.query.JDOQL")) {
            queryLanguage = "JDOQL";
        }
        else if (queryLanguage.equals("javax.jdo.query.SQL")) {
            queryLanguage = "SQL";
        }
        else if (queryLanguage.equals("javax.jdo.query.JPQL")) {
            queryLanguage = "JPQL";
        }
        if (!this.ec.getStoreManager().supportsQueryLanguage(queryLanguage)) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011006", queryLanguage));
        }
        org.datanucleus.store.query.Query internalQuery = null;
        try {
            if (query != null && query instanceof JDOQuery) {
                internalQuery = this.ec.getStoreManager().getQueryManager().newQuery(queryLanguage, this.ec, ((JDOQuery)query).getInternalQuery());
            }
            else if (query instanceof String && StringUtils.isWhitespace((String)query)) {
                internalQuery = this.ec.getStoreManager().getQueryManager().newQuery(queryLanguage, this.ec, null);
            }
            else {
                internalQuery = this.ec.getStoreManager().getQueryManager().newQuery(queryLanguage, this.ec, query);
            }
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
        return new JDOQuery(this, internalQuery, queryLanguage);
    }
    
    public Query newQuery(final Class cls) {
        final Query query = this.newQuery();
        query.setClass(cls);
        return query;
    }
    
    public Query newQuery(final Extent cln) {
        final Query query = this.newQuery();
        query.setClass(cln.getCandidateClass());
        query.setCandidates(cln);
        return query;
    }
    
    public Query newQuery(final Class cls, final Collection cln) {
        final Query query = this.newQuery();
        query.setClass(cls);
        query.setCandidates(cln);
        return query;
    }
    
    public Query newQuery(final Class cls, final String filter) {
        final Query query = this.newQuery();
        query.setClass(cls);
        query.setFilter(filter);
        return query;
    }
    
    public Query newQuery(final Class cls, final Collection cln, final String filter) {
        final Query query = this.newQuery();
        query.setClass(cls);
        query.setCandidates(cln);
        query.setFilter(filter);
        return query;
    }
    
    public Query newQuery(final Extent cln, final String filter) {
        final Query query = this.newQuery();
        query.setClass(cln.getCandidateClass());
        query.setCandidates(cln);
        query.setFilter(filter);
        return query;
    }
    
    public Query newNamedQuery(final Class cls, final String queryName) {
        this.assertIsOpen();
        if (queryName == null) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011005", queryName, cls));
        }
        final ClassLoaderResolver clr = this.ec.getClassLoaderResolver();
        final QueryMetaData qmd = this.ec.getMetaDataManager().getMetaDataForQuery(cls, clr, queryName);
        if (qmd == null) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011005", queryName, cls));
        }
        final Query query = this.newQuery(qmd.getLanguage(), qmd.getQuery());
        if (cls != null) {
            query.setClass(cls);
            if (!this.ec.getStoreManager().managesClass(cls.getName())) {
                this.ec.getStoreManager().addClass(cls.getName(), clr);
            }
        }
        if (qmd.getLanguage().equals(QueryLanguage.JDOQL.toString()) && (qmd.isUnique() || qmd.getResultClass() != null)) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011007", queryName));
        }
        if (qmd.isUnique()) {
            query.setUnique(true);
        }
        if (qmd.getResultClass() != null) {
            Class resultCls = null;
            try {
                resultCls = clr.classForName(qmd.getResultClass());
            }
            catch (ClassNotResolvedException cnre) {
                try {
                    final String resultClassName = cls.getPackage().getName() + "." + qmd.getResultClass();
                    resultCls = clr.classForName(resultClassName);
                }
                catch (ClassNotResolvedException cnre2) {
                    throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011008", queryName, qmd.getResultClass()));
                }
            }
            query.setResultClass(resultCls);
        }
        final ExtensionMetaData[] extmds = qmd.getExtensions();
        if (extmds != null) {
            for (int i = 0; i < extmds.length; ++i) {
                if (extmds[i].getVendorName().equals("datanucleus")) {
                    query.addExtension(extmds[i].getKey(), extmds[i].getValue());
                }
            }
        }
        if (qmd.isUnmodifiable()) {
            query.setUnmodifiable();
        }
        if (qmd.getFetchPlanName() != null) {
            final FetchPlanMetaData fpmd = this.ec.getMetaDataManager().getMetaDataForFetchPlan(qmd.getFetchPlanName());
            if (fpmd != null) {
                final org.datanucleus.FetchPlan fp = new org.datanucleus.FetchPlan(this.ec, clr);
                fp.removeGroup("default");
                final FetchGroupMetaData[] fgmds = fpmd.getFetchGroupMetaData();
                for (int j = 0; j < fgmds.length; ++j) {
                    fp.addGroup(fgmds[j].getName());
                }
                fp.setMaxFetchDepth(fpmd.getMaxFetchDepth());
                fp.setFetchSize(fpmd.getFetchSize());
                final JDOQuery jdoquery = (JDOQuery)query;
                jdoquery.getInternalQuery().setFetchPlan(fp);
            }
        }
        return query;
    }
    
    public <T> TypesafeQuery<T> newTypesafeQuery(final Class cls) {
        return new JDOTypesafeQuery<T>(this, cls);
    }
    
    public Extent getExtent(final Class pcClass, final boolean subclasses) {
        this.assertIsOpen();
        try {
            return new JDOExtent(this, this.ec.getExtent(pcClass, subclasses));
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Extent getExtent(final Class pcClass) {
        return this.getExtent(pcClass, true);
    }
    
    public Object newInstance(final Class pc) {
        this.assertIsOpen();
        try {
            return this.ec.newInstance(pc);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Object newObjectIdInstance(final Class pcClass, final Object key) {
        this.assertIsOpen();
        try {
            return this.ec.newObjectId(pcClass, key);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Set getManagedObjects() {
        return this.ec.getManagedObjects();
    }
    
    public Set getManagedObjects(final Class... classes) {
        return this.ec.getManagedObjects(classes);
    }
    
    public Set getManagedObjects(final EnumSet states) {
        if (states == null) {
            return null;
        }
        final String[] stateNames = new String[states.size()];
        final Iterator iter = states.iterator();
        int i = 0;
        while (iter.hasNext()) {
            final ObjectState state = iter.next();
            stateNames[i++] = state.toString();
        }
        return this.ec.getManagedObjects(stateNames);
    }
    
    public Set getManagedObjects(final EnumSet states, final Class... classes) {
        if (states == null) {
            return null;
        }
        final String[] stateNames = new String[states.size()];
        final Iterator iter = states.iterator();
        int i = 0;
        while (iter.hasNext()) {
            final ObjectState state = iter.next();
            stateNames[i++] = state.toString();
        }
        return this.ec.getManagedObjects(stateNames, classes);
    }
    
    public Object getObjectById(final Object id) {
        return this.getObjectById(id, true);
    }
    
    public Object getObjectById(final Object id, final boolean validate) {
        this.assertIsOpen();
        if (id == null) {
            throw new JDONullIdentityException(JDOPersistenceManager.LOCALISER.msg("010044"));
        }
        try {
            return this.ec.findObject(id, validate, validate, null);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Object getObjectById(final Class cls, final Object key) {
        return this.getObjectById(this.newObjectIdInstance(cls, key), true);
    }
    
    public Object[] getObjectsById(final boolean validate, final Object... oids) {
        return this.getObjectsById(oids, validate);
    }
    
    public Object[] getObjectsById(final Object[] oids, final boolean validate) {
        this.assertIsOpen();
        if (oids == null) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011002"));
        }
        return this.ec.findObjects(oids, validate);
    }
    
    public Object[] getObjectsById(final Object... oids) {
        return this.getObjectsById(oids, true);
    }
    
    public Collection getObjectsById(final Collection oids) {
        return this.getObjectsById(oids, true);
    }
    
    public Collection getObjectsById(final Collection oids, final boolean validate) {
        this.assertIsOpen();
        if (oids == null) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011002"));
        }
        if (oids.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        final Collection objects = new ArrayList(oids.size());
        final Object[] objs = this.ec.findObjects(oids.toArray(), validate);
        for (int i = 0; i < objs.length; ++i) {
            objects.add(objs[i]);
        }
        return objects;
    }
    
    public Object getObjectId(final Object pc) {
        this.assertIsOpen();
        if (pc != null && pc instanceof PersistenceCapable) {
            final PersistenceCapable p = (PersistenceCapable)pc;
            if (p.jdoIsPersistent() || p.jdoIsDetached()) {
                return p.jdoGetObjectId();
            }
        }
        return null;
    }
    
    public Object getTransactionalObjectId(final Object pc) {
        this.assertIsOpen();
        return ((PersistenceCapable)pc).jdoGetTransactionalObjectId();
    }
    
    public Class getObjectIdClass(final Class cls) {
        this.assertIsOpen();
        if (!this.ec.getNucleusContext().getApiAdapter().isPersistable(cls) || !this.hasPersistenceInformationForClass(cls)) {
            return null;
        }
        final ClassLoaderResolver clr = this.ec.getClassLoaderResolver();
        final AbstractClassMetaData cmd = this.ec.getMetaDataManager().getMetaDataForClass(cls, clr);
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            return this.ec.getNucleusContext().getDatastoreIdentityClass();
        }
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            try {
                return this.ec.getClassLoaderResolver().classForName(this.ec.getMetaDataManager().getMetaDataForClass(cls, clr).getObjectidClass(), null);
            }
            catch (ClassNotResolvedException e) {
                final String msg = JDOPersistenceManager.LOCALISER_JDO.msg("011009", cls.getName());
                JDOPersistenceManager.LOGGER.error(msg);
                throw new JDOException(msg);
            }
        }
        if (cmd.isRequiresExtent()) {
            return this.ec.getNucleusContext().getDatastoreIdentityClass();
        }
        return SCOID.class;
    }
    
    public Object putUserObject(final Object key, final Object value) {
        this.assertIsOpen();
        if (key == null) {
            return null;
        }
        if (this.userObjectMap == null) {
            this.userObjectMap = new HashMap();
        }
        if (value == null) {
            return this.userObjectMap.remove(key);
        }
        return this.userObjectMap.put(key, value);
    }
    
    public Object getUserObject(final Object key) {
        this.assertIsOpen();
        if (key == null) {
            return null;
        }
        if (this.userObjectMap == null) {
            return null;
        }
        return this.userObjectMap.get(key);
    }
    
    public Object removeUserObject(final Object key) {
        this.assertIsOpen();
        if (key == null) {
            return null;
        }
        if (this.userObjectMap == null) {
            return null;
        }
        return this.userObjectMap.remove(key);
    }
    
    public void setUserObject(final Object userObject) {
        this.assertIsOpen();
        this.userObject = userObject;
    }
    
    public Object getUserObject() {
        this.assertIsOpen();
        return this.userObject;
    }
    
    public void flush() {
        this.assertIsOpen();
        try {
            this.ec.flush();
        }
        catch (NucleusException ne) {
            if (ne instanceof NucleusOptimisticException) {
                final Throwable[] nested = ne.getNestedExceptions();
                final JDOOptimisticVerificationException[] jdoNested = new JDOOptimisticVerificationException[nested.length];
                for (int i = 0; i < nested.length; ++i) {
                    jdoNested[i] = (JDOOptimisticVerificationException)NucleusJDOHelper.getJDOExceptionForNucleusException((NucleusException)nested[i]);
                }
                throw new JDOOptimisticVerificationException(ne.getMessage(), jdoNested);
            }
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void checkConsistency() {
        this.assertIsOpen();
        if (!this.ec.getTransaction().isActive()) {
            return;
        }
        if (this.ec.getTransaction().getOptimistic()) {
            throw new JDOUserException("checkConsistency() not yet implemented for optimistic transactions");
        }
        this.flush();
    }
    
    public Sequence getSequence(final String sequenceName) {
        this.assertIsOpen();
        final SequenceMetaData seqmd = this.ec.getMetaDataManager().getMetaDataForSequence(this.ec.getClassLoaderResolver(), sequenceName);
        if (seqmd == null) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("017000", sequenceName));
        }
        Sequence seq = null;
        if (seqmd.getFactoryClass() != null) {
            seq = this.pmf.getSequenceForFactoryClass(seqmd.getFactoryClass());
            if (seq == null) {
                final Class factory = this.ec.getClassLoaderResolver().classForName(seqmd.getFactoryClass());
                if (factory == null) {
                    throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("017001", sequenceName, seqmd.getFactoryClass()));
                }
                Class[] argTypes = null;
                Object[] arguments = null;
                if (seqmd.getStrategy() != null) {
                    argTypes = new Class[] { String.class, String.class };
                    arguments = new Object[] { seqmd.getName(), seqmd.getStrategy().toString() };
                }
                else {
                    argTypes = new Class[] { String.class };
                    arguments = new Object[] { seqmd.getName() };
                }
                try {
                    final Method newInstanceMethod = factory.getMethod("newInstance", (Class[])argTypes);
                    seq = (Sequence)newInstanceMethod.invoke(null, arguments);
                }
                catch (Exception e) {
                    throw new JDOUserException(JDOPersistenceManager.LOCALISER.msg("017002", seqmd.getFactoryClass(), e.getMessage()));
                }
                this.pmf.addSequenceForFactoryClass(seqmd.getFactoryClass(), seq);
            }
        }
        else {
            final NucleusSequence nucSeq = this.ec.getStoreManager().getNucleusSequence(this.ec, seqmd);
            seq = new JDOSequence(nucSeq);
        }
        return seq;
    }
    
    public void addInstanceLifecycleListener(final InstanceLifecycleListener listener, Class... classes) {
        this.assertIsOpen();
        if (listener == null) {
            return;
        }
        classes = LifecycleListenerForClass.canonicaliseClasses(classes);
        if (classes != null && classes.length == 0) {
            return;
        }
        this.ec.addListener(listener, classes);
    }
    
    public void removeInstanceLifecycleListener(final InstanceLifecycleListener listener) {
        this.assertIsOpen();
        this.ec.removeListener(listener);
    }
    
    protected void assertIsOpen() {
        if (this.isClosed()) {
            throw new JDOFatalUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011000"));
        }
    }
    
    protected void assertActiveTransaction() {
        if (!this.ec.getTransaction().isActive()) {
            throw new TransactionNotActiveException();
        }
    }
    
    protected void assertWritable() {
        if (!this.ec.getTransaction().isActive() && !this.ec.getTransaction().getNontransactionalWrite()) {
            throw new TransactionNotWritableException();
        }
    }
    
    protected void assertReadable(final String operation) {
        if (!this.ec.getTransaction().isActive() && !this.ec.getTransaction().getNontransactionalRead()) {
            throw new JDOUserException(JDOPersistenceManager.LOCALISER_JDO.msg("011001", operation));
        }
    }
    
    protected boolean hasPersistenceInformationForClass(final Class cls) {
        return this.ec.hasPersistenceInformationForClass(cls);
    }
    
    public JDOConnection getDataStoreConnection() {
        try {
            final NucleusConnection nconn = this.ec.getStoreManager().getNucleusConnection(this.ec);
            if (this.ec.getStoreManager().isJdbcStore()) {
                return new JDOConnectionJDBCImpl(nconn);
            }
            return new JDOConnectionImpl(nconn);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public FetchGroup getFetchGroup(final Class cls, final String name) {
        if (this.jdoFetchGroups == null) {
            this.jdoFetchGroups = new HashSet<JDOFetchGroup>();
        }
        for (final JDOFetchGroup jdoGrp : this.jdoFetchGroups) {
            if (jdoGrp.getName().equals(name) && jdoGrp.getType() == cls && !jdoGrp.isUnmodifiable()) {
                return jdoGrp;
            }
        }
        JDOFetchGroup jdoGrp = (JDOFetchGroup)this.getPersistenceManagerFactory().getFetchGroup(cls, name);
        if (jdoGrp != null) {
            final org.datanucleus.FetchGroup internalGrp = jdoGrp.getInternalFetchGroup();
            final org.datanucleus.FetchGroup internalCopy = new org.datanucleus.FetchGroup(internalGrp);
            jdoGrp = new JDOFetchGroup(internalCopy);
            this.ec.addInternalFetchGroup(internalCopy);
            this.jdoFetchGroups.add(jdoGrp);
            return jdoGrp;
        }
        try {
            final org.datanucleus.FetchGroup internalGrp = this.ec.getInternalFetchGroup(cls, name);
            jdoGrp = new JDOFetchGroup(internalGrp);
            this.jdoFetchGroups.add(jdoGrp);
            return jdoGrp;
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void setProperty(final String propertyName, final Object value) {
        this.assertIsOpen();
        try {
            this.ec.setProperty(propertyName, value);
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public Map<String, Object> getProperties() {
        this.assertIsOpen();
        final Map<String, Object> pmProps = new HashMap<String, Object>();
        final Map<String, Object> ecProps = this.ec.getProperties();
        final Iterator<Map.Entry<String, Object>> propertiesIter = ecProps.entrySet().iterator();
        final PersistenceConfiguration conf = this.ec.getNucleusContext().getPersistenceConfiguration();
        while (propertiesIter.hasNext()) {
            final Map.Entry<String, Object> entry = propertiesIter.next();
            final String ecPropName = entry.getKey();
            final String pmPropName = conf.getPropertyNameWithInternalPropertyName(ecPropName, "javax.jdo");
            pmProps.put((pmPropName != null) ? pmPropName : ecPropName, entry.getValue());
        }
        return pmProps;
    }
    
    public Set<String> getSupportedProperties() {
        this.assertIsOpen();
        return this.ec.getSupportedProperties();
    }
    
    public void addTransactionEventListener(final TransactionEventListener listener) {
        this.assertIsOpen();
        this.ec.getTransaction().bindTransactionEventListener(listener);
    }
    
    public void removeTransactionEventListener(final TransactionEventListener listener) {
        this.assertIsOpen();
        this.ec.getTransaction().removeTransactionEventListener(listener);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        LOCALISER_JDO = Localiser.getInstance("org.datanucleus.api.jdo.Localisation", JDOPersistenceManagerFactory.class.getClassLoader());
        LOGGER = NucleusLogger.getLoggerInstance("DataNucleus.JDO");
    }
}
