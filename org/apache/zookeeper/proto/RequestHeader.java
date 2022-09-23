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
public class RequestHeader implements Record
{
    private int xid;
    private int type;
    
    public RequestHeader() {
    }
    
    public RequestHeader(final int xid, final int type) {
        this.xid = xid;
        this.type = type;
    }
    
    public int getXid() {
        return this.xid;
    }
    
    public void setXid(final int m_) {
        this.xid = m_;
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
        a_.writeInt(this.xid, "xid");
        a_.writeInt(this.type, "type");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.xid = a_.readInt("xid");
        this.type = a_.readInt("type");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.xid, "xid");
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
        if (!(peer_ instanceof RequestHeader)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final RequestHeader peer = (RequestHeader)peer_;
        int ret = 0;
        ret = ((this.xid == peer.xid) ? 0 : ((this.xid < peer.xid) ? -1 : 1));
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
        if (!(peer_ instanceof RequestHeader)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final RequestHeader peer = (RequestHeader)peer_;
        boolean ret = false;
        ret = (this.xid == peer.xid);
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
        int ret = this.xid;
        result = 37 * result + ret;
        ret = this.type;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LRequestHeader(ii)";
    }
}
