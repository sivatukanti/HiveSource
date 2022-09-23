// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.api.records.impl.pb.ContainerPBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;

public class ApplicationAttemptStateDataPBImpl extends ApplicationAttemptStateData
{
    YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto proto;
    YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptId attemptId;
    private Container masterContainer;
    private ByteBuffer appAttemptTokens;
    private static String RM_APP_ATTEMPT_PREFIX;
    
    public ApplicationAttemptStateDataPBImpl() {
        this.proto = YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.attemptId = null;
        this.masterContainer = null;
        this.appAttemptTokens = null;
        this.builder = YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.newBuilder();
    }
    
    public ApplicationAttemptStateDataPBImpl(final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto proto) {
        this.proto = YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.attemptId = null;
        this.masterContainer = null;
        this.appAttemptTokens = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.attemptId != null) {
            this.builder.setAttemptId(((ApplicationAttemptIdPBImpl)this.attemptId).getProto());
        }
        if (this.masterContainer != null) {
            this.builder.setMasterContainer(((ContainerPBImpl)this.masterContainer).getProto());
        }
        if (this.appAttemptTokens != null) {
            this.builder.setAppAttemptTokens(ProtoUtils.convertToProtoFormat(this.appAttemptTokens));
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
            this.builder = YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationAttemptId getAttemptId() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.attemptId != null) {
            return this.attemptId;
        }
        if (!p.hasAttemptId()) {
            return null;
        }
        return this.attemptId = new ApplicationAttemptIdPBImpl(p.getAttemptId());
    }
    
    @Override
    public void setAttemptId(final ApplicationAttemptId attemptId) {
        this.maybeInitBuilder();
        if (attemptId == null) {
            this.builder.clearAttemptId();
        }
        this.attemptId = attemptId;
    }
    
    @Override
    public Container getMasterContainer() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.masterContainer != null) {
            return this.masterContainer;
        }
        if (!p.hasMasterContainer()) {
            return null;
        }
        return this.masterContainer = new ContainerPBImpl(p.getMasterContainer());
    }
    
    @Override
    public void setMasterContainer(final Container container) {
        this.maybeInitBuilder();
        if (container == null) {
            this.builder.clearMasterContainer();
        }
        this.masterContainer = container;
    }
    
    @Override
    public ByteBuffer getAppAttemptTokens() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.appAttemptTokens != null) {
            return this.appAttemptTokens;
        }
        if (!p.hasAppAttemptTokens()) {
            return null;
        }
        return this.appAttemptTokens = ProtoUtils.convertFromProtoFormat(p.getAppAttemptTokens());
    }
    
    @Override
    public void setAppAttemptTokens(final ByteBuffer attemptTokens) {
        this.maybeInitBuilder();
        if (attemptTokens == null) {
            this.builder.clearAppAttemptTokens();
        }
        this.appAttemptTokens = attemptTokens;
    }
    
    @Override
    public RMAppAttemptState getState() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasAppAttemptState()) {
            return null;
        }
        return convertFromProtoFormat(p.getAppAttemptState());
    }
    
    @Override
    public void setState(final RMAppAttemptState state) {
        this.maybeInitBuilder();
        if (state == null) {
            this.builder.clearAppAttemptState();
            return;
        }
        this.builder.setAppAttemptState(convertToProtoFormat(state));
    }
    
    @Override
    public String getFinalTrackingUrl() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasFinalTrackingUrl()) {
            return null;
        }
        return p.getFinalTrackingUrl();
    }
    
    @Override
    public void setFinalTrackingUrl(final String url) {
        this.maybeInitBuilder();
        if (url == null) {
            this.builder.clearFinalTrackingUrl();
            return;
        }
        this.builder.setFinalTrackingUrl(url);
    }
    
    @Override
    public String getDiagnostics() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnostics()) {
            return null;
        }
        return p.getDiagnostics();
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
    
    @Override
    public long getStartTime() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getStartTime();
    }
    
    @Override
    public void setStartTime(final long startTime) {
        this.maybeInitBuilder();
        this.builder.setStartTime(startTime);
    }
    
    @Override
    public long getMemorySeconds() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getMemorySeconds();
    }
    
    @Override
    public long getVcoreSeconds() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getVcoreSeconds();
    }
    
    @Override
    public void setMemorySeconds(final long memorySeconds) {
        this.maybeInitBuilder();
        this.builder.setMemorySeconds(memorySeconds);
    }
    
    @Override
    public void setVcoreSeconds(final long vcoreSeconds) {
        this.maybeInitBuilder();
        this.builder.setVcoreSeconds(vcoreSeconds);
    }
    
    @Override
    public FinalApplicationStatus getFinalApplicationStatus() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasFinalApplicationStatus()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getFinalApplicationStatus());
    }
    
    @Override
    public void setFinalApplicationStatus(final FinalApplicationStatus finishState) {
        this.maybeInitBuilder();
        if (finishState == null) {
            this.builder.clearFinalApplicationStatus();
            return;
        }
        this.builder.setFinalApplicationStatus(this.convertToProtoFormat(finishState));
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public int getAMContainerExitStatus() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getAmContainerExitStatus();
    }
    
    @Override
    public void setAMContainerExitStatus(final int exitStatus) {
        this.maybeInitBuilder();
        this.builder.setAmContainerExitStatus(exitStatus);
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationAttemptStateDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    public static YarnServerResourceManagerRecoveryProtos.RMAppAttemptStateProto convertToProtoFormat(final RMAppAttemptState e) {
        return YarnServerResourceManagerRecoveryProtos.RMAppAttemptStateProto.valueOf(ApplicationAttemptStateDataPBImpl.RM_APP_ATTEMPT_PREFIX + e.name());
    }
    
    public static RMAppAttemptState convertFromProtoFormat(final YarnServerResourceManagerRecoveryProtos.RMAppAttemptStateProto e) {
        return RMAppAttemptState.valueOf(e.name().replace(ApplicationAttemptStateDataPBImpl.RM_APP_ATTEMPT_PREFIX, ""));
    }
    
    private YarnProtos.FinalApplicationStatusProto convertToProtoFormat(final FinalApplicationStatus s) {
        return ProtoUtils.convertToProtoFormat(s);
    }
    
    private FinalApplicationStatus convertFromProtoFormat(final YarnProtos.FinalApplicationStatusProto s) {
        return ProtoUtils.convertFromProtoFormat(s);
    }
    
    @Override
    public long getFinishTime() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationAttemptStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getFinishTime();
    }
    
    @Override
    public void setFinishTime(final long finishTime) {
        this.maybeInitBuilder();
        this.builder.setFinishTime(finishTime);
    }
    
    static {
        ApplicationAttemptStateDataPBImpl.RM_APP_ATTEMPT_PREFIX = "RMATTEMPT_";
    }
}
