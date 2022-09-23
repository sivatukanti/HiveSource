// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.LazyList;
import org.eclipse.jetty.util.log.Logger;

public class RuleContainer extends Rule
{
    private static final Logger LOG;
    protected Rule[] _rules;
    protected String _originalPathAttribute;
    protected boolean _rewriteRequestURI;
    protected boolean _rewritePathInfo;
    protected LegacyRule _legacy;
    
    public RuleContainer() {
        this._rewriteRequestURI = true;
        this._rewritePathInfo = true;
    }
    
    @Deprecated
    public LegacyRule getLegacyRule() {
        if (this._legacy == null) {
            this.addRule(this._legacy = new LegacyRule());
        }
        return this._legacy;
    }
    
    @Deprecated
    public void setLegacyRule(final LegacyRule legacyRule) {
        this._legacy = legacyRule;
    }
    
    public Rule[] getRules() {
        return this._rules;
    }
    
    public void setRules(final Rule[] rules) {
        if (this._legacy == null) {
            this._rules = rules;
        }
        else {
            this._rules = null;
            this.addRule(this._legacy);
            if (rules != null) {
                for (final Rule rule : rules) {
                    this.addRule(rule);
                }
            }
        }
    }
    
    public void addRule(final Rule rule) {
        this._rules = (Rule[])LazyList.addToArray((Object[])this._rules, (Object)rule, (Class)Rule.class);
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
    
    @Override
    public String matchAndApply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        return this.apply(target, request, response);
    }
    
    protected String apply(String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        boolean original_set = this._originalPathAttribute == null;
        for (final Rule rule : this._rules) {
            final String applied = rule.matchAndApply(target, request, response);
            if (applied != null) {
                RuleContainer.LOG.debug("applied {}", rule);
                if (!target.equals(applied)) {
                    RuleContainer.LOG.debug("rewrote {} to {}", target, applied);
                    if (!original_set) {
                        original_set = true;
                        request.setAttribute(this._originalPathAttribute, target);
                    }
                    if (this._rewriteRequestURI) {
                        if (rule instanceof ApplyURI && !target.equals(request.getRequestURI())) {
                            ((ApplyURI)rule).applyURI((Request)request, target, applied);
                        }
                        else {
                            ((Request)request).setRequestURI(applied);
                        }
                    }
                    if (this._rewritePathInfo) {
                        ((Request)request).setPathInfo(applied);
                    }
                    target = applied;
                }
                if (rule.isHandling()) {
                    RuleContainer.LOG.debug("handling {}", rule);
                    ((Request)((request instanceof Request) ? request : AbstractHttpConnection.getCurrentConnection().getRequest())).setHandled(true);
                }
                if (rule.isTerminating()) {
                    RuleContainer.LOG.debug("terminating {}", rule);
                    break;
                }
            }
        }
        return target;
    }
    
    static {
        LOG = Log.getLogger(RuleContainer.class);
    }
}
