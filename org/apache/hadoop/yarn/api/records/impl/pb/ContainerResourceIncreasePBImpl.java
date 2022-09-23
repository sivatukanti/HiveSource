// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ContainerResourceIncrease;

public class ContainerResourceIncreasePBImpl extends ContainerResourceIncrease
{
    YarnProtos.ContainerResourceIncreaseProto proto;
    YarnProtos.ContainerResourceIncreaseProto.Builder builder;
    boolean viaProto;
    private ContainerId existingContainerId;
    private Resource targetCapability;
    private Token token;
    
    public ContainerResourceIncreasePBImpl() {
        this.proto = YarnProtos.ContainerResourceIncreaseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.existingContainerId = null;
        this.targetCapability = null;
        this.token = null;
        this.builder = YarnProtos.ContainerResourceIncreaseProto.newBuilder();
    }
    
    public ContainerResourceIncreasePBImpl(final YarnProtos.ContainerResourceIncreaseProto proto) {
        this.proto = YarnProtos.ContainerResourceIncreaseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.existingContainerId = null;
        this.targetCapability = null;
        this.token = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ContainerResourceIncreaseProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public ContainerId getContainerId() {
        final YarnProtos.ContainerResourceIncreaseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.existingContainerId != null) {
            return this.existingContainerId;
        }
        if (p.hasContainerId()) {
            this.existingContainerId = this.convertFromProtoFormat(p.getContainerId());
        }
        return this.existingContainerId;
    }
    
    @Override
    public void setContainerId(final ContainerId existingContainerId) {
        this.maybeInitBuilder();
        if (existingContainerId == null) {
            this.builder.clearContainerId();
        }
        this.existingContainerId = existingContainerId;
    }
    
    @Override
    public Resource getCapability() {
        final YarnProtos.ContainerResourceIncreaseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.targetCapability != null) {
            return this.targetCapability;
        }
        if (p.hasCapability()) {
            this.targetCapability = this.convertFromProtoFormat(p.getCapability());
        }
        return this.targetCapability;
    }
    
    @Override
    public void setCapability(final Resource targetCapability) {
        this.maybeInitBuilder();
        if (targetCapability == null) {
            this.builder.clearCapability();
        }
        this.targetCapability = targetCapability;
    }
    
    @Override
    public Token getContainerToken() {
        final YarnProtos.ContainerResourceIncreaseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.token != null) {
            return this.token;
        }
        if (p.hasContainerToken()) {
            this.token = this.convertFromProtoFormat(p.getContainerToken());
        }
        return this.token;
    }
    
    @Override
    public void setContainerToken(final Token token) {
        this.maybeInitBuilder();
        if (token == null) {
            this.builder.clearContainerToken();
        }
        this.token = token;
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
    
    private Resource convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    private Token convertFromProtoFormat(final SecurityProtos.TokenProto p) {
        return new TokenPBImpl(p);
    }
    
    private SecurityProtos.TokenProto convertToProtoFormat(final Token t) {
        return ((TokenPBImpl)t).getProto();
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
            this.builder = YarnProtos.ContainerResourceIncreaseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void mergeLocalToBuilder() {
        if (this.existingContainerId != null) {
            this.builder.setContainerId(this.convertToProtoFormat(this.existingContainerId));
        }
        if (this.targetCapability != null) {
            this.builder.setCapability(this.convertToProtoFormat(this.targetCapability));
        }
        if (this.token != null) {
            this.builder.setContainerToken(this.convertToProtoFormat(this.token));
        }
    }
}
