// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import javax.jdo.PersistenceManager;

public interface PersistenceCapable
{
    public static final byte READ_WRITE_OK = 0;
    public static final byte LOAD_REQUIRED = 1;
    public static final byte READ_OK = -1;
    public static final byte CHECK_READ = 1;
    public static final byte MEDIATE_READ = 2;
    public static final byte CHECK_WRITE = 4;
    public static final byte MEDIATE_WRITE = 8;
    public static final byte SERIALIZABLE = 16;
    
    PersistenceManager jdoGetPersistenceManager();
    
    void jdoReplaceStateManager(final StateManager p0) throws SecurityException;
    
    void jdoProvideField(final int p0);
    
    void jdoProvideFields(final int[] p0);
    
    void jdoReplaceField(final int p0);
    
    void jdoReplaceFields(final int[] p0);
    
    void jdoReplaceFlags();
    
    void jdoCopyFields(final Object p0, final int[] p1);
    
    void jdoMakeDirty(final String p0);
    
    Object jdoGetObjectId();
    
    Object jdoGetTransactionalObjectId();
    
    Object jdoGetVersion();
    
    boolean jdoIsDirty();
    
    boolean jdoIsTransactional();
    
    boolean jdoIsPersistent();
    
    boolean jdoIsNew();
    
    boolean jdoIsDeleted();
    
    boolean jdoIsDetached();
    
    PersistenceCapable jdoNewInstance(final StateManager p0);
    
    PersistenceCapable jdoNewInstance(final StateManager p0, final Object p1);
    
    Object jdoNewObjectIdInstance();
    
    Object jdoNewObjectIdInstance(final Object p0);
    
    void jdoCopyKeyFieldsToObjectId(final Object p0);
    
    void jdoCopyKeyFieldsToObjectId(final ObjectIdFieldSupplier p0, final Object p1);
    
    void jdoCopyKeyFieldsFromObjectId(final ObjectIdFieldConsumer p0, final Object p1);
    
    public interface ObjectIdFieldConsumer
    {
        void storeBooleanField(final int p0, final boolean p1);
        
        void storeCharField(final int p0, final char p1);
        
        void storeByteField(final int p0, final byte p1);
        
        void storeShortField(final int p0, final short p1);
        
        void storeIntField(final int p0, final int p1);
        
        void storeLongField(final int p0, final long p1);
        
        void storeFloatField(final int p0, final float p1);
        
        void storeDoubleField(final int p0, final double p1);
        
        void storeStringField(final int p0, final String p1);
        
        void storeObjectField(final int p0, final Object p1);
    }
    
    public interface ObjectIdFieldSupplier
    {
        boolean fetchBooleanField(final int p0);
        
        char fetchCharField(final int p0);
        
        byte fetchByteField(final int p0);
        
        short fetchShortField(final int p0);
        
        int fetchIntField(final int p0);
        
        long fetchLongField(final int p0);
        
        float fetchFloatField(final int p0);
        
        double fetchDoubleField(final int p0);
        
        String fetchStringField(final int p0);
        
        Object fetchObjectField(final int p0);
    }
    
    public interface ObjectIdFieldManager extends ObjectIdFieldConsumer, ObjectIdFieldSupplier
    {
    }
}
