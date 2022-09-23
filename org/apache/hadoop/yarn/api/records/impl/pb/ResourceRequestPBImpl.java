// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ResourceRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ResourceRequestPBImpl extends ResourceRequest
{
    YarnProtos.ResourceRequestProto proto;
    YarnProtos.ResourceRequestProto.Builder builder;
    boolean viaProto;
    private Priority priority;
    private Resource capability;
    
    public ResourceRequestPBImpl() {
        this.proto = YarnProtos.ResourceRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.priority = null;
        this.capability = null;
        this.builder = YarnProtos.ResourceRequestProto.newBuilder();
    }
    
    public ResourceRequestPBImpl(final YarnProtos.ResourceRequestProto proto) {
        this.proto = YarnProtos.ResourceRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.priority = null;
        this.capability = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ResourceRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.priority != null) {
            this.builder.setPriority(this.convertToProtoFormat(this.priority));
        }
        if (this.capability != null) {
            this.builder.setCapability(this.convertToProtoFormat(this.capability));
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
            this.builder = YarnProtos.ResourceRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public Priority getPriority() {
        final YarnProtos.ResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public String getResourceName() {
        final YarnProtos.ResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasResourceName()) {
            return null;
        }
        return p.getResourceName();
    }
    
    @Override
    public void setResourceName(final String resourceName) {
        this.maybeInitBuilder();
        if (resourceName == null) {
            this.builder.clearResourceName();
            return;
        }
        this.builder.setResourceName(resourceName);
    }
    
    @Override
    public Resource getCapability() {
        final YarnProtos.ResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.capability != null) {
            return this.capability;
        }
        if (!p.hasCapability()) {
            return null;
        }
        return this.capability = this.convertFromProtoFormat(p.getCapability());
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
    public int getNumContainers() {
        final YarnProtos.ResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNumContainers();
    }
    
    @Override
    public void setNumContainers(final int numContainers) {
        this.maybeInitBuilder();
        this.builder.setNumContainers(numContainers);
    }
    
    @Override
    public boolean getRelaxLocality() {
        final YarnProtos.ResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getRelaxLocality();
    }
    
    @Override
    public void setRelaxLocality(final boolean relaxLocality) {
        this.maybeInitBuilder();
        this.builder.setRelaxLocality(relaxLocality);
    }
    
    private PriorityPBImpl convertFromProtoFormat(final YarnProtos.PriorityProto p) {
        return new PriorityPBImpl(p);
    }
    
    private YarnProtos.PriorityProto convertToProtoFormat(final Priority t) {
        return ((PriorityPBImpl)t).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    @Override
    public String toString() {
        return "{Priority: " + this.getPriority() + ", Capability: " + this.getCapability() + ", # Containers: " + this.getNumContainers() + ", Location: " + this.getResourceName() + ", Relax Locality: " + this.getRelaxLocality() + "}";
    }
    
    @Override
    public String getNodeLabelExpression() {
        final YarnProtos.ResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasNodeLabelExpression()) {
            return null;
        }
        return p.getNodeLabelExpression();
    }
    
    @Override
    public void setNodeLabelExpression(final String nodeLabelExpression) {
        this.maybeInitBuilder();
        if (nodeLabelExpression == null) {
            this.builder.clearNodeLabelExpression();
            return;
        }
        this.builder.setNodeLabelExpression(nodeLabelExpression);
    }
}
