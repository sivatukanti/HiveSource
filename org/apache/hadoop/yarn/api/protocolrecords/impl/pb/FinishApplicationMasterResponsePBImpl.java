// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FinishApplicationMasterResponsePBImpl extends FinishApplicationMasterResponse
{
    YarnServiceProtos.FinishApplicationMasterResponseProto proto;
    YarnServiceProtos.FinishApplicationMasterResponseProto.Builder builder;
    boolean viaProto;
    
    public FinishApplicationMasterResponsePBImpl() {
        this.proto = YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.FinishApplicationMasterResponseProto.newBuilder();
    }
    
    public FinishApplicationMasterResponsePBImpl(final YarnServiceProtos.FinishApplicationMasterResponseProto proto) {
        this.proto = YarnServiceProtos.FinishApplicationMasterResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.FinishApplicationMasterResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((FinishApplicationMasterResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.FinishApplicationMasterResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public boolean getIsUnregistered() {
        final YarnServiceProtos.FinishApplicationMasterResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getIsUnregistered();
    }
    
    @Override
    public void setIsUnregistered(final boolean isUnregistered) {
        this.maybeInitBuilder();
        this.builder.setIsUnregistered(isUnregistered);
    }
}
