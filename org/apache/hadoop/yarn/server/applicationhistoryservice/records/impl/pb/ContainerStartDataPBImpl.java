// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.PriorityPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.NodeIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;

public class ContainerStartDataPBImpl extends ContainerStartData
{
    ApplicationHistoryServerProtos.ContainerStartDataProto proto;
    ApplicationHistoryServerProtos.ContainerStartDataProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    private Resource resource;
    private NodeId nodeId;
    private Priority priority;
    
    public ContainerStartDataPBImpl() {
        this.proto = ApplicationHistoryServerProtos.ContainerStartDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = ApplicationHistoryServerProtos.ContainerStartDataProto.newBuilder();
    }
    
    public ContainerStartDataPBImpl(final ApplicationHistoryServerProtos.ContainerStartDataProto proto) {
        this.proto = ApplicationHistoryServerProtos.ContainerStartDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public ContainerId getContainerId() {
        if (this.containerId != null) {
            return this.containerId;
        }
        final ApplicationHistoryServerProtos.ContainerStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerId()) {
            return null;
        }
        return this.containerId = this.convertFromProtoFormat(p.getContainerId());
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
    public Resource getAllocatedResource() {
        if (this.resource != null) {
            return this.resource;
        }
        final ApplicationHistoryServerProtos.ContainerStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasAllocatedResource()) {
            return null;
        }
        return this.resource = this.convertFromProtoFormat(p.getAllocatedResource());
    }
    
    @Override
    public void setAllocatedResource(final Resource resource) {
        this.maybeInitBuilder();
        if (resource == null) {
            this.builder.clearAllocatedResource();
        }
        this.resource = resource;
    }
    
    @Override
    public NodeId getAssignedNode() {
        if (this.nodeId != null) {
            return this.nodeId;
        }
        final ApplicationHistoryServerProtos.ContainerStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasAssignedNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getAssignedNodeId());
    }
    
    @Override
    public void setAssignedNode(final NodeId nodeId) {
        this.maybeInitBuilder();
        if (nodeId == null) {
            this.builder.clearAssignedNodeId();
        }
        this.nodeId = nodeId;
    }
    
    @Override
    public Priority getPriority() {
        if (this.priority != null) {
            return this.priority;
        }
        final ApplicationHistoryServerProtos.ContainerStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public long getStartTime() {
        final ApplicationHistoryServerProtos.ContainerStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getStartTime();
    }
    
    @Override
    public void setStartTime(final long startTime) {
        this.maybeInitBuilder();
        this.builder.setStartTime(startTime);
    }
    
    public ApplicationHistoryServerProtos.ContainerStartDataProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerStartDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null && !((ContainerIdPBImpl)this.containerId).getProto().equals(this.builder.getContainerId())) {
            this.builder.setContainerId(this.convertToProtoFormat(this.containerId));
        }
        if (this.resource != null && !((ResourcePBImpl)this.resource).getProto().equals(this.builder.getAllocatedResource())) {
            this.builder.setAllocatedResource(this.convertToProtoFormat(this.resource));
        }
        if (this.nodeId != null && !((NodeIdPBImpl)this.nodeId).getProto().equals(this.builder.getAssignedNodeId())) {
            this.builder.setAssignedNodeId(this.convertToProtoFormat(this.nodeId));
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
            this.builder = ApplicationHistoryServerProtos.ContainerStartDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId containerId) {
        return ((ContainerIdPBImpl)containerId).getProto();
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto containerId) {
        return new ContainerIdPBImpl(containerId);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource resource) {
        return ((ResourcePBImpl)resource).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto resource) {
        return new ResourcePBImpl(resource);
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId nodeId) {
        return ((NodeIdPBImpl)nodeId).getProto();
    }
    
    private NodeIdPBImpl convertFromProtoFormat(final YarnProtos.NodeIdProto nodeId) {
        return new NodeIdPBImpl(nodeId);
    }
    
    private YarnProtos.PriorityProto convertToProtoFormat(final Priority priority) {
        return ((PriorityPBImpl)priority).getProto();
    }
    
    private PriorityPBImpl convertFromProtoFormat(final YarnProtos.PriorityProto priority) {
        return new PriorityPBImpl(priority);
    }
}
