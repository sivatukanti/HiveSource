// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationUpdateResponse;

public class ReservationUpdateResponsePBImpl extends ReservationUpdateResponse
{
    YarnServiceProtos.ReservationUpdateResponseProto proto;
    YarnServiceProtos.ReservationUpdateResponseProto.Builder builder;
    boolean viaProto;
    
    public ReservationUpdateResponsePBImpl() {
        this.proto = YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.ReservationUpdateResponseProto.newBuilder();
    }
    
    public ReservationUpdateResponsePBImpl(final YarnServiceProtos.ReservationUpdateResponseProto proto) {
        this.proto = YarnServiceProtos.ReservationUpdateResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.ReservationUpdateResponseProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationUpdateResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
