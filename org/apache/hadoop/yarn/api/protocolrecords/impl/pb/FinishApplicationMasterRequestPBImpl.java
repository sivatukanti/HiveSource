// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ProtoUtils;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FinishApplicationMasterRequestPBImpl extends FinishApplicationMasterRequest
{
    YarnServiceProtos.FinishApplicationMasterRequestProto proto;
    YarnServiceProtos.FinishApplicationMasterRequestProto.Builder builder;
    boolean viaProto;
    
    public FinishApplicationMasterRequestPBImpl() {
        this.proto = YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.FinishApplicationMasterRequestProto.newBuilder();
    }
    
    public FinishApplicationMasterRequestPBImpl(final YarnServiceProtos.FinishApplicationMasterRequestProto proto) {
        this.proto = YarnServiceProtos.FinishApplicationMasterRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.FinishApplicationMasterRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((FinishApplicationMasterRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
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
            this.builder = YarnServiceProtos.FinishApplicationMasterRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public String getDiagnostics() {
        final YarnServiceProtos.FinishApplicationMasterRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    public String getTrackingUrl() {
        final YarnServiceProtos.FinishApplicationMasterRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getTrackingUrl();
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
    public FinalApplicationStatus getFinalApplicationStatus() {
        final YarnServiceProtos.FinishApplicationMasterRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasFinalApplicationStatus()) {
            return null;
        }
        return this.convertFromProtoFormat(p.getFinalApplicationStatus());
    }
    
    @Override
    public void setFinalApplicationStatus(final FinalApplicationStatus finalState) {
        this.maybeInitBuilder();
        if (finalState == null) {
            this.builder.clearFinalApplicationStatus();
            return;
        }
        this.builder.setFinalApplicationStatus(this.convertToProtoFormat(finalState));
    }
    
    private FinalApplicationStatus convertFromProtoFormat(final YarnProtos.FinalApplicationStatusProto s) {
        return ProtoUtils.convertFromProtoFormat(s);
    }
    
    private YarnProtos.FinalApplicationStatusProto convertToProtoFormat(final FinalApplicationStatus s) {
        return ProtoUtils.convertToProtoFormat(s);
    }
}
