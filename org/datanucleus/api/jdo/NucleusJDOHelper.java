// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import org.datanucleus.ClassConstants;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.jdo.spi.JDOImplHelper;
import org.datanucleus.ClassLoaderResolver;
import javax.jdo.JDOFatalInternalException;
import java.sql.SQLException;
import org.datanucleus.transaction.HeuristicRollbackException;
import javax.jdo.JDOOptimisticVerificationException;
import org.datanucleus.exceptions.NucleusOptimisticException;
import javax.jdo.JDOFatalUserException;
import org.datanucleus.exceptions.NucleusUserException;
import javax.jdo.JDOCanRetryException;
import org.datanucleus.exceptions.NucleusCanRetryException;
import javax.jdo.JDOObjectNotFoundException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import javax.jdo.JDOFatalDataStoreException;
import org.datanucleus.exceptions.NucleusDataStoreException;
import javax.jdo.JDOUserException;
import org.datanucleus.store.exceptions.DatastoreReadOnlyException;
import javax.jdo.JDOUnsupportedOptionException;
import org.datanucleus.exceptions.NucleusUnsupportedOptionException;
import javax.jdo.JDODataStoreException;
import org.datanucleus.store.query.QueryTimeoutException;
import javax.jdo.JDOQueryInterruptedException;
import org.datanucleus.store.query.QueryInterruptedException;
import org.datanucleus.exceptions.TransactionNotActiveException;
import org.datanucleus.exceptions.TransactionNotWritableException;
import org.datanucleus.exceptions.TransactionNotReadableException;
import org.datanucleus.exceptions.NoPersistenceInformationException;
import org.datanucleus.api.jdo.exceptions.ClassNotPersistenceCapableException;
import org.datanucleus.exceptions.ClassNotPersistableException;
import javax.jdo.JDOException;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.ExecutionContext;
import javax.jdo.spi.StateManager;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.PersistenceManager;
import java.lang.reflect.Field;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.ClassUtils;
import java.util.Collection;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.ClassMetaData;
import javax.jdo.PersistenceManagerFactory;
import org.datanucleus.util.Localiser;
import javax.jdo.JDOHelper;

public class NucleusJDOHelper extends JDOHelper
{
    protected static final Localiser LOCALISER;
    
    public static JDOQueryCache getQueryResultCache(final PersistenceManagerFactory pmf) {
        return ((JDOPersistenceManagerFactory)pmf).getQueryCache();
    }
    
    public static void replicate(final PersistenceManagerFactory pmf1, final PersistenceManagerFactory pmf2, final Object... oids) {
        final JDOReplicationManager replicator = new JDOReplicationManager(pmf1, pmf2);
        replicator.replicate(oids);
    }
    
    public static void replicate(final PersistenceManagerFactory pmf1, final PersistenceManagerFactory pmf2, final Class... types) {
        final JDOReplicationManager replicator = new JDOReplicationManager(pmf1, pmf2);
        replicator.replicate(types);
    }
    
    public static void replicate(final PersistenceManagerFactory pmf1, final PersistenceManagerFactory pmf2, final String... classNames) {
        final JDOReplicationManager replicator = new JDOReplicationManager(pmf1, pmf2);
        replicator.replicate(classNames);
    }
    
    public static ClassMetaData getMetaDataForClass(final PersistenceManagerFactory pmf, final Class cls) {
        if (pmf == null || cls == null) {
            return null;
        }
        if (!(pmf instanceof JDOPersistenceManagerFactory)) {
            return null;
        }
        final JDOPersistenceManagerFactory myPMF = (JDOPersistenceManagerFactory)pmf;
        final MetaDataManager mdmgr = myPMF.getNucleusContext().getMetaDataManager();
        return (ClassMetaData)mdmgr.getMetaDataForClass(cls, myPMF.getNucleusContext().getClassLoaderResolver(null));
    }
    
    public static String[] getClassesWithMetaData(final PersistenceManagerFactory pmf) {
        if (pmf == null || !(pmf instanceof JDOPersistenceManagerFactory)) {
            return null;
        }
        final JDOPersistenceManagerFactory myPMF = (JDOPersistenceManagerFactory)pmf;
        final Collection classes = myPMF.getNucleusContext().getMetaDataManager().getClassesWithMetaData();
        return classes.toArray(new String[classes.size()]);
    }
    
    public static Object[] getDetachedStateForObject(final Object obj) {
        if (obj == null || !JDOHelper.isDetached(obj)) {
            return null;
        }
        try {
            final Field fld = ClassUtils.getFieldForClass(obj.getClass(), "jdoDetachedState");
            fld.setAccessible(true);
            return (Object[])fld.get(obj);
        }
        catch (Exception e) {
            throw new NucleusException("Exception accessing jdoDetachedState field", e);
        }
    }
    
    public static String[] getDirtyFields(final Object obj, final PersistenceManager pm) {
        if (obj == null || !(obj instanceof PersistenceCapable)) {
            return null;
        }
        final PersistenceCapable pc = (PersistenceCapable)obj;
        if (JDOHelper.isDetached(pc)) {
            final ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
            final ObjectProvider op = ec.newObjectProviderForDetached(pc, JDOHelper.getObjectId(pc), null);
            pc.jdoReplaceStateManager((StateManager)op);
            op.retrieveDetachState(op);
            final String[] dirtyFieldNames = op.getDirtyFieldNames();
            pc.jdoReplaceStateManager(null);
            return dirtyFieldNames;
        }
        final ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
        final ObjectProvider sm = ec.findObjectProvider(pc);
        if (sm == null) {
            return null;
        }
        return sm.getDirtyFieldNames();
    }
    
    public static String[] getLoadedFields(final Object obj, final PersistenceManager pm) {
        if (obj == null || !(obj instanceof PersistenceCapable)) {
            return null;
        }
        final PersistenceCapable pc = (PersistenceCapable)obj;
        if (JDOHelper.isDetached(pc)) {
            final ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
            final ObjectProvider op = ec.newObjectProviderForDetached(pc, JDOHelper.getObjectId(pc), null);
            pc.jdoReplaceStateManager((StateManager)op);
            op.retrieveDetachState(op);
            final String[] loadedFieldNames = op.getLoadedFieldNames();
            pc.jdoReplaceStateManager(null);
            return loadedFieldNames;
        }
        final ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
        final ObjectProvider sm = ec.findObjectProvider(pc);
        if (sm == null) {
            return null;
        }
        return sm.getLoadedFieldNames();
    }
    
    public static Boolean isLoaded(final Object obj, final String memberName, final PersistenceManager pm) {
        if (obj == null || !(obj instanceof PersistenceCapable)) {
            return null;
        }
        final PersistenceCapable pc = (PersistenceCapable)obj;
        if (JDOHelper.isDetached(pc)) {
            final ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
            final ObjectProvider op = ec.newObjectProviderForDetached(pc, JDOHelper.getObjectId(pc), null);
            pc.jdoReplaceStateManager((StateManager)op);
            op.retrieveDetachState(op);
            final int position = op.getClassMetaData().getAbsolutePositionOfMember(memberName);
            final boolean loaded = op.isFieldLoaded(position);
            pc.jdoReplaceStateManager(null);
            return loaded;
        }
        final ExecutionContext ec = ((JDOPersistenceManager)pc.jdoGetPersistenceManager()).getExecutionContext();
        final ObjectProvider sm = ec.findObjectProvider(pc);
        if (sm == null) {
            return null;
        }
        final int position = sm.getClassMetaData().getAbsolutePositionOfMember(memberName);
        return sm.isFieldLoaded(position);
    }
    
    public static Boolean isDirty(final Object obj, final String memberName, final PersistenceManager pm) {
        if (obj == null || !(obj instanceof PersistenceCapable)) {
            return null;
        }
        final PersistenceCapable pc = (PersistenceCapable)obj;
        if (JDOHelper.isDetached(pc)) {
            final ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
            final ObjectProvider op = ec.newObjectProviderForDetached(pc, JDOHelper.getObjectId(pc), null);
            pc.jdoReplaceStateManager((StateManager)op);
            op.retrieveDetachState(op);
            final int position = op.getClassMetaData().getAbsolutePositionOfMember(memberName);
            final boolean[] dirtyFieldNumbers = op.getDirtyFields();
            pc.jdoReplaceStateManager(null);
            return dirtyFieldNumbers[position];
        }
        final ExecutionContext ec = ((JDOPersistenceManager)pc.jdoGetPersistenceManager()).getExecutionContext();
        final ObjectProvider sm = ec.findObjectProvider(pc);
        if (sm == null) {
            return null;
        }
        final int position = sm.getClassMetaData().getAbsolutePositionOfMember(memberName);
        final boolean[] dirtyFieldNumbers = sm.getDirtyFields();
        return dirtyFieldNumbers[position];
    }
    
    public static JDOException getJDOExceptionForNucleusException(final NucleusException ne) {
        if (ne instanceof ClassNotPersistableException) {
            return new ClassNotPersistenceCapableException(ne.getMessage(), ne);
        }
        if (ne instanceof NoPersistenceInformationException) {
            return new org.datanucleus.api.jdo.exceptions.NoPersistenceInformationException(ne.getMessage(), ne);
        }
        if (ne instanceof TransactionNotReadableException) {
            return new org.datanucleus.api.jdo.exceptions.TransactionNotReadableException(ne.getMessage(), (Object)ne.getCause());
        }
        if (ne instanceof TransactionNotWritableException) {
            return new org.datanucleus.api.jdo.exceptions.TransactionNotWritableException(ne.getMessage(), (Object)ne.getCause());
        }
        if (ne instanceof TransactionNotActiveException) {
            return new org.datanucleus.api.jdo.exceptions.TransactionNotActiveException(ne.getMessage(), (Object)ne);
        }
        if (ne instanceof QueryInterruptedException) {
            return new JDOQueryInterruptedException(ne.getMessage());
        }
        if (ne instanceof QueryTimeoutException) {
            return new JDODataStoreException(ne.getMessage(), ne);
        }
        if (ne instanceof NucleusUnsupportedOptionException) {
            return new JDOUnsupportedOptionException(ne.getMessage(), ne);
        }
        if (ne instanceof DatastoreReadOnlyException) {
            final ClassLoaderResolver clr = ((DatastoreReadOnlyException)ne).getClassLoaderResolver();
            try {
                final Class cls = clr.classForName("javax.jdo.JDOReadOnlyException");
                throw (JDOUserException)ClassUtils.newInstance(cls, new Class[] { String.class }, new Object[] { ne.getMessage() });
            }
            catch (NucleusException ne2) {
                throw new JDOUserException(ne2.getMessage());
            }
        }
        if (ne instanceof NucleusDataStoreException) {
            if (ne.isFatal()) {
                if (ne.getFailedObject() != null) {
                    return new JDOFatalDataStoreException(ne.getMessage(), ne.getFailedObject());
                }
                if (ne.getNestedExceptions() != null) {
                    return new JDOFatalDataStoreException(ne.getMessage(), ne.getNestedExceptions());
                }
                return new JDOFatalDataStoreException(ne.getMessage(), ne);
            }
            else if (ne.getNestedExceptions() != null) {
                if (ne.getFailedObject() != null) {
                    return new JDODataStoreException(ne.getMessage(), ne.getNestedExceptions(), ne.getFailedObject());
                }
                return new JDODataStoreException(ne.getMessage(), ne.getNestedExceptions());
            }
            else {
                if (ne.getFailedObject() != null) {
                    JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                    return new JDODataStoreException(ne.getMessage(), ne.getFailedObject());
                }
                JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                return new JDODataStoreException(ne.getMessage(), ne);
            }
        }
        else if (ne instanceof NucleusObjectNotFoundException) {
            if (ne.getFailedObject() != null) {
                if (ne.getNestedExceptions() != null) {
                    return new JDOObjectNotFoundException(ne.getMessage(), ne.getNestedExceptions(), ne.getFailedObject());
                }
                return new JDOObjectNotFoundException(ne.getMessage(), ne, ne.getFailedObject());
            }
            else {
                if (ne.getNestedExceptions() != null) {
                    return new JDOObjectNotFoundException(ne.getMessage(), ne.getNestedExceptions());
                }
                return new JDOObjectNotFoundException(ne.getMessage(), new Throwable[] { ne });
            }
        }
        else if (ne instanceof NucleusCanRetryException) {
            if (ne.getNestedExceptions() != null) {
                if (ne.getFailedObject() != null) {
                    return new JDOCanRetryException(ne.getMessage(), ne.getNestedExceptions(), ne.getFailedObject());
                }
                return new JDOCanRetryException(ne.getMessage(), ne.getNestedExceptions());
            }
            else {
                if (ne.getFailedObject() != null) {
                    JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                    return new JDOCanRetryException(ne.getMessage(), ne.getFailedObject());
                }
                JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                return new JDOCanRetryException(ne.getMessage(), ne);
            }
        }
        else if (ne instanceof NucleusUserException) {
            if (ne.isFatal()) {
                if (ne.getNestedExceptions() != null) {
                    if (ne.getFailedObject() != null) {
                        return new JDOFatalUserException(ne.getMessage(), ne.getNestedExceptions(), ne.getFailedObject());
                    }
                    return new JDOFatalUserException(ne.getMessage(), ne.getNestedExceptions());
                }
                else {
                    if (ne.getFailedObject() != null) {
                        JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                        return new JDOFatalUserException(ne.getMessage(), ne.getFailedObject());
                    }
                    JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                    return new JDOFatalUserException(ne.getMessage(), ne);
                }
            }
            else if (ne.getNestedExceptions() != null) {
                if (ne.getFailedObject() != null) {
                    return new JDOUserException(ne.getMessage(), ne.getNestedExceptions(), ne.getFailedObject());
                }
                return new JDOUserException(ne.getMessage(), ne.getNestedExceptions());
            }
            else {
                if (ne.getFailedObject() != null) {
                    JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                    return new JDOUserException(ne.getMessage(), ne.getFailedObject());
                }
                JDOPersistenceManager.LOGGER.info("Exception thrown", ne);
                return new JDOUserException(ne.getMessage(), ne);
            }
        }
        else if (ne instanceof NucleusOptimisticException) {
            if (ne.getFailedObject() != null) {
                return new JDOOptimisticVerificationException(ne.getMessage(), ne.getFailedObject());
            }
            if (ne.getNestedExceptions() != null) {
                return new JDOOptimisticVerificationException(ne.getMessage(), ne.getNestedExceptions());
            }
            return new JDOOptimisticVerificationException(ne.getMessage(), (Object)ne);
        }
        else {
            if (ne instanceof HeuristicRollbackException && ne.getNestedExceptions().length == 1 && ne.getNestedExceptions()[0].getCause() instanceof SQLException) {
                return new JDODataStoreException(ne.getMessage(), ne.getNestedExceptions()[0].getCause());
            }
            if (ne instanceof HeuristicRollbackException && ne.getNestedExceptions().length == 1 && ne.getNestedExceptions()[0] instanceof NucleusDataStoreException) {
                return new JDODataStoreException(ne.getMessage(), ne.getNestedExceptions()[0].getCause());
            }
            if (ne.isFatal()) {
                if (ne.getNestedExceptions() != null) {
                    return new JDOFatalInternalException(ne.getMessage(), ne.getNestedExceptions());
                }
                return new JDOFatalInternalException(ne.getMessage(), ne);
            }
            else {
                if (ne.getNestedExceptions() != null) {
                    return new JDOException(ne.getMessage(), ne.getNestedExceptions());
                }
                return new JDOException(ne.getMessage(), ne);
            }
        }
    }
    
    public static JDOImplHelper getJDOImplHelper() {
        return AccessController.doPrivileged((PrivilegedAction<JDOImplHelper>)new PrivilegedAction() {
            public Object run() {
                try {
                    return JDOImplHelper.getInstance();
                }
                catch (SecurityException e) {
                    throw new JDOFatalUserException(NucleusJDOHelper.LOCALISER.msg("026000"), e);
                }
            }
        });
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
