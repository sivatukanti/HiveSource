// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.spi;

import javax.jdo.PersistenceManager;

public interface StateManager
{
    byte replacingFlags(final PersistenceCapable p0);
    
    StateManager replacingStateManager(final PersistenceCapable p0, final StateManager p1);
    
    boolean isDirty(final PersistenceCapable p0);
    
    boolean isTransactional(final PersistenceCapable p0);
    
    boolean isPersistent(final PersistenceCapable p0);
    
    boolean isNew(final PersistenceCapable p0);
    
    boolean isDeleted(final PersistenceCapable p0);
    
    PersistenceManager getPersistenceManager(final PersistenceCapable p0);
    
    void makeDirty(final PersistenceCapable p0, final String p1);
    
    Object getObjectId(final PersistenceCapable p0);
    
    Object getTransactionalObjectId(final PersistenceCapable p0);
    
    Object getVersion(final PersistenceCapable p0);
    
    boolean isLoaded(final PersistenceCapable p0, final int p1);
    
    void preSerialize(final PersistenceCapable p0);
    
    boolean getBooleanField(final PersistenceCapable p0, final int p1, final boolean p2);
    
    char getCharField(final PersistenceCapable p0, final int p1, final char p2);
    
    byte getByteField(final PersistenceCapable p0, final int p1, final byte p2);
    
    short getShortField(final PersistenceCapable p0, final int p1, final short p2);
    
    int getIntField(final PersistenceCapable p0, final int p1, final int p2);
    
    long getLongField(final PersistenceCapable p0, final int p1, final long p2);
    
    float getFloatField(final PersistenceCapable p0, final int p1, final float p2);
    
    double getDoubleField(final PersistenceCapable p0, final int p1, final double p2);
    
    String getStringField(final PersistenceCapable p0, final int p1, final String p2);
    
    Object getObjectField(final PersistenceCapable p0, final int p1, final Object p2);
    
    void setBooleanField(final PersistenceCapable p0, final int p1, final boolean p2, final boolean p3);
    
    void setCharField(final PersistenceCapable p0, final int p1, final char p2, final char p3);
    
    void setByteField(final PersistenceCapable p0, final int p1, final byte p2, final byte p3);
    
    void setShortField(final PersistenceCapable p0, final int p1, final short p2, final short p3);
    
    void setIntField(final PersistenceCapable p0, final int p1, final int p2, final int p3);
    
    void setLongField(final PersistenceCapable p0, final int p1, final long p2, final long p3);
    
    void setFloatField(final PersistenceCapable p0, final int p1, final float p2, final float p3);
    
    void setDoubleField(final PersistenceCapable p0, final int p1, final double p2, final double p3);
    
    void setStringField(final PersistenceCapable p0, final int p1, final String p2, final String p3);
    
    void setObjectField(final PersistenceCapable p0, final int p1, final Object p2, final Object p3);
    
    void providedBooleanField(final PersistenceCapable p0, final int p1, final boolean p2);
    
    void providedCharField(final PersistenceCapable p0, final int p1, final char p2);
    
    void providedByteField(final PersistenceCapable p0, final int p1, final byte p2);
    
    void providedShortField(final PersistenceCapable p0, final int p1, final short p2);
    
    void providedIntField(final PersistenceCapable p0, final int p1, final int p2);
    
    void providedLongField(final PersistenceCapable p0, final int p1, final long p2);
    
    void providedFloatField(final PersistenceCapable p0, final int p1, final float p2);
    
    void providedDoubleField(final PersistenceCapable p0, final int p1, final double p2);
    
    void providedStringField(final PersistenceCapable p0, final int p1, final String p2);
    
    void providedObjectField(final PersistenceCapable p0, final int p1, final Object p2);
    
    boolean replacingBooleanField(final PersistenceCapable p0, final int p1);
    
    char replacingCharField(final PersistenceCapable p0, final int p1);
    
    byte replacingByteField(final PersistenceCapable p0, final int p1);
    
    short replacingShortField(final PersistenceCapable p0, final int p1);
    
    int replacingIntField(final PersistenceCapable p0, final int p1);
    
    long replacingLongField(final PersistenceCapable p0, final int p1);
    
    float replacingFloatField(final PersistenceCapable p0, final int p1);
    
    double replacingDoubleField(final PersistenceCapable p0, final int p1);
    
    String replacingStringField(final PersistenceCapable p0, final int p1);
    
    Object replacingObjectField(final PersistenceCapable p0, final int p1);
    
    Object[] replacingDetachedState(final Detachable p0, final Object[] p1);
}
