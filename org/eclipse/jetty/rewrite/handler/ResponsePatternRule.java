// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ResponsePatternRule extends PatternRule
{
    private String _code;
    private String _reason;
    
    public ResponsePatternRule() {
        this._reason = "";
        this._handling = true;
        this._terminating = true;
    }
    
    public void setCode(final String code) {
        this._code = code;
    }
    
    public void setReason(final String reason) {
        this._reason = reason;
    }
    
    public String apply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final int code = Integer.parseInt(this._code);
        if (code >= 400) {
            response.sendError(code, this._reason);
        }
        else {
            response.setStatus(code);
        }
        return target;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._code + "," + this._reason + "]";
    }
}
