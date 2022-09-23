// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

public interface IndexedRecord extends GenericContainer
{
    void put(final int p0, final Object p1);
    
    Object get(final int p0);
}
