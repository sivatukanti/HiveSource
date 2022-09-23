// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.proto.CheckVersionRequest;
import org.apache.zookeeper.proto.SetDataRequest;
import org.apache.zookeeper.proto.DeleteRequest;
import org.apache.zookeeper.proto.CreateRequest;
import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.zookeeper.proto.MultiHeader;
import org.apache.jute.OutputArchive;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.jute.Record;

public class MultiTransactionRecord implements Record, Iterable<Op>
{
    private List<Op> ops;
    
    public MultiTransactionRecord() {
        this.ops = new ArrayList<Op>();
    }
    
    public MultiTransactionRecord(final Iterable<Op> ops) {
        this.ops = new ArrayList<Op>();
        for (final Op op : ops) {
            this.add(op);
        }
    }
    
    @Override
    public Iterator<Op> iterator() {
        return this.ops.iterator();
    }
    
    public void add(final Op op) {
        this.ops.add(op);
    }
    
    public int size() {
        return this.ops.size();
    }
    
    @Override
    public void serialize(final OutputArchive archive, final String tag) throws IOException {
        archive.startRecord(this, tag);
        final int index = 0;
        for (final Op op : this.ops) {
            final MultiHeader h = new MultiHeader(op.getType(), false, -1);
            h.serialize(archive, tag);
            switch (op.getType()) {
                case 1: {
                    op.toRequestRecord().serialize(archive, tag);
                    continue;
                }
                case 2: {
                    op.toRequestRecord().serialize(archive, tag);
                    continue;
                }
                case 5: {
                    op.toRequestRecord().serialize(archive, tag);
                    continue;
                }
                case 13: {
                    op.toRequestRecord().serialize(archive, tag);
                    continue;
                }
                default: {
                    throw new IOException("Invalid type of op");
                }
            }
        }
        new MultiHeader(-1, true, -1).serialize(archive, tag);
        archive.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive archive, final String tag) throws IOException {
        archive.startRecord(tag);
        final MultiHeader h = new MultiHeader();
        h.deserialize(archive, tag);
        while (!h.getDone()) {
            switch (h.getType()) {
                case 1: {
                    final CreateRequest cr = new CreateRequest();
                    cr.deserialize(archive, tag);
                    this.add(Op.create(cr.getPath(), cr.getData(), cr.getAcl(), cr.getFlags()));
                    break;
                }
                case 2: {
                    final DeleteRequest dr = new DeleteRequest();
                    dr.deserialize(archive, tag);
                    this.add(Op.delete(dr.getPath(), dr.getVersion()));
                    break;
                }
                case 5: {
                    final SetDataRequest sdr = new SetDataRequest();
                    sdr.deserialize(archive, tag);
                    this.add(Op.setData(sdr.getPath(), sdr.getData(), sdr.getVersion()));
                    break;
                }
                case 13: {
                    final CheckVersionRequest cvr = new CheckVersionRequest();
                    cvr.deserialize(archive, tag);
                    this.add(Op.check(cvr.getPath(), cvr.getVersion()));
                    break;
                }
                default: {
                    throw new IOException("Invalid type of op");
                }
            }
            h.deserialize(archive, tag);
        }
        archive.endRecord(tag);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiTransactionRecord)) {
            return false;
        }
        final MultiTransactionRecord that = (MultiTransactionRecord)o;
        if (this.ops != null) {
            final Iterator<Op> other = that.ops.iterator();
            for (final Op op : this.ops) {
                final boolean hasMoreData = other.hasNext();
                if (!hasMoreData) {
                    return false;
                }
                final Op otherOp = other.next();
                if (!op.equals(otherOp)) {
                    return false;
                }
            }
            return !other.hasNext();
        }
        return that.ops == null;
    }
    
    @Override
    public int hashCode() {
        int h = 1023;
        for (final Op op : this.ops) {
            h = h * 25 + op.hashCode();
        }
        return h;
    }
}
