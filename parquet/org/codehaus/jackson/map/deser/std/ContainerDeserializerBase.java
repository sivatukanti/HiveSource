// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.type.JavaType;

public abstract class ContainerDeserializerBase<T> extends StdDeserializer<T>
{
    protected ContainerDeserializerBase(final Class<?> selfType) {
        super(selfType);
    }
    
    public abstract JavaType getContentType();
    
    public abstract JsonDeserializer<Object> getContentDeserializer();
}
