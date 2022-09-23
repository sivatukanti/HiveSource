// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetDelegationTokenRequestPBImpl extends GetDelegationTokenRequest
{
    String renewer;
    SecurityProtos.GetDelegationTokenRequestProto proto;
    SecurityProtos.GetDelegationTokenRequestProto.Builder builder;
    boolean viaProto;
    
    public GetDelegationTokenRequestPBImpl() {
        this.proto = SecurityProtos.GetDelegationTokenRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = SecurityProtos.GetDelegationTokenRequestProto.newBuilder();
    }
    
    public GetDelegationTokenRequestPBImpl(final SecurityProtos.GetDelegationTokenRequestProto proto) {
        this.proto = SecurityProtos.GetDelegationTokenRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public String getRenewer() {
        final SecurityProtos.GetDelegationTokenRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.renewer != null) {
            return this.renewer;
        }
        return this.renewer = p.getRenewer();
    }
    
    @Override
    public void setRenewer(final String renewer) {
        this.maybeInitBuilder();
        if (renewer == null) {
            this.builder.clearRenewer();
        }
        this.renewer = renewer;
    }
    
    public SecurityProtos.GetDelegationTokenRequestProto getProto() {
        this.mergeLocalToProto();
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetDelegationTokenRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.renewer != null) {
            this.builder.setRenewer(this.renewer);
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
            this.builder = SecurityProtos.GetDelegationTokenRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
}
