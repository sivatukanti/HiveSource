// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

public interface FieldConsumer
{
    void storeBooleanField(final int p0, final boolean p1);
    
    void storeByteField(final int p0, final byte p1);
    
    void storeCharField(final int p0, final char p1);
    
    void storeDoubleField(final int p0, final double p1);
    
    void storeFloatField(final int p0, final float p1);
    
    void storeIntField(final int p0, final int p1);
    
    void storeLongField(final int p0, final long p1);
    
    void storeShortField(final int p0, final short p1);
    
    void storeStringField(final int p0, final String p1);
    
    void storeObjectField(final int p0, final Object p1);
}
