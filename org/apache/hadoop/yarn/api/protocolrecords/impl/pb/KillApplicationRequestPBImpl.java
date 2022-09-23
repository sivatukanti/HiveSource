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
import org.apache.hadoop.yarn.api.protocolrecords.KillApplicationRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class KillApplicationRequestPBImpl extends KillApplicationRequest
{
    YarnServiceProtos.KillApplicationRequestProto proto;
    YarnServiceProtos.KillApplicationRequestProto.Builder builder;
    boolean viaProto;
    private ApplicationId applicationId;
    
    public KillApplicationRequestPBImpl() {
        this.proto = YarnServiceProtos.KillApplicationRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.builder = YarnServiceProtos.KillApplicationRequestProto.newBuilder();
    }
    
    public KillApplicationRequestPBImpl(final YarnServiceProtos.KillApplicationRequestProto proto) {
        this.proto = YarnServiceProtos.KillApplicationRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationId = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.KillApplicationRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((KillApplicationRequestPBImpl)this.getClass().cast(other)).getProto());
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
            this.builder = YarnServiceProtos.KillApplicationRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationId getApplicationId() {
        final YarnServiceProtos.KillApplicationRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
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
