// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.sftp;

import org.slf4j.LoggerFactory;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSchException;
import org.apache.hadoop.util.StringUtils;
import java.util.Properties;
import com.jcraft.jsch.JSch;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import com.jcraft.jsch.ChannelSftp;
import java.util.HashSet;
import java.util.HashMap;
import org.slf4j.Logger;

class SFTPConnectionPool
{
    public static final Logger LOG;
    private int maxConnection;
    private int liveConnectionCount;
    private HashMap<ConnectionInfo, HashSet<ChannelSftp>> idleConnections;
    private HashMap<ChannelSftp, ConnectionInfo> con2infoMap;
    
    SFTPConnectionPool(final int maxConnection) {
        this.liveConnectionCount = 0;
        this.idleConnections = new HashMap<ConnectionInfo, HashSet<ChannelSftp>>();
        this.con2infoMap = new HashMap<ChannelSftp, ConnectionInfo>();
        this.maxConnection = maxConnection;
    }
    
    synchronized ChannelSftp getFromPool(final ConnectionInfo info) throws IOException {
        final Set<ChannelSftp> cons = this.idleConnections.get(info);
        if (cons == null || cons.size() <= 0) {
            return null;
        }
        final Iterator<ChannelSftp> it = cons.iterator();
        if (it.hasNext()) {
            final ChannelSftp channel = it.next();
            this.idleConnections.remove(info);
            return channel;
        }
        throw new IOException("Connection pool error.");
    }
    
    synchronized void returnToPool(final ChannelSftp channel) {
        final ConnectionInfo info = this.con2infoMap.get(channel);
        HashSet<ChannelSftp> cons = this.idleConnections.get(info);
        if (cons == null) {
            cons = new HashSet<ChannelSftp>();
            this.idleConnections.put(info, cons);
        }
        cons.add(channel);
    }
    
    synchronized void shutdown() {
        if (this.con2infoMap == null) {
            return;
        }
        SFTPConnectionPool.LOG.info("Inside shutdown, con2infoMap size=" + this.con2infoMap.size());
        this.maxConnection = 0;
        final Set<ChannelSftp> cons = this.con2infoMap.keySet();
        if (cons != null && cons.size() > 0) {
            final Set<ChannelSftp> copy = new HashSet<ChannelSftp>(cons);
            for (final ChannelSftp con : copy) {
                try {
                    this.disconnect(con);
                }
                catch (IOException ioe) {
                    final ConnectionInfo info = this.con2infoMap.get(con);
                    SFTPConnectionPool.LOG.error("Error encountered while closing connection to " + info.getHost(), ioe);
                }
            }
        }
        this.idleConnections = null;
        this.con2infoMap = null;
    }
    
    public synchronized int getMaxConnection() {
        return this.maxConnection;
    }
    
    public synchronized void setMaxConnection(final int maxConn) {
        this.maxConnection = maxConn;
    }
    
    public ChannelSftp connect(final String host, final int port, String user, String password, final String keyFile) throws IOException {
        final ConnectionInfo info = new ConnectionInfo(host, port, user);
        ChannelSftp channel = this.getFromPool(info);
        if (channel != null) {
            if (channel.isConnected()) {
                return channel;
            }
            channel = null;
            synchronized (this) {
                --this.liveConnectionCount;
                this.con2infoMap.remove(channel);
            }
        }
        final JSch jsch = new JSch();
        Session session = null;
        try {
            if (user == null || user.length() == 0) {
                user = System.getProperty("user.name");
            }
            if (password == null) {
                password = "";
            }
            if (keyFile != null && keyFile.length() > 0) {
                jsch.addIdentity(keyFile);
            }
            if (port <= 0) {
                session = jsch.getSession(user, host);
            }
            else {
                session = jsch.getSession(user, host, port);
            }
            session.setPassword(password);
            final Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = (ChannelSftp)session.openChannel("sftp");
            channel.connect();
            synchronized (this) {
                this.con2infoMap.put(channel, info);
                ++this.liveConnectionCount;
            }
            return channel;
        }
        catch (JSchException e) {
            throw new IOException(StringUtils.stringifyException(e));
        }
    }
    
    void disconnect(final ChannelSftp channel) throws IOException {
        if (channel != null) {
            boolean closeConnection = false;
            synchronized (this) {
                if (this.liveConnectionCount > this.maxConnection) {
                    --this.liveConnectionCount;
                    this.con2infoMap.remove(channel);
                    closeConnection = true;
                }
            }
            if (closeConnection) {
                if (!channel.isConnected()) {
                    return;
                }
                try {
                    final Session session = channel.getSession();
                    channel.disconnect();
                    session.disconnect();
                    return;
                }
                catch (JSchException e) {
                    throw new IOException(StringUtils.stringifyException(e));
                }
            }
            this.returnToPool(channel);
        }
    }
    
    public int getIdleCount() {
        return this.idleConnections.size();
    }
    
    public int getLiveConnCount() {
        return this.liveConnectionCount;
    }
    
    public int getConnPoolSize() {
        return this.con2infoMap.size();
    }
    
    static {
        LOG = LoggerFactory.getLogger(SFTPFileSystem.class);
    }
    
    static class ConnectionInfo
    {
        private String host;
        private int port;
        private String user;
        
        ConnectionInfo(final String hst, final int prt, final String usr) {
            this.host = "";
            this.user = "";
            this.host = hst;
            this.port = prt;
            this.user = usr;
        }
        
        public String getHost() {
            return this.host;
        }
        
        public void setHost(final String hst) {
            this.host = hst;
        }
        
        public int getPort() {
            return this.port;
        }
        
        public void setPort(final int prt) {
            this.port = prt;
        }
        
        public String getUser() {
            return this.user;
        }
        
        public void setUser(final String usr) {
            this.user = usr;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof ConnectionInfo) {
                final ConnectionInfo con = (ConnectionInfo)obj;
                boolean ret = true;
                if (this.host == null || !this.host.equalsIgnoreCase(con.host)) {
                    ret = false;
                }
                if (this.port >= 0 && this.port != con.port) {
                    ret = false;
                }
                if (this.user == null || !this.user.equalsIgnoreCase(con.user)) {
                    ret = false;
                }
                return ret;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            if (this.host != null) {
                hashCode += this.host.hashCode();
            }
            hashCode += this.port;
            if (this.user != null) {
                hashCode += this.user.hashCode();
            }
            return hashCode;
        }
    }
}
