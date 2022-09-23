// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.cookie;

public class DefaultCookie implements Cookie
{
    private final String name;
    private String value;
    private boolean wrap;
    private String domain;
    private String path;
    private int maxAge;
    private boolean secure;
    private boolean httpOnly;
    
    public DefaultCookie(String name, final String value) {
        this.maxAge = Integer.MIN_VALUE;
        if (name == null) {
            throw new NullPointerException("name");
        }
        name = name.trim();
        if (name.length() == 0) {
            throw new IllegalArgumentException("empty name");
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
                case ';':
                case '=': {
                    throw new IllegalArgumentException("name contains one of the following prohibited characters: =,; \\t\\r\\n\\v\\f: " + name);
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        if (name.charAt(0) == '$') {
            throw new IllegalArgumentException("name starting with '$' not allowed: " + name);
        }
        this.name = name;
        this.setValue(value);
    }
    
    public String name() {
        return this.name;
    }
    
    public String value() {
        return this.value;
    }
    
    public void setValue(final String value) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        this.value = value;
    }
    
    public boolean wrap() {
        return this.wrap;
    }
    
    public void setWrap(final boolean wrap) {
        this.wrap = wrap;
    }
    
    public String domain() {
        return this.domain;
    }
    
    public void setDomain(final String domain) {
        this.domain = this.validateValue("domain", domain);
    }
    
    public String path() {
        return this.path;
    }
    
    public void setPath(final String path) {
        this.path = this.validateValue("path", path);
    }
    
    public int maxAge() {
        return this.maxAge;
    }
    
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }
    
    public boolean isHttpOnly() {
        return this.httpOnly;
    }
    
    public void setHttpOnly(final boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
    
    @Override
    public int hashCode() {
        return this.name().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cookie)) {
            return false;
        }
        final Cookie that = (Cookie)o;
        if (!this.name().equalsIgnoreCase(that.name())) {
            return false;
        }
        if (this.path() == null) {
            if (that.path() != null) {
                return false;
            }
        }
        else {
            if (that.path() == null) {
                return false;
            }
            if (!this.path().equals(that.path())) {
                return false;
            }
        }
        if (this.domain() == null) {
            return that.domain() == null;
        }
        return that.domain() != null && this.domain().equalsIgnoreCase(that.domain());
    }
    
    public int compareTo(final Cookie c) {
        int v = this.name().compareToIgnoreCase(c.name());
        if (v != 0) {
            return v;
        }
        if (this.path() == null) {
            if (c.path() != null) {
                return -1;
            }
        }
        else {
            if (c.path() == null) {
                return 1;
            }
            v = this.path().compareTo(c.path());
            if (v != 0) {
                return v;
            }
        }
        if (this.domain() == null) {
            if (c.domain() != null) {
                return -1;
            }
            return 0;
        }
        else {
            if (c.domain() == null) {
                return 1;
            }
            v = this.domain().compareToIgnoreCase(c.domain());
            return v;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder().append(this.name()).append('=').append(this.value());
        if (this.domain() != null) {
            buf.append(", domain=").append(this.domain());
        }
        if (this.path() != null) {
            buf.append(", path=").append(this.path());
        }
        if (this.maxAge() >= 0) {
            buf.append(", maxAge=").append(this.maxAge()).append('s');
        }
        if (this.isSecure()) {
            buf.append(", secure");
        }
        if (this.isHttpOnly()) {
            buf.append(", HTTPOnly");
        }
        return buf.toString();
    }
    
    protected String validateValue(final String name, String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.length() == 0) {
            return null;
        }
        int i = 0;
        while (i < value.length()) {
            final char c = value.charAt(i);
            switch (c) {
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case ';': {
                    throw new IllegalArgumentException(name + " contains one of the following prohibited characters: " + ";\\r\\n\\f\\v (" + value + ')');
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        return value;
    }
}
