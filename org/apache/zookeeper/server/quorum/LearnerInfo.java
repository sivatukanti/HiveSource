// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

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
public class LearnerInfo implements Record
{
    private long serverid;
    private int protocolVersion;
    
    public LearnerInfo() {
    }
    
    public LearnerInfo(final long serverid, final int protocolVersion) {
        this.serverid = serverid;
        this.protocolVersion = protocolVersion;
    }
    
    public long getServerid() {
        return this.serverid;
    }
    
    public void setServerid(final long m_) {
        this.serverid = m_;
    }
    
    public int getProtocolVersion() {
        return this.protocolVersion;
    }
    
    public void setProtocolVersion(final int m_) {
        this.protocolVersion = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeLong(this.serverid, "serverid");
        a_.writeInt(this.protocolVersion, "protocolVersion");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.serverid = a_.readLong("serverid");
        this.protocolVersion = a_.readInt("protocolVersion");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeLong(this.serverid, "serverid");
            a_.writeInt(this.protocolVersion, "protocolVersion");
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
        if (!(peer_ instanceof LearnerInfo)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final LearnerInfo peer = (LearnerInfo)peer_;
        int ret = 0;
        ret = ((this.serverid == peer.serverid) ? 0 : ((this.serverid < peer.serverid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.protocolVersion == peer.protocolVersion) ? 0 : ((this.protocolVersion < peer.protocolVersion) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof LearnerInfo)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final LearnerInfo peer = (LearnerInfo)peer_;
        boolean ret = false;
        ret = (this.serverid == peer.serverid);
        if (!ret) {
            return ret;
        }
        ret = (this.protocolVersion == peer.protocolVersion);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = (int)(this.serverid ^ this.serverid >>> 32);
        result = 37 * result + ret;
        ret = this.protocolVersion;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LLearnerInfo(li)";
    }
}
