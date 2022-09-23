// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.UnsupportedEncodingException;

public class URIUtil implements Cloneable
{
    public static final String SLASH = "/";
    public static final String HTTP = "http";
    public static final String HTTP_COLON = "http:";
    public static final String HTTPS = "https";
    public static final String HTTPS_COLON = "https:";
    public static final String __CHARSET;
    
    private URIUtil() {
    }
    
    public static String encodePath(final String path) {
        if (path == null || path.length() == 0) {
            return path;
        }
        final StringBuffer buf = encodePath(null, path);
        return (buf == null) ? path : buf.toString();
    }
    
    public static StringBuffer encodePath(StringBuffer buf, final String path) {
        if (buf == null) {
            int i = 0;
        Label_0187:
            while (i < path.length()) {
                final char c = path.charAt(i);
                switch (c) {
                    case ' ':
                    case '\"':
                    case '#':
                    case '%':
                    case '\'':
                    case ';':
                    case '<':
                    case '>':
                    case '?': {
                        buf = new StringBuffer(path.length() << 1);
                        break Label_0187;
                    }
                    default: {
                        ++i;
                        continue;
                    }
                }
            }
            if (buf == null) {
                return null;
            }
        }
        synchronized (buf) {
            for (int j = 0; j < path.length(); ++j) {
                final char c2 = path.charAt(j);
                switch (c2) {
                    case '%': {
                        buf.append("%25");
                        break;
                    }
                    case '?': {
                        buf.append("%3F");
                        break;
                    }
                    case ';': {
                        buf.append("%3B");
                        break;
                    }
                    case '#': {
                        buf.append("%23");
                        break;
                    }
                    case '\"': {
                        buf.append("%22");
                        break;
                    }
                    case '\'': {
                        buf.append("%27");
                        break;
                    }
                    case '<': {
                        buf.append("%3C");
                        break;
                    }
                    case '>': {
                        buf.append("%3E");
                        break;
                    }
                    case ' ': {
                        buf.append("%20");
                        break;
                    }
                    default: {
                        buf.append(c2);
                        break;
                    }
                }
            }
        }
        return buf;
    }
    
    public static StringBuffer encodeString(StringBuffer buf, final String path, final String encode) {
        if (buf == null) {
            for (int i = 0; i < path.length(); ++i) {
                final char c = path.charAt(i);
                if (c == '%' || encode.indexOf(c) >= 0) {
                    buf = new StringBuffer(path.length() << 1);
                    break;
                }
            }
            if (buf == null) {
                return null;
            }
        }
        synchronized (buf) {
            for (int j = 0; j < path.length(); ++j) {
                final char c2 = path.charAt(j);
                if (c2 == '%' || encode.indexOf(c2) >= 0) {
                    buf.append('%');
                    StringUtil.append(buf, (byte)('\u00ff' & c2), 16);
                }
                else {
                    buf.append(c2);
                }
            }
        }
        return buf;
    }
    
    public static String decodePath(final String path) {
        if (path == null) {
            return null;
        }
        char[] chars = null;
        int n = 0;
        byte[] bytes = null;
        int b = 0;
        for (int len = path.length(), i = 0; i < len; ++i) {
            final char c = path.charAt(i);
            if (c == '%' && i + 2 < len) {
                if (chars == null) {
                    chars = new char[len];
                    bytes = new byte[len];
                    path.getChars(0, i, chars, 0);
                }
                bytes[b++] = (byte)(0xFF & TypeUtil.parseInt(path, i + 1, 2, 16));
                i += 2;
            }
            else if (bytes == null) {
                ++n;
            }
            else {
                if (b > 0) {
                    String s;
                    try {
                        s = new String(bytes, 0, b, URIUtil.__CHARSET);
                    }
                    catch (UnsupportedEncodingException e) {
                        s = new String(bytes, 0, b);
                    }
                    s.getChars(0, s.length(), chars, n);
                    n += s.length();
                    b = 0;
                }
                chars[n++] = c;
            }
        }
        if (chars == null) {
            return path;
        }
        if (b > 0) {
            String s2;
            try {
                s2 = new String(bytes, 0, b, URIUtil.__CHARSET);
            }
            catch (UnsupportedEncodingException e2) {
                s2 = new String(bytes, 0, b);
            }
            s2.getChars(0, s2.length(), chars, n);
            n += s2.length();
        }
        return new String(chars, 0, n);
    }
    
    public static String decodePath(final byte[] buf, final int offset, final int length) {
        byte[] bytes = null;
        int n = 0;
        for (int i = 0; i < length; ++i) {
            byte b = buf[i + offset];
            if (b == 37 && i + 2 < length) {
                b = (byte)(0xFF & TypeUtil.parseInt(buf, i + offset + 1, 2, 16));
                i += 2;
            }
            else if (bytes == null) {
                ++n;
                continue;
            }
            if (bytes == null) {
                bytes = new byte[length];
                for (int j = 0; j < n; ++j) {
                    bytes[j] = buf[j + offset];
                }
            }
            bytes[n++] = b;
        }
        if (bytes == null) {
            return StringUtil.toString(buf, offset, length, URIUtil.__CHARSET);
        }
        return StringUtil.toString(bytes, 0, n, URIUtil.__CHARSET);
    }
    
    public static String addPaths(final String p1, final String p2) {
        if (p1 == null || p1.length() == 0) {
            if (p1 != null && p2 == null) {
                return p1;
            }
            return p2;
        }
        else {
            if (p2 == null || p2.length() == 0) {
                return p1;
            }
            int split = p1.indexOf(59);
            if (split < 0) {
                split = p1.indexOf(63);
            }
            if (split == 0) {
                return p2 + p1;
            }
            if (split < 0) {
                split = p1.length();
            }
            final StringBuffer buf = new StringBuffer(p1.length() + p2.length() + 2);
            buf.append(p1);
            if (buf.charAt(split - 1) == '/') {
                if (p2.startsWith("/")) {
                    buf.deleteCharAt(split - 1);
                    buf.insert(split - 1, p2);
                }
                else {
                    buf.insert(split, p2);
                }
            }
            else if (p2.startsWith("/")) {
                buf.insert(split, p2);
            }
            else {
                buf.insert(split, '/');
                buf.insert(split + 1, p2);
            }
            return buf.toString();
        }
    }
    
    public static String parentPath(final String p) {
        if (p == null || "/".equals(p)) {
            return null;
        }
        final int slash = p.lastIndexOf(47, p.length() - 2);
        if (slash >= 0) {
            return p.substring(0, slash + 1);
        }
        return null;
    }
    
    public static String stripPath(final String path) {
        if (path == null) {
            return null;
        }
        final int semi = path.indexOf(59);
        if (semi < 0) {
            return path;
        }
        return path.substring(0, semi);
    }
    
    public static String canonicalPath(final String path) {
        if (path == null || path.length() == 0) {
            return path;
        }
        int end = 0;
        int start = 0;
    Label_0114:
        for (end = path.length(), start = path.lastIndexOf(47, end); end > 0; end = start, start = path.lastIndexOf(47, end - 1)) {
            switch (end - start) {
                case 2: {
                    if (path.charAt(start + 1) != '.') {
                        break;
                    }
                    break Label_0114;
                }
                case 3: {
                    if (path.charAt(start + 1) != '.' || path.charAt(start + 2) != '.') {
                        break;
                    }
                    break Label_0114;
                }
            }
        }
        if (start >= end) {
            return path;
        }
        final StringBuffer buf = new StringBuffer(path);
        int delStart = -1;
        int delEnd = -1;
        int skip = 0;
        while (end > 0) {
            switch (end - start) {
                case 2: {
                    if (buf.charAt(start + 1) != '.') {
                        if (skip <= 0 || --skip != 0) {
                            break;
                        }
                        delStart = ((start >= 0) ? start : 0);
                        if (delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                            ++delStart;
                            break;
                        }
                        break;
                    }
                    else {
                        if (start < 0 && buf.length() > 2 && buf.charAt(1) == '/' && buf.charAt(2) == '/') {
                            break;
                        }
                        if (delEnd < 0) {
                            delEnd = end;
                        }
                        delStart = start;
                        if (delStart >= 0 && (delStart != 0 || buf.charAt(delStart) != '/')) {
                            if (end == buf.length()) {
                                ++delStart;
                            }
                            for (end = start--; start >= 0 && buf.charAt(start) != '/'; --start) {}
                            continue;
                        }
                        ++delStart;
                        if (delEnd < buf.length() && buf.charAt(delEnd) == '/') {
                            ++delEnd;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 3: {
                    if (buf.charAt(start + 1) == '.' && buf.charAt(start + 2) == '.') {
                        delStart = start;
                        if (delEnd < 0) {
                            delEnd = end;
                        }
                        ++skip;
                        for (end = start--; start >= 0 && buf.charAt(start) != '/'; --start) {}
                        continue;
                    }
                    if (skip <= 0 || --skip != 0) {
                        break;
                    }
                    delStart = ((start >= 0) ? start : 0);
                    if (delStart > 0 && delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                        ++delStart;
                        break;
                    }
                    break;
                }
                default: {
                    if (skip <= 0 || --skip != 0) {
                        break;
                    }
                    delStart = ((start >= 0) ? start : 0);
                    if (delEnd == buf.length() && buf.charAt(delEnd - 1) == '.') {
                        ++delStart;
                        break;
                    }
                    break;
                }
            }
            if (skip <= 0 && delStart >= 0 && delEnd >= delStart) {
                buf.delete(delStart, delEnd);
                delEnd = (delStart = -1);
                if (skip > 0) {
                    delEnd = end;
                }
            }
            for (end = start--; start >= 0 && buf.charAt(start) != '/'; --start) {}
        }
        if (skip > 0) {
            return null;
        }
        if (delEnd >= 0) {
            buf.delete(delStart, delEnd);
        }
        return buf.toString();
    }
    
    public static String compactPath(final String path) {
        if (path == null || path.length() == 0) {
            return path;
        }
        int state = 0;
        int end = 0;
        int i = 0;
    Label_0085:
        for (end = path.length(), i = 0; i < end; ++i) {
            final char c = path.charAt(i);
            switch (c) {
                case '?': {
                    return path;
                }
                case '/': {
                    if (++state == 2) {
                        break Label_0085;
                    }
                    break;
                }
                default: {
                    state = 0;
                    break;
                }
            }
        }
        if (state < 2) {
            return path;
        }
        final StringBuffer buf = new StringBuffer(path.length());
        final char[] chars = path.toCharArray();
        buf.append(chars, 0, i);
    Label_0209:
        while (i < end) {
            final char c2 = path.charAt(i);
            switch (c2) {
                case '?': {
                    buf.append(chars, i, end - i);
                    break Label_0209;
                }
                case '/': {
                    if (state++ == 0) {
                        buf.append(c2);
                        break;
                    }
                    break;
                }
                default: {
                    state = 0;
                    buf.append(c2);
                    break;
                }
            }
            ++i;
        }
        return buf.toString();
    }
    
    public static boolean hasScheme(final String uri) {
        for (int i = 0; i < uri.length(); ++i) {
            final char c = uri.charAt(i);
            if (c == ':') {
                return true;
            }
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                if (i <= 0) {
                    break;
                }
                if ((c < '0' || c > '9') && c != '.' && c != '+' && c != '-') {
                    break;
                }
            }
        }
        return false;
    }
    
    static {
        __CHARSET = System.getProperty("org.mortbay.util.URI.charset", "UTF-8");
    }
}
