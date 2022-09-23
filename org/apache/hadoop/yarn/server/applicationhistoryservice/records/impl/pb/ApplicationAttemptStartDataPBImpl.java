// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;

public class ApplicationAttemptStartDataPBImpl extends ApplicationAttemptStartData
{
    ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto proto;
    ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptId applicationAttemptId;
    private ContainerId masterContainerId;
    
    public ApplicationAttemptStartDataPBImpl() {
        this.proto = ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto.newBuilder();
    }
    
    public ApplicationAttemptStartDataPBImpl(final ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto proto) {
        this.proto = ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public ApplicationAttemptId getApplicationAttemptId() {
        if (this.applicationAttemptId != null) {
            return this.applicationAttemptId;
        }
        final ApplicationHistoryServerProtos.ApplicationAttemptStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationAttemptId()) {
            return null;
        }
        return this.applicationAttemptId = this.convertFromProtoFormat(p.getApplicationAttemptId());
    }
    
    @Override
    public void setApplicationAttemptId(final ApplicationAttemptId applicationAttemptId) {
        this.maybeInitBuilder();
        if (applicationAttemptId == null) {
            this.builder.clearApplicationAttemptId();
        }
        this.applicationAttemptId = applicationAttemptId;
    }
    
    @Override
    public String getHost() {
        final ApplicationHistoryServerProtos.ApplicationAttemptStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasHost()) {
            return null;
        }
        return p.getHost();
    }
    
    @Override
    public void setHost(final String host) {
        this.maybeInitBuilder();
        if (host == null) {
            this.builder.clearHost();
            return;
        }
        this.builder.setHost(host);
    }
    
    @Override
    public int getRPCPort() {
        final ApplicationHistoryServerProtos.ApplicationAttemptStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getRpcPort();
    }
    
    @Override
    public void setRPCPort(final int rpcPort) {
        this.maybeInitBuilder();
        this.builder.setRpcPort(rpcPort);
    }
    
    @Override
    public ContainerId getMasterContainerId() {
        if (this.masterContainerId != null) {
            return this.masterContainerId;
        }
        final ApplicationHistoryServerProtos.ApplicationAttemptStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationAttemptId()) {
            return null;
        }
        return this.masterContainerId = this.convertFromProtoFormat(p.getMasterContainerId());
    }
    
    @Override
    public void setMasterContainerId(final ContainerId masterContainerId) {
        this.maybeInitBuilder();
        if (masterContainerId == null) {
            this.builder.clearMasterContainerId();
        }
        this.masterContainerId = masterContainerId;
    }
    
    public ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationAttemptStartDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationAttemptId != null && !((ApplicationAttemptIdPBImpl)this.applicationAttemptId).getProto().equals(this.builder.getApplicationAttemptId())) {
            this.builder.setApplicationAttemptId(this.convertToProtoFormat(this.applicationAttemptId));
        }
        if (this.masterContainerId != null && !((ContainerIdPBImpl)this.masterContainerId).getProto().equals(this.builder.getMasterContainerId())) {
            this.builder.setMasterContainerId(this.convertToProtoFormat(this.masterContainerId));
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
            this.builder = ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private ApplicationAttemptIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptIdProto applicationAttemptId) {
        return new ApplicationAttemptIdPBImpl(applicationAttemptId);
    }
    
    private YarnProtos.ApplicationAttemptIdProto convertToProtoFormat(final ApplicationAttemptId applicationAttemptId) {
        return ((ApplicationAttemptIdPBImpl)applicationAttemptId).getProto();
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto containerId) {
        return new ContainerIdPBImpl(containerId);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId masterContainerId) {
        return ((ContainerIdPBImpl)masterContainerId).getProto();
    }
}
