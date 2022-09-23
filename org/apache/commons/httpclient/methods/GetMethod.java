// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.httpclient.HttpMethodBase;

public class GetMethod extends HttpMethodBase
{
    private static final Log LOG;
    
    public GetMethod() {
        this.setFollowRedirects(true);
    }
    
    public GetMethod(final String uri) {
        super(uri);
        GetMethod.LOG.trace("enter GetMethod(String)");
        this.setFollowRedirects(true);
    }
    
    public String getName() {
        return "GET";
    }
    
    public void recycle() {
        GetMethod.LOG.trace("enter GetMethod.recycle()");
        super.recycle();
        this.setFollowRedirects(true);
    }
    
    static {
        LOG = LogFactory.getLog(GetMethod.class);
    }
}
