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
public class ExistsRequest implements Record
{
    private String path;
    private boolean watch;
    
    public ExistsRequest() {
    }
    
    public ExistsRequest(final String path, final boolean watch) {
        this.path = path;
        this.watch = watch;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String m_) {
        this.path = m_;
    }
    
    public boolean getWatch() {
        return this.watch;
    }
    
    public void setWatch(final boolean m_) {
        this.watch = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeString(this.path, "path");
        a_.writeBool(this.watch, "watch");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.path = a_.readString("path");
        this.watch = a_.readBool("watch");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeString(this.path, "path");
            a_.writeBool(this.watch, "watch");
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
        if (!(peer_ instanceof ExistsRequest)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final ExistsRequest peer = (ExistsRequest)peer_;
        int ret = 0;
        ret = this.path.compareTo(peer.path);
        if (ret != 0) {
            return ret;
        }
        ret = ((this.watch == peer.watch) ? 0 : (this.watch ? 1 : -1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof ExistsRequest)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final ExistsRequest peer = (ExistsRequest)peer_;
        boolean ret = false;
        ret = this.path.equals(peer.path);
        if (!ret) {
            return ret;
        }
        ret = (this.watch == peer.watch);
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
        ret = (this.watch ? 0 : 1);
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LExistsRequest(sz)";
    }
}
