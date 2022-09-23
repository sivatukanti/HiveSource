// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.core.io.SerializedString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.ObjectIdGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public final class ObjectIdWriter
{
    public final JavaType idType;
    public final SerializableString propertyName;
    public final ObjectIdGenerator<?> generator;
    public final JsonSerializer<Object> serializer;
    public final boolean alwaysAsId;
    
    protected ObjectIdWriter(final JavaType t, final SerializableString propName, final ObjectIdGenerator<?> gen, final JsonSerializer<?> ser, final boolean alwaysAsId) {
        this.idType = t;
        this.propertyName = propName;
        this.generator = gen;
        this.serializer = (JsonSerializer<Object>)ser;
        this.alwaysAsId = alwaysAsId;
    }
    
    public static ObjectIdWriter construct(final JavaType idType, final PropertyName propName, final ObjectIdGenerator<?> generator, final boolean alwaysAsId) {
        final String simpleName = (propName == null) ? null : propName.getSimpleName();
        return construct(idType, simpleName, generator, alwaysAsId);
    }
    
    @Deprecated
    public static ObjectIdWriter construct(final JavaType idType, final String propName, final ObjectIdGenerator<?> generator, final boolean alwaysAsId) {
        final SerializableString serName = (propName == null) ? null : new SerializedString(propName);
        return new ObjectIdWriter(idType, serName, generator, null, alwaysAsId);
    }
    
    public ObjectIdWriter withSerializer(final JsonSerializer<?> ser) {
        return new ObjectIdWriter(this.idType, this.propertyName, this.generator, ser, this.alwaysAsId);
    }
    
    public ObjectIdWriter withAlwaysAsId(final boolean newState) {
        if (newState == this.alwaysAsId) {
            return this;
        }
        return new ObjectIdWriter(this.idType, this.propertyName, this.generator, this.serializer, newState);
    }
}
