// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPathSpec extends PathSpec
{
    protected Pattern pattern;
    
    protected RegexPathSpec() {
    }
    
    public RegexPathSpec(final String regex) {
        super.pathSpec = regex;
        if (regex.startsWith("regex|")) {
            super.pathSpec = regex.substring("regex|".length());
        }
        this.pathDepth = 0;
        this.specLength = this.pathSpec.length();
        boolean inGrouping = false;
        final StringBuilder signature = new StringBuilder();
        for (final char c : this.pathSpec.toCharArray()) {
            switch (c) {
                case '[': {
                    inGrouping = true;
                    break;
                }
                case ']': {
                    inGrouping = false;
                    signature.append('g');
                    break;
                }
                case '*': {
                    signature.append('g');
                    break;
                }
                case '/': {
                    if (!inGrouping) {
                        ++this.pathDepth;
                        break;
                    }
                    break;
                }
                default: {
                    if (!inGrouping && Character.isLetterOrDigit(c)) {
                        signature.append('l');
                        break;
                    }
                    break;
                }
            }
        }
        this.pattern = Pattern.compile(this.pathSpec);
        final String sig = signature.toString();
        if (Pattern.matches("^l*$", sig)) {
            this.group = PathSpecGroup.EXACT;
        }
        else if (Pattern.matches("^l*g+", sig)) {
            this.group = PathSpecGroup.PREFIX_GLOB;
        }
        else if (Pattern.matches("^g+l+$", sig)) {
            this.group = PathSpecGroup.SUFFIX_GLOB;
        }
        else {
            this.group = PathSpecGroup.MIDDLE_GLOB;
        }
    }
    
    public Matcher getMatcher(final String path) {
        return this.pattern.matcher(path);
    }
    
    @Override
    public String getPathInfo(final String path) {
        if (this.group == PathSpecGroup.PREFIX_GLOB) {
            final Matcher matcher = this.getMatcher(path);
            if (matcher.matches() && matcher.groupCount() >= 1) {
                final String pathInfo = matcher.group(1);
                if ("".equals(pathInfo)) {
                    return "/";
                }
                return pathInfo;
            }
        }
        return null;
    }
    
    @Override
    public String getPathMatch(final String path) {
        final Matcher matcher = this.getMatcher(path);
        if (matcher.matches()) {
            if (matcher.groupCount() >= 1) {
                int idx = matcher.start(1);
                if (idx > 0) {
                    if (path.charAt(idx - 1) == '/') {
                        --idx;
                    }
                    return path.substring(0, idx);
                }
            }
            return path;
        }
        return null;
    }
    
    public Pattern getPattern() {
        return this.pattern;
    }
    
    @Override
    public String getRelativePath(final String base, final String path) {
        return null;
    }
    
    @Override
    public boolean matches(final String path) {
        final int idx = path.indexOf(63);
        if (idx >= 0) {
            return this.getMatcher(path.substring(0, idx)).matches();
        }
        return this.getMatcher(path).matches();
    }
}
