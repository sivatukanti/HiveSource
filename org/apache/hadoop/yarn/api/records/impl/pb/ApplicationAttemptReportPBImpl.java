// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;

public class ApplicationAttemptReportPBImpl extends ApplicationAttemptReport
{
    YarnProtos.ApplicationAttemptReportProto proto;
    YarnProtos.ApplicationAttemptReportProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptId ApplicationAttemptId;
    private ContainerId amContainerId;
    
    public ApplicationAttemptReportPBImpl() {
        this.proto = YarnProtos.ApplicationAttemptReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.ApplicationAttemptReportProto.newBuilder();
    }
    
    public ApplicationAttemptReportPBImpl(final YarnProtos.ApplicationAttemptReportProto proto) {
        this.proto = YarnProtos.ApplicationAttemptReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public ApplicationAttemptId getApplicationAttemptId() {
        if (this.ApplicationAttemptId != null) {
            return this.ApplicationAttemptId;
        }
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationAttemptId()) {
            return null;
        }
        return this.ApplicationAttemptId = this.convertFromProtoFormat(p.getApplicationAttemptId());
    }
    
    @Override
    public String getHost() {
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasHost()) {
            return null;
        }
        return p.getHost();
    }
    
    @Override
    public int getRpcPort() {
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getRpcPort();
    }
    
    @Override
    public String getTrackingUrl() {
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasTrackingUrl()) {
            return null;
        }
        return p.getTrackingUrl();
    }
    
    @Override
    public String getOriginalTrackingUrl() {
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasOriginalTrackingUrl()) {
            return null;
        }
        return p.getOriginalTrackingUrl();
    }
    
    @Override
    public String getDiagnostics() {
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnostics()) {
            return null;
        }
        return p.getDiagnostics();
    }
    
    @Override
    public YarnApplicationAttemptState getYarnApplicationAttemptState() {
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    
    private YarnProtos.YarnApplicationAttemptStateProto convertToProtoFormat(final YarnApplicationAttemptState state) {
        return ProtoUtils.convertToProtoFormat(state);
    }
    
    private YarnApplicationAttemptState convertFromProtoFormat(final YarnProtos.YarnApplicationAttemptStateProto yarnApplicationAttemptState) {
        return ProtoUtils.convertFromProtoFormat(yarnApplicationAttemptState);
    }
    
    @Override
    public void setApplicationAttemptId(final ApplicationAttemptId applicationAttemptId) {
        this.maybeInitBuilder();
        if (applicationAttemptId == null) {
            this.builder.clearApplicationAttemptId();
        }
        this.ApplicationAttemptId = applicationAttemptId;
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
    public void setRpcPort(final int rpcPort) {
        this.maybeInitBuilder();
        this.builder.setRpcPort(rpcPort);
    }
    
    @Override
    public void setTrackingUrl(final String url) {
        this.maybeInitBuilder();
        if (url == null) {
            this.builder.clearTrackingUrl();
            return;
        }
        this.builder.setTrackingUrl(url);
    }
    
    @Override
    public void setOriginalTrackingUrl(final String oUrl) {
        this.maybeInitBuilder();
        if (oUrl == null) {
            this.builder.clearOriginalTrackingUrl();
            return;
        }
        this.builder.setOriginalTrackingUrl(oUrl);
    }
    
    @Override
    public void setDiagnostics(final String diagnostics) {
        this.maybeInitBuilder();
        if (diagnostics == null) {
            this.builder.clearDiagnostics();
            return;
        }
        this.builder.setDiagnostics(diagnostics);
    }
    
    public YarnProtos.ApplicationAttemptReportProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationAttemptReportPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ApplicationAttemptReportProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void mergeLocalToBuilder() {
        if (this.ApplicationAttemptId != null && !((ApplicationAttemptIdPBImpl)this.ApplicationAttemptId).getProto().equals(this.builder.getApplicationAttemptId())) {
            this.builder.setApplicationAttemptId(this.convertToProtoFormat(this.ApplicationAttemptId));
        }
        if (this.amContainerId != null && !((ContainerIdPBImpl)this.amContainerId).getProto().equals(this.builder.getAmContainerId())) {
            this.builder.setAmContainerId(this.convertToProtoFormat(this.amContainerId));
        }
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId amContainerId) {
        return ((ContainerIdPBImpl)amContainerId).getProto();
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto amContainerId) {
        return new ContainerIdPBImpl(amContainerId);
    }
    
    private YarnProtos.ApplicationAttemptIdProto convertToProtoFormat(final ApplicationAttemptId t) {
        return ((ApplicationAttemptIdPBImpl)t).getProto();
    }
    
    private ApplicationAttemptIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptIdProto applicationAttemptId) {
        return new ApplicationAttemptIdPBImpl(applicationAttemptId);
    }
    
    @Override
    public ContainerId getAMContainerId() {
        if (this.amContainerId != null) {
            return this.amContainerId;
        }
        final YarnProtos.ApplicationAttemptReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasAmContainerId()) {
            return null;
        }
        return this.amContainerId = this.convertFromProtoFormat(p.getAmContainerId());
    }
    
    @Override
    public void setAMContainerId(final ContainerId amContainerId) {
        this.maybeInitBuilder();
        if (amContainerId == null) {
            this.builder.clearAmContainerId();
        }
        this.amContainerId = amContainerId;
    }
}
