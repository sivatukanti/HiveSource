// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.util.Iterator;

public final class ValidateAll implements SchemaValidator
{
    private final SchemaValidationStrategy strategy;
    
    public ValidateAll(final SchemaValidationStrategy strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public void validate(final Schema toValidate, final Iterable<Schema> schemasInOrder) throws SchemaValidationException {
        for (final Schema existing : schemasInOrder) {
            this.strategy.validate(toValidate, existing);
        }
    }
}
