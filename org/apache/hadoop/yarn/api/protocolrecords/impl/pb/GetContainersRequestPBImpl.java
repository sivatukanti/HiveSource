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
import org.apache.hadoop.yarn.api.protocolrecords.GetContainersRequest;

public class GetContainersRequestPBImpl extends GetContainersRequest
{
    YarnServiceProtos.GetContainersRequestProto proto;
    YarnServiceProtos.GetContainersRequestProto.Builder builder;
    boolean viaProto;
    private ApplicationAttemptId applicationAttemptId;
    
    public GetContainersRequestPBImpl() {
        this.proto = YarnServiceProtos.GetContainersRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationAttemptId = null;
        this.builder = YarnServiceProtos.GetContainersRequestProto.newBuilder();
    }
    
    public GetContainersRequestPBImpl(final YarnServiceProtos.GetContainersRequestProto proto) {
        this.proto = YarnServiceProtos.GetContainersRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationAttemptId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetContainersRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetContainersRequestPBImpl)this.getClass().cast(other)).getProto());
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
            this.builder = YarnServiceProtos.GetContainersRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationAttemptId getApplicationAttemptId() {
        if (this.applicationAttemptId != null) {
            return this.applicationAttemptId;
        }
        final YarnServiceProtos.GetContainersRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
