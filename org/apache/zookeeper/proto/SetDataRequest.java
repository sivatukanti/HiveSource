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
public class SetDataRequest implements Record
{
    private String path;
    private byte[] data;
    private int version;
    
    public SetDataRequest() {
    }
    
    public SetDataRequest(final String path, final byte[] data, final int version) {
        this.path = path;
        this.data = data;
        this.version = version;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String m_) {
        this.path = m_;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] m_) {
        this.data = m_;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public void setVersion(final int m_) {
        this.version = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeString(this.path, "path");
        a_.writeBuffer(this.data, "data");
        a_.writeInt(this.version, "version");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.path = a_.readString("path");
        this.data = a_.readBuffer("data");
        this.version = a_.readInt("version");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeString(this.path, "path");
            a_.writeBuffer(this.data, "data");
            a_.writeInt(this.version, "version");
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
        if (!(peer_ instanceof SetDataRequest)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final SetDataRequest peer = (SetDataRequest)peer_;
        int ret = 0;
        ret = this.path.compareTo(peer.path);
        if (ret != 0) {
            return ret;
        }
        final byte[] my = this.data;
        final byte[] ur = peer.data;
        ret = Utils.compareBytes(my, 0, my.length, ur, 0, ur.length);
        if (ret != 0) {
            return ret;
        }
        ret = ((this.version == peer.version) ? 0 : ((this.version < peer.version) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof SetDataRequest)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final SetDataRequest peer = (SetDataRequest)peer_;
        boolean ret = false;
        ret = this.path.equals(peer.path);
        if (!ret) {
            return ret;
        }
        ret = Utils.bufEquals(this.data, peer.data);
        if (!ret) {
            return ret;
        }
        ret = (this.version == peer.version);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.path.hashCode();
        result = 37 * result + ret;
        ret = Arrays.toString(this.data).hashCode();
        result = 37 * result + ret;
        ret = this.version;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LSetDataRequest(sBi)";
    }
}
