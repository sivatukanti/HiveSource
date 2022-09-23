// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.Arrays;
import java.util.Iterator;
import com.sun.jersey.core.impl.provider.header.WriterUtil;
import java.util.List;
import java.util.Map;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.net.URI;

public class LinkHeader
{
    private URI uri;
    private Set<String> rels;
    private MediaType type;
    private MultivaluedMap<String, String> parameters;
    
    public LinkHeader(final String header) throws ParseException, IllegalArgumentException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public LinkHeader(final HttpHeaderReader reader) throws ParseException, IllegalArgumentException {
        this.uri = URI.create(reader.nextSeparatedString('<', '>'));
        if (reader.hasNext()) {
            this.parseParameters(reader);
        }
    }
    
    protected LinkHeader(final LinkHeaderBuilder builder) {
        this.uri = builder.uri;
        if (builder.rels != null) {
            if (builder.rels.size() == 1) {
                this.rels = builder.rels;
            }
            else {
                this.rels = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(builder.rels));
            }
        }
        this.type = builder.type;
        if (builder.parameters != null) {
            this.parameters = new MultivaluedMapImpl(builder.parameters);
        }
    }
    
    public static LinkHeader valueOf(final String header) throws IllegalArgumentException {
        try {
            return new LinkHeader(HttpHeaderReader.newInstance(header));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('<').append(this.uri.toASCIIString()).append('>');
        if (this.rels != null) {
            sb.append(';').append("rel=");
            if (this.rels.size() == 1) {
                sb.append(this.rels.iterator().next());
            }
            else {
                sb.append('\"');
                boolean first = true;
                for (final String rel : this.rels) {
                    if (!first) {
                        sb.append(' ');
                    }
                    sb.append(rel);
                    first = false;
                }
                sb.append('\"');
            }
        }
        if (this.type != null) {
            sb.append(';').append("type=").append(this.type.getType()).append('/').append(this.type.getSubtype());
        }
        if (this.parameters != null) {
            for (final Map.Entry<String, List<String>> e : this.parameters.entrySet()) {
                final String key = e.getKey();
                final List<String> values = e.getValue();
                if (key.equals("anchor") || key.equals("title")) {
                    sb.append(";").append(key).append("=");
                    WriterUtil.appendQuoted(sb, values.get(0));
                }
                else if (key.equals("hreflang")) {
                    for (final String value : e.getValue()) {
                        sb.append(";").append(e.getKey()).append("=").append(value);
                    }
                }
                else {
                    for (final String value : e.getValue()) {
                        sb.append(";").append(e.getKey()).append("=");
                        WriterUtil.appendQuoted(sb, value);
                    }
                }
            }
        }
        return sb.toString();
    }
    
    public MultivaluedMap<String, String> getParams() {
        this.checkNull();
        return this.parameters;
    }
    
    public URI getUri() {
        return this.uri;
    }
    
    public Set<String> getRel() {
        if (this.rels == null) {
            this.rels = Collections.emptySet();
        }
        return this.rels;
    }
    
    public MediaType getType() {
        return this.type;
    }
    
    public String getOp() {
        if (this.parameters != null) {
            return this.parameters.getFirst("op");
        }
        return null;
    }
    
    private void parseParameters(final HttpHeaderReader reader) throws ParseException {
        while (reader.hasNext()) {
            reader.nextSeparator(';');
            while (reader.hasNextSeparator(';', true)) {
                reader.next();
            }
            if (!reader.hasNext()) {
                break;
            }
            final String name = reader.nextToken().toLowerCase();
            reader.nextSeparator('=');
            if (name.equals("rel")) {
                final String value = reader.nextTokenOrQuotedString();
                if (reader.getEvent() == HttpHeaderReader.Event.Token) {
                    this.rels = Collections.singleton(value);
                }
                else {
                    final String[] values = value.split(" ");
                    this.rels = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList(values)));
                }
            }
            else if (name.equals("hreflang")) {
                this.add(name, reader.nextTokenOrQuotedString());
            }
            else if (name.equals("media")) {
                if (this.containsKey("media")) {
                    continue;
                }
                this.add(name, reader.nextTokenOrQuotedString());
            }
            else if (name.equals("title")) {
                if (this.containsKey("title")) {
                    continue;
                }
                this.add(name, reader.nextQuotedString());
            }
            else if (name.equals("title*")) {
                this.add(name, reader.nextQuotedString());
            }
            else if (name.equals("type")) {
                final String typeName = reader.nextToken();
                reader.nextSeparator('/');
                final String subTypeName = reader.nextToken();
                this.type = new MediaType(typeName, subTypeName);
            }
            else {
                this.add(name, reader.nextTokenOrQuotedString());
            }
        }
    }
    
    private void checkNull() {
        if (this.parameters == null) {
            this.parameters = new MultivaluedMapImpl();
        }
    }
    
    private boolean containsKey(final String key) {
        this.checkNull();
        return this.parameters.containsKey(key);
    }
    
    private void add(final String key, final String value) {
        this.checkNull();
        this.parameters.add(key, value);
    }
    
    public static LinkHeaderBuilder uri(final URI uri) {
        return new LinkHeaderBuilder(uri);
    }
    
    public static class LinkHeaderBuilder<T extends LinkHeaderBuilder, V extends LinkHeader>
    {
        protected URI uri;
        protected Set<String> rels;
        protected MediaType type;
        protected MultivaluedMap<String, String> parameters;
        
        LinkHeaderBuilder(final URI uri) {
            this.uri = uri;
        }
        
        public T rel(String rel) {
            if (rel == null) {
                throw new IllegalArgumentException("rel parameter cannot be null");
            }
            rel = rel.trim();
            if (rel.length() == 0) {
                throw new IllegalArgumentException("rel parameter cannot an empty string or just white space");
            }
            if (this.rels == null) {
                this.rels = Collections.singleton(rel);
            }
            else if (this.rels.size() == 1 && !this.rels.contains(rel)) {
                (this.rels = new HashSet<String>(this.rels)).add(rel);
            }
            else {
                this.rels.add(rel);
            }
            return (T)this;
        }
        
        public T type(final MediaType type) {
            this.type = type;
            return (T)this;
        }
        
        public T op(final String op) {
            this.parameter("op", op);
            return (T)this;
        }
        
        public T parameter(final String key, final String value) {
            if (key.equals("rel")) {
                return this.rel(value);
            }
            if (key.equals("type")) {
                return this.type(MediaType.valueOf(value));
            }
            if (this.parameters == null) {
                this.parameters = new MultivaluedMapImpl();
            }
            this.parameters.add(key, value);
            return (T)this;
        }
        
        public V build() {
            final LinkHeader lh = new LinkHeader(this);
            return (V)lh;
        }
    }
}
