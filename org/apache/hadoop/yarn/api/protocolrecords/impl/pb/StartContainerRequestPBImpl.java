// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.TokenPBImpl;
import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerLaunchContextPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class StartContainerRequestPBImpl extends StartContainerRequest
{
    YarnServiceProtos.StartContainerRequestProto proto;
    YarnServiceProtos.StartContainerRequestProto.Builder builder;
    boolean viaProto;
    private ContainerLaunchContext containerLaunchContext;
    private Token containerToken;
    
    public StartContainerRequestPBImpl() {
        this.proto = YarnServiceProtos.StartContainerRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerLaunchContext = null;
        this.containerToken = null;
        this.builder = YarnServiceProtos.StartContainerRequestProto.newBuilder();
    }
    
    public StartContainerRequestPBImpl(final YarnServiceProtos.StartContainerRequestProto proto) {
        this.proto = YarnServiceProtos.StartContainerRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerLaunchContext = null;
        this.containerToken = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.StartContainerRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((StartContainerRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerLaunchContext != null) {
            this.builder.setContainerLaunchContext(this.convertToProtoFormat(this.containerLaunchContext));
        }
        if (this.containerToken != null) {
            this.builder.setContainerToken(this.convertToProtoFormat(this.containerToken));
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
            this.builder = YarnServiceProtos.StartContainerRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ContainerLaunchContext getContainerLaunchContext() {
        final YarnServiceProtos.StartContainerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.containerLaunchContext != null) {
            return this.containerLaunchContext;
        }
        if (!p.hasContainerLaunchContext()) {
            return null;
        }
        return this.containerLaunchContext = this.convertFromProtoFormat(p.getContainerLaunchContext());
    }
    
    @Override
    public void setContainerLaunchContext(final ContainerLaunchContext containerLaunchContext) {
        this.maybeInitBuilder();
        if (containerLaunchContext == null) {
            this.builder.clearContainerLaunchContext();
        }
        this.containerLaunchContext = containerLaunchContext;
    }
    
    @Override
    public Token getContainerToken() {
        final YarnServiceProtos.StartContainerRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.containerToken != null) {
            return this.containerToken;
        }
        if (!p.hasContainerToken()) {
            return null;
        }
        return this.containerToken = this.convertFromProtoFormat(p.getContainerToken());
    }
    
    @Override
    public void setContainerToken(final Token containerToken) {
        this.maybeInitBuilder();
        if (containerToken == null) {
            this.builder.clearContainerToken();
        }
        this.containerToken = containerToken;
    }
    
    private ContainerLaunchContextPBImpl convertFromProtoFormat(final YarnProtos.ContainerLaunchContextProto p) {
        return new ContainerLaunchContextPBImpl(p);
    }
    
    private YarnProtos.ContainerLaunchContextProto convertToProtoFormat(final ContainerLaunchContext t) {
        return ((ContainerLaunchContextPBImpl)t).getProto();
    }
    
    private TokenPBImpl convertFromProtoFormat(final SecurityProtos.TokenProto containerProto) {
        return new TokenPBImpl(containerProto);
    }
    
    private SecurityProtos.TokenProto convertToProtoFormat(final Token container) {
        return ((TokenPBImpl)container).getProto();
    }
}
