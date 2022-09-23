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
import org.apache.jute.Index;
import java.util.ArrayList;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.jute.OutputArchive;
import java.util.List;
import org.apache.yetus.audience.InterfaceAudience;
import org.apache.jute.Record;

@InterfaceAudience.Public
public class MultiTxn implements Record
{
    private List<Txn> txns;
    
    public MultiTxn() {
    }
    
    public MultiTxn(final List<Txn> txns) {
        this.txns = txns;
    }
    
    public List<Txn> getTxns() {
        return this.txns;
    }
    
    public void setTxns(final List<Txn> m_) {
        this.txns = m_;
    }
    
    @Override
    public void serialize(final OutputArchive a_, final String tag) throws IOException {
        a_.startRecord(this, tag);
        a_.startVector(this.txns, "txns");
        if (this.txns != null) {
            for (int len1 = this.txns.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                final Txn e1 = this.txns.get(vidx1);
                a_.writeRecord(e1, "e1");
            }
        }
        a_.endVector(this.txns, "txns");
        a_.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive a_, final String tag) throws IOException {
        a_.startRecord(tag);
        final Index vidx1 = a_.startVector("txns");
        if (vidx1 != null) {
            this.txns = new ArrayList<Txn>();
            while (!vidx1.done()) {
                final Txn e1 = new Txn();
                a_.readRecord(e1, "e1");
                this.txns.add(e1);
                vidx1.incr();
            }
        }
        a_.endVector("txns");
        a_.endRecord(tag);
    }
    
    @Override
    public String toString() {
        try {
            final ByteArrayOutputStream s = new ByteArrayOutputStream();
            final CsvOutputArchive a_ = new CsvOutputArchive(s);
            a_.startRecord(this, "");
            a_.startVector(this.txns, "txns");
            if (this.txns != null) {
                for (int len1 = this.txns.size(), vidx1 = 0; vidx1 < len1; ++vidx1) {
                    final Txn e1 = this.txns.get(vidx1);
                    a_.writeRecord(e1, "e1");
                }
            }
            a_.endVector(this.txns, "txns");
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
        throw new UnsupportedOperationException("comparing MultiTxn is unimplemented");
    }
    
    @Override
    public boolean equals(final Object peer_) {
        if (!(peer_ instanceof MultiTxn)) {
            return false;
        }
        if (peer_ == this) {
            return true;
        }
        final MultiTxn peer = (MultiTxn)peer_;
        boolean ret = false;
        ret = this.txns.equals(peer.txns);
        if (!ret) {
            return ret;
        }
        return ret;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        final int ret = this.txns.hashCode();
        result = 37 * result + ret;
        return result;
    }
    
    public static String signature() {
        return "LMultiTxn([LTxn(iB)])";
    }
}
