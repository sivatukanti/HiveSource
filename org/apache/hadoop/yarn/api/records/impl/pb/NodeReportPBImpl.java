// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import java.util.Collection;
import java.util.HashSet;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.NodeState;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.NodeReport;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NodeReportPBImpl extends NodeReport
{
    private YarnProtos.NodeReportProto proto;
    private YarnProtos.NodeReportProto.Builder builder;
    private boolean viaProto;
    private NodeId nodeId;
    private Resource used;
    private Resource capability;
    Set<String> labels;
    
    public NodeReportPBImpl() {
        this.proto = YarnProtos.NodeReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.NodeReportProto.newBuilder();
    }
    
    public NodeReportPBImpl(final YarnProtos.NodeReportProto proto) {
        this.proto = YarnProtos.NodeReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public Resource getCapability() {
        if (this.capability != null) {
            return this.capability;
        }
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasCapability()) {
            return null;
        }
        return this.capability = this.convertFromProtoFormat(p.getCapability());
    }
    
    @Override
    public String getHealthReport() {
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getHealthReport();
    }
    
    @Override
    public void setHealthReport(final String healthReport) {
        this.maybeInitBuilder();
        if (healthReport == null) {
            this.builder.clearHealthReport();
            return;
        }
        this.builder.setHealthReport(healthReport);
    }
    
    @Override
    public long getLastHealthReportTime() {
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getLastHealthReportTime();
    }
    
    @Override
    public void setLastHealthReportTime(final long lastHealthReportTime) {
        this.maybeInitBuilder();
        this.builder.setLastHealthReportTime(lastHealthReportTime);
    }
    
    @Override
    public String getHttpAddress() {
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasHttpAddress() ? p.getHttpAddress() : null;
    }
    
    @Override
    public int getNumContainers() {
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasNumContainers() ? p.getNumContainers() : 0;
    }
    
    @Override
    public String getRackName() {
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasRackName() ? p.getRackName() : null;
    }
    
    @Override
    public Resource getUsed() {
        if (this.used != null) {
            return this.used;
        }
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasUsed()) {
            return null;
        }
        return this.used = this.convertFromProtoFormat(p.getUsed());
    }
    
    @Override
    public NodeId getNodeId() {
        if (this.nodeId != null) {
            return this.nodeId;
        }
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeId()) {
            return null;
        }
        return this.nodeId = this.convertFromProtoFormat(p.getNodeId());
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
    public NodeState getNodeState() {
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeState()) {
            return null;
        }
        return ProtoUtils.convertFromProtoFormat(p.getNodeState());
    }
    
    @Override
    public void setNodeState(final NodeState nodeState) {
        this.maybeInitBuilder();
        if (nodeState == null) {
            this.builder.clearNodeState();
            return;
        }
        this.builder.setNodeState(ProtoUtils.convertToProtoFormat(nodeState));
    }
    
    @Override
    public void setCapability(final Resource capability) {
        this.maybeInitBuilder();
        if (capability == null) {
            this.builder.clearCapability();
        }
        this.capability = capability;
    }
    
    @Override
    public void setHttpAddress(final String httpAddress) {
        this.maybeInitBuilder();
        if (httpAddress == null) {
            this.builder.clearHttpAddress();
            return;
        }
        this.builder.setHttpAddress(httpAddress);
    }
    
    @Override
    public void setNumContainers(final int numContainers) {
        this.maybeInitBuilder();
        if (numContainers == 0) {
            this.builder.clearNumContainers();
            return;
        }
        this.builder.setNumContainers(numContainers);
    }
    
    @Override
    public void setRackName(final String rackName) {
        this.maybeInitBuilder();
        if (rackName == null) {
            this.builder.clearRackName();
            return;
        }
        this.builder.setRackName(rackName);
    }
    
    @Override
    public void setUsed(final Resource used) {
        this.maybeInitBuilder();
        if (used == null) {
            this.builder.clearUsed();
        }
        this.used = used;
    }
    
    public YarnProtos.NodeReportProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((NodeReportPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.nodeId != null && !((NodeIdPBImpl)this.nodeId).getProto().equals(this.builder.getNodeId())) {
            this.builder.setNodeId(this.convertToProtoFormat(this.nodeId));
        }
        if (this.used != null && !((ResourcePBImpl)this.used).getProto().equals(this.builder.getUsed())) {
            this.builder.setUsed(this.convertToProtoFormat(this.used));
        }
        if (this.capability != null && !((ResourcePBImpl)this.capability).getProto().equals(this.builder.getCapability())) {
            this.builder.setCapability(this.convertToProtoFormat(this.capability));
        }
        if (this.labels != null) {
            this.builder.clearNodeLabels();
            this.builder.addAllNodeLabels(this.labels);
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
            this.builder = YarnProtos.NodeReportProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private NodeIdPBImpl convertFromProtoFormat(final YarnProtos.NodeIdProto p) {
        return new NodeIdPBImpl(p);
    }
    
    private YarnProtos.NodeIdProto convertToProtoFormat(final NodeId nodeId) {
        return ((NodeIdPBImpl)nodeId).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource r) {
        return ((ResourcePBImpl)r).getProto();
    }
    
    @Override
    public Set<String> getNodeLabels() {
        this.initNodeLabels();
        return this.labels;
    }
    
    @Override
    public void setNodeLabels(final Set<String> nodeLabels) {
        this.maybeInitBuilder();
        this.builder.clearNodeLabels();
        this.labels = nodeLabels;
    }
    
    private void initNodeLabels() {
        if (this.labels != null) {
            return;
        }
        final YarnProtos.NodeReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        (this.labels = new HashSet<String>()).addAll(p.getNodeLabelsList());
    }
}
