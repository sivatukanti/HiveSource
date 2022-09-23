// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.TokenPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetDelegationTokenResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetDelegationTokenResponsePBImpl extends GetDelegationTokenResponse
{
    Token appToken;
    SecurityProtos.GetDelegationTokenResponseProto proto;
    SecurityProtos.GetDelegationTokenResponseProto.Builder builder;
    boolean viaProto;
    
    public GetDelegationTokenResponsePBImpl() {
        this.proto = SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = SecurityProtos.GetDelegationTokenResponseProto.newBuilder();
    }
    
    public GetDelegationTokenResponsePBImpl(final SecurityProtos.GetDelegationTokenResponseProto proto) {
        this.proto = SecurityProtos.GetDelegationTokenResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public Token getRMDelegationToken() {
        final SecurityProtos.GetDelegationTokenResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.appToken != null) {
            return this.appToken;
        }
        if (!p.hasToken()) {
            return null;
        }
        return this.appToken = this.convertFromProtoFormat(p.getToken());
    }
    
    @Override
    public void setRMDelegationToken(final Token appToken) {
        this.maybeInitBuilder();
        if (appToken == null) {
            this.builder.clearToken();
        }
        this.appToken = appToken;
    }
    
    public SecurityProtos.GetDelegationTokenResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetDelegationTokenResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.appToken != null) {
            this.builder.setToken(this.convertToProtoFormat(this.appToken));
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
            this.builder = SecurityProtos.GetDelegationTokenResponseProto.newBuilder(this.proto);
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
