// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.wadl;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.Marshaller;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import com.sun.research.ws.wadl.Application;
import java.util.logging.Level;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.jersey.core.header.MediaTypes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.ws.rs.core.Context;
import com.sun.jersey.server.wadl.ApplicationDescription;
import javax.ws.rs.core.Variant;
import java.net.URI;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import java.util.logging.Logger;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
public final class WadlResource
{
    public static final String HTTPDATEFORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final Logger LOGGER;
    private WadlApplicationContext wadlContext;
    private URI lastBaseUri;
    private byte[] cachedWadl;
    private String lastModified;
    private Variant lastVariant;
    private ApplicationDescription applicationDescription;
    
    public WadlResource(@Context final WadlApplicationContext wadlContext) {
        this.wadlContext = wadlContext;
        this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
    }
    
    @Produces({ "application/vnd.sun.wadl+xml", "application/vnd.sun.wadl+json", "application/xml" })
    @GET
    public synchronized Response getWadl(@Context final Request request, @Context final UriInfo uriInfo, @Context final Providers providers) {
        if (!this.wadlContext.isWadlGenerationEnabled()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final List<Variant> vl = Variant.mediaTypes(MediaTypes.WADL, MediaTypes.WADL_JSON, MediaType.APPLICATION_XML_TYPE).add().build();
        final Variant v = request.selectVariant(vl);
        if (v == null) {
            return Response.notAcceptable(vl).build();
        }
        if (this.applicationDescription == null || (this.lastBaseUri != null && !this.lastBaseUri.equals(uriInfo.getBaseUri()) && !this.lastVariant.equals(v))) {
            this.lastBaseUri = uriInfo.getBaseUri();
            this.lastModified = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date());
            this.lastVariant = v;
            this.applicationDescription = this.wadlContext.getApplication(uriInfo);
            final Application application = this.applicationDescription.getApplication();
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (v.getMediaType().equals(MediaTypes.WADL)) {
                try {
                    final Marshaller marshaller = this.wadlContext.getJAXBContext().createMarshaller();
                    marshaller.setProperty("jaxb.formatted.output", true);
                    marshaller.marshal(application, os);
                    this.cachedWadl = os.toByteArray();
                    os.close();
                    return Response.ok(new ByteArrayInputStream(this.cachedWadl)).header("Last-modified", this.lastModified).build();
                }
                catch (Exception e) {
                    WadlResource.LOGGER.log(Level.WARNING, "Could not marshal wadl Application.", e);
                    return Response.serverError().build();
                }
            }
            final MessageBodyWriter<Application> messageBodyWriter = providers.getMessageBodyWriter(Application.class, null, new Annotation[0], v.getMediaType());
            if (messageBodyWriter == null) {
                return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
            }
            try {
                messageBodyWriter.writeTo(application, Application.class, null, new Annotation[0], v.getMediaType(), null, os);
                this.cachedWadl = os.toByteArray();
                os.close();
            }
            catch (Exception e2) {
                WadlResource.LOGGER.log(Level.WARNING, "Could not serialize wadl Application.", e2);
                return Response.serverError().build();
            }
        }
        return Response.ok(new ByteArrayInputStream(this.cachedWadl)).header("Last-modified", this.lastModified).build();
    }
    
    @Produces({ "*/*" })
    @GET
    @Path("{path}")
    public synchronized Response geExternalGramar(@Context final UriInfo uriInfo, @PathParam("path") final String path) {
        if (!this.wadlContext.isWadlGenerationEnabled()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        final ApplicationDescription applicationDescription = this.wadlContext.getApplication(uriInfo);
        final ApplicationDescription.ExternalGrammar externalMetadata = applicationDescription.getExternalGrammar(path);
        if (externalMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().type(externalMetadata.getType()).entity(externalMetadata.getContent()).build();
    }
    
    static {
        LOGGER = Logger.getLogger(WadlResource.class.getName());
    }
}
