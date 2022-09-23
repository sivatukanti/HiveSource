// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.conf.Configuration;
import java.util.HashMap;
import javax.net.SocketFactory;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class ClientCache
{
    private Map<SocketFactory, Client> clients;
    
    public ClientCache() {
        this.clients = new HashMap<SocketFactory, Client>();
    }
    
    public synchronized Client getClient(final Configuration conf, final SocketFactory factory, final Class<? extends Writable> valueClass) {
        Client client = this.clients.get(factory);
        if (client == null) {
            client = new Client(valueClass, conf, factory);
            this.clients.put(factory, client);
        }
        else {
            client.incCount();
        }
        if (Client.LOG.isDebugEnabled()) {
            Client.LOG.debug("getting client out of cache: " + client);
        }
        return client;
    }
    
    public synchronized Client getClient(final Configuration conf) {
        return this.getClient(conf, SocketFactory.getDefault(), ObjectWritable.class);
    }
    
    public synchronized Client getClient(final Configuration conf, final SocketFactory factory) {
        return this.getClient(conf, factory, ObjectWritable.class);
    }
    
    public void stopClient(final Client client) {
        if (Client.LOG.isDebugEnabled()) {
            Client.LOG.debug("stopping client from cache: " + client);
        }
        synchronized (this) {
            client.decCount();
            if (client.isZeroReference()) {
                if (Client.LOG.isDebugEnabled()) {
                    Client.LOG.debug("removing client from cache: " + client);
                }
                this.clients.remove(client.getSocketFactory());
            }
        }
        if (client.isZeroReference()) {
            if (Client.LOG.isDebugEnabled()) {
                Client.LOG.debug("stopping actual client because no more references remain: " + client);
            }
            client.stop();
        }
    }
}
