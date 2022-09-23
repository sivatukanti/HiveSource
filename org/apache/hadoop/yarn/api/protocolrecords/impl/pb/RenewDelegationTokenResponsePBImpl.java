// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.RenewDelegationTokenResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RenewDelegationTokenResponsePBImpl extends RenewDelegationTokenResponse
{
    SecurityProtos.RenewDelegationTokenResponseProto proto;
    SecurityProtos.RenewDelegationTokenResponseProto.Builder builder;
    boolean viaProto;
    
    public RenewDelegationTokenResponsePBImpl() {
        this.proto = SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = SecurityProtos.RenewDelegationTokenResponseProto.newBuilder();
    }
    
    public RenewDelegationTokenResponsePBImpl(final SecurityProtos.RenewDelegationTokenResponseProto proto) {
        this.proto = SecurityProtos.RenewDelegationTokenResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public SecurityProtos.RenewDelegationTokenResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RenewDelegationTokenResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = SecurityProtos.RenewDelegationTokenResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public long getNextExpirationTime() {
        final SecurityProtos.RenewDelegationTokenResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNewExpiryTime();
    }
    
    @Override
    public void setNextExpirationTime(final long expTime) {
        this.maybeInitBuilder();
        this.builder.setNewExpiryTime(expTime);
    }
}
