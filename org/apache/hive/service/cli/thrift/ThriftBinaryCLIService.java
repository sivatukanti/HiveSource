// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.thrift;

import java.util.List;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.transport.TTransportFactory;
import java.util.concurrent.ExecutorService;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.ArrayList;
import org.apache.hive.service.auth.HiveAuthFactory;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.hive.service.server.ThreadFactoryWithGarbageCleanup;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.hive.service.cli.CLIService;

public class ThriftBinaryCLIService extends ThriftCLIService
{
    public ThriftBinaryCLIService(final CLIService cliService) {
        super(cliService, ThriftBinaryCLIService.class.getSimpleName());
    }
    
    @Override
    public void run() {
        try {
            final String threadPoolName = "HiveServer2-Handler-Pool";
            final ExecutorService executorService = new ThreadPoolExecutor(this.minWorkerThreads, this.maxWorkerThreads, this.workerKeepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryWithGarbageCleanup(threadPoolName));
            ThriftBinaryCLIService.hiveAuthFactory = new HiveAuthFactory(this.hiveConf);
            final TTransportFactory transportFactory = ThriftBinaryCLIService.hiveAuthFactory.getAuthTransFactory();
            final TProcessorFactory processorFactory = ThriftBinaryCLIService.hiveAuthFactory.getAuthProcFactory(this);
            TServerSocket serverSocket = null;
            final List<String> sslVersionBlacklist = new ArrayList<String>();
            for (final String sslVersion : this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SSL_PROTOCOL_BLACKLIST).split(",")) {
                sslVersionBlacklist.add(sslVersion);
            }
            if (!this.hiveConf.getBoolVar(HiveConf.ConfVars.HIVE_SERVER2_USE_SSL)) {
                serverSocket = HiveAuthFactory.getServerSocket(this.hiveHost, this.portNum);
            }
            else {
                final String keyStorePath = this.hiveConf.getVar(HiveConf.ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PATH).trim();
                if (keyStorePath.isEmpty()) {
                    throw new IllegalArgumentException(HiveConf.ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PATH.varname + " Not configured for SSL connection");
                }
                final String keyStorePassword = ShimLoader.getHadoopShims().getPassword(this.hiveConf, HiveConf.ConfVars.HIVE_SERVER2_SSL_KEYSTORE_PASSWORD.varname);
                serverSocket = HiveAuthFactory.getServerSSLSocket(this.hiveHost, this.portNum, keyStorePath, keyStorePassword, sslVersionBlacklist);
            }
            final int maxMessageSize = this.hiveConf.getIntVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_MAX_MESSAGE_SIZE);
            final int requestTimeout = (int)this.hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_LOGIN_TIMEOUT, TimeUnit.SECONDS);
            final int beBackoffSlotLength = (int)this.hiveConf.getTimeVar(HiveConf.ConfVars.HIVE_SERVER2_THRIFT_LOGIN_BEBACKOFF_SLOT_LENGTH, TimeUnit.MILLISECONDS);
            final TThreadPoolServer.Args sargs = new TThreadPoolServer.Args(serverSocket).processorFactory(processorFactory).transportFactory(transportFactory).protocolFactory(new TBinaryProtocol.Factory()).inputProtocolFactory(new TBinaryProtocol.Factory(true, true, maxMessageSize, maxMessageSize)).requestTimeout(requestTimeout).requestTimeoutUnit(TimeUnit.SECONDS).beBackoffSlotLength(beBackoffSlotLength).beBackoffSlotLengthUnit(TimeUnit.MILLISECONDS).executorService(executorService);
            (this.server = new TThreadPoolServer(sargs)).setServerEventHandler(this.serverEventHandler);
            final String msg = "Starting " + ThriftBinaryCLIService.class.getSimpleName() + " on port " + this.portNum + " with " + this.minWorkerThreads + "..." + this.maxWorkerThreads + " worker threads";
            ThriftBinaryCLIService.LOG.info(msg);
            this.server.serve();
        }
        catch (Throwable t) {
            ThriftBinaryCLIService.LOG.fatal("Error starting HiveServer2: could not start " + ThriftBinaryCLIService.class.getSimpleName(), t);
            System.exit(-1);
        }
    }
}
