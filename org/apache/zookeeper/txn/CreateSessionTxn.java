// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.txn;

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
public class CreateSessionTxn implements Record
{
    private int timeOut;
    
    public CreateSessionTxn() {
    }
    
    public CreateSessionTxn(final int timeOut) {
        this.timeOut = timeOut;
    }
    
    public int getTimeOut() {
        return this.timeOut;
    }
    
    public void setTimeOut(final int m_) {
        this.timeOut = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.writeInt(this.timeOut, "timeOut");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        this.timeOut = a_.readInt("timeOut");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.writeInt(this.timeOut, "timeOut");
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
        if (!(peer_ instanceof CreateSessionTxn)) {
            throw new ClassCastException("Comparing different types of records.");
        }
        final CreateSessionTxn peer = (CreateSessionTxn)peer_;
        int ret = 0;
        ret = ((this.timeOut == peer.timeOut) ? 0 : ((this.timeOut < peer.timeOut) ? -1 : 1));
        if (ret != 0) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof CreateSessionTxn)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final CreateSessionTxn peer = (CreateSessionTxn)peer_;
        boolean ret = false;
        ret = (this.timeOut == peer.timeOut);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        final int ret = this.timeOut;
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LCreateSessionTxn(i)";
    }
}
