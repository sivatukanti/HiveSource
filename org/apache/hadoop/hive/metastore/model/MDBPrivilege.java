// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

import javax.jdo.JDODetachedFieldAccessException;
import java.util.BitSet;
import javax.jdo.PersistenceManager;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.StateManager;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MDBPrivilege implements Detachable, PersistenceCapable
{
    private String principalName;
    private String principalType;
    private MDatabase database;
    private int createTime;
    private String privilege;
    private String grantor;
    private String grantorType;
    private boolean grantOption;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MDBPrivilege() {
    }
    
    public MDBPrivilege(final String principalName, final String principalType, final MDatabase database, final String dbPrivileges, final int createTime, final String grantor, final String grantorType, final boolean grantOption) {
        this.principalName = principalName;
        this.principalType = principalType;
        this.database = database;
        this.privilege = dbPrivileges;
        this.createTime = createTime;
        this.grantorType = grantorType;
        this.grantOption = grantOption;
        this.grantor = grantor;
    }
    
    public String getPrincipalName() {
        return jdoGetprincipalName(this);
    }
    
    public void setPrincipalName(final String userName) {
        jdoSetprincipalName(this, userName);
    }
    
    public String getPrivilege() {
        return jdoGetprivilege(this);
    }
    
    public void setPrivilege(final String dbPrivilege) {
        jdoSetprivilege(this, dbPrivilege);
    }
    
    public MDatabase getDatabase() {
        return jdoGetdatabase(this);
    }
    
    public void setDatabase(final MDatabase database) {
        jdoSetdatabase(this, database);
    }
    
    public int getCreateTime() {
        return jdoGetcreateTime(this);
    }
    
    public void setCreateTime(final int createTime) {
        jdoSetcreateTime(this, createTime);
    }
    
    public String getGrantor() {
        return jdoGetgrantor(this);
    }
    
    public void setGrantor(final String grantor) {
        jdoSetgrantor(this, grantor);
    }
    
    public String getGrantorType() {
        return jdoGetgrantorType(this);
    }
    
    public void setGrantorType(final String grantorType) {
        jdoSetgrantorType(this, grantorType);
    }
    
    public boolean getGrantOption() {
        return jdoGetgrantOption(this);
    }
    
    public void setGrantOption(final boolean grantOption) {
        jdoSetgrantOption(this, grantOption);
    }
    
    public String getPrincipalType() {
        return jdoGetprincipalType(this);
    }
    
    public void setPrincipalType(final String principalType) {
        jdoSetprincipalType(this, principalType);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MDBPrivilege"), MDBPrivilege.jdoFieldNames, MDBPrivilege.jdoFieldTypes, MDBPrivilege.jdoFieldFlags, MDBPrivilege.jdoPersistenceCapableSuperclass, new MDBPrivilege());
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
            while (i < MDBPrivilege.jdoFieldNames.length) {
                if (MDBPrivilege.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MDBPrivilege.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MDBPrivilege.jdoInheritedFieldCount);
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
        final MDBPrivilege result = new MDBPrivilege();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MDBPrivilege result = new MDBPrivilege();
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
                this.grantOption = this.jdoStateManager.replacingBooleanField(this, index);
                break;
            }
            case 3: {
                this.grantor = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.grantorType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 5: {
                this.principalName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 6: {
                this.principalType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 7: {
                this.privilege = this.jdoStateManager.replacingStringField(this, index);
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
                this.jdoStateManager.providedBooleanField(this, index, this.grantOption);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.grantor);
                break;
            }
            case 4: {
                this.jdoStateManager.providedStringField(this, index, this.grantorType);
                break;
            }
            case 5: {
                this.jdoStateManager.providedStringField(this, index, this.principalName);
                break;
            }
            case 6: {
                this.jdoStateManager.providedStringField(this, index, this.principalType);
                break;
            }
            case 7: {
                this.jdoStateManager.providedStringField(this, index, this.privilege);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MDBPrivilege obj, final int index) {
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
                this.grantOption = obj.grantOption;
                break;
            }
            case 3: {
                this.grantor = obj.grantor;
                break;
            }
            case 4: {
                this.grantorType = obj.grantorType;
                break;
            }
            case 5: {
                this.principalName = obj.principalName;
                break;
            }
            case 6: {
                this.principalType = obj.principalType;
                break;
            }
            case 7: {
                this.privilege = obj.privilege;
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
        if (!(obj instanceof MDBPrivilege)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MDBPrivilege");
        }
        final MDBPrivilege other = (MDBPrivilege)obj;
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
        return new String[] { "createTime", "database", "grantOption", "grantor", "grantorType", "principalName", "principalType", "privilege" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { Integer.TYPE, ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MDatabase"), Boolean.TYPE, ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 10, 21, 21, 21, 21, 21, 21 };
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
        final MDBPrivilege o = (MDBPrivilege)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static int jdoGetcreateTime(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getIntField(objPC, 0, objPC.createTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"createTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.createTime;
    }
    
    private static void jdoSetcreateTime(final MDBPrivilege objPC, final int val) {
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
    
    private static MDatabase jdoGetdatabase(final MDBPrivilege objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return (MDatabase)objPC.jdoStateManager.getObjectField(objPC, 1, objPC.database);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1) && !((BitSet)objPC.jdoDetachedState[3]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"database\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.database;
    }
    
    private static void jdoSetdatabase(final MDBPrivilege objPC, final MDatabase val) {
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
    
    private static boolean jdoGetgrantOption(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return objPC.jdoStateManager.getBooleanField(objPC, 2, objPC.grantOption);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"grantOption\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.grantOption;
    }
    
    private static void jdoSetgrantOption(final MDBPrivilege objPC, final boolean val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setBooleanField(objPC, 2, objPC.grantOption, val);
        }
        else {
            objPC.grantOption = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(2);
            }
        }
    }
    
    private static String jdoGetgrantor(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.grantor);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"grantor\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.grantor;
    }
    
    private static void jdoSetgrantor(final MDBPrivilege objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.grantor, val);
        }
        else {
            objPC.grantor = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static String jdoGetgrantorType(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return objPC.jdoStateManager.getStringField(objPC, 4, objPC.grantorType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"grantorType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.grantorType;
    }
    
    private static void jdoSetgrantorType(final MDBPrivilege objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 4, objPC.grantorType, val);
        }
        else {
            objPC.grantorType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(4);
            }
        }
    }
    
    private static String jdoGetprincipalName(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return objPC.jdoStateManager.getStringField(objPC, 5, objPC.principalName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"principalName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.principalName;
    }
    
    private static void jdoSetprincipalName(final MDBPrivilege objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 5, objPC.principalName, val);
        }
        else {
            objPC.principalName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(5);
            }
        }
    }
    
    private static String jdoGetprincipalType(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return objPC.jdoStateManager.getStringField(objPC, 6, objPC.principalType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"principalType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.principalType;
    }
    
    private static void jdoSetprincipalType(final MDBPrivilege objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 6, objPC.principalType, val);
        }
        else {
            objPC.principalType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(6);
            }
        }
    }
    
    private static String jdoGetprivilege(final MDBPrivilege objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 7)) {
            return objPC.jdoStateManager.getStringField(objPC, 7, objPC.privilege);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(7)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"privilege\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.privilege;
    }
    
    private static void jdoSetprivilege(final MDBPrivilege objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 7, objPC.privilege, val);
        }
        else {
            objPC.privilege = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(7);
            }
        }
    }
}
