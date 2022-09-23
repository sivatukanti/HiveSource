// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ReservationRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ReservationRequestPBImpl extends ReservationRequest
{
    YarnProtos.ReservationRequestProto proto;
    YarnProtos.ReservationRequestProto.Builder builder;
    boolean viaProto;
    private Resource capability;
    
    public ReservationRequestPBImpl() {
        this.proto = YarnProtos.ReservationRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.capability = null;
        this.builder = YarnProtos.ReservationRequestProto.newBuilder();
    }
    
    public ReservationRequestPBImpl(final YarnProtos.ReservationRequestProto proto) {
        this.proto = YarnProtos.ReservationRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.capability = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ReservationRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
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
            this.builder = YarnProtos.ReservationRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public Resource getCapability() {
        final YarnProtos.ReservationRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
        final YarnProtos.ReservationRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNumContainers();
    }
    
    @Override
    public void setNumContainers(final int numContainers) {
        this.maybeInitBuilder();
        this.builder.setNumContainers(numContainers);
    }
    
    @Override
    public int getConcurrency() {
        final YarnProtos.ReservationRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasConcurrency()) {
            return 1;
        }
        return p.getConcurrency();
    }
    
    @Override
    public void setConcurrency(final int numContainers) {
        this.maybeInitBuilder();
        this.builder.setConcurrency(numContainers);
    }
    
    @Override
    public long getDuration() {
        final YarnProtos.ReservationRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDuration()) {
            return 0L;
        }
        return p.getDuration();
    }
    
    @Override
    public void setDuration(final long duration) {
        this.maybeInitBuilder();
        this.builder.setDuration(duration);
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
    
    @Override
    public String toString() {
        return "{Capability: " + this.getCapability() + ", # Containers: " + this.getNumContainers() + ", Concurrency: " + this.getConcurrency() + ", Lease Duration: " + this.getDuration() + "}";
    }
}
