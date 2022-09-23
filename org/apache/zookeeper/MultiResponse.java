// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.jute.InputArchive;
import java.io.IOException;
import org.apache.zookeeper.proto.ErrorResponse;
import org.apache.zookeeper.proto.SetDataResponse;
import org.apache.zookeeper.proto.CreateResponse;
import org.apache.zookeeper.proto.MultiHeader;
import org.apache.jute.OutputArchive;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.jute.Record;

public class MultiResponse implements Record, Iterable<OpResult>
{
    private List<OpResult> results;
    
    public MultiResponse() {
        this.results = new ArrayList<OpResult>();
    }
    
    public void add(final OpResult x) {
        this.results.add(x);
    }
    
    @Override
    public Iterator<OpResult> iterator() {
        return this.results.iterator();
    }
    
    public int size() {
        return this.results.size();
    }
    
    @Override
    public void serialize(final OutputArchive archive, final String tag) throws IOException {
        archive.startRecord(this, tag);
        final int index = 0;
        for (final OpResult result : this.results) {
            final int err = (result.getType() == -1) ? ((OpResult.ErrorResult)result).getErr() : 0;
            new MultiHeader(result.getType(), false, err).serialize(archive, tag);
            switch (result.getType()) {
                case 1: {
                    new CreateResponse(((OpResult.CreateResult)result).getPath()).serialize(archive, tag);
                    continue;
                }
                case 2:
                case 13: {
                    continue;
                }
                case 5: {
                    new SetDataResponse(((OpResult.SetDataResult)result).getStat()).serialize(archive, tag);
                    continue;
                }
                case -1: {
                    new ErrorResponse(((OpResult.ErrorResult)result).getErr()).serialize(archive, tag);
                    continue;
                }
                default: {
                    throw new IOException("Invalid type " + result.getType() + " in MultiResponse");
                }
            }
        }
        new MultiHeader(-1, true, -1).serialize(archive, tag);
        archive.endRecord(this, tag);
    }
    
    @Override
    public void deserialize(final InputArchive archive, final String tag) throws IOException {
        this.results = new ArrayList<OpResult>();
        archive.startRecord(tag);
        final MultiHeader h = new MultiHeader();
        h.deserialize(archive, tag);
        while (!h.getDone()) {
            switch (h.getType()) {
                case 1: {
                    final CreateResponse cr = new CreateResponse();
                    cr.deserialize(archive, tag);
                    this.results.add(new OpResult.CreateResult(cr.getPath()));
                    break;
                }
                case 2: {
                    this.results.add(new OpResult.DeleteResult());
                    break;
                }
                case 5: {
                    final SetDataResponse sdr = new SetDataResponse();
                    sdr.deserialize(archive, tag);
                    this.results.add(new OpResult.SetDataResult(sdr.getStat()));
                    break;
                }
                case 13: {
                    this.results.add(new OpResult.CheckResult());
                    break;
                }
                case -1: {
                    final ErrorResponse er = new ErrorResponse();
                    er.deserialize(archive, tag);
                    this.results.add(new OpResult.ErrorResult(er.getErr()));
                    break;
                }
                default: {
                    throw new IOException("Invalid type " + h.getType() + " in MultiResponse");
                }
            }
            h.deserialize(archive, tag);
        }
        archive.endRecord(tag);
    }
    
    public List<OpResult> getResultList() {
        return this.results;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiResponse)) {
            return false;
        }
        final MultiResponse other = (MultiResponse)o;
        if (this.results != null) {
            final Iterator<OpResult> i = other.results.iterator();
            for (final OpResult result : this.results) {
                if (!i.hasNext()) {
                    return false;
                }
                if (!result.equals(i.next())) {
                    return false;
                }
            }
            return !i.hasNext();
        }
        return other.results == null;
    }
    
    @Override
    public int hashCode() {
        int hash = this.results.size();
        for (final OpResult result : this.results) {
            hash = hash * 35 + result.hashCode();
        }
        return hash;
    }
}
