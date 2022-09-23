// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.security.proto.SecurityProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import java.util.Collection;
import java.util.HashSet;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.Token;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.ApplicationReport;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ApplicationReportPBImpl extends ApplicationReport
{
    YarnProtos.ApplicationReportProto proto;
    YarnProtos.ApplicationReportProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    private ApplicationAttemptId currentApplicationAttemptId;
    private Token clientToAMToken;
    private Token amRmToken;
    private Set<String> applicationTags;
    
    public ApplicationReportPBImpl() {
        this.proto = YarnProtos.ApplicationReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.clientToAMToken = null;
        this.amRmToken = null;
        this.applicationTags = null;
        this.builder = YarnProtos.ApplicationReportProto.newBuilder();
    }
    
    public ApplicationReportPBImpl(final YarnProtos.ApplicationReportProto proto) {
        this.proto = YarnProtos.ApplicationReportProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.clientToAMToken = null;
        this.amRmToken = null;
        this.applicationTags = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        if (this.applicationId != null) {
            return this.applicationId;
        }
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationId()) {
            return null;
        }
        return this.applicationId = this.convertFromProtoFormat(p.getApplicationId());
    }
    
    @Override
    public void setApplicationResourceUsageReport(final ApplicationResourceUsageReport appInfo) {
        this.maybeInitBuilder();
        if (appInfo == null) {
            this.builder.clearAppResourceUsage();
            return;
        }
        this.builder.setAppResourceUsage(this.convertToProtoFormat(appInfo));
    }
    
    @Override
    public ApplicationAttemptId getCurrentApplicationAttemptId() {
        if (this.currentApplicationAttemptId != null) {
            return this.currentApplicationAttemptId;
        }
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasCurrentApplicationAttemptId()) {
            return null;
        }
        return this.currentApplicationAttemptId = this.convertFromProtoFormat(p.getCurrentApplicationAttemptId());
    }
    
    @Override
    public ApplicationResourceUsageReport getApplicationResourceUsageReport() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasAppResourceUsage()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getAppResourceUsage());
    }
    
    @Override
    public String getTrackingUrl() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasTrackingUrl()) {
            return null;
        }
        return p.getTrackingUrl();
    }
    
    @Override
    public String getOriginalTrackingUrl() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasOriginalTrackingUrl()) {
            return null;
        }
        return p.getOriginalTrackingUrl();
    }
    
    @Override
    public String getName() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasName()) {
            return null;
        }
        return p.getName();
    }
    
    @Override
    public String getQueue() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasQueue()) {
            return null;
        }
        return p.getQueue();
    }
    
    @Override
    public YarnApplicationState getYarnApplicationState() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasYarnApplicationState()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getYarnApplicationState());
    }
    
    @Override
    public String getHost() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasHost()) {
            return null;
        }
        return p.getHost();
    }
    
    @Override
    public int getRpcPort() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getRpcPort();
    }
    
    @Override
    public Token getClientToAMToken() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.clientToAMToken != null) {
            return this.clientToAMToken;
        }
        if (!p.hasClientToAmToken()) {
            return null;
        }
        return this.clientToAMToken = this.convertFromProtoFormat(p.getClientToAmToken());
    }
    
    @Override
    public String getUser() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasUser()) {
            return null;
        }
        return p.getUser();
    }
    
    @Override
    public String getDiagnostics() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasDiagnostics()) {
            return null;
        }
        return p.getDiagnostics();
    }
    
    @Override
    public long getStartTime() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getStartTime();
    }
    
    @Override
    public long getFinishTime() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getFinishTime();
    }
    
    @Override
    public FinalApplicationStatus getFinalApplicationStatus() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasFinalApplicationStatus()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getFinalApplicationStatus());
    }
    
    @Override
    public float getProgress() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getProgress();
    }
    
    @Override
    public String getApplicationType() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationType()) {
            return null;
        }
        return p.getApplicationType();
    }
    
    @Override
    public Token getAMRMToken() {
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.amRmToken != null) {
            return this.amRmToken;
        }
        if (!p.hasAmRmToken()) {
            return null;
        }
        return this.amRmToken = this.convertFromProtoFormat(p.getAmRmToken());
    }
    
    private void initApplicationTags() {
        if (this.applicationTags != null) {
            return;
        }
        final YarnProtos.ApplicationReportProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        (this.applicationTags = new HashSet<String>()).addAll(p.getApplicationTagsList());
    }
    
    @Override
    public Set<String> getApplicationTags() {
        this.initApplicationTags();
        return this.applicationTags;
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
    public void setCurrentApplicationAttemptId(final ApplicationAttemptId applicationAttemptId) {
        this.maybeInitBuilder();
        if (applicationAttemptId == null) {
            this.builder.clearCurrentApplicationAttemptId();
        }
        this.currentApplicationAttemptId = applicationAttemptId;
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
    public void setOriginalTrackingUrl(final String url) {
        this.maybeInitBuilder();
        if (url == null) {
            this.builder.clearOriginalTrackingUrl();
            return;
        }
        this.builder.setOriginalTrackingUrl(url);
    }
    
    @Override
    public void setName(final String name) {
        this.maybeInitBuilder();
        if (name == null) {
            this.builder.clearName();
            return;
        }
        this.builder.setName(name);
    }
    
    @Override
    public void setQueue(final String queue) {
        this.maybeInitBuilder();
        if (queue == null) {
            this.builder.clearQueue();
            return;
        }
        this.builder.setQueue(queue);
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
    public void setClientToAMToken(final Token clientToAMToken) {
        this.maybeInitBuilder();
        if (clientToAMToken == null) {
            this.builder.clearClientToAmToken();
        }
        this.clientToAMToken = clientToAMToken;
    }
    
    @Override
    public void setUser(final String user) {
        this.maybeInitBuilder();
        if (user == null) {
            this.builder.clearUser();
            return;
        }
        this.builder.setUser(user);
    }
    
    @Override
    public void setApplicationType(final String applicationType) {
        this.maybeInitBuilder();
        if (applicationType == null) {
            this.builder.clearApplicationType();
            return;
        }
        this.builder.setApplicationType(applicationType);
    }
    
    @Override
    public void setApplicationTags(final Set<String> tags) {
        this.maybeInitBuilder();
        if (tags == null || tags.isEmpty()) {
            this.builder.clearApplicationTags();
        }
        this.applicationTags = tags;
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
    public void setStartTime(final long startTime) {
        this.maybeInitBuilder();
        this.builder.setStartTime(startTime);
    }
    
    @Override
    public void setFinishTime(final long finishTime) {
        this.maybeInitBuilder();
        this.builder.setFinishTime(finishTime);
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
    public void setProgress(final float progress) {
        this.maybeInitBuilder();
        this.builder.setProgress(progress);
    }
    
    @Override
    public void setAMRMToken(final Token amRmToken) {
        this.maybeInitBuilder();
        if (amRmToken == null) {
            this.builder.clearAmRmToken();
        }
        this.amRmToken = amRmToken;
    }
    
    public YarnProtos.ApplicationReportProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationReportPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationId != null && !((ApplicationIdPBImpl)this.applicationId).getProto().equals(this.builder.getApplicationId())) {
            this.builder.setApplicationId(this.convertToProtoFormat(this.applicationId));
        }
        if (this.currentApplicationAttemptId != null && !((ApplicationAttemptIdPBImpl)this.currentApplicationAttemptId).getProto().equals(this.builder.getCurrentApplicationAttemptId())) {
            this.builder.setCurrentApplicationAttemptId(this.convertToProtoFormat(this.currentApplicationAttemptId));
        }
        if (this.clientToAMToken != null && !((TokenPBImpl)this.clientToAMToken).getProto().equals(this.builder.getClientToAmToken())) {
            this.builder.setClientToAmToken(this.convertToProtoFormat(this.clientToAMToken));
        }
        if (this.amRmToken != null && !((TokenPBImpl)this.amRmToken).getProto().equals(this.builder.getAmRmToken())) {
            this.builder.setAmRmToken(this.convertToProtoFormat(this.amRmToken));
        }
        if (this.applicationTags != null && !this.applicationTags.isEmpty()) {
            this.builder.clearApplicationTags();
            this.builder.addAllApplicationTags(this.applicationTags);
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
            this.builder = YarnProtos.ApplicationReportProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
    
    private YarnProtos.ApplicationAttemptIdProto convertToProtoFormat(final ApplicationAttemptId t) {
        return ((ApplicationAttemptIdPBImpl)t).getProto();
    }
    
    private ApplicationResourceUsageReport convertFromProtoFormat(final YarnProtos.ApplicationResourceUsageReportProto s) {
        return ProtoUtils.convertFromProtoFormat(s);
    }
    
    private YarnProtos.ApplicationResourceUsageReportProto convertToProtoFormat(final ApplicationResourceUsageReport s) {
        return ProtoUtils.convertToProtoFormat(s);
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto applicationId) {
        return new ApplicationIdPBImpl(applicationId);
    }
    
    private ApplicationAttemptIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptIdProto applicationAttemptId) {
        return new ApplicationAttemptIdPBImpl(applicationAttemptId);
    }
    
    private YarnApplicationState convertFromProtoFormat(final YarnProtos.YarnApplicationStateProto s) {
        return ProtoUtils.convertFromProtoFormat(s);
    }
    
    private YarnProtos.YarnApplicationStateProto convertToProtoFormat(final YarnApplicationState s) {
        return ProtoUtils.convertToProtoFormat(s);
    }
    
    private FinalApplicationStatus convertFromProtoFormat(final YarnProtos.FinalApplicationStatusProto s) {
        return ProtoUtils.convertFromProtoFormat(s);
    }
    
    private YarnProtos.FinalApplicationStatusProto convertToProtoFormat(final FinalApplicationStatus s) {
        return ProtoUtils.convertToProtoFormat(s);
    }
    
    private TokenPBImpl convertFromProtoFormat(final SecurityProtos.TokenProto p) {
        return new TokenPBImpl(p);
    }
    
    private SecurityProtos.TokenProto convertToProtoFormat(final Token t) {
        return ((TokenPBImpl)t).getProto();
    }
}
