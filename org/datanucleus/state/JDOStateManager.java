// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.metadata.PersistenceFlags;
import java.io.PrintWriter;
import org.datanucleus.store.fieldmanager.NullifyRelationFieldManager;
import org.datanucleus.store.fieldmanager.DeleteFieldManager;
import org.datanucleus.store.fieldmanager.AttachFieldManager;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.fieldmanager.DetachFieldManager;
import org.datanucleus.store.fieldmanager.MakeTransientFieldManager;
import org.datanucleus.store.exceptions.NotYetFlushedException;
import org.datanucleus.store.fieldmanager.PersistFieldManager;
import org.datanucleus.store.fieldmanager.ReachabilityFieldManager;
import org.datanucleus.store.types.SCOMap;
import org.datanucleus.store.types.SCOCollection;
import java.util.Set;
import java.util.Map;
import org.datanucleus.store.types.SCOUtils;
import java.util.Collection;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.identity.OIDFactory;
import java.util.Iterator;
import java.util.List;
import org.datanucleus.FetchPlan;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.store.objectvaluegenerator.ObjectValueGenerator;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.exceptions.NucleusException;
import java.util.BitSet;
import javax.jdo.spi.Detachable;
import org.datanucleus.store.fieldmanager.AbstractFetchDepthFieldManager;
import org.datanucleus.store.types.SCOContainer;
import org.datanucleus.store.types.SCO;
import org.datanucleus.store.ObjectReferencingStoreManager;
import javax.jdo.PersistenceManager;
import javax.jdo.JDOFatalInternalException;
import javax.jdo.JDOFatalUserException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.datanucleus.cache.L2CacheRetrieveFieldManager;
import org.datanucleus.cache.CachedPC;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.store.fieldmanager.SingleValueFieldManager;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.identity.OID;
import org.datanucleus.store.FieldValues;
import org.datanucleus.NucleusContext;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.fieldmanager.UnsetOwnerFieldManager;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.StateManager;

public class JDOStateManager extends AbstractStateManager implements StateManager, ObjectProvider
{
    protected PersistenceCapable myPC;
    protected PersistenceCapable savedImage;
    private static final JDOImplHelper HELPER;
    
    public JDOStateManager(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        super(ec, cmd);
        this.savedImage = null;
    }
    
    @Override
    public void connect(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        super.connect(ec, cmd);
        this.savedImage = null;
        ec.setAttachDetachReferencedObject(this, null);
    }
    
    @Override
    public void disconnect() {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026011", StringUtils.toJVMIDString(this.myPC), this));
        }
        if (this.isPostLoadPending()) {
            this.flags &= 0xFFFFF7FF;
            this.setPostLoadPending(false);
            this.postLoad();
        }
        final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getSCOMutableMemberPositions(), true);
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            this.provideFields(fieldNumbers, new UnsetOwnerFieldManager());
        }
        this.myEC.removeObjectProvider(this);
        this.persistenceFlags = 0;
        this.myPC.jdoReplaceFlags();
        this.setDisconnecting(true);
        try {
            this.replaceStateManager(this.myPC, null);
        }
        finally {
            this.setDisconnecting(false);
        }
        this.preDeleteLoadedFields = null;
        this.myEC.setAttachDetachReferencedObject(this, null);
        this.objectType = 0;
        this.clearSavedFields();
        final NucleusContext nucCtx = this.myEC.getNucleusContext();
        this.myPC = null;
        this.myID = null;
        this.myInternalID = null;
        this.myLC = null;
        this.myEC = null;
        this.myFP = null;
        this.myVersion = null;
        this.persistenceFlags = 0;
        this.flags = 0;
        this.restoreValues = false;
        this.transactionalVersion = null;
        this.currFM = null;
        this.dirty = false;
        this.cmd = null;
        this.dirtyFields = null;
        this.loadedFields = null;
        nucCtx.getObjectProviderFactory().disconnectObjectProvider(this);
    }
    
    @Override
    public void initialiseForHollow(final Object id, final FieldValues fv, final Class pcClass) {
        this.myID = id;
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(4);
        this.persistenceFlags = 1;
        if (id instanceof OID || id == null) {
            this.myPC = JDOStateManager.HELPER.newInstance(pcClass, this);
        }
        else {
            this.myPC = JDOStateManager.HELPER.newInstance(pcClass, this, this.myID);
            this.markPKFieldsAsLoaded();
        }
        this.myEC.putObjectIntoLevel1Cache(this);
        if (fv != null) {
            this.loadFieldValues(fv);
        }
    }
    
    @Override
    @Deprecated
    public void initialiseForHollowAppId(final FieldValues fv, final Class pcClass) {
        if (this.cmd.getIdentityType() != IdentityType.APPLICATION) {
            throw new NucleusUserException("This constructor is only for objects using application identity.").setFatal();
        }
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(4);
        this.persistenceFlags = 1;
        this.myPC = JDOStateManager.HELPER.newInstance(pcClass, this);
        if (this.myPC != null) {
            this.loadFieldValues(fv);
            this.myID = this.myPC.jdoNewObjectIdInstance();
            if (!this.cmd.usesSingleFieldIdentityClass()) {
                this.myPC.jdoCopyKeyFieldsToObjectId(this.myID);
            }
            return;
        }
        if (!JDOStateManager.HELPER.getRegisteredClasses().contains(pcClass)) {
            throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026018", pcClass.getName())).setFatal();
        }
        throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026019", pcClass.getName())).setFatal();
    }
    
    @Override
    public void initialiseForHollowPreConstructed(final Object id, final Object pc) {
        this.myID = id;
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(4);
        this.persistenceFlags = 1;
        this.replaceStateManager(this.myPC = (PersistenceCapable)pc, this);
        this.myPC.jdoReplaceFlags();
    }
    
    @Override
    public void initialiseForPersistentClean(final Object id, final Object pc) {
        this.myID = id;
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(2);
        this.persistenceFlags = 1;
        this.replaceStateManager(this.myPC = (PersistenceCapable)pc, this);
        this.myPC.jdoReplaceFlags();
        for (int i = 0; i < this.loadedFields.length; ++i) {
            this.loadedFields[i] = true;
        }
        this.myEC.putObjectIntoLevel1Cache(this);
    }
    
    @Override
    public void initialiseForEmbedded(final Object pc, final boolean copyPc) {
        this.objectType = 1;
        this.myID = null;
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(2);
        this.persistenceFlags = 1;
        this.replaceStateManager(this.myPC = (PersistenceCapable)pc, this);
        if (copyPc) {
            final PersistenceCapable pcCopy = this.myPC.jdoNewInstance(this);
            pcCopy.jdoCopyFields(this.myPC, this.cmd.getAllMemberPositions());
            this.replaceStateManager(pcCopy, this);
            this.myPC = pcCopy;
            this.disconnectClone((PersistenceCapable)pc);
        }
        for (int i = 0; i < this.loadedFields.length; ++i) {
            this.loadedFields[i] = true;
        }
    }
    
    @Override
    public void initialiseForPersistentNew(final Object pc, final FieldValues preInsertChanges) {
        this.myPC = (PersistenceCapable)pc;
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(1);
        this.persistenceFlags = -1;
        for (int i = 0; i < this.loadedFields.length; ++i) {
            this.loadedFields[i] = true;
        }
        this.replaceStateManager(this.myPC, this);
        this.myPC.jdoReplaceFlags();
        this.saveFields();
        this.populateStrategyFields();
        if (preInsertChanges != null) {
            preInsertChanges.fetchFields(this);
        }
        if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            for (int totalNumFields = this.cmd.getAllMemberPositions().length, fieldNumber = 0; fieldNumber < totalNumFields; ++fieldNumber) {
                final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
                if (fmd.isPrimaryKey() && this.myEC.getMetaDataManager().getMetaDataForClass(fmd.getType(), this.getExecutionContext().getClassLoaderResolver()) != null) {
                    try {
                        if (this.myEC.getMultithreaded()) {
                            this.myEC.getLock().lock();
                            this.lock.lock();
                        }
                        final FieldManager prevFM = this.currFM;
                        try {
                            this.currFM = new SingleValueFieldManager();
                            this.myPC.jdoProvideField(fieldNumber);
                            final PersistenceCapable pkFieldPC = (PersistenceCapable)((SingleValueFieldManager)this.currFM).fetchObjectField(fieldNumber);
                            if (pkFieldPC == null) {
                                throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026016", fmd.getFullFieldName()));
                            }
                            if (!this.myEC.getApiAdapter().isPersistent(pkFieldPC)) {
                                final Object persistedFieldPC = this.myEC.persistObjectInternal(pkFieldPC, null, null, -1, 0);
                                this.replaceField(this.myPC, fieldNumber, persistedFieldPC, false);
                            }
                        }
                        finally {
                            this.currFM = prevFM;
                        }
                    }
                    finally {
                        if (this.myEC.getMultithreaded()) {
                            this.lock.unlock();
                            this.myEC.getLock().unlock();
                        }
                    }
                }
            }
        }
        this.setIdentity(false);
        if (this.myEC.getTransaction().isActive()) {
            this.myEC.enlistInTransaction(this);
        }
        this.getCallbackHandler().postCreate(this.myPC);
        if (this.myEC.getManageRelations()) {
            final ClassLoaderResolver clr = this.myEC.getClassLoaderResolver();
            final int[] relationPositions = this.cmd.getRelationMemberPositions(clr, this.myEC.getMetaDataManager());
            if (relationPositions != null) {
                for (int j = 0; j < relationPositions.length; ++j) {
                    final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(relationPositions[j]);
                    if (RelationType.isBidirectional(mmd.getRelationType(clr))) {
                        final Object value = this.provideField(relationPositions[j]);
                        if (value != null) {
                            this.myEC.getRelationshipManager(this).relationChange(relationPositions[j], null, value);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void initialiseForTransactionalTransient(final Object pc) {
        this.myPC = (PersistenceCapable)pc;
        this.myLC = null;
        this.persistenceFlags = -1;
        for (int i = 0; i < this.loadedFields.length; ++i) {
            this.loadedFields[i] = true;
        }
        this.myPC.jdoReplaceFlags();
        this.populateStrategyFields();
        this.setIdentity(false);
        if (this.myEC.getTransaction().isActive()) {
            this.myEC.enlistInTransaction(this);
        }
    }
    
    @Override
    public void initialiseForDetached(final Object pc, final Object id, final Object version) {
        this.myID = id;
        this.myPC = (PersistenceCapable)pc;
        this.setVersion(version);
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(11);
        this.myPC.jdoReplaceFlags();
        this.replaceStateManager(this.myPC, this);
    }
    
    @Override
    public void initialiseForPNewToBeDeleted(final Object pc) {
        this.myID = null;
        this.myPC = (PersistenceCapable)pc;
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(1);
        for (int i = 0; i < this.loadedFields.length; ++i) {
            this.loadedFields[i] = true;
        }
        this.replaceStateManager(this.myPC, this);
    }
    
    @Override
    public void initialiseForCachedPC(final CachedPC cachedPC, final Object id) {
        this.initialiseForHollow(id, null, cachedPC.getObjectClass());
        this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(2);
        this.persistenceFlags = -1;
        final int[] fieldsToLoad = ClassUtils.getFlagsSetTo(cachedPC.getLoadedFields(), this.myFP.getMemberNumbers(), true);
        if (fieldsToLoad != null) {
            this.myEC.putObjectIntoLevel1Cache(this);
            final L2CacheRetrieveFieldManager l2RetFM = new L2CacheRetrieveFieldManager(this, cachedPC);
            this.replaceFields(fieldsToLoad, l2RetFM);
            for (int i = 0; i < fieldsToLoad.length; ++i) {
                this.loadedFields[fieldsToLoad[i]] = true;
            }
            final int[] fieldsNotLoaded = l2RetFM.getFieldsNotLoaded();
            if (fieldsNotLoaded != null) {
                for (int j = 0; j < fieldsNotLoaded.length; ++j) {
                    this.loadedFields[fieldsNotLoaded[j]] = false;
                }
            }
        }
        if (cachedPC.getVersion() != null) {
            this.setVersion(cachedPC.getVersion());
        }
        this.replaceAllLoadedSCOFieldsWithWrappers();
        if (this.myEC.getTransaction().isActive()) {
            this.myEC.enlistInTransaction(this);
        }
        if (this.areFieldsLoaded(this.myFP.getMemberNumbers())) {
            this.postLoad();
        }
    }
    
    @Override
    public Object getObject() {
        return this.myPC;
    }
    
    @Override
    public void saveFields() {
        (this.savedImage = this.myPC.jdoNewInstance(this)).jdoCopyFields(this.myPC, this.cmd.getAllMemberPositions());
        this.savedFlags = this.persistenceFlags;
        this.savedLoadedFields = this.loadedFields.clone();
    }
    
    @Override
    public void clearSavedFields() {
        this.savedImage = null;
        this.savedFlags = 0;
        this.savedLoadedFields = null;
    }
    
    @Override
    public void restoreFields() {
        if (this.savedImage != null) {
            this.loadedFields = this.savedLoadedFields;
            this.persistenceFlags = this.savedFlags;
            this.myPC.jdoReplaceFlags();
            this.myPC.jdoCopyFields(this.savedImage, this.cmd.getAllMemberPositions());
            this.clearDirtyFlags();
            this.clearSavedFields();
        }
    }
    
    @Override
    public void enlistInTransaction() {
        if (!this.myEC.getTransaction().isActive()) {
            return;
        }
        this.myEC.enlistInTransaction(this);
        if (this.persistenceFlags == 1 && this.areFieldsLoaded(this.cmd.getDFGMemberPositions())) {
            this.persistenceFlags = -1;
            this.myPC.jdoReplaceFlags();
        }
    }
    
    @Override
    public void evictFromTransaction() {
        this.myEC.evictFromTransaction(this);
        this.persistenceFlags = 1;
        this.myPC.jdoReplaceFlags();
    }
    
    protected void replaceStateManager(final PersistenceCapable pc, final StateManager sm) {
        try {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                @Override
                public Object run() {
                    pc.jdoReplaceStateManager(sm);
                    return null;
                }
            });
        }
        catch (SecurityException e) {
            throw new JDOFatalUserException(JDOStateManager.LOCALISER.msg("026000"), e);
        }
    }
    
    @Override
    public StateManager replacingStateManager(final PersistenceCapable pc, final StateManager sm) {
        if (this.myLC == null) {
            throw new JDOFatalInternalException("Null LifeCycleState");
        }
        if (this.myLC.stateType() == 11) {
            return sm;
        }
        if (pc == this.myPC) {
            if (sm == null) {
                return null;
            }
            if (sm == this) {
                return this;
            }
            if (this.myEC == ((JDOStateManager)sm).getExecutionContext()) {
                NucleusLogger.PERSISTENCE.debug(">> SM.replacingStateManager this=" + this + " sm=" + sm + " with same EC");
                ((JDOStateManager)sm).disconnect();
                return this;
            }
            throw this.myEC.getApiAdapter().getUserExceptionForException(JDOStateManager.LOCALISER.msg("026003"), null);
        }
        else {
            if (pc == this.savedImage) {
                return null;
            }
            return sm;
        }
    }
    
    @Override
    public void replaceManagedPC(final Object pc) {
        if (pc == null) {
            return;
        }
        this.replaceStateManager((PersistenceCapable)pc, this);
        this.replaceStateManager(this.myPC, null);
        this.myPC = (PersistenceCapable)pc;
        this.myEC.putObjectIntoLevel1Cache(this);
    }
    
    @Override
    public PersistenceManager getPersistenceManager(final PersistenceCapable pc) {
        if (this.myPC != null && this.disconnectClone(pc)) {
            return null;
        }
        if (this.myEC == null) {
            return null;
        }
        this.myEC.hereIsObjectProvider(this, this.myPC);
        return (PersistenceManager)this.myEC.getOwner();
    }
    
    @Override
    public boolean isDirty(final PersistenceCapable pc) {
        return !this.disconnectClone(pc) && this.myLC.isDirty();
    }
    
    @Override
    public boolean isTransactional(final PersistenceCapable pc) {
        return !this.disconnectClone(pc) && this.myLC.isTransactional();
    }
    
    @Override
    public boolean isPersistent(final PersistenceCapable pc) {
        return !this.disconnectClone(pc) && this.myLC.isPersistent();
    }
    
    @Override
    public boolean isNew(final PersistenceCapable pc) {
        return !this.disconnectClone(pc) && this.myLC.isNew();
    }
    
    @Override
    public boolean isDeleted(final PersistenceCapable pc) {
        return !this.disconnectClone(pc) && this.myLC.isDeleted();
    }
    
    @Override
    public Object getVersion(final PersistenceCapable pc) {
        if (pc == this.myPC) {
            return this.transactionalVersion;
        }
        return null;
    }
    
    @Override
    public Object getVersion() {
        return this.getVersion(this.myPC);
    }
    
    @Override
    public Object getTransactionalVersion() {
        return this.getTransactionalVersion(this.myPC);
    }
    
    @Override
    public void clearFields() {
        try {
            this.getCallbackHandler().preClear(this.myPC);
        }
        finally {
            this.clearFieldsByNumbers(this.cmd.getAllMemberPositions());
            this.clearDirtyFlags();
            if (this.myEC.getStoreManager() instanceof ObjectReferencingStoreManager) {
                ((ObjectReferencingStoreManager)this.myEC.getStoreManager()).notifyObjectIsOutdated(this);
            }
            this.persistenceFlags = 1;
            this.myPC.jdoReplaceFlags();
            this.getCallbackHandler().postClear(this.myPC);
        }
    }
    
    @Override
    public void clearNonPrimaryKeyFields() {
        try {
            this.getCallbackHandler().preClear(this.myPC);
        }
        finally {
            final int[] nonpkFields = this.cmd.getNonPKMemberPositions();
            this.clearFieldsByNumbers(nonpkFields);
            this.clearDirtyFlags(nonpkFields);
            if (this.myEC.getStoreManager() instanceof ObjectReferencingStoreManager) {
                ((ObjectReferencingStoreManager)this.myEC.getStoreManager()).notifyObjectIsOutdated(this);
            }
            this.persistenceFlags = 1;
            this.myPC.jdoReplaceFlags();
            this.getCallbackHandler().postClear(this.myPC);
        }
    }
    
    @Override
    public void clearLoadedFlags() {
        if (this.myEC.getStoreManager() instanceof ObjectReferencingStoreManager) {
            ((ObjectReferencingStoreManager)this.myEC.getStoreManager()).notifyObjectIsOutdated(this);
        }
        this.persistenceFlags = 1;
        this.myPC.jdoReplaceFlags();
        ClassUtils.clearFlags(this.loadedFields);
    }
    
    @Override
    public byte replacingFlags(final PersistenceCapable pc) {
        if (pc != this.myPC) {
            return 0;
        }
        return this.persistenceFlags;
    }
    
    @Override
    public void providedBooleanField(final PersistenceCapable pc, final int fieldNumber, final boolean currentValue) {
        this.currFM.storeBooleanField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedByteField(final PersistenceCapable pc, final int fieldNumber, final byte currentValue) {
        this.currFM.storeByteField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedCharField(final PersistenceCapable pc, final int fieldNumber, final char currentValue) {
        this.currFM.storeCharField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedDoubleField(final PersistenceCapable pc, final int fieldNumber, final double currentValue) {
        this.currFM.storeDoubleField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedFloatField(final PersistenceCapable pc, final int fieldNumber, final float currentValue) {
        this.currFM.storeFloatField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedIntField(final PersistenceCapable pc, final int fieldNumber, final int currentValue) {
        this.currFM.storeIntField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedLongField(final PersistenceCapable pc, final int fieldNumber, final long currentValue) {
        this.currFM.storeLongField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedShortField(final PersistenceCapable pc, final int fieldNumber, final short currentValue) {
        this.currFM.storeShortField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedStringField(final PersistenceCapable pc, final int fieldNumber, final String currentValue) {
        this.currFM.storeStringField(fieldNumber, currentValue);
    }
    
    @Override
    public void providedObjectField(final PersistenceCapable pc, final int fieldNumber, final Object currentValue) {
        this.currFM.storeObjectField(fieldNumber, currentValue);
    }
    
    @Override
    public Object provideField(final int fieldNumber) {
        return this.provideField(this.myPC, fieldNumber);
    }
    
    protected Object provideField(final PersistenceCapable pc, final int fieldNumber) {
        Object obj;
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            final FieldManager prevFM = this.currFM;
            this.currFM = new SingleValueFieldManager();
            try {
                pc.jdoProvideField(fieldNumber);
                obj = this.currFM.fetchObjectField(fieldNumber);
            }
            finally {
                this.currFM = prevFM;
            }
        }
        finally {
            if (this.myEC.getMultithreaded()) {
                this.lock.unlock();
                this.myEC.getLock().unlock();
            }
        }
        return obj;
    }
    
    @Override
    public void provideFields(final int[] fieldNumbers, final FieldManager fm) {
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            final FieldManager prevFM = this.currFM;
            this.currFM = fm;
            try {
                this.myPC.jdoProvideFields(fieldNumbers);
            }
            finally {
                this.currFM = prevFM;
            }
        }
        finally {
            if (this.myEC.getMultithreaded()) {
                this.lock.unlock();
                this.myEC.getLock().unlock();
            }
        }
    }
    
    @Override
    public void setBooleanField(final PersistenceCapable pc, final int fieldNumber, final boolean currentValue, final boolean newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue ? Boolean.TRUE : Boolean.FALSE, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue ? Boolean.TRUE : Boolean.FALSE);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue ? Boolean.TRUE : Boolean.FALSE, true);
        }
    }
    
    @Override
    public void setByteField(final PersistenceCapable pc, final int fieldNumber, final byte currentValue, final byte newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setCharField(final PersistenceCapable pc, final int fieldNumber, final char currentValue, final char newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setDoubleField(final PersistenceCapable pc, final int fieldNumber, final double currentValue, final double newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setFloatField(final PersistenceCapable pc, final int fieldNumber, final float currentValue, final float newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setIntField(final PersistenceCapable pc, final int fieldNumber, final int currentValue, final int newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setLongField(final PersistenceCapable pc, final int fieldNumber, final long currentValue, final long newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setShortField(final PersistenceCapable pc, final int fieldNumber, final short currentValue, final short newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (!this.loadedFields[fieldNumber] || currentValue != newValue) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, currentValue);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (!this.myEC.getTransaction().isActive()) {
                    this.myEC.processNontransactionalUpdate();
                }
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setStringField(final PersistenceCapable pc, final int fieldNumber, final String currentValue, final String newValue) {
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            if (this.loadedFields[fieldNumber]) {
                if (currentValue == null) {
                    if (newValue == null) {
                        return;
                    }
                }
                else if (currentValue.equals(newValue)) {
                    return;
                }
            }
            if (this.cmd.getIdentityType() == IdentityType.NONDURABLE) {
                final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                if (!this.containsAssociatedValue(key)) {
                    this.setAssociatedValue(key, currentValue);
                }
            }
            this.updateField(pc, fieldNumber, newValue);
            if (!this.myEC.getTransaction().isActive()) {
                this.myEC.processNontransactionalUpdate();
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    @Override
    public void setObjectField(final PersistenceCapable pc, final int fieldNumber, final Object currentValue, Object newValue) {
        if (currentValue != null && currentValue != newValue && currentValue instanceof PersistenceCapable) {
            final JDOStateManager currentSM = (JDOStateManager)this.myEC.findObjectProvider(currentValue);
            if (currentSM != null && currentSM.isEmbedded()) {
                this.myEC.removeEmbeddedOwnerRelation(this, fieldNumber, currentSM);
            }
        }
        if (pc != this.myPC) {
            this.replaceField(pc, fieldNumber, newValue, true);
            this.disconnectClone(pc);
        }
        else if (this.myLC != null) {
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                this.loadUnloadedFieldsInFetchPlanAndVersion();
            }
            boolean loadedOldValue = false;
            Object oldValue = currentValue;
            final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final ClassLoaderResolver clr = this.myEC.getClassLoaderResolver();
            final RelationType relationType = mmd.getRelationType(clr);
            this.myEC.removeObjectFromLevel2Cache(this.myID);
            if (!this.loadedFields[fieldNumber] && currentValue == null) {
                if (this.myEC.getManageRelations() && (relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.MANY_TO_ONE_BI)) {
                    this.loadField(fieldNumber);
                    loadedOldValue = true;
                    oldValue = this.provideField(fieldNumber);
                }
                if (relationType != RelationType.NONE && newValue == null && (mmd.isDependent() || mmd.isCascadeRemoveOrphans())) {
                    this.loadField(fieldNumber);
                    loadedOldValue = true;
                    oldValue = this.provideField(fieldNumber);
                }
            }
            boolean equal = false;
            if (oldValue == null && newValue == null) {
                equal = true;
            }
            else if (oldValue != null && newValue != null) {
                if (oldValue instanceof PersistenceCapable) {
                    if (oldValue == newValue) {
                        equal = true;
                    }
                }
                else if (oldValue.equals(newValue)) {
                    equal = true;
                }
            }
            boolean needsSCOUpdating = false;
            if (!this.loadedFields[fieldNumber] || !equal || mmd.hasArray()) {
                if (this.cmd.getIdentityType() == IdentityType.NONDURABLE && relationType == RelationType.NONE) {
                    final String key = "FIELD_VALUE.ORIGINAL." + fieldNumber;
                    if (!this.containsAssociatedValue(key)) {
                        this.setAssociatedValue(key, oldValue);
                    }
                }
                if (oldValue instanceof SCO) {
                    if (oldValue instanceof SCOContainer) {
                        ((SCOContainer)oldValue).load();
                    }
                    ((SCO)oldValue).unsetOwner();
                }
                if (newValue instanceof SCO) {
                    final SCO sco = (SCO)newValue;
                    final Object owner = sco.getOwner();
                    if (owner != null) {
                        throw this.myEC.getApiAdapter().getUserExceptionForException(JDOStateManager.LOCALISER.msg("026007", sco.getFieldName(), owner), null);
                    }
                }
                this.updateField(pc, fieldNumber, newValue);
                if (this.cmd.getSCOMutableMemberFlags()[fieldNumber] && !(newValue instanceof SCO)) {
                    needsSCOUpdating = true;
                }
            }
            else if (loadedOldValue) {
                this.updateField(pc, fieldNumber, newValue);
            }
            if (!equal && RelationType.isBidirectional(relationType) && this.myEC.getManageRelations()) {
                this.myEC.getRelationshipManager(this).relationChange(fieldNumber, oldValue, newValue);
            }
            if (needsSCOUpdating) {
                newValue = this.wrapSCOField(fieldNumber, newValue, false, true, true);
            }
            if (oldValue != null && newValue == null && oldValue instanceof PersistenceCapable && (mmd.isDependent() || mmd.isCascadeRemoveOrphans()) && this.myEC.getApiAdapter().isPersistent(oldValue)) {
                NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026026", oldValue, mmd.getFullFieldName()));
                this.myEC.deleteObjectInternal(oldValue);
            }
            if (!this.myEC.getTransaction().isActive()) {
                this.myEC.processNontransactionalUpdate();
            }
        }
        else {
            this.replaceField(pc, fieldNumber, newValue, true);
        }
    }
    
    protected void updateField(final PersistenceCapable pc, final int fieldNumber, final Object value) {
        final boolean wasDirty = this.dirty;
        if (this.activity != ActivityState.INSERTING && this.activity != ActivityState.INSERTING_CALLBACKS) {
            if (!wasDirty) {
                this.getCallbackHandler().preDirty(this.myPC);
            }
            this.transitionWriteField();
            this.dirty = true;
            this.dirtyFields[fieldNumber] = true;
            this.loadedFields[fieldNumber] = true;
        }
        this.replaceField(pc, fieldNumber, value, true);
        if (this.dirty && !wasDirty) {
            this.getCallbackHandler().postDirty(this.myPC);
        }
        if (this.activity == ActivityState.NONE && !this.isFlushing() && (!this.myLC.isTransactional() || this.myLC.isPersistent())) {
            this.myEC.markDirty(this, true);
        }
    }
    
    @Override
    public boolean replacingBooleanField(final PersistenceCapable pc, final int fieldNumber) {
        final boolean value = this.currFM.fetchBooleanField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public byte replacingByteField(final PersistenceCapable obj, final int fieldNumber) {
        final byte value = this.currFM.fetchByteField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public char replacingCharField(final PersistenceCapable obj, final int fieldNumber) {
        final char value = this.currFM.fetchCharField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public double replacingDoubleField(final PersistenceCapable obj, final int fieldNumber) {
        final double value = this.currFM.fetchDoubleField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public float replacingFloatField(final PersistenceCapable obj, final int fieldNumber) {
        final float value = this.currFM.fetchFloatField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public int replacingIntField(final PersistenceCapable obj, final int fieldNumber) {
        final int value = this.currFM.fetchIntField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public long replacingLongField(final PersistenceCapable obj, final int fieldNumber) {
        final long value = this.currFM.fetchLongField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public short replacingShortField(final PersistenceCapable obj, final int fieldNumber) {
        final short value = this.currFM.fetchShortField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public String replacingStringField(final PersistenceCapable obj, final int fieldNumber) {
        final String value = this.currFM.fetchStringField(fieldNumber);
        this.loadedFields[fieldNumber] = true;
        return value;
    }
    
    @Override
    public Object replacingObjectField(final PersistenceCapable obj, final int fieldNumber) {
        try {
            final Object value = this.currFM.fetchObjectField(fieldNumber);
            this.loadedFields[fieldNumber] = true;
            return value;
        }
        catch (AbstractFetchDepthFieldManager.EndOfFetchPlanGraphException eodge) {
            return null;
        }
    }
    
    protected void replaceField(final PersistenceCapable pc, final int fieldNumber, final Object value) {
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            final FieldManager prevFM = this.currFM;
            this.currFM = new SingleValueFieldManager();
            try {
                this.currFM.storeObjectField(fieldNumber, value);
                pc.jdoReplaceField(fieldNumber);
            }
            finally {
                this.currFM = prevFM;
            }
        }
        finally {
            if (this.myEC.getMultithreaded()) {
                this.lock.unlock();
                this.myEC.getLock().unlock();
            }
        }
    }
    
    protected boolean disconnectClone(final PersistenceCapable pc) {
        if (this.isDetaching()) {
            return false;
        }
        if (pc != this.myPC) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026001", StringUtils.toJVMIDString(pc), this));
            }
            pc.jdoReplaceFlags();
            this.replaceStateManager(pc, null);
            return true;
        }
        return false;
    }
    
    @Override
    public void retrieveDetachState(final ObjectProvider op) {
        if (op.getObject() instanceof Detachable) {
            ((AbstractStateManager)op).setRetrievingDetachedState(true);
            ((Detachable)op.getObject()).jdoReplaceDetachedState();
            ((AbstractStateManager)op).setRetrievingDetachedState(false);
        }
    }
    
    @Override
    public void resetDetachState() {
        if (this.getObject() instanceof Detachable) {
            this.setResettingDetachedState(true);
            try {
                ((Detachable)this.getObject()).jdoReplaceDetachedState();
            }
            finally {
                this.setResettingDetachedState(false);
            }
        }
    }
    
    @Override
    public Object[] replacingDetachedState(final Detachable pc, final Object[] currentState) {
        if (this.isResettingDetachedState()) {
            return null;
        }
        if (this.isRetrievingDetachedState()) {
            final BitSet jdoLoadedFields = (BitSet)currentState[2];
            for (int i = 0; i < this.loadedFields.length; ++i) {
                this.loadedFields[i] = jdoLoadedFields.get(i);
            }
            final BitSet jdoModifiedFields = (BitSet)currentState[3];
            for (int j = 0; j < this.dirtyFields.length; ++j) {
                this.dirtyFields[j] = jdoModifiedFields.get(j);
            }
            this.setVersion(currentState[1]);
            return currentState;
        }
        final Object[] state = { this.myID, this.getVersion(this.myPC), null, null };
        final BitSet loadedState = new BitSet();
        for (int j = 0; j < this.loadedFields.length; ++j) {
            if (this.loadedFields[j]) {
                loadedState.set(j);
            }
            else {
                loadedState.clear(j);
            }
        }
        state[2] = loadedState;
        final BitSet modifiedState = new BitSet();
        for (int k = 0; k < this.dirtyFields.length; ++k) {
            if (this.dirtyFields[k]) {
                modifiedState.set(k);
            }
            else {
                modifiedState.clear(k);
            }
        }
        state[3] = modifiedState;
        return state;
    }
    
    @Override
    public boolean getBooleanField(final PersistenceCapable pc, final int fieldNumber, final boolean currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public byte getByteField(final PersistenceCapable pc, final int fieldNumber, final byte currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public char getCharField(final PersistenceCapable pc, final int fieldNumber, final char currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public double getDoubleField(final PersistenceCapable pc, final int fieldNumber, final double currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public float getFloatField(final PersistenceCapable pc, final int fieldNumber, final float currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public int getIntField(final PersistenceCapable pc, final int fieldNumber, final int currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public long getLongField(final PersistenceCapable pc, final int fieldNumber, final long currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public short getShortField(final PersistenceCapable pc, final int fieldNumber, final short currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public String getStringField(final PersistenceCapable pc, final int fieldNumber, final String currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    public Object getObjectField(final PersistenceCapable pc, final int fieldNumber, final Object currentValue) {
        throw new NucleusException(JDOStateManager.LOCALISER.msg("026006"));
    }
    
    @Override
    @Deprecated
    public void checkInheritance(final FieldValues fv) {
        final ClassLoaderResolver clr = this.myEC.getClassLoaderResolver();
        final String className = this.myEC.getStoreManager().getClassNameForObjectID(this.myID, clr, this.myEC);
        if (className == null) {
            throw new NucleusObjectNotFoundException(JDOStateManager.LOCALISER.msg("026013", IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID)), this.myID);
        }
        if (!this.cmd.getFullClassName().equals(className)) {
            Class pcClass;
            try {
                pcClass = clr.classForName(className, this.myID.getClass().getClassLoader(), true);
                this.cmd = this.myEC.getMetaDataManager().getMetaDataForClass(pcClass, clr);
            }
            catch (ClassNotResolvedException e) {
                NucleusLogger.PERSISTENCE.warn(JDOStateManager.LOCALISER.msg("026014", IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID)));
                throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026014", IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID)), e);
            }
            if (this.cmd == null) {
                throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026012", pcClass)).setFatal();
            }
            if (this.cmd.getIdentityType() != IdentityType.APPLICATION) {
                throw new NucleusUserException("This method should only be used for objects using application identity.").setFatal();
            }
            this.myFP = this.myEC.getFetchPlan().getFetchPlanForClass(this.cmd);
            final int fieldCount = this.cmd.getMemberCount();
            this.dirtyFields = new boolean[fieldCount];
            this.loadedFields = new boolean[fieldCount];
            this.myPC = JDOStateManager.HELPER.newInstance(pcClass, this);
            if (this.myPC == null) {
                throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026018", this.cmd.getFullClassName())).setFatal();
            }
            this.loadFieldValues(fv);
            this.myID = this.myPC.jdoNewObjectIdInstance();
            if (!this.cmd.usesSingleFieldIdentityClass()) {
                this.myPC.jdoCopyKeyFieldsToObjectId(this.myID);
            }
        }
    }
    
    private void populateStrategyFields() {
        for (int totalFieldCount = this.cmd.getNoOfInheritedManagedMembers() + this.cmd.getNoOfManagedMembers(), fieldNumber = 0; fieldNumber < totalFieldCount; ++fieldNumber) {
            final AbstractMemberMetaData mmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            final IdentityStrategy strategy = mmd.getValueStrategy();
            if (strategy != null && !this.myEC.getStoreManager().isStrategyDatastoreAttributed(this.cmd, fieldNumber)) {
                boolean applyStrategy = true;
                if (!mmd.getType().isPrimitive() && mmd.hasExtension("strategy-when-notnull") && mmd.getValueForExtension("strategy-when-notnull").equalsIgnoreCase("false") && this.provideField(fieldNumber) != null) {
                    applyStrategy = false;
                }
                if (applyStrategy) {
                    final Object obj = this.myEC.getStoreManager().getStrategyValue(this.myEC, this.cmd, fieldNumber);
                    this.replaceField(fieldNumber, obj);
                }
            }
            else if (mmd.hasExtension("object-value-generator")) {
                final String valGenName = mmd.getValueForExtension("object-value-generator");
                final ObjectValueGenerator valGen = AbstractStateManager.getObjectValueGenerator(this.myEC, valGenName);
                final Object value = valGen.generate(this.myEC, this.myPC, mmd.getExtensions());
                this.replaceField(this.myPC, fieldNumber, value, true);
            }
        }
    }
    
    @Override
    public void loadFieldValues(final FieldValues fv) {
        final FetchPlanForClass origFetchPlan = this.myFP;
        final FetchPlan loadFetchPlan = fv.getFetchPlanForLoading();
        if (loadFetchPlan != null) {
            this.myFP = loadFetchPlan.getFetchPlanForClass(this.cmd);
        }
        boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
        if (this.loadedFields.length == 0) {
            callPostLoad = true;
        }
        fv.fetchFields(this);
        if (callPostLoad && this.areFieldsLoaded(this.myFP.getMemberNumbers())) {
            this.postLoad();
        }
        this.myFP = origFetchPlan;
    }
    
    private void setIdentity(final boolean afterPreStore) {
        if (this.cmd.isEmbeddedOnly()) {
            return;
        }
        if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
            if (this.cmd.getIdentityMetaData() == null || !this.myEC.getStoreManager().isStrategyDatastoreAttributed(this.cmd, -1)) {
                this.myID = this.myEC.newObjectId(this.cmd.getFullClassName(), this.myPC);
            }
        }
        else if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            boolean idSetInDatastore = false;
            for (int totalFieldCount = this.cmd.getNoOfInheritedManagedMembers() + this.cmd.getNoOfManagedMembers(), fieldNumber = 0; fieldNumber < totalFieldCount; ++fieldNumber) {
                final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
                if (fmd.isPrimaryKey()) {
                    if (this.myEC.getStoreManager().isStrategyDatastoreAttributed(this.cmd, fieldNumber)) {
                        idSetInDatastore = true;
                        break;
                    }
                    if (this.cmd.usesSingleFieldIdentityClass() && this.provideField(fieldNumber) == null) {
                        if (afterPreStore) {
                            throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026017", this.cmd.getFullClassName(), fmd.getName())).setFatal();
                        }
                        NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026017", this.cmd.getFullClassName(), fmd.getName()));
                        return;
                    }
                }
            }
            if (!idSetInDatastore) {
                this.myID = this.myEC.newObjectId(this.cmd.getFullClassName(), this.myPC);
            }
        }
        if (this.myInternalID != this.myID && this.myID != null && this.myEC.getApiAdapter().getIdForObject(this.myPC) != null) {
            this.myEC.replaceObjectId(this.myPC, this.myInternalID, this.myID);
            this.myInternalID = this.myID;
        }
    }
    
    @Override
    public void copyFieldsFromObject(final Object obj, final int[] fieldNumbers) {
        if (obj == null) {
            return;
        }
        if (!obj.getClass().getName().equals(this.myPC.getClass().getName())) {
            return;
        }
        if (!(obj instanceof PersistenceCapable)) {
            throw new NucleusUserException("Must be PersistenceCapable");
        }
        final PersistenceCapable pc = (PersistenceCapable)obj;
        this.replaceStateManager(pc, this);
        this.myPC.jdoCopyFields(pc, fieldNumbers);
        this.replaceStateManager(pc, null);
        for (int i = 0; i < fieldNumbers.length; ++i) {
            this.loadedFields[fieldNumbers[i]] = true;
        }
    }
    
    @Override
    public void makeDirty(final int fieldNumber) {
        if (this.activity != ActivityState.DELETING) {
            final boolean wasDirty = this.preWriteField(fieldNumber);
            this.postWriteField(wasDirty);
            final List<ExecutionContext.EmbeddedOwnerRelation> embeddedOwners = this.myEC.getOwnerInformationForEmbedded(this);
            if (embeddedOwners != null) {
                for (final ExecutionContext.EmbeddedOwnerRelation owner : embeddedOwners) {
                    final AbstractStateManager ownerOP = (AbstractStateManager)owner.getOwnerOP();
                    if (ownerOP != null) {
                        if (ownerOP.getClassMetaData() == null) {
                            continue;
                        }
                        if ((ownerOP.flags & 0x100) != 0x0) {
                            continue;
                        }
                        ownerOP.makeDirty(owner.getOwnerFieldNum());
                    }
                }
            }
        }
    }
    
    @Override
    public void makeDirty(final PersistenceCapable pc, final String fieldName) {
        if (!this.disconnectClone(pc)) {
            final int fieldNumber = this.cmd.getAbsolutePositionOfMember(fieldName);
            if (fieldNumber == -1) {
                throw this.myEC.getApiAdapter().getUserExceptionForException(JDOStateManager.LOCALISER.msg("026002", fieldName, this.cmd.getFullClassName()), null);
            }
            this.makeDirty(fieldNumber);
        }
    }
    
    @Override
    public Object getObjectId(final PersistenceCapable pc) {
        if (this.disconnectClone(pc)) {
            return null;
        }
        try {
            return this.getExternalObjectId(pc);
        }
        catch (NucleusException ne) {
            throw this.myEC.getApiAdapter().getApiExceptionForNucleusException(ne);
        }
    }
    
    @Override
    public Object getTransactionalObjectId(final PersistenceCapable pc) {
        return this.getObjectId(pc);
    }
    
    @Override
    public void setPostStoreNewObjectId(final Object id) {
        if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
            if (id instanceof OID) {
                this.myID = id;
            }
            else {
                this.myID = OIDFactory.getInstance(this.myEC.getNucleusContext(), this.cmd.getFullClassName(), id);
            }
        }
        else if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            try {
                this.myID = null;
                for (int fieldCount = this.cmd.getMemberCount(), fieldNumber = 0; fieldNumber < fieldCount; ++fieldNumber) {
                    final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
                    if (fmd.isPrimaryKey() && this.myEC.getStoreManager().isStrategyDatastoreAttributed(this.cmd, fieldNumber)) {
                        this.replaceField(this.myPC, fieldNumber, TypeConversionHelper.convertTo(id, fmd.getType()), false);
                    }
                }
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.error(e);
            }
            finally {
                this.myID = this.myEC.getApiAdapter().getNewApplicationIdentityObjectId(this.getObject(), this.cmd);
            }
        }
        if (this.myInternalID != this.myID && this.myID != null) {
            this.myEC.replaceObjectId(this.myPC, this.myInternalID, this.myID);
            this.myInternalID = this.myID;
        }
    }
    
    protected Object getExternalObjectId(final Object obj) {
        final List<ExecutionContext.EmbeddedOwnerRelation> embeddedOwners = this.myEC.getOwnerInformationForEmbedded(this);
        if (embeddedOwners != null) {
            return this.myID;
        }
        if (this.cmd.getIdentityType() == IdentityType.DATASTORE) {
            if (!this.isFlushing() && !this.isFlushedNew() && this.activity != ActivityState.INSERTING && this.activity != ActivityState.INSERTING_CALLBACKS && this.myLC.stateType() == 1 && this.myEC.getStoreManager().isStrategyDatastoreAttributed(this.cmd, -1)) {
                this.flush();
            }
        }
        else if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            if (!this.isFlushing() && !this.isFlushedNew() && this.activity != ActivityState.INSERTING && this.activity != ActivityState.INSERTING_CALLBACKS && this.myLC.stateType() == 1) {
                final int[] pkFieldNumbers = this.cmd.getPKMemberPositions();
                for (int i = 0; i < pkFieldNumbers.length; ++i) {
                    if (this.myEC.getStoreManager().isStrategyDatastoreAttributed(this.cmd, pkFieldNumbers[i])) {
                        this.flush();
                        break;
                    }
                }
            }
            if (this.cmd.usesSingleFieldIdentityClass()) {
                return this.myID;
            }
            return this.myEC.getApiAdapter().getNewApplicationIdentityObjectId(this.myPC, this.cmd);
        }
        return this.myID;
    }
    
    @Override
    public Object getExternalObjectId() {
        return this.getExternalObjectId(this.myPC);
    }
    
    protected void loadSpecifiedFields(final int[] fieldNumbers) {
        if (this.myEC.getApiAdapter().isDetached(this.myPC)) {
            return;
        }
        final int[] unloadedFieldNumbers = this.loadFieldsFromLevel2Cache(fieldNumbers);
        if (unloadedFieldNumbers != null && !this.isEmbedded()) {
            this.loadFieldsFromDatastore(unloadedFieldNumbers);
            this.updateLevel2CacheForFields(unloadedFieldNumbers);
        }
    }
    
    @Override
    public void loadField(final int fieldNumber) {
        if (this.loadedFields[fieldNumber]) {
            return;
        }
        this.loadSpecifiedFields(new int[] { fieldNumber });
    }
    
    @Override
    public void loadUnloadedFields() {
        int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getAllMemberPositions(), false);
        if (fieldNumbers == null || fieldNumbers.length == 0) {
            return;
        }
        if (this.preDeleteLoadedFields != null && ((this.myLC.isDeleted() && this.myEC.isFlushing()) || this.activity == ActivityState.DELETING)) {
            fieldNumbers = ClassUtils.getFlagsSetTo(this.preDeleteLoadedFields, fieldNumbers, false);
        }
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
            final int[] unloadedFieldNumbers = this.loadFieldsFromLevel2Cache(fieldNumbers);
            if (unloadedFieldNumbers != null) {
                this.loadFieldsFromDatastore(unloadedFieldNumbers);
            }
            final int[] secondClassMutableFieldNumbers = this.cmd.getSCOMutableMemberPositions();
            for (int i = 0; i < secondClassMutableFieldNumbers.length; ++i) {
                final SingleValueFieldManager sfv = new SingleValueFieldManager();
                this.provideFields(new int[] { secondClassMutableFieldNumbers[i] }, sfv);
                final Object value = sfv.fetchObjectField(i);
                if (value instanceof SCOContainer) {
                    ((SCOContainer)value).load();
                }
            }
            this.updateLevel2CacheForFields(fieldNumbers);
            if (callPostLoad) {
                this.postLoad();
            }
        }
    }
    
    @Override
    public void loadUnloadedFieldsInFetchPlan() {
        final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.myFP.getMemberNumbers(), false);
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
            final int[] unloadedFieldNumbers = this.loadFieldsFromLevel2Cache(fieldNumbers);
            if (unloadedFieldNumbers != null) {
                this.loadFieldsFromDatastore(unloadedFieldNumbers);
                this.updateLevel2CacheForFields(unloadedFieldNumbers);
            }
            if (callPostLoad) {
                this.postLoad();
            }
        }
    }
    
    protected void loadUnloadedFieldsInFetchPlanAndVersion() {
        if (!this.cmd.isVersioned()) {
            this.loadUnloadedFieldsInFetchPlan();
        }
        else {
            int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.myFP.getMemberNumbers(), false);
            if (fieldNumbers == null) {
                fieldNumbers = new int[0];
            }
            final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
            final int[] unloadedFieldNumbers = this.loadFieldsFromLevel2Cache(fieldNumbers);
            if (unloadedFieldNumbers != null) {
                this.loadFieldsFromDatastore(unloadedFieldNumbers);
                this.updateLevel2CacheForFields(unloadedFieldNumbers);
            }
            if (callPostLoad && fieldNumbers.length > 0) {
                this.postLoad();
            }
        }
    }
    
    @Override
    public void loadUnloadedFieldsOfClassInFetchPlan(final FetchPlan fetchPlan) {
        final FetchPlanForClass fpc = fetchPlan.getFetchPlanForClass(this.cmd);
        final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, fpc.getMemberNumbers(), false);
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            final boolean callPostLoad = fpc.isToCallPostLoadFetchPlan(this.loadedFields);
            final int[] unloadedFieldNumbers = this.loadFieldsFromLevel2Cache(fieldNumbers);
            if (unloadedFieldNumbers != null) {
                this.loadFieldsFromDatastore(unloadedFieldNumbers);
                this.updateLevel2CacheForFields(unloadedFieldNumbers);
            }
            if (callPostLoad) {
                this.postLoad();
            }
        }
    }
    
    @Override
    public void refreshFieldsInFetchPlan() {
        final int[] fieldNumbers = this.myFP.getMemberNumbers();
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            this.clearDirtyFlags(fieldNumbers);
            ClassUtils.clearFlags(this.loadedFields, fieldNumbers);
            this.markPKFieldsAsLoaded();
            final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
            this.setTransactionalVersion(null);
            this.loadFieldsFromDatastore(fieldNumbers);
            if (this.cmd.hasRelations(this.myEC.getClassLoaderResolver(), this.myEC.getMetaDataManager())) {
                for (int i = 0; i < fieldNumbers.length; ++i) {
                    final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumbers[i]);
                    final RelationType relationType = fmd.getRelationType(this.myEC.getClassLoaderResolver());
                    if (relationType != RelationType.NONE && fmd.isCascadeRefresh()) {
                        final Object value = this.provideField(fieldNumbers[i]);
                        if (value != null) {
                            if (value instanceof Collection) {
                                SCOUtils.refreshFetchPlanFieldsForCollection(this, ((Collection)value).toArray());
                            }
                            else if (value instanceof Map) {
                                SCOUtils.refreshFetchPlanFieldsForMap(this, ((Map)value).entrySet());
                            }
                            else if (value instanceof PersistenceCapable) {
                                this.myEC.refreshObject(value);
                            }
                        }
                    }
                }
            }
            if (callPostLoad) {
                this.postLoad();
            }
            this.getCallbackHandler().postRefresh(this.myPC);
        }
    }
    
    @Override
    public void refreshLoadedFields() {
        final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.myFP.getMemberNumbers(), true);
        if (fieldNumbers != null && fieldNumbers.length > 0) {
            this.clearDirtyFlags();
            ClassUtils.clearFlags(this.loadedFields);
            this.markPKFieldsAsLoaded();
            final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
            this.loadFieldsFromDatastore(fieldNumbers);
            if (callPostLoad) {
                this.postLoad();
            }
        }
    }
    
    @Override
    public boolean isLoaded(final int fieldNumber) {
        return this.isLoaded(this.myPC, fieldNumber);
    }
    
    @Override
    public boolean isLoaded(final PersistenceCapable pc, final int fieldNumber) {
        try {
            if (this.disconnectClone(pc)) {
                return true;
            }
            boolean checkRead = true;
            boolean beingDeleted = false;
            if ((this.myLC.isDeleted() && this.myEC.isFlushing()) || this.activity == ActivityState.DELETING) {
                checkRead = false;
                beingDeleted = true;
            }
            if (checkRead) {
                this.transitionReadField(this.loadedFields[fieldNumber]);
            }
            if (!this.loadedFields[fieldNumber]) {
                if (this.objectType != 0) {
                    return true;
                }
                if (beingDeleted && this.preDeleteLoadedFields != null && this.preDeleteLoadedFields[fieldNumber]) {
                    return true;
                }
                if (!beingDeleted && this.myFP.hasMember(fieldNumber)) {
                    this.loadUnloadedFieldsInFetchPlan();
                }
                else {
                    this.loadSpecifiedFields(new int[] { fieldNumber });
                }
            }
            return true;
        }
        catch (NucleusException ne) {
            NucleusLogger.PERSISTENCE.warn("Exception thrown by StateManager.isLoaded", ne);
            throw this.myEC.getApiAdapter().getApiExceptionForNucleusException(ne);
        }
    }
    
    @Override
    public void replaceFieldValue(final int fieldNumber, final Object newValue) {
        if (this.myLC.isDeleted()) {
            return;
        }
        final boolean currentWasDirty = this.preWriteField(fieldNumber);
        this.replaceField(this.myPC, fieldNumber, newValue, true);
        this.postWriteField(currentWasDirty);
    }
    
    @Override
    public void replaceField(final int fieldNumber, final Object value) {
        this.replaceField(this.myPC, fieldNumber, value, false);
    }
    
    @Override
    public void replaceFieldMakeDirty(final int fieldNumber, final Object value) {
        this.replaceField(this.myPC, fieldNumber, value, true);
    }
    
    protected void replaceField(final PersistenceCapable pc, final int fieldNumber, final Object value, final boolean makeDirty) {
        final List<ExecutionContext.EmbeddedOwnerRelation> embeddedOwners = this.myEC.getOwnerInformationForEmbedded(this);
        if (embeddedOwners != null) {
            for (final ExecutionContext.EmbeddedOwnerRelation ownerRel : embeddedOwners) {
                final AbstractStateManager ownerOP = (AbstractStateManager)ownerRel.getOwnerOP();
                if (ownerOP != null) {
                    if (ownerOP.getClassMetaData() == null) {
                        continue;
                    }
                    final AbstractMemberMetaData ownerMmd = ownerOP.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(ownerRel.getOwnerFieldNum());
                    if (ownerMmd.getCollection() != null) {
                        final Object ownerField = ownerOP.provideField(ownerRel.getOwnerFieldNum());
                        if (!(ownerField instanceof SCOCollection)) {
                            continue;
                        }
                        ((SCOCollection)ownerField).updateEmbeddedElement(this.myPC, fieldNumber, value);
                    }
                    else if (ownerMmd.getMap() != null) {
                        final Object ownerField = ownerOP.provideField(ownerRel.getOwnerFieldNum());
                        if (!(ownerField instanceof SCOMap)) {
                            continue;
                        }
                        if (this.objectType == 3) {
                            ((SCOMap)ownerField).updateEmbeddedKey(this.myPC, fieldNumber, value);
                        }
                        if (this.objectType != 4) {
                            continue;
                        }
                        ((SCOMap)ownerField).updateEmbeddedValue(this.myPC, fieldNumber, value);
                    }
                    else {
                        if ((ownerOP.flags & 0x100) != 0x0) {
                            continue;
                        }
                        if (makeDirty) {
                            ownerOP.replaceFieldMakeDirty(ownerRel.getOwnerFieldNum(), pc);
                        }
                        else {
                            ownerOP.replaceField(ownerRel.getOwnerFieldNum(), pc);
                        }
                    }
                }
            }
        }
        if (embeddedOwners == null && makeDirty && !this.myLC.isDeleted() && this.myEC.getTransaction().isActive()) {
            final boolean wasDirty = this.preWriteField(fieldNumber);
            this.replaceField(pc, fieldNumber, value);
            this.postWriteField(wasDirty);
        }
        else {
            this.replaceField(pc, fieldNumber, value);
        }
    }
    
    @Override
    public void replaceFields(final int[] fieldNumbers, final FieldManager fm, final boolean replaceWhenDirty) {
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            final FieldManager prevFM = this.currFM;
            this.currFM = fm;
            try {
                int[] fieldsToReplace = fieldNumbers;
                if (!replaceWhenDirty) {
                    int numberToReplace = fieldNumbers.length;
                    for (int i = 0; i < fieldNumbers.length; ++i) {
                        if (this.dirtyFields[fieldNumbers[i]]) {
                            --numberToReplace;
                        }
                    }
                    if (numberToReplace > 0 && numberToReplace != fieldNumbers.length) {
                        fieldsToReplace = new int[numberToReplace];
                        int n = 0;
                        for (int j = 0; j < fieldNumbers.length; ++j) {
                            if (!this.dirtyFields[fieldNumbers[j]]) {
                                fieldsToReplace[n++] = fieldNumbers[j];
                            }
                        }
                    }
                    else if (numberToReplace == 0) {
                        fieldsToReplace = null;
                    }
                }
                if (fieldsToReplace != null) {
                    this.myPC.jdoReplaceFields(fieldsToReplace);
                }
            }
            finally {
                this.currFM = prevFM;
            }
        }
        finally {
            if (this.myEC.getMultithreaded()) {
                this.lock.unlock();
                this.myEC.getLock().unlock();
            }
        }
    }
    
    @Override
    public void replaceFields(final int[] fieldNumbers, final FieldManager fm) {
        this.replaceFields(fieldNumbers, fm, true);
    }
    
    @Override
    public void replaceNonLoadedFields(final int[] fieldNumbers, final FieldManager fm) {
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            final FieldManager prevFM = this.currFM;
            this.currFM = fm;
            final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
            try {
                final int[] fieldsToReplace = ClassUtils.getFlagsSetTo(this.loadedFields, fieldNumbers, false);
                if (fieldsToReplace != null && fieldsToReplace.length > 0) {
                    this.myPC.jdoReplaceFields(fieldsToReplace);
                }
            }
            finally {
                this.currFM = prevFM;
            }
            if (callPostLoad && this.areFieldsLoaded(this.myFP.getMemberNumbers())) {
                this.postLoad();
            }
        }
        finally {
            if (this.myEC.getMultithreaded()) {
                this.lock.unlock();
                this.myEC.getLock().unlock();
            }
        }
    }
    
    @Override
    public void replaceAllLoadedSCOFieldsWithWrappers() {
        final boolean[] scoMutableFieldFlags = this.cmd.getSCOMutableMemberFlags();
        for (int i = 0; i < scoMutableFieldFlags.length; ++i) {
            if (scoMutableFieldFlags[i] && this.loadedFields[i]) {
                final Object value = this.provideField(i);
                if (!(value instanceof SCO)) {
                    this.wrapSCOField(i, value, false, false, true);
                }
            }
        }
    }
    
    @Override
    public void replaceAllLoadedSCOFieldsWithValues() {
        final boolean[] scoMutableFieldFlags = this.cmd.getSCOMutableMemberFlags();
        for (int i = 0; i < scoMutableFieldFlags.length; ++i) {
            if (scoMutableFieldFlags[i] && this.loadedFields[i]) {
                final Object value = this.provideField(i);
                if (value instanceof SCO) {
                    this.unwrapSCOField(i, value, true);
                }
            }
        }
    }
    
    @Override
    public Object unwrapSCOField(final int fieldNumber, final Object value, final boolean replaceFieldIfChanged) {
        if (value == null) {
            return value;
        }
        if (this.cmd.getSCOMutableMemberFlags()[fieldNumber] && value instanceof SCO) {
            final SCO sco = (SCO)value;
            final Object unwrappedValue = sco.getValue();
            if (replaceFieldIfChanged) {
                final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026030", StringUtils.toJVMIDString(this.myPC), IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID), fmd.getName()));
                }
                this.replaceField(this.myPC, fieldNumber, unwrappedValue, false);
            }
            return unwrappedValue;
        }
        return value;
    }
    
    @Override
    public Object wrapSCOField(final int fieldNumber, final Object value, final boolean forInsert, final boolean forUpdate, final boolean replaceFieldIfChanged) {
        if (value == null) {
            return value;
        }
        if (value instanceof PersistenceCapable) {
            final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            if (fmd.getEmbeddedMetaData() != null && fmd.getEmbeddedMetaData().getOwnerMember() != null) {
                final AbstractStateManager subSM = (AbstractStateManager)this.myEC.findObjectProvider(value);
                final int ownerAbsFieldNum = subSM.getClassMetaData().getAbsolutePositionOfMember(fmd.getEmbeddedMetaData().getOwnerMember());
                if (ownerAbsFieldNum >= 0) {
                    this.flags |= 0x100;
                    subSM.replaceFieldMakeDirty(ownerAbsFieldNum, this.myPC);
                    this.flags &= 0xFFFFFEFF;
                }
            }
        }
        if (this.cmd.getSCOMutableMemberFlags()[fieldNumber] && (!(value instanceof SCO) || this.myPC != ((SCO)value).getOwner())) {
            final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            if (replaceFieldIfChanged && NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026029", StringUtils.toJVMIDString(this.myPC), (this.myEC != null) ? IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID) : this.myID, fmd.getName()));
            }
            return SCOUtils.newSCOInstance(this, fmd, fmd.getType(), value.getClass(), value, forInsert, forUpdate, replaceFieldIfChanged);
        }
        return value;
    }
    
    @Override
    public void runReachability(final Set reachables) {
        if (reachables == null) {
            return;
        }
        if (!reachables.contains(this.getInternalObjectId())) {
            this.flush();
            if (this.isDeleted(this.myPC)) {
                return;
            }
            if (this.myEC.isEnlistedInTransaction(this.getInternalObjectId())) {
                this.loadUnloadedFields();
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("007000", StringUtils.toJVMIDString(this.myPC), this.getInternalObjectId(), this.myLC));
            }
            reachables.add(this.getInternalObjectId());
            final int[] loadedFieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getAllMemberPositions(), true);
            if (loadedFieldNumbers != null && loadedFieldNumbers.length > 0) {
                this.provideFields(loadedFieldNumbers, new ReachabilityFieldManager(this, reachables));
            }
        }
    }
    
    @Override
    public void makePersistent() {
        if (this.myLC.isDeleted() && !this.myEC.getNucleusContext().getApiAdapter().allowPersistOfDeletedObject()) {
            return;
        }
        if (this.activity != ActivityState.NONE) {
            return;
        }
        if (this.dirty && !this.myLC.isDeleted() && this.myLC.isTransactional() && this.myEC.isDelayDatastoreOperationsEnabled()) {
            if (this.cmd.hasRelations(this.myEC.getClassLoaderResolver(), this.myEC.getMetaDataManager())) {
                this.provideFields(this.cmd.getAllMemberPositions(), new PersistFieldManager(this, false));
            }
            return;
        }
        this.getCallbackHandler().prePersist(this.myPC);
        if (this.isFlushedNew()) {
            this.registerTransactional();
            return;
        }
        if (this.cmd.isEmbeddedOnly()) {
            return;
        }
        if (this.myID == null) {
            this.setIdentity(false);
        }
        this.dirty = true;
        if (this.myEC.isDelayDatastoreOperationsEnabled()) {
            this.myEC.markDirty(this, false);
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026028", StringUtils.toJVMIDString(this.myPC)));
            }
            this.registerTransactional();
            if (this.myLC.isTransactional() && this.myLC.isDeleted()) {
                this.myLC = this.myLC.transitionMakePersistent(this);
            }
            if (this.cmd.hasRelations(this.myEC.getClassLoaderResolver(), this.myEC.getMetaDataManager())) {
                this.provideFields(this.cmd.getAllMemberPositions(), new PersistFieldManager(this, false));
            }
        }
        else {
            this.internalMakePersistent();
            this.registerTransactional();
        }
    }
    
    private void internalMakePersistent() {
        this.activity = ActivityState.INSERTING;
        final boolean[] tmpDirtyFields = this.dirtyFields.clone();
        try {
            this.getCallbackHandler().preStore(this.myPC);
            if (this.myID == null) {
                this.setIdentity(true);
            }
            this.clearDirtyFlags();
            this.myEC.getStoreManager().getPersistenceHandler().insertObject(this);
            this.setFlushedNew(true);
            this.getCallbackHandler().postStore(this.myPC);
            if (!this.isEmbedded()) {
                this.myEC.putObjectIntoLevel1Cache(this);
            }
        }
        catch (NotYetFlushedException ex) {
            this.dirtyFields = tmpDirtyFields;
            this.myEC.markDirty(this, false);
            this.dirty = true;
            throw ex;
        }
        finally {
            this.activity = ActivityState.NONE;
        }
    }
    
    @Override
    public void makeTransactional() {
        this.preStateChange();
        try {
            if (this.myLC == null) {
                final JDOStateManager thisSM = this;
                this.myLC = this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(5);
                try {
                    if (this.myLC.isPersistent()) {
                        this.myEC.addObjectProvider(this);
                    }
                    this.replaceStateManager(this.myPC, thisSM);
                }
                catch (SecurityException e) {
                    throw new NucleusUserException(e.getMessage());
                }
                catch (NucleusException ne) {
                    if (this.myEC.findObjectProvider(this.myEC.getObjectFromCache(this.myID)) == this) {
                        this.myEC.removeObjectProvider(this);
                    }
                    throw ne;
                }
                this.restoreValues = true;
            }
            else {
                this.myLC = this.myLC.transitionMakeTransactional(this, true);
            }
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void makeTransient(final FetchPlanState state) {
        if (this.isMakingTransient()) {
            return;
        }
        try {
            this.setMakingTransient(true);
            if (state == null) {
                final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getSCOMutableMemberPositions(), true);
                if (fieldNumbers != null && fieldNumbers.length > 0) {
                    this.provideFields(fieldNumbers, new UnsetOwnerFieldManager());
                }
            }
            else {
                this.loadUnloadedFieldsInFetchPlan();
                final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getAllMemberPositions(), true);
                if (fieldNumbers != null && fieldNumbers.length > 0) {
                    this.replaceFields(fieldNumbers, new MakeTransientFieldManager(this, this.cmd.getSCOMutableMemberFlags(), this.myFP, state));
                }
            }
            this.preStateChange();
            try {
                this.myLC = this.myLC.transitionMakeTransient(this, state != null, this.myEC.isRunningDetachAllOnCommit());
            }
            finally {
                this.postStateChange();
            }
        }
        finally {
            this.setMakingTransient(false);
        }
    }
    
    @Override
    public void detach(final FetchPlanState state) {
        if (this.myEC == null) {
            return;
        }
        final ApiAdapter api = this.myEC.getApiAdapter();
        if (this.myLC.isDeleted() || api.isDetached(this.myPC) || this.isDetaching()) {
            return;
        }
        final boolean detachable = api.isDetachable(this.myPC);
        if (detachable) {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("010009", StringUtils.toJVMIDString(this.myPC), "" + state.getCurrentFetchDepth()));
            }
            this.getCallbackHandler().preDetach(this.myPC);
        }
        try {
            this.setDetaching(true);
            final String detachedState = this.myEC.getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.detachedState");
            if (detachedState.equalsIgnoreCase("all")) {
                this.loadUnloadedFields();
            }
            else if (!detachedState.equalsIgnoreCase("loaded")) {
                if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x1) != 0x0) {
                    this.loadUnloadedFieldsInFetchPlan();
                }
                if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x2) != 0x0) {
                    this.unloadNonFetchPlanFields();
                    final int[] unloadedFields = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getAllMemberPositions(), false);
                    if (unloadedFields != null && unloadedFields.length > 0) {
                        final PersistenceCapable dummyPC = this.myPC.jdoNewInstance(this);
                        this.myPC.jdoCopyFields(dummyPC, unloadedFields);
                        this.replaceStateManager(dummyPC, null);
                    }
                }
            }
            final FieldManager detachFieldManager = new DetachFieldManager(this, this.cmd.getSCOMutableMemberFlags(), this.myFP, state, false);
            for (int i = 0; i < this.loadedFields.length; ++i) {
                if (this.loadedFields[i]) {
                    try {
                        detachFieldManager.fetchObjectField(i);
                    }
                    catch (AbstractFetchDepthFieldManager.EndOfFetchPlanGraphException eofpge) {
                        final Object value = this.provideField(i);
                        if (api.isPersistable(value)) {
                            final JDOStateManager valueSM = (JDOStateManager)this.myEC.findObjectProvider(value);
                            if (!api.isDetached(value) && (valueSM == null || !valueSM.isDetaching())) {
                                final String fieldName = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(i).getName();
                                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                                    NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("026032", StringUtils.toJVMIDString(this.myPC), IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID), fieldName));
                                }
                                this.unloadField(fieldName);
                            }
                        }
                    }
                }
            }
            if (detachable) {
                this.myLC = this.myLC.transitionDetach(this);
                this.myPC.jdoReplaceFlags();
                ((Detachable)this.myPC).jdoReplaceDetachedState();
                this.getCallbackHandler().postDetach(this.myPC, this.myPC);
                final PersistenceCapable toCheckPC = this.myPC;
                final Object toCheckID = this.myID;
                this.disconnect();
                if (!toCheckPC.jdoIsDetached()) {
                    throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026025", toCheckPC.getClass().getName(), toCheckID));
                }
            }
            else {
                NucleusLogger.PERSISTENCE.warn(JDOStateManager.LOCALISER.msg("026031", this.myPC.getClass().getName(), IdentityUtils.getIdentityAsString(this.myEC.getApiAdapter(), this.myID)));
                this.makeTransient(null);
            }
        }
        finally {
            this.setDetaching(false);
        }
    }
    
    @Override
    public Object detachCopy(final FetchPlanState state) {
        if (this.myLC.isDeleted()) {
            throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026023", this.myPC.getClass().getName(), this.myID));
        }
        if (this.myEC.getApiAdapter().isDetached(this.myPC)) {
            throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026024", this.myPC.getClass().getName(), this.myID));
        }
        if (this.dirty) {
            this.myEC.flushInternal(false);
        }
        if (this.isDetaching()) {
            return this.getReferencedPC();
        }
        final DetachState detachState = (DetachState)state;
        final DetachState.Entry existingDetached = detachState.getDetachedCopyEntry(this.myPC);
        PersistenceCapable detachedPC;
        if (existingDetached == null) {
            detachedPC = this.myPC.jdoNewInstance(this);
            detachState.setDetachedCopyEntry(this.myPC, detachedPC);
        }
        else {
            detachedPC = (PersistenceCapable)existingDetached.getDetachedCopyObject();
            if (existingDetached.checkCurrentState()) {
                return detachedPC;
            }
        }
        this.myEC.setAttachDetachReferencedObject(this, detachedPC);
        final boolean detachable = this.myEC.getApiAdapter().isDetachable(this.myPC);
        Object referencedPC = this.getReferencedPC();
        synchronized (referencedPC) {
            final int[] detachFieldNums = this.getFieldsNumbersToDetach();
            if (detachable) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    int[] fieldsToLoad = null;
                    if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x1) != 0x0) {
                        fieldsToLoad = ClassUtils.getFlagsSetTo(this.loadedFields, this.myFP.getMemberNumbers(), false);
                    }
                    NucleusLogger.PERSISTENCE.debug(JDOStateManager.LOCALISER.msg("010010", StringUtils.toJVMIDString(this.myPC), "" + state.getCurrentFetchDepth(), StringUtils.toJVMIDString(detachedPC), StringUtils.intArrayToString(detachFieldNums), StringUtils.intArrayToString(fieldsToLoad)));
                }
                this.getCallbackHandler().preDetach(this.myPC);
            }
            try {
                this.setDetaching(true);
                if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x1) != 0x0) {
                    this.loadUnloadedFieldsInFetchPlan();
                }
                if (this.myLC == this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(4) || this.myLC == this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(9)) {
                    this.myLC = this.myLC.transitionReadField(this, true);
                }
                final JDOStateManager smDetachedPC = new JDOStateManager(this.myEC, this.cmd);
                smDetachedPC.initialiseForDetached(detachedPC, this.getExternalObjectId(this.myPC), this.getVersion(this.myPC));
                this.myEC.setAttachDetachReferencedObject(smDetachedPC, this.myPC);
                if (existingDetached != null) {
                    smDetachedPC.retrieveDetachState(smDetachedPC);
                }
                smDetachedPC.replaceFields(detachFieldNums, new DetachFieldManager(this, this.cmd.getSCOMutableMemberFlags(), this.myFP, state, true));
                this.myEC.setAttachDetachReferencedObject(smDetachedPC, null);
                if (detachable) {
                    detachedPC.jdoReplaceFlags();
                    ((Detachable)detachedPC).jdoReplaceDetachedState();
                }
                else {
                    smDetachedPC.makeTransient(null);
                }
                this.replaceStateManager(detachedPC, null);
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.warn("DETACH ERROR : Error thrown while detaching " + StringUtils.toJVMIDString(this.myPC) + " (id=" + this.myID + "). Provide a testcase that demonstrates this", e);
            }
            finally {
                this.setDetaching(false);
                referencedPC = null;
            }
            if (detachable && !this.myEC.getApiAdapter().isDetached(detachedPC)) {
                throw new NucleusUserException(JDOStateManager.LOCALISER.msg("026025", detachedPC.getClass().getName(), this.myID));
            }
            if (detachable) {
                this.getCallbackHandler().postDetach(this.myPC, detachedPC);
            }
        }
        return detachedPC;
    }
    
    private int[] getFieldsNumbersToDetach() {
        final String detachedState = this.myEC.getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.detachedState");
        if (detachedState.equalsIgnoreCase("all")) {
            return this.cmd.getAllMemberPositions();
        }
        if (detachedState.equalsIgnoreCase("loaded")) {
            return this.getLoadedFieldNumbers();
        }
        if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x2) == 0x0) {
            if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x1) == 0x0) {
                return this.getLoadedFieldNumbers();
            }
            int[] fieldsToDetach = this.myFP.getMemberNumbers();
            final int[] allFieldNumbers = this.cmd.getAllMemberPositions();
            final int[] loadedFieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, allFieldNumbers, true);
            if (loadedFieldNumbers != null && loadedFieldNumbers.length > 0) {
                final boolean[] flds = new boolean[allFieldNumbers.length];
                for (int i = 0; i < fieldsToDetach.length; ++i) {
                    flds[fieldsToDetach[i]] = true;
                }
                for (int i = 0; i < loadedFieldNumbers.length; ++i) {
                    flds[loadedFieldNumbers[i]] = true;
                }
                fieldsToDetach = ClassUtils.getFlagsSetTo(flds, true);
            }
            return fieldsToDetach;
        }
        else {
            if ((this.myEC.getFetchPlan().getDetachmentOptions() & 0x1) == 0x0) {
                return ClassUtils.getFlagsSetTo(this.loadedFields, this.myFP.getMemberNumbers(), true);
            }
            return this.myFP.getMemberNumbers();
        }
    }
    
    @Override
    public Object getReferencedPC() {
        return this.myEC.getAttachDetachReferencedObject(this);
    }
    
    @Override
    public void attach(final Object trans) {
        if (this.isAttaching()) {
            return;
        }
        this.setAttaching(true);
        try {
            this.getCallbackHandler().preAttach(this.myPC);
            final JDOStateManager detachedSM = new JDOStateManager(this.myEC, this.cmd);
            detachedSM.initialiseForDetached(trans, this.myID, null);
            this.myEC.putObjectIntoLevel1Cache(this);
            final int[] nonPKFieldNumbers = this.cmd.getNonPKMemberPositions();
            if (nonPKFieldNumbers != null && nonPKFieldNumbers.length > 0) {
                NucleusLogger.GENERAL.debug("Attaching id=" + this.getInternalObjectId() + " fields=" + StringUtils.intArrayToString(nonPKFieldNumbers));
                detachedSM.provideFields(nonPKFieldNumbers, new AttachFieldManager(this, this.cmd.getSCOMutableMemberFlags(), this.cmd.getNonPKMemberFlags(), true, true, false));
            }
            this.replaceStateManager((PersistenceCapable)trans, null);
            this.getCallbackHandler().postAttach(this.myPC, this.myPC);
        }
        finally {
            this.setAttaching(false);
        }
    }
    
    @Override
    public void attach(final boolean embedded) {
        if (this.isAttaching()) {
            return;
        }
        this.setAttaching(true);
        try {
            boolean persistent = false;
            if (embedded) {
                persistent = true;
            }
            else if (!this.myEC.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.attachSameDatastore")) {
                try {
                    this.locate();
                    persistent = true;
                }
                catch (NucleusObjectNotFoundException onfe) {}
            }
            else {
                persistent = true;
            }
            this.getCallbackHandler().preAttach(this.myPC);
            this.replaceStateManager(this.myPC, this);
            this.retrieveDetachState(this);
            if (!persistent) {
                this.makePersistent();
            }
            this.myLC = this.myLC.transitionAttach(this);
            this.myEC.putObjectIntoLevel1Cache(this);
            final int[] attachFieldNumbers = this.getFieldNumbersOfLoadedOrDirtyFields(this.loadedFields, this.dirtyFields);
            if (attachFieldNumbers != null) {
                NucleusLogger.GENERAL.debug("Attaching id=" + this.getInternalObjectId() + " fields=" + StringUtils.intArrayToString(attachFieldNumbers));
                this.provideFields(attachFieldNumbers, new AttachFieldManager(this, this.cmd.getSCOMutableMemberFlags(), this.dirtyFields, persistent, true, false));
            }
            this.getCallbackHandler().postAttach(this.myPC, this.myPC);
        }
        finally {
            this.setAttaching(false);
        }
    }
    
    @Override
    public Object attachCopy(final Object obj, final boolean embedded) {
        if (this.isAttaching()) {
            return this.myPC;
        }
        this.setAttaching(true);
        final PersistenceCapable detachedPC = (PersistenceCapable)obj;
        try {
            boolean persistent = false;
            if (embedded) {
                persistent = true;
            }
            else if (!this.myEC.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.attachSameDatastore")) {
                try {
                    this.locate();
                    persistent = true;
                }
                catch (NucleusObjectNotFoundException onfe) {}
            }
            else {
                persistent = true;
            }
            this.getCallbackHandler().preAttach(detachedPC);
            if (this.myEC.getApiAdapter().isDeleted(detachedPC)) {
                this.myLC = this.myLC.transitionDeletePersistent(this);
            }
            if (!this.myEC.getTransaction().getOptimistic() && (this.myLC == this.myEC.getApiAdapter().getLifeCycleState(4) || this.myLC == this.myEC.getApiAdapter().getLifeCycleState(9))) {
                this.myLC = this.myLC.transitionMakeTransactional(this, persistent);
            }
            JDOStateManager smDetachedPC = null;
            if (persistent) {
                final int[] noncontainerFieldNumbers = this.cmd.getSCONonContainerMemberPositions();
                final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, noncontainerFieldNumbers, false);
                if (fieldNumbers != null && fieldNumbers.length > 0) {
                    final int[] unloadedFieldNumbers = this.loadFieldsFromLevel2Cache(fieldNumbers);
                    if (unloadedFieldNumbers != null) {
                        this.loadFieldsFromDatastore(unloadedFieldNumbers);
                        this.updateLevel2CacheForFields(unloadedFieldNumbers);
                    }
                }
                smDetachedPC = new JDOStateManager(this.myEC, this.cmd);
                smDetachedPC.initialiseForDetached(detachedPC, this.getExternalObjectId(detachedPC), null);
                this.myEC.setAttachDetachReferencedObject(smDetachedPC, this.myPC);
                this.myEC.setAttachDetachReferencedObject(this, detachedPC);
                this.retrieveDetachState(smDetachedPC);
            }
            else {
                this.replaceStateManager(detachedPC, this);
                this.myPC.jdoCopyFields(detachedPC, this.cmd.getAllMemberPositions());
                this.replaceStateManager(detachedPC, null);
                smDetachedPC = new JDOStateManager(this.myEC, this.cmd);
                smDetachedPC.initialiseForDetached(detachedPC, this.getExternalObjectId(detachedPC), null);
                this.myEC.setAttachDetachReferencedObject(smDetachedPC, this.myPC);
                this.myEC.setAttachDetachReferencedObject(this, detachedPC);
                this.retrieveDetachState(smDetachedPC);
                this.internalAttachCopy(smDetachedPC, smDetachedPC.loadedFields, smDetachedPC.dirtyFields, persistent, smDetachedPC.myVersion, false);
                this.makePersistent();
            }
            this.internalAttachCopy(smDetachedPC, smDetachedPC.loadedFields, smDetachedPC.dirtyFields, persistent, smDetachedPC.myVersion, true);
            this.replaceStateManager(detachedPC, null);
            this.myEC.setAttachDetachReferencedObject(smDetachedPC, null);
            this.myEC.setAttachDetachReferencedObject(this, null);
            this.getCallbackHandler().postAttach(this.myPC, detachedPC);
        }
        catch (NucleusException ne) {
            NucleusLogger.PERSISTENCE.debug("Unexpected exception thrown in attach", ne);
            throw ne;
        }
        finally {
            this.setAttaching(false);
        }
        return this.myPC;
    }
    
    private void internalAttachCopy(final ObjectProvider detachedOP, final boolean[] loadedFields, final boolean[] dirtyFields, final boolean persistent, final Object version, final boolean cascade) {
        final int[] attachFieldNumbers = this.getFieldNumbersOfLoadedOrDirtyFields(loadedFields, dirtyFields);
        this.setVersion(version);
        if (attachFieldNumbers != null) {
            NucleusLogger.GENERAL.debug("Attaching id=" + this.getInternalObjectId() + " fields=" + StringUtils.intArrayToString(attachFieldNumbers));
            detachedOP.provideFields(attachFieldNumbers, new AttachFieldManager(this, this.cmd.getSCOMutableMemberFlags(), dirtyFields, persistent, cascade, true));
        }
    }
    
    @Override
    public void deletePersistent() {
        if (!this.myLC.isDeleted()) {
            if (this.myEC.isDelayDatastoreOperationsEnabled()) {
                this.getCallbackHandler().preDelete(this.myPC);
                this.myEC.markDirty(this, false);
                if (this.myLC.stateType() == 2 || this.myLC.stateType() == 3 || this.myLC.stateType() == 4 || this.myLC.stateType() == 9 || this.myLC.stateType() == 10) {
                    this.loadUnloadedFields();
                }
                this.setBecomingDeleted(true);
                if (this.cmd.hasRelations(this.myEC.getClassLoaderResolver(), this.myEC.getMetaDataManager())) {
                    this.provideFields(this.cmd.getAllMemberPositions(), new DeleteFieldManager(this));
                }
                this.dirty = true;
                this.preStateChange();
                try {
                    this.preDeleteLoadedFields = new boolean[this.loadedFields.length];
                    for (int i = 0; i < this.preDeleteLoadedFields.length; ++i) {
                        this.preDeleteLoadedFields[i] = this.loadedFields[i];
                    }
                    this.myLC = this.myLC.transitionDeletePersistent(this);
                }
                finally {
                    this.setBecomingDeleted(false);
                    this.postStateChange();
                }
            }
            else {
                this.getCallbackHandler().preDelete(this.myPC);
                this.dirty = true;
                this.preStateChange();
                try {
                    this.preDeleteLoadedFields = new boolean[this.loadedFields.length];
                    for (int i = 0; i < this.preDeleteLoadedFields.length; ++i) {
                        this.preDeleteLoadedFields[i] = this.loadedFields[i];
                    }
                    this.myLC = this.myLC.transitionDeletePersistent(this);
                }
                finally {
                    this.postStateChange();
                }
                this.internalDeletePersistent();
                this.getCallbackHandler().postDelete(this.myPC);
            }
        }
    }
    
    @Override
    public void nullifyFields() {
        if (!this.myLC.isDeleted() && !this.myEC.getApiAdapter().isDetached(this.myPC)) {
            this.replaceFields(this.cmd.getNonPKMemberPositions(), new NullifyRelationFieldManager(this));
            this.flush();
        }
    }
    
    @Override
    public void validate() {
        if (!this.myLC.isTransactional()) {
            int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.myFP.getMemberNumbers(), false);
            if (fieldNumbers != null && fieldNumbers.length > 0) {
                fieldNumbers = ClassUtils.getFlagsSetTo(this.cmd.getNonPKMemberFlags(), fieldNumbers, true);
            }
            if (fieldNumbers != null && fieldNumbers.length > 0) {
                fieldNumbers = ClassUtils.getFlagsSetTo(this.cmd.getSCOMutableMemberFlags(), fieldNumbers, false);
            }
            boolean versionNeedsLoading = false;
            if (this.cmd.isVersioned() && this.transactionalVersion == null) {
                versionNeedsLoading = true;
            }
            if ((fieldNumbers != null && fieldNumbers.length > 0) || versionNeedsLoading) {
                this.transitionReadField(false);
                fieldNumbers = this.myFP.getMemberNumbers();
                if (fieldNumbers != null || versionNeedsLoading) {
                    final boolean callPostLoad = this.myFP.isToCallPostLoadFetchPlan(this.loadedFields);
                    this.setTransactionalVersion(null);
                    this.loadFieldsFromDatastore(fieldNumbers);
                    if (callPostLoad) {
                        this.postLoad();
                    }
                }
            }
            else {
                this.locate();
                this.transitionReadField(false);
            }
        }
    }
    
    protected boolean preWriteField(final int fieldNumber) {
        final boolean wasDirty = this.dirty;
        if (this.activity != ActivityState.INSERTING && this.activity != ActivityState.INSERTING_CALLBACKS) {
            if (!wasDirty) {
                this.getCallbackHandler().preDirty(this.myPC);
            }
            this.transitionWriteField();
            this.dirty = true;
            this.dirtyFields[fieldNumber] = true;
            this.loadedFields[fieldNumber] = true;
        }
        return wasDirty;
    }
    
    protected void postWriteField(final boolean wasDirty) {
        if (this.dirty && !wasDirty) {
            this.getCallbackHandler().postDirty(this.myPC);
        }
        if (this.activity == ActivityState.NONE && !this.isFlushing() && (!this.myLC.isTransactional() || this.myLC.isPersistent())) {
            if (this.isDetaching() && this.getReferencedPC() == null) {
                return;
            }
            this.myEC.markDirty(this, true);
        }
    }
    
    @Override
    protected void postStateChange() {
        this.flags &= 0xFFFFF7FF;
        if (this.isPostLoadPending() && this.areFieldsLoaded(this.myFP.getMemberNumbers())) {
            this.setPostLoadPending(false);
            this.postLoad();
        }
    }
    
    private void postLoad() {
        if (this.isChangingState()) {
            this.setPostLoadPending(true);
        }
        else {
            if (this.persistenceFlags == 1 && this.myLC.isTransactional()) {
                this.persistenceFlags = -1;
                this.myPC.jdoReplaceFlags();
            }
            this.getCallbackHandler().postLoad(this.myPC);
        }
    }
    
    @Override
    public void preSerialize(final PersistenceCapable pc) {
        if (this.disconnectClone(pc)) {
            return;
        }
        this.retrieve(false);
        this.myLC = this.myLC.transitionSerialize(this);
        if (!this.isStoringPC() && pc instanceof Detachable && !this.myLC.isDeleted() && this.myLC.isPersistent()) {
            if (this.myLC.isDirty()) {
                this.flush();
            }
            ((Detachable)pc).jdoReplaceDetachedState();
        }
    }
    
    @Override
    public void flush() {
        if (this.dirty) {
            if (this.isFlushing()) {
                return;
            }
            if (this.activity == ActivityState.INSERTING || this.activity == ActivityState.INSERTING_CALLBACKS) {
                return;
            }
            this.setFlushing(true);
            try {
                if (this.myLC.stateType() == 1 && !this.isFlushedNew()) {
                    if (!this.isEmbedded()) {
                        this.internalMakePersistent();
                    }
                    else {
                        this.getCallbackHandler().preStore(this.myPC);
                        if (this.myID == null) {
                            this.setIdentity(true);
                        }
                        this.getCallbackHandler().postStore(this.myPC);
                    }
                    this.dirty = false;
                }
                else if (this.myLC.stateType() == 8) {
                    this.getCallbackHandler().preDelete(this.myPC);
                    if (!this.isEmbedded()) {
                        this.internalDeletePersistent();
                    }
                    this.getCallbackHandler().postDelete(this.myPC);
                }
                else if (this.myLC.stateType() == 7) {
                    if (this.isFlushedNew()) {
                        this.getCallbackHandler().preDelete(this.myPC);
                        if (!this.isEmbedded()) {
                            this.internalDeletePersistent();
                        }
                        this.setFlushedNew(false);
                        this.getCallbackHandler().postDelete(this.myPC);
                    }
                    else {
                        this.dirty = false;
                    }
                }
                else {
                    if (!this.isDeleting()) {
                        this.getCallbackHandler().preStore(this.myPC);
                        if (this.myID == null) {
                            this.setIdentity(true);
                        }
                    }
                    if (!this.isEmbedded()) {
                        final int[] dirtyFieldNumbers = ClassUtils.getFlagsSetTo(this.dirtyFields, true);
                        if (!this.isEmbedded() && dirtyFieldNumbers == null) {
                            throw new NucleusException(JDOStateManager.LOCALISER.msg("026010")).setFatal();
                        }
                        if (this.myEC.getNucleusContext().isClassCacheable(this.getClassMetaData())) {
                            this.myEC.markFieldsForUpdateInLevel2Cache(this.getInternalObjectId(), this.dirtyFields);
                        }
                        this.myEC.getStoreManager().getPersistenceHandler().updateObject(this, dirtyFieldNumbers);
                        this.myEC.putObjectIntoLevel1Cache(this);
                    }
                    this.clearDirtyFlags();
                    this.getCallbackHandler().postStore(this.myPC);
                }
            }
            finally {
                this.setFlushing(false);
            }
        }
    }
    
    @Override
    public void registerTransactional() {
        this.myEC.addObjectProvider(this);
    }
    
    private static void dumpPC(final Object pc, final PrintWriter out) {
        out.println(StringUtils.toJVMIDString(pc));
        if (pc == null) {
            return;
        }
        out.print("jdoStateManager = " + peekField(pc, "jdoStateManager"));
        out.print("jdoFlags = ");
        final Object flagsObj = peekField(pc, "jdoFlags");
        if (flagsObj instanceof Byte) {
            out.println(PersistenceFlags.persistenceFlagsToString((byte)flagsObj));
        }
        else {
            out.println(flagsObj);
        }
        Class c = pc.getClass();
        do {
            final String[] fieldNames = JDOStateManager.HELPER.getFieldNames(c);
            for (int i = 0; i < fieldNames.length; ++i) {
                out.print(fieldNames[i]);
                out.print(" = ");
                out.println(peekField(pc, fieldNames[i]));
            }
            c = c.getSuperclass();
        } while (c != null && PersistenceCapable.class.isAssignableFrom(c));
    }
    
    public void dump(final PrintWriter out) {
        out.println("myEC = " + this.myEC);
        out.println("myID = " + this.myID);
        out.println("myLC = " + this.myLC);
        out.println("cmd = " + this.cmd);
        out.println("fieldCount = " + this.cmd.getMemberCount());
        out.println("dirty = " + this.dirty);
        out.println("flushing = " + this.isFlushing());
        out.println("changingState = " + this.isChangingState());
        out.println("postLoadPending = " + this.isPostLoadPending());
        out.println("disconnecting = " + this.isDisconnecting());
        out.println("dirtyFields = " + StringUtils.booleanArrayToString(this.dirtyFields));
        out.println("getSecondClassMutableFields() = " + StringUtils.booleanArrayToString(this.cmd.getSCOMutableMemberFlags()));
        out.println("getAllFieldNumbers() = " + StringUtils.intArrayToString(this.cmd.getAllMemberPositions()));
        out.println("secondClassMutableFieldNumbers = " + StringUtils.intArrayToString(this.cmd.getSCOMutableMemberPositions()));
        out.println();
        out.println("persistenceFlags = " + PersistenceFlags.persistenceFlagsToString(this.persistenceFlags));
        out.println("loadedFields = " + StringUtils.booleanArrayToString(this.loadedFields));
        out.print("myPC = ");
        dumpPC(this.myPC, out);
        out.println();
        out.println("savedFlags = " + PersistenceFlags.persistenceFlagsToString(this.savedFlags));
        out.println("savedLoadedFields = " + StringUtils.booleanArrayToString(this.savedLoadedFields));
        out.print("savedImage = ");
        dumpPC(this.savedImage, out);
    }
    
    protected static Object peekField(final Object obj, final String fieldName) {
        try {
            final Object value = obj.getClass().getDeclaredField(fieldName).get(obj);
            if (value instanceof PersistenceCapable) {
                return StringUtils.toJVMIDString(value);
            }
            return value;
        }
        catch (Exception e) {
            return e.toString();
        }
    }
    
    @Override
    public ObjectProvider[] getEmbeddedOwners() {
        final List<ExecutionContext.EmbeddedOwnerRelation> ownerRels = this.myEC.getOwnerInformationForEmbedded(this);
        if (ownerRels == null) {
            return null;
        }
        final ObjectProvider[] owners = new ObjectProvider[ownerRels.size()];
        int i = 0;
        for (final ExecutionContext.EmbeddedOwnerRelation rel : ownerRels) {
            owners[i++] = rel.getOwnerOP();
        }
        return owners;
    }
    
    @Override
    public void changeActivityState(final ActivityState state) {
    }
    
    @Override
    public void updateFieldAfterInsert(final Object pc, final int fieldNumber) {
    }
    
    static {
        HELPER = AccessController.doPrivileged((PrivilegedAction<JDOImplHelper>)new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    return JDOImplHelper.getInstance();
                }
                catch (SecurityException e) {
                    throw new JDOFatalUserException(AbstractStateManager.LOCALISER.msg("026000"), e);
                }
            }
        });
    }
}
