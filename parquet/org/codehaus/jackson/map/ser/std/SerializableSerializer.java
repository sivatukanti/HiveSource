// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import parquet.org.codehaus.jackson.JsonProcessingException;
import parquet.org.codehaus.jackson.map.JsonMappingException;
import parquet.org.codehaus.jackson.node.ObjectNode;
import parquet.org.codehaus.jackson.map.ObjectMapper;
import java.lang.annotation.Annotation;
import parquet.org.codehaus.jackson.schema.JsonSerializableSchema;
import parquet.org.codehaus.jackson.map.type.TypeFactory;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.map.JsonSerializableWithType;
import parquet.org.codehaus.jackson.map.TypeSerializer;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;
import parquet.org.codehaus.jackson.map.JsonSerializable;
import parquet.org.codehaus.jackson.map.ser.SerializerBase;

@JacksonStdImpl
public class SerializableSerializer extends SerializerBase<JsonSerializable>
{
    public static final SerializableSerializer instance;
    
    protected SerializableSerializer() {
        super(JsonSerializable.class);
    }
    
    @Override
    public void serialize(final JsonSerializable value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        value.serialize(jgen, provider);
    }
    
    @Override
    public final void serializeWithType(final JsonSerializable value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        if (value instanceof JsonSerializableWithType) {
            ((JsonSerializableWithType)value).serializeWithType(jgen, provider, typeSer);
        }
        else {
            this.serialize(value, jgen, provider);
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
        final ObjectNode objectNode = this.createObjectNode();
        String schemaType = "any";
        String objectProperties = null;
        String itemDefinition = null;
        if (typeHint != null) {
            final Class<?> rawClass = TypeFactory.type(typeHint).getRawClass();
            if (rawClass.isAnnotationPresent(JsonSerializableSchema.class)) {
                final JsonSerializableSchema schemaInfo = rawClass.getAnnotation(JsonSerializableSchema.class);
                schemaType = schemaInfo.schemaType();
                if (!"##irrelevant".equals(schemaInfo.schemaObjectPropertiesDefinition())) {
                    objectProperties = schemaInfo.schemaObjectPropertiesDefinition();
                }
                if (!"##irrelevant".equals(schemaInfo.schemaItemDefinition())) {
                    itemDefinition = schemaInfo.schemaItemDefinition();
                }
            }
        }
        objectNode.put("type", schemaType);
        if (objectProperties != null) {
            try {
                objectNode.put("properties", new ObjectMapper().readValue(objectProperties, JsonNode.class));
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        if (itemDefinition != null) {
            try {
                objectNode.put("items", new ObjectMapper().readValue(itemDefinition, JsonNode.class));
            }
            catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return objectNode;
    }
    
    static {
        instance = new SerializableSerializer();
    }
}
