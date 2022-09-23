// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import org.slf4j.LoggerFactory;
import java.net.SocketAddress;
import org.apache.zookeeper.proto.SyncResponse;
import org.apache.zookeeper.proto.SyncRequest;
import org.apache.zookeeper.proto.GetChildren2Response;
import org.apache.zookeeper.proto.GetChildren2Request;
import org.apache.zookeeper.proto.GetChildrenResponse;
import org.apache.zookeeper.proto.GetChildrenRequest;
import org.apache.zookeeper.proto.SetACLResponse;
import org.apache.zookeeper.proto.SetACLRequest;
import org.apache.zookeeper.proto.GetACLResponse;
import org.apache.zookeeper.proto.GetACLRequest;
import org.apache.zookeeper.proto.SetDataRequest;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.proto.GetDataResponse;
import org.apache.zookeeper.proto.GetDataRequest;
import org.apache.zookeeper.proto.SetDataResponse;
import org.apache.zookeeper.proto.ExistsRequest;
import org.apache.zookeeper.data.Stat;
import java.util.Iterator;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.CreateResponse;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.proto.RequestHeader;
import org.apache.zookeeper.common.PathUtils;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.client.HostProvider;
import java.net.InetSocketAddress;
import org.apache.zookeeper.client.StaticHostProvider;
import org.apache.zookeeper.client.ConnectStringParser;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class ZooKeeper
{
    public static final String ZOOKEEPER_CLIENT_CNXN_SOCKET = "zookeeper.clientCnxnSocket";
    protected final ClientCnxn cnxn;
    private static final Logger LOG;
    private final ZKWatchManager watchManager;
    
    public ZooKeeperSaslClient getSaslClient() {
        return this.cnxn.zooKeeperSaslClient;
    }
    
    List<String> getDataWatches() {
        synchronized (this.watchManager.dataWatches) {
            final List<String> rc = new ArrayList<String>(this.watchManager.dataWatches.keySet());
            return rc;
        }
    }
    
    List<String> getExistWatches() {
        synchronized (this.watchManager.existWatches) {
            final List<String> rc = new ArrayList<String>(this.watchManager.existWatches.keySet());
            return rc;
        }
    }
    
    List<String> getChildWatches() {
        synchronized (this.watchManager.childWatches) {
            final List<String> rc = new ArrayList<String>(this.watchManager.childWatches.keySet());
            return rc;
        }
    }
    
    public ZooKeeper(final String connectString, final int sessionTimeout, final Watcher watcher) throws IOException {
        this(connectString, sessionTimeout, watcher, false);
    }
    
    public ZooKeeper(final String connectString, final int sessionTimeout, final Watcher watcher, final boolean canBeReadOnly) throws IOException {
        this.watchManager = new ZKWatchManager();
        ZooKeeper.LOG.info("Initiating client connection, connectString=" + connectString + " sessionTimeout=" + sessionTimeout + " watcher=" + watcher);
        this.watchManager.defaultWatcher = watcher;
        final ConnectStringParser connectStringParser = new ConnectStringParser(connectString);
        final HostProvider hostProvider = new StaticHostProvider(connectStringParser.getServerAddresses());
        (this.cnxn = new ClientCnxn(connectStringParser.getChrootPath(), hostProvider, sessionTimeout, this, this.watchManager, getClientCnxnSocket(), canBeReadOnly)).start();
    }
    
    public ZooKeeper(final String connectString, final int sessionTimeout, final Watcher watcher, final long sessionId, final byte[] sessionPasswd) throws IOException {
        this(connectString, sessionTimeout, watcher, sessionId, sessionPasswd, false);
    }
    
    public ZooKeeper(final String connectString, final int sessionTimeout, final Watcher watcher, final long sessionId, final byte[] sessionPasswd, final boolean canBeReadOnly) throws IOException {
        this.watchManager = new ZKWatchManager();
        ZooKeeper.LOG.info("Initiating client connection, connectString=" + connectString + " sessionTimeout=" + sessionTimeout + " watcher=" + watcher + " sessionId=" + Long.toHexString(sessionId) + " sessionPasswd=" + ((sessionPasswd == null) ? "<null>" : "<hidden>"));
        this.watchManager.defaultWatcher = watcher;
        final ConnectStringParser connectStringParser = new ConnectStringParser(connectString);
        final HostProvider hostProvider = new StaticHostProvider(connectStringParser.getServerAddresses());
        this.cnxn = new ClientCnxn(connectStringParser.getChrootPath(), hostProvider, sessionTimeout, this, this.watchManager, getClientCnxnSocket(), sessionId, sessionPasswd, canBeReadOnly);
        this.cnxn.seenRwServerBefore = true;
        this.cnxn.start();
    }
    
    public Testable getTestable() {
        return new ZooKeeperTestable(this, this.cnxn);
    }
    
    public long getSessionId() {
        return this.cnxn.getSessionId();
    }
    
    public byte[] getSessionPasswd() {
        return this.cnxn.getSessionPasswd();
    }
    
    public int getSessionTimeout() {
        return this.cnxn.getSessionTimeout();
    }
    
    public void addAuthInfo(final String scheme, final byte[] auth) {
        this.cnxn.addAuthInfo(scheme, auth);
    }
    
    public synchronized void register(final Watcher watcher) {
        this.watchManager.defaultWatcher = watcher;
    }
    
    public synchronized void close() throws InterruptedException {
        if (!this.cnxn.getState().isAlive()) {
            if (ZooKeeper.LOG.isDebugEnabled()) {
                ZooKeeper.LOG.debug("Close called on already closed client");
            }
            return;
        }
        if (ZooKeeper.LOG.isDebugEnabled()) {
            ZooKeeper.LOG.debug("Closing session: 0x" + Long.toHexString(this.getSessionId()));
        }
        try {
            this.cnxn.close();
        }
        catch (IOException e) {
            if (ZooKeeper.LOG.isDebugEnabled()) {
                ZooKeeper.LOG.debug("Ignoring unexpected exception during close", e);
            }
        }
        ZooKeeper.LOG.info("Session: 0x" + Long.toHexString(this.getSessionId()) + " closed");
    }
    
    private String prependChroot(final String clientPath) {
        if (this.cnxn.chrootPath == null) {
            return clientPath;
        }
        if (clientPath.length() == 1) {
            return this.cnxn.chrootPath;
        }
        return this.cnxn.chrootPath + clientPath;
    }
    
    public String create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath, createMode.isSequential());
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(1);
        final CreateRequest request = new CreateRequest();
        final CreateResponse response = new CreateResponse();
        request.setData(data);
        request.setFlags(createMode.toFlag());
        request.setPath(serverPath);
        if (acl != null && acl.size() == 0) {
            throw new KeeperException.InvalidACLException();
        }
        request.setAcl(acl);
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, null);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        if (this.cnxn.chrootPath == null) {
            return response.getPath();
        }
        return response.getPath().substring(this.cnxn.chrootPath.length());
    }
    
    public void create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode, final AsyncCallback.StringCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath, createMode.isSequential());
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(1);
        final CreateRequest request = new CreateRequest();
        final CreateResponse response = new CreateResponse();
        final ReplyHeader r = new ReplyHeader();
        request.setData(data);
        request.setFlags(createMode.toFlag());
        request.setPath(serverPath);
        request.setAcl(acl);
        this.cnxn.queuePacket(h, r, request, response, cb, clientPath, serverPath, ctx, null);
    }
    
    public void delete(final String path, final int version) throws InterruptedException, KeeperException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        String serverPath;
        if (clientPath.equals("/")) {
            serverPath = clientPath;
        }
        else {
            serverPath = this.prependChroot(clientPath);
        }
        final RequestHeader h = new RequestHeader();
        h.setType(2);
        final DeleteRequest request = new DeleteRequest();
        request.setPath(serverPath);
        request.setVersion(version);
        final ReplyHeader r = this.cnxn.submitRequest(h, request, null, null);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
    }
    
    public List<OpResult> multi(final Iterable<Op> ops) throws InterruptedException, KeeperException {
        for (final Op op : ops) {
            op.validate();
        }
        return this.multiInternal(this.generateMultiTransaction(ops));
    }
    
    public void multi(final Iterable<Op> ops, final AsyncCallback.MultiCallback cb, final Object ctx) {
        final List<OpResult> results = this.validatePath(ops);
        if (results.size() > 0) {
            cb.processResult(KeeperException.Code.BADARGUMENTS.intValue(), null, ctx, results);
            return;
        }
        this.multiInternal(this.generateMultiTransaction(ops), cb, ctx);
    }
    
    private List<OpResult> validatePath(final Iterable<Op> ops) {
        final List<OpResult> results = new ArrayList<OpResult>();
        boolean error = false;
        for (final Op op : ops) {
            try {
                op.validate();
            }
            catch (IllegalArgumentException iae) {
                ZooKeeper.LOG.error("IllegalArgumentException: " + iae.getMessage());
                final OpResult.ErrorResult err = new OpResult.ErrorResult(KeeperException.Code.BADARGUMENTS.intValue());
                results.add(err);
                error = true;
                continue;
            }
            catch (KeeperException ke) {
                ZooKeeper.LOG.error("KeeperException: " + ke.getMessage());
                final OpResult.ErrorResult err = new OpResult.ErrorResult(ke.code().intValue());
                results.add(err);
                error = true;
                continue;
            }
            final OpResult.ErrorResult err2 = new OpResult.ErrorResult(KeeperException.Code.RUNTIMEINCONSISTENCY.intValue());
            results.add(err2);
        }
        if (!error) {
            results.clear();
        }
        return results;
    }
    
    private MultiTransactionRecord generateMultiTransaction(final Iterable<Op> ops) {
        final List<Op> transaction = new ArrayList<Op>();
        for (final Op op : ops) {
            transaction.add(this.withRootPrefix(op));
        }
        return new MultiTransactionRecord(transaction);
    }
    
    private Op withRootPrefix(final Op op) {
        if (null != op.getPath()) {
            final String serverPath = this.prependChroot(op.getPath());
            if (!op.getPath().equals(serverPath)) {
                return op.withChroot(serverPath);
            }
        }
        return op;
    }
    
    protected void multiInternal(final MultiTransactionRecord request, final AsyncCallback.MultiCallback cb, final Object ctx) {
        final RequestHeader h = new RequestHeader();
        h.setType(14);
        final MultiResponse response = new MultiResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, null, null, ctx, null);
    }
    
    protected List<OpResult> multiInternal(final MultiTransactionRecord request) throws InterruptedException, KeeperException {
        final RequestHeader h = new RequestHeader();
        h.setType(14);
        final MultiResponse response = new MultiResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, null);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()));
        }
        final List<OpResult> results = response.getResultList();
        OpResult.ErrorResult fatalError = null;
        for (final OpResult result : results) {
            if (result instanceof OpResult.ErrorResult && ((OpResult.ErrorResult)result).getErr() != KeeperException.Code.OK.intValue()) {
                fatalError = (OpResult.ErrorResult)result;
                break;
            }
        }
        if (fatalError != null) {
            final KeeperException ex = KeeperException.create(KeeperException.Code.get(fatalError.getErr()));
            ex.setMultiResults(results);
            throw ex;
        }
        return results;
    }
    
    public Transaction transaction() {
        return new Transaction(this);
    }
    
    public void delete(final String path, final int version, final AsyncCallback.VoidCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        String serverPath;
        if (clientPath.equals("/")) {
            serverPath = clientPath;
        }
        else {
            serverPath = this.prependChroot(clientPath);
        }
        final RequestHeader h = new RequestHeader();
        h.setType(2);
        final DeleteRequest request = new DeleteRequest();
        request.setPath(serverPath);
        request.setVersion(version);
        this.cnxn.queuePacket(h, new ReplyHeader(), request, null, cb, clientPath, serverPath, ctx, null);
    }
    
    public Stat exists(final String path, final Watcher watcher) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new ExistsWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(3);
        final ExistsRequest request = new ExistsRequest();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final SetDataResponse response = new SetDataResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, wcb);
        if (r.getErr() == 0) {
            return (response.getStat().getCzxid() == -1L) ? null : response.getStat();
        }
        if (r.getErr() == KeeperException.Code.NONODE.intValue()) {
            return null;
        }
        throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
    }
    
    public Stat exists(final String path, final boolean watch) throws KeeperException, InterruptedException {
        return this.exists(path, watch ? this.watchManager.defaultWatcher : null);
    }
    
    public void exists(final String path, final Watcher watcher, final AsyncCallback.StatCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new ExistsWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(3);
        final ExistsRequest request = new ExistsRequest();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final SetDataResponse response = new SetDataResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, wcb);
    }
    
    public void exists(final String path, final boolean watch, final AsyncCallback.StatCallback cb, final Object ctx) {
        this.exists(path, watch ? this.watchManager.defaultWatcher : null, cb, ctx);
    }
    
    public byte[] getData(final String path, final Watcher watcher, final Stat stat) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new DataWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(4);
        final GetDataRequest request = new GetDataRequest();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final GetDataResponse response = new GetDataResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, wcb);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        if (stat != null) {
            DataTree.copyStat(response.getStat(), stat);
        }
        return response.getData();
    }
    
    public byte[] getData(final String path, final boolean watch, final Stat stat) throws KeeperException, InterruptedException {
        return this.getData(path, watch ? this.watchManager.defaultWatcher : null, stat);
    }
    
    public void getData(final String path, final Watcher watcher, final AsyncCallback.DataCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new DataWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(4);
        final GetDataRequest request = new GetDataRequest();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final GetDataResponse response = new GetDataResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, wcb);
    }
    
    public void getData(final String path, final boolean watch, final AsyncCallback.DataCallback cb, final Object ctx) {
        this.getData(path, watch ? this.watchManager.defaultWatcher : null, cb, ctx);
    }
    
    public Stat setData(final String path, final byte[] data, final int version) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(5);
        final SetDataRequest request = new SetDataRequest();
        request.setPath(serverPath);
        request.setData(data);
        request.setVersion(version);
        final SetDataResponse response = new SetDataResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, null);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        return response.getStat();
    }
    
    public void setData(final String path, final byte[] data, final int version, final AsyncCallback.StatCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(5);
        final SetDataRequest request = new SetDataRequest();
        request.setPath(serverPath);
        request.setData(data);
        request.setVersion(version);
        final SetDataResponse response = new SetDataResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, null);
    }
    
    public List<ACL> getACL(final String path, final Stat stat) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(6);
        final GetACLRequest request = new GetACLRequest();
        request.setPath(serverPath);
        final GetACLResponse response = new GetACLResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, null);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        if (stat != null) {
            DataTree.copyStat(response.getStat(), stat);
        }
        return response.getAcl();
    }
    
    public void getACL(final String path, final Stat stat, final AsyncCallback.ACLCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(6);
        final GetACLRequest request = new GetACLRequest();
        request.setPath(serverPath);
        final GetACLResponse response = new GetACLResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, null);
    }
    
    public Stat setACL(final String path, final List<ACL> acl, final int aclVersion) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(7);
        final SetACLRequest request = new SetACLRequest();
        request.setPath(serverPath);
        if (acl != null && acl.size() == 0) {
            throw new KeeperException.InvalidACLException(clientPath);
        }
        request.setAcl(acl);
        request.setVersion(aclVersion);
        final SetACLResponse response = new SetACLResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, null);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        return response.getStat();
    }
    
    public void setACL(final String path, final List<ACL> acl, final int version, final AsyncCallback.StatCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(7);
        final SetACLRequest request = new SetACLRequest();
        request.setPath(serverPath);
        request.setAcl(acl);
        request.setVersion(version);
        final SetACLResponse response = new SetACLResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, null);
    }
    
    public List<String> getChildren(final String path, final Watcher watcher) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new ChildWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(8);
        final GetChildrenRequest request = new GetChildrenRequest();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final GetChildrenResponse response = new GetChildrenResponse();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, wcb);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        return response.getChildren();
    }
    
    public List<String> getChildren(final String path, final boolean watch) throws KeeperException, InterruptedException {
        return this.getChildren(path, watch ? this.watchManager.defaultWatcher : null);
    }
    
    public void getChildren(final String path, final Watcher watcher, final AsyncCallback.ChildrenCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new ChildWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(8);
        final GetChildrenRequest request = new GetChildrenRequest();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final GetChildrenResponse response = new GetChildrenResponse();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, wcb);
    }
    
    public void getChildren(final String path, final boolean watch, final AsyncCallback.ChildrenCallback cb, final Object ctx) {
        this.getChildren(path, watch ? this.watchManager.defaultWatcher : null, cb, ctx);
    }
    
    public List<String> getChildren(final String path, final Watcher watcher, final Stat stat) throws KeeperException, InterruptedException {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new ChildWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(12);
        final GetChildren2Request request = new GetChildren2Request();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final GetChildren2Response response = new GetChildren2Response();
        final ReplyHeader r = this.cnxn.submitRequest(h, request, response, wcb);
        if (r.getErr() != 0) {
            throw KeeperException.create(KeeperException.Code.get(r.getErr()), clientPath);
        }
        if (stat != null) {
            DataTree.copyStat(response.getStat(), stat);
        }
        return response.getChildren();
    }
    
    public List<String> getChildren(final String path, final boolean watch, final Stat stat) throws KeeperException, InterruptedException {
        return this.getChildren(path, watch ? this.watchManager.defaultWatcher : null, stat);
    }
    
    public void getChildren(final String path, final Watcher watcher, final AsyncCallback.Children2Callback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        WatchRegistration wcb = null;
        if (watcher != null) {
            wcb = new ChildWatchRegistration(watcher, clientPath);
        }
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(12);
        final GetChildren2Request request = new GetChildren2Request();
        request.setPath(serverPath);
        request.setWatch(watcher != null);
        final GetChildren2Response response = new GetChildren2Response();
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, wcb);
    }
    
    public void getChildren(final String path, final boolean watch, final AsyncCallback.Children2Callback cb, final Object ctx) {
        this.getChildren(path, watch ? this.watchManager.defaultWatcher : null, cb, ctx);
    }
    
    public void sync(final String path, final AsyncCallback.VoidCallback cb, final Object ctx) {
        final String clientPath = path;
        PathUtils.validatePath(clientPath);
        final String serverPath = this.prependChroot(clientPath);
        final RequestHeader h = new RequestHeader();
        h.setType(9);
        final SyncRequest request = new SyncRequest();
        final SyncResponse response = new SyncResponse();
        request.setPath(serverPath);
        this.cnxn.queuePacket(h, new ReplyHeader(), request, response, cb, clientPath, serverPath, ctx, null);
    }
    
    public States getState() {
        return this.cnxn.getState();
    }
    
    @Override
    public String toString() {
        final States state = this.getState();
        return "State:" + state.toString() + (state.isConnected() ? (" Timeout:" + this.getSessionTimeout() + " ") : " ") + this.cnxn;
    }
    
    protected boolean testableWaitForShutdown(final int wait) throws InterruptedException {
        this.cnxn.sendThread.join(wait);
        if (this.cnxn.sendThread.isAlive()) {
            return false;
        }
        this.cnxn.eventThread.join(wait);
        return !this.cnxn.eventThread.isAlive();
    }
    
    protected SocketAddress testableRemoteSocketAddress() {
        return this.cnxn.sendThread.getClientCnxnSocket().getRemoteSocketAddress();
    }
    
    protected SocketAddress testableLocalSocketAddress() {
        return this.cnxn.sendThread.getClientCnxnSocket().getLocalSocketAddress();
    }
    
    private static ClientCnxnSocket getClientCnxnSocket() throws IOException {
        String clientCnxnSocketName = System.getProperty("zookeeper.clientCnxnSocket");
        if (clientCnxnSocketName == null) {
            clientCnxnSocketName = ClientCnxnSocketNIO.class.getName();
        }
        try {
            return (ClientCnxnSocket)Class.forName(clientCnxnSocketName).getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            final IOException ioe = new IOException("Couldn't instantiate " + clientCnxnSocketName);
            ioe.initCause(e);
            throw ioe;
        }
    }
    
    static {
        Environment.logEnv("Client environment:", LOG = LoggerFactory.getLogger(ZooKeeper.class));
    }
    
    private static class ZKWatchManager implements ClientWatchManager
    {
        private final Map<String, Set<Watcher>> dataWatches;
        private final Map<String, Set<Watcher>> existWatches;
        private final Map<String, Set<Watcher>> childWatches;
        private volatile Watcher defaultWatcher;
        
        private ZKWatchManager() {
            this.dataWatches = new HashMap<String, Set<Watcher>>();
            this.existWatches = new HashMap<String, Set<Watcher>>();
            this.childWatches = new HashMap<String, Set<Watcher>>();
        }
        
        private final void addTo(final Set<Watcher> from, final Set<Watcher> to) {
            if (from != null) {
                to.addAll(from);
            }
        }
        
        @Override
        public Set<Watcher> materialize(final Watcher.Event.KeeperState state, final Watcher.Event.EventType type, final String clientPath) {
            final Set<Watcher> result = new HashSet<Watcher>();
            switch (type) {
                case None: {
                    result.add(this.defaultWatcher);
                    final boolean clear = ClientCnxn.getDisableAutoResetWatch() && state != Watcher.Event.KeeperState.SyncConnected;
                    synchronized (this.dataWatches) {
                        for (final Set<Watcher> ws : this.dataWatches.values()) {
                            result.addAll(ws);
                        }
                        if (clear) {
                            this.dataWatches.clear();
                        }
                    }
                    synchronized (this.existWatches) {
                        for (final Set<Watcher> ws : this.existWatches.values()) {
                            result.addAll(ws);
                        }
                        if (clear) {
                            this.existWatches.clear();
                        }
                    }
                    synchronized (this.childWatches) {
                        for (final Set<Watcher> ws : this.childWatches.values()) {
                            result.addAll(ws);
                        }
                        if (clear) {
                            this.childWatches.clear();
                        }
                    }
                    return result;
                }
                case NodeDataChanged:
                case NodeCreated: {
                    synchronized (this.dataWatches) {
                        this.addTo(this.dataWatches.remove(clientPath), result);
                    }
                    synchronized (this.existWatches) {
                        this.addTo(this.existWatches.remove(clientPath), result);
                    }
                    break;
                }
                case NodeChildrenChanged: {
                    synchronized (this.childWatches) {
                        this.addTo(this.childWatches.remove(clientPath), result);
                    }
                    break;
                }
                case NodeDeleted: {
                    synchronized (this.dataWatches) {
                        this.addTo(this.dataWatches.remove(clientPath), result);
                    }
                    synchronized (this.existWatches) {
                        final Set<Watcher> list = this.existWatches.remove(clientPath);
                        if (list != null) {
                            this.addTo(list, result);
                            ZooKeeper.LOG.warn("We are triggering an exists watch for delete! Shouldn't happen!");
                        }
                    }
                    synchronized (this.childWatches) {
                        this.addTo(this.childWatches.remove(clientPath), result);
                    }
                    break;
                }
                default: {
                    final String msg = "Unhandled watch event type " + type + " with state " + state + " on path " + clientPath;
                    ZooKeeper.LOG.error(msg);
                    throw new RuntimeException(msg);
                }
            }
            return result;
        }
    }
    
    abstract class WatchRegistration
    {
        private Watcher watcher;
        private String clientPath;
        
        public WatchRegistration(final Watcher watcher, final String clientPath) {
            this.watcher = watcher;
            this.clientPath = clientPath;
        }
        
        protected abstract Map<String, Set<Watcher>> getWatches(final int p0);
        
        public void register(final int rc) {
            if (this.shouldAddWatch(rc)) {
                final Map<String, Set<Watcher>> watches = this.getWatches(rc);
                synchronized (watches) {
                    Set<Watcher> watchers = watches.get(this.clientPath);
                    if (watchers == null) {
                        watchers = new HashSet<Watcher>();
                        watches.put(this.clientPath, watchers);
                    }
                    watchers.add(this.watcher);
                }
            }
        }
        
        protected boolean shouldAddWatch(final int rc) {
            return rc == 0;
        }
    }
    
    class ExistsWatchRegistration extends WatchRegistration
    {
        public ExistsWatchRegistration(final Watcher watcher, final String clientPath) {
            super(watcher, clientPath);
        }
        
        @Override
        protected Map<String, Set<Watcher>> getWatches(final int rc) {
            return (rc == 0) ? ZooKeeper.this.watchManager.dataWatches : ZooKeeper.this.watchManager.existWatches;
        }
        
        @Override
        protected boolean shouldAddWatch(final int rc) {
            return rc == 0 || rc == KeeperException.Code.NONODE.intValue();
        }
    }
    
    class DataWatchRegistration extends WatchRegistration
    {
        public DataWatchRegistration(final Watcher watcher, final String clientPath) {
            super(watcher, clientPath);
        }
        
        @Override
        protected Map<String, Set<Watcher>> getWatches(final int rc) {
            return ZooKeeper.this.watchManager.dataWatches;
        }
    }
    
    class ChildWatchRegistration extends WatchRegistration
    {
        public ChildWatchRegistration(final Watcher watcher, final String clientPath) {
            super(watcher, clientPath);
        }
        
        @Override
        protected Map<String, Set<Watcher>> getWatches(final int rc) {
            return ZooKeeper.this.watchManager.childWatches;
        }
    }
    
    @InterfaceAudience.Public
    public enum States
    {
        CONNECTING, 
        ASSOCIATING, 
        CONNECTED, 
        CONNECTEDREADONLY, 
        CLOSED, 
        AUTH_FAILED, 
        NOT_CONNECTED;
        
        public boolean isAlive() {
            return this != States.CLOSED && this != States.AUTH_FAILED;
        }
        
        public boolean isConnected() {
            return this == States.CONNECTED || this == States.CONNECTEDREADONLY;
        }
    }
}
