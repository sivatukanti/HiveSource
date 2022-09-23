// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.core.Context;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.jersey.spi.inject.Injectable;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.w3c.dom.Document;
import com.sun.jersey.core.provider.AbstractMessageReaderWriterProvider;

@Produces({ "application/xml", "text/xml", "*/*" })
@Consumes({ "application/xml", "text/xml", "*/*" })
public final class DocumentProvider extends AbstractMessageReaderWriterProvider<Document>
{
    private final Injectable<DocumentBuilderFactory> dbf;
    private final Injectable<TransformerFactory> tf;
    
    public DocumentProvider(@Context final Injectable<DocumentBuilderFactory> dbf, @Context final Injectable<TransformerFactory> tf) {
        this.dbf = dbf;
        this.tf = tf;
    }
    
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Document.class == type;
    }
    
    @Override
    public Document readFrom(final Class<Document> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
        try {
            return this.dbf.getValue().newDocumentBuilder().parse(entityStream);
        }
        catch (SAXException ex) {
            throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
        }
        catch (ParserConfigurationException ex2) {
            throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return Document.class.isAssignableFrom(type);
    }
    
    @Override
    public void writeTo(final Document t, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
        try {
            final StreamResult sr = new StreamResult(entityStream);
            this.tf.getValue().newTransformer().transform(new DOMSource(t), sr);
        }
        catch (TransformerException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
