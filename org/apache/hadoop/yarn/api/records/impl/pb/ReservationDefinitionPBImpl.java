// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.ReservationRequests;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;

public class ReservationDefinitionPBImpl extends ReservationDefinition
{
    YarnProtos.ReservationDefinitionProto proto;
    YarnProtos.ReservationDefinitionProto.Builder builder;
    boolean viaProto;
    private ReservationRequests reservationReqs;
    
    public ReservationDefinitionPBImpl() {
        this.proto = YarnProtos.ReservationDefinitionProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.ReservationDefinitionProto.newBuilder();
    }
    
    public ReservationDefinitionPBImpl(final YarnProtos.ReservationDefinitionProto proto) {
        this.proto = YarnProtos.ReservationDefinitionProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ReservationDefinitionProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.reservationReqs != null) {
            this.builder.setReservationRequests(this.convertToProtoFormat(this.reservationReqs));
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
            this.builder = YarnProtos.ReservationDefinitionProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public long getArrival() {
        final YarnProtos.ReservationDefinitionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasArrival()) {
            return 0L;
        }
        return p.getArrival();
    }
    
    @Override
    public void setArrival(final long earliestStartTime) {
        this.maybeInitBuilder();
        if (earliestStartTime <= 0L) {
            this.builder.clearArrival();
            return;
        }
        this.builder.setArrival(earliestStartTime);
    }
    
    @Override
    public long getDeadline() {
        final YarnProtos.ReservationDefinitionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDeadline()) {
            return 0L;
        }
        return p.getDeadline();
    }
    
    @Override
    public void setDeadline(final long latestEndTime) {
        this.maybeInitBuilder();
        if (latestEndTime <= 0L) {
            this.builder.clearDeadline();
            return;
        }
        this.builder.setDeadline(latestEndTime);
    }
    
    @Override
    public ReservationRequests getReservationRequests() {
        final YarnProtos.ReservationDefinitionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.reservationReqs != null) {
            return this.reservationReqs;
        }
        if (!p.hasReservationRequests()) {
            return null;
        }
        return this.reservationReqs = this.convertFromProtoFormat(p.getReservationRequests());
    }
    
    @Override
    public void setReservationRequests(final ReservationRequests reservationRequests) {
        if (reservationRequests == null) {
            this.builder.clearReservationRequests();
            return;
        }
        this.reservationReqs = reservationRequests;
    }
    
    @Override
    public String getReservationName() {
        final YarnProtos.ReservationDefinitionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasReservationName()) {
            return null;
        }
        return p.getReservationName();
    }
    
    @Override
    public void setReservationName(final String name) {
        this.maybeInitBuilder();
        if (name == null) {
            this.builder.clearReservationName();
            return;
        }
        this.builder.setReservationName(name);
    }
    
    private ReservationRequestsPBImpl convertFromProtoFormat(final YarnProtos.ReservationRequestsProto p) {
        return new ReservationRequestsPBImpl(p);
    }
    
    private YarnProtos.ReservationRequestsProto convertToProtoFormat(final ReservationRequests t) {
        return ((ReservationRequestsPBImpl)t).getProto();
    }
    
    @Override
    public String toString() {
        return "{Arrival: " + this.getArrival() + ", Deadline: " + this.getDeadline() + ", Reservation Name: " + this.getReservationName() + ", Resources: " + this.getReservationRequests() + "}";
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationDefinitionPBImpl)this.getClass().cast(other)).getProto());
    }
}
