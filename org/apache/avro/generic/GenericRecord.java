// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.generic;

public interface GenericRecord extends IndexedRecord
{
    void put(final String p0, final Object p1);
    
    Object get(final String p0);
}
