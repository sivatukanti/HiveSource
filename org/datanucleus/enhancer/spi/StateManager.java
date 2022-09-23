// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import org.datanucleus.ExecutionContext;

public interface StateManager
{
    byte replacingFlags(final Persistable p0);
    
    StateManager replacingStateManager(final Persistable p0, final StateManager p1);
    
    boolean isDirty(final Persistable p0);
    
    boolean isTransactional(final Persistable p0);
    
    boolean isPersistent(final Persistable p0);
    
    boolean isNew(final Persistable p0);
    
    boolean isDeleted(final Persistable p0);
    
    ExecutionContext getExecutionContext(final Persistable p0);
    
    void makeDirty(final Persistable p0, final String p1);
    
    Object getObjectId(final Persistable p0);
    
    Object getTransactionalObjectId(final Persistable p0);
    
    Object getVersion(final Persistable p0);
    
    boolean isLoaded(final Persistable p0, final int p1);
    
    void preSerialize(final Persistable p0);
    
    boolean getBooleanField(final Persistable p0, final int p1, final boolean p2);
    
    char getCharField(final Persistable p0, final int p1, final char p2);
    
    byte getByteField(final Persistable p0, final int p1, final byte p2);
    
    short getShortField(final Persistable p0, final int p1, final short p2);
    
    int getIntField(final Persistable p0, final int p1, final int p2);
    
    long getLongField(final Persistable p0, final int p1, final long p2);
    
    float getFloatField(final Persistable p0, final int p1, final float p2);
    
    double getDoubleField(final Persistable p0, final int p1, final double p2);
    
    String getStringField(final Persistable p0, final int p1, final String p2);
    
    Object getObjectField(final Persistable p0, final int p1, final Object p2);
    
    void setBooleanField(final Persistable p0, final int p1, final boolean p2, final boolean p3);
    
    void setCharField(final Persistable p0, final int p1, final char p2, final char p3);
    
    void setByteField(final Persistable p0, final int p1, final byte p2, final byte p3);
    
    void setShortField(final Persistable p0, final int p1, final short p2, final short p3);
    
    void setIntField(final Persistable p0, final int p1, final int p2, final int p3);
    
    void setLongField(final Persistable p0, final int p1, final long p2, final long p3);
    
    void setFloatField(final Persistable p0, final int p1, final float p2, final float p3);
    
    void setDoubleField(final Persistable p0, final int p1, final double p2, final double p3);
    
    void setStringField(final Persistable p0, final int p1, final String p2, final String p3);
    
    void setObjectField(final Persistable p0, final int p1, final Object p2, final Object p3);
    
    void providedBooleanField(final Persistable p0, final int p1, final boolean p2);
    
    void providedCharField(final Persistable p0, final int p1, final char p2);
    
    void providedByteField(final Persistable p0, final int p1, final byte p2);
    
    void providedShortField(final Persistable p0, final int p1, final short p2);
    
    void providedIntField(final Persistable p0, final int p1, final int p2);
    
    void providedLongField(final Persistable p0, final int p1, final long p2);
    
    void providedFloatField(final Persistable p0, final int p1, final float p2);
    
    void providedDoubleField(final Persistable p0, final int p1, final double p2);
    
    void providedStringField(final Persistable p0, final int p1, final String p2);
    
    void providedObjectField(final Persistable p0, final int p1, final Object p2);
    
    boolean replacingBooleanField(final Persistable p0, final int p1);
    
    char replacingCharField(final Persistable p0, final int p1);
    
    byte replacingByteField(final Persistable p0, final int p1);
    
    short replacingShortField(final Persistable p0, final int p1);
    
    int replacingIntField(final Persistable p0, final int p1);
    
    long replacingLongField(final Persistable p0, final int p1);
    
    float replacingFloatField(final Persistable p0, final int p1);
    
    double replacingDoubleField(final Persistable p0, final int p1);
    
    String replacingStringField(final Persistable p0, final int p1);
    
    Object replacingObjectField(final Persistable p0, final int p1);
    
    Object[] replacingDetachedState(final Persistable p0, final Object[] p1);
}
