// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.util.Iterator;
import javax.jdo.Extent;
import javax.jdo.Transaction;
import javax.jdo.PersistenceManager;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.NucleusContext;
import java.util.Collection;
import java.util.ArrayList;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import javax.jdo.JDOUserException;
import java.util.Properties;
import javax.jdo.PersistenceManagerFactory;
import org.datanucleus.util.Localiser;

public class JDOReplicationManager
{
    protected static final Localiser LOCALISER_JDO;
    final PersistenceManagerFactory pmfSource;
    final PersistenceManagerFactory pmfTarget;
    protected Properties properties;
    
    public JDOReplicationManager(final PersistenceManagerFactory pmf1, final PersistenceManagerFactory pmf2) {
        this.properties = new Properties();
        if (pmf1 == null || pmf1.isClosed()) {
            throw new JDOUserException(JDOReplicationManager.LOCALISER_JDO.msg("012050"));
        }
        if (pmf2 == null || pmf2.isClosed()) {
            throw new JDOUserException(JDOReplicationManager.LOCALISER_JDO.msg("012050"));
        }
        this.pmfSource = pmf1;
        this.pmfTarget = pmf2;
        this.properties.setProperty("datanucleus.replicateObjectGraph", "true");
        this.properties.setProperty("datanucleus.deleteUnknownObjects", "false");
    }
    
    public void setProperty(final String key, final String value) {
        this.properties.setProperty(key, value);
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    protected boolean getBooleanProperty(final String key) {
        final String val = this.properties.getProperty(key);
        return val != null && val.equalsIgnoreCase("true");
    }
    
    public void replicate(final Class... types) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012052", this.pmfSource, this.pmfTarget, StringUtils.objectArrayToString(types)));
        }
        final NucleusContext nucleusCtxSource = ((JDOPersistenceManagerFactory)this.pmfSource).getNucleusContext();
        final MetaDataManager mmgr = nucleusCtxSource.getMetaDataManager();
        final ClassLoaderResolver clr = nucleusCtxSource.getClassLoaderResolver(null);
        for (int i = 0; i < types.length; ++i) {
            final AbstractClassMetaData cmd = mmgr.getMetaDataForClass(types[i], clr);
            if (!cmd.isDetachable()) {
                throw new JDOUserException("Class " + types[i] + " is not detachable so cannot replicate");
            }
        }
        Object[] detachedObjects = null;
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012053"));
        }
        final PersistenceManager pm1 = this.pmfSource.getPersistenceManager();
        final Transaction tx1 = pm1.currentTransaction();
        if (this.getBooleanProperty("datanucleus.replicateObjectGraph")) {
            pm1.getFetchPlan().setGroup("all");
            pm1.getFetchPlan().setMaxFetchDepth(-1);
        }
        try {
            tx1.begin();
            final ArrayList objects = new ArrayList();
            for (int j = 0; j < types.length; ++j) {
                final AbstractClassMetaData cmd2 = mmgr.getMetaDataForClass(types[j], clr);
                if (!cmd2.isEmbeddedOnly()) {
                    final Extent ex = pm1.getExtent((Class<Object>)types[j]);
                    final Iterator iter = ex.iterator();
                    while (iter.hasNext()) {
                        objects.add(iter.next());
                    }
                }
            }
            final Collection detachedColl = pm1.detachCopyAll((Collection<Object>)objects);
            detachedObjects = detachedColl.toArray();
            tx1.commit();
        }
        finally {
            if (tx1.isActive()) {
                tx1.rollback();
            }
            pm1.close();
        }
        this.replicateInTarget(detachedObjects);
    }
    
    public void replicate(final String... classNames) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012052", this.pmfSource, this.pmfTarget, StringUtils.objectArrayToString(classNames)));
        }
        final NucleusContext nucleusCtxSource = ((JDOPersistenceManagerFactory)this.pmfSource).getNucleusContext();
        final MetaDataManager mmgr = nucleusCtxSource.getMetaDataManager();
        ClassLoaderResolver clr = nucleusCtxSource.getClassLoaderResolver(null);
        for (int i = 0; i < classNames.length; ++i) {
            final AbstractClassMetaData cmd = mmgr.getMetaDataForClass(classNames[i], clr);
            if (!cmd.isDetachable()) {
                throw new JDOUserException("Class " + classNames[i] + " is not detachable so cannot replicate");
            }
        }
        Object[] detachedObjects = null;
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012053"));
        }
        final PersistenceManager pm1 = this.pmfSource.getPersistenceManager();
        final Transaction tx1 = pm1.currentTransaction();
        if (this.getBooleanProperty("datanucleus.replicateObjectGraph")) {
            pm1.getFetchPlan().setGroup("all");
            pm1.getFetchPlan().setMaxFetchDepth(-1);
        }
        try {
            tx1.begin();
            clr = ((JDOPersistenceManager)pm1).getExecutionContext().getClassLoaderResolver();
            final ArrayList objects = new ArrayList();
            for (int j = 0; j < classNames.length; ++j) {
                final Class cls = clr.classForName(classNames[j]);
                final AbstractClassMetaData cmd2 = mmgr.getMetaDataForClass(cls, clr);
                if (!cmd2.isEmbeddedOnly()) {
                    final Extent ex = pm1.getExtent((Class<Object>)cls);
                    final Iterator iter = ex.iterator();
                    while (iter.hasNext()) {
                        objects.add(iter.next());
                    }
                }
            }
            final Collection detachedColl = pm1.detachCopyAll((Collection<Object>)objects);
            detachedObjects = detachedColl.toArray();
            tx1.commit();
        }
        finally {
            if (tx1.isActive()) {
                tx1.rollback();
            }
            pm1.close();
        }
        this.replicateInTarget(detachedObjects);
    }
    
    public void replicate(final Object... oids) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012051", this.pmfSource, this.pmfTarget, StringUtils.objectArrayToString(oids)));
        }
        Object[] detachedObjects = null;
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012053"));
        }
        final PersistenceManager pm1 = this.pmfSource.getPersistenceManager();
        final Transaction tx1 = pm1.currentTransaction();
        if (this.getBooleanProperty("datanucleus.replicateObjectGraph")) {
            pm1.getFetchPlan().setGroup("all");
            pm1.getFetchPlan().setMaxFetchDepth(-1);
        }
        try {
            tx1.begin();
            final Object[] objs = pm1.getObjectsById(oids);
            detachedObjects = pm1.detachCopyAll(objs);
            tx1.commit();
        }
        finally {
            if (tx1.isActive()) {
                tx1.rollback();
            }
            pm1.close();
        }
        this.replicateInTarget(detachedObjects);
    }
    
    public void replicateRegisteredClasses() {
        final ClassLoaderResolver clr = ((JDOPersistenceManager)this.pmfSource.getPersistenceManager()).getExecutionContext().getClassLoaderResolver();
        final MetaDataManager mmgr = ((JDOPersistenceManagerFactory)this.pmfSource).getNucleusContext().getMetaDataManager();
        final Collection classNames = mmgr.getClassesWithMetaData();
        final ArrayList arrayTypes = new ArrayList();
        for (final String className : classNames) {
            final AbstractClassMetaData cmd = mmgr.getMetaDataForClass(className, clr);
            if (!cmd.isEmbeddedOnly()) {
                arrayTypes.add(clr.classForName(className));
            }
        }
        this.replicate((Class[])arrayTypes.toArray(new Class[arrayTypes.size()]));
    }
    
    protected void replicateInTarget(final Object... detachedObjects) {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012054"));
        }
        final JDOPersistenceManager pm2 = (JDOPersistenceManager)this.pmfTarget.getPersistenceManager();
        final Transaction tx2 = pm2.currentTransaction();
        try {
            tx2.begin();
            pm2.makePersistentAll(detachedObjects);
            tx2.commit();
        }
        finally {
            if (tx2.isActive()) {
                tx2.rollback();
            }
            pm2.close();
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOReplicationManager.LOCALISER_JDO.msg("012055"));
        }
    }
    
    static {
        LOCALISER_JDO = Localiser.getInstance("org.datanucleus.api.jdo.Localisation", JDOPersistenceManagerFactory.class.getClassLoader());
    }
}
