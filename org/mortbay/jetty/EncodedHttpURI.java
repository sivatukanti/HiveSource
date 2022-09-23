// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.util.Utf8StringBuffer;
import java.io.UnsupportedEncodingException;
import org.mortbay.util.UrlEncoded;
import org.mortbay.util.MultiMap;
import org.mortbay.util.URIUtil;
import org.mortbay.util.TypeUtil;
import org.mortbay.util.StringUtil;

public class EncodedHttpURI extends HttpURI
{
    private String _encoding;
    
    public EncodedHttpURI(final String encoding) {
        this._encoding = encoding;
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
        return StringUtil.toString(this._raw, this._scheme, this._authority - this._scheme - 1, this._encoding);
    }
    
    public String getAuthority() {
        if (this._authority == this._path) {
            return null;
        }
        return StringUtil.toString(this._raw, this._authority, this._path - this._authority, this._encoding);
    }
    
    public String getHost() {
        if (this._host == this._port) {
            return null;
        }
        return StringUtil.toString(this._raw, this._host, this._port - this._host, this._encoding);
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
        return StringUtil.toString(this._raw, this._path, this._param - this._path, this._encoding);
    }
    
    public String getDecodedPath() {
        if (this._path == this._param) {
            return null;
        }
        return URIUtil.decodePath(this._raw, this._path, this._param - this._path);
    }
    
    public String getPathAndParam() {
        if (this._path == this._query) {
            return null;
        }
        return StringUtil.toString(this._raw, this._path, this._query - this._path, this._encoding);
    }
    
    public String getCompletePath() {
        if (this._path == this._end) {
            return null;
        }
        return StringUtil.toString(this._raw, this._path, this._end - this._path, this._encoding);
    }
    
    public String getParam() {
        if (this._param == this._query) {
            return null;
        }
        return StringUtil.toString(this._raw, this._param + 1, this._query - this._param - 1, this._encoding);
    }
    
    public String getQuery() {
        if (this._query == this._fragment) {
            return null;
        }
        return StringUtil.toString(this._raw, this._query + 1, this._fragment - this._query - 1, this._encoding);
    }
    
    public boolean hasQuery() {
        return this._fragment > this._query;
    }
    
    public String getFragment() {
        if (this._fragment == this._end) {
            return null;
        }
        return StringUtil.toString(this._raw, this._fragment + 1, this._end - this._fragment - 1, this._encoding);
    }
    
    public void decodeQueryTo(final MultiMap parameters) {
        if (this._query == this._fragment) {
            return;
        }
        UrlEncoded.decodeTo(StringUtil.toString(this._raw, this._query + 1, this._fragment - this._query - 1, this._encoding), parameters, this._encoding);
    }
    
    public void decodeQueryTo(final MultiMap parameters, String encoding) throws UnsupportedEncodingException {
        if (this._query == this._fragment) {
            return;
        }
        if (encoding == null) {
            encoding = this._encoding;
        }
        UrlEncoded.decodeTo(StringUtil.toString(this._raw, this._query + 1, this._fragment - this._query - 1, encoding), parameters, encoding);
    }
    
    public String toString() {
        if (this._rawString == null) {
            this._rawString = StringUtil.toString(this._raw, this._scheme, this._end - this._scheme, this._encoding);
        }
        return this._rawString;
    }
    
    public void writeTo(final Utf8StringBuffer buf) {
        buf.getStringBuffer().append(this.toString());
    }
}
