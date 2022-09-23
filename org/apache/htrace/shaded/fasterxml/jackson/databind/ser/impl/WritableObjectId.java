// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;

public final class WritableObjectId
{
    public final ObjectIdGenerator<?> generator;
    public Object id;
    protected boolean idWritten;
    
    public WritableObjectId(final ObjectIdGenerator<?> generator) {
        this.idWritten = false;
        this.generator = generator;
    }
    
    public boolean writeAsId(final JsonGenerator jgen, final SerializerProvider provider, final ObjectIdWriter w) throws IOException, JsonGenerationException {
        if (this.id != null && (this.idWritten || w.alwaysAsId)) {
            if (jgen.canWriteObjectId()) {
                jgen.writeObjectRef(String.valueOf(this.id));
            }
            else {
                w.serializer.serialize(this.id, jgen, provider);
            }
            return true;
        }
        return false;
    }
    
    public Object generateId(final Object forPojo) {
        return this.id = this.generator.generateId(forPojo);
    }
    
    public void writeAsField(final JsonGenerator jgen, final SerializerProvider provider, final ObjectIdWriter w) throws IOException, JsonGenerationException {
        this.idWritten = true;
        if (jgen.canWriteObjectId()) {
            jgen.writeObjectId(String.valueOf(this.id));
            return;
        }
        final SerializableString name = w.propertyName;
        if (name != null) {
            jgen.writeFieldName(name);
            w.serializer.serialize(this.id, jgen, provider);
        }
    }
}
