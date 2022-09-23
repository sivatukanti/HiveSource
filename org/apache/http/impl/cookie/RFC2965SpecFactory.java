// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.cookie;

import org.apache.http.protocol.HttpContext;
import java.util.Collection;
import org.apache.http.params.HttpParams;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.CookieSpecFactory;

@Deprecated
@Immutable
public class RFC2965SpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    private final CookieSpec cookieSpec;
    
    public RFC2965SpecFactory(final String[] datepatterns, final boolean oneHeader) {
        this.cookieSpec = new RFC2965Spec(datepatterns, oneHeader);
    }
    
    public RFC2965SpecFactory() {
        this(null, false);
    }
    
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            final Collection<?> param = (Collection<?>)params.getParameter("http.protocol.cookie-datepatterns");
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            final boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
            return new RFC2965Spec(patterns, singleHeader);
        }
        return new RFC2965Spec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }
}
