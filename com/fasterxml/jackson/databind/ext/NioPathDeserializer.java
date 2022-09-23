// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;
import java.nio.file.Paths;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.JsonParser;
import java.nio.file.Path;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class NioPathDeserializer extends StdScalarDeserializer<Path>
{
    private static final long serialVersionUID = 1L;
    
    public NioPathDeserializer() {
        super(Path.class);
    }
    
    @Override
    public Path deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            return (Path)ctxt.handleUnexpectedToken(Path.class, p);
        }
        final String value = p.getText();
        if (value.indexOf(58) < 0) {
            return Paths.get(value, new String[0]);
        }
        try {
            final URI uri = new URI(value);
            return Paths.get(uri);
        }
        catch (URISyntaxException e) {
            return (Path)ctxt.handleInstantiationProblem(this.handledType(), value, e);
        }
    }
}
