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
public class GetChildrenResponse implements Record
{
    private List<String> children;
    
    public GetChildrenResponse() {
    }
    
    public GetChildrenResponse(final List<String> children) {
        this.children = children;
    }
    
    public List<String> getChildren() {
        return this.children;
    }
    
    public void setChildren(final List<String> m_) {
        this.children = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.startVector(this.children, "children");
        if (this.children != null) {
            for (int len1 = this.children.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final String e1 = this.children.get(vidx1);
                a_.writeString(e1, "e1");
            }
        }
        a_.endVector(this.children, "children");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        final Index vidx1 = a_.startVector("children");
        if (vidx1 != null) {
            this.children = new ArrayList<String>();
            while (!vidx1.done()) {
                final String e1 = a_.readString("e1");
                this.children.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("children");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.startVector(this.children, "children");
            if (this.children != null) {
                for (int len1 = this.children.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final String e1 = this.children.get(vidx1);
                    a_.writeString(e1, "e1");
                }
            }
            a_.endVector(this.children, "children");
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
        throw new UnsupportedOperationException("comparing GetChildrenResponse is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof GetChildrenResponse)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final GetChildrenResponse peer = (GetChildrenResponse)peer_;
        boolean ret = false;
        ret = this.children.equals(peer.children);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        final int ret = this.children.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LGetChildrenResponse([s])";
    }
}
