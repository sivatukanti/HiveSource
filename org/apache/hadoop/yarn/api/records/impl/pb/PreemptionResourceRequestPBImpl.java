// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.api.records.PreemptionResourceRequest;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class PreemptionResourceRequestPBImpl extends PreemptionResourceRequest
{
    YarnProtos.PreemptionResourceRequestProto proto;
    YarnProtos.PreemptionResourceRequestProto.Builder builder;
    boolean viaProto;
    private ResourceRequest rr;
    
    public PreemptionResourceRequestPBImpl() {
        this.proto = YarnProtos.PreemptionResourceRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.PreemptionResourceRequestProto.newBuilder();
    }
    
    public PreemptionResourceRequestPBImpl(final YarnProtos.PreemptionResourceRequestProto proto) {
        this.proto = YarnProtos.PreemptionResourceRequestProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public synchronized YarnProtos.PreemptionResourceRequestProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((PreemptionResourceRequestPBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
    
    private void mergeLocalToProto() {
        if (this.viaProto) {
            this.maybeInitBuilder();
        }
        this.mergeLocalToBuilder();
        this.proto = this.builder.build();
        this.viaProto = true;
    }
    
    private void mergeLocalToBuilder() {
        if (this.rr != null) {
            this.builder.setResource(this.convertToProtoFormat(this.rr));
        }
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.PreemptionResourceRequestProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    @Override
    public synchronized ResourceRequest getResourceRequest() {
        final YarnProtos.PreemptionResourceRequestProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        if (this.rr != null) {
            return this.rr;
        }
        if (!p.hasResource()) {
            return null;
        }
        return this.rr = this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    public synchronized void setResourceRequest(final ResourceRequest rr) {
        this.maybeInitBuilder();
        if (null == rr) {
            this.builder.clearResource();
        }
        this.rr = rr;
    }
    
    private ResourceRequestPBImpl convertFromProtoFormat(final YarnProtos.ResourceRequestProto p) {
        return new ResourceRequestPBImpl(p);
    }
    
    private YarnProtos.ResourceRequestProto convertToProtoFormat(final ResourceRequest t) {
        return ((ResourceRequestPBImpl)t).getProto();
    }
}
