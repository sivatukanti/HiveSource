// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.UpdateNodeResourceResponse;

public class UpdateNodeResourceResponsePBImpl extends UpdateNodeResourceResponse
{
    YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto proto;
    YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.Builder builder;
    boolean viaProto;
    
    public UpdateNodeResourceResponsePBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.newBuilder();
    }
    
    public UpdateNodeResourceResponsePBImpl(final YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerResourceManagerServiceProtos.UpdateNodeResourceResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((UpdateNodeResourceResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return this.getProto().toString().replaceAll("\\n", ", ").replaceAll("\\s+", " ");
    }
}
