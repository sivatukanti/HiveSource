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
public class Id implements Record
{
    private String scheme;
    private String id;
    
    public Id() {
    }
    
    public Id(final String scheme, final String id) {
        this.scheme = scheme;
        this.id = id;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public void setScheme(final String m_) {
        this.scheme = m_;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String m_) {
        this.id = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeString(this.scheme, "scheme");
        a_.writeString(this.id, "id");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.scheme = a_.readString("scheme");
        this.id = a_.readString("id");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeString(this.scheme, "scheme");
            a_.writeString(this.id, "id");
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
        if (!(peer_ instanceof Id)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final Id peer = (Id)peer_;
        int ret = 0;
        ret = this.scheme.compareTo(peer.scheme);
        if (ret != 0) {
            return ret;
        }
        ret = this.id.compareTo(peer.id);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof Id)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final Id peer = (Id)peer_;
        boolean ret = false;
        ret = this.scheme.equals(peer.scheme);
        if (!ret) {
            return ret;
        }
        ret = this.id.equals(peer.id);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.scheme.hashCode();
        result = 37 * result + ret;
        ret = this.id.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LId(ss)";
    }
}
