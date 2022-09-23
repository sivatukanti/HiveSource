// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class KillApplicationResponsePBImpl extends KillApplicationResponse
{
    YarnServiceProtos.KillApplicationResponseProto proto;
    YarnServiceProtos.KillApplicationResponseProto.Builder builder;
    boolean viaProto;
    
    public KillApplicationResponsePBImpl() {
        this.proto = YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.KillApplicationResponseProto.newBuilder();
    }
    
    public KillApplicationResponsePBImpl(final YarnServiceProtos.KillApplicationResponseProto proto) {
        this.proto = YarnServiceProtos.KillApplicationResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.KillApplicationResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((KillApplicationResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.KillApplicationResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public boolean getIsKillCompleted() {
        final YarnServiceProtos.KillApplicationResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getIsKillCompleted();
    }
    
    @Override
    public void setIsKillCompleted(final boolean isKillCompleted) {
        this.maybeInitBuilder();
        this.builder.setIsKillCompleted(isKillCompleted);
    }
}
