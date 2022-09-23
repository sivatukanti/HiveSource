// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.client;

import org.apache.zookeeper.common.PathUtils;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public final class ConnectStringParser
{
    private static final int DEFAULT_PORT = 2181;
    private final String chrootPath;
    private final ArrayList<InetSocketAddress> serverAddresses;
    
    public ConnectStringParser(String connectString) {
        this.serverAddresses = new ArrayList<InetSocketAddress>();
        final int off = connectString.indexOf(47);
        if (off >= 0) {
            final String chrootPath = connectString.substring(off);
            if (chrootPath.length() == 1) {
                this.chrootPath = null;
            }
            else {
                PathUtils.validatePath(chrootPath);
                this.chrootPath = chrootPath;
            }
            connectString = connectString.substring(0, off);
        }
        else {
            this.chrootPath = null;
        }
        final String[] split;
        final String[] hostsList = split = connectString.split(",");
        for (String host : split) {
            int port = 2181;
            final int pidx = host.lastIndexOf(58);
            if (pidx >= 0) {
                if (pidx < host.length() - 1) {
                    port = Integer.parseInt(host.substring(pidx + 1));
                }
                host = host.substring(0, pidx);
            }
            this.serverAddresses.add(InetSocketAddress.createUnresolved(host, port));
        }
    }
    
    public String getChrootPath() {
        return this.chrootPath;
    }
    
    public ArrayList<InetSocketAddress> getServerAddresses() {
        return this.serverAddresses;
    }
}
