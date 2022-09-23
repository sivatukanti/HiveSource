// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

public interface SchemaValidationStrategy
{
    void validate(final Schema p0, final Schema p1) throws SchemaValidationException;
}
