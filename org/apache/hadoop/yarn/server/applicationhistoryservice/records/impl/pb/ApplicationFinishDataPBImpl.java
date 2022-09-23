// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;

public class ApplicationFinishDataPBImpl extends ApplicationFinishData
{
    ApplicationHistoryServerProtos.ApplicationFinishDataProto proto;
    ApplicationHistoryServerProtos.ApplicationFinishDataProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    
    public ApplicationFinishDataPBImpl() {
        this.proto = ApplicationHistoryServerProtos.ApplicationFinishDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = ApplicationHistoryServerProtos.ApplicationFinishDataProto.newBuilder();
    }
    
    public ApplicationFinishDataPBImpl(final ApplicationHistoryServerProtos.ApplicationFinishDataProto proto) {
        this.proto = ApplicationHistoryServerProtos.ApplicationFinishDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        if (this.applicationId != null) {
            return this.applicationId;
        }
        final ApplicationHistoryServerProtos.ApplicationFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationId()) {
            return null;
        }
        return this.applicationId = this.convertFromProtoFormat(p.getApplicationId());
    }
    
    @Override
    public void setApplicationId(final ApplicationId applicationId) {
        this.maybeInitBuilder();
        if (applicationId == null) {
            this.builder.clearApplicationId();
        }
        this.applicationId = applicationId;
    }
    
    @Override
    public long getFinishTime() {
        final ApplicationHistoryServerProtos.ApplicationFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getFinishTime();
    }
    
    @Override
    public void setFinishTime(final long finishTime) {
        this.maybeInitBuilder();
        this.builder.setFinishTime(finishTime);
    }
    
    @Override
    public String getDiagnosticsInfo() {
        final ApplicationHistoryServerProtos.ApplicationFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public FinalApplicationStatus getFinalApplicationStatus() {
        final ApplicationHistoryServerProtos.ApplicationFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasFinalApplicationStatus()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getFinalApplicationStatus());
    }
    
    @Override
    public void setFinalApplicationStatus(final FinalApplicationStatus finalApplicationStatus) {
        this.maybeInitBuilder();
        if (finalApplicationStatus == null) {
            this.builder.clearFinalApplicationStatus();
            return;
        }
        this.builder.setFinalApplicationStatus(this.convertToProtoFormat(finalApplicationStatus));
    }
    
    @Override
    public YarnApplicationState getYarnApplicationState() {
        final ApplicationHistoryServerProtos.ApplicationFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasYarnApplicationState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getYarnApplicationState());
    }
    
    @Override
    public void setYarnApplicationState(final YarnApplicationState state) {
        this.maybeInitBuilder();
        if (state == null) {
            this.builder.clearYarnApplicationState();
            return;
        }
        this.builder.setYarnApplicationState(this.convertToProtoFormat(state));
    }
    
    public ApplicationHistoryServerProtos.ApplicationFinishDataProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationFinishDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationId != null && !((ApplicationIdPBImpl)this.applicationId).getProto().equals(this.builder.getApplicationId())) {
            this.builder.setApplicationId(this.convertToProtoFormat(this.applicationId));
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
            this.builder = ApplicationHistoryServerProtos.ApplicationFinishDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId applicationId) {
        return ((ApplicationIdPBImpl)applicationId).getProto();
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto applicationId) {
        return new ApplicationIdPBImpl(applicationId);
    }
    
    private FinalApplicationStatus convertFromProtoFormat(final YarnProtos.FinalApplicationStatusProto finalApplicationStatus) {
        return ProtoUtils.convertFromProtoFormat(finalApplicationStatus);
    }
    
    private YarnProtos.FinalApplicationStatusProto convertToProtoFormat(final FinalApplicationStatus finalApplicationStatus) {
        return ProtoUtils.convertToProtoFormat(finalApplicationStatus);
    }
    
    private YarnProtos.YarnApplicationStateProto convertToProtoFormat(final YarnApplicationState state) {
        return ProtoUtils.convertToProtoFormat(state);
    }
    
    private YarnApplicationState convertFromProtoFormat(final YarnProtos.YarnApplicationStateProto yarnApplicationState) {
        return ProtoUtils.convertFromProtoFormat(yarnApplicationState);
    }
}
