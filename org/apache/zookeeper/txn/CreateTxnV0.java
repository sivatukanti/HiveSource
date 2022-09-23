// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.txn;

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
public class CreateTxnV0 implements Record
{
    private String path;
    private byte[] data;
    private List<ACL> acl;
    private boolean ephemeral;
    
    public CreateTxnV0() {
    }
    
    public CreateTxnV0(final String path, final byte[] data, final List<ACL> acl, final boolean ephemeral) {
        this.path = path;
        this.data = data;
        this.acl = acl;
        this.ephemeral = ephemeral;
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
    
    public boolean getEphemeral() {
        return this.ephemeral;
    }
    
    public void setEphemeral(final boolean m_) {
        this.ephemeral = m_;
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
        a_.writeBool(this.ephemeral, "ephemeral");
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
        this.ephemeral = a_.readBool("ephemeral");
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
            a_.writeBool(this.ephemeral, "ephemeral");
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
        throw new UnsupportedOperationException("comparing CreateTxnV0 is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof CreateTxnV0)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final CreateTxnV0 peer = (CreateTxnV0)peer_;
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
        ret = (this.ephemeral == peer.ephemeral);
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
        ret = (this.ephemeral ? 0 : 1);
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LCreateTxnV0(sB[LACL(iLId(ss))]z)";
    }
}
