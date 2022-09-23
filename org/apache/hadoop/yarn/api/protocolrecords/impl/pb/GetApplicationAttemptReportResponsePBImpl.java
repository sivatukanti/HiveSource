// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationAttemptReportPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationAttemptReportResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationAttemptReportResponsePBImpl extends GetApplicationAttemptReportResponse
{
    YarnServiceProtos.GetApplicationAttemptReportResponseProto proto;
    YarnServiceProtos.GetApplicationAttemptReportResponseProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptReport applicationAttemptReport;
    
    public GetApplicationAttemptReportResponsePBImpl() {
        this.proto = YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationAttemptReport = null;
        this.builder = YarnServiceProtos.GetApplicationAttemptReportResponseProto.newBuilder();
    }
    
    public GetApplicationAttemptReportResponsePBImpl(final YarnServiceProtos.GetApplicationAttemptReportResponseProto proto) {
        this.proto = YarnServiceProtos.GetApplicationAttemptReportResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationAttemptReport = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetApplicationAttemptReportResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationAttemptReportResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationAttemptReport != null) {
            this.builder.setApplicationAttemptReport(this.convertToProtoFormat(this.applicationAttemptReport));
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
            this.builder = YarnServiceProtos.GetApplicationAttemptReportResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationAttemptReport getApplicationAttemptReport() {
        if (this.applicationAttemptReport != null) {
            return this.applicationAttemptReport;
        }
        final YarnServiceProtos.GetApplicationAttemptReportResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationAttemptReport()) {
            return null;
        }
        return this.applicationAttemptReport = this.convertFromProtoFormat(p.getApplicationAttemptReport());
    }
    
    @Override
    public void setApplicationAttemptReport(final ApplicationAttemptReport ApplicationAttemptReport) {
        this.maybeInitBuilder();
        if (ApplicationAttemptReport == null) {
            this.builder.clearApplicationAttemptReport();
        }
        this.applicationAttemptReport = ApplicationAttemptReport;
    }
    
    private ApplicationAttemptReportPBImpl convertFromProtoFormat(final YarnProtos.ApplicationAttemptReportProto p) {
        return new ApplicationAttemptReportPBImpl(p);
    }
    
    private YarnProtos.ApplicationAttemptReportProto convertToProtoFormat(final ApplicationAttemptReport t) {
        return ((ApplicationAttemptReportPBImpl)t).getProto();
    }
}
