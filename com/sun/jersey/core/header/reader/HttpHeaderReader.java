// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header.reader;

import com.sun.jersey.core.impl.provider.header.MediaTypeProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import com.sun.jersey.core.header.MediaTypes;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Cookie;
import java.util.LinkedHashMap;
import java.util.Map;
import com.sun.jersey.core.header.HttpDateFormat;
import java.util.Date;
import java.text.ParseException;
import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.header.AcceptableToken;
import com.sun.jersey.core.header.QualitySourceMediaType;
import java.util.Comparator;
import com.sun.jersey.core.header.AcceptableMediaType;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.MatchingEntityTag;

public abstract class HttpHeaderReader
{
    private static final ListElementCreator<MatchingEntityTag> MATCHING_ENTITY_TAG_CREATOR;
    private static final ListElementCreator<MediaType> MEDIA_TYPE_CREATOR;
    private static final ListElementCreator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_CREATOR;
    private static final Comparator<AcceptableMediaType> ACCEPTABLE_MEDIA_TYPE_COMPARATOR;
    private static final ListElementCreator<QualitySourceMediaType> QUALITY_SOURCE_MEDIA_TYPE_CREATOR;
    private static final ListElementCreator<AcceptableToken> ACCEPTABLE_TOKEN_CREATOR;
    private static final ListElementCreator<AcceptableLanguageTag> LANGUAGE_CREATOR;
    private static final Comparator<QualityFactor> QUALITY_COMPARATOR;
    
    public abstract boolean hasNext();
    
    public abstract boolean hasNextSeparator(final char p0, final boolean p1);
    
    public abstract Event next() throws ParseException;
    
    public abstract Event next(final boolean p0) throws ParseException;
    
    public abstract Event next(final boolean p0, final boolean p1) throws ParseException;
    
    public abstract String nextSeparatedString(final char p0, final char p1) throws ParseException;
    
    public abstract Event getEvent();
    
    public abstract String getEventValue();
    
    public abstract String getRemainder();
    
    public abstract int getIndex();
    
    public String nextToken() throws ParseException {
        final Event e = this.next(false);
        if (e != Event.Token) {
            throw new ParseException("Next event is not a Token", this.getIndex());
        }
        return this.getEventValue();
    }
    
    public char nextSeparator() throws ParseException {
        final Event e = this.next(false);
        if (e != Event.Separator) {
            throw new ParseException("Next event is not a Separator", this.getIndex());
        }
        return this.getEventValue().charAt(0);
    }
    
    public void nextSeparator(final char c) throws ParseException {
        final Event e = this.next(false);
        if (e != Event.Separator) {
            throw new ParseException("Next event is not a Separator", this.getIndex());
        }
        if (c != this.getEventValue().charAt(0)) {
            throw new ParseException("Expected separator '" + c + "' instead of '" + this.getEventValue().charAt(0) + "'", this.getIndex());
        }
    }
    
    public String nextQuotedString() throws ParseException {
        final Event e = this.next(false);
        if (e != Event.QuotedString) {
            throw new ParseException("Next event is not a Quoted String", this.getIndex());
        }
        return this.getEventValue();
    }
    
    public String nextTokenOrQuotedString() throws ParseException {
        return this.nextTokenOrQuotedString(false);
    }
    
    public String nextTokenOrQuotedString(final boolean preserveBackslash) throws ParseException {
        final Event e = this.next(false, preserveBackslash);
        if (e != Event.Token && e != Event.QuotedString) {
            throw new ParseException("Next event is not a Token or a Quoted String, " + this.getEventValue(), this.getIndex());
        }
        return this.getEventValue();
    }
    
    public static HttpHeaderReader newInstance(final String header) {
        return new HttpHeaderReaderImpl(header);
    }
    
    public static HttpHeaderReader newInstance(final String header, final boolean processComments) {
        return new HttpHeaderReaderImpl(header, processComments);
    }
    
    public static Date readDate(final String date) throws ParseException {
        return HttpDateFormat.readDate(date);
    }
    
    public static int readQualityFactor(final String q) throws ParseException {
        if (q == null || q.length() == 0) {
            throw new ParseException("Quality value cannot be null or an empty String", 0);
        }
        int index = 0;
        final int length = q.length();
        if (length > 5) {
            throw new ParseException("Quality value is greater than the maximum length, 5", 0);
        }
        char c;
        final char wholeNumber = c = q.charAt(index++);
        if (c == '0' || c == '1') {
            if (index == length) {
                return (c - '0') * 1000;
            }
            c = q.charAt(index++);
            if (c != '.') {
                throw new ParseException("Error parsing Quality value: a decimal place is expected rather than '" + c + "'", index);
            }
            if (index == length) {
                return (c - '0') * 1000;
            }
        }
        else {
            if (c != '.') {
                throw new ParseException("Error parsing Quality value: a decimal numeral '0' or '1' is expected rather than '" + c + "'", index);
            }
            if (index == length) {
                throw new ParseException("Error parsing Quality value: a decimal numeral is expected after the decimal point", index);
            }
        }
        int value = 0;
        int exponent = 100;
        while (index < length) {
            c = q.charAt(index++);
            if (c < '0' || c > '9') {
                throw new ParseException("Error parsing Quality value: a decimal numeral is expected rather than '" + c + "'", index);
            }
            value += (c - '0') * exponent;
            exponent /= 10;
        }
        if (wholeNumber != '1') {
            return value;
        }
        if (value > 0) {
            throw new ParseException("The Quality value, " + q + ", is greater than 1", index);
        }
        return 1000;
    }
    
    public static int readQualityFactorParameter(final HttpHeaderReader reader) throws ParseException {
        int q = -1;
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            if (!reader.hasNext()) {
                return 1000;
            }
            final String name = reader.nextToken();
            reader.nextSeparator('=');
            final String value = reader.nextTokenOrQuotedString();
            if (q != -1 || !name.equalsIgnoreCase("q")) {
                continue;
            }
            q = readQualityFactor(value);
        }
        return (q == -1) ? 1000 : q;
    }
    
    public static Map<String, String> readParameters(final HttpHeaderReader reader) throws ParseException {
        return readParameters(reader, false);
    }
    
    public static Map<String, String> readParameters(final HttpHeaderReader reader, final boolean fileNameFix) throws ParseException {
        Map<String, String> m = null;
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            while (reader.hasNextSeparator(';', true)) {
                reader.next();
            }
            if (!reader.hasNext()) {
                break;
            }
            final String name = reader.nextToken();
            reader.nextSeparator('=');
            String value;
            if ("filename".equalsIgnoreCase(name) && fileNameFix) {
                value = reader.nextTokenOrQuotedString(true);
                value = value.substring(value.lastIndexOf(92) + 1);
            }
            else {
                value = reader.nextTokenOrQuotedString(false);
            }
            if (m == null) {
                m = new LinkedHashMap<String, String>();
            }
            m.put(name.toLowerCase(), value);
        }
        return m;
    }
    
    public static Map<String, Cookie> readCookies(final String header) {
        return CookiesParser.parseCookies(header);
    }
    
    public static Cookie readCookie(final String header) {
        return CookiesParser.parseCookie(header);
    }
    
    public static NewCookie readNewCookie(final String header) {
        return CookiesParser.parseNewCookie(header);
    }
    
    public static Set<MatchingEntityTag> readMatchingEntityTag(final String header) throws ParseException {
        if (header.equals("*")) {
            return MatchingEntityTag.ANY_MATCH;
        }
        final HttpHeaderReader reader = new HttpHeaderReaderImpl(header);
        final Set<MatchingEntityTag> l = new HashSet<MatchingEntityTag>(1);
        final HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
        while (reader.hasNext()) {
            l.add(HttpHeaderReader.MATCHING_ENTITY_TAG_CREATOR.create(adapter));
            adapter.reset();
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return l;
    }
    
    public static List<MediaType> readMediaTypes(final List<MediaType> l, final String header) throws ParseException {
        return readList(l, HttpHeaderReader.MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<AcceptableMediaType> readAcceptMediaType(final String header) throws ParseException {
        return readAcceptableList(HttpHeaderReader.ACCEPTABLE_MEDIA_TYPE_COMPARATOR, HttpHeaderReader.ACCEPTABLE_MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<QualitySourceMediaType> readQualitySourceMediaType(final String header) throws ParseException {
        return readAcceptableList(MediaTypes.QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR, HttpHeaderReader.QUALITY_SOURCE_MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<QualitySourceMediaType> readQualitySourceMediaType(final String[] header) throws ParseException {
        if (header.length < 2) {
            return readQualitySourceMediaType(header[0]);
        }
        final StringBuilder sb = new StringBuilder();
        for (final String h : header) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(h);
        }
        return readQualitySourceMediaType(sb.toString());
    }
    
    public static List<AcceptableMediaType> readAcceptMediaType(final String header, final List<QualitySourceMediaType> priorityMediaTypes) throws ParseException {
        return readAcceptableList(new Comparator<AcceptableMediaType>() {
            @Override
            public int compare(final AcceptableMediaType o1, final AcceptableMediaType o2) {
                boolean q_o1_set = false;
                int q_o1 = 1000000;
                boolean q_o2_set = false;
                int q_o2 = 1000000;
                for (final QualitySourceMediaType m : priorityMediaTypes) {
                    if (!q_o1_set && MediaTypes.typeEquals(o1, m)) {
                        q_o1 = o1.getQuality() * m.getQualitySource();
                        q_o1_set = true;
                    }
                    else {
                        if (q_o2_set || !MediaTypes.typeEquals(o2, m)) {
                            continue;
                        }
                        q_o2 = o2.getQuality() * m.getQualitySource();
                        q_o2_set = true;
                    }
                }
                int i = q_o2 - q_o1;
                if (i != 0) {
                    return i;
                }
                i = o2.getQuality() - o1.getQuality();
                if (i != 0) {
                    return i;
                }
                return MediaTypes.MEDIA_TYPE_COMPARATOR.compare(o1, o2);
            }
        }, HttpHeaderReader.ACCEPTABLE_MEDIA_TYPE_CREATOR, header);
    }
    
    public static List<AcceptableToken> readAcceptToken(final String header) throws ParseException {
        return readAcceptableList(HttpHeaderReader.ACCEPTABLE_TOKEN_CREATOR, header);
    }
    
    public static List<AcceptableLanguageTag> readAcceptLanguage(final String header) throws ParseException {
        return readAcceptableList(HttpHeaderReader.LANGUAGE_CREATOR, header);
    }
    
    public static <T extends QualityFactor> List<T> readAcceptableList(final ListElementCreator<T> c, final String header) throws ParseException {
        final List<T> l = (List<T>)readList((ListElementCreator<Object>)c, header);
        Collections.sort(l, HttpHeaderReader.QUALITY_COMPARATOR);
        return l;
    }
    
    public static <T> List<T> readAcceptableList(final Comparator<T> comparator, final ListElementCreator<T> c, final String header) throws ParseException {
        final List<T> l = readList(c, header);
        Collections.sort(l, comparator);
        return l;
    }
    
    public static <T> List<T> readList(final ListElementCreator<T> c, final String header) throws ParseException {
        return readList(new ArrayList<T>(), c, header);
    }
    
    public static <T> List<T> readList(final List<T> l, final ListElementCreator<T> c, final String header) throws ParseException {
        final HttpHeaderReader reader = new HttpHeaderReaderImpl(header);
        final HttpHeaderListAdapter adapter = new HttpHeaderListAdapter(reader);
        while (reader.hasNext()) {
            l.add(c.create(adapter));
            adapter.reset();
            if (reader.hasNext()) {
                reader.next();
            }
        }
        return l;
    }
    
    static {
        MATCHING_ENTITY_TAG_CREATOR = new ListElementCreator<MatchingEntityTag>() {
            @Override
            public MatchingEntityTag create(final HttpHeaderReader reader) throws ParseException {
                return MatchingEntityTag.valueOf(reader);
            }
        };
        MEDIA_TYPE_CREATOR = new ListElementCreator<MediaType>() {
            @Override
            public MediaType create(final HttpHeaderReader reader) throws ParseException {
                return MediaTypeProvider.valueOf(reader);
            }
        };
        ACCEPTABLE_MEDIA_TYPE_CREATOR = new ListElementCreator<AcceptableMediaType>() {
            @Override
            public AcceptableMediaType create(final HttpHeaderReader reader) throws ParseException {
                return AcceptableMediaType.valueOf(reader);
            }
        };
        ACCEPTABLE_MEDIA_TYPE_COMPARATOR = new Comparator<AcceptableMediaType>() {
            @Override
            public int compare(final AcceptableMediaType o1, final AcceptableMediaType o2) {
                final int i = o2.getQuality() - o1.getQuality();
                if (i != 0) {
                    return i;
                }
                return MediaTypes.MEDIA_TYPE_COMPARATOR.compare(o1, o2);
            }
        };
        QUALITY_SOURCE_MEDIA_TYPE_CREATOR = new ListElementCreator<QualitySourceMediaType>() {
            @Override
            public QualitySourceMediaType create(final HttpHeaderReader reader) throws ParseException {
                return QualitySourceMediaType.valueOf(reader);
            }
        };
        ACCEPTABLE_TOKEN_CREATOR = new ListElementCreator<AcceptableToken>() {
            @Override
            public AcceptableToken create(final HttpHeaderReader reader) throws ParseException {
                return new AcceptableToken(reader);
            }
        };
        LANGUAGE_CREATOR = new ListElementCreator<AcceptableLanguageTag>() {
            @Override
            public AcceptableLanguageTag create(final HttpHeaderReader reader) throws ParseException {
                return new AcceptableLanguageTag(reader);
            }
        };
        QUALITY_COMPARATOR = new Comparator<QualityFactor>() {
            @Override
            public int compare(final QualityFactor o1, final QualityFactor o2) {
                return o2.getQuality() - o1.getQuality();
            }
        };
    }
    
    public enum Event
    {
        Token, 
        QuotedString, 
        Comment, 
        Separator, 
        Control;
    }
    
    public interface ListElementCreator<T>
    {
        T create(final HttpHeaderReader p0) throws ParseException;
    }
}
