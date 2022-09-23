// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.impl.pb.ReservationIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationSubmissionResponse;

public class ReservationSubmissionResponsePBImpl extends ReservationSubmissionResponse
{
    YarnServiceProtos.ReservationSubmissionResponseProto proto;
    YarnServiceProtos.ReservationSubmissionResponseProto.Builder builder;
    boolean viaProto;
    private ReservationId reservationId;
    
    public ReservationSubmissionResponsePBImpl() {
        this.proto = YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.ReservationSubmissionResponseProto.newBuilder();
    }
    
    public ReservationSubmissionResponsePBImpl(final YarnServiceProtos.ReservationSubmissionResponseProto proto) {
        this.proto = YarnServiceProtos.ReservationSubmissionResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.ReservationSubmissionResponseProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.reservationId != null) {
            this.builder.setReservationId(this.convertToProtoFormat(this.reservationId));
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
            this.builder = YarnServiceProtos.ReservationSubmissionResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ReservationId getReservationId() {
        final YarnServiceProtos.ReservationSubmissionResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationSubmissionResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
