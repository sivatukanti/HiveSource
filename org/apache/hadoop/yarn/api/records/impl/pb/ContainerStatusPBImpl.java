// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ContainerStatus;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ContainerStatusPBImpl extends ContainerStatus
{
    YarnProtos.ContainerStatusProto proto;
    YarnProtos.ContainerStatusProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    
    public ContainerStatusPBImpl() {
        this.proto = YarnProtos.ContainerStatusProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.builder = YarnProtos.ContainerStatusProto.newBuilder();
    }
    
    public ContainerStatusPBImpl(final YarnProtos.ContainerStatusProto proto) {
        this.proto = YarnProtos.ContainerStatusProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.ContainerStatusProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerStatusPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContainerStatus: [");
        sb.append("ContainerId: ").append(this.getContainerId()).append(", ");
        sb.append("State: ").append(this.getState()).append(", ");
        sb.append("Diagnostics: ").append(this.getDiagnostics()).append(", ");
        sb.append("ExitStatus: ").append(this.getExitStatus()).append(", ");
        sb.append("]");
        return sb.toString();
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null) {
            this.builder.setContainerId(this.convertToProtoFormat(this.containerId));
        }
    }
    
    private synchronized void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private synchronized void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ContainerStatusProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized ContainerState getState() {
        final YarnProtos.ContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getState());
    }
    
    @Override
    public synchronized void setState(final ContainerState state) {
        this.maybeInitBuilder();
        if (state == null) {
            this.builder.clearState();
            return;
        }
        this.builder.setState(this.convertToProtoFormat(state));
    }
    
    @Override
    public synchronized ContainerId getContainerId() {
        final YarnProtos.ContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.containerId != null) {
            return this.containerId;
        }
        if (!p.hasContainerId()) {
            return null;
        }
        return this.containerId = this.convertFromProtoFormat(p.getContainerId());
    }
    
    @Override
    public synchronized void setContainerId(final ContainerId containerId) {
        this.maybeInitBuilder();
        if (containerId == null) {
            this.builder.clearContainerId();
        }
        this.containerId = containerId;
    }
    
    @Override
    public synchronized int getExitStatus() {
        final YarnProtos.ContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getExitStatus();
    }
    
    @Override
    public synchronized void setExitStatus(final int exitStatus) {
        this.maybeInitBuilder();
        this.builder.setExitStatus(exitStatus);
    }
    
    @Override
    public synchronized String getDiagnostics() {
        final YarnProtos.ContainerStatusProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getDiagnostics();
    }
    
    @Override
    public synchronized void setDiagnostics(final String diagnostics) {
        this.maybeInitBuilder();
        this.builder.setDiagnostics(diagnostics);
    }
    
    private YarnProtos.ContainerStateProto convertToProtoFormat(final ContainerState e) {
        return ProtoUtils.convertToProtoFormat(e);
    }
    
    private ContainerState convertFromProtoFormat(final YarnProtos.ContainerStateProto e) {
        return ProtoUtils.convertFromProtoFormat(e);
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
}
