// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb;

import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;

public class ApplicationStartDataPBImpl extends ApplicationStartData
{
    ApplicationHistoryServerProtos.ApplicationStartDataProto proto;
    ApplicationHistoryServerProtos.ApplicationStartDataProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    
    public ApplicationStartDataPBImpl() {
        this.proto = ApplicationHistoryServerProtos.ApplicationStartDataProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = ApplicationHistoryServerProtos.ApplicationStartDataProto.newBuilder();
    }
    
    public ApplicationStartDataPBImpl(final ApplicationHistoryServerProtos.ApplicationStartDataProto proto) {
        this.proto = ApplicationHistoryServerProtos.ApplicationStartDataProto.getDefaultInstance();
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
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public String getApplicationName() {
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationName()) {
            return null;
        }
        return p.getApplicationName();
    }
    
    @Override
    public void setApplicationName(final String applicationName) {
        this.maybeInitBuilder();
        if (applicationName == null) {
            this.builder.clearApplicationName();
            return;
        }
        this.builder.setApplicationName(applicationName);
    }
    
    @Override
    public String getApplicationType() {
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationType()) {
            return null;
        }
        return p.getApplicationType();
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
    public String getUser() {
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasUser()) {
            return null;
        }
        return p.getUser();
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
    public String getQueue() {
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasQueue()) {
            return null;
        }
        return p.getQueue();
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
    public long getSubmitTime() {
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getSubmitTime();
    }
    
    @Override
    public void setSubmitTime(final long submitTime) {
        this.maybeInitBuilder();
        this.builder.setSubmitTime(submitTime);
    }
    
    @Override
    public long getStartTime() {
        final ApplicationHistoryServerProtos.ApplicationStartDataProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getStartTime();
    }
    
    @Override
    public void setStartTime(final long startTime) {
        this.maybeInitBuilder();
        this.builder.setStartTime(startTime);
    }
    
    public ApplicationHistoryServerProtos.ApplicationStartDataProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ApplicationStartDataPBImpl)this.getClass().cast(other)).getProto());
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
            this.builder = ApplicationHistoryServerProtos.ApplicationStartDataProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId applicationId) {
        return ((ApplicationIdPBImpl)applicationId).getProto();
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto applicationId) {
        return new ApplicationIdPBImpl(applicationId);
    }
}
