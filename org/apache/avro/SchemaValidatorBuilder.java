// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

public final class SchemaValidatorBuilder
{
    private SchemaValidationStrategy strategy;
    
    public SchemaValidatorBuilder strategy(final SchemaValidationStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    public SchemaValidatorBuilder canReadStrategy() {
        this.strategy = new ValidateCanRead();
        return this;
    }
    
    public SchemaValidatorBuilder canBeReadStrategy() {
        this.strategy = new ValidateCanBeRead();
        return this;
    }
    
    public SchemaValidatorBuilder mutualReadStrategy() {
        this.strategy = new ValidateMutualRead();
        return this;
    }
    
    public SchemaValidator validateLatest() {
        this.valid();
        return new ValidateLatest(this.strategy);
    }
    
    public SchemaValidator validateAll() {
        this.valid();
        return new ValidateAll(this.strategy);
    }
    
    private void valid() {
        if (null == this.strategy) {
            throw new AvroRuntimeException("SchemaValidationStrategy not specified in builder");
        }
    }
}
