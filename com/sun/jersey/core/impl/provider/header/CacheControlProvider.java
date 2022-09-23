// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import java.util.regex.Matcher;
import java.text.ParseException;
import java.util.Collection;
import java.util.Arrays;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import javax.ws.rs.core.CacheControl;
import com.sun.jersey.spi.HeaderDelegateProvider;

public final class CacheControlProvider implements HeaderDelegateProvider<CacheControl>
{
    private static final Pattern WHITESPACE;
    private static final Pattern COMMA_SEPARATED_LIST;
    
    @Override
    public boolean supports(final Class<?> type) {
        return type == CacheControl.class;
    }
    
    @Override
    public String toString(final CacheControl header) {
        final StringBuffer b = new StringBuffer();
        if (header.isPrivate()) {
            this.appendQuotedWithSeparator(b, "private", this.buildListValue(header.getPrivateFields()));
        }
        if (header.isNoCache()) {
            this.appendQuotedWithSeparator(b, "no-cache", this.buildListValue(header.getNoCacheFields()));
        }
        if (header.isNoStore()) {
            this.appendWithSeparator(b, "no-store");
        }
        if (header.isNoTransform()) {
            this.appendWithSeparator(b, "no-transform");
        }
        if (header.isMustRevalidate()) {
            this.appendWithSeparator(b, "must-revalidate");
        }
        if (header.isProxyRevalidate()) {
            this.appendWithSeparator(b, "proxy-revalidate");
        }
        if (header.getMaxAge() != -1) {
            this.appendWithSeparator(b, "max-age", header.getMaxAge());
        }
        if (header.getSMaxAge() != -1) {
            this.appendWithSeparator(b, "s-maxage", header.getSMaxAge());
        }
        for (final Map.Entry<String, String> e : header.getCacheExtension().entrySet()) {
            this.appendWithSeparator(b, e.getKey(), this.quoteIfWhitespace(e.getValue()));
        }
        return b.toString();
    }
    
    private void readFieldNames(final List<String> fieldNames, final HttpHeaderReader reader, final String directiveName) throws ParseException {
        if (!reader.hasNextSeparator('=', false)) {
            return;
        }
        reader.nextSeparator('=');
        fieldNames.addAll(Arrays.asList(CacheControlProvider.COMMA_SEPARATED_LIST.split(reader.nextQuotedString())));
    }
    
    private int readIntValue(final HttpHeaderReader reader, final String directiveName) throws ParseException {
        reader.nextSeparator('=');
        final int index = reader.getIndex();
        try {
            return Integer.parseInt(reader.nextToken());
        }
        catch (NumberFormatException nfe) {
            final ParseException pe = new ParseException("Error parsing integer value for " + directiveName + " directive", index);
            pe.initCause(nfe);
            throw pe;
        }
    }
    
    private void readDirective(final CacheControl cacheControl, final HttpHeaderReader reader) throws ParseException {
        final String directiveName = reader.nextToken();
        if (directiveName.equalsIgnoreCase("private")) {
            cacheControl.setPrivate(true);
            this.readFieldNames(cacheControl.getPrivateFields(), reader, directiveName);
        }
        else if (directiveName.equalsIgnoreCase("public")) {
            cacheControl.getCacheExtension().put(directiveName, null);
        }
        else if (directiveName.equalsIgnoreCase("no-cache")) {
            cacheControl.setNoCache(true);
            this.readFieldNames(cacheControl.getNoCacheFields(), reader, directiveName);
        }
        else if (directiveName.equalsIgnoreCase("no-store")) {
            cacheControl.setNoStore(true);
        }
        else if (directiveName.equalsIgnoreCase("no-transform")) {
            cacheControl.setNoTransform(true);
        }
        else if (directiveName.equalsIgnoreCase("must-revalidate")) {
            cacheControl.setMustRevalidate(true);
        }
        else if (directiveName.equalsIgnoreCase("proxy-revalidate")) {
            cacheControl.setProxyRevalidate(true);
        }
        else if (directiveName.equalsIgnoreCase("max-age")) {
            cacheControl.setMaxAge(this.readIntValue(reader, directiveName));
        }
        else if (directiveName.equalsIgnoreCase("s-maxage")) {
            cacheControl.setSMaxAge(this.readIntValue(reader, directiveName));
        }
        else {
            String value = null;
            if (reader.hasNextSeparator('=', false)) {
                reader.nextSeparator('=');
                value = reader.nextTokenOrQuotedString();
            }
            cacheControl.getCacheExtension().put(directiveName, value);
        }
    }
    
    @Override
    public CacheControl fromString(final String header) {
        if (header == null) {
            throw new IllegalArgumentException("Cache control is null");
        }
        try {
            final HttpHeaderReader reader = HttpHeaderReader.newInstance(header);
            final CacheControl cacheControl = new CacheControl();
            cacheControl.setNoTransform(false);
            while (reader.hasNext()) {
                this.readDirective(cacheControl, reader);
                if (reader.hasNextSeparator(',', true)) {
                    reader.nextSeparator(',');
                }
            }
            return cacheControl;
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException("Error parsing cache control '" + header + "'", pe);
        }
    }
    
    private void appendWithSeparator(final StringBuffer b, final String field) {
        if (b.length() > 0) {
            b.append(", ");
        }
        b.append(field);
    }
    
    private void appendQuotedWithSeparator(final StringBuffer b, final String field, final String value) {
        this.appendWithSeparator(b, field);
        if (value != null && value.length() > 0) {
            b.append("=\"");
            b.append(value);
            b.append("\"");
        }
    }
    
    private void appendWithSeparator(final StringBuffer b, final String field, final String value) {
        this.appendWithSeparator(b, field);
        if (value != null && value.length() > 0) {
            b.append("=");
            b.append(value);
        }
    }
    
    private void appendWithSeparator(final StringBuffer b, final String field, final int value) {
        this.appendWithSeparator(b, field);
        b.append("=");
        b.append(value);
    }
    
    private String buildListValue(final List<String> values) {
        final StringBuffer b = new StringBuffer();
        for (final String value : values) {
            this.appendWithSeparator(b, value);
        }
        return b.toString();
    }
    
    private String quoteIfWhitespace(final String value) {
        if (value == null) {
            return null;
        }
        final Matcher m = CacheControlProvider.WHITESPACE.matcher(value);
        if (m.find()) {
            return "\"" + value + "\"";
        }
        return value;
    }
    
    static {
        WHITESPACE = Pattern.compile("\\s");
        COMMA_SEPARATED_LIST = Pattern.compile("[\\s]*,[\\s]*");
    }
}
