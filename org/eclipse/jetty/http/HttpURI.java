// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.TypeUtil;
import java.net.URI;

public class HttpURI
{
    private String _scheme;
    private String _user;
    private String _host;
    private int _port;
    private String _path;
    private String _param;
    private String _query;
    private String _fragment;
    String _uri;
    String _decodedPath;
    
    public static HttpURI createHttpURI(final String scheme, final String host, int port, final String path, final String param, final String query, final String fragment) {
        if (port == 80 && HttpScheme.HTTP.is(scheme)) {
            port = 0;
        }
        if (port == 443 && HttpScheme.HTTPS.is(scheme)) {
            port = 0;
        }
        return new HttpURI(scheme, host, port, path, param, query, fragment);
    }
    
    public HttpURI() {
    }
    
    public HttpURI(final String scheme, final String host, final int port, final String path, final String param, final String query, final String fragment) {
        this._scheme = scheme;
        this._host = host;
        this._port = port;
        this._path = path;
        this._param = param;
        this._query = query;
        this._fragment = fragment;
    }
    
    public HttpURI(final HttpURI uri) {
        this(uri._scheme, uri._host, uri._port, uri._path, uri._param, uri._query, uri._fragment);
    }
    
    public HttpURI(final String uri) {
        this._port = -1;
        this.parse(State.START, uri, 0, uri.length());
    }
    
    public HttpURI(final URI uri) {
        this._uri = null;
        this._scheme = uri.getScheme();
        this._host = uri.getHost();
        if (this._host == null && uri.getRawSchemeSpecificPart().startsWith("//")) {
            this._host = "";
        }
        this._port = uri.getPort();
        this._user = uri.getUserInfo();
        this._path = uri.getRawPath();
        this._decodedPath = uri.getPath();
        if (this._decodedPath != null) {
            final int p = this._decodedPath.lastIndexOf(59);
            if (p >= 0) {
                this._param = this._decodedPath.substring(p + 1);
            }
        }
        this._query = uri.getRawQuery();
        this._fragment = uri.getFragment();
        this._decodedPath = null;
    }
    
    public HttpURI(final String scheme, final String host, final int port, final String pathQuery) {
        this._uri = null;
        this._scheme = scheme;
        this._host = host;
        this._port = port;
        this.parse(State.PATH, pathQuery, 0, pathQuery.length());
    }
    
    public void parse(final String uri) {
        this.clear();
        this._uri = uri;
        this.parse(State.START, uri, 0, uri.length());
    }
    
    public void parseRequestTarget(final String method, final String uri) {
        this.clear();
        this._uri = uri;
        if (HttpMethod.CONNECT.is(method)) {
            this._path = uri;
        }
        else {
            this.parse(uri.startsWith("/") ? State.PATH : State.START, uri, 0, uri.length());
        }
    }
    
    @Deprecated
    public void parseConnect(final String uri) {
        this.clear();
        this._uri = uri;
        this._path = uri;
    }
    
    public void parse(final String uri, final int offset, final int length) {
        this.clear();
        final int end = offset + length;
        this._uri = uri.substring(offset, end);
        this.parse(State.START, uri, offset, end);
    }
    
    private void parse(State state, final String uri, final int offset, final int end) {
        boolean encoded = false;
        int mark = offset;
        int path_mark = 0;
        for (int i = offset; i < end; ++i) {
            char c = uri.charAt(i);
            switch (state) {
                case START: {
                    switch (c) {
                        case '/': {
                            mark = i;
                            state = State.HOST_OR_PATH;
                            continue;
                        }
                        case ';': {
                            mark = i + 1;
                            state = State.PARAM;
                            continue;
                        }
                        case '?': {
                            this._path = "";
                            mark = i + 1;
                            state = State.QUERY;
                            continue;
                        }
                        case '#': {
                            mark = i + 1;
                            state = State.FRAGMENT;
                            continue;
                        }
                        case '*': {
                            this._path = "*";
                            state = State.ASTERISK;
                            continue;
                        }
                        default: {
                            mark = i;
                            if (this._scheme == null) {
                                state = State.SCHEME_OR_PATH;
                                continue;
                            }
                            path_mark = i;
                            state = State.PATH;
                            continue;
                        }
                    }
                    break;
                }
                case SCHEME_OR_PATH: {
                    switch (c) {
                        case ':': {
                            this._scheme = uri.substring(mark, i);
                            state = State.START;
                            break;
                        }
                        case '/': {
                            state = State.PATH;
                            break;
                        }
                        case ';': {
                            mark = i + 1;
                            state = State.PARAM;
                            break;
                        }
                        case '?': {
                            this._path = uri.substring(mark, i);
                            mark = i + 1;
                            state = State.QUERY;
                            break;
                        }
                        case '%': {
                            encoded = true;
                            state = State.PATH;
                            break;
                        }
                        case '#': {
                            this._path = uri.substring(mark, i);
                            state = State.FRAGMENT;
                            break;
                        }
                    }
                    break;
                }
                case HOST_OR_PATH: {
                    switch (c) {
                        case '/': {
                            this._host = "";
                            mark = i + 1;
                            state = State.HOST;
                            continue;
                        }
                        case '#':
                        case ';':
                        case '?':
                        case '@': {
                            --i;
                            path_mark = mark;
                            state = State.PATH;
                            continue;
                        }
                        default: {
                            path_mark = mark;
                            state = State.PATH;
                            continue;
                        }
                    }
                    break;
                }
                case HOST: {
                    switch (c) {
                        case '/': {
                            this._host = uri.substring(mark, i);
                            mark = (path_mark = i);
                            state = State.PATH;
                            break;
                        }
                        case ':': {
                            if (i > mark) {
                                this._host = uri.substring(mark, i);
                            }
                            mark = i + 1;
                            state = State.PORT;
                            break;
                        }
                        case '@': {
                            if (this._user != null) {
                                throw new IllegalArgumentException("Bad authority");
                            }
                            this._user = uri.substring(mark, i);
                            mark = i + 1;
                            break;
                        }
                        case '[': {
                            state = State.IPV6;
                            break;
                        }
                    }
                    break;
                }
                case IPV6: {
                    switch (c) {
                        case '/': {
                            throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
                        }
                        case ']': {
                            c = uri.charAt(++i);
                            this._host = uri.substring(mark, i);
                            if (c == ':') {
                                mark = i + 1;
                                state = State.PORT;
                                break;
                            }
                            mark = (path_mark = i);
                            state = State.PATH;
                            break;
                        }
                    }
                    break;
                }
                case PORT: {
                    if (c == '@') {
                        if (this._user != null) {
                            throw new IllegalArgumentException("Bad authority");
                        }
                        this._user = this._host + ":" + uri.substring(mark, i);
                        mark = i + 1;
                        state = State.HOST;
                        break;
                    }
                    else {
                        if (c == '/') {
                            this._port = TypeUtil.parseInt(uri, mark, i - mark, 10);
                            mark = (path_mark = i);
                            state = State.PATH;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case PATH: {
                    switch (c) {
                        case ';': {
                            mark = i + 1;
                            state = State.PARAM;
                            break;
                        }
                        case '?': {
                            this._path = uri.substring(path_mark, i);
                            mark = i + 1;
                            state = State.QUERY;
                            break;
                        }
                        case '#': {
                            this._path = uri.substring(path_mark, i);
                            mark = i + 1;
                            state = State.FRAGMENT;
                            break;
                        }
                        case '%': {
                            encoded = true;
                            break;
                        }
                    }
                    break;
                }
                case PARAM: {
                    switch (c) {
                        case '?': {
                            this._path = uri.substring(path_mark, i);
                            this._param = uri.substring(mark, i);
                            mark = i + 1;
                            state = State.QUERY;
                            break;
                        }
                        case '#': {
                            this._path = uri.substring(path_mark, i);
                            this._param = uri.substring(mark, i);
                            mark = i + 1;
                            state = State.FRAGMENT;
                            break;
                        }
                        case '/': {
                            encoded = true;
                            state = State.PATH;
                            break;
                        }
                        case ';': {
                            mark = i + 1;
                            break;
                        }
                    }
                    break;
                }
                case QUERY: {
                    if (c == '#') {
                        this._query = uri.substring(mark, i);
                        mark = i + 1;
                        state = State.FRAGMENT;
                        break;
                    }
                    break;
                }
                case ASTERISK: {
                    throw new IllegalArgumentException("only '*'");
                }
                case FRAGMENT: {
                    this._fragment = uri.substring(mark, end);
                    i = end;
                    break;
                }
            }
        }
        switch (state) {
            case SCHEME_OR_PATH: {
                this._path = uri.substring(mark, end);
                break;
            }
            case HOST_OR_PATH: {
                this._path = uri.substring(mark, end);
                break;
            }
            case HOST: {
                if (end > mark) {
                    this._host = uri.substring(mark, end);
                    break;
                }
                break;
            }
            case IPV6: {
                throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
            }
            case PORT: {
                this._port = TypeUtil.parseInt(uri, mark, end - mark, 10);
            }
            case FRAGMENT: {
                this._fragment = uri.substring(mark, end);
                break;
            }
            case PARAM: {
                this._path = uri.substring(path_mark, end);
                this._param = uri.substring(mark, end);
                break;
            }
            case PATH: {
                this._path = uri.substring(path_mark, end);
                break;
            }
            case QUERY: {
                this._query = uri.substring(mark, end);
                break;
            }
        }
        if (!encoded) {
            if (this._param == null) {
                this._decodedPath = this._path;
            }
            else {
                this._decodedPath = this._path.substring(0, this._path.length() - this._param.length() - 1);
            }
        }
    }
    
    public String getScheme() {
        return this._scheme;
    }
    
    public String getHost() {
        if (this._host != null && this._host.length() == 0) {
            return null;
        }
        return this._host;
    }
    
    public int getPort() {
        return this._port;
    }
    
    public String getPath() {
        return this._path;
    }
    
    public String getDecodedPath() {
        if (this._decodedPath == null && this._path != null) {
            this._decodedPath = URIUtil.decodePath(this._path);
        }
        return this._decodedPath;
    }
    
    public String getParam() {
        return this._param;
    }
    
    public String getQuery() {
        return this._query;
    }
    
    public boolean hasQuery() {
        return this._query != null && this._query.length() > 0;
    }
    
    public String getFragment() {
        return this._fragment;
    }
    
    public void decodeQueryTo(final MultiMap<String> parameters) {
        if (this._query == null) {
            return;
        }
        UrlEncoded.decodeUtf8To(this._query, parameters);
    }
    
    public void decodeQueryTo(final MultiMap<String> parameters, final String encoding) throws UnsupportedEncodingException {
        this.decodeQueryTo(parameters, Charset.forName(encoding));
    }
    
    public void decodeQueryTo(final MultiMap<String> parameters, final Charset encoding) throws UnsupportedEncodingException {
        if (this._query == null) {
            return;
        }
        if (encoding == null || StandardCharsets.UTF_8.equals(encoding)) {
            UrlEncoded.decodeUtf8To(this._query, parameters);
        }
        else {
            UrlEncoded.decodeTo(this._query, parameters, encoding);
        }
    }
    
    public void clear() {
        this._uri = null;
        this._scheme = null;
        this._host = null;
        this._port = -1;
        this._path = null;
        this._param = null;
        this._query = null;
        this._fragment = null;
        this._decodedPath = null;
    }
    
    public boolean isAbsolute() {
        return this._scheme != null && this._scheme.length() > 0;
    }
    
    @Override
    public String toString() {
        if (this._uri == null) {
            final StringBuilder out = new StringBuilder();
            if (this._scheme != null) {
                out.append(this._scheme).append(':');
            }
            if (this._host != null) {
                out.append("//");
                if (this._user != null) {
                    out.append(this._user).append('@');
                }
                out.append(this._host);
            }
            if (this._port > 0) {
                out.append(':').append(this._port);
            }
            if (this._path != null) {
                out.append(this._path);
            }
            if (this._query != null) {
                out.append('?').append(this._query);
            }
            if (this._fragment != null) {
                out.append('#').append(this._fragment);
            }
            if (out.length() > 0) {
                this._uri = out.toString();
            }
            else {
                this._uri = "";
            }
        }
        return this._uri;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof HttpURI && this.toString().equals(o.toString()));
    }
    
    public void setScheme(final String scheme) {
        this._scheme = scheme;
        this._uri = null;
    }
    
    public void setAuthority(final String host, final int port) {
        this._host = host;
        this._port = port;
        this._uri = null;
    }
    
    public void setPath(final String path) {
        this._uri = null;
        this._path = path;
        this._decodedPath = null;
    }
    
    public void setPathQuery(final String path) {
        this._uri = null;
        this._path = null;
        this._decodedPath = null;
        this._param = null;
        this._fragment = null;
        if (path != null) {
            this.parse(State.PATH, path, 0, path.length());
        }
    }
    
    public void setQuery(final String query) {
        this._query = query;
        this._uri = null;
    }
    
    public URI toURI() throws URISyntaxException {
        return new URI(this._scheme, null, this._host, this._port, this._path, (this._query == null) ? null : UrlEncoded.decodeString(this._query), this._fragment);
    }
    
    public String getPathQuery() {
        if (this._query == null) {
            return this._path;
        }
        return this._path + "?" + this._query;
    }
    
    public String getAuthority() {
        if (this._port > 0) {
            return this._host + ":" + this._port;
        }
        return this._host;
    }
    
    public String getUser() {
        return this._user;
    }
    
    private enum State
    {
        START, 
        HOST_OR_PATH, 
        SCHEME_OR_PATH, 
        HOST, 
        IPV6, 
        PORT, 
        PATH, 
        PARAM, 
        QUERY, 
        FRAGMENT, 
        ASTERISK;
    }
}
