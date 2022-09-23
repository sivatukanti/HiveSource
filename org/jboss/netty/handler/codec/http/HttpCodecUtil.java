// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http;

import java.util.Iterator;
import java.util.List;

final class HttpCodecUtil
{
    static void validateHeaderName(final String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        int i = 0;
        while (i < name.length()) {
            final char c = name.charAt(i);
            if (c > '\u007f') {
                throw new IllegalArgumentException("name contains non-ascii character: " + name);
            }
            switch (c) {
                case '\t':
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ' ':
                case ',':
                case ':':
                case ';':
                case '=': {
                    throw new IllegalArgumentException("name contains one of the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " + name);
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
    }
    
    static void validateHeaderValue(final String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        int state = 0;
        int i = 0;
        while (i < value.length()) {
            final char c = value.charAt(i);
            switch (c) {
                case '\u000b': {
                    throw new IllegalArgumentException("value contains a prohibited character '\\v': " + value);
                }
                case '\f': {
                    throw new IllegalArgumentException("value contains a prohibited character '\\f': " + value);
                }
                default: {
                    Label_0288: {
                        switch (state) {
                            case 0: {
                                switch (c) {
                                    case '\r': {
                                        state = 1;
                                        break;
                                    }
                                    case '\n': {
                                        state = 2;
                                        break;
                                    }
                                }
                                break;
                            }
                            case 1: {
                                switch (c) {
                                    case '\n': {
                                        state = 2;
                                        break Label_0288;
                                    }
                                    default: {
                                        throw new IllegalArgumentException("Only '\\n' is allowed after '\\r': " + value);
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (c) {
                                    case '\t':
                                    case ' ': {
                                        state = 0;
                                        break Label_0288;
                                    }
                                    default: {
                                        throw new IllegalArgumentException("Only ' ' and '\\t' are allowed after '\\n': " + value);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    ++i;
                    continue;
                }
            }
        }
        if (state != 0) {
            throw new IllegalArgumentException("value must not end with '\\r' or '\\n':" + value);
        }
    }
    
    static boolean isTransferEncodingChunked(final HttpMessage m) {
        final List<String> chunked = m.headers().getAll("Transfer-Encoding");
        if (chunked.isEmpty()) {
            return false;
        }
        for (final String v : chunked) {
            if (v.equalsIgnoreCase("chunked")) {
                return true;
            }
        }
        return false;
    }
    
    static void removeTransferEncodingChunked(final HttpMessage m) {
        final List<String> values = m.headers().getAll("Transfer-Encoding");
        if (values.isEmpty()) {
            return;
        }
        final Iterator<String> valuesIt = values.iterator();
        while (valuesIt.hasNext()) {
            final String value = valuesIt.next();
            if (value.equalsIgnoreCase("chunked")) {
                valuesIt.remove();
            }
        }
        if (values.isEmpty()) {
            m.headers().remove("Transfer-Encoding");
        }
        else {
            m.headers().set("Transfer-Encoding", values);
        }
    }
    
    static boolean isContentLengthSet(final HttpMessage m) {
        final List<String> contentLength = m.headers().getAll("Content-Length");
        return !contentLength.isEmpty();
    }
    
    private HttpCodecUtil() {
    }
}
