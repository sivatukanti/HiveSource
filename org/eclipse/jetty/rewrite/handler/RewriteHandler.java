// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

public class RewriteHandler extends HandlerWrapper
{
    private RuleContainer _rules;
    
    public RewriteHandler() {
        this._rules = new RuleContainer();
    }
    
    @Deprecated
    public void setLegacyRule(final LegacyRule legacyRule) {
        this._rules.setLegacyRule(legacyRule);
    }
    
    public Rule[] getRules() {
        return this._rules.getRules();
    }
    
    public void setRules(final Rule[] rules) {
        this._rules.setRules(rules);
    }
    
    public void setRules(final RuleContainer rules) {
        this._rules = rules;
    }
    
    public void addRule(final Rule rule) {
        this._rules.addRule(rule);
    }
    
    public boolean isRewriteRequestURI() {
        return this._rules.isRewriteRequestURI();
    }
    
    public void setRewriteRequestURI(final boolean rewriteRequestURI) {
        this._rules.setRewriteRequestURI(rewriteRequestURI);
    }
    
    public boolean isRewritePathInfo() {
        return this._rules.isRewritePathInfo();
    }
    
    public void setRewritePathInfo(final boolean rewritePathInfo) {
        this._rules.setRewritePathInfo(rewritePathInfo);
    }
    
    public String getOriginalPathAttribute() {
        return this._rules.getOriginalPathAttribute();
    }
    
    public void setOriginalPathAttribute(final String originalPathAttribute) {
        this._rules.setOriginalPathAttribute(originalPathAttribute);
    }
    
    @Override
    public void handle(String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (this.isStarted()) {
            final String returned = this._rules.matchAndApply(target, request, response);
            target = ((returned == null) ? target : returned);
            if (!baseRequest.isHandled()) {
                super.handle(target, baseRequest, request, response);
            }
        }
    }
}
