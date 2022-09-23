// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.state;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.fieldmanager.LoadFieldManager;
import org.datanucleus.cache.L2CacheRetrieveFieldManager;
import org.datanucleus.cache.CachedPC;
import org.datanucleus.cache.Level2Cache;
import org.datanucleus.cache.L2CachePopulateFieldManager;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.Transaction;
import org.datanucleus.identity.IdentityReference;
import org.datanucleus.util.StringUtils;
import java.util.concurrent.locks.ReentrantLock;
import org.datanucleus.store.objectvaluegenerator.ObjectValueGenerator;
import java.util.HashMap;
import org.datanucleus.store.fieldmanager.FieldManager;
import java.util.concurrent.locks.Lock;
import org.datanucleus.FetchPlanForClass;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.fieldmanager.SingleTypeFieldManager;
import org.datanucleus.util.Localiser;

public abstract class AbstractStateManager implements ObjectProvider
{
    protected static final Localiser LOCALISER;
    protected static final SingleTypeFieldManager HOLLOWFIELDMANAGER;
    protected static final int FLAG_STORING_PC = 65536;
    protected static final int FLAG_NEED_INHERITANCE_VALIDATION = 32768;
    protected static final int FLAG_POSTINSERT_UPDATE = 16384;
    protected static final int FLAG_LOADINGFPFIELDS = 8192;
    protected static final int FLAG_POSTLOAD_PENDING = 4096;
    protected static final int FLAG_CHANGING_STATE = 2048;
    protected static final int FLAG_FLUSHED_NEW = 1024;
    protected static final int FLAG_BECOMING_DELETED = 512;
    protected static final int FLAG_UPDATING_EMBEDDING_FIELDS_WITH_OWNER = 256;
    protected static final int FLAG_RETRIEVING_DETACHED_STATE = 128;
    protected static final int FLAG_RESETTING_DETACHED_STATE = 64;
    protected static final int FLAG_ATTACHING = 32;
    protected static final int FLAG_DETACHING = 16;
    protected static final int FLAG_MAKING_TRANSIENT = 8;
    protected static final int FLAG_FLUSHING = 4;
    protected static final int FLAG_DISCONNECTING = 2;
    protected int flags;
    protected boolean restoreValues;
    protected ExecutionContext myEC;
    protected AbstractClassMetaData cmd;
    protected Object myInternalID;
    protected Object myID;
    protected LifeCycleState myLC;
    protected Object myVersion;
    protected Object transactionalVersion;
    protected byte persistenceFlags;
    protected FetchPlanForClass myFP;
    protected boolean dirty;
    protected boolean[] dirtyFields;
    protected boolean[] loadedFields;
    protected Lock lock;
    protected short lockMode;
    protected byte savedFlags;
    protected boolean[] savedLoadedFields;
    protected ActivityState activity;
    protected FieldManager currFM;
    protected short objectType;
    boolean[] preDeleteLoadedFields;
    public static final HashMap<String, ObjectValueGenerator> objectValGenerators;
    
    public AbstractStateManager(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        this.restoreValues = false;
        this.dirty = false;
        this.lock = null;
        this.lockMode = 0;
        this.savedLoadedFields = null;
        this.currFM = null;
        this.objectType = 0;
        this.preDeleteLoadedFields = null;
        this.connect(ec, cmd);
    }
    
    @Override
    public void connect(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        final int fieldCount = cmd.getMemberCount();
        this.cmd = cmd;
        this.dirtyFields = new boolean[fieldCount];
        this.loadedFields = new boolean[fieldCount];
        this.dirty = false;
        this.myEC = ec;
        this.myFP = this.myEC.getFetchPlan().getFetchPlanForClass(cmd);
        this.lock = new ReentrantLock();
        this.lockMode = 0;
        this.savedFlags = 0;
        this.savedLoadedFields = null;
        this.objectType = 0;
        this.activity = ActivityState.NONE;
        this.myVersion = null;
        this.transactionalVersion = null;
        this.persistenceFlags = 0;
    }
    
    @Override
    public AbstractClassMetaData getClassMetaData() {
        return this.cmd;
    }
    
    @Override
    public ExecutionContext getExecutionContext() {
        return this.myEC;
    }
    
    @Override
    public LifeCycleState getLifecycleState() {
        return this.myLC;
    }
    
    protected CallbackHandler getCallbackHandler() {
        return this.myEC.getCallbackHandler();
    }
    
    @Override
    public abstract Object getObject();
    
    @Override
    public String getObjectAsPrintable() {
        return StringUtils.toJVMIDString(this.getObject());
    }
    
    @Override
    public String toString() {
        return "StateManager[pc=" + StringUtils.toJVMIDString(this.getObject()) + ", lifecycle=" + this.myLC + "]";
    }
    
    @Override
    public Object getInternalObjectId() {
        if (this.myID != null) {
            return this.myID;
        }
        if (this.myInternalID == null) {
            return this.myInternalID = new IdentityReference(this);
        }
        return this.myInternalID;
    }
    
    @Override
    public boolean isInserting() {
        return this.activity == ActivityState.INSERTING;
    }
    
    @Override
    public boolean isWaitingToBeFlushedToDatastore() {
        return this.myLC.stateType() == 1 && !this.isFlushedNew();
    }
    
    @Override
    public boolean isRestoreValues() {
        return this.restoreValues;
    }
    
    @Override
    public void setStoringPC() {
        this.flags |= 0x10000;
    }
    
    @Override
    public void unsetStoringPC() {
        this.flags &= 0xFFFEFFFF;
    }
    
    protected boolean isStoringPC() {
        return (this.flags & 0x10000) != 0x0;
    }
    
    void setPostLoadPending(final boolean flag) {
        if (flag) {
            this.flags |= 0x1000;
        }
        else {
            this.flags &= 0xFFFFEFFF;
        }
    }
    
    protected boolean isPostLoadPending() {
        return (this.flags & 0x1000) != 0x0;
    }
    
    protected boolean isChangingState() {
        return (this.flags & 0x800) != 0x0;
    }
    
    void setResettingDetachedState(final boolean flag) {
        if (flag) {
            this.flags |= 0x40;
        }
        else {
            this.flags &= 0xFFFFFFBF;
        }
    }
    
    protected boolean isResettingDetachedState() {
        return (this.flags & 0x40) != 0x0;
    }
    
    void setRetrievingDetachedState(final boolean flag) {
        if (flag) {
            this.flags |= 0x80;
        }
        else {
            this.flags &= 0xFFFFFF7F;
        }
    }
    
    protected boolean isRetrievingDetachedState() {
        return (this.flags & 0x80) != 0x0;
    }
    
    void setDisconnecting(final boolean flag) {
        if (flag) {
            this.flags |= 0x2;
        }
        else {
            this.flags &= 0xFFFFFFFD;
        }
    }
    
    protected boolean isDisconnecting() {
        return (this.flags & 0x2) != 0x0;
    }
    
    void setMakingTransient(final boolean flag) {
        if (flag) {
            this.flags |= 0x8;
        }
        else {
            this.flags &= 0xFFFFFFF7;
        }
    }
    
    protected boolean isMakingTransient() {
        return (this.flags & 0x8) != 0x0;
    }
    
    @Override
    public boolean isDeleting() {
        return this.activity == ActivityState.DELETING;
    }
    
    void setBecomingDeleted(final boolean flag) {
        if (flag) {
            this.flags |= 0x200;
        }
        else {
            this.flags &= 0xFFFFFDFF;
        }
    }
    
    @Override
    public boolean becomingDeleted() {
        return (this.flags & 0x200) > 0;
    }
    
    @Override
    public void markForInheritanceValidation() {
        this.flags |= 0x8000;
    }
    
    void setDetaching(final boolean flag) {
        if (flag) {
            this.flags |= 0x10;
        }
        else {
            this.flags &= 0xFFFFFFEF;
        }
    }
    
    public boolean isDetaching() {
        return (this.flags & 0x10) != 0x0;
    }
    
    void setAttaching(final boolean flag) {
        if (flag) {
            this.flags |= 0x20;
        }
        else {
            this.flags &= 0xFFFFFFDF;
        }
    }
    
    public boolean isAttaching() {
        return (this.flags & 0x20) != 0x0;
    }
    
    @Override
    public void setTransactionalVersion(final Object version) {
        this.transactionalVersion = version;
    }
    
    public Object getTransactionalVersion(final Object pc) {
        return this.transactionalVersion;
    }
    
    @Override
    public void setVersion(final Object version) {
        this.myVersion = version;
        this.transactionalVersion = version;
    }
    
    @Override
    public void setFlushedNew(final boolean flag) {
        if (flag) {
            this.flags |= 0x400;
        }
        else {
            this.flags &= 0xFFFFFBFF;
        }
    }
    
    @Override
    public boolean isFlushedNew() {
        return (this.flags & 0x400) != 0x0;
    }
    
    @Override
    public boolean isFlushedToDatastore() {
        return !this.dirty;
    }
    
    @Override
    public void setFlushing(final boolean flushing) {
        if (flushing) {
            this.flags |= 0x4;
        }
        else {
            this.flags &= 0xFFFFFFFB;
        }
    }
    
    protected boolean isFlushing() {
        return (this.flags & 0x4) != 0x0;
    }
    
    @Override
    public void markAsFlushed() {
        this.clearDirtyFlags();
    }
    
    protected void preStateChange() {
        this.flags |= 0x800;
    }
    
    protected abstract void postStateChange();
    
    @Override
    public void refresh() {
        this.preStateChange();
        try {
            this.myLC = this.myLC.transitionRefresh(this);
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void retrieve(final boolean fgOnly) {
        this.preStateChange();
        try {
            this.myLC = this.myLC.transitionRetrieve(this, fgOnly);
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void makePersistentTransactionalTransient() {
        this.preStateChange();
        try {
            if (this.myLC.isTransactional && !this.myLC.isPersistent) {
                this.makePersistent();
                this.myLC = this.myLC.transitionMakePersistent(this);
            }
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void makeNontransactional() {
        this.preStateChange();
        try {
            this.myLC = this.myLC.transitionMakeNontransactional(this);
        }
        finally {
            this.postStateChange();
        }
    }
    
    protected void transitionReadField(final boolean isLoaded) {
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            if (this.myLC == null) {
                return;
            }
            this.preStateChange();
            try {
                this.myLC = this.myLC.transitionReadField(this, isLoaded);
            }
            finally {
                this.postStateChange();
            }
        }
        finally {
            if (this.myEC.getMultithreaded()) {
                this.lock.unlock();
                this.myEC.getLock().unlock();
            }
        }
    }
    
    protected void transitionWriteField() {
        try {
            if (this.myEC.getMultithreaded()) {
                this.myEC.getLock().lock();
                this.lock.lock();
            }
            this.preStateChange();
            try {
                this.myLC = this.myLC.transitionWriteField(this);
            }
            finally {
                this.postStateChange();
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
    public void evict() {
        if (this.myLC != this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(2) && this.myLC != this.myEC.getNucleusContext().getApiAdapter().getLifeCycleState(9)) {
            return;
        }
        this.preStateChange();
        try {
            try {
                this.getCallbackHandler().preClear(this.getObject());
                this.getCallbackHandler().postClear(this.getObject());
            }
            finally {
                this.myLC = this.myLC.transitionEvict(this);
            }
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void preBegin(final Transaction tx) {
        this.preStateChange();
        try {
            this.myLC = this.myLC.transitionBegin(this, tx);
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void postCommit(final Transaction tx) {
        this.preStateChange();
        try {
            this.myLC = this.myLC.transitionCommit(this, tx);
            if (this.transactionalVersion != this.myVersion) {
                this.myVersion = this.transactionalVersion;
            }
            this.lockMode = 0;
        }
        finally {
            this.postStateChange();
        }
    }
    
    @Override
    public void preRollback(final Transaction tx) {
        this.preStateChange();
        try {
            this.myEC.clearDirty(this);
            this.myLC = this.myLC.transitionRollback(this, tx);
            if (this.transactionalVersion != this.myVersion) {
                this.transactionalVersion = this.myVersion;
            }
            this.lockMode = 0;
        }
        finally {
            this.postStateChange();
        }
    }
    
    protected void internalDeletePersistent() {
        if (this.isDeleting()) {
            throw new NucleusUserException(AbstractStateManager.LOCALISER.msg("026008"));
        }
        this.activity = ActivityState.DELETING;
        try {
            if (this.dirty) {
                this.clearDirtyFlags();
                this.myEC.flushInternal(false);
            }
            if (!this.isEmbedded()) {
                this.myEC.getStoreManager().getPersistenceHandler().deleteObject(this);
            }
            this.preDeleteLoadedFields = null;
        }
        finally {
            this.activity = ActivityState.NONE;
        }
    }
    
    @Override
    public void locate() {
        this.myEC.getStoreManager().getPersistenceHandler().locateObject(this);
    }
    
    @Override
    public abstract void provideFields(final int[] p0, final FieldManager p1);
    
    @Override
    public abstract void replaceFields(final int[] p0, final FieldManager p1);
    
    protected boolean areFieldsLoaded(final int[] fieldNumbers) {
        if (fieldNumbers == null) {
            return true;
        }
        for (int i = 0; i < fieldNumbers.length; ++i) {
            if (!this.loadedFields[fieldNumbers[i]]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void unloadNonFetchPlanFields() {
        final int[] fpFieldNumbers = this.myFP.getMemberNumbers();
        int[] nonfpFieldNumbers = null;
        if (fpFieldNumbers == null || fpFieldNumbers.length == 0) {
            nonfpFieldNumbers = this.cmd.getAllMemberPositions();
        }
        else {
            final int fieldCount = this.cmd.getMemberCount();
            if (fieldCount == fpFieldNumbers.length) {
                return;
            }
            nonfpFieldNumbers = new int[fieldCount - fpFieldNumbers.length];
            int currentFPFieldIndex = 0;
            int j = 0;
            for (int i = 0; i < fieldCount; ++i) {
                if (currentFPFieldIndex >= fpFieldNumbers.length) {
                    nonfpFieldNumbers[j++] = i;
                }
                else if (fpFieldNumbers[currentFPFieldIndex] == i) {
                    ++currentFPFieldIndex;
                }
                else {
                    nonfpFieldNumbers[j++] = i;
                }
            }
        }
        for (int k = 0; k < nonfpFieldNumbers.length; ++k) {
            this.loadedFields[nonfpFieldNumbers[k]] = false;
        }
    }
    
    protected void markPKFieldsAsLoaded() {
        if (this.cmd.getIdentityType() == IdentityType.APPLICATION) {
            final int[] pkPositions = this.cmd.getPKMemberPositions();
            for (int i = 0; i < pkPositions.length; ++i) {
                this.loadedFields[pkPositions[i]] = true;
            }
        }
    }
    
    protected void updateLevel2CacheForFields(final int[] fieldNumbers) {
        final String updateMode = (String)this.myEC.getProperty("datanucleus.cache.level2.updateMode");
        if (updateMode != null && updateMode.equalsIgnoreCase("commit-only")) {
            return;
        }
        if (fieldNumbers == null || fieldNumbers.length == 0) {
            return;
        }
        final Level2Cache l2cache = this.myEC.getNucleusContext().getLevel2Cache();
        if (l2cache != null && this.myEC.getNucleusContext().isClassCacheable(this.cmd) && !this.myEC.isObjectModifiedInTransaction(this.myID)) {
            final CachedPC cachedPC = l2cache.get(this.myID);
            if (cachedPC != null) {
                final int[] cacheFieldsToLoad = ClassUtils.getFlagsSetTo(cachedPC.getLoadedFields(), fieldNumbers, false);
                if (cacheFieldsToLoad != null && cacheFieldsToLoad.length > 0) {
                    final CachedPC copyCachedPC = cachedPC.getCopy();
                    if (NucleusLogger.CACHE.isDebugEnabled()) {
                        NucleusLogger.CACHE.debug(AbstractStateManager.LOCALISER.msg("026033", StringUtils.toJVMIDString(this.getObject()), this.myID, StringUtils.intArrayToString(cacheFieldsToLoad)));
                    }
                    this.provideFields(cacheFieldsToLoad, new L2CachePopulateFieldManager(this, copyCachedPC));
                    this.myEC.getNucleusContext().getLevel2Cache().put(this.getInternalObjectId(), copyCachedPC);
                }
            }
        }
    }
    
    protected int[] loadFieldsFromLevel2Cache(final int[] fieldNumbers) {
        if (fieldNumbers == null || fieldNumbers.length == 0 || this.myEC.isFlushing() || this.myLC.isDeleted() || this.isDeleting() || this.getExecutionContext().getTransaction().isCommitting()) {
            return fieldNumbers;
        }
        if (!this.myEC.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.cache.level2.loadFields", true)) {
            return fieldNumbers;
        }
        final Level2Cache l2cache = this.myEC.getNucleusContext().getLevel2Cache();
        if (l2cache != null && this.myEC.getNucleusContext().isClassCacheable(this.cmd)) {
            final CachedPC cachedPC = l2cache.get(this.myID);
            if (cachedPC != null) {
                final int[] cacheFieldsToLoad = ClassUtils.getFlagsSetTo(cachedPC.getLoadedFields(), fieldNumbers, true);
                if (cacheFieldsToLoad != null && cacheFieldsToLoad.length > 0) {
                    if (NucleusLogger.CACHE.isDebugEnabled()) {
                        NucleusLogger.CACHE.debug(AbstractStateManager.LOCALISER.msg("026034", StringUtils.toJVMIDString(this.getObject()), this.myID, StringUtils.intArrayToString(cacheFieldsToLoad)));
                    }
                    final L2CacheRetrieveFieldManager l2RetFM = new L2CacheRetrieveFieldManager(this, cachedPC);
                    this.replaceFields(cacheFieldsToLoad, l2RetFM);
                    final int[] fieldsNotLoaded = l2RetFM.getFieldsNotLoaded();
                    if (fieldsNotLoaded != null) {
                        for (int i = 0; i < fieldsNotLoaded.length; ++i) {
                            this.loadedFields[fieldsNotLoaded[i]] = false;
                        }
                    }
                }
            }
        }
        return ClassUtils.getFlagsSetTo(this.loadedFields, fieldNumbers, false);
    }
    
    @Override
    public void loadFieldsInFetchPlan(final FetchPlanState state) {
        if ((this.flags & 0x2000) != 0x0) {
            return;
        }
        this.flags |= 0x2000;
        try {
            this.loadUnloadedFieldsInFetchPlan();
            final int[] fieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, this.cmd.getAllMemberPositions(), true);
            if (fieldNumbers != null && fieldNumbers.length > 0) {
                this.replaceFields(fieldNumbers, new LoadFieldManager(this, this.cmd.getSCOMutableMemberFlags(), this.myFP, state));
                this.updateLevel2CacheForFields(fieldNumbers);
            }
        }
        finally {
            this.flags &= 0xFFFFDFFF;
        }
    }
    
    @Override
    public void loadFieldFromDatastore(final int fieldNumber) {
        this.loadFieldsFromDatastore(new int[] { fieldNumber });
    }
    
    protected void loadFieldsFromDatastore(final int[] fieldNumbers) {
        if (this.myLC.isNew() && this.myLC.isPersistent() && !this.isFlushedNew()) {
            return;
        }
        if ((this.flags & 0x8000) != 0x0) {
            final String className = this.myEC.getStoreManager().getClassNameForObjectID(this.myID, this.myEC.getClassLoaderResolver(), this.myEC);
            if (!this.getObject().getClass().getName().equals(className)) {
                this.myEC.removeObjectFromLevel1Cache(this.myID);
                this.myEC.removeObjectFromLevel2Cache(this.myID);
                throw new NucleusObjectNotFoundException("Object with id " + this.myID + " was created without validating of type " + this.getObject().getClass().getName() + " but is actually of type " + className);
            }
            this.flags &= 0xFFFF7FFF;
        }
        this.myEC.getStoreManager().getPersistenceHandler().fetchObject(this, fieldNumbers);
    }
    
    protected int[] getFieldNumbersOfLoadedOrDirtyFields(final boolean[] loadedFields, final boolean[] dirtyFields) {
        int numFields = 0;
        for (int i = 0; i < loadedFields.length; ++i) {
            if (loadedFields[i] || dirtyFields[i]) {
                ++numFields;
            }
        }
        final int[] fieldNumbers = new int[numFields];
        int n = 0;
        final int[] allFieldNumbers = this.cmd.getAllMemberPositions();
        for (int j = 0; j < loadedFields.length; ++j) {
            if (loadedFields[j] || dirtyFields[j]) {
                fieldNumbers[n++] = allFieldNumbers[j];
            }
        }
        return fieldNumbers;
    }
    
    @Override
    public boolean[] getDirtyFields() {
        final boolean[] copy = new boolean[this.dirtyFields.length];
        System.arraycopy(this.dirtyFields, 0, copy, 0, this.dirtyFields.length);
        return copy;
    }
    
    @Override
    public int[] getDirtyFieldNumbers() {
        return ClassUtils.getFlagsSetTo(this.dirtyFields, true);
    }
    
    @Override
    public boolean[] getLoadedFields() {
        return this.loadedFields.clone();
    }
    
    @Override
    public int[] getLoadedFieldNumbers() {
        return ClassUtils.getFlagsSetTo(this.loadedFields, true);
    }
    
    @Override
    public boolean getAllFieldsLoaded() {
        for (int i = 0; i < this.loadedFields.length; ++i) {
            if (!this.loadedFields[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String[] getDirtyFieldNames() {
        final int[] dirtyFieldNumbers = ClassUtils.getFlagsSetTo(this.dirtyFields, true);
        if (dirtyFieldNumbers != null && dirtyFieldNumbers.length > 0) {
            final String[] dirtyFieldNames = new String[dirtyFieldNumbers.length];
            for (int i = 0; i < dirtyFieldNumbers.length; ++i) {
                dirtyFieldNames[i] = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(dirtyFieldNumbers[i]).getName();
            }
            return dirtyFieldNames;
        }
        return null;
    }
    
    @Override
    public String[] getLoadedFieldNames() {
        final int[] loadedFieldNumbers = ClassUtils.getFlagsSetTo(this.loadedFields, true);
        if (loadedFieldNumbers != null && loadedFieldNumbers.length > 0) {
            final String[] loadedFieldNames = new String[loadedFieldNumbers.length];
            for (int i = 0; i < loadedFieldNumbers.length; ++i) {
                loadedFieldNames[i] = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(loadedFieldNumbers[i]).getName();
            }
            return loadedFieldNames;
        }
        return null;
    }
    
    @Override
    public boolean isFieldLoaded(final int fieldNumber) {
        return this.loadedFields[fieldNumber];
    }
    
    protected void clearFieldsByNumbers(final int[] fieldNumbers) {
        this.replaceFields(fieldNumbers, AbstractStateManager.HOLLOWFIELDMANAGER);
        for (int i = 0; i < fieldNumbers.length; ++i) {
            this.loadedFields[fieldNumbers[i]] = false;
            this.dirtyFields[fieldNumbers[i]] = false;
        }
    }
    
    protected void clearDirtyFlags() {
        this.dirty = false;
        ClassUtils.clearFlags(this.dirtyFields);
    }
    
    protected void clearDirtyFlags(final int[] fieldNumbers) {
        this.dirty = false;
        ClassUtils.clearFlags(this.dirtyFields, fieldNumbers);
    }
    
    @Override
    public void unloadField(final String fieldName) {
        if (this.objectType == 0) {
            final AbstractMemberMetaData mmd = this.getClassMetaData().getMetaDataForMember(fieldName);
            this.loadedFields[mmd.getAbsoluteFieldNumber()] = false;
            return;
        }
        throw new NucleusUserException("Cannot unload field/property of embedded object");
    }
    
    @Override
    public boolean isEmbedded() {
        return this.objectType > 0;
    }
    
    @Override
    public void setPcObjectType(final short objType) {
        this.objectType = objType;
    }
    
    @Override
    public void lock(final short lockMode) {
        this.lockMode = lockMode;
    }
    
    @Override
    public void unlock() {
        this.lockMode = 0;
    }
    
    @Override
    public short getLockMode() {
        return this.lockMode;
    }
    
    @Override
    public void setAssociatedValue(final Object key, final Object value) {
        this.myEC.setObjectProviderAssociatedValue(this, key, value);
    }
    
    @Override
    public Object getAssociatedValue(final Object key) {
        return this.myEC.getObjectProviderAssociatedValue(this, key);
    }
    
    @Override
    public void removeAssociatedValue(final Object key) {
        this.myEC.removeObjectProviderAssociatedValue(this, key);
    }
    
    public boolean containsAssociatedValue(final Object key) {
        return this.myEC.containsObjectProviderAssociatedValue(this, key);
    }
    
    protected static ObjectValueGenerator getObjectValueGenerator(final ExecutionContext ec, final String genName) {
        if (!AbstractStateManager.objectValGenerators.isEmpty()) {
            final ObjectValueGenerator valGen = AbstractStateManager.objectValGenerators.get(genName);
            if (valGen != null) {
                return valGen;
            }
        }
        try {
            final ObjectValueGenerator valGen = (ObjectValueGenerator)ec.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.store_objectvaluegenerator", new String[] { "name" }, new String[] { genName }, "class-name", null, null);
            AbstractStateManager.objectValGenerators.put(genName, valGen);
            return valGen;
        }
        catch (Exception e) {
            NucleusLogger.VALUEGENERATION.info("Exception thrown generating value using objectvaluegenerator " + genName, e);
            throw new NucleusException("Exception thrown generating value for object", e);
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        HOLLOWFIELDMANAGER = new SingleTypeFieldManager();
        objectValGenerators = new HashMap<String, ObjectValueGenerator>(1);
    }
}
