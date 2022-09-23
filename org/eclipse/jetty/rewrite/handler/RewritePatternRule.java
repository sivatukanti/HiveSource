// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import org.eclipse.jetty.server.Request;
import java.io.IOException;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.http.PathMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RewritePatternRule extends PatternRule implements ApplyURI
{
    private String _replacement;
    
    public RewritePatternRule() {
        this._handling = false;
        this._terminating = false;
    }
    
    public void setReplacement(final String value) {
        this._replacement = value;
    }
    
    public String apply(String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        target = URIUtil.addPaths(this._replacement, PathMap.pathInfo(this._pattern, target));
        return target;
    }
    
    public void applyURI(final Request request, final String oldTarget, final String newTarget) throws IOException {
        final String uri = URIUtil.addPaths(this._replacement, PathMap.pathInfo(this._pattern, request.getRequestURI()));
        request.setRequestURI(uri);
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._replacement + "]";
    }
}
