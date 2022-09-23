// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import com.sun.jersey.core.util.KeyComparatorLinkedHashMap;
import java.security.Principal;
import java.text.ParseException;
import java.util.Date;
import javax.ws.rs.core.EntityTag;
import com.sun.jersey.core.header.MatchingEntityTag;
import java.util.Set;
import javax.ws.rs.core.Response;
import com.sun.jersey.server.impl.VariantSelector;
import javax.ws.rs.core.Variant;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Collection;
import java.util.ArrayList;
import com.sun.jersey.server.impl.model.HttpHelper;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.util.Iterator;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.ws.rs.ext.MessageBodyReader;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.core.reflection.ReflectionHelper;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.api.Responses;
import com.sun.jersey.api.MessageException;
import java.lang.reflect.Type;
import com.sun.jersey.api.uri.UriComponent;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.api.core.TraceInformation;
import com.sun.jersey.spi.MessageBodyWorkers;
import java.util.HashMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Cookie;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.InBoundHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import java.util.List;
import java.net.URI;
import java.io.InputStream;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;
import com.sun.jersey.api.core.HttpRequestContext;

public class ContainerRequest implements HttpRequestContext
{
    private static final Logger LOGGER;
    private static final Annotation[] EMPTY_ANNOTATIONS;
    public static final String VARY_HEADER = "Vary";
    private final WebApplication wa;
    private final boolean isTraceEnabled;
    private Map<String, Object> properties;
    private String method;
    private InputStream entity;
    private URI baseUri;
    private URI requestUri;
    private URI absolutePathUri;
    private String encodedPath;
    private String decodedPath;
    private List<PathSegment> decodedPathSegments;
    private List<PathSegment> encodedPathSegments;
    private MultivaluedMap<String, String> decodedQueryParameters;
    private MultivaluedMap<String, String> encodedQueryParameters;
    private InBoundHeaders headers;
    private int headersModCount;
    private MediaType contentType;
    private List<MediaType> accept;
    private List<Locale> acceptLanguages;
    private Map<String, Cookie> cookies;
    private MultivaluedMap<String, String> cookieNames;
    private SecurityContext securityContext;
    
    public ContainerRequest(final WebApplication wa, final String method, final URI baseUri, final URI requestUri, final InBoundHeaders headers, final InputStream entity) {
        this.wa = wa;
        this.isTraceEnabled = wa.isTracingEnabled();
        this.method = method;
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.headers = headers;
        this.headersModCount = headers.getModCount();
        this.entity = entity;
    }
    
    ContainerRequest(final ContainerRequest r) {
        this.wa = r.wa;
        this.isTraceEnabled = r.isTraceEnabled;
    }
    
    public Map<String, Object> getProperties() {
        if (this.properties != null) {
            return this.properties;
        }
        return this.properties = new HashMap<String, Object>();
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public void setUris(final URI baseUri, final URI requestUri) {
        this.baseUri = baseUri;
        this.requestUri = requestUri;
        this.absolutePathUri = null;
        final String s = null;
        this.decodedPath = s;
        this.encodedPath = s;
        final List<PathSegment> list = null;
        this.encodedPathSegments = list;
        this.decodedPathSegments = list;
        final MultivaluedMap<String, String> multivaluedMap = null;
        this.encodedQueryParameters = multivaluedMap;
        this.decodedQueryParameters = multivaluedMap;
    }
    
    public InputStream getEntityInputStream() {
        return this.entity;
    }
    
    public void setEntityInputStream(final InputStream entity) {
        this.entity = entity;
    }
    
    public void setHeaders(final InBoundHeaders headers) {
        this.headers = headers;
        this.headersModCount = headers.getModCount();
        this.contentType = null;
        this.accept = null;
        this.cookies = null;
        this.cookieNames = null;
    }
    
    public void setSecurityContext(final SecurityContext securityContext) {
        this.securityContext = securityContext;
    }
    
    public SecurityContext getSecurityContext() {
        return this.securityContext;
    }
    
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.wa.getMessageBodyWorkers();
    }
    
    @Override
    public boolean isTracingEnabled() {
        return this.isTraceEnabled;
    }
    
    @Override
    public void trace(final String message) {
        if (!this.isTracingEnabled()) {
            return;
        }
        if (this.wa.getFeaturesAndProperties().getFeature("com.sun.jersey.config.feature.TracePerRequest") && !this.getRequestHeaders().containsKey("X-Jersey-Trace-Accept")) {
            return;
        }
        final TraceInformation ti = this.getProperties().get(TraceInformation.class.getName());
        ti.trace(message);
    }
    
    @Override
    public URI getBaseUri() {
        return this.baseUri;
    }
    
    @Override
    public UriBuilder getBaseUriBuilder() {
        return UriBuilder.fromUri(this.getBaseUri());
    }
    
    @Override
    public URI getRequestUri() {
        return this.requestUri;
    }
    
    @Override
    public UriBuilder getRequestUriBuilder() {
        return UriBuilder.fromUri(this.getRequestUri());
    }
    
    @Override
    public URI getAbsolutePath() {
        if (this.absolutePathUri != null) {
            return this.absolutePathUri;
        }
        return this.absolutePathUri = UriBuilder.fromUri(this.requestUri).replaceQuery("").fragment("").build(new Object[0]);
    }
    
    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return UriBuilder.fromUri(this.getAbsolutePath());
    }
    
    @Override
    public String getPath() {
        return this.getPath(true);
    }
    
    @Override
    public String getPath(final boolean decode) {
        if (!decode) {
            return this.getEncodedPath();
        }
        if (this.decodedPath != null) {
            return this.decodedPath;
        }
        return this.decodedPath = UriComponent.decode(this.getEncodedPath(), UriComponent.Type.PATH);
    }
    
    private String getEncodedPath() {
        if (this.encodedPath != null) {
            return this.encodedPath;
        }
        final int length = this.getBaseUri().getRawPath().length();
        if (length < this.getRequestUri().getRawPath().length()) {
            return this.encodedPath = this.getRequestUri().getRawPath().substring(length);
        }
        return "";
    }
    
    @Override
    public List<PathSegment> getPathSegments() {
        return this.getPathSegments(true);
    }
    
    @Override
    public List<PathSegment> getPathSegments(final boolean decode) {
        if (decode) {
            if (this.decodedPathSegments != null) {
                return this.decodedPathSegments;
            }
            return this.decodedPathSegments = UriComponent.decodePath(this.getPath(false), true);
        }
        else {
            if (this.encodedPathSegments != null) {
                return this.encodedPathSegments;
            }
            return this.encodedPathSegments = UriComponent.decodePath(this.getPath(false), false);
        }
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return this.getQueryParameters(true);
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters(final boolean decode) {
        if (decode) {
            if (this.decodedQueryParameters != null) {
                return this.decodedQueryParameters;
            }
            return this.decodedQueryParameters = UriComponent.decodeQuery(this.getRequestUri(), true);
        }
        else {
            if (this.encodedQueryParameters != null) {
                return this.encodedQueryParameters;
            }
            return this.encodedQueryParameters = UriComponent.decodeQuery(this.getRequestUri(), false);
        }
    }
    
    @Override
    public String getHeaderValue(final String name) {
        final List<String> v = this.getRequestHeaders().get(name);
        if (v == null) {
            return null;
        }
        if (v.isEmpty()) {
            return "";
        }
        if (v.size() == 1) {
            return v.get(0);
        }
        final StringBuilder sb = new StringBuilder(v.get(0));
        for (int i = 1; i < v.size(); ++i) {
            final String s = v.get(i);
            if (s.length() > 0) {
                sb.append(',').append(s);
            }
        }
        return sb.toString();
    }
    
    @Override
    public <T> T getEntity(final Class<T> type, final Type genericType, final Annotation[] as) {
        MediaType mediaType = this.getMediaType();
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
        final MessageBodyReader<T> bw = this.getMessageBodyWorkers().getMessageBodyReader(type, genericType, as, mediaType);
        if (bw == null) {
            final String message = "A message body reader for Java class " + type.getName() + ", and Java type " + genericType + ", and MIME media type " + mediaType + " was not found.\n";
            final Map<MediaType, List<MessageBodyReader>> m = this.getMessageBodyWorkers().getReaders(mediaType);
            ContainerRequest.LOGGER.severe(message + "The registered message body readers compatible with the MIME media type are:\n" + this.getMessageBodyWorkers().readersToString(m));
            throw new WebApplicationException(new MessageException(message), Responses.unsupportedMediaType().build());
        }
        if (this.isTracingEnabled()) {
            this.trace(String.format("matched message body reader: %s, \"%s\" -> %s", genericType, mediaType, ReflectionHelper.objectToString(bw)));
        }
        try {
            return bw.readFrom(type, genericType, as, mediaType, this.headers, this.entity);
        }
        catch (WebApplicationException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new MappableContainerException(e);
        }
    }
    
    public <T> void setEntity(final Class<T> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders, final T entity) {
        final MessageBodyWriter<T> writer = this.getMessageBodyWorkers().getMessageBodyWriter(type, genericType, annotations, mediaType);
        if (writer == null) {
            final String message = "A message body writer for Java class " + type.getName() + ", and Java type " + genericType + ", and MIME media type " + mediaType + " was not found.\n";
            final Map<MediaType, List<MessageBodyReader>> m = this.getMessageBodyWorkers().getReaders(mediaType);
            ContainerRequest.LOGGER.severe(message + "The registered message body readers compatible with the MIME media type are:\n" + this.getMessageBodyWorkers().readersToString(m));
            throw new WebApplicationException(new MessageException(message), Responses.unsupportedMediaType().build());
        }
        if (this.isTracingEnabled()) {
            this.trace(String.format("matched message body writer: %s, \"%s\" -> %s", genericType, mediaType, ReflectionHelper.objectToString(writer)));
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            writer.writeTo(entity, type, genericType, annotations, mediaType, httpHeaders, byteArrayOutputStream);
        }
        catch (IOException e) {
            throw new MappableContainerException(e);
        }
        this.entity = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
    
    @Override
    public <T> T getEntity(final Class<T> type) {
        return this.getEntity(type, type, ContainerRequest.EMPTY_ANNOTATIONS);
    }
    
    @Override
    public MediaType getAcceptableMediaType(final List<MediaType> mediaTypes) {
        if (mediaTypes.isEmpty()) {
            return this.getAcceptableMediaTypes().get(0);
        }
        for (final MediaType a : this.getAcceptableMediaTypes()) {
            if (a.getType().equals("*")) {
                return mediaTypes.get(0);
            }
            for (final MediaType m : mediaTypes) {
                if (m.isCompatible(a) && !m.isWildcardType() && !m.isWildcardSubtype()) {
                    return m;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<MediaType> getAcceptableMediaTypes(final List<QualitySourceMediaType> priorityMediaTypes) {
        return new ArrayList<MediaType>(HttpHelper.getAccept(this, priorityMediaTypes));
    }
    
    @Override
    public MultivaluedMap<String, String> getCookieNameValueMap() {
        if (this.cookieNames == null || this.headersModCount != this.headers.getModCount()) {
            this.cookieNames = new MultivaluedMapImpl();
            for (final Map.Entry<String, Cookie> e : this.getCookies().entrySet()) {
                this.cookieNames.putSingle(e.getKey(), e.getValue().getValue());
            }
        }
        return this.cookieNames;
    }
    
    @Override
    public Form getFormParameters() {
        if (MediaTypes.typeEquals(MediaType.APPLICATION_FORM_URLENCODED_TYPE, this.getMediaType())) {
            InputStream in = this.getEntityInputStream();
            if (in.getClass() != ByteArrayInputStream.class) {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    ReaderWriter.writeTo(in, byteArrayOutputStream);
                }
                catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
                in = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                this.setEntityInputStream(in);
            }
            final ByteArrayInputStream byteArrayInputStream = (ByteArrayInputStream)in;
            final Form f = this.getEntity(Form.class);
            byteArrayInputStream.reset();
            return f;
        }
        return new Form();
    }
    
    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        return this.headers;
    }
    
    @Override
    public List<String> getRequestHeader(final String name) {
        return ((KeyComparatorLinkedHashMap<K, List<String>>)this.headers).get(name);
    }
    
    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        if (this.accept == null || this.headersModCount != this.headers.getModCount()) {
            this.accept = new ArrayList<MediaType>(HttpHelper.getAccept(this));
        }
        return this.accept;
    }
    
    @Override
    public List<Locale> getAcceptableLanguages() {
        if (this.acceptLanguages == null || this.headersModCount != this.headers.getModCount()) {
            final List<AcceptableLanguageTag> alts = HttpHelper.getAcceptLangauge(this);
            this.acceptLanguages = new ArrayList<Locale>(alts.size());
            for (final AcceptableLanguageTag alt : alts) {
                this.acceptLanguages.add(alt.getAsLocale());
            }
        }
        return this.acceptLanguages;
    }
    
    @Override
    public MediaType getMediaType() {
        if (this.contentType == null || this.headersModCount != this.headers.getModCount()) {
            this.contentType = HttpHelper.getContentType(this);
        }
        return this.contentType;
    }
    
    @Override
    public Locale getLanguage() {
        return HttpHelper.getContentLanguageAsLocale(this);
    }
    
    @Override
    public Map<String, Cookie> getCookies() {
        if (this.cookies == null || this.headersModCount != this.headers.getModCount()) {
            this.cookies = new HashMap<String, Cookie>();
            final List<String> cl = this.getRequestHeaders().get("Cookie");
            if (cl != null) {
                for (final String cookie : cl) {
                    if (cookie != null) {
                        this.cookies.putAll(HttpHeaderReader.readCookies(cookie));
                    }
                }
            }
        }
        return this.cookies;
    }
    
    @Override
    public String getMethod() {
        return this.method;
    }
    
    @Override
    public Variant selectVariant(final List<Variant> variants) {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("The list of variants is null or empty");
        }
        return VariantSelector.selectVariant(this, variants);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions() {
        final Set<MatchingEntityTag> matchingTags = HttpHelper.getIfMatch(this);
        if (matchingTags == null) {
            return null;
        }
        return Responses.preconditionFailed();
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions(final EntityTag eTag) {
        if (eTag == null) {
            throw new IllegalArgumentException("Parameter 'eTag' cannot be null.");
        }
        final Response.ResponseBuilder r = this.evaluateIfMatch(eTag);
        if (r != null) {
            return r;
        }
        return this.evaluateIfNoneMatch(eTag);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions(final Date lastModified) {
        if (lastModified == null) {
            throw new IllegalArgumentException("Parameter 'lastModified' cannot be null.");
        }
        final long lastModifiedTime = lastModified.getTime();
        final Response.ResponseBuilder r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
        if (r != null) {
            return r;
        }
        return this.evaluateIfModifiedSince(lastModifiedTime);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions(final Date lastModified, final EntityTag eTag) {
        if (lastModified == null || eTag == null) {
            throw new IllegalArgumentException("Parameters 'lastModified' and 'eTag' cannot be null.");
        }
        Response.ResponseBuilder r = this.evaluateIfMatch(eTag);
        if (r != null) {
            return r;
        }
        final long lastModifiedTime = lastModified.getTime();
        r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
        if (r != null) {
            return r;
        }
        final boolean isGetOrHead = this.getMethod().equals("GET") || this.getMethod().equals("HEAD");
        final Set<MatchingEntityTag> matchingTags = HttpHelper.getIfNoneMatch(this);
        if (matchingTags != null) {
            r = this.evaluateIfNoneMatch(eTag, matchingTags, isGetOrHead);
            if (r == null) {
                return r;
            }
        }
        final String ifModifiedSinceHeader = this.getRequestHeaders().getFirst("If-Modified-Since");
        if (ifModifiedSinceHeader != null && isGetOrHead) {
            r = this.evaluateIfModifiedSince(lastModifiedTime, ifModifiedSinceHeader);
            if (r != null) {
                r.tag(eTag);
            }
        }
        return r;
    }
    
    private Response.ResponseBuilder evaluateIfMatch(final EntityTag eTag) {
        final Set<MatchingEntityTag> matchingTags = HttpHelper.getIfMatch(this);
        if (matchingTags == null) {
            return null;
        }
        if (eTag.isWeak()) {
            return Responses.preconditionFailed();
        }
        if (matchingTags != MatchingEntityTag.ANY_MATCH && !matchingTags.contains(eTag)) {
            return Responses.preconditionFailed();
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfNoneMatch(final EntityTag eTag) {
        final Set<MatchingEntityTag> matchingTags = HttpHelper.getIfNoneMatch(this);
        if (matchingTags == null) {
            return null;
        }
        final String httpMethod = this.getMethod();
        return this.evaluateIfNoneMatch(eTag, matchingTags, httpMethod.equals("GET") || httpMethod.equals("HEAD"));
    }
    
    private Response.ResponseBuilder evaluateIfNoneMatch(final EntityTag eTag, final Set<MatchingEntityTag> matchingTags, final boolean isGetOrHead) {
        if (isGetOrHead) {
            if (matchingTags == MatchingEntityTag.ANY_MATCH) {
                return Response.notModified(eTag);
            }
            if (matchingTags.contains(eTag) || matchingTags.contains(new EntityTag(eTag.getValue(), !eTag.isWeak()))) {
                return Response.notModified(eTag);
            }
        }
        else {
            if (eTag.isWeak()) {
                return null;
            }
            if (matchingTags == MatchingEntityTag.ANY_MATCH || matchingTags.contains(eTag)) {
                return Responses.preconditionFailed();
            }
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfUnmodifiedSince(final long lastModified) {
        final String ifUnmodifiedSinceHeader = this.getRequestHeaders().getFirst("If-Unmodified-Since");
        if (ifUnmodifiedSinceHeader != null) {
            try {
                final long ifUnmodifiedSince = HttpHeaderReader.readDate(ifUnmodifiedSinceHeader).getTime();
                if (roundDown(lastModified) > ifUnmodifiedSince) {
                    return Responses.preconditionFailed();
                }
            }
            catch (ParseException ex) {}
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfModifiedSince(final long lastModified) {
        final String ifModifiedSinceHeader = this.getRequestHeaders().getFirst("If-Modified-Since");
        if (ifModifiedSinceHeader == null) {
            return null;
        }
        final String httpMethod = this.getMethod();
        if (httpMethod.equals("GET") || httpMethod.equals("HEAD")) {
            return this.evaluateIfModifiedSince(lastModified, ifModifiedSinceHeader);
        }
        return null;
    }
    
    private Response.ResponseBuilder evaluateIfModifiedSince(final long lastModified, final String ifModifiedSinceHeader) {
        try {
            final long ifModifiedSince = HttpHeaderReader.readDate(ifModifiedSinceHeader).getTime();
            if (roundDown(lastModified) <= ifModifiedSince) {
                return Responses.notModified();
            }
        }
        catch (ParseException ex) {}
        return null;
    }
    
    private static long roundDown(final long time) {
        return time - time % 1000L;
    }
    
    @Override
    public Principal getUserPrincipal() {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.getUserPrincipal();
    }
    
    @Override
    public boolean isUserInRole(final String role) {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.isUserInRole(role);
    }
    
    @Override
    public boolean isSecure() {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.isSecure();
    }
    
    @Override
    public String getAuthenticationScheme() {
        if (this.securityContext == null) {
            throw new UnsupportedOperationException();
        }
        return this.securityContext.getAuthenticationScheme();
    }
    
    static {
        LOGGER = Logger.getLogger(ContainerRequest.class.getName());
        EMPTY_ANNOTATIONS = new Annotation[0];
    }
}
