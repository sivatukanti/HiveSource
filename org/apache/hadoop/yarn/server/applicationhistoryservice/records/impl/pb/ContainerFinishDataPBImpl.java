// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;

public class ContainerFinishDataPBImpl extends ContainerFinishData
{
    ApplicationHistoryServerProtos.ContainerFinishDataProto proto;
    ApplicationHistoryServerProtos.ContainerFinishDataProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    
    public ContainerFinishDataPBImpl() {
        this.proto = ApplicationHistoryServerProtos.ContainerFinishDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = ApplicationHistoryServerProtos.ContainerFinishDataProto.newBuilder();
    }
    
    public ContainerFinishDataPBImpl(final ApplicationHistoryServerProtos.ContainerFinishDataProto proto) {
        this.proto = ApplicationHistoryServerProtos.ContainerFinishDataProto.getDefaultInstance();
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
        final ApplicationHistoryServerProtos.ContainerFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public long getFinishTime() {
        final ApplicationHistoryServerProtos.ContainerFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getFinishTime();
    }
    
    @Override
    public void setFinishTime(final long finishTime) {
        this.maybeInitBuilder();
        this.builder.setFinishTime(finishTime);
    }
    
    @Override
    public String getDiagnosticsInfo() {
        final ApplicationHistoryServerProtos.ContainerFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnosticsInfo()) {
            return null;
        }
        return p.getDiagnosticsInfo();
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
    public int getContainerExitStatus() {
        final ApplicationHistoryServerProtos.ContainerFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getContainerExitStatus();
    }
    
    @Override
    public ContainerState getContainerState() {
        final ApplicationHistoryServerProtos.ContainerFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getContainerState());
    }
    
    @Override
    public void setContainerState(final ContainerState state) {
        this.maybeInitBuilder();
        if (state == null) {
            this.builder.clearContainerState();
            return;
        }
        this.builder.setContainerState(this.convertToProtoFormat(state));
    }
    
    @Override
    public void setContainerExitStatus(final int containerExitStatus) {
        this.maybeInitBuilder();
        this.builder.setContainerExitStatus(containerExitStatus);
    }
    
    public ApplicationHistoryServerProtos.ContainerFinishDataProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ContainerFinishDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null && !((ContainerIdPBImpl)this.containerId).getProto().equals(this.builder.getContainerId())) {
            this.builder.setContainerId(this.convertToProtoFormat(this.containerId));
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
            this.builder = ApplicationHistoryServerProtos.ContainerFinishDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId containerId) {
        return ((ContainerIdPBImpl)containerId).getProto();
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto containerId) {
        return new ContainerIdPBImpl(containerId);
    }
    
    private YarnProtos.ContainerStateProto convertToProtoFormat(final ContainerState state) {
        return ProtoUtils.convertToProtoFormat(state);
    }
    
    private ContainerState convertFromProtoFormat(final YarnProtos.ContainerStateProto containerState) {
        return ProtoUtils.convertFromProtoFormat(containerState);
    }
}
