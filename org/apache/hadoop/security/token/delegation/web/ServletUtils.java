// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.token.delegation.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
class ServletUtils
{
    private static final Charset UTF8_CHARSET;
    
    public static String getParameter(final HttpServletRequest request, final String name) throws IOException {
        final String queryString = request.getQueryString();
        if (queryString == null) {
            return null;
        }
        final List<NameValuePair> list = URLEncodedUtils.parse(queryString, ServletUtils.UTF8_CHARSET);
        if (list != null) {
            for (final NameValuePair nv : list) {
                if (name.equals(nv.getName())) {
                    return nv.getValue();
                }
            }
        }
        return null;
    }
    
    static {
        UTF8_CHARSET = Charset.forName("UTF-8");
    }
}
