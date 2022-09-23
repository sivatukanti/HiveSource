// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.security.Principal;
import java.util.Date;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Variant;
import javax.ws.rs.core.Cookie;
import java.util.Locale;
import com.sun.jersey.api.representation.Form;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.core.header.QualitySourceMediaType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.spi.MessageBodyWorkers;
import javax.ws.rs.core.SecurityContext;
import com.sun.jersey.core.header.InBoundHeaders;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public class AdaptingContainerRequest extends ContainerRequest
{
    protected final ContainerRequest acr;
    
    protected AdaptingContainerRequest(final ContainerRequest acr) {
        super(acr);
        this.acr = acr;
    }
    
    @Override
    public Map<String, Object> getProperties() {
        return this.acr.getProperties();
    }
    
    @Override
    public void setMethod(final String method) {
        this.acr.setMethod(method);
    }
    
    @Override
    public void setUris(final URI baseUri, final URI requestUri) {
        this.acr.setUris(baseUri, requestUri);
    }
    
    @Override
    public InputStream getEntityInputStream() {
        return this.acr.getEntityInputStream();
    }
    
    @Override
    public void setEntityInputStream(final InputStream entity) {
        this.acr.setEntityInputStream(entity);
    }
    
    @Override
    public void setHeaders(final InBoundHeaders headers) {
        this.acr.setHeaders(headers);
    }
    
    @Override
    public void setSecurityContext(final SecurityContext securityContext) {
        this.acr.setSecurityContext(securityContext);
    }
    
    @Override
    public SecurityContext getSecurityContext() {
        return this.acr.getSecurityContext();
    }
    
    @Override
    public MessageBodyWorkers getMessageBodyWorkers() {
        return this.acr.getMessageBodyWorkers();
    }
    
    @Override
    public boolean isTracingEnabled() {
        return this.acr.isTracingEnabled();
    }
    
    @Override
    public void trace(final String message) {
        this.acr.trace(message);
    }
    
    @Override
    public URI getBaseUri() {
        return this.acr.getBaseUri();
    }
    
    @Override
    public UriBuilder getBaseUriBuilder() {
        return this.acr.getBaseUriBuilder();
    }
    
    @Override
    public URI getRequestUri() {
        return this.acr.getRequestUri();
    }
    
    @Override
    public UriBuilder getRequestUriBuilder() {
        return this.acr.getRequestUriBuilder();
    }
    
    @Override
    public URI getAbsolutePath() {
        return this.acr.getAbsolutePath();
    }
    
    @Override
    public UriBuilder getAbsolutePathBuilder() {
        return this.acr.getAbsolutePathBuilder();
    }
    
    @Override
    public String getPath() {
        return this.acr.getPath();
    }
    
    @Override
    public String getPath(final boolean decode) {
        return this.acr.getPath(decode);
    }
    
    @Override
    public List<PathSegment> getPathSegments() {
        return this.acr.getPathSegments();
    }
    
    @Override
    public List<PathSegment> getPathSegments(final boolean decode) {
        return this.acr.getPathSegments(decode);
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters() {
        return this.acr.getQueryParameters();
    }
    
    @Override
    public MultivaluedMap<String, String> getQueryParameters(final boolean decode) {
        return this.acr.getQueryParameters(decode);
    }
    
    @Override
    public String getHeaderValue(final String name) {
        return this.acr.getHeaderValue(name);
    }
    
    @Override
    public MediaType getAcceptableMediaType(final List<MediaType> mediaTypes) {
        return this.acr.getAcceptableMediaType(mediaTypes);
    }
    
    @Override
    public List<MediaType> getAcceptableMediaTypes(final List<QualitySourceMediaType> priorityMediaTypes) {
        return this.acr.getAcceptableMediaTypes(priorityMediaTypes);
    }
    
    @Override
    public MultivaluedMap<String, String> getCookieNameValueMap() {
        return this.acr.getCookieNameValueMap();
    }
    
    @Override
    public <T> T getEntity(final Class<T> type) throws WebApplicationException {
        return this.acr.getEntity(type);
    }
    
    @Override
    public <T> T getEntity(final Class<T> type, final Type genericType, final Annotation[] as) throws WebApplicationException {
        return this.acr.getEntity(type, genericType, as);
    }
    
    @Override
    public Form getFormParameters() {
        return this.acr.getFormParameters();
    }
    
    @Override
    public List<String> getRequestHeader(final String name) {
        return this.acr.getRequestHeader(name);
    }
    
    @Override
    public MultivaluedMap<String, String> getRequestHeaders() {
        return this.acr.getRequestHeaders();
    }
    
    @Override
    public List<MediaType> getAcceptableMediaTypes() {
        return this.acr.getAcceptableMediaTypes();
    }
    
    @Override
    public List<Locale> getAcceptableLanguages() {
        return this.acr.getAcceptableLanguages();
    }
    
    @Override
    public MediaType getMediaType() {
        return this.acr.getMediaType();
    }
    
    @Override
    public Locale getLanguage() {
        return this.acr.getLanguage();
    }
    
    @Override
    public Map<String, Cookie> getCookies() {
        return this.acr.getCookies();
    }
    
    @Override
    public String getMethod() {
        return this.acr.getMethod();
    }
    
    @Override
    public Variant selectVariant(final List<Variant> variants) throws IllegalArgumentException {
        return this.acr.selectVariant(variants);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions(final EntityTag eTag) {
        return this.acr.evaluatePreconditions(eTag);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions(final Date lastModified) {
        return this.acr.evaluatePreconditions(lastModified);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions(final Date lastModified, final EntityTag eTag) {
        return this.acr.evaluatePreconditions(lastModified, eTag);
    }
    
    @Override
    public Response.ResponseBuilder evaluatePreconditions() {
        return this.acr.evaluatePreconditions();
    }
    
    @Override
    public Principal getUserPrincipal() {
        return this.acr.getUserPrincipal();
    }
    
    @Override
    public boolean isUserInRole(final String role) {
        return this.acr.isUserInRole(role);
    }
    
    @Override
    public boolean isSecure() {
        return this.acr.isSecure();
    }
    
    @Override
    public String getAuthenticationScheme() {
        return this.acr.getAuthenticationScheme();
    }
}
