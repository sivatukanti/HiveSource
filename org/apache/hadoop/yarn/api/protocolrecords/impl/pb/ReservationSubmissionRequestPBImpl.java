// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.impl.pb.ReservationDefinitionPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionRequest;

public class ReservationSubmissionRequestPBImpl extends ReservationSubmissionRequest
{
    YarnServiceProtos.ReservationSubmissionRequestProto proto;
    YarnServiceProtos.ReservationSubmissionRequestProto.Builder builder;
    boolean viaProto;
    private ReservationDefinition reservationDefinition;
    
    public ReservationSubmissionRequestPBImpl() {
        this.proto = YarnServiceProtos.ReservationSubmissionRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.ReservationSubmissionRequestProto.newBuilder();
    }
    
    public ReservationSubmissionRequestPBImpl(final YarnServiceProtos.ReservationSubmissionRequestProto proto) {
        this.proto = YarnServiceProtos.ReservationSubmissionRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.ReservationSubmissionRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.reservationDefinition != null) {
            this.builder.setReservationDefinition(this.convertToProtoFormat(this.reservationDefinition));
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
            this.builder = YarnServiceProtos.ReservationSubmissionRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ReservationDefinition getReservationDefinition() {
        final YarnServiceProtos.ReservationSubmissionRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.reservationDefinition != null) {
            return this.reservationDefinition;
        }
        if (!p.hasReservationDefinition()) {
            return null;
        }
        return this.reservationDefinition = this.convertFromProtoFormat(p.getReservationDefinition());
    }
    
    @Override
    public void setReservationDefinition(final ReservationDefinition reservationDefinition) {
        this.maybeInitBuilder();
        if (reservationDefinition == null) {
            this.builder.clearReservationDefinition();
        }
        this.reservationDefinition = reservationDefinition;
    }
    
    @Override
    public String getQueue() {
        final YarnServiceProtos.ReservationSubmissionRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasQueue()) {
            return null;
        }
        return p.getQueue();
    }
    
    @Override
    public void setQueue(final String planName) {
        this.maybeInitBuilder();
        if (planName == null) {
            this.builder.clearQueue();
            return;
        }
        this.builder.setQueue(planName);
    }
    
    private YarnProtos.ReservationDefinitionProto convertToProtoFormat(final ReservationDefinition r) {
        return ((ReservationDefinitionPBImpl)r).getProto();
    }
    
    private ReservationDefinitionPBImpl convertFromProtoFormat(final YarnProtos.ReservationDefinitionProto r) {
        return new ReservationDefinitionPBImpl(r);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationSubmissionRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
