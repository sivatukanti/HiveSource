// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ContainerReportPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportResponse;

public class GetContainerReportResponsePBImpl extends GetContainerReportResponse
{
    YarnServiceProtos.GetContainerReportResponseProto proto;
    YarnServiceProtos.GetContainerReportResponseProto.Builder builder;
    boolean viaProto;
    private ContainerReport containerReport;
    
    public GetContainerReportResponsePBImpl() {
        this.proto = YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerReport = null;
        this.builder = YarnServiceProtos.GetContainerReportResponseProto.newBuilder();
    }
    
    public GetContainerReportResponsePBImpl(final YarnServiceProtos.GetContainerReportResponseProto proto) {
        this.proto = YarnServiceProtos.GetContainerReportResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerReport = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetContainerReportResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetContainerReportResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerReport != null) {
            this.builder.setContainerReport(this.convertToProtoFormat(this.containerReport));
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
            this.builder = YarnServiceProtos.GetContainerReportResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ContainerReport getContainerReport() {
        if (this.containerReport != null) {
            return this.containerReport;
        }
        final YarnServiceProtos.GetContainerReportResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerReport()) {
            return null;
        }
        return this.containerReport = this.convertFromProtoFormat(p.getContainerReport());
    }
    
    @Override
    public void setContainerReport(final ContainerReport containerReport) {
        this.maybeInitBuilder();
        if (containerReport == null) {
            this.builder.clearContainerReport();
        }
        this.containerReport = containerReport;
    }
    
    private ContainerReportPBImpl convertFromProtoFormat(final YarnProtos.ContainerReportProto p) {
        return new ContainerReportPBImpl(p);
    }
    
    private YarnProtos.ContainerReportProto convertToProtoFormat(final ContainerReport t) {
        return ((ContainerReportPBImpl)t).getProto();
    }
}
