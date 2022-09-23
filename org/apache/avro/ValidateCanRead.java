// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

class ValidateCanRead implements SchemaValidationStrategy
{
    @Override
    public void validate(final Schema toValidate, final Schema existing) throws SchemaValidationException {
        ValidateMutualRead.canRead(existing, toValidate);
    }
}
