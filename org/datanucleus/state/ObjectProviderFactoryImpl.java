// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.cache.CachedPC;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.FieldValues;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PersistenceConfiguration;
import java.security.AccessController;
import javax.jdo.spi.JDOImplHelper;
import java.security.PrivilegedAction;
import org.datanucleus.util.StringUtils;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;

public class ObjectProviderFactoryImpl implements ObjectProviderFactory
{
    protected static final Localiser LOCALISER;
    Class opClass;
    public static Class[] OBJECT_PROVIDER_CTR_ARG_CLASSES;
    
    public ObjectProviderFactoryImpl(final NucleusContext nucCtx) {
        this.opClass = null;
        final PersistenceConfiguration conf = nucCtx.getPersistenceConfiguration();
        String opClassName = conf.getStringProperty("datanucleus.objectProvider.className");
        if (StringUtils.isWhitespace(opClassName)) {
            opClassName = nucCtx.getStoreManager().getDefaultObjectProviderClassName();
        }
        this.opClass = nucCtx.getClassLoaderResolver(null).classForName(opClassName);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                JDOImplHelper.registerAuthorizedStateManagerClass(ObjectProviderFactoryImpl.this.opClass);
                return null;
            }
        });
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public ObjectProvider newForHollow(final ExecutionContext ec, final Class pcClass, final Object id) {
        final Class cls = this.getInitialisedClassForClass(pcClass, ec.getClassLoaderResolver());
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pcClass, ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForHollow(id, null, cls);
        return op;
    }
    
    @Override
    public ObjectProvider newForHollow(final ExecutionContext ec, final Class pcClass, final Object id, final FieldValues fv) {
        final Class cls = this.getInitialisedClassForClass(pcClass, ec.getClassLoaderResolver());
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pcClass, ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForHollow(id, fv, cls);
        return op;
    }
    
    @Override
    public ObjectProvider newForHollowPreConstructed(final ExecutionContext ec, final Object id, final Object pc) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForHollowPreConstructed(id, pc);
        return op;
    }
    
    @Override
    @Deprecated
    public ObjectProvider newForHollowPopulatedAppId(final ExecutionContext ec, final Class pcClass, final FieldValues fv) {
        final Class cls = this.getInitialisedClassForClass(pcClass, ec.getClassLoaderResolver());
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pcClass, ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForHollowAppId(fv, cls);
        return op;
    }
    
    @Override
    public ObjectProvider newForPersistentClean(final ExecutionContext ec, final Object id, final Object pc) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForPersistentClean(id, pc);
        return op;
    }
    
    @Override
    public ObjectProvider newForEmbedded(final ExecutionContext ec, final Object pc, final boolean copyPc, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForEmbedded(pc, copyPc);
        if (ownerOP != null) {
            ec.registerEmbeddedRelation(ownerOP, ownerFieldNumber, op);
        }
        return op;
    }
    
    @Override
    public ObjectProvider newForEmbedded(final ExecutionContext ec, final AbstractClassMetaData cmd, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        final Class pcClass = ec.getClassLoaderResolver().classForName(cmd.getFullClassName());
        final ObjectProvider op = this.newForHollow(ec, pcClass, null);
        op.initialiseForEmbedded(op.getObject(), false);
        if (ownerOP != null) {
            ec.registerEmbeddedRelation(ownerOP, ownerFieldNumber, op);
        }
        return op;
    }
    
    @Override
    public ObjectProvider newForPersistentNew(final ExecutionContext ec, final Object pc, final FieldValues preInsertChanges) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForPersistentNew(pc, preInsertChanges);
        return op;
    }
    
    @Override
    public ObjectProvider newForTransactionalTransient(final ExecutionContext ec, final Object pc) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForTransactionalTransient(pc);
        return op;
    }
    
    @Override
    public ObjectProvider newForDetached(final ExecutionContext ec, final Object pc, final Object id, final Object version) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForDetached(pc, id, version);
        return op;
    }
    
    @Override
    public ObjectProvider newForPNewToBeDeleted(final ExecutionContext ec, final Object pc) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(pc.getClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForPNewToBeDeleted(pc);
        return op;
    }
    
    @Override
    public ObjectProvider newForCachedPC(final ExecutionContext ec, final Object id, final CachedPC cachedPC) {
        final AbstractClassMetaData cmd = ec.getMetaDataManager().getMetaDataForClass(cachedPC.getObjectClass(), ec.getClassLoaderResolver());
        final ObjectProvider op = this.getObjectProvider(ec, cmd);
        op.initialiseForCachedPC(cachedPC, id);
        return op;
    }
    
    @Override
    public void disconnectObjectProvider(final ObjectProvider op) {
    }
    
    protected ObjectProvider getObjectProvider(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        return (ObjectProvider)ClassUtils.newInstance(this.opClass, ObjectProviderFactoryImpl.OBJECT_PROVIDER_CTR_ARG_CLASSES, new Object[] { ec, cmd });
    }
    
    private Class getInitialisedClassForClass(final Class pcCls, final ClassLoaderResolver clr) {
        try {
            return clr.classForName(pcCls.getName(), pcCls.getClassLoader(), true);
        }
        catch (ClassNotResolvedException e) {
            throw new NucleusUserException(ObjectProviderFactoryImpl.LOCALISER.msg("026015", pcCls.getName())).setFatal();
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        ObjectProviderFactoryImpl.OBJECT_PROVIDER_CTR_ARG_CLASSES = new Class[] { ExecutionContext.class, AbstractClassMetaData.class };
    }
}
