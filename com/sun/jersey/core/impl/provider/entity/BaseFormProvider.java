// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import java.util.Iterator;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.OutputStream;
import java.io.IOException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.InputStream;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;
import javax.ws.rs.core.MultivaluedMap;

public abstract class BaseFormProvider<T extends MultivaluedMap<String, String>> extends AbstractMessageReaderWriterProvider<T>
{
    public T readFrom(final T map, final MediaType mediaType, final InputStream entityStream) throws IOException {
        final String encoded = AbstractMessageReaderWriterProvider.readFromAsString(entityStream, mediaType);
        final String charsetName = ReaderWriter.getCharset(mediaType).name();
        final StringTokenizer tokenizer = new StringTokenizer(encoded, "&");
        try {
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                final int idx = token.indexOf(61);
                if (idx < 0) {
                    map.add(URLDecoder.decode(token, charsetName), null);
                }
                else {
                    if (idx <= 0) {
                        continue;
                    }
                    map.add(URLDecoder.decode(token.substring(0, idx), charsetName), URLDecoder.decode(token.substring(idx + 1), charsetName));
                }
            }
            return map;
        }
        catch (IllegalArgumentException ex) {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
    }
    
    public void writeTo(final T t, final MediaType mediaType, final OutputStream entityStream) throws IOException {
        final String charsetName = ReaderWriter.getCharset(mediaType).name();
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, List<String>> e : t.entrySet()) {
            for (final String value : e.getValue()) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(e.getKey(), charsetName));
                if (value != null) {
                    sb.append('=');
                    sb.append(URLEncoder.encode(value, charsetName));
                }
            }
        }
        AbstractMessageReaderWriterProvider.writeToAsString(sb.toString(), entityStream, mediaType);
    }
}
