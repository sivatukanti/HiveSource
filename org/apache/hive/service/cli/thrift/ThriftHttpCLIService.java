// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import org.apache.thrift.server.TServlet;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.TProcessor;
import java.util.concurrent.ExecutorService;
import javax.servlet.Servlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.hive.service.auth.HiveAuthFactory;
import org.eclipse.jetty.server.Connector;
import org.apache.hadoop.util.Shell;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import java.util.Arrays;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.hive.conf.HiveConf;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hive.service.server.ThreadFactoryWithGarbageCleanup;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.server.Server;
import org.apache.hive.service.cli.CLIService;

public class ThriftHttpCLIService extends ThriftCLIService
{
    public ThriftHttpCLIService(final CLIService cliService) {
        super(cliService, ThriftHttpCLIService.class.getSimpleName());
    }
    
    @Override
    public void run() {
        try {
            this.httpServer = new Server();
            final String threadPoolName = "HiveServer2-HttpHandler-Pool";
            final ExecutorService executorService = new ThreadPoolExecutor(this.minWorkerThreads, this.maxWorkerThreads, this.workerKeepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryWithGarbageCleanup(threadPoolName));
            final ExecutorThreadPool threadPool = new ExecutorThreadPool(executorService);
            this.httpServer.setThreadPool((ThreadPool)threadPool);
            SelectChannelConnector connector = new SelectChannelConnector();
            final boolean useSsl = this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_USE_SSL);
            final String schemeName = useSsl ? "https" : "http";
            if (useSsl) {
                final String keyStorePath = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PATH).trim();
                final String keyStorePassword = ShimLoader.getHadoopShims().getPassword(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PASSWORD.varname);
                if (keyStorePath.isEmpty()) {
                    throw new IllegalArgumentException(HiveConf.ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PATH.varname + " Not configured for SSL connection");
                }
                final SslContextFactory sslContextFactory = new SslContextFactory();
                final String[] excludedProtocols = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SSL_PROTOCOL_BLACKLIST).split(",");
                ThriftHttpCLIService.LOG.info("HTTP Server SSL: adding excluded protocols: " + Arrays.toString(excludedProtocols));
                sslContextFactory.addExcludeProtocols(excludedProtocols);
                ThriftHttpCLIService.LOG.info("HTTP Server SSL: SslContextFactory.getExcludeProtocols = " + Arrays.toString(sslContextFactory.getExcludeProtocols()));
                sslContextFactory.setKeyStorePath(keyStorePath);
                sslContextFactory.setKeyStorePassword(keyStorePassword);
                connector = new SslSelectChannelConnector(sslContextFactory);
            }
            connector.setPort(this.portNum);
            connector.setReuseAddress(!Shell.WINDOWS);
            final int maxIdleTime = (int)this.hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_MAX_IDLE_TIME, TimeUnit.MILLISECONDS);
            connector.setMaxIdleTime(maxIdleTime);
            this.httpServer.addConnector(connector);
            ThriftHttpCLIService.hiveAuthFactory = new HiveAuthFactory(this.hiveConf);
            final TProcessor processor = new TCLIService.Processor<Object>(this);
            final TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
            final UserGroupInformation serviceUGI = this.cliService.getServiceUGI();
            final UserGroupInformation httpUGI = this.cliService.getHttpUGI();
            final String authType = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_AUTHENTICATION);
            final TServlet thriftHttpServlet = new ThriftHttpServlet(processor, protocolFactory, authType, serviceUGI, httpUGI);
            final ServletContextHandler context = new ServletContextHandler(1);
            context.setContextPath("/");
            final String httpPath = this.getHttpPath(this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_HTTP_PATH));
            this.httpServer.setHandler(context);
            context.addServlet(new ServletHolder(thriftHttpServlet), httpPath);
            this.httpServer.start();
            final String msg = "Started " + ThriftHttpCLIService.class.getSimpleName() + " in " + schemeName + " mode on port " + this.portNum + " path=" + httpPath + " with " + this.minWorkerThreads + "..." + this.maxWorkerThreads + " worker threads";
            ThriftHttpCLIService.LOG.info(msg);
            this.httpServer.join();
        }
        catch (Throwable t) {
            ThriftHttpCLIService.LOG.fatal("Error starting HiveServer2: could not start " + ThriftHttpCLIService.class.getSimpleName(), t);
            System.exit(-1);
        }
    }
    
    private String getHttpPath(String httpPath) {
        if (httpPath == null || httpPath.equals("")) {
            httpPath = "/*";
        }
        else {
            if (!httpPath.startsWith("/")) {
                httpPath = "/" + httpPath;
            }
            if (httpPath.endsWith("/")) {
                httpPath += "*";
            }
            if (!httpPath.endsWith("/*")) {
                httpPath += "/*";
            }
        }
        return httpPath;
    }
}
