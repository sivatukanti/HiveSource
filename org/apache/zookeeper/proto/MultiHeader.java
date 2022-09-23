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
public class MultiHeader implements Record
{
    private int type;
    private boolean done;
    private int err;
    
    public MultiHeader() {
    }
    
    public MultiHeader(final int type, final boolean done, final int err) {
        this.type = type;
        this.done = done;
        this.err = err;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int m_) {
        this.type = m_;
    }
    
    public boolean getDone() {
        return this.done;
    }
    
    public void setDone(final boolean m_) {
        this.done = m_;
    }
    
    public int getErr() {
        return this.err;
    }
    
    public void setErr(final int m_) {
        this.err = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.type, "type");
        a_.writeBool(this.done, "done");
        a_.writeInt(this.err, "err");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.type = a_.readInt("type");
        this.done = a_.readBool("done");
        this.err = a_.readInt("err");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.type, "type");
            a_.writeBool(this.done, "done");
            a_.writeInt(this.err, "err");
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
        if (!(peer_ instanceof MultiHeader)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final MultiHeader peer = (MultiHeader)peer_;
        int ret = 0;
        ret = ((this.type == peer.type) ? 0 : ((this.type < peer.type) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.done == peer.done) ? 0 : (this.done ? 1 : -1));
        if (ret != 0) {
            return ret;
        }
        ret = ((this.err == peer.err) ? 0 : ((this.err < peer.err) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof MultiHeader)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final MultiHeader peer = (MultiHeader)peer_;
        boolean ret = false;
        ret = (this.type == peer.type);
        if (!ret) {
            return ret;
        }
        ret = (this.done == peer.done);
        if (!ret) {
            return ret;
        }
        ret = (this.err == peer.err);
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
        ret = (this.done ? 0 : 1);
        result = 37 * result + ret;
        ret = this.err;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LMultiHeader(izi)";
    }
}
