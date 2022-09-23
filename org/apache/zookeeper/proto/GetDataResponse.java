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
import org.apache.zookeeper.data.Stat;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class GetDataResponse implements Record
{
    private byte[] data;
    private Stat stat;
    
    public GetDataResponse() {
    }
    
    public GetDataResponse(final byte[] data, final Stat stat) {
        this.data = data;
        this.stat = stat;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] m_) {
        this.data = m_;
    }
    
    public Stat getStat() {
        return this.stat;
    }
    
    public void setStat(final Stat m_) {
        this.stat = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeBuffer(this.data, "data");
        a_.writeRecord(this.stat, "stat");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.data = a_.readBuffer("data");
        a_.readRecord(this.stat = new Stat(), "stat");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeBuffer(this.data, "data");
            a_.writeRecord(this.stat, "stat");
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
        if (!(peer_ instanceof GetDataResponse)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final GetDataResponse peer = (GetDataResponse)peer_;
        int ret = 0;
        final byte[] my = this.data;
        final byte[] ur = peer.data;
        ret = Utils.compareBytes(my, 0, my.length, ur, 0, ur.length);
        if (ret != 0) {
            return ret;
        }
        ret = this.stat.compareTo(peer.stat);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof GetDataResponse)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final GetDataResponse peer = (GetDataResponse)peer_;
        boolean ret = false;
        ret = Utils.bufEquals(this.data, peer.data);
        if (!ret) {
            return ret;
        }
        ret = this.stat.equals(peer.stat);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = Arrays.toString(this.data).hashCode();
        result = 37 * result + ret;
        ret = this.stat.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LGetDataResponse(BLStat(lllliiiliil))";
    }
}
