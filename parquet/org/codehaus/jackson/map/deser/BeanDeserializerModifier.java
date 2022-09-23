// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.map.introspect.BasicBeanDescription;
import parquet.org.codehaus.jackson.map.DeserializationConfig;

public abstract class BeanDeserializerModifier
{
    public BeanDeserializerBuilder updateBuilder(final DeserializationConfig config, final BasicBeanDescription beanDesc, final BeanDeserializerBuilder builder) {
        return builder;
    }
    
    public JsonDeserializer<?> modifyDeserializer(final DeserializationConfig config, final BasicBeanDescription beanDesc, final JsonDeserializer<?> deserializer) {
        return deserializer;
    }
}
