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

public class MRoleMap implements Detachable, PersistenceCapable
{
    private String principalName;
    private String principalType;
    private MRole role;
    private int addTime;
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
    
    public MRoleMap() {
    }
    
    public MRoleMap(final String principalName, final String principalType, final MRole role, final int addTime, final String grantor, final String grantorType, final boolean grantOption) {
        this.principalName = principalName;
        this.principalType = principalType;
        this.role = role;
        this.addTime = addTime;
        this.grantor = grantor;
        this.grantorType = grantorType;
        this.grantOption = grantOption;
    }
    
    public String getPrincipalName() {
        return jdoGetprincipalName(this);
    }
    
    public void setPrincipalName(final String userName) {
        jdoSetprincipalName(this, userName);
    }
    
    public String getPrincipalType() {
        return jdoGetprincipalType(this);
    }
    
    public void setPrincipalType(final String principalType) {
        jdoSetprincipalType(this, principalType);
    }
    
    public int getAddTime() {
        return jdoGetaddTime(this);
    }
    
    public void setAddTime(final int addTime) {
        jdoSetaddTime(this, addTime);
    }
    
    public MRole getRole() {
        return jdoGetrole(this);
    }
    
    public void setRole(final MRole role) {
        jdoSetrole(this, role);
    }
    
    public boolean getGrantOption() {
        return jdoGetgrantOption(this);
    }
    
    public void setGrantOption(final boolean grantOption) {
        jdoSetgrantOption(this, grantOption);
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
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MRoleMap"), MRoleMap.jdoFieldNames, MRoleMap.jdoFieldTypes, MRoleMap.jdoFieldFlags, MRoleMap.jdoPersistenceCapableSuperclass, new MRoleMap());
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
            while (i < MRoleMap.jdoFieldNames.length) {
                if (MRoleMap.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MRoleMap.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MRoleMap.jdoInheritedFieldCount);
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
        final MRoleMap result = new MRoleMap();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MRoleMap result = new MRoleMap();
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
                this.addTime = this.jdoStateManager.replacingIntField(this, index);
                break;
            }
            case 1: {
                this.grantOption = this.jdoStateManager.replacingBooleanField(this, index);
                break;
            }
            case 2: {
                this.grantor = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 3: {
                this.grantorType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 4: {
                this.principalName = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 5: {
                this.principalType = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 6: {
                this.role = (MRole)this.jdoStateManager.replacingObjectField(this, index);
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
                this.jdoStateManager.providedIntField(this, index, this.addTime);
                break;
            }
            case 1: {
                this.jdoStateManager.providedBooleanField(this, index, this.grantOption);
                break;
            }
            case 2: {
                this.jdoStateManager.providedStringField(this, index, this.grantor);
                break;
            }
            case 3: {
                this.jdoStateManager.providedStringField(this, index, this.grantorType);
                break;
            }
            case 4: {
                this.jdoStateManager.providedStringField(this, index, this.principalName);
                break;
            }
            case 5: {
                this.jdoStateManager.providedStringField(this, index, this.principalType);
                break;
            }
            case 6: {
                this.jdoStateManager.providedObjectField(this, index, this.role);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MRoleMap obj, final int index) {
        switch (index) {
            case 0: {
                this.addTime = obj.addTime;
                break;
            }
            case 1: {
                this.grantOption = obj.grantOption;
                break;
            }
            case 2: {
                this.grantor = obj.grantor;
                break;
            }
            case 3: {
                this.grantorType = obj.grantorType;
                break;
            }
            case 4: {
                this.principalName = obj.principalName;
                break;
            }
            case 5: {
                this.principalType = obj.principalType;
                break;
            }
            case 6: {
                this.role = obj.role;
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
        if (!(obj instanceof MRoleMap)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MRoleMap");
        }
        final MRoleMap other = (MRoleMap)obj;
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
        return new String[] { "addTime", "grantOption", "grantor", "grantorType", "principalName", "principalType", "role" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { Integer.TYPE, Boolean.TYPE, ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MRole") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 21, 21, 21, 21, 21, 10 };
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
        final MRoleMap o = (MRoleMap)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static int jdoGetaddTime(final MRoleMap objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getIntField(objPC, 0, objPC.addTime);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"addTime\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.addTime;
    }
    
    private static void jdoSetaddTime(final MRoleMap objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 0, objPC.addTime, val);
        }
        else {
            objPC.addTime = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(0);
            }
        }
    }
    
    private static boolean jdoGetgrantOption(final MRoleMap objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getBooleanField(objPC, 1, objPC.grantOption);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"grantOption\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.grantOption;
    }
    
    private static void jdoSetgrantOption(final MRoleMap objPC, final boolean val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setBooleanField(objPC, 1, objPC.grantOption, val);
        }
        else {
            objPC.grantOption = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
    
    private static String jdoGetgrantor(final MRoleMap objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 2)) {
            return objPC.jdoStateManager.getStringField(objPC, 2, objPC.grantor);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(2)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"grantor\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.grantor;
    }
    
    private static void jdoSetgrantor(final MRoleMap objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 2, objPC.grantor, val);
        }
        else {
            objPC.grantor = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(2);
            }
        }
    }
    
    private static String jdoGetgrantorType(final MRoleMap objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 3)) {
            return objPC.jdoStateManager.getStringField(objPC, 3, objPC.grantorType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(3)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"grantorType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.grantorType;
    }
    
    private static void jdoSetgrantorType(final MRoleMap objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 3, objPC.grantorType, val);
        }
        else {
            objPC.grantorType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(3);
            }
        }
    }
    
    private static String jdoGetprincipalName(final MRoleMap objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 4)) {
            return objPC.jdoStateManager.getStringField(objPC, 4, objPC.principalName);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(4)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"principalName\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.principalName;
    }
    
    private static void jdoSetprincipalName(final MRoleMap objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 4, objPC.principalName, val);
        }
        else {
            objPC.principalName = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(4);
            }
        }
    }
    
    private static String jdoGetprincipalType(final MRoleMap objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 5)) {
            return objPC.jdoStateManager.getStringField(objPC, 5, objPC.principalType);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(5)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"principalType\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.principalType;
    }
    
    private static void jdoSetprincipalType(final MRoleMap objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 5, objPC.principalType, val);
        }
        else {
            objPC.principalType = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(5);
            }
        }
    }
    
    private static MRole jdoGetrole(final MRoleMap objPC) {
        if (objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 6)) {
            return (MRole)objPC.jdoStateManager.getObjectField(objPC, 6, objPC.role);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(6) && !((BitSet)objPC.jdoDetachedState[3]).get(6)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"role\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.role;
    }
    
    private static void jdoSetrole(final MRoleMap objPC, final MRole val) {
        if (objPC.jdoStateManager == null) {
            objPC.role = val;
        }
        else {
            objPC.jdoStateManager.setObjectField(objPC, 6, objPC.role, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(6);
        }
    }
}
