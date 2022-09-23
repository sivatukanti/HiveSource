// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.provider.entity;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.IOException;
import org.codehaus.jettison.json.JSONException;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.json.impl.ImplMessages;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.codehaus.jettison.json.JSONObject;

public class JSONObjectProvider extends JSONLowLevelProvider<JSONObject>
{
    JSONObjectProvider() {
        super(JSONObject.class);
    }
    
    @Override
    public JSONObject readFrom(final Class<JSONObject> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        try {
            return new JSONObject(AbstractMessageReaderWriterProvider.readFromAsString(entityStream, mediaType));
        }
        catch (JSONException je) {
            throw new WebApplicationException(new Exception(ImplMessages.ERROR_PARSING_JSON_OBJECT(), je), 400);
        }
    }
    
    @Override
    public void writeTo(final JSONObject t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final OutputStreamWriter writer = new OutputStreamWriter(entityStream, AbstractMessageReaderWriterProvider.getCharset(mediaType));
            t.write(writer);
            writer.flush();
        }
        catch (JSONException je) {
            throw new WebApplicationException(new Exception(ImplMessages.ERROR_WRITING_JSON_OBJECT(), je), 500);
        }
    }
    
    @Produces({ "application/json" })
    @Consumes({ "application/json" })
    public static final class App extends JSONObjectProvider
    {
    }
    
    @Produces({ "*/*" })
    @Consumes({ "*/*" })
    public static final class General extends JSONObjectProvider
    {
        @Override
        protected boolean isSupported(final MediaType m) {
            return m.getSubtype().endsWith("+json");
        }
    }
}
