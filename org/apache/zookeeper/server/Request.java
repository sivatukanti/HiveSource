// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import org.apache.zookeeper.common.Time;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Id;
import java.util.List;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

public class Request
{
    private static final Logger LOG;
    public static final Request requestOfDeath;
    public final long sessionId;
    public final int cxid;
    public final int type;
    public final ByteBuffer request;
    public final ServerCnxn cnxn;
    public TxnHeader hdr;
    public Record txn;
    public long zxid;
    public final List<Id> authInfo;
    public final long createTime;
    private Object owner;
    private KeeperException e;
    
    public Request(final ServerCnxn cnxn, final long sessionId, final int xid, final int type, final ByteBuffer bb, final List<Id> authInfo) {
        this.zxid = -1L;
        this.createTime = Time.currentElapsedTime();
        this.cnxn = cnxn;
        this.sessionId = sessionId;
        this.cxid = xid;
        this.type = type;
        this.request = bb;
        this.authInfo = authInfo;
    }
    
    public Request(final long sessionId, final int xid, final int type, final TxnHeader hdr, final Record txn, final long zxid) {
        this.zxid = -1L;
        this.createTime = Time.currentElapsedTime();
        this.sessionId = sessionId;
        this.cxid = xid;
        this.type = type;
        this.hdr = hdr;
        this.txn = txn;
        this.zxid = zxid;
        this.request = null;
        this.cnxn = null;
        this.authInfo = null;
    }
    
    public Object getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Object owner) {
        this.owner = owner;
    }
    
    static boolean isValid(final int type) {
        switch (type) {
            case 0: {
                return false;
            }
            case -11:
            case -10:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 11:
            case 12:
            case 13:
            case 14:
            case 101: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static boolean isQuorum(final int type) {
        switch (type) {
            case 3:
            case 4:
            case 6:
            case 8:
            case 12: {
                return false;
            }
            case -11:
            case -10:
            case -1:
            case 1:
            case 2:
            case 5:
            case 7:
            case 13:
            case 14: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static String op2String(final int op) {
        switch (op) {
            case 0: {
                return "notification";
            }
            case 1: {
                return "create";
            }
            case 101: {
                return "setWatches";
            }
            case 2: {
                return "delete";
            }
            case 3: {
                return "exists";
            }
            case 4: {
                return "getData";
            }
            case 13: {
                return "check";
            }
            case 14: {
                return "multi";
            }
            case 5: {
                return "setData";
            }
            case 9: {
                return "sync:";
            }
            case 6: {
                return "getACL";
            }
            case 7: {
                return "setACL";
            }
            case 8: {
                return "getChildren";
            }
            case 12: {
                return "getChildren2";
            }
            case 11: {
                return "ping";
            }
            case -10: {
                return "createSession";
            }
            case -11: {
                return "closeSession";
            }
            case -1: {
                return "error";
            }
            default: {
                return "unknown " + op;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("sessionid:0x").append(Long.toHexString(this.sessionId)).append(" type:").append(op2String(this.type)).append(" cxid:0x").append(Long.toHexString(this.cxid)).append(" zxid:0x").append(Long.toHexString((this.hdr == null) ? -2L : this.hdr.getZxid())).append(" txntype:").append((this.hdr == null) ? "unknown" : ("" + this.hdr.getType()));
        String path = "n/a";
        if (this.type != -10 && this.type != 101 && this.type != -11 && this.request != null && this.request.remaining() >= 4) {
            try {
                final ByteBuffer rbuf = this.request.asReadOnlyBuffer();
                rbuf.clear();
                final int pathLen = rbuf.getInt();
                if (pathLen >= 0 && pathLen < 4096 && rbuf.remaining() >= pathLen) {
                    final byte[] b = new byte[pathLen];
                    rbuf.get(b);
                    path = new String(b);
                }
            }
            catch (Exception ex) {}
        }
        sb.append(" reqpath:").append(path);
        return sb.toString();
    }
    
    public void setException(final KeeperException e) {
        this.e = e;
    }
    
    public KeeperException getException() {
        return this.e;
    }
    
    static {
        LOG = LoggerFactory.getLogger(Request.class);
        requestOfDeath = new Request(null, 0L, 0, 0, null, null);
    }
}
