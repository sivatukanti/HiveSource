// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.util.Locale;
import org.eclipse.jetty.http.QuotedCSV;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import org.eclipse.jetty.util.log.Logger;

public class CookieCutter
{
    private static final Logger LOG;
    private Cookie[] _cookies;
    private Cookie[] _lastCookies;
    private final List<String> _fieldList;
    int _fields;
    
    public CookieCutter() {
        this._fieldList = new ArrayList<String>();
    }
    
    public Cookie[] getCookies() {
        if (this._cookies != null) {
            return this._cookies;
        }
        if (this._lastCookies != null && this._fields == this._fieldList.size()) {
            this._cookies = this._lastCookies;
        }
        else {
            this.parseFields();
        }
        this._lastCookies = this._cookies;
        return this._cookies;
    }
    
    public void setCookies(final Cookie[] cookies) {
        this._cookies = cookies;
        this._lastCookies = null;
        this._fieldList.clear();
        this._fields = 0;
    }
    
    public void reset() {
        this._cookies = null;
        this._fields = 0;
    }
    
    public void addCookieField(String f) {
        if (f == null) {
            return;
        }
        f = f.trim();
        if (f.length() == 0) {
            return;
        }
        if (this._fieldList.size() > this._fields) {
            if (f.equals(this._fieldList.get(this._fields))) {
                ++this._fields;
                return;
            }
            while (this._fieldList.size() > this._fields) {
                this._fieldList.remove(this._fields);
            }
        }
        this._cookies = null;
        this._lastCookies = null;
        this._fieldList.add(this._fields++, f);
    }
    
    protected void parseFields() {
        this._lastCookies = null;
        this._cookies = null;
        final List<Cookie> cookies = new ArrayList<Cookie>();
        int version = 0;
        while (this._fieldList.size() > this._fields) {
            this._fieldList.remove(this._fields);
        }
        for (final String hdr : this._fieldList) {
            String name = null;
            String value = null;
            Cookie cookie = null;
            boolean invalue = false;
            boolean quoted = false;
            boolean escaped = false;
            int tokenstart = -1;
            int tokenend = -1;
            int i = 0;
            final int length = hdr.length();
            final int last = length - 1;
            while (i < length) {
                final char c = hdr.charAt(i);
                Label_0807: {
                    if (quoted) {
                        if (escaped) {
                            escaped = false;
                            break Label_0807;
                        }
                        switch (c) {
                            case '\"': {
                                tokenend = i;
                                quoted = false;
                                if (i != last) {
                                    break;
                                }
                                if (invalue) {
                                    value = hdr.substring(tokenstart, tokenend + 1);
                                    break;
                                }
                                name = hdr.substring(tokenstart, tokenend + 1);
                                value = "";
                                break;
                            }
                            case '\\': {
                                escaped = true;
                            }
                            default: {
                                break Label_0807;
                            }
                        }
                    }
                    else if (invalue) {
                        switch (c) {
                            case '\t':
                            case ' ': {
                                break Label_0807;
                            }
                            case '\"': {
                                if (tokenstart < 0) {
                                    quoted = true;
                                    tokenstart = i;
                                }
                                if ((tokenend = i) == last) {
                                    value = hdr.substring(tokenstart, tokenend + 1);
                                    break;
                                }
                                break Label_0807;
                            }
                            case ';': {
                                if (tokenstart >= 0) {
                                    value = hdr.substring(tokenstart, tokenend + 1);
                                }
                                else {
                                    value = "";
                                }
                                tokenstart = -1;
                                invalue = false;
                                break;
                            }
                            default: {
                                if (tokenstart < 0) {
                                    tokenstart = i;
                                }
                                if ((tokenend = i) == last) {
                                    value = hdr.substring(tokenstart, tokenend + 1);
                                    break;
                                }
                                break Label_0807;
                            }
                        }
                    }
                    else {
                        switch (c) {
                            case '\t':
                            case ' ': {
                                break Label_0807;
                            }
                            case '\"': {
                                if (tokenstart < 0) {
                                    quoted = true;
                                    tokenstart = i;
                                }
                                if ((tokenend = i) == last) {
                                    name = hdr.substring(tokenstart, tokenend + 1);
                                    value = "";
                                    break;
                                }
                                break Label_0807;
                            }
                            case ';': {
                                if (tokenstart >= 0) {
                                    name = hdr.substring(tokenstart, tokenend + 1);
                                    value = "";
                                }
                                tokenstart = -1;
                                break;
                            }
                            case '=': {
                                if (tokenstart >= 0) {
                                    name = hdr.substring(tokenstart, tokenend + 1);
                                }
                                tokenstart = -1;
                                invalue = true;
                                break Label_0807;
                            }
                            default: {
                                if (tokenstart < 0) {
                                    tokenstart = i;
                                }
                                if ((tokenend = i) == last) {
                                    name = hdr.substring(tokenstart, tokenend + 1);
                                    value = "";
                                    break;
                                }
                                break Label_0807;
                            }
                        }
                    }
                    if (value != null && name != null) {
                        name = QuotedCSV.unquote(name);
                        value = QuotedCSV.unquote(value);
                        try {
                            if (name.startsWith("$")) {
                                final String lowercaseName = name.toLowerCase(Locale.ENGLISH);
                                if ("$path".equals(lowercaseName)) {
                                    if (cookie != null) {
                                        cookie.setPath(value);
                                    }
                                }
                                else if ("$domain".equals(lowercaseName)) {
                                    if (cookie != null) {
                                        cookie.setDomain(value);
                                    }
                                }
                                else if ("$port".equals(lowercaseName)) {
                                    if (cookie != null) {
                                        cookie.setComment("$port=" + value);
                                    }
                                }
                                else if ("$version".equals(lowercaseName)) {
                                    version = Integer.parseInt(value);
                                }
                            }
                            else {
                                cookie = new Cookie(name, value);
                                if (version > 0) {
                                    cookie.setVersion(version);
                                }
                                cookies.add(cookie);
                            }
                        }
                        catch (Exception e) {
                            CookieCutter.LOG.debug(e);
                        }
                        name = null;
                        value = null;
                    }
                }
                ++i;
            }
        }
        this._cookies = cookies.toArray(new Cookie[cookies.size()]);
        this._lastCookies = this._cookies;
    }
    
    static {
        LOG = Log.getLogger(CookieCutter.class);
    }
}
