// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.io.IOException;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;

class ValidateMutualRead implements SchemaValidationStrategy
{
    @Override
    public void validate(final Schema toValidate, final Schema existing) throws SchemaValidationException {
        canRead(toValidate, existing);
        canRead(existing, toValidate);
    }
    
    static void canRead(final Schema writtenWith, final Schema readUsing) throws SchemaValidationException {
        boolean error;
        try {
            error = Symbol.hasErrors(new ResolvingGrammarGenerator().generate(writtenWith, readUsing));
        }
        catch (IOException e) {
            throw new SchemaValidationException(readUsing, writtenWith, e);
        }
        if (error) {
            throw new SchemaValidationException(readUsing, writtenWith);
        }
    }
}
