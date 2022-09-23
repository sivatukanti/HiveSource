// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

public interface MessageBodyWorkers
{
    Map<MediaType, List<MessageBodyReader>> getReaders(final MediaType p0);
    
    Map<MediaType, List<MessageBodyWriter>> getWriters(final MediaType p0);
    
    String readersToString(final Map<MediaType, List<MessageBodyReader>> p0);
    
    String writersToString(final Map<MediaType, List<MessageBodyWriter>> p0);
    
     <T> MessageBodyReader<T> getMessageBodyReader(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
     <T> MessageBodyWriter<T> getMessageBodyWriter(final Class<T> p0, final Type p1, final Annotation[] p2, final MediaType p3);
    
     <T> List<MediaType> getMessageBodyWriterMediaTypes(final Class<T> p0, final Type p1, final Annotation[] p2);
    
     <T> MediaType getMessageBodyWriterMediaType(final Class<T> p0, final Type p1, final Annotation[] p2, final List<MediaType> p3);
}
