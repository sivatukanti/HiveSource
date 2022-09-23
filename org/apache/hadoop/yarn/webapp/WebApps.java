// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import com.google.inject.Injector;
import java.util.Iterator;
import java.util.List;
import com.google.inject.Guice;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import java.io.IOException;
import org.mortbay.jetty.servlet.Context;
import com.google.inject.servlet.GuiceFilter;
import java.util.Map;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.security.AdminACLsManager;
import java.net.URI;
import org.apache.hadoop.http.HttpServer2;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.hadoop.http.HttpConfig;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class WebApps
{
    static final Logger LOG;
    
    public static <T> Builder<T> $for(final String prefix, final Class<T> api, final T app, final String wsPrefix) {
        return new Builder<T>(prefix, api, app, wsPrefix);
    }
    
    public static <T> Builder<T> $for(final String prefix, final Class<T> api, final T app) {
        return new Builder<T>(prefix, api, app);
    }
    
    public static <T> Builder<T> $for(final String prefix, final T app) {
        return $for(prefix, app.getClass(), app);
    }
    
    public static <T> Builder<T> $for(final T app) {
        return $for("", app);
    }
    
    public static <T> Builder<T> $for(final String prefix) {
        return $for(prefix, null, (T)null);
    }
    
    static {
        LOG = LoggerFactory.getLogger(WebApps.class);
    }
    
    public static class Builder<T>
    {
        final String name;
        final String wsName;
        final Class<T> api;
        final T application;
        String bindAddress;
        int port;
        boolean findPort;
        Configuration conf;
        HttpConfig.Policy httpPolicy;
        boolean devMode;
        private String spnegoPrincipalKey;
        private String spnegoKeytabKey;
        private final HashSet<ServletStruct> servlets;
        private final HashMap<String, Object> attributes;
        
        Builder(final String name, final Class<T> api, final T application, final String wsName) {
            this.bindAddress = "0.0.0.0";
            this.port = 0;
            this.findPort = false;
            this.httpPolicy = null;
            this.devMode = false;
            this.servlets = new HashSet<ServletStruct>();
            this.attributes = new HashMap<String, Object>();
            this.name = name;
            this.api = api;
            this.application = application;
            this.wsName = wsName;
        }
        
        Builder(final String name, final Class<T> api, final T application) {
            this(name, (Class<Object>)api, application, null);
        }
        
        public Builder<T> at(final String bindAddress) {
            final String[] parts = StringUtils.split(bindAddress, ':');
            if (parts.length == 2) {
                final int port = Integer.parseInt(parts[1]);
                return this.at(parts[0], port, port == 0);
            }
            return this.at(bindAddress, 0, true);
        }
        
        public Builder<T> at(final int port) {
            return this.at("0.0.0.0", port, port == 0);
        }
        
        public Builder<T> at(final String address, final int port, final boolean findPort) {
            this.bindAddress = Preconditions.checkNotNull(address, (Object)"bind address");
            this.port = port;
            this.findPort = findPort;
            return this;
        }
        
        public Builder<T> withAttribute(final String key, final Object value) {
            this.attributes.put(key, value);
            return this;
        }
        
        public Builder<T> withServlet(final String name, final String pathSpec, final Class<? extends HttpServlet> servlet) {
            final ServletStruct struct = new ServletStruct();
            struct.clazz = servlet;
            struct.name = name;
            struct.spec = pathSpec;
            this.servlets.add(struct);
            return this;
        }
        
        public Builder<T> with(final Configuration conf) {
            this.conf = conf;
            return this;
        }
        
        public Builder<T> withHttpPolicy(final Configuration conf, final HttpConfig.Policy httpPolicy) {
            this.conf = conf;
            this.httpPolicy = httpPolicy;
            return this;
        }
        
        public Builder<T> withHttpSpnegoPrincipalKey(final String spnegoPrincipalKey) {
            this.spnegoPrincipalKey = spnegoPrincipalKey;
            return this;
        }
        
        public Builder<T> withHttpSpnegoKeytabKey(final String spnegoKeytabKey) {
            this.spnegoKeytabKey = spnegoKeytabKey;
            return this;
        }
        
        public Builder<T> inDevMode() {
            this.devMode = true;
            return this;
        }
        
        public WebApp start(WebApp webapp) {
            if (webapp == null) {
                webapp = new WebApp() {
                    @Override
                    public void setup() {
                    }
                };
            }
            webapp.setName(this.name);
            webapp.setWebServices(this.wsName);
            final String basePath = "/" + this.name;
            webapp.setRedirectPath(basePath);
            final List<String> pathList = new ArrayList<String>();
            if (basePath.equals("/")) {
                webapp.addServePathSpec("/*");
                pathList.add("/*");
            }
            else {
                webapp.addServePathSpec(basePath);
                webapp.addServePathSpec(basePath + "/*");
                pathList.add(basePath + "/*");
            }
            if (this.wsName != null && !this.wsName.equals(basePath)) {
                if (this.wsName.equals("/")) {
                    webapp.addServePathSpec("/*");
                    pathList.add("/*");
                }
                else {
                    webapp.addServePathSpec("/" + this.wsName);
                    webapp.addServePathSpec("/" + this.wsName + "/*");
                    pathList.add("/" + this.wsName + "/*");
                }
            }
            if (this.conf == null) {
                this.conf = new Configuration();
            }
            try {
                if (this.application != null) {
                    webapp.setHostClass(this.application.getClass());
                }
                else {
                    final String cls = this.inferHostClass();
                    WebApps.LOG.debug("setting webapp host class to {}", cls);
                    webapp.setHostClass(Class.forName(cls));
                }
                if (this.devMode) {
                    if (this.port > 0) {
                        try {
                            new URL("http://localhost:" + this.port + "/__stop").getContent();
                            WebApps.LOG.info("stopping existing webapp instance");
                            Thread.sleep(100L);
                        }
                        catch (ConnectException e) {
                            WebApps.LOG.info("no existing webapp instance found: {}", e.toString());
                        }
                        catch (Exception e2) {
                            WebApps.LOG.warn("error stopping existing instance: {}", e2.toString());
                        }
                    }
                    else {
                        WebApps.LOG.error("dev mode does NOT work with ephemeral port!");
                        System.exit(1);
                    }
                }
                String httpScheme;
                if (this.httpPolicy == null) {
                    httpScheme = WebAppUtils.getHttpSchemePrefix(this.conf);
                }
                else {
                    httpScheme = ((this.httpPolicy == HttpConfig.Policy.HTTPS_ONLY) ? "https://" : "http://");
                }
                final HttpServer2.Builder builder = new HttpServer2.Builder().setName(this.name).addEndpoint(URI.create(httpScheme + this.bindAddress + ":" + this.port)).setConf(this.conf).setFindPort(this.findPort).setACL(new AdminACLsManager(this.conf).getAdminAcl()).setPathSpec(pathList.toArray(new String[0]));
                final boolean hasSpnegoConf = this.spnegoPrincipalKey != null && this.conf.get(this.spnegoPrincipalKey) != null && this.spnegoKeytabKey != null && this.conf.get(this.spnegoKeytabKey) != null;
                if (hasSpnegoConf) {
                    builder.setUsernameConfKey(this.spnegoPrincipalKey).setKeytabConfKey(this.spnegoKeytabKey).setSecurityEnabled(UserGroupInformation.isSecurityEnabled());
                }
                if (httpScheme.equals("https://")) {
                    WebAppUtils.loadSslConfiguration(builder);
                }
                final HttpServer2 server = builder.build();
                for (final ServletStruct struct : this.servlets) {
                    server.addServlet(struct.name, struct.spec, struct.clazz);
                }
                for (final Map.Entry<String, Object> entry : this.attributes.entrySet()) {
                    server.setAttribute(entry.getKey(), entry.getValue());
                }
                HttpServer2.defineFilter((Context)server.getWebAppContext(), "guice", GuiceFilter.class.getName(), (Map)null, new String[] { "/*" });
                webapp.setConf(this.conf);
                webapp.setHttpServer(server);
                server.start();
                WebApps.LOG.info("Web app /" + this.name + " started at " + server.getConnectorAddress(0).getPort());
            }
            catch (ClassNotFoundException e3) {
                throw new WebAppException("Error starting http server", e3);
            }
            catch (IOException e4) {
                throw new WebAppException("Error starting http server", e4);
            }
            final Injector injector = Guice.createInjector(webapp, new AbstractModule() {
                @Override
                protected void configure() {
                    if (Builder.this.api != null) {
                        this.bind(Builder.this.api).toInstance(Builder.this.application);
                    }
                }
            });
            WebApps.LOG.info("Registered webapp guice modules");
            webapp.setGuiceFilter(injector.getInstance(GuiceFilter.class));
            if (this.devMode) {
                injector.getInstance(Dispatcher.class).setDevMode(this.devMode);
                WebApps.LOG.info("in dev mode!");
            }
            return webapp;
        }
        
        public WebApp start() {
            return this.start(null);
        }
        
        private String inferHostClass() {
            final String thisClass = this.getClass().getName();
            final Throwable t = new Throwable();
            for (final StackTraceElement e : t.getStackTrace()) {
                if (!e.getClassName().equals(thisClass)) {
                    return e.getClassName();
                }
            }
            WebApps.LOG.warn("could not infer host class from", t);
            return thisClass;
        }
        
        static class ServletStruct
        {
            public Class<? extends HttpServlet> clazz;
            public String name;
            public String spec;
        }
    }
}
