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
import org.apache.zookeeper.data.Stat;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class SetDataResponse implements Record
{
    private Stat stat;
    
    public SetDataResponse() {
    }
    
    public SetDataResponse(final Stat stat) {
        this.stat = stat;
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
        a_.writeRecord(this.stat, "stat");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        a_.readRecord(this.stat = new Stat(), "stat");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
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
        if (!(peer_ instanceof SetDataResponse)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final SetDataResponse peer = (SetDataResponse)peer_;
        int ret = 0;
        ret = this.stat.compareTo(peer.stat);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof SetDataResponse)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final SetDataResponse peer = (SetDataResponse)peer_;
        boolean ret = false;
        ret = this.stat.equals(peer.stat);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        final int ret = this.stat.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LSetDataResponse(LStat(lllliiiliil))";
    }
}
