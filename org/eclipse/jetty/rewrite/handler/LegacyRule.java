// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import java.util.Map;
import org.eclipse.jetty.util.URIUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.http.PathMap;

public class LegacyRule extends Rule
{
    private PathMap _rewrite;
    
    public LegacyRule() {
        this._rewrite = new PathMap(true);
        this._handling = false;
        this._terminating = false;
    }
    
    @Override
    public String matchAndApply(String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Map.Entry<?, ?> rewrite = (Map.Entry<?, ?>)this._rewrite.getMatch(target);
        if (rewrite != null && rewrite.getValue() != null) {
            target = URIUtil.addPaths(rewrite.getValue().toString(), PathMap.pathInfo(rewrite.getKey().toString(), target));
            return target;
        }
        return null;
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
        this._rewrite.put((Object)pattern, prefix);
    }
}
