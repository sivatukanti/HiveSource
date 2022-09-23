// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetQueueInfoRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetQueueInfoRequestPBImpl extends GetQueueInfoRequest
{
    YarnServiceProtos.GetQueueInfoRequestProto proto;
    YarnServiceProtos.GetQueueInfoRequestProto.Builder builder;
    boolean viaProto;
    
    public GetQueueInfoRequestPBImpl() {
        this.proto = YarnServiceProtos.GetQueueInfoRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServiceProtos.GetQueueInfoRequestProto.newBuilder();
    }
    
    public GetQueueInfoRequestPBImpl(final YarnServiceProtos.GetQueueInfoRequestProto proto) {
        this.proto = YarnServiceProtos.GetQueueInfoRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    @Override
    public boolean getIncludeApplications() {
        final YarnServiceProtos.GetQueueInfoRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasIncludeApplications() && p.getIncludeApplications();
    }
    
    @Override
    public boolean getIncludeChildQueues() {
        final YarnServiceProtos.GetQueueInfoRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasIncludeChildQueues() && p.getIncludeChildQueues();
    }
    
    @Override
    public String getQueueName() {
        final YarnServiceProtos.GetQueueInfoRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasQueueName() ? p.getQueueName() : null;
    }
    
    @Override
    public boolean getRecursive() {
        final YarnServiceProtos.GetQueueInfoRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.hasRecursive() && p.getRecursive();
    }
    
    @Override
    public void setIncludeApplications(final boolean includeApplications) {
        this.maybeInitBuilder();
        this.builder.setIncludeApplications(includeApplications);
    }
    
    @Override
    public void setIncludeChildQueues(final boolean includeChildQueues) {
        this.maybeInitBuilder();
        this.builder.setIncludeChildQueues(includeChildQueues);
    }
    
    @Override
    public void setQueueName(final String queueName) {
        this.maybeInitBuilder();
        if (queueName == null) {
            this.builder.clearQueueName();
            return;
        }
        this.builder.setQueueName(queueName);
    }
    
    @Override
    public void setRecursive(final boolean recursive) {
        this.maybeInitBuilder();
        this.builder.setRecursive(recursive);
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnServiceProtos.GetQueueInfoRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    public YarnServiceProtos.GetQueueInfoRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetQueueInfoRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
