// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import org.eclipse.jetty.server.Request;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class Rule
{
    protected boolean _terminating;
    protected boolean _handling;
    
    public abstract String matchAndApply(final String p0, final HttpServletRequest p1, final HttpServletResponse p2) throws IOException;
    
    public void setTerminating(final boolean terminating) {
        this._terminating = terminating;
    }
    
    public boolean isTerminating() {
        return this._terminating;
    }
    
    public boolean isHandling() {
        return this._handling;
    }
    
    public void setHandling(final boolean handling) {
        this._handling = handling;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + (this._handling ? "[H" : "[h") + (this._terminating ? "T]" : "t]");
    }
    
    public interface ApplyURI
    {
        void applyURI(final Request p0, final String p1, final String p2) throws IOException;
    }
}
