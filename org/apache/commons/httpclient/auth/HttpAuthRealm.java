// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.auth;

public class HttpAuthRealm extends AuthScope
{
    public HttpAuthRealm(final String domain, final String realm) {
        super(domain, -1, realm, HttpAuthRealm.ANY_SCHEME);
    }
}
