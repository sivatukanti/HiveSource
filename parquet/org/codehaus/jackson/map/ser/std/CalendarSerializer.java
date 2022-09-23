// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser.std;

import parquet.org.codehaus.jackson.map.SerializationConfig;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;
import java.util.Calendar;

@JacksonStdImpl
public class CalendarSerializer extends ScalarSerializerBase<Calendar>
{
    public static CalendarSerializer instance;
    
    public CalendarSerializer() {
        super(Calendar.class);
    }
    
    @Override
    public void serialize(final Calendar value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        provider.defaultSerializeDateValue(value.getTimeInMillis(), jgen);
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode(provider.isEnabled(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS) ? "number" : "string", true);
    }
    
    static {
        CalendarSerializer.instance = new CalendarSerializer();
    }
}
