// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import java.util.Collection;
import java.lang.reflect.Constructor;
import parquet.org.codehaus.jackson.map.TypeDeserializer;
import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.type.JavaType;

@Deprecated
public class CollectionDeserializer extends parquet.org.codehaus.jackson.map.deser.std.CollectionDeserializer
{
    @Deprecated
    public CollectionDeserializer(final JavaType collectionType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final Constructor<Collection<Object>> defCtor) {
        super(collectionType, valueDeser, valueTypeDeser, defCtor);
    }
    
    public CollectionDeserializer(final JavaType collectionType, final JsonDeserializer<Object> valueDeser, final TypeDeserializer valueTypeDeser, final ValueInstantiator valueInstantiator) {
        super(collectionType, valueDeser, valueTypeDeser, valueInstantiator);
    }
    
    protected CollectionDeserializer(final CollectionDeserializer src) {
        super(src);
    }
}
