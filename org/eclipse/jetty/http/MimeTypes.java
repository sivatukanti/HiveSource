// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Locale;
import org.eclipse.jetty.util.BufferUtil;
import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.ArrayTrie;
import org.eclipse.jetty.util.log.Log;
import java.util.Properties;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import org.eclipse.jetty.util.StringUtil;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Trie;
import org.eclipse.jetty.util.log.Logger;

public class MimeTypes
{
    private static final Logger LOG;
    private static final Trie<ByteBuffer> TYPES;
    private static final Map<String, String> __dftMimeMap;
    private static final Map<String, String> __inferredEncodings;
    private static final Map<String, String> __assumedEncodings;
    public static final Trie<Type> CACHE;
    private final Map<String, String> _mimeMap;
    
    public MimeTypes() {
        this._mimeMap = new HashMap<String, String>();
    }
    
    public synchronized Map<String, String> getMimeMap() {
        return this._mimeMap;
    }
    
    public void setMimeMap(final Map<String, String> mimeMap) {
        this._mimeMap.clear();
        if (mimeMap != null) {
            for (final Map.Entry<String, String> ext : mimeMap.entrySet()) {
                this._mimeMap.put(StringUtil.asciiToLowerCase(ext.getKey()), normalizeMimeType(ext.getValue()));
            }
        }
    }
    
    public static String getDefaultMimeByExtension(final String filename) {
        String type = null;
        if (filename != null) {
            int i = -1;
            while (type == null) {
                i = filename.indexOf(".", i + 1);
                if (i < 0) {
                    break;
                }
                if (i >= filename.length()) {
                    break;
                }
                final String ext = StringUtil.asciiToLowerCase(filename.substring(i + 1));
                if (type != null) {
                    continue;
                }
                type = MimeTypes.__dftMimeMap.get(ext);
            }
        }
        if (type == null && type == null) {
            type = MimeTypes.__dftMimeMap.get("*");
        }
        return type;
    }
    
    public String getMimeByExtension(final String filename) {
        String type = null;
        if (filename != null) {
            int i = -1;
            while (type == null) {
                i = filename.indexOf(".", i + 1);
                if (i < 0) {
                    break;
                }
                if (i >= filename.length()) {
                    break;
                }
                final String ext = StringUtil.asciiToLowerCase(filename.substring(i + 1));
                if (this._mimeMap != null) {
                    type = this._mimeMap.get(ext);
                }
                if (type != null) {
                    continue;
                }
                type = MimeTypes.__dftMimeMap.get(ext);
            }
        }
        if (type == null) {
            if (this._mimeMap != null) {
                type = this._mimeMap.get("*");
            }
            if (type == null) {
                type = MimeTypes.__dftMimeMap.get("*");
            }
        }
        return type;
    }
    
    public void addMimeMapping(final String extension, final String type) {
        this._mimeMap.put(StringUtil.asciiToLowerCase(extension), normalizeMimeType(type));
    }
    
    public static Set<String> getKnownMimeTypes() {
        return new HashSet<String>(MimeTypes.__dftMimeMap.values());
    }
    
    private static String normalizeMimeType(final String type) {
        final Type t = MimeTypes.CACHE.get(type);
        if (t != null) {
            return t.asString();
        }
        return StringUtil.asciiToLowerCase(type);
    }
    
    public static String getCharsetFromContentType(final String value) {
        if (value == null) {
            return null;
        }
        final int end = value.length();
        int state = 0;
        int start = 0;
        boolean quote = false;
        int i;
        for (i = 0; i < end; ++i) {
            final char b = value.charAt(i);
            if (quote && state != 10) {
                if ('\"' == b) {
                    quote = false;
                }
            }
            else if (';' == b && state <= 8) {
                state = 1;
            }
            else {
                switch (state) {
                    case 0: {
                        if ('\"' == b) {
                            quote = true;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        if ('c' == b) {
                            state = 2;
                            break;
                        }
                        if (' ' != b) {
                            state = 0;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if ('h' == b) {
                            state = 3;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 3: {
                        if ('a' == b) {
                            state = 4;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 4: {
                        if ('r' == b) {
                            state = 5;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 5: {
                        if ('s' == b) {
                            state = 6;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 6: {
                        if ('e' == b) {
                            state = 7;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 7: {
                        if ('t' == b) {
                            state = 8;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 8: {
                        if ('=' == b) {
                            state = 9;
                            break;
                        }
                        if (' ' != b) {
                            state = 0;
                            break;
                        }
                        break;
                    }
                    case 9: {
                        if (' ' == b) {
                            break;
                        }
                        if ('\"' == b) {
                            quote = true;
                            start = i + 1;
                            state = 10;
                            break;
                        }
                        start = i;
                        state = 10;
                        break;
                    }
                    case 10: {
                        if ((!quote && (';' == b || ' ' == b)) || (quote && '\"' == b)) {
                            return StringUtil.normalizeCharset(value, start, i - start);
                        }
                        break;
                    }
                }
            }
        }
        if (state == 10) {
            return StringUtil.normalizeCharset(value, start, i - start);
        }
        return null;
    }
    
    public static Map<String, String> getInferredEncodings() {
        return MimeTypes.__inferredEncodings;
    }
    
    public static Map<String, String> getAssumedEncodings() {
        return MimeTypes.__inferredEncodings;
    }
    
    @Deprecated
    public static String inferCharsetFromContentType(final String contentType) {
        return getCharsetAssumedFromContentType(contentType);
    }
    
    public static String getCharsetInferredFromContentType(final String contentType) {
        return MimeTypes.__inferredEncodings.get(contentType);
    }
    
    public static String getCharsetAssumedFromContentType(final String contentType) {
        return MimeTypes.__assumedEncodings.get(contentType);
    }
    
    public static String getContentTypeWithoutCharset(final String value) {
        final int end = value.length();
        int state = 0;
        int start = 0;
        boolean quote = false;
        int i = 0;
        StringBuilder builder = null;
        while (i < end) {
            final char b = value.charAt(i);
            if ('\"' == b) {
                quote = !quote;
                switch (state) {
                    case 11: {
                        builder.append(b);
                        break;
                    }
                    case 10: {
                        break;
                    }
                    case 9: {
                        builder = new StringBuilder();
                        builder.append(value, 0, start + 1);
                        state = 10;
                        break;
                    }
                    default: {
                        start = i;
                        state = 0;
                        break;
                    }
                }
            }
            else if (quote) {
                if (builder != null && state != 10) {
                    builder.append(b);
                }
            }
            else {
                switch (state) {
                    case 0: {
                        if (';' == b) {
                            state = 1;
                            break;
                        }
                        if (' ' != b) {
                            start = i;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        if ('c' == b) {
                            state = 2;
                            break;
                        }
                        if (' ' != b) {
                            state = 0;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if ('h' == b) {
                            state = 3;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 3: {
                        if ('a' == b) {
                            state = 4;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 4: {
                        if ('r' == b) {
                            state = 5;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 5: {
                        if ('s' == b) {
                            state = 6;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 6: {
                        if ('e' == b) {
                            state = 7;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 7: {
                        if ('t' == b) {
                            state = 8;
                            break;
                        }
                        state = 0;
                        break;
                    }
                    case 8: {
                        if ('=' == b) {
                            state = 9;
                            break;
                        }
                        if (' ' != b) {
                            state = 0;
                            break;
                        }
                        break;
                    }
                    case 9: {
                        if (' ' == b) {
                            break;
                        }
                        builder = new StringBuilder();
                        builder.append(value, 0, start + 1);
                        state = 10;
                        break;
                    }
                    case 10: {
                        if (';' == b) {
                            builder.append(b);
                            state = 11;
                            break;
                        }
                        break;
                    }
                    case 11: {
                        if (' ' != b) {
                            builder.append(b);
                            break;
                        }
                        break;
                    }
                }
            }
            ++i;
        }
        if (builder == null) {
            return value;
        }
        return builder.toString();
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(MimeTypes.class);
        TYPES = new ArrayTrie<ByteBuffer>(512);
        __dftMimeMap = new HashMap<String, String>();
        __inferredEncodings = new HashMap<String, String>();
        __assumedEncodings = new HashMap<String, String>();
        CACHE = new ArrayTrie<Type>(512);
        for (final Type type : Type.values()) {
            MimeTypes.CACHE.put(type.toString(), type);
            MimeTypes.TYPES.put(type.toString(), type.asBuffer());
            final int charset = type.toString().indexOf(";charset=");
            if (charset > 0) {
                final String alt = type.toString().replace(";charset=", "; charset=");
                MimeTypes.CACHE.put(alt, type);
                MimeTypes.TYPES.put(alt, type.asBuffer());
            }
            if (type.isCharsetAssumed()) {
                MimeTypes.__assumedEncodings.put(type.asString(), type.getCharsetString());
            }
        }
        String resourceName = "org/eclipse/jetty/http/mime.properties";
        try {
            final InputStream stream = MimeTypes.class.getClassLoader().getResourceAsStream(resourceName);
            Throwable x2 = null;
            try {
                if (stream == null) {
                    MimeTypes.LOG.warn("Missing mime-type resource: {}", resourceName);
                }
                else {
                    try {
                        final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                        Throwable x3 = null;
                        try {
                            final Properties props = new Properties();
                            props.load(reader);
                            final String s;
                            props.stringPropertyNames().stream().filter(x -> x != null).forEach(x -> s = MimeTypes.__dftMimeMap.put(StringUtil.asciiToLowerCase(x), normalizeMimeType(props.getProperty(x))));
                            if (MimeTypes.__dftMimeMap.size() == 0) {
                                MimeTypes.LOG.warn("Empty mime types at {}", resourceName);
                            }
                            else if (MimeTypes.__dftMimeMap.size() < props.keySet().size()) {
                                MimeTypes.LOG.warn("Duplicate or null mime-type extension in resource: {}", resourceName);
                            }
                        }
                        catch (Throwable t2) {
                            x3 = t2;
                            throw t2;
                        }
                        finally {
                            $closeResource(x3, reader);
                        }
                    }
                    catch (IOException e) {
                        MimeTypes.LOG.warn(e.toString(), new Object[0]);
                        MimeTypes.LOG.debug(e);
                    }
                }
            }
            catch (Throwable t3) {
                x2 = t3;
                throw t3;
            }
            finally {
                if (stream != null) {
                    $closeResource(x2, stream);
                }
            }
        }
        catch (IOException e2) {
            MimeTypes.LOG.warn(e2.toString(), new Object[0]);
            MimeTypes.LOG.debug(e2);
        }
        resourceName = "org/eclipse/jetty/http/encoding.properties";
        try {
            final InputStream stream = MimeTypes.class.getClassLoader().getResourceAsStream(resourceName);
            Throwable x4 = null;
            try {
                if (stream == null) {
                    MimeTypes.LOG.warn("Missing encoding resource: {}", resourceName);
                }
                else {
                    try {
                        final InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                        Throwable x5 = null;
                        try {
                            final Properties props = new Properties();
                            props.load(reader);
                            final Properties properties;
                            final String charset2;
                            props.stringPropertyNames().stream().filter(t -> t != null).forEach(t -> {
                                charset2 = properties.getProperty(t);
                                if (charset2.startsWith("-")) {
                                    MimeTypes.__assumedEncodings.put(t, charset2.substring(1));
                                }
                                else {
                                    MimeTypes.__inferredEncodings.put(t, properties.getProperty(t));
                                }
                                return;
                            });
                            if (MimeTypes.__inferredEncodings.size() == 0) {
                                MimeTypes.LOG.warn("Empty encodings at {}", resourceName);
                            }
                            else if (MimeTypes.__inferredEncodings.size() + MimeTypes.__assumedEncodings.size() < props.keySet().size()) {
                                MimeTypes.LOG.warn("Null or duplicate encodings in resource: {}", resourceName);
                            }
                        }
                        catch (Throwable t4) {
                            x5 = t4;
                            throw t4;
                        }
                        finally {
                            $closeResource(x5, reader);
                        }
                    }
                    catch (IOException e) {
                        MimeTypes.LOG.warn(e.toString(), new Object[0]);
                        MimeTypes.LOG.debug(e);
                    }
                }
            }
            catch (Throwable t5) {
                x4 = t5;
                throw t5;
            }
            finally {
                if (stream != null) {
                    $closeResource(x4, stream);
                }
            }
        }
        catch (IOException e2) {
            MimeTypes.LOG.warn(e2.toString(), new Object[0]);
            MimeTypes.LOG.debug(e2);
        }
    }
    
    public enum Type
    {
        FORM_ENCODED("application/x-www-form-urlencoded"), 
        MESSAGE_HTTP("message/http"), 
        MULTIPART_BYTERANGES("multipart/byteranges"), 
        TEXT_HTML("text/html"), 
        TEXT_PLAIN("text/plain"), 
        TEXT_XML("text/xml"), 
        TEXT_JSON("text/json", StandardCharsets.UTF_8), 
        APPLICATION_JSON("application/json", StandardCharsets.UTF_8), 
        TEXT_HTML_8859_1("text/html;charset=iso-8859-1", Type.TEXT_HTML), 
        TEXT_HTML_UTF_8("text/html;charset=utf-8", Type.TEXT_HTML), 
        TEXT_PLAIN_8859_1("text/plain;charset=iso-8859-1", Type.TEXT_PLAIN), 
        TEXT_PLAIN_UTF_8("text/plain;charset=utf-8", Type.TEXT_PLAIN), 
        TEXT_XML_8859_1("text/xml;charset=iso-8859-1", Type.TEXT_XML), 
        TEXT_XML_UTF_8("text/xml;charset=utf-8", Type.TEXT_XML), 
        TEXT_JSON_8859_1("text/json;charset=iso-8859-1", Type.TEXT_JSON), 
        TEXT_JSON_UTF_8("text/json;charset=utf-8", Type.TEXT_JSON), 
        APPLICATION_JSON_8859_1("application/json;charset=iso-8859-1", Type.APPLICATION_JSON), 
        APPLICATION_JSON_UTF_8("application/json;charset=utf-8", Type.APPLICATION_JSON);
        
        private final String _string;
        private final Type _base;
        private final ByteBuffer _buffer;
        private final Charset _charset;
        private final String _charsetString;
        private final boolean _assumedCharset;
        private final HttpField _field;
        
        private Type(final String s) {
            this._string = s;
            this._buffer = BufferUtil.toBuffer(s);
            this._base = this;
            this._charset = null;
            this._charsetString = null;
            this._assumedCharset = false;
            this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
        }
        
        private Type(final String s, final Type base) {
            this._string = s;
            this._buffer = BufferUtil.toBuffer(s);
            this._base = base;
            final int i = s.indexOf(";charset=");
            this._charset = Charset.forName(s.substring(i + 9));
            this._charsetString = this._charset.toString().toLowerCase(Locale.ENGLISH);
            this._assumedCharset = false;
            this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
        }
        
        private Type(final String s, final Charset cs) {
            this._string = s;
            this._base = this;
            this._buffer = BufferUtil.toBuffer(s);
            this._charset = cs;
            this._charsetString = ((this._charset == null) ? null : this._charset.toString().toLowerCase(Locale.ENGLISH));
            this._assumedCharset = true;
            this._field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, this._string);
        }
        
        public ByteBuffer asBuffer() {
            return this._buffer.asReadOnlyBuffer();
        }
        
        public Charset getCharset() {
            return this._charset;
        }
        
        public String getCharsetString() {
            return this._charsetString;
        }
        
        public boolean is(final String s) {
            return this._string.equalsIgnoreCase(s);
        }
        
        public String asString() {
            return this._string;
        }
        
        @Override
        public String toString() {
            return this._string;
        }
        
        public boolean isCharsetAssumed() {
            return this._assumedCharset;
        }
        
        public HttpField getContentTypeField() {
            return this._field;
        }
        
        public Type getBaseType() {
            return this._base;
        }
    }
}
