// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import com.google.inject.servlet.RequestScoped;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.hadoop.yarn.util.StringHelper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class View implements Params
{
    public static final Logger LOG;
    private ViewContext vc;
    @Inject
    Injector injector;
    
    public View() {
    }
    
    public View(final ViewContext ctx) {
        this.vc = ctx;
    }
    
    public abstract void render();
    
    public ViewContext context() {
        if (this.vc == null) {
            if (this.injector == null) {
                throw new WebAppException(StringHelper.join("Error accessing ViewContext from a\n", "child constructor, either move the usage of the View methods\n", "out of the constructor or inject the ViewContext into the\n", "constructor"));
            }
            this.vc = this.injector.getInstance(ViewContext.class);
        }
        return this.vc;
    }
    
    public Throwable error() {
        return this.context().rc.error;
    }
    
    public int status() {
        return this.context().rc.status;
    }
    
    public boolean inDevMode() {
        return this.context().rc.devMode;
    }
    
    public Injector injector() {
        return this.context().rc.injector;
    }
    
    public <T> T getInstance(final Class<T> cls) {
        return this.injector().getInstance(cls);
    }
    
    public HttpServletRequest request() {
        return this.context().rc.request;
    }
    
    public HttpServletResponse response() {
        return this.context().rc.response;
    }
    
    public Map<String, String> moreParams() {
        return this.context().rc.moreParams();
    }
    
    public Map<String, Cookie> cookies() {
        return this.context().rc.cookies();
    }
    
    public ServletOutputStream outputStream() {
        try {
            return this.response().getOutputStream();
        }
        catch (IOException e) {
            throw new WebAppException(e);
        }
    }
    
    public PrintWriter writer() {
        try {
            return this.response().getWriter();
        }
        catch (IOException e) {
            throw new WebAppException(e);
        }
    }
    
    public String $(final String key, final String defaultValue) {
        String value = this.moreParams().get(key);
        if (value == null) {
            value = this.request().getParameter(key);
        }
        return (value == null) ? defaultValue : value;
    }
    
    public String $(final String key) {
        return this.$(key, "");
    }
    
    public void set(final String key, final String value) {
        this.moreParams().put(key, value);
    }
    
    public String root() {
        String root = System.getenv("APPLICATION_WEB_PROXY_BASE");
        if (root == null || root.isEmpty()) {
            root = "/";
        }
        return root;
    }
    
    public String prefix() {
        if (this.context().rc.prefix == null) {
            return this.root();
        }
        return StringHelper.ujoin(this.root(), this.context().rc.prefix);
    }
    
    public void setTitle(final String title) {
        this.set("title", title);
    }
    
    public void setTitle(final String title, final String url) {
        this.setTitle(title);
        this.set("title.href", url);
    }
    
    public String root_url(final String... parts) {
        return StringHelper.ujoin(this.root(), parts);
    }
    
    public String url(final String... parts) {
        return StringHelper.ujoin(this.prefix(), parts);
    }
    
    public ResponseInfo info(final String about) {
        return this.getInstance(ResponseInfo.class).about(about);
    }
    
    public void render(final Class<? extends SubView> cls) {
        final int saved = this.context().nestLevel;
        this.getInstance(cls).renderPartial();
        if (this.context().nestLevel != saved) {
            throw new WebAppException("View " + cls.getSimpleName() + " not complete");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(View.class);
    }
    
    @RequestScoped
    public static class ViewContext
    {
        final Controller.RequestContext rc;
        int nestLevel;
        boolean wasInline;
        
        @Inject
        ViewContext(final Controller.RequestContext ctx) {
            this.nestLevel = 0;
            this.rc = ctx;
        }
        
        public int nestLevel() {
            return this.nestLevel;
        }
        
        public boolean wasInline() {
            return this.wasInline;
        }
        
        public void set(final int nestLevel, final boolean wasInline) {
            this.nestLevel = nestLevel;
            this.wasInline = wasInline;
        }
        
        public Controller.RequestContext requestContext() {
            return this.rc;
        }
    }
}
