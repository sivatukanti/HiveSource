// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map;

public interface ContextualSerializer<T>
{
    JsonSerializer<T> createContextual(final SerializationConfig p0, final BeanProperty p1) throws JsonMappingException;
}
