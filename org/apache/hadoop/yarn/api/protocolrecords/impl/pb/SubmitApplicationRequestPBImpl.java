// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.api.records.impl.pb.ApplicationSubmissionContextPBImpl;
import org.apache.hadoop.yarn.proto.YarnProtos;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.protocolrecords.SubmitApplicationRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class SubmitApplicationRequestPBImpl extends SubmitApplicationRequest
{
    YarnServiceProtos.SubmitApplicationRequestProto proto;
    YarnServiceProtos.SubmitApplicationRequestProto.Builder builder;
    boolean viaProto;
    private ApplicationSubmissionContext applicationSubmissionContext;
    
    public SubmitApplicationRequestPBImpl() {
        this.proto = YarnServiceProtos.SubmitApplicationRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationSubmissionContext = null;
        this.builder = YarnServiceProtos.SubmitApplicationRequestProto.newBuilder();
    }
    
    public SubmitApplicationRequestPBImpl(final YarnServiceProtos.SubmitApplicationRequestProto proto) {
        this.proto = YarnServiceProtos.SubmitApplicationRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.applicationSubmissionContext = null;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServiceProtos.SubmitApplicationRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((SubmitApplicationRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToBuilder() {
        if (this.applicationSubmissionContext != null) {
            this.builder.setApplicationSubmissionContext(this.convertToProtoFormat(this.applicationSubmissionContext));
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
            this.builder = YarnServiceProtos.SubmitApplicationRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public ApplicationSubmissionContext getApplicationSubmissionContext() {
        final YarnServiceProtos.SubmitApplicationRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.applicationSubmissionContext != null) {
            return this.applicationSubmissionContext;
        }
        if (!p.hasApplicationSubmissionContext()) {
            return null;
        }
        return this.applicationSubmissionContext = this.convertFromProtoFormat(p.getApplicationSubmissionContext());
    }
    
    @Override
    public void setApplicationSubmissionContext(final ApplicationSubmissionContext applicationSubmissionContext) {
        this.maybeInitBuilder();
        if (applicationSubmissionContext == null) {
            this.builder.clearApplicationSubmissionContext();
        }
        this.applicationSubmissionContext = applicationSubmissionContext;
    }
    
    private ApplicationSubmissionContextPBImpl convertFromProtoFormat(final YarnProtos.ApplicationSubmissionContextProto p) {
        return new ApplicationSubmissionContextPBImpl(p);
    }
    
    private YarnProtos.ApplicationSubmissionContextProto convertToProtoFormat(final ApplicationSubmissionContext t) {
        return ((ApplicationSubmissionContextPBImpl)t).getProto();
    }
}
