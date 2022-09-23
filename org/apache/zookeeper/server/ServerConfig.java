// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import java.util.Arrays;
import java.net.InetSocketAddress;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class ServerConfig
{
    protected InetSocketAddress clientPortAddress;
    protected String dataDir;
    protected String dataLogDir;
    protected int tickTime;
    protected int maxClientCnxns;
    protected int minSessionTimeout;
    protected int maxSessionTimeout;
    
    public ServerConfig() {
        this.tickTime = 3000;
        this.minSessionTimeout = -1;
        this.maxSessionTimeout = -1;
    }
    
    public void parse(final String[] args) {
        if (args.length < 2 || args.length > 4) {
            throw new IllegalArgumentException("Invalid number of arguments:" + Arrays.toString(args));
        }
        this.clientPortAddress = new InetSocketAddress(Integer.parseInt(args[0]));
        this.dataDir = args[1];
        this.dataLogDir = this.dataDir;
        if (args.length >= 3) {
            this.tickTime = Integer.parseInt(args[2]);
        }
        if (args.length == 4) {
            this.maxClientCnxns = Integer.parseInt(args[3]);
        }
    }
    
    public void parse(final String path) throws QuorumPeerConfig.ConfigException {
        final QuorumPeerConfig config = new QuorumPeerConfig();
        config.parse(path);
        this.readFrom(config);
    }
    
    public void readFrom(final QuorumPeerConfig config) {
        this.clientPortAddress = config.getClientPortAddress();
        this.dataDir = config.getDataDir();
        this.dataLogDir = config.getDataLogDir();
        this.tickTime = config.getTickTime();
        this.maxClientCnxns = config.getMaxClientCnxns();
        this.minSessionTimeout = config.getMinSessionTimeout();
        this.maxSessionTimeout = config.getMaxSessionTimeout();
    }
    
    public InetSocketAddress getClientPortAddress() {
        return this.clientPortAddress;
    }
    
    public String getDataDir() {
        return this.dataDir;
    }
    
    public String getDataLogDir() {
        return this.dataLogDir;
    }
    
    public int getTickTime() {
        return this.tickTime;
    }
    
    public int getMaxClientCnxns() {
        return this.maxClientCnxns;
    }
    
    public int getMinSessionTimeout() {
        return this.minSessionTimeout;
    }
    
    public int getMaxSessionTimeout() {
        return this.maxSessionTimeout;
    }
}
