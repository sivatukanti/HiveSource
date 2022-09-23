// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.ContainerState;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ContainerReport;

public class ContainerReportPBImpl extends ContainerReport
{
    YarnProtos.ContainerReportProto proto;
    YarnProtos.ContainerReportProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    private Resource resource;
    private NodeId nodeId;
    private Priority priority;
    
    public ContainerReportPBImpl() {
        this.proto = YarnProtos.ContainerReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.resource = null;
        this.nodeId = null;
        this.priority = null;
        this.builder = YarnProtos.ContainerReportProto.newBuilder();
    }
    
    public ContainerReportPBImpl(final YarnProtos.ContainerReportProto proto) {
        this.proto = YarnProtos.ContainerReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.resource = null;
        this.nodeId = null;
        this.priority = null;
        this.proto = proto;
        this.viaProto = true;
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
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasResource()) {
            return null;
        }
        return this.resource = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public NodeId getAssignedNode() {
        if (this.nodeId != null) {
            return this.nodeId;
        }
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getNodeId());
    }
    
    @Override
    public ContainerId getContainerId() {
        if (this.containerId != null) {
            return this.containerId;
        }
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerId()) {
            return null;
        }
        return this.containerId = this.convertFromProtoFormat(p.getContainerId());
    }
    
    @Override
    public String getDiagnosticsInfo() {
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnosticsInfo()) {
            return null;
        }
        return p.getDiagnosticsInfo();
    }
    
    @Override
    public ContainerState getContainerState() {
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getContainerState());
    }
    
    @Override
    public long getFinishTime() {
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getFinishTime();
    }
    
    @Override
    public String getLogUrl() {
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasLogUrl()) {
            return null;
        }
        return p.getLogUrl();
    }
    
    @Override
    public Priority getPriority() {
        if (this.priority != null) {
            return this.priority;
        }
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasPriority()) {
            return null;
        }
        return this.priority = this.convertFromProtoFormat(p.getPriority());
    }
    
    @Override
    public long getCreationTime() {
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getCreationTime();
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
    public void setAssignedNode(final NodeId nodeId) {
        this.maybeInitBuilder();
        if (nodeId == null) {
            this.builder.clearNodeId();
        }
        this.nodeId = nodeId;
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
    public void setDiagnosticsInfo(final String diagnosticsInfo) {
        this.maybeInitBuilder();
        if (diagnosticsInfo == null) {
            this.builder.clearDiagnosticsInfo();
            return;
        }
        this.builder.setDiagnosticsInfo(diagnosticsInfo);
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
        final YarnProtos.ContainerReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getContainerExitStatus();
    }
    
    @Override
    public void setContainerExitStatus(final int containerExitStatus) {
        this.maybeInitBuilder();
        this.builder.setContainerExitStatus(containerExitStatus);
    }
    
    @Override
    public void setFinishTime(final long finishTime) {
        this.maybeInitBuilder();
        this.builder.setFinishTime(finishTime);
    }
    
    @Override
    public void setLogUrl(final String logUrl) {
        this.maybeInitBuilder();
        if (logUrl == null) {
            this.builder.clearLogUrl();
            return;
        }
        this.builder.setLogUrl(logUrl);
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
    public void setCreationTime(final long creationTime) {
        this.maybeInitBuilder();
        this.builder.setCreationTime(creationTime);
    }
    
    public YarnProtos.ContainerReportProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerReportPBImpl)this.getClass().cast(other)).getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null && !((ContainerIdPBImpl)this.containerId).getProto().equals(this.builder.getContainerId())) {
            this.builder.setContainerId(this.convertToProtoFormat(this.containerId));
        }
        if (this.nodeId != null && !((NodeIdPBImpl)this.nodeId).getProto().equals(this.builder.getNodeId())) {
            this.builder.setNodeId(this.convertToProtoFormat(this.nodeId));
        }
        if (this.resource != null && !((ResourcePBImpl)this.resource).getProto().equals(this.builder.getResource())) {
            this.builder.setResource(this.convertToProtoFormat(this.resource));
        }
        if (this.priority != null && !((PriorityPBImpl)this.priority).getProto().equals(this.builder.getPriority())) {
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
            this.builder = YarnProtos.ContainerReportProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private NodeIdPBImpl convertFromProtoFormat(final YarnProtos.NodeIdProto p) {
        return new NodeIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId t) {
        return ((NodeIdPBImpl)t).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    private PriorityPBImpl convertFromProtoFormat(final YarnProtos.PriorityProto p) {
        return new PriorityPBImpl(p);
    }
    
    private YarnProtos.PriorityProto convertToProtoFormat(final Priority p) {
        return ((PriorityPBImpl)p).getProto();
    }
    
    private YarnProtos.ContainerStateProto convertToProtoFormat(final ContainerState containerState) {
        return ProtoUtils.convertToProtoFormat(containerState);
    }
    
    private ContainerState convertFromProtoFormat(final YarnProtos.ContainerStateProto containerState) {
        return ProtoUtils.convertFromProtoFormat(containerState);
    }
}
