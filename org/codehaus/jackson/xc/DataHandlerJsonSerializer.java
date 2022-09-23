// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.JsonGenerator;
import javax.activation.DataHandler;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class DataHandlerJsonSerializer extends SerializerBase<DataHandler>
{
    public DataHandlerJsonSerializer() {
        super(DataHandler.class);
    }
    
    @Override
    public void serialize(final DataHandler value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonProcessingException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        final InputStream in = value.getInputStream();
        for (int len = in.read(buffer); len > 0; len = in.read(buffer)) {
            out.write(buffer, 0, len);
        }
        jgen.writeBinary(out.toByteArray());
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        final ObjectNode o = this.createSchemaNode("array", true);
        final ObjectNode itemSchema = this.createSchemaNode("string");
        o.put("items", itemSchema);
        return o;
    }
}
