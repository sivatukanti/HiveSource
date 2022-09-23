// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ResourceOption;

public class ResourceOptionPBImpl extends ResourceOption
{
    YarnProtos.ResourceOptionProto proto;
    YarnProtos.ResourceOptionProto.Builder builder;
    boolean viaProto;
    
    public ResourceOptionPBImpl() {
        this.proto = YarnProtos.ResourceOptionProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnProtos.ResourceOptionProto.newBuilder();
    }
    
    public ResourceOptionPBImpl(final YarnProtos.ResourceOptionProto proto) {
        this.proto = YarnProtos.ResourceOptionProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnProtos.ResourceOptionProto getProto() {
        this.proto = (this.viaProto ? this.proto : this.builder.build());
        this.viaProto = true;
        return this.proto;
    }
    
    @Override
    public Resource getResource() {
        final YarnProtos.ResourceOptionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return this.convertFromProtoFormat(p.getResource());
    }
    
    @Override
    protected void setResource(final Resource resource) {
        this.maybeInitBuilder();
        this.builder.setResource(this.convertToProtoFormat(resource));
    }
    
    @Override
    public int getOverCommitTimeout() {
        final YarnProtos.ResourceOptionProtoOrBuilder p = this.viaProto ? this.proto : this.builder;
        return p.getOverCommitTimeout();
    }
    
    @Override
    protected void setOverCommitTimeout(final int overCommitTimeout) {
        this.maybeInitBuilder();
        this.builder.setOverCommitTimeout(overCommitTimeout);
    }
    
    private void maybeInitBuilder() {
        if (this.viaProto || this.builder == null) {
            this.builder = YarnProtos.ResourceOptionProto.newBuilder(this.proto);
        }
        this.viaProto = false;
    }
    
    private YarnProtos.ResourceProto convertToProtoFormat(final Resource resource) {
        return ((ResourcePBImpl)resource).getProto();
    }
    
    private ResourcePBImpl convertFromProtoFormat(final YarnProtos.ResourceProto p) {
        return new ResourcePBImpl(p);
    }
    
    @Override
    protected void build() {
        this.proto = this.builder.build();
        this.viaProto = true;
        this.builder = null;
    }
    
    @Override
    public int hashCode() {
        return this.getProto().hashCode();
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((ResourceOptionPBImpl)this.getClass().cast(other)).getProto());
    }
}
