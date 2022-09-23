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
import org.apache.hadoop.yarn.api.protocolrecords.CancelDelegationTokenRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class CancelDelegationTokenRequestPBImpl extends CancelDelegationTokenRequest
{
    SecurityProtos.CancelDelegationTokenRequestProto proto;
    SecurityProtos.CancelDelegationTokenRequestProto.Builder builder;
    boolean viaProto;
    Token token;
    
    public CancelDelegationTokenRequestPBImpl() {
        this.proto = SecurityProtos.CancelDelegationTokenRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = SecurityProtos.CancelDelegationTokenRequestProto.newBuilder();
    }
    
    public CancelDelegationTokenRequestPBImpl(final SecurityProtos.CancelDelegationTokenRequestProto proto) {
        this.proto = SecurityProtos.CancelDelegationTokenRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public Token getDelegationToken() {
        final SecurityProtos.CancelDelegationTokenRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    
    public SecurityProtos.CancelDelegationTokenRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((CancelDelegationTokenRequestPBImpl)this.getClass().cast(other)).getProto());
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
            this.builder = SecurityProtos.CancelDelegationTokenRequestProto.newBuilder(this.proto);
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
