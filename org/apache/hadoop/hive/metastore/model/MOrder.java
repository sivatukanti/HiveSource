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

public class MOrder implements Detachable, PersistenceCapable
{
    private String col;
    private int order;
    protected transient StateManager jdoStateManager;
    protected transient byte jdoFlags;
    protected Object[] jdoDetachedState;
    private static final byte[] jdoFieldFlags;
    private static final Class jdoPersistenceCapableSuperclass;
    private static final Class[] jdoFieldTypes;
    private static final String[] jdoFieldNames;
    private static final int jdoInheritedFieldCount;
    
    public MOrder(final String col, final int order) {
        this.col = col;
        this.order = order;
    }
    
    public String getCol() {
        return jdoGetcol(this);
    }
    
    public void setCol(final String col) {
        jdoSetcol(this, col);
    }
    
    public int getOrder() {
        return jdoGetorder(this);
    }
    
    public void setOrder(final int order) {
        jdoSetorder(this, order);
    }
    
    static {
        jdoFieldNames = __jdoFieldNamesInit();
        jdoFieldTypes = __jdoFieldTypesInit();
        jdoFieldFlags = __jdoFieldFlagsInit();
        jdoInheritedFieldCount = __jdoGetInheritedFieldCount();
        jdoPersistenceCapableSuperclass = __jdoPersistenceCapableSuperclassInit();
        JDOImplHelper.registerClass(___jdo$loadClass("org.apache.hadoop.hive.metastore.model.MOrder"), MOrder.jdoFieldNames, MOrder.jdoFieldTypes, MOrder.jdoFieldFlags, MOrder.jdoPersistenceCapableSuperclass, new MOrder());
    }
    
    protected MOrder() {
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
            while (i < MOrder.jdoFieldNames.length) {
                if (MOrder.jdoFieldNames[i].equals(fldName)) {
                    if (((BitSet)this.jdoDetachedState[2]).get(i + MOrder.jdoInheritedFieldCount)) {
                        ((BitSet)this.jdoDetachedState[3]).set(i + MOrder.jdoInheritedFieldCount);
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
        final MOrder result = new MOrder();
        result.jdoFlags = 1;
        result.jdoStateManager = sm;
        return result;
    }
    
    @Override
    public PersistenceCapable jdoNewInstance(final StateManager sm, final Object obj) {
        final MOrder result = new MOrder();
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
                this.col = this.jdoStateManager.replacingStringField(this, index);
                break;
            }
            case 1: {
                this.order = this.jdoStateManager.replacingIntField(this, index);
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
                this.jdoStateManager.providedStringField(this, index, this.col);
                break;
            }
            case 1: {
                this.jdoStateManager.providedIntField(this, index, this.order);
                break;
            }
            default: {
                throw new IllegalArgumentException(new StringBuffer("out of field index :").append(index).toString());
            }
        }
    }
    
    protected final void jdoCopyField(final MOrder obj, final int index) {
        switch (index) {
            case 0: {
                this.col = obj.col;
                break;
            }
            case 1: {
                this.order = obj.order;
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
        if (!(obj instanceof MOrder)) {
            throw new IllegalArgumentException("object is not an object of type org.apache.hadoop.hive.metastore.model.MOrder");
        }
        final MOrder other = (MOrder)obj;
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
        return new String[] { "col", "order" };
    }
    
    private static final Class[] __jdoFieldTypesInit() {
        return new Class[] { ___jdo$loadClass("java.lang.String"), Integer.TYPE };
    }
    
    private static final byte[] __jdoFieldFlagsInit() {
        return new byte[] { 21, 21 };
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
        final MOrder o = (MOrder)super.clone();
        o.jdoFlags = 0;
        o.jdoStateManager = null;
        return o;
    }
    
    private static String jdoGetcol(final MOrder objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 0)) {
            return objPC.jdoStateManager.getStringField(objPC, 0, objPC.col);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(0)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"col\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.col;
    }
    
    private static void jdoSetcol(final MOrder objPC, final String val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setStringField(objPC, 0, objPC.col, val);
        }
        else {
            objPC.col = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(0);
            }
        }
    }
    
    private static int jdoGetorder(final MOrder objPC) {
        if (objPC.jdoFlags > 0 && objPC.jdoStateManager != null && !objPC.jdoStateManager.isLoaded(objPC, 1)) {
            return objPC.jdoStateManager.getIntField(objPC, 1, objPC.order);
        }
        if (objPC.jdoIsDetached() && !((BitSet)objPC.jdoDetachedState[2]).get(1)) {
            throw new JDODetachedFieldAccessException("You have just attempted to access field \"order\" yet this field was not detached when you detached the object. Either dont access this field, or detach it when detaching the object.");
        }
        return objPC.order;
    }
    
    private static void jdoSetorder(final MOrder objPC, final int val) {
        if (objPC.jdoFlags != 0 && objPC.jdoStateManager != null) {
            objPC.jdoStateManager.setIntField(objPC, 1, objPC.order, val);
        }
        else {
            objPC.order = val;
            if (objPC.jdoIsDetached()) {
                ((BitSet)objPC.jdoDetachedState[3]).set(1);
            }
        }
    }
}
