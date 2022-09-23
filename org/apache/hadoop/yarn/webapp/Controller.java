// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import com.google.common.collect.Maps;
import com.google.inject.servlet.RequestScoped;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.io.Writer;
import org.apache.hadoop.yarn.webapp.view.DefaultPage;
import javax.servlet.http.Cookie;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.yarn.util.StringHelper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class Controller implements Params
{
    public static final Logger LOG;
    static final ObjectMapper jsonMapper;
    private RequestContext context;
    @Inject
    Injector injector;
    
    public Controller() {
    }
    
    public Controller(final RequestContext ctx) {
        this.context = ctx;
    }
    
    public RequestContext context() {
        if (this.context == null) {
            if (this.injector == null) {
                throw new WebAppException(StringHelper.join("Error accessing RequestContext from\n", "a child constructor, either move the usage of the Controller\n", "methods out of the constructor or inject the RequestContext\n", "into the constructor"));
            }
            this.context = this.injector.getInstance(RequestContext.class);
        }
        return this.context;
    }
    
    public Throwable error() {
        return this.context().error;
    }
    
    public int status() {
        return this.context().status;
    }
    
    public void setStatus(final int status) {
        this.context().setStatus(status);
    }
    
    public boolean inDevMode() {
        return this.context().devMode;
    }
    
    public Injector injector() {
        return this.context().injector;
    }
    
    public <T> T getInstance(final Class<T> cls) {
        return this.injector.getInstance(cls);
    }
    
    public HttpServletRequest request() {
        return this.context().request;
    }
    
    public HttpServletResponse response() {
        return this.context().response;
    }
    
    public void set(final String key, final String value) {
        this.context().set(key, value);
    }
    
    public String get(final String key, final String defaultValue) {
        return this.context().get(key, defaultValue);
    }
    
    public String $(final String key) {
        return this.get(key, "");
    }
    
    public void setTitle(final String title) {
        this.set("title", title);
    }
    
    public void setTitle(final String title, final String url) {
        this.setTitle(title);
        this.set("title.href", url);
    }
    
    public ResponseInfo info(final String about) {
        return this.getInstance(ResponseInfo.class).about(about);
    }
    
    public Map<String, Cookie> cookies() {
        return this.context().cookies();
    }
    
    public String url(final String... parts) {
        return StringHelper.ujoin(this.context().prefix, parts);
    }
    
    public abstract void index();
    
    public void echo() {
        this.render(DefaultPage.class);
    }
    
    protected void render(final Class<? extends View> cls) {
        this.context().rendered = true;
        this.getInstance(cls).render();
    }
    
    protected void renderJSON(final Object object) {
        Controller.LOG.debug("{}: {}", "application/json; charset=UTF-8", object);
        this.context().rendered = true;
        this.context().response.setContentType("application/json; charset=UTF-8");
        try {
            Controller.jsonMapper.writeValue(this.writer(), object);
        }
        catch (Exception e) {
            throw new WebAppException(e);
        }
    }
    
    protected void renderJSON(final Class<? extends ToJSON> cls) {
        this.context().rendered = true;
        this.response().setContentType("application/json; charset=UTF-8");
        this.getInstance(cls).toJSON(this.writer());
    }
    
    protected void renderText(final String s) {
        Controller.LOG.debug("{}: {}", "text/plain; charset=UTF-8", s);
        this.context().rendered = true;
        this.response().setContentType("text/plain; charset=UTF-8");
        this.writer().print(s);
    }
    
    protected PrintWriter writer() {
        try {
            return this.response().getWriter();
        }
        catch (Exception e) {
            throw new WebAppException(e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(Controller.class);
        jsonMapper = new ObjectMapper();
    }
    
    @RequestScoped
    public static class RequestContext
    {
        final Injector injector;
        final HttpServletRequest request;
        final HttpServletResponse response;
        private Map<String, String> moreParams;
        private Map<String, Cookie> cookies;
        int status;
        boolean rendered;
        Throwable error;
        boolean devMode;
        String prefix;
        
        @Inject
        RequestContext(final Injector injector, final HttpServletRequest request, final HttpServletResponse response) {
            this.status = 200;
            this.rendered = false;
            this.devMode = false;
            this.injector = injector;
            this.request = request;
            this.response = response;
        }
        
        public int status() {
            return this.status;
        }
        
        public void setStatus(final int status) {
            this.status = status;
            this.response.setStatus(status);
        }
        
        public void setRendered(final boolean rendered) {
            this.rendered = rendered;
        }
        
        public Map<String, String> moreParams() {
            if (this.moreParams == null) {
                this.moreParams = (Map<String, String>)Maps.newHashMap();
            }
            return this.moreParams;
        }
        
        public Map<String, Cookie> cookies() {
            if (this.cookies == null) {
                this.cookies = (Map<String, Cookie>)Maps.newHashMap();
                final Cookie[] rcookies = this.request.getCookies();
                if (rcookies != null) {
                    for (final Cookie cookie : rcookies) {
                        this.cookies.put(cookie.getName(), cookie);
                    }
                }
            }
            return this.cookies;
        }
        
        public void set(final String key, final String value) {
            this.moreParams().put(key, value);
        }
        
        public String get(final String key, final String defaultValue) {
            String value = this.moreParams().get(key);
            if (value == null) {
                value = this.request.getParameter(key);
            }
            return (value == null) ? defaultValue : value;
        }
        
        public String prefix() {
            return this.prefix;
        }
    }
}
