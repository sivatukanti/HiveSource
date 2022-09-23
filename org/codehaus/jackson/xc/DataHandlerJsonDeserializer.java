// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.xc;

import org.codehaus.jackson.JsonProcessingException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.activation.DataSource;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.JsonParser;
import javax.activation.DataHandler;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;

public class DataHandlerJsonDeserializer extends StdScalarDeserializer<DataHandler>
{
    public DataHandlerJsonDeserializer() {
        super(DataHandler.class);
    }
    
    @Override
    public DataHandler deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final byte[] value = jp.getBinaryValue();
        return new DataHandler(new DataSource() {
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(value);
            }
            
            public OutputStream getOutputStream() throws IOException {
                throw new IOException();
            }
            
            public String getContentType() {
                return "application/octet-stream";
            }
            
            public String getName() {
                return "json-binary-data";
            }
        });
    }
}
