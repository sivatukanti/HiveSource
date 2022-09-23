// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum UriPatternType
{
    SERVLET, 
    REGEX;
    
    static UriPatternMatcher get(final UriPatternType type, final String pattern) {
        switch (type) {
            case SERVLET: {
                return new ServletStyleUriPatternMatcher(pattern);
            }
            case REGEX: {
                return new RegexUriPatternMatcher(pattern);
            }
            default: {
                return null;
            }
        }
    }
    
    private static class ServletStyleUriPatternMatcher implements UriPatternMatcher
    {
        private final String pattern;
        private final Kind patternKind;
        
        public ServletStyleUriPatternMatcher(final String pattern) {
            if (pattern.startsWith("*")) {
                this.pattern = pattern.substring(1);
                this.patternKind = Kind.PREFIX;
            }
            else if (pattern.endsWith("*")) {
                this.pattern = pattern.substring(0, pattern.length() - 1);
                this.patternKind = Kind.SUFFIX;
            }
            else {
                this.pattern = pattern;
                this.patternKind = Kind.LITERAL;
            }
        }
        
        public boolean matches(final String uri) {
            if (null == uri) {
                return false;
            }
            if (this.patternKind == Kind.PREFIX) {
                return uri.endsWith(this.pattern);
            }
            if (this.patternKind == Kind.SUFFIX) {
                return uri.startsWith(this.pattern);
            }
            return this.pattern.equals(uri);
        }
        
        public String extractPath(final String path) {
            if (this.patternKind == Kind.PREFIX) {
                return null;
            }
            if (this.patternKind == Kind.SUFFIX) {
                String extract = this.pattern;
                if (extract.endsWith("/")) {
                    extract = extract.substring(0, extract.length() - 1);
                }
                return extract;
            }
            return path;
        }
        
        public UriPatternType getPatternType() {
            return UriPatternType.SERVLET;
        }
        
        private enum Kind
        {
            PREFIX, 
            SUFFIX, 
            LITERAL;
        }
    }
    
    private static class RegexUriPatternMatcher implements UriPatternMatcher
    {
        private final Pattern pattern;
        
        public RegexUriPatternMatcher(final String pattern) {
            this.pattern = Pattern.compile(pattern);
        }
        
        public boolean matches(final String uri) {
            return null != uri && this.pattern.matcher(uri).matches();
        }
        
        public String extractPath(final String path) {
            final Matcher matcher = this.pattern.matcher(path);
            if (matcher.matches() && matcher.groupCount() >= 1) {
                final int end = matcher.start(1);
                if (end < path.length()) {
                    return path.substring(0, end);
                }
            }
            return null;
        }
        
        public UriPatternType getPatternType() {
            return UriPatternType.REGEX;
        }
    }
}
