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
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class Txn implements Record
{
    private int type;
    private byte[] data;
    
    public Txn() {
    }
    
    public Txn(final int type, final byte[] data) {
        this.type = type;
        this.data = data;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int m_) {
        this.type = m_;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public void setData(final byte[] m_) {
        this.data = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.type, "type");
        a_.writeBuffer(this.data, "data");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.type = a_.readInt("type");
        this.data = a_.readBuffer("data");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.type, "type");
            a_.writeBuffer(this.data, "data");
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
        if (!(peer_ instanceof Txn)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final Txn peer = (Txn)peer_;
        int ret = 0;
        ret = ((this.type == peer.type) ? 0 : ((this.type < peer.type) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        final byte[] my = this.data;
        final byte[] ur = peer.data;
        ret = Utils.compareBytes(my, 0, my.length, ur, 0, ur.length);
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof Txn)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final Txn peer = (Txn)peer_;
        boolean ret = false;
        ret = (this.type == peer.type);
        if (!ret) {
            return ret;
        }
        ret = Utils.bufEquals(this.data, peer.data);
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
        ret = Arrays.toString(this.data).hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LTxn(iB)";
    }
}
