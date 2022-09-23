// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.security.proto.SecurityProtos;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.Container;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ContainerPBImpl extends Container
{
    YarnProtos.ContainerProto proto;
    YarnProtos.ContainerProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    private NodeId nodeId;
    private Resource resource;
    private Priority priority;
    private Token containerToken;
    
    public ContainerPBImpl() {
        this.proto = YarnProtos.ContainerProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.nodeId = null;
        this.resource = null;
        this.priority = null;
        this.containerToken = null;
        this.builder = YarnProtos.ContainerProto.newBuilder();
    }
    
    public ContainerPBImpl(final YarnProtos.ContainerProto proto) {
        this.proto = YarnProtos.ContainerProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.nodeId = null;
        this.resource = null;
        this.priority = null;
        this.containerToken = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ContainerProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerPBImpl)this.getClass().cast(other)).getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null && !((ContainerIdPBImpl)this.containerId).getProto().equals(this.builder.getId())) {
            this.builder.setId(this.convertToProtoFormat(this.containerId));
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
        if (this.containerToken != null && !((TokenPBImpl)this.containerToken).getProto().equals(this.builder.getContainerToken())) {
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
            this.builder = YarnProtos.ContainerProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ContainerId getId() {
        final YarnProtos.ContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.containerId != null) {
            return this.containerId;
        }
        if (!p.hasId()) {
            return null;
        }
        return this.containerId = this.convertFromProtoFormat(p.getId());
    }
    
    @Override
    public void setNodeId(final NodeId nodeId) {
        this.maybeInitBuilder();
        if (nodeId == null) {
            this.builder.clearNodeId();
        }
        this.nodeId = nodeId;
    }
    
    @Override
    public NodeId getNodeId() {
        final YarnProtos.ContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.nodeId != null) {
            return this.nodeId;
        }
        if (!p.hasNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getNodeId());
    }
    
    @Override
    public void setId(final ContainerId id) {
        this.maybeInitBuilder();
        if (id == null) {
            this.builder.clearId();
        }
        this.containerId = id;
    }
    
    @Override
    public String getNodeHttpAddress() {
        final YarnProtos.ContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeHttpAddress()) {
            return null;
        }
        return p.getNodeHttpAddress();
    }
    
    @Override
    public void setNodeHttpAddress(final String nodeHttpAddress) {
        this.maybeInitBuilder();
        if (nodeHttpAddress == null) {
            this.builder.clearNodeHttpAddress();
            return;
        }
        this.builder.setNodeHttpAddress(nodeHttpAddress);
    }
    
    @Override
    public Resource getResource() {
        final YarnProtos.ContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.resource != null) {
            return this.resource;
        }
        if (!p.hasResource()) {
            return null;
        }
        return this.resource = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public void setResource(final Resource resource) {
        this.maybeInitBuilder();
        if (resource == null) {
            this.builder.clearResource();
        }
        this.resource = resource;
    }
    
    @Override
    public Priority getPriority() {
        final YarnProtos.ContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public Token getContainerToken() {
        final YarnProtos.ContainerProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    
    private TokenPBImpl convertFromProtoFormat(final SecurityProtos.TokenProto p) {
        return new TokenPBImpl(p);
    }
    
    private SecurityProtos.TokenProto convertToProtoFormat(final Token t) {
        return ((TokenPBImpl)t).getProto();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Container: [");
        sb.append("ContainerId: ").append(this.getId()).append(", ");
        sb.append("NodeId: ").append(this.getNodeId()).append(", ");
        sb.append("NodeHttpAddress: ").append(this.getNodeHttpAddress()).append(", ");
        sb.append("Resource: ").append(this.getResource()).append(", ");
        sb.append("Priority: ").append(this.getPriority()).append(", ");
        sb.append("Token: ").append(this.getContainerToken()).append(", ");
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int compareTo(final Container other) {
        if (this.getId().compareTo(other.getId()) != 0) {
            return this.getId().compareTo(other.getId());
        }
        if (this.getNodeId().compareTo(other.getNodeId()) == 0) {
            return this.getResource().compareTo(other.getResource());
        }
        return this.getNodeId().compareTo(other.getNodeId());
    }
}
