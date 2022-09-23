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
public class GetMaxChildrenResponse implements Record
{
    private int max;
    
    public GetMaxChildrenResponse() {
    }
    
    public GetMaxChildrenResponse(final int max) {
        this.max = max;
    }
    
    public int getMax() {
        return this.max;
    }
    
    public void setMax(final int m_) {
        this.max = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.max, "max");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.max = a_.readInt("max");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.max, "max");
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
        if (!(peer_ instanceof GetMaxChildrenResponse)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final GetMaxChildrenResponse peer = (GetMaxChildrenResponse)peer_;
        int ret = 0;
        ret = ((this.max == peer.max) ? 0 : ((this.max < peer.max) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof GetMaxChildrenResponse)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final GetMaxChildrenResponse peer = (GetMaxChildrenResponse)peer_;
        boolean ret = false;
        ret = (this.max == peer.max);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        final int ret = this.max;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LGetMaxChildrenResponse(i)";
    }
}
