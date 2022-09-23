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
import org.apache.hadoop.yarn.api.protocolrecords.MoveApplicationAcrossQueuesRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MoveApplicationAcrossQueuesRequestPBImpl extends MoveApplicationAcrossQueuesRequest
{
    YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto proto;
    YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    private String targetQueue;
    
    public MoveApplicationAcrossQueuesRequestPBImpl() {
        this.proto = YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.newBuilder();
    }
    
    public MoveApplicationAcrossQueuesRequestPBImpl(final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto proto) {
        this.proto = YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto getProto() {
        this.mergeLocalToProto();
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        if (this.applicationId != null) {
            return this.applicationId;
        }
        final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationId()) {
            return null;
        }
        return this.applicationId = this.convertFromProtoFormat(p.getApplicationId());
    }
    
    @Override
    public void setApplicationId(final ApplicationId appId) {
        this.maybeInitBuilder();
        if (this.applicationId == null) {
            this.builder.clearApplicationId();
        }
        this.applicationId = appId;
    }
    
    @Override
    public String getTargetQueue() {
        if (this.targetQueue != null) {
            return this.targetQueue;
        }
        final YarnServiceProtos.MoveApplicationAcrossQueuesRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasApplicationId()) {
            return null;
        }
        return this.targetQueue = p.getTargetQueue();
    }
    
    @Override
    public void setTargetQueue(final String queue) {
        this.maybeInitBuilder();
        if (this.applicationId == null) {
            this.builder.clearTargetQueue();
        }
        this.targetQueue = queue;
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationId != null) {
            this.builder.setApplicationId(this.convertToProtoFormat(this.applicationId));
        }
        if (this.targetQueue != null) {
            this.builder.setTargetQueue(this.targetQueue);
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
            this.builder = YarnServiceProtos.MoveApplicationAcrossQueuesRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((MoveApplicationAcrossQueuesRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
}
