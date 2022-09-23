// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ResourcePBImpl;
import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationIdPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class GetNewApplicationResponsePBImpl extends GetNewApplicationResponse
{
    YarnServiceProtos.GetNewApplicationResponseProto proto;
    YarnServiceProtos.GetNewApplicationResponseProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    private Resource maximumResourceCapability;
    
    public GetNewApplicationResponsePBImpl() {
        this.proto = YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.maximumResourceCapability = null;
        this.builder = YarnServiceProtos.GetNewApplicationResponseProto.newBuilder();
    }
    
    public GetNewApplicationResponsePBImpl(final YarnServiceProtos.GetNewApplicationResponseProto proto) {
        this.proto = YarnServiceProtos.GetNewApplicationResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.maximumResourceCapability = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.GetNewApplicationResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((GetNewApplicationResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationId != null) {
            this.builder.setApplicationId(this.convertToProtoFormat(this.applicationId));
        }
        if (this.maximumResourceCapability != null) {
            this.builder.setMaximumCapability(this.convertToProtoFormat(this.maximumResourceCapability));
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
            this.builder = YarnServiceProtos.GetNewApplicationResponseProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        if (this.applicationId != null) {
            return this.applicationId;
        }
        final YarnServiceProtos.GetNewApplicationResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
    
    @Override
    public Resource getMaximumResourceCapability() {
        if (this.maximumResourceCapability != null) {
            return this.maximumResourceCapability;
        }
        final YarnServiceProtos.GetNewApplicationResponseProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (!p.hasMaximumCapability()) {
            return null;
        }
        return this.maximumResourceCapability = this.convertFromProtoFormat(p.getMaximumCapability());
    }
    
    @Override
    public void setMaximumResourceCapability(final Resource capability) {
        this.maybeInitBuilder();
        if (this.maximumResourceCapability == null) {
            this.builder.clearMaximumCapability();
        }
        this.maximumResourceCapability = capability;
    }
    
    private ApplicationIdPBImpl convertFromProtoFormat(final YarnProtos.ApplicationIdProto p) {
        return new ApplicationIdPBImpl(p);
    }
    
    private YarnProtos.ApplicationIdProto convertToProtoFormat(final ApplicationId t) {
        return ((ApplicationIdPBImpl)t).getProto();
    }
    
    private Resource convertFromProtoFormat(final YarnProtos.ResourceProto resource) {
        return new ResourcePBImpl(resource);
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource resource) {
        return ((ResourcePBImpl)resource).getProto();
    }
}
