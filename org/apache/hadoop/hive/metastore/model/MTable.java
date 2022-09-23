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
import java.util.List;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MTable implements Detachable, PersistenceCapable
{
    private String tableName;
    private MDatabase database;
    private MStorageDescriptor sd;
    private String owner;
    private int createTime;
    private int lastAccessTime;
    private int retention;
    private List<MFieldSchema> partitionKeys;
    private Map<String, String> parameters;
    private String viewOriginalText;
    private String viewExpandedText;
    private String tableType;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MTable() {
    }
    
    public MTable(final String tableName, final MDatabase database, final MStorageDescriptor sd, final String owner, final int createTime, final int lastAccessTime, final int retention, final List<MFieldSchema> partitionKeys, final Map<String, String> parameters, final String viewOriginalText, final String viewExpandedText, final String tableType) {
        this.tableName = tableName;
        this.database = database;
        this.sd = sd;
        this.owner = owner;
        this.createTime = createTime;
        this.setLastAccessTime(lastAccessTime);
        this.retention = retention;
        this.partitionKeys = partitionKeys;
        this.parameters = parameters;
        this.viewOriginalText = viewOriginalText;
        this.viewExpandedText = viewExpandedText;
        this.tableType = tableType;
    }
    
    public String getTableName() {
        return jdoGettableName(this);
    }
    
    public void setTableName(final String tableName) {
        jdoSettableName(this, tableName);
    }
    
    public MStorageDescriptor getSd() {
        return jdoGetsd(this);
    }
    
    public void setSd(final MStorageDescriptor sd) {
        jdoSetsd(this, sd);
    }
    
    public List<MFieldSchema> getPartitionKeys() {
        return (List<MFieldSchema>)jdoGetpartitionKeys(this);
    }
    
    public void setPartitionKeys(final List<MFieldSchema> partKeys) {
        jdoSetpartitionKeys(this, partKeys);
    }
    
    public Map<String, String> getParameters() {
        return (Map<String, String>)jdoGetparameters(this);
    }
    
    public void setParameters(final Map<String, String> parameters) {
        jdoSetparameters(this, parameters);
    }
    
    public String getViewOriginalText() {
        return jdoGetviewOriginalText(this);
    }
    
    public void setViewOriginalText(final String viewOriginalText) {
        jdoSetviewOriginalText(this, viewOriginalText);
    }
    
    public String getViewExpandedText() {
        return jdoGetviewExpandedText(this);
    }
    
    public void setViewExpandedText(final String viewExpandedText) {
        jdoSetviewExpandedText(this, viewExpandedText);
    }
    
    public String getOwner() {
        return jdoGetowner(this);
    }
    
    public void setOwner(final String owner) {
        jdoSetowner(this, owner);
    }
    
    public int getCreateTime() {
        return jdoGetcreateTime(this);
    }
    
    public void setCreateTime(final int createTime) {
        jdoSetcreateTime(this, createTime);
    }
    
    public MDatabase getDatabase() {
        return jdoGetdatabase(this);
    }
    
    public void setDatabase(final MDatabase database) {
        jdoSetdatabase(this, database);
    }
    
    public int getRetention() {
        return jdoGetretention(this);
    }
    
    public void setRetention(final int retention) {
        jdoSetretention(this, retention);
    }
    
    public void setLastAccessTime(final int lastAccessTime) {
        jdoSetlastAccessTime(this, lastAccessTime);
    }
    
    public int getLastAccessTime() {
        return jdoGetlastAccessTime(this);
    }
    
    public void setTableType(final String tableType) {
        jdoSettableType(this, tableType);
    }
    
    public String getTableType() {
        return jdoGettableType(this);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MTable"), MTable.jdoFieldNames, MTable.jdoFieldTypes, MTable.jdoFieldFlags, MTable.jdoPersistenceCapableSuperclass, new MTable());
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
            while (i < MTable.jdoFieldNames.length) {
                if (MTable.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MTable.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MTable.jdoInheritedFieldCount);
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
        final MTable result = new MTable();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MTable result = new MTable();
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
                this.database = (MDatabase)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 2: {
                this.lastAccessTime = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 3: {
                this.owner = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.parameters = (Map<String, String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 5: {
                this.partitionKeys = (List<MFieldSchema>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 6: {
                this.retention = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 7: {
                this.sd = (MStorageDescriptor)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 8: {
                this.tableName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 9: {
                this.tableType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 10: {
                this.viewExpandedText = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 11: {
                this.viewOriginalText = this.jdoStateManager.replacingStringField(this, index);
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
                this.jdoStateManager.providedObjectField(this, index, this.database);
                break;
            }
            case 2: {
                this.jdoStateManager.providedIntField(this, index, this.lastAccessTime);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.owner);
                break;
            }
            case 4: {
                this.jdoStateManager.providedObjectField(this, index, this.parameters);
                break;
            }
            case 5: {
                this.jdoStateManager.providedObjectField(this, index, this.partitionKeys);
                break;
            }
            case 6: {
                this.jdoStateManager.providedIntField(this, index, this.retention);
                break;
            }
            case 7: {
                this.jdoStateManager.providedObjectField(this, index, this.sd);
                break;
            }
            case 8: {
                this.jdoStateManager.providedStringField(this, index, this.tableName);
                break;
            }
            case 9: {
                this.jdoStateManager.providedStringField(this, index, this.tableType);
                break;
            }
            case 10: {
                this.jdoStateManager.providedStringField(this, index, this.viewExpandedText);
                break;
            }
            case 11: {
                this.jdoStateManager.providedStringField(this, index, this.viewOriginalText);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MTable obj, final int index) {
        switch (index) {
            case 0: {
                this.createTime = obj.createTime;
                break;
            }
            case 1: {
                this.database = obj.database;
                break;
            }
            case 2: {
                this.lastAccessTime = obj.lastAccessTime;
                break;
            }
            case 3: {
                this.owner = obj.owner;
                break;
            }
            case 4: {
                this.parameters = obj.parameters;
                break;
            }
            case 5: {
                this.partitionKeys = obj.partitionKeys;
                break;
            }
            case 6: {
                this.retention = obj.retention;
                break;
            }
            case 7: {
                this.sd = obj.sd;
                break;
            }
            case 8: {
                this.tableName = obj.tableName;
                break;
            }
            case 9: {
                this.tableType = obj.tableType;
                break;
            }
            case 10: {
                this.viewExpandedText = obj.viewExpandedText;
                break;
            }
            case 11: {
                this.viewOriginalText = obj.viewOriginalText;
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
        if (!(obj instanceof MTable)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MTable");
        }
        final MTable other = (MTable)obj;
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
        return new String[] { "createTime", "database", "lastAccessTime", "owner", "parameters", "partitionKeys", "retention", "sd", "tableName", "tableType", "viewExpandedText", "viewOriginalText" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { Integer.TYPE, ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MDatabase"), Integer.TYPE, ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.util.Map"), ___jdo$loadClass("java.util.List"), Integer.TYPE, ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MStorageDescriptor"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 10, 21, 21, 10, 10, 21, 10, 21, 21, 26, 26 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 12;
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
        final MTable o = (MTable)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static int jdoGetcreateTime(final MTable objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getIntField(objPC, 0, objPC.createTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"createTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.createTime;
    }
    
    private static void jdoSetcreateTime(final MTable objPC, final int val) {
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
    
    private static MDatabase jdoGetdatabase(final MTable objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return (MDatabase)objPC.jdoStateManager.getObjectField(objPC, 1, objPC.database);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1) && !((BitSet)objPC.jdoDetachedState[3]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"database\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.database;
    }
    
    private static void jdoSetdatabase(final MTable objPC, final MDatabase val) {
        if (objPC.jdoStateManager == null) {
            objPC.database = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 1, objPC.database, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(1);
        }
    }
    
    private static int jdoGetlastAccessTime(final MTable objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return objPC.jdoStateManager.getIntField(objPC, 2, objPC.lastAccessTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"lastAccessTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.lastAccessTime;
    }
    
    private static void jdoSetlastAccessTime(final MTable objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 2, objPC.lastAccessTime, val);
        }
        else {
            objPC.lastAccessTime = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(2);
            }
        }
    }
    
    private static String jdoGetowner(final MTable objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.owner);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"owner\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.owner;
    }
    
    private static void jdoSetowner(final MTable objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.owner, val);
        }
        else {
            objPC.owner = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static Map jdoGetparameters(final MTable objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return (Map)objPC.jdoStateManager.getObjectField(objPC, 4, objPC.parameters);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4) && !((BitSet)objPC.jdoDetachedState[3]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"parameters\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.parameters;
    }
    
    private static void jdoSetparameters(final MTable objPC, final Map val) {
        if (objPC.jdoStateManager == null) {
            objPC.parameters = (Map<String, String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 4, objPC.parameters, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(4);
        }
    }
    
    private static List jdoGetpartitionKeys(final MTable objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 5, objPC.partitionKeys);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5) && !((BitSet)objPC.jdoDetachedState[3]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"partitionKeys\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.partitionKeys;
    }
    
    private static void jdoSetpartitionKeys(final MTable objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.partitionKeys = (List<MFieldSchema>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 5, objPC.partitionKeys, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(5);
        }
    }
    
    private static int jdoGetretention(final MTable objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return objPC.jdoStateManager.getIntField(objPC, 6, objPC.retention);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"retention\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.retention;
    }
    
    private static void jdoSetretention(final MTable objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 6, objPC.retention, val);
        }
        else {
            objPC.retention = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(6);
            }
        }
    }
    
    private static MStorageDescriptor jdoGetsd(final MTable objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 7)) {
            return (MStorageDescriptor)objPC.jdoStateManager.getObjectField(objPC, 7, objPC.sd);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(7) && !((BitSet)objPC.jdoDetachedState[3]).get(7)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"sd\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.sd;
    }
    
    private static void jdoSetsd(final MTable objPC, final MStorageDescriptor val) {
        if (objPC.jdoStateManager == null) {
            objPC.sd = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 7, objPC.sd, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(7);
        }
    }
    
    private static String jdoGettableName(final MTable objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 8)) {
            return objPC.jdoStateManager.getStringField(objPC, 8, objPC.tableName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(8)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"tableName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.tableName;
    }
    
    private static void jdoSettableName(final MTable objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 8, objPC.tableName, val);
        }
        else {
            objPC.tableName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(8);
            }
        }
    }
    
    private static String jdoGettableType(final MTable objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 9)) {
            return objPC.jdoStateManager.getStringField(objPC, 9, objPC.tableType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(9)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"tableType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.tableType;
    }
    
    private static void jdoSettableType(final MTable objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 9, objPC.tableType, val);
        }
        else {
            objPC.tableType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(9);
            }
        }
    }
    
    private static String jdoGetviewExpandedText(final MTable objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 10)) {
            return objPC.jdoStateManager.getStringField(objPC, 10, objPC.viewExpandedText);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(10) && !((BitSet)objPC.jdoDetachedState[3]).get(10)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"viewExpandedText\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.viewExpandedText;
    }
    
    private static void jdoSetviewExpandedText(final MTable objPC, final String val) {
        if (objPC.jdoStateManager == null) {
            objPC.viewExpandedText = val;
        }
        else {
            objPC.jdoStateManager.setStringField(objPC, 10, objPC.viewExpandedText, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(10);
        }
    }
    
    private static String jdoGetviewOriginalText(final MTable objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 11)) {
            return objPC.jdoStateManager.getStringField(objPC, 11, objPC.viewOriginalText);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(11) && !((BitSet)objPC.jdoDetachedState[3]).get(11)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"viewOriginalText\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.viewOriginalText;
    }
    
    private static void jdoSetviewOriginalText(final MTable objPC, final String val) {
        if (objPC.jdoStateManager == null) {
            objPC.viewOriginalText = val;
        }
        else {
            objPC.jdoStateManager.setStringField(objPC, 11, objPC.viewOriginalText, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(11);
        }
    }
}
