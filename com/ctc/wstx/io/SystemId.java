// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.io;

import java.io.IOException;
import com.ctc.wstx.util.URLUtil;
import java.net.URL;

public class SystemId
{
    protected URL mURL;
    protected String mSystemId;
    
    protected SystemId(final String systemId, final URL url) {
        if (systemId == null && url == null) {
            throw new IllegalArgumentException("Can not pass null for both systemId and url");
        }
        this.mSystemId = systemId;
        this.mURL = url;
    }
    
    public static SystemId construct(final String systemId) {
        return (systemId == null) ? null : new SystemId(systemId, null);
    }
    
    public static SystemId construct(final URL url) {
        return (url == null) ? null : new SystemId(null, url);
    }
    
    public static SystemId construct(final String systemId, final URL url) {
        if (systemId == null && url == null) {
            return null;
        }
        return new SystemId(systemId, url);
    }
    
    public URL asURL() throws IOException {
        if (this.mURL == null) {
            this.mURL = URLUtil.urlFromSystemId(this.mSystemId);
        }
        return this.mURL;
    }
    
    public boolean hasResolvedURL() {
        return this.mURL != null;
    }
    
    @Override
    public String toString() {
        if (this.mSystemId == null) {
            this.mSystemId = this.mURL.toExternalForm();
        }
        return this.mSystemId;
    }
}
