// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.ReservationDeleteResponse;

public class ReservationDeleteResponsePBImpl extends ReservationDeleteResponse
{
    YarnServiceProtos.ReservationDeleteResponseProto proto;
    YarnServiceProtos.ReservationDeleteResponseProto.Builder builder;
    boolean viaProto;
    
    public ReservationDeleteResponsePBImpl() {
        this.proto = YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.ReservationDeleteResponseProto.newBuilder();
    }
    
    public ReservationDeleteResponsePBImpl(final YarnServiceProtos.ReservationDeleteResponseProto proto) {
        this.proto = YarnServiceProtos.ReservationDeleteResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.ReservationDeleteResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ReservationDeleteResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
