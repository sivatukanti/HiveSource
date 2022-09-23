// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.util.Iterator;

public final class ValidateLatest implements SchemaValidator
{
    private final SchemaValidationStrategy strategy;
    
    public ValidateLatest(final SchemaValidationStrategy strategy) {
        this.strategy = strategy;
    }
    
    @Override
    public void validate(final Schema toValidate, final Iterable<Schema> schemasInOrder) throws SchemaValidationException {
        final Iterator<Schema> schemas = schemasInOrder.iterator();
        if (schemas.hasNext()) {
            final Schema existing = schemas.next();
            this.strategy.validate(toValidate, existing);
        }
    }
}
