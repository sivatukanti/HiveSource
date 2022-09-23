// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.uri;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.Iterator;
import java.util.regex.PatternSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Comparator;

public class UriTemplate
{
    private static String[] EMPTY_VALUES;
    public static final Comparator<UriTemplate> COMPARATOR;
    private static final Pattern TEMPLATE_NAMES_PATTERN;
    public static final UriTemplate EMPTY;
    private final String template;
    private final String normalizedTemplate;
    private final UriPattern pattern;
    private final boolean endsWithSlash;
    private final List<String> templateVariables;
    private final int numOfExplicitRegexes;
    private final int numOfCharacters;
    
    private UriTemplate() {
        final String s = "";
        this.normalizedTemplate = s;
        this.template = s;
        this.pattern = UriPattern.EMPTY;
        this.endsWithSlash = false;
        this.templateVariables = Collections.emptyList();
        final int n = 0;
        this.numOfCharacters = n;
        this.numOfExplicitRegexes = n;
    }
    
    public UriTemplate(final String template) throws PatternSyntaxException, IllegalArgumentException {
        this(new UriTemplateParser(template));
    }
    
    protected UriTemplate(final UriTemplateParser templateParser) throws PatternSyntaxException, IllegalArgumentException {
        this.template = templateParser.getTemplate();
        this.normalizedTemplate = templateParser.getNormalizedTemplate();
        this.pattern = this.createUriPattern(templateParser);
        this.numOfExplicitRegexes = templateParser.getNumberOfExplicitRegexes();
        this.numOfCharacters = templateParser.getNumberOfLiteralCharacters();
        this.endsWithSlash = (this.template.charAt(this.template.length() - 1) == '/');
        this.templateVariables = Collections.unmodifiableList((List<? extends String>)templateParser.getNames());
    }
    
    protected UriPattern createUriPattern(final UriTemplateParser templateParser) {
        return new UriPattern(templateParser.getPattern(), templateParser.getGroupIndexes());
    }
    
    public final String getTemplate() {
        return this.template;
    }
    
    public final UriPattern getPattern() {
        return this.pattern;
    }
    
    public final boolean endsWithSlash() {
        return this.endsWithSlash;
    }
    
    public final List<String> getTemplateVariables() {
        return this.templateVariables;
    }
    
    public final boolean isTemplateVariablePresent(final String name) {
        for (final String s : this.templateVariables) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public final int getNumberOfExplicitRegexes() {
        return this.numOfExplicitRegexes;
    }
    
    public final int getNumberOfExplicitCharacters() {
        return this.numOfCharacters;
    }
    
    public final int getNumberOfTemplateVariables() {
        return this.templateVariables.size();
    }
    
    public final boolean match(final CharSequence uri, final Map<String, String> templateVariableToValue) throws IllegalArgumentException {
        if (templateVariableToValue == null) {
            throw new IllegalArgumentException();
        }
        return this.pattern.match(uri, this.templateVariables, templateVariableToValue);
    }
    
    public final boolean match(final CharSequence uri, final List<String> groupValues) throws IllegalArgumentException {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        return this.pattern.match(uri, groupValues);
    }
    
    public final String createURI(final Map<String, String> values) {
        final StringBuilder b = new StringBuilder();
        final Matcher m = UriTemplate.TEMPLATE_NAMES_PATTERN.matcher(this.normalizedTemplate);
        int i = 0;
        while (m.find()) {
            b.append(this.normalizedTemplate, i, m.start());
            final String tValue = values.get(m.group(1));
            if (tValue != null) {
                b.append(tValue);
            }
            i = m.end();
        }
        b.append(this.normalizedTemplate, i, this.normalizedTemplate.length());
        return b.toString();
    }
    
    public final String createURI(final String... values) {
        return this.createURI(values, 0, values.length);
    }
    
    public final String createURI(final String[] values, final int offset, int length) {
        final Map<String, String> mapValues = new HashMap<String, String>();
        final StringBuilder b = new StringBuilder();
        final Matcher m = UriTemplate.TEMPLATE_NAMES_PATTERN.matcher(this.normalizedTemplate);
        int v = offset;
        length += offset;
        int i = 0;
        while (m.find()) {
            b.append(this.normalizedTemplate, i, m.start());
            final String tVariable = m.group(1);
            String tValue = mapValues.get(tVariable);
            if (tValue != null) {
                b.append(tValue);
            }
            else if (v < length) {
                tValue = values[v++];
                if (tValue != null) {
                    mapValues.put(tVariable, tValue);
                    b.append(tValue);
                }
            }
            i = m.end();
        }
        b.append(this.normalizedTemplate, i, this.normalizedTemplate.length());
        return b.toString();
    }
    
    @Override
    public final String toString() {
        return this.pattern.toString();
    }
    
    @Override
    public final int hashCode() {
        return this.pattern.hashCode();
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (o instanceof UriTemplate) {
            final UriTemplate that = (UriTemplate)o;
            return this.pattern.equals(that.pattern);
        }
        return false;
    }
    
    public static final String createURI(final String scheme, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Map<String, ?> values, final boolean encode) {
        return createURI(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }
    
    public static final String createURI(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Map<String, ?> values, final boolean encode) {
        final Map<String, String> stringValues = new HashMap<String, String>();
        for (final Map.Entry<String, ?> e : values.entrySet()) {
            if (e.getValue() != null) {
                stringValues.put(e.getKey(), e.getValue().toString());
            }
        }
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, stringValues, encode);
    }
    
    public static final String createURIWithStringValues(final String scheme, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Map<String, ?> values, final boolean encode) {
        return createURIWithStringValues(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }
    
    public static final String createURIWithStringValues(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Map<String, ?> values, final boolean encode) {
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, UriTemplate.EMPTY_VALUES, encode, (Map<String, Object>)values);
    }
    
    public static final String createURI(final String scheme, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Object[] values, final boolean encode) {
        return createURI(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }
    
    public static final String createURI(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final Object[] values, final boolean encode) {
        final String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != null) {
                stringValues[i] = values[i].toString();
            }
        }
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, stringValues, encode);
    }
    
    public static final String createURIWithStringValues(final String scheme, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final String[] values, final boolean encode) {
        return createURIWithStringValues(scheme, null, userInfo, host, port, path, query, fragment, values, encode);
    }
    
    public static final String createURIWithStringValues(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final String[] values, final boolean encode) {
        return createURIWithStringValues(scheme, authority, userInfo, host, port, path, query, fragment, values, encode, new HashMap<String, Object>());
    }
    
    private static String createURIWithStringValues(final String scheme, final String authority, final String userInfo, final String host, final String port, final String path, final String query, final String fragment, final String[] values, final boolean encode, final Map<String, Object> mapValues) {
        final StringBuilder sb = new StringBuilder();
        int offset = 0;
        if (scheme != null) {
            offset = createURIComponent(UriComponent.Type.SCHEME, scheme, values, offset, false, mapValues, sb);
            sb.append(':');
        }
        if (userInfo != null || host != null || port != null) {
            sb.append("//");
            if (userInfo != null && userInfo.length() > 0) {
                offset = createURIComponent(UriComponent.Type.USER_INFO, userInfo, values, offset, encode, mapValues, sb);
                sb.append('@');
            }
            if (host != null) {
                offset = createURIComponent(UriComponent.Type.HOST, host, values, offset, encode, mapValues, sb);
            }
            if (port != null && port.length() > 0) {
                sb.append(':');
                offset = createURIComponent(UriComponent.Type.PORT, port, values, offset, false, mapValues, sb);
            }
        }
        else if (authority != null) {
            sb.append("//");
            offset = createURIComponent(UriComponent.Type.AUTHORITY, authority, values, offset, encode, mapValues, sb);
        }
        if (path != null && path.length() > 0) {
            if (sb.length() > 0 && path.charAt(0) != '/') {
                sb.append('/');
            }
            offset = createURIComponent(UriComponent.Type.PATH, path, values, offset, encode, mapValues, sb);
        }
        if (query != null && query.length() > 0) {
            sb.append('?');
            offset = createURIComponent(UriComponent.Type.QUERY_PARAM, query, values, offset, encode, mapValues, sb);
        }
        if (fragment != null && fragment.length() > 0) {
            sb.append('#');
            offset = createURIComponent(UriComponent.Type.FRAGMENT, fragment, values, offset, encode, mapValues, sb);
        }
        return sb.toString();
    }
    
    private static int createURIComponent(final UriComponent.Type t, String template, final String[] values, final int offset, final boolean encode, final Map<String, Object> mapValues, final StringBuilder b) {
        if (template.indexOf(123) == -1) {
            b.append(template);
            return offset;
        }
        template = new UriTemplateParser(template).getNormalizedTemplate();
        final Matcher m = UriTemplate.TEMPLATE_NAMES_PATTERN.matcher(template);
        int v = offset;
        int i = 0;
        while (m.find()) {
            b.append(template, i, m.start());
            final String tVariable = m.group(1);
            Object tValue = mapValues.get(tVariable);
            if (tValue == null && v < values.length) {
                tValue = values[v++];
            }
            if (tValue == null) {
                throw templateVariableHasNoValue(tVariable);
            }
            mapValues.put(tVariable, tValue);
            tValue = (encode ? UriComponent.encode(tValue.toString(), t) : UriComponent.contextualEncode(tValue.toString(), t));
            b.append(tValue);
            i = m.end();
        }
        b.append(template, i, template.length());
        return v;
    }
    
    private static IllegalArgumentException templateVariableHasNoValue(final String tVariable) {
        return new IllegalArgumentException("The template variable, " + tVariable + ", has no value");
    }
    
    static {
        UriTemplate.EMPTY_VALUES = new String[0];
        COMPARATOR = new Comparator<UriTemplate>() {
            @Override
            public int compare(final UriTemplate o1, final UriTemplate o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                if (o1 == UriTemplate.EMPTY && o2 == UriTemplate.EMPTY) {
                    return 0;
                }
                if (o1 == UriTemplate.EMPTY) {
                    return 1;
                }
                if (o2 == UriTemplate.EMPTY) {
                    return -1;
                }
                int i = o2.getNumberOfExplicitCharacters() - o1.getNumberOfExplicitCharacters();
                if (i != 0) {
                    return i;
                }
                i = o2.getNumberOfTemplateVariables() - o1.getNumberOfTemplateVariables();
                if (i != 0) {
                    return i;
                }
                i = o2.getNumberOfExplicitRegexes() - o1.getNumberOfExplicitRegexes();
                if (i != 0) {
                    return i;
                }
                return o2.pattern.getRegex().compareTo(o1.pattern.getRegex());
            }
        };
        TEMPLATE_NAMES_PATTERN = Pattern.compile("\\{(\\w[-\\w\\.]*)\\}");
        EMPTY = new UriTemplate();
    }
}
