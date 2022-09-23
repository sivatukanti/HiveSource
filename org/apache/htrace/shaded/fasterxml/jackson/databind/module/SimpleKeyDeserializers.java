// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.module;

import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.KeyDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.KeyDeserializers;

public class SimpleKeyDeserializers implements KeyDeserializers, Serializable
{
    private static final long serialVersionUID = -6786398737835438187L;
    protected HashMap<ClassKey, KeyDeserializer> _classMappings;
    
    public SimpleKeyDeserializers() {
        this._classMappings = null;
    }
    
    public SimpleKeyDeserializers addDeserializer(final Class<?> forClass, final KeyDeserializer deser) {
        if (this._classMappings == null) {
            this._classMappings = new HashMap<ClassKey, KeyDeserializer>();
        }
        this._classMappings.put(new ClassKey(forClass), deser);
        return this;
    }
    
    @Override
    public KeyDeserializer findKeyDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) {
        if (this._classMappings == null) {
            return null;
        }
        return this._classMappings.get(new ClassKey(type.getRawClass()));
    }
}
