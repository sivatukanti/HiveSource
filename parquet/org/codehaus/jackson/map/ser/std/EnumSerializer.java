// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import java.util.Iterator;
import parquet.org.codehaus.jackson.node.ArrayNode;
import parquet.org.codehaus.jackson.type.JavaType;
import parquet.org.codehaus.jackson.node.ObjectNode;
import parquet.org.codehaus.jackson.io.SerializedString;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.SerializableString;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.AnnotationIntrospector;
import parquet.org.codehaus.jackson.map.introspect.BasicBeanDescription;
import parquet.org.codehaus.jackson.map.SerializationConfig;
import parquet.org.codehaus.jackson.map.util.EnumValues;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;

@JacksonStdImpl
public class EnumSerializer extends ScalarSerializerBase<Enum<?>>
{
    protected final EnumValues _values;
    
    public EnumSerializer(final EnumValues v) {
        super(Enum.class, false);
        this._values = v;
    }
    
    public static EnumSerializer construct(final Class<Enum<?>> enumClass, final SerializationConfig config, final BasicBeanDescription beanDesc) {
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final EnumValues v = config.isEnabled(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING) ? EnumValues.constructFromToString(enumClass, intr) : EnumValues.constructFromName(enumClass, intr);
        return new EnumSerializer(v);
    }
    
    @Override
    public final void serialize(final Enum<?> en, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (provider.isEnabled(SerializationConfig.Feature.WRITE_ENUMS_USING_INDEX)) {
            jgen.writeNumber(en.ordinal());
            return;
        }
        jgen.writeString(this._values.serializedValueFor(en));
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        if (provider.isEnabled(SerializationConfig.Feature.WRITE_ENUMS_USING_INDEX)) {
            return this.createSchemaNode("integer", true);
        }
        final ObjectNode objectNode = this.createSchemaNode("string", true);
        if (typeHint != null) {
            final JavaType type = provider.constructType(typeHint);
            if (type.isEnumType()) {
                final ArrayNode enumNode = objectNode.putArray("enum");
                for (final SerializedString value : this._values.values()) {
                    enumNode.add(value.getValue());
                }
            }
        }
        return objectNode;
    }
    
    public EnumValues getEnumValues() {
        return this._values;
    }
}
