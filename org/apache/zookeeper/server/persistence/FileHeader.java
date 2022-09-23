// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.persistence;

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
public class FileHeader implements Record
{
    private int magic;
    private int version;
    private long dbid;
    
    public FileHeader() {
    }
    
    public FileHeader(final int magic, final int version, final long dbid) {
        this.magic = magic;
        this.version = version;
        this.dbid = dbid;
    }
    
    public int getMagic() {
        return this.magic;
    }
    
    public void setMagic(final int m_) {
        this.magic = m_;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int m_) {
        this.version = m_;
    }
    
    public long getDbid() {
        return this.dbid;
    }
    
    public void setDbid(final long m_) {
        this.dbid = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.magic, "magic");
        a_.writeInt(this.version, "version");
        a_.writeLong(this.dbid, "dbid");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.magic = a_.readInt("magic");
        this.version = a_.readInt("version");
        this.dbid = a_.readLong("dbid");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.magic, "magic");
            a_.writeInt(this.version, "version");
            a_.writeLong(this.dbid, "dbid");
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
        if (!(peer_ instanceof FileHeader)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final FileHeader peer = (FileHeader)peer_;
        int ret = 0;
        ret = ((this.magic == peer.magic) ? 0 : ((this.magic < peer.magic) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.version == peer.version) ? 0 : ((this.version < peer.version) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.dbid == peer.dbid) ? 0 : ((this.dbid < peer.dbid) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof FileHeader)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final FileHeader peer = (FileHeader)peer_;
        boolean ret = false;
        ret = (this.magic == peer.magic);
        if (!ret) {
            return ret;
        }
        ret = (this.version == peer.version);
        if (!ret) {
            return ret;
        }
        ret = (this.dbid == peer.dbid);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.magic;
        result = 37 * result + ret;
        ret = this.version;
        result = 37 * result + ret;
        ret = (int)(this.dbid ^ this.dbid >>> 32);
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LFileHeader(iil)";
    }
}
