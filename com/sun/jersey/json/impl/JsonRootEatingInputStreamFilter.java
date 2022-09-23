// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl;

import org.codehaus.jackson.JsonToken;
import java.io.IOException;
import java.io.OutputStream;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import java.io.InputStream;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;

public class JsonRootEatingInputStreamFilter extends FilteringInputStream
{
    private JsonParser jsonParser;
    private JsonGenerator jsonGenerator;
    private int depth;
    private BufferingInputOutputStream buffers;
    
    public JsonRootEatingInputStreamFilter(final InputStream inputStream) throws IOException {
        final JsonFactory jsonFactory = new JsonFactory();
        this.jsonParser = jsonFactory.createJsonParser(inputStream);
        this.buffers = new BufferingInputOutputStream();
        this.jsonGenerator = jsonFactory.createJsonGenerator(this.buffers, JsonEncoding.UTF8);
        this.depth = 0;
    }
    
    @Override
    protected byte[] nextBytes() throws IOException {
        if (!this.jsonParser.hasCurrentToken()) {
            this.jsonParser.nextToken();
        }
        final JsonToken token = this.jsonParser.getCurrentToken();
        if (this.depth == 0 && token == JsonToken.START_OBJECT) {
            this.jsonParser.nextToken();
            return this.nextBytes();
        }
        if (this.depth == 0 && token == JsonToken.FIELD_NAME) {
            ++this.depth;
            this.jsonParser.nextToken();
            return this.nextBytes();
        }
        if (this.depth == 1 && (token == JsonToken.END_OBJECT || token == JsonToken.END_ARRAY)) {
            this.jsonParser.nextToken();
            return null;
        }
        this.jsonGenerator.copyCurrentEvent(this.jsonParser);
        this.jsonGenerator.flush();
        this.jsonParser.nextToken();
        if (token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT) {
            ++this.depth;
        }
        else if (token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT) {
            --this.depth;
        }
        return this.buffers.nextBytes();
    }
    
    @Override
    public int available() throws IOException {
        return super.available();
    }
}
