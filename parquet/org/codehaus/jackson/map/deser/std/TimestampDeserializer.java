// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import java.sql.Timestamp;

public class TimestampDeserializer extends StdScalarDeserializer<Timestamp>
{
    public TimestampDeserializer() {
        super(Timestamp.class);
    }
    
    @Override
    public Timestamp deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new Timestamp(this._parseDate(jp, ctxt).getTime());
    }
}
