// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsResponse;

public class AddToClusterNodeLabelsResponsePBImpl extends AddToClusterNodeLabelsResponse
{
    YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto proto;
    YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.Builder builder;
    boolean viaProto;
    
    public AddToClusterNodeLabelsResponsePBImpl() {
        this.proto = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.builder = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.newBuilder();
    }
    
    public AddToClusterNodeLabelsResponsePBImpl(final YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto proto) {
        this.proto = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto.getDefaultInstance();
        this.builder = null;
        this.viaProto = false;
        this.proto = proto;
        this.viaProto = true;
    }
    
    public YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsResponseProto getProto() {
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
        return other != null && other.getClass().isAssignableFrom(this.getClass()) && this.getProto().equals(((AddToClusterNodeLabelsResponsePBImpl)this.getClass().cast(other)).getProto());
    }
    
    @Override
    public String toString() {
        return TextFormat.shortDebugString(this.getProto());
    }
}
