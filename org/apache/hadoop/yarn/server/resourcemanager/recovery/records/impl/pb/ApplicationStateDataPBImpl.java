// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.recovery.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerRecoveryProtos;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;

public class ApplicationStateDataPBImpl extends ApplicationStateData
{
    YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto proto;
    YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.Builder builder;
    boolean viaProto;
    private ApplicationSubmissionContext applicationSubmissionContext;
    private static String RM_APP_PREFIX;
    
    public ApplicationStateDataPBImpl() {
        this.proto = YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationSubmissionContext = null;
        this.builder = YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.newBuilder();
    }
    
    public ApplicationStateDataPBImpl(final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto proto) {
        this.proto = YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationSubmissionContext = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationSubmissionContext != null) {
            this.builder.setApplicationSubmissionContext(((ApplicationSubmissionContextPBImpl)this.applicationSubmissionContext).getProto());
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
            this.builder = YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public long getSubmitTime() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasSubmitTime()) {
            return -1L;
        }
        return p.getSubmitTime();
    }
    
    @Override
    public void setSubmitTime(final long submitTime) {
        this.maybeInitBuilder();
        this.builder.setSubmitTime(submitTime);
    }
    
    @Override
    public long getStartTime() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getStartTime();
    }
    
    @Override
    public void setStartTime(final long startTime) {
        this.maybeInitBuilder();
        this.builder.setStartTime(startTime);
    }
    
    @Override
    public String getUser() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasUser()) {
            return null;
        }
        return p.getUser();
    }
    
    @Override
    public void setUser(final String user) {
        this.maybeInitBuilder();
        this.builder.setUser(user);
    }
    
    @Override
    public ApplicationSubmissionContext getApplicationSubmissionContext() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.applicationSubmissionContext != null) {
            return this.applicationSubmissionContext;
        }
        if (!p.hasApplicationSubmissionContext()) {
            return null;
        }
        return this.applicationSubmissionContext = new ApplicationSubmissionContextPBImpl(p.getApplicationSubmissionContext());
    }
    
    @Override
    public void setApplicationSubmissionContext(final ApplicationSubmissionContext context) {
        this.maybeInitBuilder();
        if (context == null) {
            this.builder.clearApplicationSubmissionContext();
        }
        this.applicationSubmissionContext = context;
    }
    
    @Override
    public RMAppState getState() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationState()) {
            return null;
        }
        return convertFromProtoFormat(p.getApplicationState());
    }
    
    @Override
    public void setState(final RMAppState finalState) {
        this.maybeInitBuilder();
        if (finalState == null) {
            this.builder.clearApplicationState();
            return;
        }
        this.builder.setApplicationState(convertToProtoFormat(finalState));
    }
    
    @Override
    public String getDiagnostics() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public long getFinishTime() {
        final YarnServerResourceManagerRecoveryProtos.ApplicationStateDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getFinishTime();
    }
    
    @Override
    public void setFinishTime(final long finishTime) {
        this.maybeInitBuilder();
        this.builder.setFinishTime(finishTime);
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationStateDataPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    public static YarnServerResourceManagerRecoveryProtos.RMAppStateProto convertToProtoFormat(final RMAppState e) {
        return YarnServerResourceManagerRecoveryProtos.RMAppStateProto.valueOf(ApplicationStateDataPBImpl.RM_APP_PREFIX + e.name());
    }
    
    public static RMAppState convertFromProtoFormat(final YarnServerResourceManagerRecoveryProtos.RMAppStateProto e) {
        return RMAppState.valueOf(e.name().replace(ApplicationStateDataPBImpl.RM_APP_PREFIX, ""));
    }
    
    static {
        ApplicationStateDataPBImpl.RM_APP_PREFIX = "RMAPP_";
    }
}
