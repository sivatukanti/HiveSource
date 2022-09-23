// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.nested;

import org.eclipse.jetty.server.AbstractHttpConnection;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;

public class NestedRequest extends Request
{
    private final HttpServletRequest _outer;
    
    public NestedRequest(final HttpServletRequest outer) {
        this._outer = outer;
    }
    
    void setConnection(final NestedConnection connection) {
        super.setConnection((AbstractHttpConnection)connection);
    }
    
    @Override
    public boolean isSecure() {
        return this._outer.isSecure() || "https".equals(this.getScheme());
    }
}
