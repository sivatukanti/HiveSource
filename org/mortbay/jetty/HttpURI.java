// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.UnsupportedEncodingException;
import org.mortbay.util.UrlEncoded;
import org.mortbay.util.MultiMap;
import org.mortbay.util.TypeUtil;
import org.mortbay.util.StringUtil;
import org.mortbay.util.URIUtil;
import org.mortbay.util.Utf8StringBuffer;

public class HttpURI
{
    private static byte[] __empty;
    private static final int START = 0;
    private static final int AUTH_OR_PATH = 1;
    private static final int SCHEME_OR_PATH = 2;
    private static final int AUTH = 4;
    private static final int IPV6 = 5;
    private static final int PORT = 6;
    private static final int PATH = 7;
    private static final int PARAM = 8;
    private static final int QUERY = 9;
    private static final int ASTERISK = 10;
    boolean _partial;
    byte[] _raw;
    String _rawString;
    int _scheme;
    int _authority;
    int _host;
    int _port;
    int _path;
    int _param;
    int _query;
    int _fragment;
    int _end;
    Utf8StringBuffer _utf8b;
    
    public HttpURI() {
        this._partial = false;
        this._raw = HttpURI.__empty;
        this._utf8b = new Utf8StringBuffer(64);
    }
    
    public HttpURI(final boolean parsePartialAuth) {
        this._partial = false;
        this._raw = HttpURI.__empty;
        this._utf8b = new Utf8StringBuffer(64);
        this._partial = parsePartialAuth;
    }
    
    public HttpURI(final String raw) {
        this._partial = false;
        this._raw = HttpURI.__empty;
        this._utf8b = new Utf8StringBuffer(64);
        this._rawString = raw;
        final byte[] b = raw.getBytes();
        this.parse(b, 0, b.length);
    }
    
    public HttpURI(final byte[] raw, final int offset, final int length) {
        this._partial = false;
        this._raw = HttpURI.__empty;
        this._utf8b = new Utf8StringBuffer(64);
        this.parse2(raw, offset, length);
    }
    
    public void parse(final String raw) {
        final byte[] b = raw.getBytes();
        this.parse2(b, 0, b.length);
        this._rawString = raw;
    }
    
    public void parse(final byte[] raw, final int offset, final int length) {
        this._rawString = null;
        this.parse2(raw, offset, length);
    }
    
    private void parse2(final byte[] raw, final int offset, final int length) {
        this._raw = raw;
        int i = offset;
        final int e = offset + length;
        int state = 0;
        int m = offset;
        this._end = offset + length;
        this._scheme = offset;
        this._authority = offset;
        this._host = offset;
        this._port = offset;
        this._path = offset;
        this._param = this._end;
        this._query = this._end;
        this._fragment = this._end;
        while (i < e) {
            char c = (char)(0xFF & this._raw[i]);
            int s = i++;
            switch (state) {
                case 0: {
                    m = s;
                    switch (c) {
                        case '/': {
                            state = 1;
                            continue;
                        }
                        case ';': {
                            this._param = s;
                            state = 8;
                            continue;
                        }
                        case '?': {
                            this._param = s;
                            this._query = s;
                            state = 9;
                            continue;
                        }
                        case '#': {
                            this._param = s;
                            this._query = s;
                            this._fragment = s;
                            continue;
                        }
                        case '*': {
                            this._path = s;
                            state = 10;
                            continue;
                        }
                        default: {
                            if (Character.isLetterOrDigit(c)) {
                                state = 2;
                                continue;
                            }
                            throw new IllegalArgumentException(StringUtil.toString(this._raw, offset, length, URIUtil.__CHARSET));
                        }
                    }
                    break;
                }
                case 1: {
                    if ((this._partial || this._scheme != this._authority) && c == '/') {
                        this._host = i;
                        this._port = this._end;
                        this._path = this._end;
                        state = 4;
                        continue;
                    }
                    if (c == ';' || c == '?' || c == '#') {
                        --i;
                        state = 7;
                        continue;
                    }
                    this._host = m;
                    this._port = m;
                    state = 7;
                    continue;
                }
                case 2: {
                    if (length > 6 && c == 't') {
                        if (this._raw[offset + 3] == 58) {
                            s = offset + 3;
                            i = offset + 4;
                            c = ':';
                        }
                        else if (this._raw[offset + 4] == 58) {
                            s = offset + 4;
                            i = offset + 5;
                            c = ':';
                        }
                        else if (this._raw[offset + 5] == 58) {
                            s = offset + 5;
                            i = offset + 6;
                            c = ':';
                        }
                    }
                    switch (c) {
                        case ':': {
                            m = i++;
                            this._authority = m;
                            this._path = m;
                            c = (char)(0xFF & this._raw[i]);
                            if (c == '/') {
                                state = 1;
                                continue;
                            }
                            this._host = m;
                            this._port = m;
                            state = 7;
                            continue;
                        }
                        case '/': {
                            state = 7;
                            continue;
                        }
                        case ';': {
                            this._param = s;
                            state = 8;
                            continue;
                        }
                        case '?': {
                            this._param = s;
                            this._query = s;
                            state = 9;
                            continue;
                        }
                        case '#': {
                            this._param = s;
                            this._query = s;
                            this._fragment = s;
                            continue;
                        }
                    }
                    continue;
                }
                case 4: {
                    switch (c) {
                        case '/': {
                            m = s;
                            this._path = m;
                            this._port = this._path;
                            state = 7;
                            continue;
                        }
                        case '@': {
                            this._host = i;
                            continue;
                        }
                        case ':': {
                            this._port = s;
                            state = 6;
                            continue;
                        }
                        case '[': {
                            state = 5;
                            continue;
                        }
                    }
                    continue;
                }
                case 5: {
                    switch (c) {
                        case '/': {
                            throw new IllegalArgumentException("No closing ']' for " + StringUtil.toString(this._raw, offset, length, URIUtil.__CHARSET));
                        }
                        case ']': {
                            state = 4;
                            continue;
                        }
                    }
                    continue;
                }
                case 6: {
                    if (c == '/') {
                        m = s;
                        this._path = m;
                        if (this._port <= this._authority) {
                            this._port = this._path;
                        }
                        state = 7;
                        continue;
                    }
                    continue;
                }
                case 7: {
                    switch (c) {
                        case ';': {
                            this._param = s;
                            state = 8;
                            continue;
                        }
                        case '?': {
                            this._param = s;
                            this._query = s;
                            state = 9;
                            continue;
                        }
                        case '#': {
                            this._param = s;
                            this._query = s;
                            this._fragment = s;
                            continue;
                        }
                    }
                    continue;
                }
                case 8: {
                    switch (c) {
                        case '?': {
                            this._query = s;
                            state = 9;
                            continue;
                        }
                        case '#': {
                            this._query = s;
                            this._fragment = s;
                            continue;
                        }
                    }
                    continue;
                }
                case 9: {
                    if (c == '#') {
                        this._fragment = s;
                        continue;
                    }
                    continue;
                }
                case 10: {
                    throw new IllegalArgumentException("only '*'");
                }
            }
        }
    }
    
    private String toUtf8String(final int offset, final int length) {
        this._utf8b.reset();
        this._utf8b.append(this._raw, offset, length);
        return this._utf8b.toString();
    }
    
    public String getScheme() {
        if (this._scheme == this._authority) {
            return null;
        }
        final int l = this._authority - this._scheme;
        if (l == 5 && this._raw[this._scheme] == 104 && this._raw[this._scheme + 1] == 116 && this._raw[this._scheme + 2] == 116 && this._raw[this._scheme + 3] == 112) {
            return "http";
        }
        if (l == 6 && this._raw[this._scheme] == 104 && this._raw[this._scheme + 1] == 116 && this._raw[this._scheme + 2] == 116 && this._raw[this._scheme + 3] == 112 && this._raw[this._scheme + 4] == 115) {
            return "https";
        }
        return this.toUtf8String(this._scheme, this._authority - this._scheme - 1);
    }
    
    public String getAuthority() {
        if (this._authority == this._path) {
            return null;
        }
        return this.toUtf8String(this._authority, this._path - this._authority);
    }
    
    public String getHost() {
        if (this._host == this._port) {
            return null;
        }
        return this.toUtf8String(this._host, this._port - this._host);
    }
    
    public int getPort() {
        if (this._port == this._path) {
            return -1;
        }
        return TypeUtil.parseInt(this._raw, this._port + 1, this._path - this._port - 1, 10);
    }
    
    public String getPath() {
        if (this._path == this._param) {
            return null;
        }
        return this.toUtf8String(this._path, this._param - this._path);
    }
    
    public String getDecodedPath() {
        if (this._path == this._param) {
            return null;
        }
        final int length = this._param - this._path;
        byte[] bytes = null;
        int n = 0;
        for (int i = this._path; i < this._param; ++i) {
            byte b = this._raw[i];
            if (b == 37 && i + 2 < this._param) {
                b = (byte)(0xFF & TypeUtil.parseInt(this._raw, i + 1, 2, 16));
                i += 2;
            }
            else if (bytes == null) {
                ++n;
                continue;
            }
            if (bytes == null) {
                bytes = new byte[length];
                for (int j = 0; j < n; ++j) {
                    bytes[j] = this._raw[this._path + j];
                }
            }
            bytes[n++] = b;
        }
        if (bytes == null) {
            return this.toUtf8String(this._path, length);
        }
        this._utf8b.reset();
        this._utf8b.append(bytes, 0, n);
        return this._utf8b.toString();
    }
    
    public String getPathAndParam() {
        if (this._path == this._query) {
            return null;
        }
        return this.toUtf8String(this._path, this._query - this._path);
    }
    
    public String getCompletePath() {
        if (this._path == this._end) {
            return null;
        }
        return this.toUtf8String(this._path, this._end - this._path);
    }
    
    public String getParam() {
        if (this._param == this._query) {
            return null;
        }
        return this.toUtf8String(this._param + 1, this._query - this._param - 1);
    }
    
    public String getQuery() {
        if (this._query == this._fragment) {
            return null;
        }
        return this.toUtf8String(this._query + 1, this._fragment - this._query - 1);
    }
    
    public String getQuery(final String encoding) {
        if (this._query == this._fragment) {
            return null;
        }
        return StringUtil.toString(this._raw, this._query + 1, this._fragment - this._query - 1, encoding);
    }
    
    public boolean hasQuery() {
        return this._fragment > this._query;
    }
    
    public String getFragment() {
        if (this._fragment == this._end) {
            return null;
        }
        return this.toUtf8String(this._fragment + 1, this._end - this._fragment - 1);
    }
    
    public void decodeQueryTo(final MultiMap parameters) {
        if (this._query == this._fragment) {
            return;
        }
        this._utf8b.reset();
        UrlEncoded.decodeUtf8To(this._raw, this._query + 1, this._fragment - this._query - 1, parameters, this._utf8b);
    }
    
    public void decodeQueryTo(final MultiMap parameters, final String encoding) throws UnsupportedEncodingException {
        if (this._query == this._fragment) {
            return;
        }
        if (encoding == null || StringUtil.isUTF8(encoding)) {
            UrlEncoded.decodeUtf8To(this._raw, this._query + 1, this._fragment - this._query - 1, parameters);
        }
        else {
            UrlEncoded.decodeTo(this.toUtf8String(this._query + 1, this._fragment - this._query - 1), parameters, encoding);
        }
    }
    
    public void clear() {
        final int scheme = 0;
        this._end = scheme;
        this._fragment = scheme;
        this._query = scheme;
        this._param = scheme;
        this._path = scheme;
        this._port = scheme;
        this._host = scheme;
        this._authority = scheme;
        this._scheme = scheme;
        this._raw = HttpURI.__empty;
        this._rawString = "";
    }
    
    public String toString() {
        if (this._rawString == null) {
            this._rawString = this.toUtf8String(this._scheme, this._end - this._scheme);
        }
        return this._rawString;
    }
    
    public void writeTo(final Utf8StringBuffer buf) {
        buf.append(this._raw, this._scheme, this._end - this._scheme);
    }
    
    static {
        HttpURI.__empty = new byte[0];
    }
}
