// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.webproxy;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.YarnUncaughtExceptionHandler;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.service.Service;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.CompositeService;

public class WebAppProxyServer extends CompositeService
{
    public static final int SHUTDOWN_HOOK_PRIORITY = 30;
    private static final Log LOG;
    private WebAppProxy proxy;
    
    public WebAppProxyServer() {
        super(WebAppProxyServer.class.getName());
        this.proxy = null;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        final Configuration config = new YarnConfiguration(conf);
        try {
            this.doSecureLogin(conf);
        }
        catch (IOException ie) {
            throw new YarnRuntimeException("Proxy Server Failed to login", ie);
        }
        this.addService(this.proxy = new WebAppProxy());
        super.serviceInit(config);
    }
    
    protected void doSecureLogin(final Configuration conf) throws IOException {
        final InetSocketAddress socAddr = getBindAddress(conf);
        SecurityUtil.login(conf, "yarn.web-proxy.keytab", "yarn.web-proxy.principal", socAddr.getHostName());
    }
    
    public static InetSocketAddress getBindAddress(final Configuration conf) {
        return conf.getSocketAddr("yarn.web-proxy.address", "0.0.0.0:9099", 9099);
    }
    
    public static void main(final String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new YarnUncaughtExceptionHandler());
        StringUtils.startupShutdownMessage(WebAppProxyServer.class, args, WebAppProxyServer.LOG);
        try {
            final YarnConfiguration configuration = new YarnConfiguration();
            final WebAppProxyServer proxyServer = startServer(configuration);
            proxyServer.proxy.join();
        }
        catch (Throwable t) {
            WebAppProxyServer.LOG.fatal("Error starting Proxy server", t);
            System.exit(-1);
        }
    }
    
    protected static WebAppProxyServer startServer(final Configuration configuration) throws Exception {
        final WebAppProxyServer proxy = new WebAppProxyServer();
        ShutdownHookManager.get().addShutdownHook(new CompositeServiceShutdownHook(proxy), 30);
        proxy.init(configuration);
        proxy.start();
        return proxy;
    }
    
    static {
        LOG = LogFactory.getLog(WebAppProxyServer.class);
    }
}
