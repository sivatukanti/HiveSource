// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.PriorityPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.api.records.ContainerState;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnServerCommonServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;

public class NMContainerStatusPBImpl extends NMContainerStatus
{
    YarnServerCommonServiceProtos.NMContainerStatusProto proto;
    YarnServerCommonServiceProtos.NMContainerStatusProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    private Resource resource;
    private Priority priority;
    
    public NMContainerStatusPBImpl() {
        this.proto = YarnServerCommonServiceProtos.NMContainerStatusProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.resource = null;
        this.priority = null;
        this.builder = YarnServerCommonServiceProtos.NMContainerStatusProto.newBuilder();
    }
    
    public NMContainerStatusPBImpl(final YarnServerCommonServiceProtos.NMContainerStatusProto proto) {
        this.proto = YarnServerCommonServiceProtos.NMContainerStatusProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.resource = null;
        this.priority = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerCommonServiceProtos.NMContainerStatusProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((NMContainerStatusPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    @Override
    public Resource getAllocatedResource() {
        if (this.resource != null) {
            return this.resource;
        }
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasResource()) {
            return null;
        }
        return this.resource = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public ContainerId getContainerId() {
        if (this.containerId != null) {
            return this.containerId;
        }
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerId()) {
            return null;
        }
        return this.containerId = this.convertFromProtoFormat(p.getContainerId());
    }
    
    @Override
    public String getDiagnostics() {
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnostics()) {
            return null;
        }
        return p.getDiagnostics();
    }
    
    @Override
    public ContainerState getContainerState() {
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getContainerState());
    }
    
    @Override
    public void setAllocatedResource(final Resource resource) {
        this.maybeInitBuilder();
        if (resource == null) {
            this.builder.clearResource();
        }
        this.resource = resource;
    }
    
    @Override
    public void setContainerId(final ContainerId containerId) {
        this.maybeInitBuilder();
        if (containerId == null) {
            this.builder.clearContainerId();
        }
        this.containerId = containerId;
    }
    
    @Override
    public void setDiagnostics(final String diagnosticsInfo) {
        this.maybeInitBuilder();
        if (diagnosticsInfo == null) {
            this.builder.clearDiagnostics();
            return;
        }
        this.builder.setDiagnostics(diagnosticsInfo);
    }
    
    @Override
    public void setContainerState(final ContainerState containerState) {
        this.maybeInitBuilder();
        if (containerState == null) {
            this.builder.clearContainerState();
            return;
        }
        this.builder.setContainerState(this.convertToProtoFormat(containerState));
    }
    
    @Override
    public int getContainerExitStatus() {
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getContainerExitStatus();
    }
    
    @Override
    public void setContainerExitStatus(final int containerExitStatus) {
        this.maybeInitBuilder();
        this.builder.setContainerExitStatus(containerExitStatus);
    }
    
    @Override
    public Priority getPriority() {
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.priority != null) {
            return this.priority;
        }
        if (!p.hasPriority()) {
            return null;
        }
        return this.priority = this.convertFromProtoFormat(p.getPriority());
    }
    
    @Override
    public void setPriority(final Priority priority) {
        this.maybeInitBuilder();
        if (priority == null) {
            this.builder.clearPriority();
        }
        this.priority = priority;
    }
    
    @Override
    public long getCreationTime() {
        final YarnServerCommonServiceProtos.NMContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getCreationTime();
    }
    
    @Override
    public void setCreationTime(final long creationTime) {
        this.maybeInitBuilder();
        this.builder.setCreationTime(creationTime);
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null && !((ContainerIdPBImpl)this.containerId).getProto().equals(this.builder.getContainerId())) {
            this.builder.setContainerId(this.convertToProtoFormat(this.containerId));
        }
        if (this.resource != null && !((ResourcePBImpl)this.resource).getProto().equals(this.builder.getResource())) {
            this.builder.setResource(this.convertToProtoFormat(this.resource));
        }
        if (this.priority != null) {
            this.builder.setPriority(this.convertToProtoFormat(this.priority));
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
            this.builder = YarnServerCommonServiceProtos.NMContainerStatusProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    private YarnProtos.ContainerStateProto convertToProtoFormat(final ContainerState containerState) {
        return ProtoUtils.convertToProtoFormat(containerState);
    }
    
    private ContainerState convertFromProtoFormat(final YarnProtos.ContainerStateProto containerState) {
        return ProtoUtils.convertFromProtoFormat(containerState);
    }
    
    private PriorityPBImpl convertFromProtoFormat(final YarnProtos.PriorityProto p) {
        return new PriorityPBImpl(p);
    }
    
    private YarnProtos.PriorityProto convertToProtoFormat(final Priority t) {
        return ((PriorityPBImpl)t).getProto();
    }
}
