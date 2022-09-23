// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import java.util.LinkedList;
import org.apache.zookeeper.txn.MultiTxn;
import java.nio.ByteBuffer;
import org.apache.jute.OutputArchive;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.Txn;
import java.util.ArrayList;
import org.apache.zookeeper.common.PathUtils;
import java.io.IOException;
import java.util.HashSet;
import org.apache.zookeeper.txn.CheckVersionTxn;
import org.apache.zookeeper.proto.CheckVersionRequest;
import org.apache.zookeeper.txn.CreateSessionTxn;
import org.apache.zookeeper.txn.SetACLTxn;
import org.apache.zookeeper.proto.SetACLRequest;
import org.apache.zookeeper.txn.SetDataTxn;
import org.apache.zookeeper.proto.SetDataRequest;
import org.apache.zookeeper.txn.DeleteTxn;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.data.StatPersisted;
import org.apache.zookeeper.txn.CreateTxn;
import java.util.Locale;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.zookeeper.txn.TxnHeader;
import org.apache.zookeeper.common.Time;
import org.apache.jute.Record;
import org.apache.zookeeper.server.auth.AuthenticationProvider;
import org.apache.zookeeper.server.auth.ProviderRegistry;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import org.apache.zookeeper.Op;
import java.util.HashMap;
import org.apache.zookeeper.MultiTransactionRecord;
import java.util.Set;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.quorum.Leader;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;

public class PrepRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor
{
    private static final Logger LOG;
    static boolean skipACL;
    private static boolean failCreate;
    LinkedBlockingQueue<Request> submittedRequests;
    RequestProcessor nextProcessor;
    ZooKeeperServer zks;
    
    public PrepRequestProcessor(final ZooKeeperServer zks, final RequestProcessor nextProcessor) {
        super("ProcessThread(sid:" + zks.getServerId() + " cport:" + zks.getClientPort() + "):", zks.getZooKeeperServerListener());
        this.submittedRequests = new LinkedBlockingQueue<Request>();
        this.nextProcessor = nextProcessor;
        this.zks = zks;
    }
    
    public static void setFailCreate(final boolean b) {
        PrepRequestProcessor.failCreate = b;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                final Request request = this.submittedRequests.take();
                long traceMask = 2L;
                if (request.type == 11) {
                    traceMask = 8L;
                }
                if (PrepRequestProcessor.LOG.isTraceEnabled()) {
                    ZooTrace.logRequest(PrepRequestProcessor.LOG, traceMask, 'P', request, "");
                }
                if (Request.requestOfDeath == request) {
                    break;
                }
                this.pRequest(request);
            }
        }
        catch (RequestProcessorException e) {
            if (e.getCause() instanceof Leader.XidRolloverException) {
                PrepRequestProcessor.LOG.info(e.getCause().getMessage());
            }
            this.handleException(this.getName(), e);
        }
        catch (Exception e2) {
            this.handleException(this.getName(), e2);
        }
        PrepRequestProcessor.LOG.info("PrepRequestProcessor exited loop!");
    }
    
    ZooKeeperServer.ChangeRecord getRecordForPath(final String path) throws KeeperException.NoNodeException {
        ZooKeeperServer.ChangeRecord lastChange = null;
        synchronized (this.zks.outstandingChanges) {
            lastChange = this.zks.outstandingChangesForPath.get(path);
            if (lastChange == null) {
                final DataNode n = this.zks.getZKDatabase().getNode(path);
                if (n != null) {
                    final Set<String> children;
                    synchronized (n) {
                        children = n.getChildren();
                    }
                    lastChange = new ZooKeeperServer.ChangeRecord(-1L, path, n.stat, children.size(), this.zks.getZKDatabase().aclForNode(n));
                }
            }
        }
        if (lastChange == null || lastChange.stat == null) {
            throw new KeeperException.NoNodeException(path);
        }
        return lastChange;
    }
    
    private ZooKeeperServer.ChangeRecord getOutstandingChange(final String path) {
        synchronized (this.zks.outstandingChanges) {
            return this.zks.outstandingChangesForPath.get(path);
        }
    }
    
    void addChangeRecord(final ZooKeeperServer.ChangeRecord c) {
        synchronized (this.zks.outstandingChanges) {
            this.zks.outstandingChanges.add(c);
            this.zks.outstandingChangesForPath.put(c.path, c);
        }
    }
    
    HashMap<String, ZooKeeperServer.ChangeRecord> getPendingChanges(final MultiTransactionRecord multiRequest) {
        final HashMap<String, ZooKeeperServer.ChangeRecord> pendingChangeRecords = new HashMap<String, ZooKeeperServer.ChangeRecord>();
        for (final Op op : multiRequest) {
            final String path = op.getPath();
            final ZooKeeperServer.ChangeRecord cr = this.getOutstandingChange(path);
            if (cr != null) {
                pendingChangeRecords.put(path, cr);
            }
            final int lastSlash = path.lastIndexOf(47);
            if (lastSlash != -1) {
                if (path.indexOf(0) != -1) {
                    continue;
                }
                final String parentPath = path.substring(0, lastSlash);
                final ZooKeeperServer.ChangeRecord parentCr = this.getOutstandingChange(parentPath);
                if (parentCr == null) {
                    continue;
                }
                pendingChangeRecords.put(parentPath, parentCr);
            }
        }
        return pendingChangeRecords;
    }
    
    void rollbackPendingChanges(final long zxid, final HashMap<String, ZooKeeperServer.ChangeRecord> pendingChangeRecords) {
        synchronized (this.zks.outstandingChanges) {
            final ListIterator<ZooKeeperServer.ChangeRecord> iter = this.zks.outstandingChanges.listIterator(this.zks.outstandingChanges.size());
            while (iter.hasPrevious()) {
                final ZooKeeperServer.ChangeRecord c = iter.previous();
                if (c.zxid != zxid) {
                    break;
                }
                iter.remove();
                this.zks.outstandingChangesForPath.remove(c.path);
            }
            if (this.zks.outstandingChanges.isEmpty()) {
                return;
            }
            final long firstZxid = this.zks.outstandingChanges.get(0).zxid;
            for (final ZooKeeperServer.ChangeRecord c2 : pendingChangeRecords.values()) {
                if (c2.zxid < firstZxid) {
                    continue;
                }
                this.zks.outstandingChangesForPath.put(c2.path, c2);
            }
        }
    }
    
    static void checkACL(final ZooKeeperServer zks, final List<ACL> acl, final int perm, final List<Id> ids) throws KeeperException.NoAuthException {
        if (PrepRequestProcessor.skipACL) {
            return;
        }
        if (acl == null || acl.size() == 0) {
            return;
        }
        for (final Id authId : ids) {
            if (authId.getScheme().equals("super")) {
                return;
            }
        }
        for (final ACL a : acl) {
            final Id id = a.getId();
            if ((a.getPerms() & perm) != 0x0) {
                if (id.getScheme().equals("world") && id.getId().equals("anyone")) {
                    return;
                }
                final AuthenticationProvider ap = ProviderRegistry.getProvider(id.getScheme());
                if (ap == null) {
                    continue;
                }
                for (final Id authId2 : ids) {
                    if (authId2.getScheme().equals(id.getScheme()) && ap.matches(authId2.getId(), id.getId())) {
                        return;
                    }
                }
            }
        }
        throw new KeeperException.NoAuthException();
    }
    
    protected void pRequest2Txn(final int type, final long zxid, final Request request, final Record record, final boolean deserialize) throws KeeperException, IOException, RequestProcessorException {
        request.hdr = new TxnHeader(request.sessionId, request.cxid, zxid, Time.currentWallTime(), type);
        switch (type) {
            case 1: {
                this.zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                final CreateRequest createRequest = (CreateRequest)record;
                if (deserialize) {
                    ByteBufferInputStream.byteBuffer2Record(request.request, createRequest);
                }
                String path = createRequest.getPath();
                final int lastSlash = path.lastIndexOf(47);
                if (lastSlash == -1 || path.indexOf(0) != -1 || PrepRequestProcessor.failCreate) {
                    PrepRequestProcessor.LOG.info("Invalid path " + path + " with session 0x" + Long.toHexString(request.sessionId));
                    throw new KeeperException.BadArgumentsException(path);
                }
                final List<ACL> listACL = this.removeDuplicates(createRequest.getAcl());
                if (!this.fixupACL(request.authInfo, listACL)) {
                    throw new KeeperException.InvalidACLException(path);
                }
                final String parentPath = path.substring(0, lastSlash);
                ZooKeeperServer.ChangeRecord parentRecord = this.getRecordForPath(parentPath);
                checkACL(this.zks, parentRecord.acl, 4, request.authInfo);
                final int parentCVersion = parentRecord.stat.getCversion();
                final CreateMode createMode = CreateMode.fromFlag(createRequest.getFlags());
                if (createMode.isSequential()) {
                    path += String.format(Locale.ENGLISH, "%010d", parentCVersion);
                }
                this.validatePath(path, request.sessionId);
                try {
                    if (this.getRecordForPath(path) != null) {
                        throw new KeeperException.NodeExistsException(path);
                    }
                }
                catch (KeeperException.NoNodeException ex) {}
                final boolean ephemeralParent = parentRecord.stat.getEphemeralOwner() != 0L;
                if (ephemeralParent) {
                    throw new KeeperException.NoChildrenForEphemeralsException(path);
                }
                final int newCversion = parentRecord.stat.getCversion() + 1;
                request.txn = new CreateTxn(path, createRequest.getData(), listACL, createMode.isEphemeral(), newCversion);
                final StatPersisted s = new StatPersisted();
                if (createMode.isEphemeral()) {
                    s.setEphemeralOwner(request.sessionId);
                }
                final ZooKeeperServer.ChangeRecord duplicate;
                parentRecord = (duplicate = parentRecord.duplicate(request.hdr.getZxid()));
                ++duplicate.childCount;
                parentRecord.stat.setCversion(newCversion);
                this.addChangeRecord(parentRecord);
                this.addChangeRecord(new ZooKeeperServer.ChangeRecord(request.hdr.getZxid(), path, s, 0, listACL));
                break;
            }
            case 2: {
                this.zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                final DeleteRequest deleteRequest = (DeleteRequest)record;
                if (deserialize) {
                    ByteBufferInputStream.byteBuffer2Record(request.request, deleteRequest);
                }
                final String path = deleteRequest.getPath();
                final int lastSlash = path.lastIndexOf(47);
                if (lastSlash == -1 || path.indexOf(0) != -1 || this.zks.getZKDatabase().isSpecialPath(path)) {
                    throw new KeeperException.BadArgumentsException(path);
                }
                final String parentPath = path.substring(0, lastSlash);
                ZooKeeperServer.ChangeRecord parentRecord = this.getRecordForPath(parentPath);
                final ZooKeeperServer.ChangeRecord nodeRecord = this.getRecordForPath(path);
                checkACL(this.zks, parentRecord.acl, 8, request.authInfo);
                final int version = deleteRequest.getVersion();
                if (version != -1 && nodeRecord.stat.getVersion() != version) {
                    throw new KeeperException.BadVersionException(path);
                }
                if (nodeRecord.childCount > 0) {
                    throw new KeeperException.NotEmptyException(path);
                }
                request.txn = new DeleteTxn(path);
                final ZooKeeperServer.ChangeRecord duplicate2;
                parentRecord = (duplicate2 = parentRecord.duplicate(request.hdr.getZxid()));
                --duplicate2.childCount;
                this.addChangeRecord(parentRecord);
                this.addChangeRecord(new ZooKeeperServer.ChangeRecord(request.hdr.getZxid(), path, null, -1, null));
                break;
            }
            case 5: {
                this.zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                final SetDataRequest setDataRequest = (SetDataRequest)record;
                if (deserialize) {
                    ByteBufferInputStream.byteBuffer2Record(request.request, setDataRequest);
                }
                final String path = setDataRequest.getPath();
                this.validatePath(path, request.sessionId);
                ZooKeeperServer.ChangeRecord nodeRecord = this.getRecordForPath(path);
                checkACL(this.zks, nodeRecord.acl, 2, request.authInfo);
                int version = setDataRequest.getVersion();
                final int currentVersion = nodeRecord.stat.getVersion();
                if (version != -1 && version != currentVersion) {
                    throw new KeeperException.BadVersionException(path);
                }
                version = currentVersion + 1;
                request.txn = new SetDataTxn(path, setDataRequest.getData(), version);
                nodeRecord = nodeRecord.duplicate(request.hdr.getZxid());
                nodeRecord.stat.setVersion(version);
                this.addChangeRecord(nodeRecord);
                break;
            }
            case 7: {
                this.zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                final SetACLRequest setAclRequest = (SetACLRequest)record;
                if (deserialize) {
                    ByteBufferInputStream.byteBuffer2Record(request.request, setAclRequest);
                }
                final String path = setAclRequest.getPath();
                this.validatePath(path, request.sessionId);
                final List<ACL> listACL = this.removeDuplicates(setAclRequest.getAcl());
                if (!this.fixupACL(request.authInfo, listACL)) {
                    throw new KeeperException.InvalidACLException(path);
                }
                ZooKeeperServer.ChangeRecord nodeRecord = this.getRecordForPath(path);
                checkACL(this.zks, nodeRecord.acl, 16, request.authInfo);
                int version = setAclRequest.getVersion();
                final int currentVersion = nodeRecord.stat.getAversion();
                if (version != -1 && version != currentVersion) {
                    throw new KeeperException.BadVersionException(path);
                }
                version = currentVersion + 1;
                request.txn = new SetACLTxn(path, listACL, version);
                nodeRecord = nodeRecord.duplicate(request.hdr.getZxid());
                nodeRecord.stat.setAversion(version);
                this.addChangeRecord(nodeRecord);
                break;
            }
            case -10: {
                request.request.rewind();
                final int to = request.request.getInt();
                request.txn = new CreateSessionTxn(to);
                request.request.rewind();
                this.zks.sessionTracker.addSession(request.sessionId, to);
                this.zks.setOwner(request.sessionId, request.getOwner());
                break;
            }
            case -11: {
                final HashSet<String> es = this.zks.getZKDatabase().getEphemerals(request.sessionId);
                synchronized (this.zks.outstandingChanges) {
                    for (final ZooKeeperServer.ChangeRecord c : this.zks.outstandingChanges) {
                        if (c.stat == null) {
                            es.remove(c.path);
                        }
                        else {
                            if (c.stat.getEphemeralOwner() != request.sessionId) {
                                continue;
                            }
                            es.add(c.path);
                        }
                    }
                    for (final String path2Delete : es) {
                        this.addChangeRecord(new ZooKeeperServer.ChangeRecord(request.hdr.getZxid(), path2Delete, null, 0, null));
                    }
                    this.zks.sessionTracker.setSessionClosing(request.sessionId);
                }
                PrepRequestProcessor.LOG.info("Processed session termination for sessionid: 0x" + Long.toHexString(request.sessionId));
                break;
            }
            case 13: {
                this.zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                final CheckVersionRequest checkVersionRequest = (CheckVersionRequest)record;
                if (deserialize) {
                    ByteBufferInputStream.byteBuffer2Record(request.request, checkVersionRequest);
                }
                final String path = checkVersionRequest.getPath();
                this.validatePath(path, request.sessionId);
                final ZooKeeperServer.ChangeRecord nodeRecord = this.getRecordForPath(path);
                checkACL(this.zks, nodeRecord.acl, 1, request.authInfo);
                int version = checkVersionRequest.getVersion();
                final int currentVersion = nodeRecord.stat.getVersion();
                if (version != -1 && version != currentVersion) {
                    throw new KeeperException.BadVersionException(path);
                }
                version = currentVersion + 1;
                request.txn = new CheckVersionTxn(path, version);
                break;
            }
            default: {
                PrepRequestProcessor.LOG.error("Invalid OpCode: {} received by PrepRequestProcessor", (Object)type);
                break;
            }
        }
    }
    
    private void validatePath(final String path, final long sessionId) throws KeeperException.BadArgumentsException {
        try {
            PathUtils.validatePath(path);
        }
        catch (IllegalArgumentException ie) {
            PrepRequestProcessor.LOG.info("Invalid path " + path + " with session 0x" + Long.toHexString(sessionId) + ", reason: " + ie.getMessage());
            throw new KeeperException.BadArgumentsException(path);
        }
    }
    
    protected void pRequest(final Request request) throws RequestProcessorException {
        request.hdr = null;
        request.txn = null;
        try {
            switch (request.type) {
                case 1: {
                    final CreateRequest createRequest = new CreateRequest();
                    this.pRequest2Txn(request.type, this.zks.getNextZxid(), request, createRequest, true);
                    break;
                }
                case 2: {
                    final DeleteRequest deleteRequest = new DeleteRequest();
                    this.pRequest2Txn(request.type, this.zks.getNextZxid(), request, deleteRequest, true);
                    break;
                }
                case 5: {
                    final SetDataRequest setDataRequest = new SetDataRequest();
                    this.pRequest2Txn(request.type, this.zks.getNextZxid(), request, setDataRequest, true);
                    break;
                }
                case 7: {
                    final SetACLRequest setAclRequest = new SetACLRequest();
                    this.pRequest2Txn(request.type, this.zks.getNextZxid(), request, setAclRequest, true);
                    break;
                }
                case 13: {
                    final CheckVersionRequest checkRequest = new CheckVersionRequest();
                    this.pRequest2Txn(request.type, this.zks.getNextZxid(), request, checkRequest, true);
                    break;
                }
                case 14: {
                    final MultiTransactionRecord multiRequest = new MultiTransactionRecord();
                    try {
                        ByteBufferInputStream.byteBuffer2Record(request.request, multiRequest);
                    }
                    catch (IOException e) {
                        request.hdr = new TxnHeader(request.sessionId, request.cxid, this.zks.getNextZxid(), Time.currentWallTime(), 14);
                        throw e;
                    }
                    final List<Txn> txns = new ArrayList<Txn>();
                    final long zxid = this.zks.getNextZxid();
                    KeeperException ke = null;
                    final HashMap<String, ZooKeeperServer.ChangeRecord> pendingChanges = this.getPendingChanges(multiRequest);
                    int index = 0;
                    for (final Op op : multiRequest) {
                        final Record subrequest = op.toRequestRecord();
                        if (ke != null) {
                            request.hdr.setType(-1);
                            request.txn = new ErrorTxn(KeeperException.Code.RUNTIMEINCONSISTENCY.intValue());
                        }
                        else {
                            try {
                                this.pRequest2Txn(op.getType(), zxid, request, subrequest, false);
                            }
                            catch (KeeperException e2) {
                                ke = e2;
                                request.hdr.setType(-1);
                                request.txn = new ErrorTxn(e2.code().intValue());
                                PrepRequestProcessor.LOG.info("Got user-level KeeperException when processing " + request.toString() + " aborting remaining multi ops. Error Path:" + e2.getPath() + " Error:" + e2.getMessage());
                                request.setException(e2);
                                this.rollbackPendingChanges(zxid, pendingChanges);
                            }
                        }
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        final BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
                        request.txn.serialize(boa, "request");
                        final ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
                        txns.add(new Txn(request.hdr.getType(), bb.array()));
                        ++index;
                    }
                    request.hdr = new TxnHeader(request.sessionId, request.cxid, zxid, Time.currentWallTime(), request.type);
                    request.txn = new MultiTxn(txns);
                    break;
                }
                case -11:
                case -10: {
                    this.pRequest2Txn(request.type, this.zks.getNextZxid(), request, null, true);
                    break;
                }
                case 3:
                case 4:
                case 6:
                case 8:
                case 9:
                case 11:
                case 12:
                case 101: {
                    this.zks.sessionTracker.checkSession(request.sessionId, request.getOwner());
                    break;
                }
                default: {
                    PrepRequestProcessor.LOG.warn("unknown type " + request.type);
                    break;
                }
            }
        }
        catch (KeeperException e3) {
            if (request.hdr != null) {
                request.hdr.setType(-1);
                request.txn = new ErrorTxn(e3.code().intValue());
            }
            PrepRequestProcessor.LOG.info("Got user-level KeeperException when processing " + request.toString() + " Error Path:" + e3.getPath() + " Error:" + e3.getMessage());
            request.setException(e3);
        }
        catch (Exception e4) {
            PrepRequestProcessor.LOG.error("Failed to process " + request, e4);
            final StringBuilder sb = new StringBuilder();
            final ByteBuffer bb2 = request.request;
            if (bb2 != null) {
                bb2.rewind();
                while (bb2.hasRemaining()) {
                    sb.append(Integer.toHexString(bb2.get() & 0xFF));
                }
            }
            else {
                sb.append("request buffer is null");
            }
            PrepRequestProcessor.LOG.error("Dumping request buffer: 0x" + sb.toString());
            if (request.hdr != null) {
                request.hdr.setType(-1);
                request.txn = new ErrorTxn(KeeperException.Code.MARSHALLINGERROR.intValue());
            }
        }
        request.zxid = this.zks.getZxid();
        this.nextProcessor.processRequest(request);
    }
    
    private List<ACL> removeDuplicates(final List<ACL> acl) {
        final ArrayList<ACL> retval = new ArrayList<ACL>();
        for (final ACL a : acl) {
            if (!retval.contains(a)) {
                retval.add(a);
            }
        }
        return retval;
    }
    
    private boolean fixupACL(final List<Id> authInfo, final List<ACL> acl) {
        if (PrepRequestProcessor.skipACL) {
            return true;
        }
        if (acl == null || acl.size() == 0) {
            return false;
        }
        final Iterator<ACL> it = acl.iterator();
        LinkedList<ACL> toAdd = null;
        while (it.hasNext()) {
            final ACL a = it.next();
            final Id id = a.getId();
            if (id.getScheme().equals("world") && id.getId().equals("anyone")) {
                continue;
            }
            if (id.getScheme().equals("auth")) {
                it.remove();
                if (toAdd == null) {
                    toAdd = new LinkedList<ACL>();
                }
                boolean authIdValid = false;
                for (final Id cid : authInfo) {
                    final AuthenticationProvider ap = ProviderRegistry.getProvider(cid.getScheme());
                    if (ap == null) {
                        PrepRequestProcessor.LOG.error("Missing AuthenticationProvider for " + cid.getScheme());
                    }
                    else {
                        if (!ap.isAuthenticated()) {
                            continue;
                        }
                        authIdValid = true;
                        toAdd.add(new ACL(a.getPerms(), cid));
                    }
                }
                if (!authIdValid) {
                    return false;
                }
                continue;
            }
            else {
                final AuthenticationProvider ap2 = ProviderRegistry.getProvider(id.getScheme());
                if (ap2 == null) {
                    return false;
                }
                if (!ap2.isValid(id.getId())) {
                    return false;
                }
                continue;
            }
        }
        if (toAdd != null) {
            for (final ACL a2 : toAdd) {
                acl.add(a2);
            }
        }
        return acl.size() > 0;
    }
    
    @Override
    public void processRequest(final Request request) {
        this.submittedRequests.add(request);
    }
    
    @Override
    public void shutdown() {
        PrepRequestProcessor.LOG.info("Shutting down");
        this.submittedRequests.clear();
        this.submittedRequests.add(Request.requestOfDeath);
        this.nextProcessor.shutdown();
    }
    
    static {
        LOG = LoggerFactory.getLogger(PrepRequestProcessor.class);
        PrepRequestProcessor.skipACL = System.getProperty("zookeeper.skipACL", "no").equals("yes");
        if (PrepRequestProcessor.skipACL) {
            PrepRequestProcessor.LOG.info("zookeeper.skipACL==\"yes\", ACL checks will be skipped");
        }
        PrepRequestProcessor.failCreate = false;
    }
}
