// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map;

import parquet.org.codehaus.jackson.type.JavaType;

public interface KeyDeserializers
{
    KeyDeserializer findKeyDeserializer(final JavaType p0, final DeserializationConfig p1, final BeanDescription p2, final BeanProperty p3) throws JsonMappingException;
}
