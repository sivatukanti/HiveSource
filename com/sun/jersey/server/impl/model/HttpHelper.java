// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model;

import java.util.Iterator;
import javax.ws.rs.core.Response;
import javax.ws.rs.WebApplicationException;
import com.sun.jersey.core.header.AcceptableToken;
import java.util.Collections;
import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.impl.ImplMessages;
import com.sun.jersey.core.header.MediaTypes;
import com.sun.jersey.core.header.AcceptableMediaType;
import java.util.List;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import com.sun.jersey.core.header.MatchingEntityTag;
import java.util.Set;
import java.text.ParseException;
import com.sun.jersey.core.header.LanguageTag;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.core.HttpRequestContext;

public final class HttpHelper
{
    public static MediaType getContentType(final HttpRequestContext request) {
        return getContentType(request.getRequestHeaders().getFirst("Content-Type"));
    }
    
    public static MediaType getContentType(final String contentTypeString) {
        try {
            return (contentTypeString != null) ? MediaType.valueOf(contentTypeString) : null;
        }
        catch (IllegalArgumentException e) {
            throw clientError("Bad Content-Type header value: '" + contentTypeString + "'", e);
        }
    }
    
    public static MediaType getContentType(final Object contentType) {
        if (contentType == null) {
            return null;
        }
        if (contentType instanceof MediaType) {
            return (MediaType)contentType;
        }
        return MediaType.valueOf(contentType.toString());
    }
    
    public static Locale getContentLanguageAsLocale(final HttpRequestContext request) {
        return getLanguageTagAsLocale(request.getRequestHeaders().getFirst("Content-Language"));
    }
    
    public static Locale getLanguageTagAsLocale(final String language) {
        if (language == null) {
            return null;
        }
        try {
            return new LanguageTag(language).getAsLocale();
        }
        catch (ParseException e) {
            throw clientError("Bad Content-Language header value: '" + language + "'", e);
        }
    }
    
    public static Set<MatchingEntityTag> getIfMatch(final HttpRequestContext request) {
        final String ifMatch = request.getHeaderValue("If-Match");
        if (ifMatch == null || ifMatch.length() == 0) {
            return null;
        }
        try {
            return HttpHeaderReader.readMatchingEntityTag(ifMatch);
        }
        catch (ParseException e) {
            throw clientError("Bad If-Match header value: '" + ifMatch + "'", e);
        }
    }
    
    public static Set<MatchingEntityTag> getIfNoneMatch(final HttpRequestContext request) {
        final String ifNoneMatch = request.getHeaderValue("If-None-Match");
        if (ifNoneMatch == null || ifNoneMatch.length() == 0) {
            return null;
        }
        try {
            return HttpHeaderReader.readMatchingEntityTag(ifNoneMatch);
        }
        catch (ParseException e) {
            throw clientError("Bad If-None-Match header value: '" + ifNoneMatch + "'", e);
        }
    }
    
    public static List<AcceptableMediaType> getAccept(final HttpRequestContext request) {
        final String accept = request.getHeaderValue("Accept");
        if (accept == null || accept.length() == 0) {
            return MediaTypes.GENERAL_ACCEPT_MEDIA_TYPE_LIST;
        }
        try {
            return HttpHeaderReader.readAcceptMediaType(accept);
        }
        catch (ParseException e) {
            throw clientError(ImplMessages.BAD_ACCEPT_FIELD(accept), e);
        }
    }
    
    public static List<AcceptableMediaType> getAccept(final HttpRequestContext request, final List<QualitySourceMediaType> priorityMediaTypes) {
        final String accept = request.getHeaderValue("Accept");
        if (accept == null || accept.length() == 0) {
            return MediaTypes.GENERAL_ACCEPT_MEDIA_TYPE_LIST;
        }
        try {
            return HttpHeaderReader.readAcceptMediaType(accept, priorityMediaTypes);
        }
        catch (ParseException e) {
            throw clientError(ImplMessages.BAD_ACCEPT_FIELD(accept), e);
        }
    }
    
    @Deprecated
    public static List<AcceptableLanguageTag> getAcceptLangauge(final HttpRequestContext request) {
        return getAcceptLanguage(request);
    }
    
    public static List<AcceptableLanguageTag> getAcceptLanguage(final HttpRequestContext request) {
        final String acceptLanguage = request.getHeaderValue("Accept-Language");
        if (acceptLanguage == null || acceptLanguage.length() == 0) {
            return Collections.singletonList(new AcceptableLanguageTag("*", null));
        }
        try {
            return HttpHeaderReader.readAcceptLanguage(acceptLanguage);
        }
        catch (ParseException e) {
            throw clientError("Bad Accept-Language header value: '" + acceptLanguage + "'", e);
        }
    }
    
    public static List<AcceptableToken> getAcceptCharset(final HttpRequestContext request) {
        final String acceptCharset = request.getHeaderValue("Accept-Charset");
        try {
            if (acceptCharset == null || acceptCharset.length() == 0) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptCharset);
        }
        catch (ParseException e) {
            throw clientError("Bad Accept-Charset header value: '" + acceptCharset + "'", e);
        }
    }
    
    public static List<AcceptableToken> getAcceptEncoding(final HttpRequestContext request) {
        final String acceptEncoding = request.getHeaderValue("Accept-Encoding");
        try {
            if (acceptEncoding == null || acceptEncoding.length() == 0) {
                return Collections.singletonList(new AcceptableToken("*"));
            }
            return HttpHeaderReader.readAcceptToken(acceptEncoding);
        }
        catch (ParseException e) {
            throw clientError("Bad Accept-Encoding header value: '" + acceptEncoding + "'", e);
        }
    }
    
    private static WebApplicationException clientError(final String message, final Exception e) {
        return new WebApplicationException(e, Response.status(Response.Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }
    
    public static boolean produces(final MediaType contentType, final List<MediaType> accept) {
        for (final MediaType a : accept) {
            if (a.getType().equals("*")) {
                return true;
            }
            if (contentType.isCompatible(a)) {
                return true;
            }
        }
        return false;
    }
}
