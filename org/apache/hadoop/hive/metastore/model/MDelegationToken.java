// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

import javax.jdo.JDODetachedFieldAccessException;
import java.util.BitSet;
import javax.jdo.PersistenceManager;
import javax.jdo.JDOFatalInternalException;
import javax.jdo.identity.StringIdentity;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.spi.StateManager;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.Detachable;

public class MDelegationToken implements Detachable, PersistenceCapable
{
    private String tokenStr;
    private String tokenIdentifier;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MDelegationToken(final String tokenIdentifier, final String tokenStr) {
        this.tokenStr = tokenStr;
        this.tokenIdentifier = tokenIdentifier;
    }
    
    public String getTokenStr() {
        return jdoGettokenStr(this);
    }
    
    public void setTokenStr(final String tokenStr) {
        jdoSettokenStr(this, tokenStr);
    }
    
    public String getTokenIdentifier() {
        return jdoGettokenIdentifier(this);
    }
    
    public void setTokenIdentifier(final String tokenIdentifier) {
        jdoSettokenIdentifier(this, tokenIdentifier);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MDelegationToken"), MDelegationToken.jdoFieldNames, MDelegationToken.jdoFieldTypes, MDelegationToken.jdoFieldFlags, MDelegationToken.jdoPersistenceCapableSuperclass, new MDelegationToken());
    }
    
    protected MDelegationToken() {
    }
    
    @Override
    public void jdoCopyKeyFieldsFromObjectId(final ObjectIdFieldConsumer fc, final Object oid) {
        if (fc == null) {
            throw new IllegalArgumentException("ObjectIdFieldConsumer is null");
        }
        if (!(oid instanceof StringIdentity)) {
            throw new ClassCastException("oid is not instanceof javax.jdo.identity.StringIdentity");
        }
        final StringIdentity o = (StringIdentity)oid;
        fc.storeStringField(0, o.getKey());
    }
    
    protected void jdoCopyKeyFieldsFromObjectId(final Object oid) {
        if (!(oid instanceof StringIdentity)) {
            throw new ClassCastException("key class is not javax.jdo.identity.StringIdentity or null");
        }
        final StringIdentity o = (StringIdentity)oid;
        this.tokenIdentifier = o.getKey();
    }
    
    @Override
    public void jdoCopyKeyFieldsToObjectId(final Object oid) {
        throw new JDOFatalInternalException("It's illegal to call jdoCopyKeyFieldsToObjectId for a class with SingleFieldIdentity.");
    }
    
    @Override
    public void jdoCopyKeyFieldsToObjectId(final ObjectIdFieldSupplier fs, final Object paramObject) {
        throw new JDOFatalInternalException("It's illegal to call jdoCopyKeyFieldsToObjectId for a class with SingleFieldIdentity.");
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
            while (i < MDelegationToken.jdoFieldNames.length) {
                if (MDelegationToken.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MDelegationToken.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MDelegationToken.jdoInheritedFieldCount);
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
        return new StringIdentity(this.getClass(), this.tokenIdentifier);
    }
    
    @Override
    public Object jdoNewObjectIdInstance(final Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (!(key instanceof String)) {
            return new StringIdentity(this.getClass(), (String)key);
        }
        return new StringIdentity(this.getClass(), (String)key);
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
        final MDelegationToken result = new MDelegationToken();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MDelegationToken result = new MDelegationToken();
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
                this.tokenIdentifier = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 1: {
                this.tokenStr = this.jdoStateManager.replacingStringField(this, index);
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
                this.jdoStateManager.providedStringField(this, index, this.tokenIdentifier);
                break;
            }
            case 1: {
                this.jdoStateManager.providedStringField(this, index, this.tokenStr);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MDelegationToken obj, final int index) {
        switch (index) {
            case 0: {
                this.tokenIdentifier = obj.tokenIdentifier;
                break;
            }
            case 1: {
                this.tokenStr = obj.tokenStr;
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
        if (!(obj instanceof MDelegationToken)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MDelegationToken");
        }
        final MDelegationToken other = (MDelegationToken)obj;
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
        return new String[] { "tokenIdentifier", "tokenStr" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { ___jdo$loadClass("java.lang.String"), ___jdo$loadClass("java.lang.String") };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 24, 21 };
    }
    
    protected static int __jdoGetInheritedFieldCount() {
        return 0;
    }
    
    protected static int jdoGetManagedFieldCount() {
        return 2;
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
        final MDelegationToken o = (MDelegationToken)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static String jdoGettokenIdentifier(final MDelegationToken objPC) {
        return objPC.tokenIdentifier;
    }
    
    private static void jdoSettokenIdentifier(final MDelegationToken objPC, final String val) {
        if (objPC.jdoStateManager == null) {
            objPC.tokenIdentifier = val;
        }
        else {
            objPC.jdoStateManager.setStringField(objPC, 0, objPC.tokenIdentifier, val);
        }
        if (objPC.jdoIsDetached()) {
            ((BitSet)objPC.jdoDetachedState[3]).set(0);
        }
    }
    
    private static String jdoGettokenStr(final MDelegationToken objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getStringField(objPC, 1, objPC.tokenStr);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"tokenStr\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.tokenStr;
    }
    
    private static void jdoSettokenStr(final MDelegationToken objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 1, objPC.tokenStr, val);
        }
        else {
            objPC.tokenStr = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
}
