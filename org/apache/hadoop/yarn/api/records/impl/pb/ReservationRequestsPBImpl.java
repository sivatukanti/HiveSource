// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.ReservationRequestInterpreter;
import org.apache.hadoop.yarn.api.records.ReservationRequest;
import java.util.List;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ReservationRequests;

public class ReservationRequestsPBImpl extends ReservationRequests
{
    YarnProtos.ReservationRequestsProto proto;
    YarnProtos.ReservationRequestsProto.Builder builder;
    boolean viaProto;
    public List<ReservationRequest> reservationRequests;
    
    public ReservationRequestsPBImpl() {
        this.proto = YarnProtos.ReservationRequestsProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.ReservationRequestsProto.newBuilder();
    }
    
    public ReservationRequestsPBImpl(final YarnProtos.ReservationRequestsProto proto) {
        this.proto = YarnProtos.ReservationRequestsProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ReservationRequestsProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.reservationRequests != null) {
            this.addReservationResourcesToProto();
        }
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ReservationRequestsProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public List<ReservationRequest> getReservationResources() {
        this.initReservationRequestsList();
        return this.reservationRequests;
    }
    
    @Override
    public void setReservationResources(final List<ReservationRequest> resources) {
        if (resources == null) {
            this.builder.clearReservationResources();
            return;
        }
        this.reservationRequests = resources;
    }
    
    @Override
    public ReservationRequestInterpreter getInterpreter() {
        final YarnProtos.ReservationRequestsProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasInterpreter()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getInterpreter());
    }
    
    @Override
    public void setInterpreter(final ReservationRequestInterpreter interpreter) {
        this.maybeInitBuilder();
        if (interpreter == null) {
            this.builder.clearInterpreter();
            return;
        }
        this.builder.setInterpreter(this.convertToProtoFormat(interpreter));
    }
    
    private void initReservationRequestsList() {
        if (this.reservationRequests != null) {
            return;
        }
        final YarnProtos.ReservationRequestsProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        final List<YarnProtos.ReservationRequestProto> resourceProtos = p.getReservationResourcesList();
        this.reservationRequests = new ArrayList<ReservationRequest>();
        for (final YarnProtos.ReservationRequestProto r : resourceProtos) {
            this.reservationRequests.add(this.convertFromProtoFormat(r));
        }
    }
    
    private void addReservationResourcesToProto() {
        this.maybeInitBuilder();
        this.builder.clearReservationResources();
        if (this.reservationRequests == null) {
            return;
        }
        final Iterable<YarnProtos.ReservationRequestProto> iterable = new Iterable<YarnProtos.ReservationRequestProto>() {
            @Override
            public Iterator<YarnProtos.ReservationRequestProto> iterator() {
                return new Iterator<YarnProtos.ReservationRequestProto>() {
                    Iterator<ReservationRequest> iter = ReservationRequestsPBImpl.this.reservationRequests.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.iter.hasNext();
                    }
                    
                    @Override
                    public YarnProtos.ReservationRequestProto next() {
                        return ReservationRequestsPBImpl.this.convertToProtoFormat(this.iter.next());
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        this.builder.addAllReservationResources(iterable);
    }
    
    private YarnProtos.ReservationRequestProto convertToProtoFormat(final ReservationRequest r) {
        return ((ReservationRequestPBImpl)r).getProto();
    }
    
    private ReservationRequestPBImpl convertFromProtoFormat(final YarnProtos.ReservationRequestProto r) {
        return new ReservationRequestPBImpl(r);
    }
    
    private YarnProtos.ReservationRequestInterpreterProto convertToProtoFormat(final ReservationRequestInterpreter r) {
        return ProtoUtils.convertToProtoFormat(r);
    }
    
    private ReservationRequestInterpreter convertFromProtoFormat(final YarnProtos.ReservationRequestInterpreterProto r) {
        return ProtoUtils.convertFromProtoFormat(r);
    }
    
    @Override
    public String toString() {
        return "{Reservation Resources: " + this.getReservationResources() + ", Reservation Type: " + this.getInterpreter() + "}";
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationRequestsPBImpl)this.getClass().cast(other)).getProto());
    }
}
