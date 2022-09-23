// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.container.filter;

import java.util.List;
import javax.ws.rs.core.PathSegment;
import com.sun.jersey.spi.container.ContainerRequest;
import java.util.Collections;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import com.sun.jersey.spi.container.ContainerRequestFilter;

public class UriConnegFilter implements ContainerRequestFilter
{
    private final Map<String, MediaType> mediaExtentions;
    private final Map<String, String> languageExtentions;
    
    public UriConnegFilter(final Map<String, MediaType> mediaExtentions) {
        if (mediaExtentions == null) {
            throw new IllegalArgumentException();
        }
        this.mediaExtentions = mediaExtentions;
        this.languageExtentions = Collections.emptyMap();
    }
    
    public UriConnegFilter(final Map<String, MediaType> mediaExtentions, final Map<String, String> languageExtentions) {
        if (mediaExtentions == null) {
            throw new IllegalArgumentException();
        }
        if (languageExtentions == null) {
            throw new IllegalArgumentException();
        }
        this.mediaExtentions = mediaExtentions;
        this.languageExtentions = languageExtentions;
    }
    
    @Override
    public ContainerRequest filter(final ContainerRequest request) {
        String path = request.getRequestUri().getRawPath();
        if (path.indexOf(46) == -1) {
            return request;
        }
        final List<PathSegment> l = request.getPathSegments(false);
        if (l.isEmpty()) {
            return request;
        }
        PathSegment segment = null;
        for (int i = l.size() - 1; i >= 0; --i) {
            segment = l.get(i);
            if (segment.getPath().length() > 0) {
                break;
            }
        }
        if (segment == null) {
            return request;
        }
        final int length = path.length();
        final String[] suffixes = segment.getPath().split("\\.");
        for (int j = suffixes.length - 1; j >= 1; --j) {
            final String suffix = suffixes[j];
            if (suffix.length() != 0) {
                final MediaType accept = this.mediaExtentions.get(suffix);
                if (accept != null) {
                    request.getRequestHeaders().putSingle("Accept", accept.toString());
                    final int index = path.lastIndexOf('.' + suffix);
                    path = new StringBuilder(path).delete(index, index + suffix.length() + 1).toString();
                    suffixes[j] = "";
                    break;
                }
            }
        }
        for (int j = suffixes.length - 1; j >= 1; --j) {
            final String suffix = suffixes[j];
            if (suffix.length() != 0) {
                final String acceptLanguage = this.languageExtentions.get(suffix);
                if (acceptLanguage != null) {
                    request.getRequestHeaders().putSingle("Accept-Language", acceptLanguage);
                    final int index = path.lastIndexOf('.' + suffix);
                    path = new StringBuilder(path).delete(index, index + suffix.length() + 1).toString();
                    suffixes[j] = "";
                    break;
                }
            }
        }
        if (length != path.length()) {
            request.setUris(request.getBaseUri(), request.getRequestUriBuilder().replacePath(path).build(new Object[0]));
        }
        return request;
    }
}
