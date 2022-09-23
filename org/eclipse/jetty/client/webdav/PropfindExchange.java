// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.webdav;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.client.HttpExchange;

public class PropfindExchange extends HttpExchange
{
    private static final Logger LOG;
    boolean _propertyExists;
    
    public PropfindExchange() {
        this._propertyExists = false;
    }
    
    @Override
    protected void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        if (status == 200) {
            PropfindExchange.LOG.debug("PropfindExchange:Status: Exists", new Object[0]);
            this._propertyExists = true;
        }
        else {
            PropfindExchange.LOG.debug("PropfindExchange:Status: Not Exists", new Object[0]);
        }
        super.onResponseStatus(version, status, reason);
    }
    
    public boolean exists() {
        return this._propertyExists;
    }
    
    static {
        LOG = Log.getLogger(PropfindExchange.class);
    }
}
