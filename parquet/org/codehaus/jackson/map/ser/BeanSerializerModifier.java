// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser;

import parquet.org.codehaus.jackson.map.JsonSerializer;
import java.util.List;
import parquet.org.codehaus.jackson.map.introspect.BasicBeanDescription;
import parquet.org.codehaus.jackson.map.SerializationConfig;

public abstract class BeanSerializerModifier
{
    public List<BeanPropertyWriter> changeProperties(final SerializationConfig config, final BasicBeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
        return beanProperties;
    }
    
    public List<BeanPropertyWriter> orderProperties(final SerializationConfig config, final BasicBeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
        return beanProperties;
    }
    
    public BeanSerializerBuilder updateBuilder(final SerializationConfig config, final BasicBeanDescription beanDesc, final BeanSerializerBuilder builder) {
        return builder;
    }
    
    public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BasicBeanDescription beanDesc, final JsonSerializer<?> serializer) {
        return serializer;
    }
}
