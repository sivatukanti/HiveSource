// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.entity;

import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.ws.rs.ext.MessageBodyWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.ws.rs.core.Response;
import org.xml.sax.InputSource;
import javax.ws.rs.core.Context;
import javax.xml.parsers.SAXParserFactory;
import com.sun.jersey.spi.inject.Injectable;
import javax.xml.transform.sax.SAXSource;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.xml.transform.stream.StreamSource;
import javax.ws.rs.ext.MessageBodyReader;

public final class SourceProvider
{
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    public static final class StreamSourceReader implements MessageBodyReader<StreamSource>
    {
        @Override
        public boolean isReadable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return StreamSource.class == t;
        }
        
        @Override
        public StreamSource readFrom(final Class<StreamSource> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
            return new StreamSource(entityStream);
        }
    }
    
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    public static final class SAXSourceReader implements MessageBodyReader<SAXSource>
    {
        private final Injectable<SAXParserFactory> spf;
        
        public SAXSourceReader(@Context final Injectable<SAXParserFactory> spf) {
            this.spf = spf;
        }
        
        @Override
        public boolean isReadable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return SAXSource.class == t;
        }
        
        @Override
        public SAXSource readFrom(final Class<SAXSource> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
            try {
                return new SAXSource(this.spf.getValue().newSAXParser().getXMLReader(), new InputSource(entityStream));
            }
            catch (SAXParseException ex) {
                throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
            }
            catch (SAXException ex2) {
                throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (ParserConfigurationException ex3) {
                throw new WebApplicationException(ex3, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }
    
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    public static final class DOMSourceReader implements MessageBodyReader<DOMSource>
    {
        private final Injectable<DocumentBuilderFactory> dbf;
        
        public DOMSourceReader(@Context final Injectable<DocumentBuilderFactory> dbf) {
            this.dbf = dbf;
        }
        
        @Override
        public boolean isReadable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return DOMSource.class == t;
        }
        
        @Override
        public DOMSource readFrom(final Class<DOMSource> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws IOException {
            try {
                final Document d = this.dbf.getValue().newDocumentBuilder().parse(entityStream);
                return new DOMSource(d);
            }
            catch (SAXParseException ex) {
                throw new WebApplicationException(ex, Response.Status.BAD_REQUEST);
            }
            catch (SAXException ex2) {
                throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (ParserConfigurationException ex3) {
                throw new WebApplicationException(ex3, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }
    
    @Produces({ "application/xml", "text/xml", "*/*" })
    @Consumes({ "application/xml", "text/xml", "*/*" })
    public static final class SourceWriter implements MessageBodyWriter<Source>
    {
        private final Injectable<SAXParserFactory> spf;
        private final Injectable<TransformerFactory> tf;
        
        public SourceWriter(@Context final Injectable<SAXParserFactory> spf, @Context final Injectable<TransformerFactory> tf) {
            this.spf = spf;
            this.tf = tf;
        }
        
        @Override
        public boolean isWriteable(final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType) {
            return Source.class.isAssignableFrom(t);
        }
        
        @Override
        public long getSize(final Source o, final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
            return -1L;
        }
        
        @Override
        public void writeTo(Source o, final Class<?> t, final Type gt, final Annotation[] as, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream) throws IOException {
            try {
                if (o instanceof StreamSource) {
                    final StreamSource s = (StreamSource)o;
                    final InputSource is = new InputSource(s.getInputStream());
                    o = new SAXSource(this.spf.getValue().newSAXParser().getXMLReader(), is);
                }
                final StreamResult sr = new StreamResult(entityStream);
                this.tf.getValue().newTransformer().transform(o, sr);
            }
            catch (SAXException ex) {
                throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (ParserConfigurationException ex2) {
                throw new WebApplicationException(ex2, Response.Status.INTERNAL_SERVER_ERROR);
            }
            catch (TransformerException ex3) {
                throw new WebApplicationException(ex3, Response.Status.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
