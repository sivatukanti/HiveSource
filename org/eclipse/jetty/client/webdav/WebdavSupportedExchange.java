// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.webdav;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.client.HttpExchange;

public class WebdavSupportedExchange extends HttpExchange
{
    private static final Logger LOG;
    private boolean _webdavSupported;
    private boolean _isComplete;
    
    public WebdavSupportedExchange() {
        this._webdavSupported = false;
        this._isComplete = false;
    }
    
    @Override
    protected void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
        if (WebdavSupportedExchange.LOG.isDebugEnabled()) {
            WebdavSupportedExchange.LOG.debug("WebdavSupportedExchange:Header:" + name.toString() + " / " + value.toString(), new Object[0]);
        }
        if ("DAV".equals(name.toString()) && (value.toString().indexOf("1") >= 0 || value.toString().indexOf("2") >= 0)) {
            this._webdavSupported = true;
        }
        super.onResponseHeader(name, value);
    }
    
    public void waitTilCompletion() throws InterruptedException {
        synchronized (this) {
            while (!this._isComplete) {
                this.wait();
            }
        }
    }
    
    @Override
    protected void onResponseComplete() throws IOException {
        this._isComplete = true;
        super.onResponseComplete();
    }
    
    public boolean isWebdavSupported() {
        return this._webdavSupported;
    }
    
    static {
        LOG = Log.getLogger(WebdavSupportedExchange.class);
    }
}
