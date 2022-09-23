// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ApplicationResourceUsageReportPBImpl extends ApplicationResourceUsageReport
{
    YarnProtos.ApplicationResourceUsageReportProto proto;
    YarnProtos.ApplicationResourceUsageReportProto.Builder builder;
    boolean viaProto;
    Resource usedResources;
    Resource reservedResources;
    Resource neededResources;
    
    public ApplicationResourceUsageReportPBImpl() {
        this.proto = YarnProtos.ApplicationResourceUsageReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.ApplicationResourceUsageReportProto.newBuilder();
    }
    
    public ApplicationResourceUsageReportPBImpl(final YarnProtos.ApplicationResourceUsageReportProto proto) {
        this.proto = YarnProtos.ApplicationResourceUsageReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.ApplicationResourceUsageReportProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationResourceUsageReportPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.usedResources != null && !((ResourcePBImpl)this.usedResources).getProto().equals(this.builder.getUsedResources())) {
            this.builder.setUsedResources(this.convertToProtoFormat(this.usedResources));
        }
        if (this.reservedResources != null && !((ResourcePBImpl)this.reservedResources).getProto().equals(this.builder.getReservedResources())) {
            this.builder.setReservedResources(this.convertToProtoFormat(this.reservedResources));
        }
        if (this.neededResources != null && !((ResourcePBImpl)this.neededResources).getProto().equals(this.builder.getNeededResources())) {
            this.builder.setNeededResources(this.convertToProtoFormat(this.neededResources));
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
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ApplicationResourceUsageReportProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized int getNumUsedContainers() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNumUsedContainers();
    }
    
    @Override
    public synchronized void setNumUsedContainers(final int num_containers) {
        this.maybeInitBuilder();
        this.builder.setNumUsedContainers(num_containers);
    }
    
    @Override
    public synchronized int getNumReservedContainers() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getNumReservedContainers();
    }
    
    @Override
    public synchronized void setNumReservedContainers(final int num_reserved_containers) {
        this.maybeInitBuilder();
        this.builder.setNumReservedContainers(num_reserved_containers);
    }
    
    @Override
    public synchronized Resource getUsedResources() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.usedResources != null) {
            return this.usedResources;
        }
        if (!p.hasUsedResources()) {
            return null;
        }
        return this.usedResources = this.convertFromProtoFormat(p.getUsedResources());
    }
    
    @Override
    public synchronized void setUsedResources(final Resource resources) {
        this.maybeInitBuilder();
        if (resources == null) {
            this.builder.clearUsedResources();
        }
        this.usedResources = resources;
    }
    
    @Override
    public synchronized Resource getReservedResources() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.reservedResources != null) {
            return this.reservedResources;
        }
        if (!p.hasReservedResources()) {
            return null;
        }
        return this.reservedResources = this.convertFromProtoFormat(p.getReservedResources());
    }
    
    @Override
    public synchronized void setReservedResources(final Resource reserved_resources) {
        this.maybeInitBuilder();
        if (reserved_resources == null) {
            this.builder.clearReservedResources();
        }
        this.reservedResources = reserved_resources;
    }
    
    @Override
    public synchronized Resource getNeededResources() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.neededResources != null) {
            return this.neededResources;
        }
        if (!p.hasNeededResources()) {
            return null;
        }
        return this.neededResources = this.convertFromProtoFormat(p.getNeededResources());
    }
    
    @Override
    public synchronized void setNeededResources(final Resource reserved_resources) {
        this.maybeInitBuilder();
        if (reserved_resources == null) {
            this.builder.clearNeededResources();
        }
        this.neededResources = reserved_resources;
    }
    
    @Override
    public synchronized void setMemorySeconds(final long memory_seconds) {
        this.maybeInitBuilder();
        this.builder.setMemorySeconds(memory_seconds);
    }
    
    @Override
    public synchronized long getMemorySeconds() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMemorySeconds();
    }
    
    @Override
    public synchronized void setVcoreSeconds(final long vcore_seconds) {
        this.maybeInitBuilder();
        this.builder.setVcoreSeconds(vcore_seconds);
    }
    
    @Override
    public synchronized long getVcoreSeconds() {
        final YarnProtos.ApplicationResourceUsageReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getVcoreSeconds();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource t) {
        return ((ResourcePBImpl)t).getProto();
    }
}
