// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import org.datanucleus.store.fieldmanager.FieldConsumer;
import org.datanucleus.store.fieldmanager.FieldSupplier;
import org.datanucleus.state.ObjectProvider;

public interface Persistable
{
    public static final byte READ_WRITE_OK = 0;
    public static final byte LOAD_REQUIRED = 1;
    public static final byte READ_OK = -1;
    public static final byte CHECK_READ = 1;
    public static final byte MEDIATE_READ = 2;
    public static final byte CHECK_WRITE = 4;
    public static final byte MEDIATE_WRITE = 8;
    public static final byte SERIALIZABLE = 16;
    
    ObjectProvider dnGetStateManager();
    
    void dnReplaceStateManager(final ObjectProvider p0) throws SecurityException;
    
    void dnProvideField(final int p0);
    
    void dnProvideFields(final int[] p0);
    
    void dnReplaceField(final int p0);
    
    void dnReplaceFields(final int[] p0);
    
    void dnReplaceFlags();
    
    void dnCopyFields(final Object p0, final int[] p1);
    
    void dnMakeDirty(final String p0);
    
    Object dnGetObjectId();
    
    Object dnGetTransactionalObjectId();
    
    Object dnGetVersion();
    
    boolean dnIsDirty();
    
    boolean dnIsTransactional();
    
    boolean dnIsPersistent();
    
    boolean dnIsNew();
    
    boolean dnIsDeleted();
    
    boolean dnIsDetached();
    
    Persistable dnNewInstance(final ObjectProvider p0);
    
    Persistable dnNewInstance(final ObjectProvider p0, final Object p1);
    
    Object dnNewObjectIdInstance();
    
    Object dnNewObjectIdInstance(final Object p0);
    
    void dnCopyKeyFieldsToObjectId(final Object p0);
    
    void dnCopyKeyFieldsToObjectId(final FieldSupplier p0, final Object p1);
    
    void dnCopyKeyFieldsFromObjectId(final FieldConsumer p0, final Object p1);
    
    void dnReplaceDetachedState();
}
