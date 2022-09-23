// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.fieldmanager;

public interface FieldSupplier
{
    boolean fetchBooleanField(final int p0);
    
    byte fetchByteField(final int p0);
    
    char fetchCharField(final int p0);
    
    double fetchDoubleField(final int p0);
    
    float fetchFloatField(final int p0);
    
    int fetchIntField(final int p0);
    
    long fetchLongField(final int p0);
    
    short fetchShortField(final int p0);
    
    String fetchStringField(final int p0);
    
    Object fetchObjectField(final int p0);
}
