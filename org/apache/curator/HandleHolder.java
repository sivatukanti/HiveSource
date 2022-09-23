// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.zookeeper.Watcher;
import org.apache.curator.utils.ZookeeperFactory;

class HandleHolder
{
    private final ZookeeperFactory zookeeperFactory;
    private final Watcher watcher;
    private final EnsembleProvider ensembleProvider;
    private final int sessionTimeout;
    private final boolean canBeReadOnly;
    private volatile Helper helper;
    
    HandleHolder(final ZookeeperFactory zookeeperFactory, final Watcher watcher, final EnsembleProvider ensembleProvider, final int sessionTimeout, final boolean canBeReadOnly) {
        this.zookeeperFactory = zookeeperFactory;
        this.watcher = watcher;
        this.ensembleProvider = ensembleProvider;
        this.sessionTimeout = sessionTimeout;
        this.canBeReadOnly = canBeReadOnly;
    }
    
    ZooKeeper getZooKeeper() throws Exception {
        return (this.helper != null) ? this.helper.getZooKeeper() : null;
    }
    
    String getConnectionString() {
        return (this.helper != null) ? this.helper.getConnectionString() : null;
    }
    
    boolean hasNewConnectionString() {
        final String helperConnectionString = (this.helper != null) ? this.helper.getConnectionString() : null;
        return helperConnectionString != null && !this.ensembleProvider.getConnectionString().equals(helperConnectionString);
    }
    
    void closeAndClear() throws Exception {
        this.internalClose();
        this.helper = null;
    }
    
    void closeAndReset() throws Exception {
        this.internalClose();
        this.helper = new Helper() {
            private volatile ZooKeeper zooKeeperHandle = null;
            private volatile String connectionString = null;
            
            @Override
            public ZooKeeper getZooKeeper() throws Exception {
                synchronized (this) {
                    if (this.zooKeeperHandle == null) {
                        this.connectionString = HandleHolder.this.ensembleProvider.getConnectionString();
                        this.zooKeeperHandle = HandleHolder.this.zookeeperFactory.newZooKeeper(this.connectionString, HandleHolder.this.sessionTimeout, HandleHolder.this.watcher, HandleHolder.this.canBeReadOnly);
                    }
                    HandleHolder.this.helper = new Helper() {
                        @Override
                        public ZooKeeper getZooKeeper() throws Exception {
                            return Helper.this.zooKeeperHandle;
                        }
                        
                        @Override
                        public String getConnectionString() {
                            return Helper.this.connectionString;
                        }
                    };
                    return this.zooKeeperHandle;
                }
            }
            
            @Override
            public String getConnectionString() {
                return this.connectionString;
            }
        };
    }
    
    private void internalClose() throws Exception {
        try {
            final ZooKeeper zooKeeper = (this.helper != null) ? this.helper.getZooKeeper() : null;
            if (zooKeeper != null) {
                final Watcher dummyWatcher = new Watcher() {
                    @Override
                    public void process(final WatchedEvent event) {
                    }
                };
                zooKeeper.register(dummyWatcher);
                zooKeeper.close();
            }
        }
        catch (InterruptedException dummy) {
            Thread.currentThread().interrupt();
        }
    }
    
    private interface Helper
    {
        ZooKeeper getZooKeeper() throws Exception;
        
        String getConnectionString();
    }
}
