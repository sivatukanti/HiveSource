// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.CuratorFrameworkFactory;
import java.util.Random;
import org.apache.commons.logging.Log;

public class ZooKeeperHiveClientHelper
{
    public static final Log LOG;
    
    static String getNextServerUriFromZooKeeper(final Utils.JdbcConnectionParams connParams) throws ZooKeeperHiveClientException {
        final String zooKeeperEnsemble = connParams.getZooKeeperEnsemble();
        String zooKeeperNamespace = connParams.getSessionVars().get("zooKeeperNamespace");
        if (zooKeeperNamespace == null || zooKeeperNamespace.isEmpty()) {
            zooKeeperNamespace = "hiveserver2";
        }
        final Random randomizer = new Random();
        final CuratorFramework zooKeeperClient = CuratorFrameworkFactory.builder().connectString(zooKeeperEnsemble).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        try {
            zooKeeperClient.start();
            final List<String> serverHosts = zooKeeperClient.getChildren().forPath("/" + zooKeeperNamespace);
            serverHosts.removeAll(connParams.getRejectedHostZnodePaths());
            if (serverHosts.isEmpty()) {
                throw new ZooKeeperHiveClientException("Tried all existing HiveServer2 uris from ZooKeeper.");
            }
            final String serverNode = serverHosts.get(randomizer.nextInt(serverHosts.size()));
            connParams.setCurrentHostZnodePath(serverNode);
            final String serverUri = new String(zooKeeperClient.getData().forPath("/" + zooKeeperNamespace + "/" + serverNode), Charset.forName("UTF-8"));
            ZooKeeperHiveClientHelper.LOG.info("Selected HiveServer2 instance with uri: " + serverUri);
            return serverUri;
        }
        catch (Exception e) {
            throw new ZooKeeperHiveClientException("Unable to read HiveServer2 uri from ZooKeeper", e);
        }
        finally {
            if (zooKeeperClient != null) {
                zooKeeperClient.close();
            }
        }
    }
    
    static {
        LOG = LogFactory.getLog(ZooKeeperHiveClientHelper.class.getName());
    }
    
    public static class DummyWatcher implements Watcher
    {
        @Override
        public void process(final WatchedEvent event) {
        }
    }
}
