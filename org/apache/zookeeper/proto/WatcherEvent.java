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
public class WatcherEvent implements Record
{
    private int type;
    private int state;
    private String path;
    
    public WatcherEvent() {
    }
    
    public WatcherEvent(final int type, final int state, final String path) {
        this.type = type;
        this.state = state;
        this.path = path;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int m_) {
        this.type = m_;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int m_) {
        this.state = m_;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String m_) {
        this.path = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.type, "type");
        a_.writeInt(this.state, "state");
        a_.writeString(this.path, "path");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.type = a_.readInt("type");
        this.state = a_.readInt("state");
        this.path = a_.readString("path");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.type, "type");
            a_.writeInt(this.state, "state");
            a_.writeString(this.path, "path");
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
        if (!(peer_ instanceof WatcherEvent)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final WatcherEvent peer = (WatcherEvent)peer_;
        int ret = 0;
        ret = ((this.type == peer.type) ? 0 : ((this.type < peer.type) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.state == peer.state) ? 0 : ((this.state < peer.state) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = this.path.compareTo(peer.path);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof WatcherEvent)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final WatcherEvent peer = (WatcherEvent)peer_;
        boolean ret = false;
        ret = (this.type == peer.type);
        if (!ret) {
            return ret;
        }
        ret = (this.state == peer.state);
        if (!ret) {
            return ret;
        }
        ret = this.path.equals(peer.path);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = this.type;
        result = 37 * result + ret;
        ret = this.state;
        result = 37 * result + ret;
        ret = this.path.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LWatcherEvent(iis)";
    }
}
