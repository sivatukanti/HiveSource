// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

import javax.jdo.JDODetachedFieldAccessException;
import java.util.BitSet;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.StateManager;
import java.util.Map;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MIndex implements Detachable, PersistenceCapable
{
    private String indexName;
    private MTable origTable;
    private int createTime;
    private int lastAccessTime;
    private Map<String, String> parameters;
    private MTable indexTable;
    private MStorageDescriptor sd;
    private String indexHandlerClass;
    private boolean deferredRebuild;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MIndex() {
    }
    
    public MIndex(final String indexName, final MTable baseTable, final int createTime, final int lastAccessTime, final Map<String, String> parameters, final MTable indexTable, final MStorageDescriptor sd, final String indexHandlerClass, final boolean deferredRebuild) {
        this.indexName = indexName;
        this.origTable = baseTable;
        this.createTime = createTime;
        this.lastAccessTime = lastAccessTime;
        this.parameters = parameters;
        this.indexTable = indexTable;
        this.sd = sd;
        this.indexHandlerClass = indexHandlerClass;
        this.deferredRebuild = deferredRebuild;
    }
    
    public String getIndexName() {
        return jdoGetindexName(this);
    }
    
    public void setIndexName(final String indexName) {
        jdoSetindexName(this, indexName);
    }
    
    public int getCreateTime() {
        return jdoGetcreateTime(this);
    }
    
    public void setCreateTime(final int createTime) {
        jdoSetcreateTime(this, createTime);
    }
    
    public int getLastAccessTime() {
        return jdoGetlastAccessTime(this);
    }
    
    public void setLastAccessTime(final int lastAccessTime) {
        jdoSetlastAccessTime(this, lastAccessTime);
    }
    
    public Map<String, String> getParameters() {
        return (Map<String, String>)jdoGetparameters(this);
    }
    
    public void setParameters(final Map<String, String> parameters) {
        jdoSetparameters(this, parameters);
    }
    
    public MTable getOrigTable() {
        return jdoGetorigTable(this);
    }
    
    public void setOrigTable(final MTable origTable) {
        jdoSetorigTable(this, origTable);
    }
    
    public MTable getIndexTable() {
        return jdoGetindexTable(this);
    }
    
    public void setIndexTable(final MTable indexTable) {
        jdoSetindexTable(this, indexTable);
    }
    
    public MStorageDescriptor getSd() {
        return jdoGetsd(this);
    }
    
    public void setSd(final MStorageDescriptor sd) {
        jdoSetsd(this, sd);
    }
    
    public String getIndexHandlerClass() {
        return jdoGetindexHandlerClass(this);
    }
    
    public void setIndexHandlerClass(final String indexHandlerClass) {
        jdoSetindexHandlerClass(this, indexHandlerClass);
    }
    
    public boolean isDeferredRebuild() {
        return jdoGetdeferredRebuild(this);
    }
    
    public boolean getDeferredRebuild() {
        return jdoGetdeferredRebuild(this);
    }
    
    public void setDeferredRebuild(final boolean deferredRebuild) {
        jdoSetdeferredRebuild(this, deferredRebuild);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MIndex"), MIndex.jdoFieldNames, MIndex.jdoFieldTypes, MIndex.jdoFieldFlags, MIndex.jdoPersistenceCapableSuperclass, new MIndex());
    }
    
    @Override
    public void jdoCopyKeyFieldsFromObjectId(final ObjectIdFieldConsumer fc, final Object oid) {
    }
    
    protected void jdoCopyKeyFieldsFromObjectId(final Object oid) {
    }
    
    @Override
    public void jdoCopyKeyFieldsToObjectId(final Object oid) {
    }
    
    @Override
    public void jdoCopyKeyFieldsToObjectId(final ObjectIdFieldSupplier fs, final Object oid) {
    }
    
    @Override
    public final Object jdoGetObjectId() {
        if (this.jdoStateManager != null) {
            return this.jdoStateManager.getObjectId(this);
        }
        if (!this.jdoIsDetached()) {
            return null;
        }
        return this.jdoDetachedState[0];
    }
    
    @Override
    public final Object jdoGetVersion() {
        if (this.jdoStateManager != null) {
            return this.jdoStateManager.getVersion(this);
        }
        if (!this.jdoIsDetached()) {
            return null;
        }
        return this.jdoDetachedState[1];
    }
    
    protected final void jdoPreSerialize() {
        if (this.jdoStateManager != null) {
            this.jdoStateManager.preSerialize(this);
        }
    }
    
    @Override
    public final PersistenceManager jdoGetPersistenceManager() {
        return (this.jdoStateManager != null) ? this.jdoStateManager.getPersistenceManager(this) : null;
    }
    
    @Override
    public final Object jdoGetTransactionalObjectId() {
        return (this.jdoStateManager != null) ? this.jdoStateManager.getTransactionalObjectId(this) : null;
    }
    
    @Override
    public final boolean jdoIsDeleted() {
        return this.jdoStateManager != null && this.jdoStateManager.isDeleted(this);
    }
    
    @Override
    public final boolean jdoIsDirty() {
        if (this.jdoStateManager != null) {
            return this.jdoStateManager.isDirty(this);
        }
        return this.jdoIsDetached() && ((BitSet)this.jdoDetachedState[3]).length() > 0;
    }
    
    @Override
    public final boolean jdoIsNew() {
        return this.jdoStateManager != null && this.jdoStateManager.isNew(this);
    }
    
    @Override
    public final boolean jdoIsPersistent() {
        return this.jdoStateManager != null && this.jdoStateManager.isPersistent(this);
    }
    
    @Override
    public final boolean jdoIsTransactional() {
        return this.jdoStateManager != null && this.jdoStateManager.isTransactional(this);
    }
    
    @Override
    public void jdoMakeDirty(final String fieldName) {
        if (this.jdoStateManager != null) {
            this.jdoStateManager.makeDirty(this, fieldName);
        }
        if (this.jdoIsDetached() && fieldName != null) {
            String fldName = null;
            if (fieldName.indexOf(46) >= 0) {
                fldName = fieldName.substring(fieldName.lastIndexOf(46) + 1);
            }
            else {
                fldName = fieldName;
            }
            int i = 0;
            while (i < MIndex.jdoFieldNames.length) {
                if (MIndex.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MIndex.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MIndex.jdoInheritedFieldCount);
                        return;
                    }
                    throw new JDODetachedFieldAccessException("You have just attempted to access a field/property that hasn't been detached. Please detach it first before performing this operation");
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    @Override
    public Object jdoNewObjectIdInstance() {
        return null;
    }
    
    @Override
    public Object jdoNewObjectIdInstance(final Object key) {
        return null;
    }
    
    @Override
    public final void jdoProvideFields(final int[] indices) {
        if (indices == null) {
            throw new IllegalArgumentException("argment is null");
        }
        int i = indices.length - 1;
        if (i >= 0) {
            do {
                this.jdoProvideField(indices[i]);
            } while (--i >= 0);
        }
    }
    
    @Override
    public final void jdoReplaceFields(final int[] indices) {
        if (indices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        final int i = indices.length;
        if (i > 0) {
            int j = 0;
            do {
                this.jdoReplaceField(indices[j]);
            } while (++j < i);
        }
    }
    
    @Override
    public final void jdoReplaceFlags() {
        if (this.jdoStateManager != null) {
            this.jdoFlags = this.jdoStateManager.replacingFlags(this);
        }
    }
    
    @Override
    public final synchronized void jdoReplaceStateManager(final StateManager sm) {
        if (this.jdoStateManager != null) {
            this.jdoStateManager = this.jdoStateManager.replacingStateManager(this, sm);
        }
        else {
            JDOImplHelper.checkAuthorizedStateManager(sm);
            this.jdoStateManager = sm;
            this.jdoFlags = 1;
        }
    }
    
    @Override
    public final synchronized void jdoReplaceDetachedState() {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        this.jdoDetachedState = this.jdoStateManager.replacingDetachedState(this, this.jdoDetachedState);
    }
    
    @Override
    public boolean jdoIsDetached() {
        return this.jdoStateManager == null && this.jdoDetachedState != null;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm) {
        final MIndex result = new MIndex();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MIndex result = new MIndex();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        result.jdoCopyKeyFieldsFromObjectId(obj);
        return result;
    }
    
    @Override
    public void jdoReplaceField(final int index) {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        switch (index) {
            case 0: {
                this.createTime = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 1: {
                this.deferredRebuild = this.jdoStateManager.replacingBooleanField(this, index);
                break;
            }
            case 2: {
                this.indexHandlerClass = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 3: {
                this.indexName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.indexTable = (MTable)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 5: {
                this.lastAccessTime = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 6: {
                this.origTable = (MTable)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 7: {
                this.parameters = (Map<String, String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 8: {
                this.sd = (MStorageDescriptor)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    @Override
    public void jdoProvideField(final int index) {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        switch (index) {
            case 0: {
                this.jdoStateManager.providedIntField(this, index, this.createTime);
                break;
            }
            case 1: {
                this.jdoStateManager.providedBooleanField(this, index, this.deferredRebuild);
                break;
            }
            case 2: {
                this.jdoStateManager.providedStringField(this, index, this.indexHandlerClass);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.indexName);
                break;
            }
            case 4: {
                this.jdoStateManager.providedObjectField(this, index, this.indexTable);
                break;
            }
            case 5: {
                this.jdoStateManager.providedIntField(this, index, this.lastAccessTime);
                break;
            }
            case 6: {
                this.jdoStateManager.providedObjectField(this, index, this.origTable);
                break;
            }
            case 7: {
                this.jdoStateManager.providedObjectField(this, index, this.parameters);
                break;
            }
            case 8: {
                this.jdoStateManager.providedObjectField(this, index, this.sd);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MIndex obj, final int index) {
        switch (index) {
            case 0: {
                this.createTime = obj.createTime;
                break;
            }
            case 1: {
                this.deferredRebuild = obj.deferredRebuild;
                break;
            }
            case 2: {
                this.indexHandlerClass = obj.indexHandlerClass;
                break;
            }
            case 3: {
                this.indexName = obj.indexName;
                break;
            }
            case 4: {
                this.indexTable = obj.indexTable;
                break;
            }
            case 5: {
                this.lastAccessTime = obj.lastAccessTime;
                break;
            }
            case 6: {
                this.origTable = obj.origTable;
                break;
            }
            case 7: {
                this.parameters = obj.parameters;
                break;
            }
            case 8: {
                this.sd = obj.sd;
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    @Override
    public void jdoCopyFields(final Object obj, final int[] indices) {
        if (this.jdoStateManager == null) {
            throw new IllegalStateException("state manager is null");
        }
        if (indices == null) {
            throw new IllegalStateException("fieldNumbers is null");
        }
        if (!(obj instanceof MIndex)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MIndex");
        }
        final MIndex other = (MIndex)obj;
        if (this.jdoStateManager != other.jdoStateManager) {
            throw new IllegalArgumentException("state managers do not match");
        }
        int i = indices.length - 1;
        if (i >= 0) {
            do {
                this.jdoCopyField(other, indices[i]);
            } while (--i >= 0);
        }
    }
    
    private static final String[] __jdoFieldNamesInit() {
        return new String[] { "createTime", "deferredRebuild", "indexHandlerClass", "indexName", "indexTable", "lastAccessTime", "origTable", "parameters", "sd" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { Integer.TYPE, Boolean.TYPE, ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MTable"), Integer.TYPE, ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MTable"), ___jdo$loadClass("java.util.Map"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MStorageDescriptor") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 21, 21, 21, 10, 21, 10, 10, 10 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 9;
    }
    
    private static Class __jdoPersistenceCapableSuperclassInit() {
        return null;
    }
    
    public static Class ___jdo$loadClass(final String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
    
    private Object jdoSuperClone() throws CloneNotSupportedException {
        final MIndex o = (MIndex)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static int jdoGetcreateTime(final MIndex objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getIntField(objPC, 0, objPC.createTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"createTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.createTime;
    }
    
    private static void jdoSetcreateTime(final MIndex objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 0, objPC.createTime, val);
        }
        else {
            objPC.createTime = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(0);
            }
        }
    }
    
    private static boolean jdoGetdeferredRebuild(final MIndex objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getBooleanField(objPC, 1, objPC.deferredRebuild);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"deferredRebuild\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.deferredRebuild;
    }
    
    private static void jdoSetdeferredRebuild(final MIndex objPC, final boolean val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setBooleanField(objPC, 1, objPC.deferredRebuild, val);
        }
        else {
            objPC.deferredRebuild = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
    
    private static String jdoGetindexHandlerClass(final MIndex objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return objPC.jdoStateManager.getStringField(objPC, 2, objPC.indexHandlerClass);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"indexHandlerClass\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.indexHandlerClass;
    }
    
    private static void jdoSetindexHandlerClass(final MIndex objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 2, objPC.indexHandlerClass, val);
        }
        else {
            objPC.indexHandlerClass = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(2);
            }
        }
    }
    
    private static String jdoGetindexName(final MIndex objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.indexName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"indexName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.indexName;
    }
    
    private static void jdoSetindexName(final MIndex objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.indexName, val);
        }
        else {
            objPC.indexName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static MTable jdoGetindexTable(final MIndex objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return (MTable)objPC.jdoStateManager.getObjectField(objPC, 4, objPC.indexTable);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4) && !((BitSet)objPC.jdoDetachedState[3]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"indexTable\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.indexTable;
    }
    
    private static void jdoSetindexTable(final MIndex objPC, final MTable val) {
        if (objPC.jdoStateManager == null) {
            objPC.indexTable = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 4, objPC.indexTable, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(4);
        }
    }
    
    private static int jdoGetlastAccessTime(final MIndex objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return objPC.jdoStateManager.getIntField(objPC, 5, objPC.lastAccessTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"lastAccessTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.lastAccessTime;
    }
    
    private static void jdoSetlastAccessTime(final MIndex objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 5, objPC.lastAccessTime, val);
        }
        else {
            objPC.lastAccessTime = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(5);
            }
        }
    }
    
    private static MTable jdoGetorigTable(final MIndex objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return (MTable)objPC.jdoStateManager.getObjectField(objPC, 6, objPC.origTable);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6) && !((BitSet)objPC.jdoDetachedState[3]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"origTable\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.origTable;
    }
    
    private static void jdoSetorigTable(final MIndex objPC, final MTable val) {
        if (objPC.jdoStateManager == null) {
            objPC.origTable = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 6, objPC.origTable, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(6);
        }
    }
    
    private static Map jdoGetparameters(final MIndex objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 7)) {
            return (Map)objPC.jdoStateManager.getObjectField(objPC, 7, objPC.parameters);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(7) && !((BitSet)objPC.jdoDetachedState[3]).get(7)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"parameters\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.parameters;
    }
    
    private static void jdoSetparameters(final MIndex objPC, final Map val) {
        if (objPC.jdoStateManager == null) {
            objPC.parameters = (Map<String, String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 7, objPC.parameters, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(7);
        }
    }
    
    private static MStorageDescriptor jdoGetsd(final MIndex objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 8)) {
            return (MStorageDescriptor)objPC.jdoStateManager.getObjectField(objPC, 8, objPC.sd);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(8) && !((BitSet)objPC.jdoDetachedState[3]).get(8)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"sd\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.sd;
    }
    
    private static void jdoSetsd(final MIndex objPC, final MStorageDescriptor val) {
        if (objPC.jdoStateManager == null) {
            objPC.sd = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 8, objPC.sd, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(8);
        }
    }
}
