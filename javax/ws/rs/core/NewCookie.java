// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;

public class NewCookie extends Cookie
{
    public static final int DEFAULT_MAX_AGE = -1;
    private static final RuntimeDelegate.HeaderDelegate<NewCookie> delegate;
    private String comment;
    private int maxAge;
    private boolean secure;
    
    public NewCookie(final String name, final String value) {
        super(name, value);
        this.comment = null;
        this.maxAge = -1;
        this.secure = false;
    }
    
    public NewCookie(final String name, final String value, final String path, final String domain, final String comment, final int maxAge, final boolean secure) {
        super(name, value, path, domain);
        this.comment = null;
        this.maxAge = -1;
        this.secure = false;
        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }
    
    public NewCookie(final String name, final String value, final String path, final String domain, final int version, final String comment, final int maxAge, final boolean secure) {
        super(name, value, path, domain, version);
        this.comment = null;
        this.maxAge = -1;
        this.secure = false;
        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }
    
    public NewCookie(final Cookie cookie) {
        super((cookie == null) ? null : cookie.getName(), (cookie == null) ? null : cookie.getValue(), (cookie == null) ? null : cookie.getPath(), (cookie == null) ? null : cookie.getDomain(), (cookie == null) ? 1 : cookie.getVersion());
        this.comment = null;
        this.maxAge = -1;
        this.secure = false;
    }
    
    public NewCookie(final Cookie cookie, final String comment, final int maxAge, final boolean secure) {
        this(cookie);
        this.comment = comment;
        this.maxAge = maxAge;
        this.secure = secure;
    }
    
    public static NewCookie valueOf(final String value) throws IllegalArgumentException {
        return NewCookie.delegate.fromString(value);
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public int getMaxAge() {
        return this.maxAge;
    }
    
    public boolean isSecure() {
        return this.secure;
    }
    
    public Cookie toCookie() {
        return new Cookie(this.getName(), this.getValue(), this.getPath(), this.getDomain(), this.getVersion());
    }
    
    @Override
    public String toString() {
        return NewCookie.delegate.toString(this);
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + ((this.comment != null) ? this.comment.hashCode() : 0);
        hash = 59 * hash + this.maxAge;
        hash = 59 * hash + (this.secure ? 1 : 0);
        return hash;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final NewCookie other = (NewCookie)obj;
        return (this.getName() == other.getName() || (this.getName() != null && this.getName().equals(other.getName()))) && (this.getValue() == other.getValue() || (this.getValue() != null && this.getValue().equals(other.getValue()))) && this.getVersion() == other.getVersion() && (this.getPath() == other.getPath() || (this.getPath() != null && this.getPath().equals(other.getPath()))) && (this.getDomain() == other.getDomain() || (this.getDomain() != null && this.getDomain().equals(other.getDomain()))) && (this.comment == other.comment || (this.comment != null && this.comment.equals(other.comment))) && this.maxAge == other.maxAge && this.secure == other.secure;
    }
    
    static {
        delegate = RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class);
    }
}
