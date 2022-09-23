// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

import org.eclipse.jetty.util.StringUtil;

public class ServletPathSpec extends PathSpec
{
    public static String normalize(final String pathSpec) {
        if (StringUtil.isNotBlank(pathSpec) && !pathSpec.startsWith("/") && !pathSpec.startsWith("*")) {
            return "/" + pathSpec;
        }
        return pathSpec;
    }
    
    public ServletPathSpec(String servletPathSpec) {
        if (servletPathSpec == null) {
            servletPathSpec = "";
        }
        if (servletPathSpec.startsWith("servlet|")) {
            servletPathSpec = servletPathSpec.substring("servlet|".length());
        }
        this.assertValidServletPathSpec(servletPathSpec);
        if (servletPathSpec.length() == 0) {
            super.pathSpec = "";
            super.pathDepth = -1;
            this.specLength = 1;
            this.group = PathSpecGroup.ROOT;
            return;
        }
        if ("/".equals(servletPathSpec)) {
            super.pathSpec = "/";
            super.pathDepth = -1;
            this.specLength = 1;
            this.group = PathSpecGroup.DEFAULT;
            return;
        }
        this.specLength = servletPathSpec.length();
        super.pathDepth = 0;
        final char lastChar = servletPathSpec.charAt(this.specLength - 1);
        if (servletPathSpec.charAt(0) == '/' && this.specLength > 1 && lastChar == '*') {
            this.group = PathSpecGroup.PREFIX_GLOB;
        }
        else if (servletPathSpec.charAt(0) == '*') {
            this.group = PathSpecGroup.SUFFIX_GLOB;
        }
        else {
            this.group = PathSpecGroup.EXACT;
        }
        for (int i = 0; i < this.specLength; ++i) {
            final int cp = servletPathSpec.codePointAt(i);
            if (cp < 128) {
                final char c = (char)cp;
                switch (c) {
                    case '/': {
                        ++super.pathDepth;
                        break;
                    }
                }
            }
        }
        super.pathSpec = servletPathSpec;
    }
    
    private void assertValidServletPathSpec(final String servletPathSpec) {
        if (servletPathSpec == null || servletPathSpec.equals("")) {
            return;
        }
        final int len = servletPathSpec.length();
        if (servletPathSpec.charAt(0) == '/') {
            if (len == 1) {
                return;
            }
            final int idx = servletPathSpec.indexOf(42);
            if (idx < 0) {
                return;
            }
            if (idx != len - 1) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: glob '*' can only exist at end of prefix based matches: bad spec \"" + servletPathSpec + "\"");
            }
        }
        else {
            if (!servletPathSpec.startsWith("*.")) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: path spec must start with \"/\" or \"*.\": bad spec \"" + servletPathSpec + "\"");
            }
            int idx = servletPathSpec.indexOf(47);
            if (idx >= 0) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: suffix based path spec cannot have path separators: bad spec \"" + servletPathSpec + "\"");
            }
            idx = servletPathSpec.indexOf(42, 2);
            if (idx >= 1) {
                throw new IllegalArgumentException("Servlet Spec 12.2 violation: suffix based path spec cannot have multiple glob '*': bad spec \"" + servletPathSpec + "\"");
            }
        }
    }
    
    @Override
    public String getPathInfo(final String path) {
        if (this.group != PathSpecGroup.PREFIX_GLOB) {
            return null;
        }
        if (path.length() == this.specLength - 2) {
            return null;
        }
        return path.substring(this.specLength - 2);
    }
    
    @Override
    public String getPathMatch(final String path) {
        switch (this.group) {
            case EXACT: {
                if (this.pathSpec.equals(path)) {
                    return path;
                }
                return null;
            }
            case PREFIX_GLOB: {
                if (this.isWildcardMatch(path)) {
                    return path.substring(0, this.specLength - 2);
                }
                return null;
            }
            case SUFFIX_GLOB: {
                if (path.regionMatches(path.length() - (this.specLength - 1), this.pathSpec, 1, this.specLength - 1)) {
                    return path;
                }
                return null;
            }
            case DEFAULT: {
                return path;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public String getRelativePath(final String base, String path) {
        String info = this.getPathInfo(path);
        if (info == null) {
            info = path;
        }
        if (info.startsWith("./")) {
            info = info.substring(2);
        }
        if (base.endsWith("/")) {
            if (info.startsWith("/")) {
                path = base + info.substring(1);
            }
            else {
                path = base + info;
            }
        }
        else if (info.startsWith("/")) {
            path = base + info;
        }
        else {
            path = base + "/" + info;
        }
        return path;
    }
    
    private boolean isWildcardMatch(final String path) {
        final int cpl = this.specLength - 2;
        return this.group == PathSpecGroup.PREFIX_GLOB && path.regionMatches(0, this.pathSpec, 0, cpl) && (path.length() == cpl || '/' == path.charAt(cpl));
    }
    
    @Override
    public boolean matches(final String path) {
        switch (this.group) {
            case EXACT: {
                return this.pathSpec.equals(path);
            }
            case PREFIX_GLOB: {
                return this.isWildcardMatch(path);
            }
            case SUFFIX_GLOB: {
                return path.regionMatches(path.length() - this.specLength + 1, this.pathSpec, 1, this.specLength - 1);
            }
            case ROOT: {
                return "/".equals(path);
            }
            case DEFAULT: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
