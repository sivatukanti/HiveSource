// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.txn;

import org.apache.jute.BinaryInputArchive;
import java.io.DataInput;
import org.apache.jute.BinaryOutputArchive;
import java.io.DataOutput;
import java.io.OutputStream;
import org.apache.jute.CsvOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class TxnHeader implements Record
{
    private long clientId;
    private int cxid;
    private long zxid;
    private long time;
    private int type;
    
    public TxnHeader() {
    }
    
    public TxnHeader(final long clientId, final int cxid, final long zxid, final long time, final int type) {
        this.clientId = clientId;
        this.cxid = cxid;
        this.zxid = zxid;
        this.time = time;
        this.type = type;
    }
    
    public long getClientId() {
        return this.clientId;
    }
    
    public void setClientId(final long m_) {
        this.clientId = m_;
    }
    
    public int getCxid() {
        return this.cxid;
    }
    
    public void setCxid(final int m_) {
        this.cxid = m_;
    }
    
    public long getZxid() {
        return this.zxid;
    }
    
    public void setZxid(final long m_) {
        this.zxid = m_;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public void setTime(final long m_) {
        this.time = m_;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int m_) {
        this.type = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeLong(this.clientId, "clientId");
        a_.writeInt(this.cxid, "cxid");
        a_.writeLong(this.zxid, "zxid");
        a_.writeLong(this.time, "time");
        a_.writeInt(this.type, "type");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.clientId = a_.readLong("clientId");
        this.cxid = a_.readInt("cxid");
        this.zxid = a_.readLong("zxid");
        this.time = a_.readLong("time");
        this.type = a_.readInt("type");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeLong(this.clientId, "clientId");
            a_.writeInt(this.cxid, "cxid");
            a_.writeLong(this.zxid, "zxid");
            a_.writeLong(this.time, "time");
            a_.writeInt(this.type, "type");
            a_.endRecord(this, "");
            return new String(s.toByteArray(), "UTF-8");
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            return "ERROR";
        }
    }
    
    public void write(final DataOutput out) throws IOException {
        final BinaryOutputArchive archive = new BinaryOutputArchive(out);
        this.serialize(archive, "");
    }
    
    public void readFields(final DataInput in) throws IOException {
        final BinaryInputArchive archive = new BinaryInputArchive(in);
        this.deserialize(archive, "");
    }
    
    public int compareTo(final Object peer_) throws ClassCastException {
        if (!(peer_ instanceof TxnHeader)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final TxnHeader peer = (TxnHeader)peer_;
        int ret = 0;
        ret = ((this.clientId == peer.clientId) ? 0 : ((this.clientId < peer.clientId) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.cxid == peer.cxid) ? 0 : ((this.cxid < peer.cxid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.zxid == peer.zxid) ? 0 : ((this.zxid < peer.zxid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.time == peer.time) ? 0 : ((this.time < peer.time) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.type == peer.type) ? 0 : ((this.type < peer.type) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof TxnHeader)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final TxnHeader peer = (TxnHeader)peer_;
        boolean ret = false;
        ret = (this.clientId == peer.clientId);
        if (!ret) {
            return ret;
        }
        ret = (this.cxid == peer.cxid);
        if (!ret) {
            return ret;
        }
        ret = (this.zxid == peer.zxid);
        if (!ret) {
            return ret;
        }
        ret = (this.time == peer.time);
        if (!ret) {
            return ret;
        }
        ret = (this.type == peer.type);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = (int)(this.clientId ^ this.clientId >>> 32);
        result = 37 * result + ret;
        ret = this.cxid;
        result = 37 * result + ret;
        ret = (int)(this.zxid ^ this.zxid >>> 32);
        result = 37 * result + ret;
        ret = (int)(this.time ^ this.time >>> 32);
        result = 37 * result + ret;
        ret = this.type;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LTxnHeader(lilli)";
    }
}
