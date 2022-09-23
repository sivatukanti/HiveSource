// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;
import org.mortbay.jetty.Request;
import org.mortbay.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.jetty.servlet.PathMap;

public class RewriteHandler extends HandlerWrapper
{
    private boolean _rewriteRequestURI;
    private boolean _rewritePathInfo;
    private String _originalPathAttribute;
    private PathMap _rewrite;
    
    public RewriteHandler() {
        this._rewriteRequestURI = true;
        this._rewritePathInfo = true;
        this._rewrite = new PathMap(true);
    }
    
    public boolean isRewriteRequestURI() {
        return this._rewriteRequestURI;
    }
    
    public void setRewriteRequestURI(final boolean rewriteRequestURI) {
        this._rewriteRequestURI = rewriteRequestURI;
    }
    
    public boolean isRewritePathInfo() {
        return this._rewritePathInfo;
    }
    
    public void setRewritePathInfo(final boolean rewritePathInfo) {
        this._rewritePathInfo = rewritePathInfo;
    }
    
    public String getOriginalPathAttribute() {
        return this._originalPathAttribute;
    }
    
    public void setOriginalPathAttribute(final String originalPathAttribte) {
        this._originalPathAttribute = originalPathAttribte;
    }
    
    public PathMap getRewrite() {
        return this._rewrite;
    }
    
    public void setRewrite(final PathMap rewrite) {
        this._rewrite = rewrite;
    }
    
    public void addRewriteRule(final String pattern, final String prefix) {
        if (pattern == null || pattern.length() == 0 || !pattern.startsWith("/")) {
            throw new IllegalArgumentException();
        }
        if (this._rewrite == null) {
            this._rewrite = new PathMap(true);
        }
        this._rewrite.put(pattern, prefix);
    }
    
    public void handle(String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        if (this.isStarted() && this._rewrite != null) {
            final Map.Entry rewrite = this._rewrite.getMatch(target);
            if (rewrite != null && rewrite.getValue() != null) {
                if (this._originalPathAttribute != null) {
                    request.setAttribute(this._originalPathAttribute, target);
                }
                target = URIUtil.addPaths(rewrite.getValue().toString(), PathMap.pathInfo(rewrite.getKey().toString(), target));
                if (this._rewriteRequestURI) {
                    ((Request)request).setRequestURI(target);
                }
                if (this._rewritePathInfo) {
                    ((Request)request).setPathInfo(target);
                }
            }
        }
        super.handle(target, request, response, dispatch);
    }
}
