// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class RegisterApplicationMasterRequestPBImpl extends RegisterApplicationMasterRequest
{
    YarnServiceProtos.RegisterApplicationMasterRequestProto proto;
    YarnServiceProtos.RegisterApplicationMasterRequestProto.Builder builder;
    boolean viaProto;
    
    public RegisterApplicationMasterRequestPBImpl() {
        this.proto = YarnServiceProtos.RegisterApplicationMasterRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.RegisterApplicationMasterRequestProto.newBuilder();
    }
    
    public RegisterApplicationMasterRequestPBImpl(final YarnServiceProtos.RegisterApplicationMasterRequestProto proto) {
        this.proto = YarnServiceProtos.RegisterApplicationMasterRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.RegisterApplicationMasterRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((RegisterApplicationMasterRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
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
            this.builder = YarnServiceProtos.RegisterApplicationMasterRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public String getHost() {
        final YarnServiceProtos.RegisterApplicationMasterRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getHost();
    }
    
    @Override
    public void setHost(final String host) {
        this.maybeInitBuilder();
        if (host == null) {
            this.builder.clearHost();
            return;
        }
        this.builder.setHost(host);
    }
    
    @Override
    public int getRpcPort() {
        final YarnServiceProtos.RegisterApplicationMasterRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getRpcPort();
    }
    
    @Override
    public void setRpcPort(final int port) {
        this.maybeInitBuilder();
        this.builder.setRpcPort(port);
    }
    
    @Override
    public String getTrackingUrl() {
        final YarnServiceProtos.RegisterApplicationMasterRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getTrackingUrl();
    }
    
    @Override
    public void setTrackingUrl(final String url) {
        this.maybeInitBuilder();
        if (url == null) {
            this.builder.clearTrackingUrl();
            return;
        }
        this.builder.setTrackingUrl(url);
    }
}
