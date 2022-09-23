// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

public interface SchemaValidator
{
    void validate(final Schema p0, final Iterable<Schema> p1) throws SchemaValidationException;
}
