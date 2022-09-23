// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.lib;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import java.io.IOException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZKClient
{
    private ZooKeeper zkClient;
    
    public ZKClient(final String string) throws IOException {
        this.zkClient = new ZooKeeper(string, 30000, new ZKWatcher());
    }
    
    public void registerService(final String path, final String data) throws IOException, InterruptedException {
        try {
            this.zkClient.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        }
        catch (KeeperException ke) {
            throw new IOException(ke);
        }
    }
    
    public void unregisterService(final String path) throws IOException, InterruptedException {
        try {
            this.zkClient.delete(path, -1);
        }
        catch (KeeperException ke) {
            throw new IOException(ke);
        }
    }
    
    public List<String> listServices(final String path) throws IOException, InterruptedException {
        List<String> children = null;
        try {
            children = this.zkClient.getChildren(path, false);
        }
        catch (KeeperException ke) {
            throw new IOException(ke);
        }
        return children;
    }
    
    public String getServiceData(final String path) throws IOException, InterruptedException {
        String data;
        try {
            final Stat stat = new Stat();
            final byte[] byteData = this.zkClient.getData(path, false, stat);
            data = new String(byteData);
        }
        catch (KeeperException ke) {
            throw new IOException(ke);
        }
        return data;
    }
    
    private static class ZKWatcher implements Watcher
    {
        @Override
        public void process(final WatchedEvent arg0) {
        }
    }
}
