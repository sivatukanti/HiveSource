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
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class SetSASLRequest implements Record
{
    private byte[] token;
    
    public SetSASLRequest() {
    }
    
    public SetSASLRequest(final byte[] token) {
        this.token = token;
    }
    
    public byte[] getToken() {
        return this.token;
    }
    
    public void setToken(final byte[] m_) {
        this.token = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeBuffer(this.token, "token");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.token = a_.readBuffer("token");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeBuffer(this.token, "token");
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
        if (!(peer_ instanceof SetSASLRequest)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final SetSASLRequest peer = (SetSASLRequest)peer_;
        int ret = 0;
        final byte[] my = this.token;
        final byte[] ur = peer.token;
        ret = Utils.compareBytes(my, 0, my.length, ur, 0, ur.length);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof SetSASLRequest)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final SetSASLRequest peer = (SetSASLRequest)peer_;
        boolean ret = false;
        ret = Utils.bufEquals(this.token, peer.token);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        final int ret = Arrays.toString(this.token).hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LSetSASLRequest(B)";
    }
}
