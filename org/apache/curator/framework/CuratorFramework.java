// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework;

import java.util.concurrent.TimeUnit;
import org.apache.zookeeper.Watcher;
import org.apache.curator.utils.EnsurePath;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.api.SyncBuilder;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.SetACLBuilder;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import java.io.Closeable;

public interface CuratorFramework extends Closeable
{
    void start();
    
    void close();
    
    CuratorFrameworkState getState();
    
    @Deprecated
    boolean isStarted();
    
    CreateBuilder create();
    
    DeleteBuilder delete();
    
    ExistsBuilder checkExists();
    
    GetDataBuilder getData();
    
    SetDataBuilder setData();
    
    GetChildrenBuilder getChildren();
    
    GetACLBuilder getACL();
    
    SetACLBuilder setACL();
    
    CuratorTransaction inTransaction();
    
    @Deprecated
    void sync(final String p0, final Object p1);
    
    void createContainers(final String p0) throws Exception;
    
    SyncBuilder sync();
    
    Listenable<ConnectionStateListener> getConnectionStateListenable();
    
    Listenable<CuratorListener> getCuratorListenable();
    
    Listenable<UnhandledErrorListener> getUnhandledErrorListenable();
    
    @Deprecated
    CuratorFramework nonNamespaceView();
    
    CuratorFramework usingNamespace(final String p0);
    
    String getNamespace();
    
    CuratorZookeeperClient getZookeeperClient();
    
    @Deprecated
    EnsurePath newNamespaceAwareEnsurePath(final String p0);
    
    void clearWatcherReferences(final Watcher p0);
    
    boolean blockUntilConnected(final int p0, final TimeUnit p1) throws InterruptedException;
    
    void blockUntilConnected() throws InterruptedException;
}
