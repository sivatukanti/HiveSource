// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationReportPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationReportResponsePBImpl extends GetApplicationReportResponse
{
    YarnServiceProtos.GetApplicationReportResponseProto proto;
    YarnServiceProtos.GetApplicationReportResponseProto.Builder builder;
    boolean viaProto;
    private ApplicationReport applicationReport;
    
    public GetApplicationReportResponsePBImpl() {
        this.proto = YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationReport = null;
        this.builder = YarnServiceProtos.GetApplicationReportResponseProto.newBuilder();
    }
    
    public GetApplicationReportResponsePBImpl(final YarnServiceProtos.GetApplicationReportResponseProto proto) {
        this.proto = YarnServiceProtos.GetApplicationReportResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationReport = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetApplicationReportResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationReportResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationReport != null) {
            this.builder.setApplicationReport(this.convertToProtoFormat(this.applicationReport));
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
            this.builder = YarnServiceProtos.GetApplicationReportResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationReport getApplicationReport() {
        final YarnServiceProtos.GetApplicationReportResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.applicationReport != null) {
            return this.applicationReport;
        }
        if (!p.hasApplicationReport()) {
            return null;
        }
        return this.applicationReport = this.convertFromProtoFormat(p.getApplicationReport());
    }
    
    @Override
    public void setApplicationReport(final ApplicationReport applicationMaster) {
        this.maybeInitBuilder();
        if (applicationMaster == null) {
            this.builder.clearApplicationReport();
        }
        this.applicationReport = applicationMaster;
    }
    
    private ApplicationReportPBImpl convertFromProtoFormat(final YarnProtos.ApplicationReportProto p) {
        return new ApplicationReportPBImpl(p);
    }
    
    private YarnProtos.ApplicationReportProto convertToProtoFormat(final ApplicationReport t) {
        return ((ApplicationReportPBImpl)t).getProto();
    }
}
