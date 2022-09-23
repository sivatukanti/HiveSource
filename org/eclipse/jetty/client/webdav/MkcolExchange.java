// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.webdav;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.client.CachedExchange;

public class MkcolExchange extends CachedExchange
{
    private static final Logger LOG;
    boolean exists;
    
    public MkcolExchange() {
        super(true);
        this.exists = false;
    }
    
    @Override
    protected void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        if (status == 201) {
            MkcolExchange.LOG.debug("MkcolExchange:Status: Successfully created resource", new Object[0]);
            this.exists = true;
        }
        if (status == 405) {
            MkcolExchange.LOG.debug("MkcolExchange:Status: Resource must exist", new Object[0]);
            this.exists = true;
        }
        super.onResponseStatus(version, status, reason);
    }
    
    public boolean exists() {
        return this.exists;
    }
    
    static {
        LOG = Log.getLogger(MkcolExchange.class);
    }
}
