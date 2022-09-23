// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.proto;

import java.util.Arrays;
import org.apache.jute.Utils;
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
public class ConnectRequest implements Record
{
    private int protocolVersion;
    private long lastZxidSeen;
    private int timeOut;
    private long sessionId;
    private byte[] passwd;
    
    public ConnectRequest() {
    }
    
    public ConnectRequest(final int protocolVersion, final long lastZxidSeen, final int timeOut, final long sessionId, final byte[] passwd) {
        this.protocolVersion = protocolVersion;
        this.lastZxidSeen = lastZxidSeen;
        this.timeOut = timeOut;
        this.sessionId = sessionId;
        this.passwd = passwd;
    }
    
    public int getProtocolVersion() {
        return this.protocolVersion;
    }
    
    public void setProtocolVersion(final int m_) {
        this.protocolVersion = m_;
    }
    
    public long getLastZxidSeen() {
        return this.lastZxidSeen;
    }
    
    public void setLastZxidSeen(final long m_) {
        this.lastZxidSeen = m_;
    }
    
    public int getTimeOut() {
        return this.timeOut;
    }
    
    public void setTimeOut(final int m_) {
        this.timeOut = m_;
    }
    
    public long getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final long m_) {
        this.sessionId = m_;
    }
    
    public byte[] getPasswd() {
        return this.passwd;
    }
    
    public void setPasswd(final byte[] m_) {
        this.passwd = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.protocolVersion, "protocolVersion");
        a_.writeLong(this.lastZxidSeen, "lastZxidSeen");
        a_.writeInt(this.timeOut, "timeOut");
        a_.writeLong(this.sessionId, "sessionId");
        a_.writeBuffer(this.passwd, "passwd");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.protocolVersion = a_.readInt("protocolVersion");
        this.lastZxidSeen = a_.readLong("lastZxidSeen");
        this.timeOut = a_.readInt("timeOut");
        this.sessionId = a_.readLong("sessionId");
        this.passwd = a_.readBuffer("passwd");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.protocolVersion, "protocolVersion");
            a_.writeLong(this.lastZxidSeen, "lastZxidSeen");
            a_.writeInt(this.timeOut, "timeOut");
            a_.writeLong(this.sessionId, "sessionId");
            a_.writeBuffer(this.passwd, "passwd");
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
        if (!(peer_ instanceof ConnectRequest)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final ConnectRequest peer = (ConnectRequest)peer_;
        int ret = 0;
        ret = ((this.protocolVersion == peer.protocolVersion) ? 0 : ((this.protocolVersion < peer.protocolVersion) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.lastZxidSeen == peer.lastZxidSeen) ? 0 : ((this.lastZxidSeen < peer.lastZxidSeen) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.timeOut == peer.timeOut) ? 0 : ((this.timeOut < peer.timeOut) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.sessionId == peer.sessionId) ? 0 : ((this.sessionId < peer.sessionId) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        final byte[] my = this.passwd;
        final byte[] ur = peer.passwd;
        ret = Utils.compareBytes(my, 0, my.length, ur, 0, ur.length);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof ConnectRequest)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final ConnectRequest peer = (ConnectRequest)peer_;
        boolean ret = false;
        ret = (this.protocolVersion == peer.protocolVersion);
        if (!ret) {
            return ret;
        }
        ret = (this.lastZxidSeen == peer.lastZxidSeen);
        if (!ret) {
            return ret;
        }
        ret = (this.timeOut == peer.timeOut);
        if (!ret) {
            return ret;
        }
        ret = (this.sessionId == peer.sessionId);
        if (!ret) {
            return ret;
        }
        ret = Utils.bufEquals(this.passwd, peer.passwd);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.protocolVersion;
        result = 37 * result + ret;
        ret = (int)(this.lastZxidSeen ^ this.lastZxidSeen >>> 32);
        result = 37 * result + ret;
        ret = this.timeOut;
        result = 37 * result + ret;
        ret = (int)(this.sessionId ^ this.sessionId >>> 32);
        result = 37 * result + ret;
        ret = Arrays.toString(this.passwd).hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LConnectRequest(ililB)";
    }
}
