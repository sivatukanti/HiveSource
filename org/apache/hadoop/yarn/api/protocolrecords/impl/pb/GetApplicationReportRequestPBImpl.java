// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetApplicationReportRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetApplicationReportRequestPBImpl extends GetApplicationReportRequest
{
    YarnServiceProtos.GetApplicationReportRequestProto proto;
    YarnServiceProtos.GetApplicationReportRequestProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    
    public GetApplicationReportRequestPBImpl() {
        this.proto = YarnServiceProtos.GetApplicationReportRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.builder = YarnServiceProtos.GetApplicationReportRequestProto.newBuilder();
    }
    
    public GetApplicationReportRequestPBImpl(final YarnServiceProtos.GetApplicationReportRequestProto proto) {
        this.proto = YarnServiceProtos.GetApplicationReportRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetApplicationReportRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetApplicationReportRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationId != null) {
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
            this.builder = YarnServiceProtos.GetApplicationReportRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        final YarnServiceProtos.GetApplicationReportRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.applicationId != null) {
            return this.applicationId;
        }
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
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
}
