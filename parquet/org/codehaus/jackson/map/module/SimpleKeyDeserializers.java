// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.module;

import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.map.BeanDescription;
import parquet.org.codehaus.jackson.map.DeserializationConfig;
import parquet.org.codehaus.jackson.type.JavaType;
import parquet.org.codehaus.jackson.map.KeyDeserializer;
import parquet.org.codehaus.jackson.map.type.ClassKey;
import java.util.HashMap;
import parquet.org.codehaus.jackson.map.KeyDeserializers;

public class SimpleKeyDeserializers implements KeyDeserializers
{
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
    
    public KeyDeserializer findKeyDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc, final BeanProperty property) {
        if (this._classMappings == null) {
            return null;
        }
        return this._classMappings.get(new ClassKey(type.getRawClass()));
    }
}
