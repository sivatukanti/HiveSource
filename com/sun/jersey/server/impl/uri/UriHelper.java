// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public final class UriHelper
{
    public static URI normalize(final URI uri, final boolean preserveContdSlashes) {
        if (!uri.getRawPath().contains("//")) {
            return uri.normalize();
        }
        final String np = removeDotSegments(uri.getRawPath(), preserveContdSlashes);
        if (np.equals(uri.getRawPath())) {
            return uri;
        }
        return UriBuilder.fromUri(uri).replacePath(np).build(new Object[0]);
    }
    
    private static String removeLeadingSlashesIfNeeded(final String path, final boolean preserveSlashes) {
        if (preserveSlashes) {
            return path;
        }
        String trimmed;
        for (trimmed = path; trimmed.startsWith("/"); trimmed = trimmed.substring(1)) {}
        return trimmed;
    }
    
    public static String removeDotSegments(String path, final boolean preserveContdSlashes) {
        if (null == path) {
            return null;
        }
        final List<String> outputSegments = new LinkedList<String>();
        while (path.length() > 0) {
            if (path.startsWith("../")) {
                path = removeLeadingSlashesIfNeeded(path.substring(3), preserveContdSlashes);
            }
            else if (path.startsWith("./")) {
                path = removeLeadingSlashesIfNeeded(path.substring(2), preserveContdSlashes);
            }
            else if (path.startsWith("/./")) {
                path = "/" + removeLeadingSlashesIfNeeded(path.substring(3), preserveContdSlashes);
            }
            else if ("/.".equals(path)) {
                path = "/";
            }
            else if (path.startsWith("/../")) {
                path = "/" + removeLeadingSlashesIfNeeded(path.substring(4), preserveContdSlashes);
                if (outputSegments.isEmpty()) {
                    continue;
                }
                outputSegments.remove(outputSegments.size() - 1);
            }
            else if ("/..".equals(path)) {
                path = "/";
                if (outputSegments.isEmpty()) {
                    continue;
                }
                outputSegments.remove(outputSegments.size() - 1);
            }
            else if ("..".equals(path) || ".".equals(path)) {
                path = "";
            }
            else {
                int slashStartSearchIndex;
                if (path.startsWith("/")) {
                    path = "/" + removeLeadingSlashesIfNeeded(path.substring(1), preserveContdSlashes);
                    slashStartSearchIndex = 1;
                }
                else {
                    slashStartSearchIndex = 0;
                }
                int segLength = path.indexOf(47, slashStartSearchIndex);
                if (-1 == segLength) {
                    segLength = path.length();
                }
                outputSegments.add(path.substring(0, segLength));
                path = path.substring(segLength);
            }
        }
        final StringBuffer result = new StringBuffer();
        for (final String segment : outputSegments) {
            result.append(segment);
        }
        return result.toString();
    }
}
