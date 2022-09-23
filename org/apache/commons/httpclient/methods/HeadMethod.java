// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.ProtocolException;
import java.io.IOException;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.httpclient.HttpMethodBase;

public class HeadMethod extends HttpMethodBase
{
    private static final Log LOG;
    
    public HeadMethod() {
        this.setFollowRedirects(true);
    }
    
    public HeadMethod(final String uri) {
        super(uri);
        this.setFollowRedirects(true);
    }
    
    public String getName() {
        return "HEAD";
    }
    
    public void recycle() {
        super.recycle();
        this.setFollowRedirects(true);
    }
    
    protected void readResponseBody(final HttpState state, final HttpConnection conn) throws HttpException, IOException {
        HeadMethod.LOG.trace("enter HeadMethod.readResponseBody(HttpState, HttpConnection)");
        final int bodyCheckTimeout = this.getParams().getIntParameter("http.protocol.head-body-timeout", -1);
        if (bodyCheckTimeout < 0) {
            this.responseBodyConsumed();
        }
        else {
            if (HeadMethod.LOG.isDebugEnabled()) {
                HeadMethod.LOG.debug("Check for non-compliant response body. Timeout in " + bodyCheckTimeout + " ms");
            }
            boolean responseAvailable = false;
            try {
                responseAvailable = conn.isResponseAvailable(bodyCheckTimeout);
            }
            catch (IOException e) {
                HeadMethod.LOG.debug("An IOException occurred while testing if a response was available, we will assume one is not.", e);
                responseAvailable = false;
            }
            if (responseAvailable) {
                if (this.getParams().isParameterTrue("http.protocol.reject-head-body")) {
                    throw new ProtocolException("Body content may not be sent in response to HTTP HEAD request");
                }
                HeadMethod.LOG.warn("Body content returned in response to HTTP HEAD");
                super.readResponseBody(state, conn);
            }
        }
    }
    
    public int getBodyCheckTimeout() {
        return this.getParams().getIntParameter("http.protocol.head-body-timeout", -1);
    }
    
    public void setBodyCheckTimeout(final int timeout) {
        this.getParams().setIntParameter("http.protocol.head-body-timeout", timeout);
    }
    
    static {
        LOG = LogFactory.getLog(HeadMethod.class);
    }
}
