// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.map.TypeDeserializer;
import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.JsonToken;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.util.EnumResolver;
import parquet.org.codehaus.jackson.map.JsonDeserializer;
import java.util.EnumSet;

public class EnumSetDeserializer extends StdDeserializer<EnumSet<?>>
{
    protected final Class<Enum> _enumClass;
    protected final JsonDeserializer<Enum<?>> _enumDeserializer;
    
    public EnumSetDeserializer(final EnumResolver enumRes) {
        this(enumRes.getEnumClass(), new EnumDeserializer(enumRes));
    }
    
    public EnumSetDeserializer(final Class<?> enumClass, final JsonDeserializer<?> deser) {
        super(EnumSet.class);
        this._enumClass = (Class<Enum>)enumClass;
        this._enumDeserializer = (JsonDeserializer<Enum<?>>)deser;
    }
    
    @Override
    public EnumSet<?> deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (!jp.isExpectedStartArrayToken()) {
            throw ctxt.mappingException(EnumSet.class);
        }
        final EnumSet result = this.constructSet();
        JsonToken t;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            if (t == JsonToken.VALUE_NULL) {
                throw ctxt.mappingException(this._enumClass);
            }
            final Enum<?> value = this._enumDeserializer.deserialize(jp, ctxt);
            result.add(value);
        }
        return (EnumSet<?>)result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }
    
    private EnumSet constructSet() {
        return EnumSet.noneOf(this._enumClass);
    }
}
