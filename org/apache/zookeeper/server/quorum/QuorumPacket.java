// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.util.Arrays;
import org.apache.jute.Utils;
import org.apache.jute.BinaryInputArchive;
import java.io.DataInput;
import org.apache.jute.BinaryOutputArchive;
import java.io.DataOutput;
import java.io.OutputStream;
import org.apache.jute.CsvOutputArchive;
import java.io.ByteArrayOutputStream;
import org.apache.jute.Index;
import java.util.ArrayList;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.data.Id;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class QuorumPacket implements Record
{
    private int type;
    private long zxid;
    private byte[] data;
    private List<Id> authinfo;
    
    public QuorumPacket() {
    }
    
    public QuorumPacket(final int type, final long zxid, final byte[] data, final List<Id> authinfo) {
        this.type = type;
        this.zxid = zxid;
        this.data = data;
        this.authinfo = authinfo;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int m_) {
        this.type = m_;
    }
    
    public long getZxid() {
        return this.zxid;
    }
    
    public void setZxid(final long m_) {
        this.zxid = m_;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] m_) {
        this.data = m_;
    }
    
    public List<Id> getAuthinfo() {
        return this.authinfo;
    }
    
    public void setAuthinfo(final List<Id> m_) {
        this.authinfo = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.type, "type");
        a_.writeLong(this.zxid, "zxid");
        a_.writeBuffer(this.data, "data");
        a_.startVector(this.authinfo, "authinfo");
        if (this.authinfo != null) {
            for (int len1 = this.authinfo.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final Id e1 = this.authinfo.get(vidx1);
                a_.writeRecord(e1, "e1");
            }
        }
        a_.endVector(this.authinfo, "authinfo");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.type = a_.readInt("type");
        this.zxid = a_.readLong("zxid");
        this.data = a_.readBuffer("data");
        final Index vidx1 = a_.startVector("authinfo");
        if (vidx1 != null) {
            this.authinfo = new ArrayList<Id>();
            while (!vidx1.done()) {
                final Id e1 = new Id();
                a_.readRecord(e1, "e1");
                this.authinfo.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("authinfo");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.type, "type");
            a_.writeLong(this.zxid, "zxid");
            a_.writeBuffer(this.data, "data");
            a_.startVector(this.authinfo, "authinfo");
            if (this.authinfo != null) {
                for (int len1 = this.authinfo.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final Id e1 = this.authinfo.get(vidx1);
                    a_.writeRecord(e1, "e1");
                }
            }
            a_.endVector(this.authinfo, "authinfo");
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
        throw new UnsupportedOperationException("comparing QuorumPacket is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof QuorumPacket)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final QuorumPacket peer = (QuorumPacket)peer_;
        boolean ret = false;
        ret = (this.type == peer.type);
        if (!ret) {
            return ret;
        }
        ret = (this.zxid == peer.zxid);
        if (!ret) {
            return ret;
        }
        ret = Utils.bufEquals(this.data, peer.data);
        if (!ret) {
            return ret;
        }
        ret = this.authinfo.equals(peer.authinfo);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.type;
        result = 37 * result + ret;
        ret = (int)(this.zxid ^ this.zxid >>> 32);
        result = 37 * result + ret;
        ret = Arrays.toString(this.data).hashCode();
        result = 37 * result + ret;
        ret = this.authinfo.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LQuorumPacket(ilB[LId(ss)])";
    }
}
