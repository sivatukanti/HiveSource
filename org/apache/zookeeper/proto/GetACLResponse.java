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
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class GetACLResponse implements Record
{
    private List<ACL> acl;
    private Stat stat;
    
    public GetACLResponse() {
    }
    
    public GetACLResponse(final List<ACL> acl, final Stat stat) {
        this.acl = acl;
        this.stat = stat;
    }
    
    public List<ACL> getAcl() {
        return this.acl;
    }
    
    public void setAcl(final List<ACL> m_) {
        this.acl = m_;
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
        a_.startVector(this.acl, "acl");
        if (this.acl != null) {
            for (int len1 = this.acl.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final ACL e1 = this.acl.get(vidx1);
                a_.writeRecord(e1, "e1");
            }
        }
        a_.endVector(this.acl, "acl");
        a_.writeRecord(this.stat, "stat");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
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
        a_.readRecord(this.stat = new Stat(), "stat");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.startVector(this.acl, "acl");
            if (this.acl != null) {
                for (int len1 = this.acl.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final ACL e1 = this.acl.get(vidx1);
                    a_.writeRecord(e1, "e1");
                }
            }
            a_.endVector(this.acl, "acl");
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
        throw new UnsupportedOperationException("comparing GetACLResponse is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof GetACLResponse)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final GetACLResponse peer = (GetACLResponse)peer_;
        boolean ret = false;
        ret = this.acl.equals(peer.acl);
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
        int ret = this.acl.hashCode();
        result = 37 * result + ret;
        ret = this.stat.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LGetACLResponse([LACL(iLId(ss))]LStat(lllliiiliil))";
    }
}
