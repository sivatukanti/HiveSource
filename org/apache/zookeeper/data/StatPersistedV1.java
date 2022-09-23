// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.data;

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
public class StatPersistedV1 implements Record
{
    private long czxid;
    private long mzxid;
    private long ctime;
    private long mtime;
    private int version;
    private int cversion;
    private int aversion;
    private long ephemeralOwner;
    
    public StatPersistedV1() {
    }
    
    public StatPersistedV1(final long czxid, final long mzxid, final long ctime, final long mtime, final int version, final int cversion, final int aversion, final long ephemeralOwner) {
        this.czxid = czxid;
        this.mzxid = mzxid;
        this.ctime = ctime;
        this.mtime = mtime;
        this.version = version;
        this.cversion = cversion;
        this.aversion = aversion;
        this.ephemeralOwner = ephemeralOwner;
    }
    
    public long getCzxid() {
        return this.czxid;
    }
    
    public void setCzxid(final long m_) {
        this.czxid = m_;
    }
    
    public long getMzxid() {
        return this.mzxid;
    }
    
    public void setMzxid(final long m_) {
        this.mzxid = m_;
    }
    
    public long getCtime() {
        return this.ctime;
    }
    
    public void setCtime(final long m_) {
        this.ctime = m_;
    }
    
    public long getMtime() {
        return this.mtime;
    }
    
    public void setMtime(final long m_) {
        this.mtime = m_;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int m_) {
        this.version = m_;
    }
    
    public int getCversion() {
        return this.cversion;
    }
    
    public void setCversion(final int m_) {
        this.cversion = m_;
    }
    
    public int getAversion() {
        return this.aversion;
    }
    
    public void setAversion(final int m_) {
        this.aversion = m_;
    }
    
    public long getEphemeralOwner() {
        return this.ephemeralOwner;
    }
    
    public void setEphemeralOwner(final long m_) {
        this.ephemeralOwner = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeLong(this.czxid, "czxid");
        a_.writeLong(this.mzxid, "mzxid");
        a_.writeLong(this.ctime, "ctime");
        a_.writeLong(this.mtime, "mtime");
        a_.writeInt(this.version, "version");
        a_.writeInt(this.cversion, "cversion");
        a_.writeInt(this.aversion, "aversion");
        a_.writeLong(this.ephemeralOwner, "ephemeralOwner");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.czxid = a_.readLong("czxid");
        this.mzxid = a_.readLong("mzxid");
        this.ctime = a_.readLong("ctime");
        this.mtime = a_.readLong("mtime");
        this.version = a_.readInt("version");
        this.cversion = a_.readInt("cversion");
        this.aversion = a_.readInt("aversion");
        this.ephemeralOwner = a_.readLong("ephemeralOwner");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeLong(this.czxid, "czxid");
            a_.writeLong(this.mzxid, "mzxid");
            a_.writeLong(this.ctime, "ctime");
            a_.writeLong(this.mtime, "mtime");
            a_.writeInt(this.version, "version");
            a_.writeInt(this.cversion, "cversion");
            a_.writeInt(this.aversion, "aversion");
            a_.writeLong(this.ephemeralOwner, "ephemeralOwner");
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
        if (!(peer_ instanceof StatPersistedV1)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final StatPersistedV1 peer = (StatPersistedV1)peer_;
        int ret = 0;
        ret = ((this.czxid == peer.czxid) ? 0 : ((this.czxid < peer.czxid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.mzxid == peer.mzxid) ? 0 : ((this.mzxid < peer.mzxid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.ctime == peer.ctime) ? 0 : ((this.ctime < peer.ctime) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.mtime == peer.mtime) ? 0 : ((this.mtime < peer.mtime) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.version == peer.version) ? 0 : ((this.version < peer.version) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.cversion == peer.cversion) ? 0 : ((this.cversion < peer.cversion) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.aversion == peer.aversion) ? 0 : ((this.aversion < peer.aversion) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.ephemeralOwner == peer.ephemeralOwner) ? 0 : ((this.ephemeralOwner < peer.ephemeralOwner) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof StatPersistedV1)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final StatPersistedV1 peer = (StatPersistedV1)peer_;
        boolean ret = false;
        ret = (this.czxid == peer.czxid);
        if (!ret) {
            return ret;
        }
        ret = (this.mzxid == peer.mzxid);
        if (!ret) {
            return ret;
        }
        ret = (this.ctime == peer.ctime);
        if (!ret) {
            return ret;
        }
        ret = (this.mtime == peer.mtime);
        if (!ret) {
            return ret;
        }
        ret = (this.version == peer.version);
        if (!ret) {
            return ret;
        }
        ret = (this.cversion == peer.cversion);
        if (!ret) {
            return ret;
        }
        ret = (this.aversion == peer.aversion);
        if (!ret) {
            return ret;
        }
        ret = (this.ephemeralOwner == peer.ephemeralOwner);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = (int)(this.czxid ^ this.czxid >>> 32);
        result = 37 * result + ret;
        ret = (int)(this.mzxid ^ this.mzxid >>> 32);
        result = 37 * result + ret;
        ret = (int)(this.ctime ^ this.ctime >>> 32);
        result = 37 * result + ret;
        ret = (int)(this.mtime ^ this.mtime >>> 32);
        result = 37 * result + ret;
        ret = this.version;
        result = 37 * result + ret;
        ret = this.cversion;
        result = 37 * result + ret;
        ret = this.aversion;
        result = 37 * result + ret;
        ret = (int)(this.ephemeralOwner ^ this.ephemeralOwner >>> 32);
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LStatPersistedV1(lllliiil)";
    }
}
