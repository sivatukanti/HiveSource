// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.util.regex.Pattern;
import org.eclipse.jetty.util.log.Log;
import java.util.Collections;
import java.util.Collection;
import org.eclipse.jetty.util.component.AggregateLifeCycle;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.Iterator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.server.handler.ScopedHandler;

public class RegexTargetHandler extends ScopedHandler
{
    private static final Logger LOG;
    public static final String REGEX_MAPPINGS = "org.eclipse.jetty.rewrite.handler.REGEX_MAPPINGS";
    private final List<RegexMapping> _patterns;
    
    public RegexTargetHandler() {
        this._patterns = new CopyOnWriteArrayList<RegexMapping>();
    }
    
    public void addPatternTarget(final String pattern, final String target) {
        this._patterns.add(new RegexMapping(pattern, target));
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        final ContextHandler.Context context = ContextHandler.getCurrentContext();
        if (context != null) {
            final String config = context.getInitParameter("org.eclipse.jetty.rewrite.handler.REGEX_MAPPINGS");
            RegexTargetHandler.LOG.debug("{}={}", "org.eclipse.jetty.rewrite.handler.REGEX_MAPPINGS", config);
            final String[] arr$;
            final String[] mappings = arr$ = config.split("\\s*,\\s*");
            for (String mapping : arr$) {
                mapping = mapping.trim();
                final String[] parts = mapping.split("\\s*==\\s*");
                if (parts.length == 2) {
                    final String pattern = parts[0];
                    final String target = parts[1];
                    this.addPatternTarget(pattern, target);
                }
                else {
                    RegexTargetHandler.LOG.warn("Bad regex mapping: " + mapping, new Object[0]);
                }
            }
        }
    }
    
    @Override
    public void doScope(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        for (final RegexMapping rm : this._patterns) {
            final Matcher m = rm._pattern.matcher(target);
            if (m.matches()) {
                final String new_target = rm._target;
                String pi;
                String sp;
                if (m.groupCount() == 1 && target.endsWith(m.group(1))) {
                    pi = m.group(1);
                    sp = target.substring(0, target.length() - pi.length());
                }
                else {
                    sp = target;
                    pi = null;
                }
                baseRequest.setServletPath(sp);
                baseRequest.setPathInfo(pi);
                baseRequest.setAttribute("org.eclipse.jetty.servlet.REGEX_PATH", target);
                super.nextScope(new_target, baseRequest, request, response);
                return;
            }
        }
        super.nextScope(target, baseRequest, request, response);
    }
    
    @Override
    public void doHandle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        String path = (String)baseRequest.getAttribute("org.eclipse.jetty.servlet.REGEX_PATH");
        if (path == null) {
            path = target;
        }
        else {
            baseRequest.setAttribute("org.eclipse.jetty.servlet.REGEX_PATH", null);
        }
        super.nextHandle(path, baseRequest, request, response);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        AggregateLifeCycle.dumpObject(out, this);
        AggregateLifeCycle.dump(out, indent, this._patterns, Collections.singletonList(this.getHandler()));
    }
    
    static {
        LOG = Log.getLogger(RegexTargetHandler.class);
    }
    
    static class RegexMapping
    {
        final Pattern _pattern;
        final String _target;
        
        RegexMapping(final String regex, final String target) {
            this._pattern = Pattern.compile(regex);
            this._target = target;
        }
        
        @Override
        public String toString() {
            return this._pattern + "==" + this._target;
        }
    }
}
