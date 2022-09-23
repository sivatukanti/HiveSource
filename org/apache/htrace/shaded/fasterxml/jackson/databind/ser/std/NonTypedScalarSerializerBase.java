// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;

public abstract class NonTypedScalarSerializerBase<T> extends StdScalarSerializer<T>
{
    protected NonTypedScalarSerializerBase(final Class<T> t) {
        super(t);
    }
    
    @Override
    public final void serializeWithType(final T value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        this.serialize(value, jgen, provider);
    }
}
