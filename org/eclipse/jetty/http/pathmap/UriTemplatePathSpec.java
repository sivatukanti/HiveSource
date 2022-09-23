// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

import java.util.HashSet;
import org.eclipse.jetty.util.log.Log;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import org.eclipse.jetty.util.TypeUtil;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.jetty.util.log.Logger;

public class UriTemplatePathSpec extends RegexPathSpec
{
    private static final Logger LOG;
    private static final Pattern VARIABLE_PATTERN;
    private static final String VARIABLE_RESERVED = ":/?#[]@!$&'()*+,;=";
    private static final String VARIABLE_SYMBOLS = "-._";
    private static final Set<String> FORBIDDEN_SEGMENTS;
    private String[] variables;
    
    public UriTemplatePathSpec(final String rawSpec) {
        Objects.requireNonNull(rawSpec, "Path Param Spec cannot be null");
        if ("".equals(rawSpec) || "/".equals(rawSpec)) {
            super.pathSpec = "/";
            super.pattern = Pattern.compile("^/$");
            super.pathDepth = 1;
            this.specLength = 1;
            this.variables = new String[0];
            this.group = PathSpecGroup.EXACT;
            return;
        }
        if (rawSpec.charAt(0) != '/') {
            final StringBuilder err = new StringBuilder();
            err.append("Syntax Error: path spec \"");
            err.append(rawSpec);
            err.append("\" must start with '/'");
            throw new IllegalArgumentException(err.toString());
        }
        for (final String forbidden : UriTemplatePathSpec.FORBIDDEN_SEGMENTS) {
            if (rawSpec.contains(forbidden)) {
                final StringBuilder err2 = new StringBuilder();
                err2.append("Syntax Error: segment ");
                err2.append(forbidden);
                err2.append(" is forbidden in path spec: ");
                err2.append(rawSpec);
                throw new IllegalArgumentException(err2.toString());
            }
        }
        this.pathSpec = rawSpec;
        final StringBuilder regex = new StringBuilder();
        regex.append('^');
        final List<String> varNames = new ArrayList<String>();
        final String[] segments = rawSpec.substring(1).split("/");
        final char[] segmentSignature = new char[segments.length];
        this.pathDepth = segments.length;
        for (int i = 0; i < segments.length; ++i) {
            final String segment = segments[i];
            final Matcher mat = UriTemplatePathSpec.VARIABLE_PATTERN.matcher(segment);
            if (mat.matches()) {
                final String variable = mat.group(1);
                if (varNames.contains(variable)) {
                    final StringBuilder err3 = new StringBuilder();
                    err3.append("Syntax Error: variable ");
                    err3.append(variable);
                    err3.append(" is duplicated in path spec: ");
                    err3.append(rawSpec);
                    throw new IllegalArgumentException(err3.toString());
                }
                this.assertIsValidVariableLiteral(variable);
                segmentSignature[i] = 'v';
                varNames.add(variable);
                regex.append("/([^/]+)");
            }
            else {
                if (mat.find(0)) {
                    final StringBuilder err4 = new StringBuilder();
                    err4.append("Syntax Error: variable ");
                    err4.append(mat.group());
                    err4.append(" must exist as entire path segment: ");
                    err4.append(rawSpec);
                    throw new IllegalArgumentException(err4.toString());
                }
                if (segment.indexOf(123) >= 0 || segment.indexOf(125) >= 0) {
                    final StringBuilder err4 = new StringBuilder();
                    err4.append("Syntax Error: invalid path segment /");
                    err4.append(segment);
                    err4.append("/ variable declaration incomplete: ");
                    err4.append(rawSpec);
                    throw new IllegalArgumentException(err4.toString());
                }
                if (segment.indexOf(42) >= 0) {
                    final StringBuilder err4 = new StringBuilder();
                    err4.append("Syntax Error: path segment /");
                    err4.append(segment);
                    err4.append("/ contains a wildcard symbol (not supported by this uri-template implementation): ");
                    err4.append(rawSpec);
                    throw new IllegalArgumentException(err4.toString());
                }
                segmentSignature[i] = 'e';
                regex.append('/');
                for (final char c : segment.toCharArray()) {
                    if (c == '.' || c == '[' || c == ']' || c == '\\') {
                        regex.append('\\');
                    }
                    regex.append(c);
                }
            }
        }
        if (rawSpec.charAt(rawSpec.length() - 1) == '/') {
            regex.append('/');
        }
        regex.append('$');
        this.pattern = Pattern.compile(regex.toString());
        final int varcount = varNames.size();
        this.variables = varNames.toArray(new String[varcount]);
        final String sig = String.valueOf(segmentSignature);
        if (Pattern.matches("^e*$", sig)) {
            this.group = PathSpecGroup.EXACT;
        }
        else if (Pattern.matches("^e*v+", sig)) {
            this.group = PathSpecGroup.PREFIX_GLOB;
        }
        else if (Pattern.matches("^v+e+", sig)) {
            this.group = PathSpecGroup.SUFFIX_GLOB;
        }
        else {
            this.group = PathSpecGroup.MIDDLE_GLOB;
        }
    }
    
    private void assertIsValidVariableLiteral(final String variable) {
        final int len = variable.length();
        int i = 0;
        boolean valid = len > 0;
        while (valid && i < len) {
            int codepoint = variable.codePointAt(i);
            i += Character.charCount(codepoint);
            if (this.isValidBasicLiteralCodepoint(codepoint)) {
                continue;
            }
            if (Character.isSupplementaryCodePoint(codepoint)) {
                continue;
            }
            if (codepoint == 37) {
                if (i + 2 > len) {
                    valid = false;
                    continue;
                }
                codepoint = TypeUtil.convertHexDigit(variable.codePointAt(i++)) << 4;
                codepoint |= TypeUtil.convertHexDigit(variable.codePointAt(i++));
                if (this.isValidBasicLiteralCodepoint(codepoint)) {
                    continue;
                }
            }
            valid = false;
        }
        if (!valid) {
            final StringBuilder err = new StringBuilder();
            err.append("Syntax Error: variable {");
            err.append(variable);
            err.append("} an invalid variable name: ");
            err.append(this.pathSpec);
            throw new IllegalArgumentException(err.toString());
        }
    }
    
    private boolean isValidBasicLiteralCodepoint(final int codepoint) {
        if ((codepoint >= 97 && codepoint <= 122) || (codepoint >= 65 && codepoint <= 90) || (codepoint >= 48 && codepoint <= 57)) {
            return true;
        }
        if ("-._".indexOf(codepoint) >= 0) {
            return true;
        }
        if (":/?#[]@!$&'()*+,;=".indexOf(codepoint) >= 0) {
            UriTemplatePathSpec.LOG.warn("Detected URI Template reserved symbol [{}] in path spec \"{}\"", (char)codepoint, this.pathSpec);
            return false;
        }
        return false;
    }
    
    public Map<String, String> getPathParams(final String path) {
        final Matcher matcher = this.getMatcher(path);
        if (!matcher.matches()) {
            return null;
        }
        if (this.group == PathSpecGroup.EXACT) {
            return Collections.emptyMap();
        }
        final Map<String, String> ret = new HashMap<String, String>();
        for (int groupCount = matcher.groupCount(), i = 1; i <= groupCount; ++i) {
            ret.put(this.variables[i - 1], matcher.group(i));
        }
        return ret;
    }
    
    public int getVariableCount() {
        return this.variables.length;
    }
    
    public String[] getVariables() {
        return this.variables;
    }
    
    static {
        LOG = Log.getLogger(UriTemplatePathSpec.class);
        VARIABLE_PATTERN = Pattern.compile("\\{(.*)\\}");
        (FORBIDDEN_SEGMENTS = new HashSet<String>()).add("/./");
        UriTemplatePathSpec.FORBIDDEN_SEGMENTS.add("/../");
        UriTemplatePathSpec.FORBIDDEN_SEGMENTS.add("//");
    }
}
