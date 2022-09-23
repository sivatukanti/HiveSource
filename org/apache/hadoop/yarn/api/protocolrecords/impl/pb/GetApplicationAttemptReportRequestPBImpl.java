// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationAttemptReportRequestPBImpl extends GetApplicationAttemptReportRequest
{
    YarnServiceProtos.GetApplicationAttemptReportRequestProto proto;
    YarnServiceProtos.GetApplicationAttemptReportRequestProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptId applicationAttemptId;
    
    public GetApplicationAttemptReportRequestPBImpl() {
        this.proto = YarnServiceProtos.GetApplicationAttemptReportRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationAttemptId = null;
        this.builder = YarnServiceProtos.GetApplicationAttemptReportRequestProto.newBuilder();
    }
    
    public GetApplicationAttemptReportRequestPBImpl(final YarnServiceProtos.GetApplicationAttemptReportRequestProto proto) {
        this.proto = YarnServiceProtos.GetApplicationAttemptReportRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationAttemptId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetApplicationAttemptReportRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationAttemptReportRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationAttemptId != null) {
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
            this.builder = YarnServiceProtos.GetApplicationAttemptReportRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationAttemptId getApplicationAttemptId() {
        if (this.applicationAttemptId != null) {
            return this.applicationAttemptId;
        }
        final YarnServiceProtos.GetApplicationAttemptReportRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    
    private ApplicationAttemptIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptIdProto p) {
        return new ApplicationAttemptIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationAttemptIdProto convertToProtoFormat(final ApplicationAttemptId t) {
        return ((ApplicationAttemptIdPBImpl)t).getProto();
    }
}
