// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

import javax.jdo.JDODetachedFieldAccessException;
import java.util.BitSet;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.StateManager;
import java.util.List;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MFunction implements Detachable, PersistenceCapable
{
    private String functionName;
    private MDatabase database;
    private String className;
    private String ownerName;
    private String ownerType;
    private int createTime;
    private int functionType;
    private List<MResourceUri> resourceUris;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MFunction() {
    }
    
    public MFunction(final String functionName, final MDatabase database, final String className, final String ownerName, final String ownerType, final int createTime, final int functionType, final List<MResourceUri> resourceUris) {
        this.setFunctionName(functionName);
        this.setDatabase(database);
        this.setFunctionType(functionType);
        this.setClassName(className);
        this.setOwnerName(ownerName);
        this.setOwnerType(ownerType);
        this.setCreateTime(createTime);
        this.setResourceUris(resourceUris);
    }
    
    public String getFunctionName() {
        return jdoGetfunctionName(this);
    }
    
    public void setFunctionName(final String functionName) {
        jdoSetfunctionName(this, functionName);
    }
    
    public MDatabase getDatabase() {
        return jdoGetdatabase(this);
    }
    
    public void setDatabase(final MDatabase database) {
        jdoSetdatabase(this, database);
    }
    
    public String getClassName() {
        return jdoGetclassName(this);
    }
    
    public void setClassName(final String className) {
        jdoSetclassName(this, className);
    }
    
    public String getOwnerName() {
        return jdoGetownerName(this);
    }
    
    public void setOwnerName(final String owner) {
        jdoSetownerName(this, owner);
    }
    
    public String getOwnerType() {
        return jdoGetownerType(this);
    }
    
    public void setOwnerType(final String ownerType) {
        jdoSetownerType(this, ownerType);
    }
    
    public int getCreateTime() {
        return jdoGetcreateTime(this);
    }
    
    public void setCreateTime(final int createTime) {
        jdoSetcreateTime(this, createTime);
    }
    
    public int getFunctionType() {
        return jdoGetfunctionType(this);
    }
    
    public void setFunctionType(final int functionType) {
        jdoSetfunctionType(this, functionType);
    }
    
    public List<MResourceUri> getResourceUris() {
        return (List<MResourceUri>)jdoGetresourceUris(this);
    }
    
    public void setResourceUris(final List<MResourceUri> resourceUris) {
        jdoSetresourceUris(this, resourceUris);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MFunction"), MFunction.jdoFieldNames, MFunction.jdoFieldTypes, MFunction.jdoFieldFlags, MFunction.jdoPersistenceCapableSuperclass, new MFunction());
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
            while (i < MFunction.jdoFieldNames.length) {
                if (MFunction.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MFunction.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MFunction.jdoInheritedFieldCount);
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
        final MFunction result = new MFunction();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MFunction result = new MFunction();
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
                this.className = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 1: {
                this.createTime = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 2: {
                this.database = (MDatabase)this.jdoStateManager.replacingObjectField(this, index);
                break;
            }
            case 3: {
                this.functionName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.functionType = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 5: {
                this.ownerName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 6: {
                this.ownerType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 7: {
                this.resourceUris = (List<MResourceUri>)this.jdoStateManager.replacingObjectField(this, index);
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
                this.jdoStateManager.providedStringField(this, index, this.className);
                break;
            }
            case 1: {
                this.jdoStateManager.providedIntField(this, index, this.createTime);
                break;
            }
            case 2: {
                this.jdoStateManager.providedObjectField(this, index, this.database);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.functionName);
                break;
            }
            case 4: {
                this.jdoStateManager.providedIntField(this, index, this.functionType);
                break;
            }
            case 5: {
                this.jdoStateManager.providedStringField(this, index, this.ownerName);
                break;
            }
            case 6: {
                this.jdoStateManager.providedStringField(this, index, this.ownerType);
                break;
            }
            case 7: {
                this.jdoStateManager.providedObjectField(this, index, this.resourceUris);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MFunction obj, final int index) {
        switch (index) {
            case 0: {
                this.className = obj.className;
                break;
            }
            case 1: {
                this.createTime = obj.createTime;
                break;
            }
            case 2: {
                this.database = obj.database;
                break;
            }
            case 3: {
                this.functionName = obj.functionName;
                break;
            }
            case 4: {
                this.functionType = obj.functionType;
                break;
            }
            case 5: {
                this.ownerName = obj.ownerName;
                break;
            }
            case 6: {
                this.ownerType = obj.ownerType;
                break;
            }
            case 7: {
                this.resourceUris = obj.resourceUris;
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
        if (!(obj instanceof MFunction)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MFunction");
        }
        final MFunction other = (MFunction)obj;
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
        return new String[] { "className", "createTime", "database", "functionName", "functionType", "ownerName", "ownerType", "resourceUris" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { ___jdo$loadClass("java.lang.String"), Integer.TYPE, ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MDatabase"), ___jdo$loadClass("java.lang.String"), Integer.TYPE, ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.util.List") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 21, 10, 21, 21, 21, 21, 10 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 8;
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
        final MFunction o = (MFunction)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static String jdoGetclassName(final MFunction objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getStringField(objPC, 0, objPC.className);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"className\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.className;
    }
    
    private static void jdoSetclassName(final MFunction objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 0, objPC.className, val);
        }
        else {
            objPC.className = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(0);
            }
        }
    }
    
    private static int jdoGetcreateTime(final MFunction objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getIntField(objPC, 1, objPC.createTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"createTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.createTime;
    }
    
    private static void jdoSetcreateTime(final MFunction objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 1, objPC.createTime, val);
        }
        else {
            objPC.createTime = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
    
    private static MDatabase jdoGetdatabase(final MFunction objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return (MDatabase)objPC.jdoStateManager.getObjectField(objPC, 2, objPC.database);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2) && !((BitSet)objPC.jdoDetachedState[3]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"database\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.database;
    }
    
    private static void jdoSetdatabase(final MFunction objPC, final MDatabase val) {
        if (objPC.jdoStateManager == null) {
            objPC.database = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 2, objPC.database, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(2);
        }
    }
    
    private static String jdoGetfunctionName(final MFunction objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.functionName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"functionName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.functionName;
    }
    
    private static void jdoSetfunctionName(final MFunction objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.functionName, val);
        }
        else {
            objPC.functionName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static int jdoGetfunctionType(final MFunction objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return objPC.jdoStateManager.getIntField(objPC, 4, objPC.functionType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"functionType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.functionType;
    }
    
    private static void jdoSetfunctionType(final MFunction objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 4, objPC.functionType, val);
        }
        else {
            objPC.functionType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(4);
            }
        }
    }
    
    private static String jdoGetownerName(final MFunction objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return objPC.jdoStateManager.getStringField(objPC, 5, objPC.ownerName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"ownerName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.ownerName;
    }
    
    private static void jdoSetownerName(final MFunction objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 5, objPC.ownerName, val);
        }
        else {
            objPC.ownerName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(5);
            }
        }
    }
    
    private static String jdoGetownerType(final MFunction objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return objPC.jdoStateManager.getStringField(objPC, 6, objPC.ownerType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"ownerType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.ownerType;
    }
    
    private static void jdoSetownerType(final MFunction objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 6, objPC.ownerType, val);
        }
        else {
            objPC.ownerType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(6);
            }
        }
    }
    
    private static List jdoGetresourceUris(final MFunction objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 7)) {
            return (List)objPC.jdoStateManager.getObjectField(objPC, 7, objPC.resourceUris);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(7) && !((BitSet)objPC.jdoDetachedState[3]).get(7)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"resourceUris\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.resourceUris;
    }
    
    private static void jdoSetresourceUris(final MFunction objPC, final List val) {
        if (objPC.jdoStateManager == null) {
            objPC.resourceUris = (List<MResourceUri>)val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 7, objPC.resourceUris, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(7);
        }
    }
}
