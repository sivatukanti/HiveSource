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
import org.apache.jute.Index;
import java.util.ArrayList;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class CreateRequest implements Record
{
    private String path;
    private byte[] data;
    private List<ACL> acl;
    private int flags;
    
    public CreateRequest() {
    }
    
    public CreateRequest(final String path, final byte[] data, final List<ACL> acl, final int flags) {
        this.path = path;
        this.data = data;
        this.acl = acl;
        this.flags = flags;
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
    
    public List<ACL> getAcl() {
        return this.acl;
    }
    
    public void setAcl(final List<ACL> m_) {
        this.acl = m_;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public void setFlags(final int m_) {
        this.flags = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeString(this.path, "path");
        a_.writeBuffer(this.data, "data");
        a_.startVector(this.acl, "acl");
        if (this.acl != null) {
            for (int len1 = this.acl.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final ACL e1 = this.acl.get(vidx1);
                a_.writeRecord(e1, "e1");
            }
        }
        a_.endVector(this.acl, "acl");
        a_.writeInt(this.flags, "flags");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.path = a_.readString("path");
        this.data = a_.readBuffer("data");
        final Index vidx1 = a_.startVector("acl");
        if (vidx1 != null) {
            this.acl = new ArrayList<ACL>();
            while (!vidx1.done()) {
                final ACL e1 = new ACL();
                a_.readRecord(e1, "e1");
                this.acl.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("acl");
        this.flags = a_.readInt("flags");
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
            a_.startVector(this.acl, "acl");
            if (this.acl != null) {
                for (int len1 = this.acl.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final ACL e1 = this.acl.get(vidx1);
                    a_.writeRecord(e1, "e1");
                }
            }
            a_.endVector(this.acl, "acl");
            a_.writeInt(this.flags, "flags");
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
        throw new UnsupportedOperationException("comparing CreateRequest is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof CreateRequest)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final CreateRequest peer = (CreateRequest)peer_;
        boolean ret = false;
        ret = this.path.equals(peer.path);
        if (!ret) {
            return ret;
        }
        ret = Utils.bufEquals(this.data, peer.data);
        if (!ret) {
            return ret;
        }
        ret = this.acl.equals(peer.acl);
        if (!ret) {
            return ret;
        }
        ret = (this.flags == peer.flags);
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
        ret = this.acl.hashCode();
        result = 37 * result + ret;
        ret = this.flags;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LCreateRequest(sB[LACL(iLId(ss))]i)";
    }
}
