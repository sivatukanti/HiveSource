// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ContainerIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetContainerReportRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetContainerReportRequestPBImpl extends GetContainerReportRequest
{
    YarnServiceProtos.GetContainerReportRequestProto proto;
    YarnServiceProtos.GetContainerReportRequestProto.Builder builder;
    boolean viaProto;
    private ContainerId containerId;
    
    public GetContainerReportRequestPBImpl() {
        this.proto = YarnServiceProtos.GetContainerReportRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.builder = YarnServiceProtos.GetContainerReportRequestProto.newBuilder();
    }
    
    public GetContainerReportRequestPBImpl(final YarnServiceProtos.GetContainerReportRequestProto proto) {
        this.proto = YarnServiceProtos.GetContainerReportRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.containerId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetContainerReportRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetContainerReportRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.containerId != null) {
            this.builder.setContainerId(this.convertToProtoFormat(this.containerId));
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
            this.builder = YarnServiceProtos.GetContainerReportRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ContainerId getContainerId() {
        if (this.containerId != null) {
            return this.containerId;
        }
        final YarnServiceProtos.GetContainerReportRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasContainerId()) {
            return null;
        }
        return this.containerId = this.convertFromProtoFormat(p.getContainerId());
    }
    
    @Override
    public void setContainerId(final ContainerId containerId) {
        this.maybeInitBuilder();
        if (containerId == null) {
            this.builder.clearContainerId();
        }
        this.containerId = containerId;
    }
    
    private ContainerIdPBImpl convertFromProtoFormat(final YarnProtos.ContainerIdProto p) {
        return new ContainerIdPBImpl(p);
    }
    
    private YarnProtos.ContainerIdProto convertToProtoFormat(final ContainerId t) {
        return ((ContainerIdPBImpl)t).getProto();
    }
}
