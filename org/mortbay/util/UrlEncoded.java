// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public class UrlEncoded extends MultiMap
{
    public static final String ENCODING;
    
    public UrlEncoded(final UrlEncoded url) {
        super(url);
    }
    
    public UrlEncoded() {
        super(6);
    }
    
    public UrlEncoded(final String s) {
        super(6);
        this.decode(s, UrlEncoded.ENCODING);
    }
    
    public UrlEncoded(final String s, final String charset) {
        super(6);
        this.decode(s, charset);
    }
    
    public void decode(final String query) {
        decodeTo(query, this, UrlEncoded.ENCODING);
    }
    
    public void decode(final String query, final String charset) {
        decodeTo(query, this, charset);
    }
    
    public String encode() {
        return this.encode(UrlEncoded.ENCODING, false);
    }
    
    public String encode(final String charset) {
        return this.encode(charset, false);
    }
    
    public synchronized String encode(final String charset, final boolean equalsForNullValue) {
        return encode(this, charset, equalsForNullValue);
    }
    
    public static String encode(final MultiMap map, String charset, final boolean equalsForNullValue) {
        if (charset == null) {
            charset = UrlEncoded.ENCODING;
        }
        final StringBuffer result = new StringBuffer(128);
        synchronized (result) {
            final Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = iter.next();
                final String key = entry.getKey().toString();
                final Object list = entry.getValue();
                final int s = LazyList.size(list);
                if (s == 0) {
                    result.append(encodeString(key, charset));
                    if (equalsForNullValue) {
                        result.append('=');
                    }
                }
                else {
                    for (int i = 0; i < s; ++i) {
                        if (i > 0) {
                            result.append('&');
                        }
                        final Object val = LazyList.get(list, i);
                        result.append(encodeString(key, charset));
                        if (val != null) {
                            final String str = val.toString();
                            if (str.length() > 0) {
                                result.append('=');
                                result.append(encodeString(str, charset));
                            }
                            else if (equalsForNullValue) {
                                result.append('=');
                            }
                        }
                        else if (equalsForNullValue) {
                            result.append('=');
                        }
                    }
                }
                if (iter.hasNext()) {
                    result.append('&');
                }
            }
            return result.toString();
        }
    }
    
    public static void decodeTo(final String content, final MultiMap map, String charset) {
        if (charset == null) {
            charset = UrlEncoded.ENCODING;
        }
        synchronized (map) {
            String key = null;
            String value = null;
            int mark = -1;
            boolean encoded = false;
            for (int i = 0; i < content.length(); ++i) {
                final char c = content.charAt(i);
                switch (c) {
                    case '&': {
                        final int l = i - mark - 1;
                        value = ((l == 0) ? "" : (encoded ? decodeString(content, mark + 1, l, charset) : content.substring(mark + 1, i)));
                        mark = i;
                        encoded = false;
                        if (key != null) {
                            map.add(key, value);
                        }
                        else if (value != null && value.length() > 0) {
                            map.add(value, "");
                        }
                        key = null;
                        value = null;
                        break;
                    }
                    case '=': {
                        if (key != null) {
                            break;
                        }
                        key = (encoded ? decodeString(content, mark + 1, i - mark - 1, charset) : content.substring(mark + 1, i));
                        mark = i;
                        encoded = false;
                        break;
                    }
                    case '+': {
                        encoded = true;
                        break;
                    }
                    case '%': {
                        encoded = true;
                        break;
                    }
                }
            }
            if (key != null) {
                final int j = content.length() - mark - 1;
                value = ((j == 0) ? "" : (encoded ? decodeString(content, mark + 1, j, charset) : content.substring(mark + 1)));
                map.add(key, value);
            }
            else if (mark < content.length()) {
                key = (encoded ? decodeString(content, mark + 1, content.length() - mark - 1, charset) : content.substring(mark + 1));
                map.add(key, "");
            }
        }
    }
    
    public static void decodeUtf8To(final byte[] raw, final int offset, final int length, final MultiMap map) {
        decodeUtf8To(raw, offset, length, map, new Utf8StringBuffer());
    }
    
    public static void decodeUtf8To(final byte[] raw, final int offset, final int length, final MultiMap map, final Utf8StringBuffer buffer) {
        synchronized (map) {
            String key = null;
            String value = null;
            for (int end = offset + length, i = offset; i < end; ++i) {
                final byte b = raw[i];
                switch ((char)(0xFF & b)) {
                    case '&': {
                        value = ((buffer.length() == 0) ? "" : buffer.toString());
                        buffer.reset();
                        if (key != null) {
                            map.add(key, value);
                        }
                        else if (value != null && value.length() > 0) {
                            map.add(value, "");
                        }
                        key = null;
                        value = null;
                        break;
                    }
                    case '=': {
                        if (key != null) {
                            buffer.append(b);
                            break;
                        }
                        key = buffer.toString();
                        buffer.reset();
                        break;
                    }
                    case '+': {
                        buffer.append((byte)32);
                        break;
                    }
                    case '%': {
                        if (i + 2 < end) {
                            buffer.append((byte)((TypeUtil.convertHexDigit(raw[++i]) << 4) + TypeUtil.convertHexDigit(raw[++i])));
                            break;
                        }
                        break;
                    }
                    default: {
                        buffer.append(b);
                        break;
                    }
                }
            }
            if (key != null) {
                value = ((buffer.length() == 0) ? "" : buffer.toString());
                buffer.reset();
                map.add(key, value);
            }
            else if (buffer.length() > 0) {
                map.add(buffer.toString(), "");
            }
        }
    }
    
    public static void decode88591To(final InputStream in, final MultiMap map, final int maxLength) throws IOException {
        synchronized (map) {
            final StringBuffer buffer = new StringBuffer();
            String key = null;
            String value = null;
            int totalLength = 0;
            int b;
            while ((b = in.read()) >= 0) {
                switch ((char)b) {
                    case '&': {
                        value = ((buffer.length() == 0) ? "" : buffer.toString());
                        buffer.setLength(0);
                        if (key != null) {
                            map.add(key, value);
                        }
                        else if (value != null && value.length() > 0) {
                            map.add(value, "");
                        }
                        key = null;
                        value = null;
                        break;
                    }
                    case '=': {
                        if (key != null) {
                            buffer.append((char)b);
                            break;
                        }
                        key = buffer.toString();
                        buffer.setLength(0);
                        break;
                    }
                    case '+': {
                        buffer.append(' ');
                        break;
                    }
                    case '%': {
                        final int dh = in.read();
                        final int dl = in.read();
                        if (dh < 0) {
                            break;
                        }
                        if (dl < 0) {
                            break;
                        }
                        buffer.append((char)((TypeUtil.convertHexDigit((byte)dh) << 4) + TypeUtil.convertHexDigit((byte)dl)));
                        break;
                    }
                    default: {
                        buffer.append((char)b);
                        break;
                    }
                }
                if (maxLength >= 0 && ++totalLength > maxLength) {
                    throw new IllegalStateException("Form too large");
                }
            }
            if (key != null) {
                value = ((buffer.length() == 0) ? "" : buffer.toString());
                buffer.setLength(0);
                map.add(key, value);
            }
            else if (buffer.length() > 0) {
                map.add(buffer.toString(), "");
            }
        }
    }
    
    public static void decodeUtf8To(final InputStream in, final MultiMap map, final int maxLength) throws IOException {
        synchronized (map) {
            final Utf8StringBuffer buffer = new Utf8StringBuffer();
            String key = null;
            String value = null;
            int totalLength = 0;
            int b;
            while ((b = in.read()) >= 0) {
                switch ((char)b) {
                    case '&': {
                        value = ((buffer.length() == 0) ? "" : buffer.toString());
                        buffer.reset();
                        if (key != null) {
                            map.add(key, value);
                        }
                        else if (value != null && value.length() > 0) {
                            map.add(value, "");
                        }
                        key = null;
                        value = null;
                        break;
                    }
                    case '=': {
                        if (key != null) {
                            buffer.append((byte)b);
                            break;
                        }
                        key = buffer.toString();
                        buffer.reset();
                        break;
                    }
                    case '+': {
                        buffer.append((byte)32);
                        break;
                    }
                    case '%': {
                        final int dh = in.read();
                        final int dl = in.read();
                        if (dh < 0) {
                            break;
                        }
                        if (dl < 0) {
                            break;
                        }
                        buffer.append((byte)((TypeUtil.convertHexDigit((byte)dh) << 4) + TypeUtil.convertHexDigit((byte)dl)));
                        break;
                    }
                    default: {
                        buffer.append((byte)b);
                        break;
                    }
                }
                if (maxLength >= 0 && ++totalLength > maxLength) {
                    throw new IllegalStateException("Form too large");
                }
            }
            if (key != null) {
                value = ((buffer.length() == 0) ? "" : buffer.toString());
                buffer.reset();
                map.add(key, value);
            }
            else if (buffer.length() > 0) {
                map.add(buffer.toString(), "");
            }
        }
    }
    
    public static void decodeUtf16To(final InputStream in, final MultiMap map, int maxLength) throws IOException {
        final InputStreamReader input = new InputStreamReader(in, "UTF-16");
        final StringBuffer buf = new StringBuffer();
        int length = 0;
        if (maxLength < 0) {
            maxLength = Integer.MAX_VALUE;
        }
        int c;
        while ((c = input.read()) > 0 && length++ < maxLength) {
            buf.append((char)c);
        }
        decodeTo(buf.toString(), map, UrlEncoded.ENCODING);
    }
    
    public static void decodeTo(final InputStream in, final MultiMap map, final String charset, final int maxLength) throws IOException {
        if (charset == null || "UTF-8".equalsIgnoreCase(charset)) {
            decodeUtf8To(in, map, maxLength);
            return;
        }
        if (StringUtil.__ISO_8859_1.equals(charset)) {
            decode88591To(in, map, maxLength);
            return;
        }
        if ("UTF-16".equalsIgnoreCase(charset)) {
            decodeUtf16To(in, map, maxLength);
            return;
        }
        synchronized (map) {
            String key = null;
            String value = null;
            int digit = 0;
            int digits = 0;
            int totalLength = 0;
            final ByteArrayOutputStream2 output = new ByteArrayOutputStream2();
            int size = 0;
            int c;
            while ((c = in.read()) > 0) {
                switch ((char)c) {
                    case '&': {
                        size = output.size();
                        value = ((size == 0) ? "" : output.toString(charset));
                        output.setCount(0);
                        if (key != null) {
                            map.add(key, value);
                        }
                        else if (value != null && value.length() > 0) {
                            map.add(value, "");
                        }
                        key = null;
                        value = null;
                        break;
                    }
                    case '=': {
                        if (key != null) {
                            output.write(c);
                            break;
                        }
                        size = output.size();
                        key = ((size == 0) ? "" : output.toString(charset));
                        output.setCount(0);
                        break;
                    }
                    case '+': {
                        output.write(32);
                        break;
                    }
                    case '%': {
                        digits = 2;
                        break;
                    }
                    default: {
                        if (digits == 2) {
                            digit = TypeUtil.convertHexDigit((byte)c);
                            digits = 1;
                            break;
                        }
                        if (digits == 1) {
                            output.write((digit << 4) + TypeUtil.convertHexDigit((byte)c));
                            digits = 0;
                            break;
                        }
                        output.write(c);
                        break;
                    }
                }
                ++totalLength;
                if (maxLength >= 0 && totalLength > maxLength) {
                    throw new IllegalStateException("Form too large");
                }
            }
            size = output.size();
            if (key != null) {
                value = ((size == 0) ? "" : output.toString(charset));
                output.setCount(0);
                map.add(key, value);
            }
            else if (size > 0) {
                map.add(output.toString(charset), "");
            }
        }
    }
    
    public static String decodeString(final String encoded, final int offset, final int length, final String charset) {
        if (charset == null || StringUtil.isUTF8(charset)) {
            Utf8StringBuffer buffer = null;
            for (int i = 0; i < length; ++i) {
                char c = encoded.charAt(offset + i);
                if (c < '\0' || c > '\u00ff') {
                    if (buffer == null) {
                        buffer = new Utf8StringBuffer(length);
                        buffer.getStringBuffer().append(encoded.substring(offset, offset + i + 1));
                    }
                    else {
                        buffer.getStringBuffer().append(c);
                    }
                }
                else if (c == '+') {
                    if (buffer == null) {
                        buffer = new Utf8StringBuffer(length);
                        buffer.getStringBuffer().append(encoded.substring(offset, offset + i));
                    }
                    buffer.getStringBuffer().append(' ');
                }
                else if (c == '%' && i + 2 < length) {
                    if (buffer == null) {
                        buffer = new Utf8StringBuffer(length);
                        buffer.getStringBuffer().append(encoded.substring(offset, offset + i));
                    }
                    while (c == '%' && i + 2 < length) {
                        try {
                            final byte b = (byte)TypeUtil.parseInt(encoded, offset + i + 1, 2, 16);
                            buffer.append(b);
                            i += 3;
                        }
                        catch (NumberFormatException nfe) {
                            buffer.getStringBuffer().append('%');
                            char next;
                            while ((next = encoded.charAt(++i + offset)) != '%') {
                                buffer.getStringBuffer().append((next == '+') ? ' ' : next);
                            }
                        }
                        if (i < length) {
                            c = encoded.charAt(offset + i);
                        }
                    }
                    --i;
                }
                else if (buffer != null) {
                    buffer.getStringBuffer().append(c);
                }
            }
            if (buffer != null) {
                return buffer.toString();
            }
            if (offset == 0 && encoded.length() == length) {
                return encoded;
            }
            return encoded.substring(offset, offset + length);
        }
        else {
            StringBuffer buffer2 = null;
            try {
                for (int i = 0; i < length; ++i) {
                    char c = encoded.charAt(offset + i);
                    if (c < '\0' || c > '\u00ff') {
                        if (buffer2 == null) {
                            buffer2 = new StringBuffer(length);
                            buffer2.append(encoded.substring(offset, offset + i + 1));
                        }
                        else {
                            buffer2.append(c);
                        }
                    }
                    else if (c == '+') {
                        if (buffer2 == null) {
                            buffer2 = new StringBuffer(length);
                            buffer2.append(encoded.substring(offset, offset + i));
                        }
                        buffer2.append(' ');
                    }
                    else if (c == '%' && i + 2 < length) {
                        if (buffer2 == null) {
                            buffer2 = new StringBuffer(length);
                            buffer2.append(encoded.substring(offset, offset + i));
                        }
                        final byte[] ba = new byte[length];
                        int n = 0;
                        while (c >= '\0' && c <= '\u00ff') {
                            if (c == '%') {
                                if (i + 2 < length) {
                                    try {
                                        ba[n++] = (byte)TypeUtil.parseInt(encoded, offset + i + 1, 2, 16);
                                        i += 3;
                                    }
                                    catch (NumberFormatException nfe2) {
                                        ba[n - 1] = 37;
                                        char next2;
                                        while ((next2 = encoded.charAt(++i + offset)) != '%') {
                                            ba[n++] = (byte)((next2 == '+') ? ' ' : next2);
                                        }
                                    }
                                }
                                else {
                                    ba[n++] = 37;
                                    ++i;
                                }
                            }
                            else if (c == '+') {
                                ba[n++] = 32;
                                ++i;
                            }
                            else {
                                ba[n++] = (byte)c;
                                ++i;
                            }
                            if (i >= length) {
                                break;
                            }
                            c = encoded.charAt(offset + i);
                        }
                        --i;
                        buffer2.append(new String(ba, 0, n, charset));
                    }
                    else if (buffer2 != null) {
                        buffer2.append(c);
                    }
                }
                if (buffer2 != null) {
                    return buffer2.toString();
                }
                if (offset == 0 && encoded.length() == length) {
                    return encoded;
                }
                return encoded.substring(offset, offset + length);
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static String encodeString(final String string) {
        return encodeString(string, UrlEncoded.ENCODING);
    }
    
    public static String encodeString(final String string, String charset) {
        if (charset == null) {
            charset = UrlEncoded.ENCODING;
        }
        byte[] bytes = null;
        try {
            bytes = string.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            bytes = string.getBytes();
        }
        final int len = bytes.length;
        final byte[] encoded = new byte[bytes.length * 3];
        int n = 0;
        boolean noEncode = true;
        for (final byte b : bytes) {
            if (b == 32) {
                noEncode = false;
                encoded[n++] = 43;
            }
            else if ((b >= 97 && b <= 122) || (b >= 65 && b <= 90) || (b >= 48 && b <= 57)) {
                encoded[n++] = b;
            }
            else {
                noEncode = false;
                encoded[n++] = 37;
                byte nibble = (byte)((b & 0xF0) >> 4);
                if (nibble >= 10) {
                    encoded[n++] = (byte)(65 + nibble - 10);
                }
                else {
                    encoded[n++] = (byte)(48 + nibble);
                }
                nibble = (byte)(b & 0xF);
                if (nibble >= 10) {
                    encoded[n++] = (byte)(65 + nibble - 10);
                }
                else {
                    encoded[n++] = (byte)(48 + nibble);
                }
            }
        }
        if (noEncode) {
            return string;
        }
        try {
            return new String(encoded, 0, n, charset);
        }
        catch (UnsupportedEncodingException e2) {
            return new String(encoded, 0, n);
        }
    }
    
    public Object clone() {
        return new UrlEncoded(this);
    }
    
    static {
        ENCODING = System.getProperty("org.mortbay.util.UrlEncoding.charset", "UTF-8");
    }
}
