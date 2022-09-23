// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.proto;

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
public class ReplyHeader implements Record
{
    private int xid;
    private long zxid;
    private int err;
    
    public ReplyHeader() {
    }
    
    public ReplyHeader(final int xid, final long zxid, final int err) {
        this.xid = xid;
        this.zxid = zxid;
        this.err = err;
    }
    
    public int getXid() {
        return this.xid;
    }
    
    public void setXid(final int m_) {
        this.xid = m_;
    }
    
    public long getZxid() {
        return this.zxid;
    }
    
    public void setZxid(final long m_) {
        this.zxid = m_;
    }
    
    public int getErr() {
        return this.err;
    }
    
    public void setErr(final int m_) {
        this.err = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.xid, "xid");
        a_.writeLong(this.zxid, "zxid");
        a_.writeInt(this.err, "err");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.xid = a_.readInt("xid");
        this.zxid = a_.readLong("zxid");
        this.err = a_.readInt("err");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.xid, "xid");
            a_.writeLong(this.zxid, "zxid");
            a_.writeInt(this.err, "err");
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
        if (!(peer_ instanceof ReplyHeader)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final ReplyHeader peer = (ReplyHeader)peer_;
        int ret = 0;
        ret = ((this.xid == peer.xid) ? 0 : ((this.xid < peer.xid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.zxid == peer.zxid) ? 0 : ((this.zxid < peer.zxid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.err == peer.err) ? 0 : ((this.err < peer.err) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof ReplyHeader)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final ReplyHeader peer = (ReplyHeader)peer_;
        boolean ret = false;
        ret = (this.xid == peer.xid);
        if (!ret) {
            return ret;
        }
        ret = (this.zxid == peer.zxid);
        if (!ret) {
            return ret;
        }
        ret = (this.err == peer.err);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.xid;
        result = 37 * result + ret;
        ret = (int)(this.zxid ^ this.zxid >>> 32);
        result = 37 * result + ret;
        ret = this.err;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LReplyHeader(ili)";
    }
}
