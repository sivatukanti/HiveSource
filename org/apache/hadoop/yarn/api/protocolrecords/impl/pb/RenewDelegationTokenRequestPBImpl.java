// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.TokenPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RenewDelegationTokenRequestPBImpl extends RenewDelegationTokenRequest
{
    SecurityProtos.RenewDelegationTokenRequestProto proto;
    SecurityProtos.RenewDelegationTokenRequestProto.Builder builder;
    boolean viaProto;
    Token token;
    
    public RenewDelegationTokenRequestPBImpl() {
        this.proto = SecurityProtos.RenewDelegationTokenRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = SecurityProtos.RenewDelegationTokenRequestProto.newBuilder();
    }
    
    public RenewDelegationTokenRequestPBImpl(final SecurityProtos.RenewDelegationTokenRequestProto proto) {
        this.proto = SecurityProtos.RenewDelegationTokenRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public Token getDelegationToken() {
        final SecurityProtos.RenewDelegationTokenRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.token != null) {
            return this.token;
        }
        return this.token = this.convertFromProtoFormat(p.getToken());
    }
    
    @Override
    public void setDelegationToken(final Token token) {
        this.maybeInitBuilder();
        if (token == null) {
            this.builder.clearToken();
        }
        this.token = token;
    }
    
    public SecurityProtos.RenewDelegationTokenRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RenewDelegationTokenRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.token != null) {
            this.builder.setToken(this.convertToProtoFormat(this.token));
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
            this.builder = SecurityProtos.RenewDelegationTokenRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private TokenPBImpl convertFromProtoFormat(final SecurityProtos.TokenProto p) {
        return new TokenPBImpl(p);
    }
    
    private SecurityProtos.TokenProto convertToProtoFormat(final Token t) {
        return ((TokenPBImpl)t).getProto();
    }
}
