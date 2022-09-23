// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;

public class ApplicationAttemptFinishDataPBImpl extends ApplicationAttemptFinishData
{
    ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto proto;
    ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptId applicationAttemptId;
    
    public ApplicationAttemptFinishDataPBImpl() {
        this.proto = ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto.newBuilder();
    }
    
    public ApplicationAttemptFinishDataPBImpl(final ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto proto) {
        this.proto = ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto.getDefaultInstance();
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
        final ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public String getTrackingURL() {
        final ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasTrackingUrl()) {
            return null;
        }
        return p.getTrackingUrl();
    }
    
    @Override
    public void setTrackingURL(final String trackingURL) {
        this.maybeInitBuilder();
        if (trackingURL == null) {
            this.builder.clearTrackingUrl();
            return;
        }
        this.builder.setTrackingUrl(trackingURL);
    }
    
    @Override
    public String getDiagnosticsInfo() {
        final ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
        final ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public YarnApplicationAttemptState getYarnApplicationAttemptState() {
        final ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasYarnApplicationAttemptState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getYarnApplicationAttemptState());
    }
    
    @Override
    public void setYarnApplicationAttemptState(final YarnApplicationAttemptState state) {
        this.maybeInitBuilder();
        if (state == null) {
            this.builder.clearYarnApplicationAttemptState();
            return;
        }
        this.builder.setYarnApplicationAttemptState(this.convertToProtoFormat(state));
    }
    
    public ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationAttemptFinishDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationAttemptId != null && !((ApplicationAttemptIdPBImpl)this.applicationAttemptId).getProto().equals(this.builder.getApplicationAttemptId())) {
            this.builder.setApplicationAttemptId(this.convertToProtoFormat(this.applicationAttemptId));
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
            this.builder = ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private ApplicationAttemptIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptIdProto applicationAttemptId) {
        return new ApplicationAttemptIdPBImpl(applicationAttemptId);
    }
    
    private YarnProtos.ApplicationAttemptIdProto convertToProtoFormat(final ApplicationAttemptId applicationAttemptId) {
        return ((ApplicationAttemptIdPBImpl)applicationAttemptId).getProto();
    }
    
    private FinalApplicationStatus convertFromProtoFormat(final YarnProtos.FinalApplicationStatusProto finalApplicationStatus) {
        return ProtoUtils.convertFromProtoFormat(finalApplicationStatus);
    }
    
    private YarnProtos.FinalApplicationStatusProto convertToProtoFormat(final FinalApplicationStatus finalApplicationStatus) {
        return ProtoUtils.convertToProtoFormat(finalApplicationStatus);
    }
    
    private YarnProtos.YarnApplicationAttemptStateProto convertToProtoFormat(final YarnApplicationAttemptState state) {
        return ProtoUtils.convertToProtoFormat(state);
    }
    
    private YarnApplicationAttemptState convertFromProtoFormat(final YarnProtos.YarnApplicationAttemptStateProto yarnApplicationAttemptState) {
        return ProtoUtils.convertFromProtoFormat(yarnApplicationAttemptState);
    }
}
