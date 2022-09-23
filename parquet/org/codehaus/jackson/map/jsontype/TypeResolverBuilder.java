// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.jsontype;

import parquet.org.codehaus.jackson.annotate.JsonTypeInfo;
import parquet.org.codehaus.jackson.map.TypeDeserializer;
import parquet.org.codehaus.jackson.map.DeserializationConfig;
import parquet.org.codehaus.jackson.map.TypeSerializer;
import parquet.org.codehaus.jackson.map.BeanProperty;
import java.util.Collection;
import parquet.org.codehaus.jackson.type.JavaType;
import parquet.org.codehaus.jackson.map.SerializationConfig;

public interface TypeResolverBuilder<T extends TypeResolverBuilder<T>>
{
    Class<?> getDefaultImpl();
    
    TypeSerializer buildTypeSerializer(final SerializationConfig p0, final JavaType p1, final Collection<NamedType> p2, final BeanProperty p3);
    
    TypeDeserializer buildTypeDeserializer(final DeserializationConfig p0, final JavaType p1, final Collection<NamedType> p2, final BeanProperty p3);
    
    T init(final JsonTypeInfo.Id p0, final TypeIdResolver p1);
    
    T inclusion(final JsonTypeInfo.As p0);
    
    T typeProperty(final String p0);
    
    T defaultImpl(final Class<?> p0);
}
