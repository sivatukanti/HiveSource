// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.slf4j.LoggerFactory;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import java.util.Map;
import javax.servlet.Filter;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServlet;
import java.net.InetSocketAddress;
import com.google.inject.Provides;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import com.google.common.base.Splitter;
import com.google.inject.servlet.GuiceFilter;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import com.google.inject.servlet.ServletModule;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public abstract class WebApp extends ServletModule
{
    private static final Logger LOG;
    private volatile String name;
    private volatile List<String> servePathSpecs;
    private volatile String redirectPath;
    private volatile String wsName;
    private volatile Configuration conf;
    private volatile HttpServer2 httpServer;
    private volatile GuiceFilter guiceFilter;
    private final Router router;
    static final int R_PATH = 0;
    static final int R_CONTROLLER = 1;
    static final int R_ACTION = 2;
    static final int R_PARAMS = 3;
    static final Splitter pathSplitter;
    
    public WebApp() {
        this.servePathSpecs = new ArrayList<String>();
        this.router = new Router();
    }
    
    void setHttpServer(final HttpServer2 server) {
        this.httpServer = Preconditions.checkNotNull(server, (Object)"http server");
    }
    
    @Provides
    public HttpServer2 httpServer() {
        return this.httpServer;
    }
    
    public InetSocketAddress getListenerAddress() {
        return Preconditions.checkNotNull(this.httpServer, (Object)"httpServer").getConnectorAddress(0);
    }
    
    public int port() {
        final InetSocketAddress addr = Preconditions.checkNotNull(this.httpServer, (Object)"httpServer").getConnectorAddress(0);
        return (addr == null) ? -1 : addr.getPort();
    }
    
    public void stop() {
        try {
            Preconditions.checkNotNull(this.httpServer, (Object)"httpServer").stop();
            Preconditions.checkNotNull(this.guiceFilter, (Object)"guiceFilter").destroy();
        }
        catch (Exception e) {
            throw new WebAppException(e);
        }
    }
    
    public void joinThread() {
        try {
            Preconditions.checkNotNull(this.httpServer, (Object)"httpServer").join();
        }
        catch (InterruptedException e) {
            WebApp.LOG.info("interrupted", e);
        }
    }
    
    void setConf(final Configuration conf) {
        this.conf = conf;
    }
    
    @Provides
    public Configuration conf() {
        return this.conf;
    }
    
    @Provides
    Router router() {
        return this.router;
    }
    
    @Provides
    WebApp webApp() {
        return this;
    }
    
    void setName(final String name) {
        this.name = name;
    }
    
    public String name() {
        return this.name;
    }
    
    public String wsName() {
        return this.wsName;
    }
    
    void addServePathSpec(final String path) {
        this.servePathSpecs.add(path);
    }
    
    public String[] getServePathSpecs() {
        return this.servePathSpecs.toArray(new String[this.servePathSpecs.size()]);
    }
    
    void setRedirectPath(final String path) {
        this.redirectPath = path;
    }
    
    void setWebServices(final String name) {
        this.wsName = name;
    }
    
    public String getRedirectPath() {
        return this.redirectPath;
    }
    
    void setHostClass(final Class<?> cls) {
        this.router.setHostClass(cls);
    }
    
    void setGuiceFilter(final GuiceFilter instance) {
        this.guiceFilter = instance;
    }
    
    public void configureServlets() {
        this.setup();
        this.serve("/", "/__stop").with(Dispatcher.class);
        for (final String path : this.servePathSpecs) {
            this.serve(path, new String[0]).with(Dispatcher.class);
        }
        this.configureWebAppServlets();
    }
    
    protected void configureWebAppServlets() {
        if (this.wsName != null) {
            final String regex = "(?!/" + this.wsName + ")";
            this.serveRegex(regex, new String[0]).with(DefaultWrapperServlet.class);
            final Map<String, String> params = new HashMap<String, String>();
            params.put("com.sun.jersey.config.feature.ImplicitViewables", "true");
            params.put("com.sun.jersey.config.feature.FilterForwardOn404", "true");
            params.put("com.sun.jersey.config.feature.XmlRootElementProcessing", "true");
            params.put("com.sun.jersey.spi.container.ContainerRequestFilters", GZIPContentEncodingFilter.class.getName());
            params.put("com.sun.jersey.spi.container.ContainerResponseFilters", GZIPContentEncodingFilter.class.getName());
            this.filter("/*", new String[0]).through(this.getWebAppFilterClass(), params);
        }
    }
    
    protected Class<? extends GuiceContainer> getWebAppFilterClass() {
        return GuiceContainer.class;
    }
    
    public void route(final HTTP method, final String pathSpec, final Class<? extends Controller> cls, final String action) {
        final List<String> res = parseRoute(pathSpec);
        this.router.add(method, res.get(0), cls, action, res.subList(3, res.size()));
    }
    
    public void route(final String pathSpec, final Class<? extends Controller> cls, final String action) {
        this.route(HTTP.GET, pathSpec, cls, action);
    }
    
    public void route(final String pathSpec, final Class<? extends Controller> cls) {
        final List<String> res = parseRoute(pathSpec);
        this.router.add(HTTP.GET, res.get(0), cls, res.get(2), res.subList(3, res.size()));
    }
    
    static List<String> parseRoute(final String pathSpec) {
        final List<String> result = (List<String>)Lists.newArrayList();
        result.add(getPrefix(Preconditions.checkNotNull(pathSpec, (Object)"pathSpec")));
        final Iterable<String> parts = WebApp.pathSplitter.split(pathSpec);
        String controller = null;
        String action = null;
        for (final String s : parts) {
            if (controller == null) {
                if (s.charAt(0) == ':') {
                    controller = "default";
                    result.add(controller);
                    action = "index";
                    result.add(action);
                }
                else {
                    controller = s;
                }
            }
            else if (action == null) {
                if (s.charAt(0) == ':') {
                    action = "index";
                    result.add(action);
                }
                else {
                    action = s;
                }
            }
            result.add(s);
        }
        if (controller == null) {
            result.add("default");
        }
        if (action == null) {
            result.add("index");
        }
        return result;
    }
    
    static String getPrefix(final String pathSpec) {
        int start;
        for (start = 0; CharMatcher.WHITESPACE.matches(pathSpec.charAt(start)); ++start) {}
        if (pathSpec.charAt(start) != '/') {
            throw new WebAppException("Path spec syntax error: " + pathSpec);
        }
        int ci = pathSpec.indexOf(58);
        if (ci == -1) {
            ci = pathSpec.length();
        }
        if (ci == 1) {
            return "/";
        }
        char c;
        do {
            c = pathSpec.charAt(--ci);
        } while (c == '/' || CharMatcher.WHITESPACE.matches(c));
        return pathSpec.substring(start, ci + 1);
    }
    
    public abstract void setup();
    
    static {
        LOG = LoggerFactory.getLogger(WebApp.class);
        pathSplitter = Splitter.on('/').trimResults().omitEmptyStrings();
    }
    
    public enum HTTP
    {
        GET, 
        POST, 
        HEAD, 
        PUT, 
        DELETE;
    }
}
