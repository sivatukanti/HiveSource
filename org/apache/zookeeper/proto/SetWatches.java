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
import org.apache.jute.Index;
import java.util.ArrayList;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class SetWatches implements Record
{
    private long relativeZxid;
    private List<String> dataWatches;
    private List<String> existWatches;
    private List<String> childWatches;
    
    public SetWatches() {
    }
    
    public SetWatches(final long relativeZxid, final List<String> dataWatches, final List<String> existWatches, final List<String> childWatches) {
        this.relativeZxid = relativeZxid;
        this.dataWatches = dataWatches;
        this.existWatches = existWatches;
        this.childWatches = childWatches;
    }
    
    public long getRelativeZxid() {
        return this.relativeZxid;
    }
    
    public void setRelativeZxid(final long m_) {
        this.relativeZxid = m_;
    }
    
    public List<String> getDataWatches() {
        return this.dataWatches;
    }
    
    public void setDataWatches(final List<String> m_) {
        this.dataWatches = m_;
    }
    
    public List<String> getExistWatches() {
        return this.existWatches;
    }
    
    public void setExistWatches(final List<String> m_) {
        this.existWatches = m_;
    }
    
    public List<String> getChildWatches() {
        return this.childWatches;
    }
    
    public void setChildWatches(final List<String> m_) {
        this.childWatches = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeLong(this.relativeZxid, "relativeZxid");
        a_.startVector(this.dataWatches, "dataWatches");
        if (this.dataWatches != null) {
            for (int len1 = this.dataWatches.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final String e1 = this.dataWatches.get(vidx1);
                a_.writeString(e1, "e1");
            }
        }
        a_.endVector(this.dataWatches, "dataWatches");
        a_.startVector(this.existWatches, "existWatches");
        if (this.existWatches != null) {
            for (int len1 = this.existWatches.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final String e1 = this.existWatches.get(vidx1);
                a_.writeString(e1, "e1");
            }
        }
        a_.endVector(this.existWatches, "existWatches");
        a_.startVector(this.childWatches, "childWatches");
        if (this.childWatches != null) {
            for (int len1 = this.childWatches.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final String e1 = this.childWatches.get(vidx1);
                a_.writeString(e1, "e1");
            }
        }
        a_.endVector(this.childWatches, "childWatches");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.relativeZxid = a_.readLong("relativeZxid");
        Index vidx1 = a_.startVector("dataWatches");
        if (vidx1 != null) {
            this.dataWatches = new ArrayList<String>();
            while (!vidx1.done()) {
                final String e1 = a_.readString("e1");
                this.dataWatches.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("dataWatches");
        vidx1 = a_.startVector("existWatches");
        if (vidx1 != null) {
            this.existWatches = new ArrayList<String>();
            while (!vidx1.done()) {
                final String e1 = a_.readString("e1");
                this.existWatches.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("existWatches");
        vidx1 = a_.startVector("childWatches");
        if (vidx1 != null) {
            this.childWatches = new ArrayList<String>();
            while (!vidx1.done()) {
                final String e1 = a_.readString("e1");
                this.childWatches.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("childWatches");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeLong(this.relativeZxid, "relativeZxid");
            a_.startVector(this.dataWatches, "dataWatches");
            if (this.dataWatches != null) {
                for (int len1 = this.dataWatches.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final String e1 = this.dataWatches.get(vidx1);
                    a_.writeString(e1, "e1");
                }
            }
            a_.endVector(this.dataWatches, "dataWatches");
            a_.startVector(this.existWatches, "existWatches");
            if (this.existWatches != null) {
                for (int len1 = this.existWatches.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final String e1 = this.existWatches.get(vidx1);
                    a_.writeString(e1, "e1");
                }
            }
            a_.endVector(this.existWatches, "existWatches");
            a_.startVector(this.childWatches, "childWatches");
            if (this.childWatches != null) {
                for (int len1 = this.childWatches.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final String e1 = this.childWatches.get(vidx1);
                    a_.writeString(e1, "e1");
                }
            }
            a_.endVector(this.childWatches, "childWatches");
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
        throw new UnsupportedOperationException("comparing SetWatches is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof SetWatches)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final SetWatches peer = (SetWatches)peer_;
        boolean ret = false;
        ret = (this.relativeZxid == peer.relativeZxid);
        if (!ret) {
            return ret;
        }
        ret = this.dataWatches.equals(peer.dataWatches);
        if (!ret) {
            return ret;
        }
        ret = this.existWatches.equals(peer.existWatches);
        if (!ret) {
            return ret;
        }
        ret = this.childWatches.equals(peer.childWatches);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        int ret = (int)(this.relativeZxid ^ this.relativeZxid >>> 32);
        result = 37 * result + ret;
        ret = this.dataWatches.hashCode();
        result = 37 * result + ret;
        ret = this.existWatches.hashCode();
        result = 37 * result + ret;
        ret = this.childWatches.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LSetWatches(l[s][s][s])";
    }
}
