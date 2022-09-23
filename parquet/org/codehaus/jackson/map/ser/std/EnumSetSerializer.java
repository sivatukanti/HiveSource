// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import java.util.Iterator;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.JsonSerializer;
import parquet.org.codehaus.jackson.map.TypeSerializer;
import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.type.JavaType;
import java.util.EnumSet;

public class EnumSetSerializer extends AsArraySerializerBase<EnumSet<? extends Enum<?>>>
{
    public EnumSetSerializer(final JavaType elemType, final BeanProperty property) {
        super(EnumSet.class, elemType, true, null, property, null);
    }
    
    @Override
    public ContainerSerializerBase<?> _withValueTypeSerializer(final TypeSerializer vts) {
        return this;
    }
    
    public void serializeContents(final EnumSet<? extends Enum<?>> value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        JsonSerializer<Object> enumSer = this._elementSerializer;
        for (final Enum<?> en : value) {
            if (enumSer == null) {
                enumSer = provider.findValueSerializer(en.getDeclaringClass(), this._property);
            }
            enumSer.serialize(en, jgen, provider);
        }
    }
}
