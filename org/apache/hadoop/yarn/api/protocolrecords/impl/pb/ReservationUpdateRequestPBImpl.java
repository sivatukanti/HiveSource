// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.impl.pb.ReservationDefinitionPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ReservationIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.api.records.ReservationDefinition;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateRequest;

public class ReservationUpdateRequestPBImpl extends ReservationUpdateRequest
{
    YarnServiceProtos.ReservationUpdateRequestProto proto;
    YarnServiceProtos.ReservationUpdateRequestProto.Builder builder;
    boolean viaProto;
    private ReservationDefinition reservationDefinition;
    private ReservationId reservationId;
    
    public ReservationUpdateRequestPBImpl() {
        this.proto = YarnServiceProtos.ReservationUpdateRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.ReservationUpdateRequestProto.newBuilder();
    }
    
    public ReservationUpdateRequestPBImpl(final YarnServiceProtos.ReservationUpdateRequestProto proto) {
        this.proto = YarnServiceProtos.ReservationUpdateRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.ReservationUpdateRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.reservationId != null) {
            this.builder.setReservationId(this.convertToProtoFormat(this.reservationId));
        }
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
            this.builder = YarnServiceProtos.ReservationUpdateRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ReservationDefinition getReservationDefinition() {
        final YarnServiceProtos.ReservationUpdateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public ReservationId getReservationId() {
        final YarnServiceProtos.ReservationUpdateRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.reservationId != null) {
            return this.reservationId;
        }
        if (!p.hasReservationId()) {
            return null;
        }
        return this.reservationId = this.convertFromProtoFormat(p.getReservationId());
    }
    
    @Override
    public void setReservationId(final ReservationId reservationId) {
        this.maybeInitBuilder();
        if (reservationId == null) {
            this.builder.clearReservationId();
            return;
        }
        this.reservationId = reservationId;
    }
    
    private ReservationIdPBImpl convertFromProtoFormat(final YarnProtos.ReservationIdProto p) {
        return new ReservationIdPBImpl(p);
    }
    
    private YarnProtos.ReservationIdProto convertToProtoFormat(final ReservationId t) {
        return ((ReservationIdPBImpl)t).getProto();
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationUpdateRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
