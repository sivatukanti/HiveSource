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

public class MPartition implements Detachable, PersistenceCapable
{
    private String partitionName;
    private MTable table;
    private List<String> values;
    private int createTime;
    private int lastAccessTime;
    private MStorageDescriptor sd;
    private Map<String, String> parameters;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MPartition() {
    }
    
    public MPartition(final String partitionName, final MTable table, final List<String> values, final int createTime, final int lastAccessTime, final MStorageDescriptor sd, final Map<String, String> parameters) {
        this.partitionName = partitionName;
        this.table = table;
        this.values = values;
        this.createTime = createTime;
        this.lastAccessTime = lastAccessTime;
        this.sd = sd;
        this.parameters = parameters;
    }
    
    public int getLastAccessTime() {
        return jdoGetlastAccessTime(this);
    }
    
    public void setLastAccessTime(final int lastAccessTime) {
        jdoSetlastAccessTime(this, lastAccessTime);
    }
    
    public List<String> getValues() {
        return (List<String>)jdoGetvalues(this);
    }
    
    public void setValues(final List<String> values) {
        jdoSetvalues(this, values);
    }
    
    public MTable getTable() {
        return jdoGettable(this);
    }
    
    public void setTable(final MTable table) {
        jdoSettable(this, table);
    }
    
    public MStorageDescriptor getSd() {
        return jdoGetsd(this);
    }
    
    public void setSd(final MStorageDescriptor sd) {
        jdoSetsd(this, sd);
    }
    
    public Map<String, String> getParameters() {
        return (Map<String, String>)jdoGetparameters(this);
    }
    
    public void setParameters(final Map<String, String> parameters) {
        jdoSetparameters(this, parameters);
    }
    
    public String getPartitionName() {
        return jdoGetpartitionName(this);
    }
    
    public void setPartitionName(final String partitionName) {
        jdoSetpartitionName(this, partitionName);
    }
    
    public int getCreateTime() {
        return jdoGetcreateTime(this);
    }
    
    public void setCreateTime(final int createTime) {
        jdoSetcreateTime(this, createTime);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MPartition"), MPartition.jdoFieldNames, MPartition.jdoFieldTypes, MPartition.jdoFieldFlags, MPartition.jdoPersistenceCapableSuperclass, new MPartition());
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
            while (i < MPartition.jdoFieldNames.length) {
                if (MPartition.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MPartition.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MPartition.jdoInheritedFieldCount);
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
        final MPartition result = new MPartition();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MPartition result = new MPartition();
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
                this.lastAccessTime = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 2: {
                this.parameters = (Map<String, String>)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 3: {
                this.partitionName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.sd = (MStorageDescriptor)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 5: {
                this.table = (MTable)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 6: {
                this.values = (List<String>)this.jdoStateManager.replacingObjectField(this, index);
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
                this.jdoStateManager.providedIntField(this, index, this.lastAccessTime);
                break;
            }
            case 2: {
                this.jdoStateManager.providedObjectField(this, index, this.parameters);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.partitionName);
                break;
            }
            case 4: {
                this.jdoStateManager.providedObjectField(this, index, this.sd);
                break;
            }
            case 5: {
                this.jdoStateManager.providedObjectField(this, index, this.table);
                break;
            }
            case 6: {
                this.jdoStateManager.providedObjectField(this, index, this.values);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MPartition obj, final int index) {
        switch (index) {
            case 0: {
                this.createTime = obj.createTime;
                break;
            }
            case 1: {
                this.lastAccessTime = obj.lastAccessTime;
                break;
            }
            case 2: {
                this.parameters = obj.parameters;
                break;
            }
            case 3: {
                this.partitionName = obj.partitionName;
                break;
            }
            case 4: {
                this.sd = obj.sd;
                break;
            }
            case 5: {
                this.table = obj.table;
                break;
            }
            case 6: {
                this.values = obj.values;
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
        if (!(obj instanceof MPartition)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MPartition");
        }
        final MPartition other = (MPartition)obj;
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
        return new String[] { "createTime", "lastAccessTime", "parameters", "partitionName", "sd", "table", "values" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { Integer.TYPE, Integer.TYPE, ___jdo$loadClass("java.util.Map"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MStorageDescriptor"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MTable"), ___jdo$loadClass("java.util.List") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 21, 10, 21, 10, 10, 10 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 7;
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
        final MPartition o = (MPartition)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static int jdoGetcreateTime(final MPartition objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getIntField(objPC, 0, objPC.createTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"createTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.createTime;
    }
    
    private static void jdoSetcreateTime(final MPartition objPC, final int val) {
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
    
    private static int jdoGetlastAccessTime(final MPartition objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getIntField(objPC, 1, objPC.lastAccessTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"lastAccessTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.lastAccessTime;
    }
    
    private static void jdoSetlastAccessTime(final MPartition objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 1, objPC.lastAccessTime, val);
        }
        else {
            objPC.lastAccessTime = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
    
    private static Map jdoGetparameters(final MPartition objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return (Map)objPC.jdoStateManager.getObjectField(objPC, 2, objPC.parameters);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2) && !((BitSet)objPC.jdoDetachedState[3]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"parameters\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.parameters;
    }
    
    private static void jdoSetparameters(final MPartition objPC, final Map val) {
        if (objPC.jdoStateManager == null) {
            objPC.parameters = (Map<String, String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 2, objPC.parameters, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(2);
        }
    }
    
    private static String jdoGetpartitionName(final MPartition objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.partitionName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"partitionName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.partitionName;
    }
    
    private static void jdoSetpartitionName(final MPartition objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.partitionName, val);
        }
        else {
            objPC.partitionName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static MStorageDescriptor jdoGetsd(final MPartition objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return (MStorageDescriptor)objPC.jdoStateManager.getObjectField(objPC, 4, objPC.sd);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4) && !((BitSet)objPC.jdoDetachedState[3]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"sd\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.sd;
    }
    
    private static void jdoSetsd(final MPartition objPC, final MStorageDescriptor val) {
        if (objPC.jdoStateManager == null) {
            objPC.sd = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 4, objPC.sd, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(4);
        }
    }
    
    private static MTable jdoGettable(final MPartition objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return (MTable)objPC.jdoStateManager.getObjectField(objPC, 5, objPC.table);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5) && !((BitSet)objPC.jdoDetachedState[3]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"table\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.table;
    }
    
    private static void jdoSettable(final MPartition objPC, final MTable val) {
        if (objPC.jdoStateManager == null) {
            objPC.table = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 5, objPC.table, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(5);
        }
    }
    
    private static List jdoGetvalues(final MPartition objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 6, objPC.values);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6) && !((BitSet)objPC.jdoDetachedState[3]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"values\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.values;
    }
    
    private static void jdoSetvalues(final MPartition objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.values = (List<String>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 6, objPC.values, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(6);
        }
    }
}
