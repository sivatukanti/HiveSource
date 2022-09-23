// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.util;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import org.apache.jute.BinaryOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.zookeeper.server.Request;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.server.ZooTrace;
import java.util.Map;
import org.apache.zookeeper.server.DataTree;
import org.apache.jute.InputArchive;
import java.io.EOFException;
import org.apache.zookeeper.txn.CreateTxnV0;
import java.io.IOException;
import org.apache.zookeeper.txn.MultiTxn;
import org.apache.zookeeper.txn.ErrorTxn;
import org.apache.zookeeper.txn.SetACLTxn;
import org.apache.zookeeper.txn.SetDataTxn;
import org.apache.zookeeper.txn.DeleteTxn;
import org.apache.zookeeper.txn.CreateTxn;
import org.apache.zookeeper.txn.CreateSessionTxn;
import java.io.InputStream;
import org.apache.jute.BinaryInputArchive;
import java.io.ByteArrayInputStream;
import org.apache.jute.Record;
import org.apache.zookeeper.txn.TxnHeader;
import org.slf4j.Logger;

public class SerializeUtils
{
    private static final Logger LOG;
    
    public static Record deserializeTxn(final byte[] txnBytes, final TxnHeader hdr) throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(txnBytes);
        final InputArchive ia = BinaryInputArchive.getArchive(bais);
        hdr.deserialize(ia, "hdr");
        bais.mark(bais.available());
        Record txn = null;
        switch (hdr.getType()) {
            case -10: {
                txn = new CreateSessionTxn();
                break;
            }
            case -11: {
                return null;
            }
            case 1: {
                txn = new CreateTxn();
                break;
            }
            case 2: {
                txn = new DeleteTxn();
                break;
            }
            case 5: {
                txn = new SetDataTxn();
                break;
            }
            case 7: {
                txn = new SetACLTxn();
                break;
            }
            case -1: {
                txn = new ErrorTxn();
                break;
            }
            case 14: {
                txn = new MultiTxn();
                break;
            }
            default: {
                throw new IOException("Unsupported Txn with type=%d" + hdr.getType());
            }
        }
        if (txn != null) {
            try {
                txn.deserialize(ia, "txn");
            }
            catch (EOFException e) {
                if (hdr.getType() != 1) {
                    throw e;
                }
                final CreateTxn create = (CreateTxn)txn;
                bais.reset();
                final CreateTxnV0 createv0 = new CreateTxnV0();
                createv0.deserialize(ia, "txn");
                create.setPath(createv0.getPath());
                create.setData(createv0.getData());
                create.setAcl(createv0.getAcl());
                create.setEphemeral(createv0.getEphemeral());
                create.setParentCVersion(-1);
            }
        }
        return txn;
    }
    
    public static void deserializeSnapshot(final DataTree dt, final InputArchive ia, final Map<Long, Integer> sessions) throws IOException {
        for (int count = ia.readInt("count"); count > 0; --count) {
            final long id = ia.readLong("id");
            final int to = ia.readInt("timeout");
            sessions.put(id, to);
            if (SerializeUtils.LOG.isTraceEnabled()) {
                ZooTrace.logTraceMessage(SerializeUtils.LOG, 32L, "loadData --- session in archive: " + id + " with timeout: " + to);
            }
        }
        dt.deserialize(ia, "tree");
    }
    
    public static void serializeSnapshot(final DataTree dt, final OutputArchive oa, final Map<Long, Integer> sessions) throws IOException {
        final HashMap<Long, Integer> sessSnap = new HashMap<Long, Integer>(sessions);
        oa.writeInt(sessSnap.size(), "count");
        for (final Map.Entry<Long, Integer> entry : sessSnap.entrySet()) {
            oa.writeLong(entry.getKey(), "id");
            oa.writeInt(entry.getValue(), "timeout");
        }
        dt.serialize(oa, "tree");
    }
    
    public static byte[] serializeRequest(final Request request) {
        if (request == null || request.hdr == null) {
            return null;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
        try {
            request.hdr.serialize(boa, "hdr");
            if (request.txn != null) {
                request.txn.serialize(boa, "txn");
            }
            baos.close();
        }
        catch (IOException e) {
            SerializeUtils.LOG.error("This really should be impossible", e);
        }
        return baos.toByteArray();
    }
    
    static {
        LOG = LoggerFactory.getLogger(SerializeUtils.class);
    }
}
